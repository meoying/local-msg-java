package com.meoying.localmessage.api;


import com.meoying.localmessage.core.Pair;
import com.meoying.localmessage.core.exception.MessageException;
import com.meoying.localmessage.core.utils.ShardingFuncThreadLocal;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface LocalMessageManager {

    /**
     * need to double-check the local message status ,
     * must message status is {@link MessageStatus#Init} .
     */
    void send(@NotNull Message message) throws MessageException;

    Long save(@NotNull Message message) throws MessageException;

    StopFunc fixMessage() throws MessageException;

    default <T> T accept(@NotNull Transaction transaction,
                         @NotNull Supplier<MessageResHolder<T>> supplier) {
        Logger logger = getLogger();
        MessageResHolder<T> holder = transaction.doWithTransaction(() -> {
            MessageResHolder<T> resHolder = supplier.get();
            Message message = resHolder.getMessage();
            if (message != null) {
                try {
                    message.setId(save(message));
                } catch (MessageException e) {
                    logger.warn("write message to db failed {}, message:{}", e.getMessage(), message, e);
                }
            }
            return resHolder;
        });
        Message message = holder.getMessage();
        if (message != null) {
            try {
                send(message);
            } catch (MessageException e) {
                logger.warn("send message failed {}, message:{}", e.getMessage(), message, e);
            }
        }

        return holder.getT();
    }

    default <T> T acceptSharding(@NotNull Transaction transaction,
                                 @NotNull Supplier<MessageResHolder<T>> supplier,
                                 @NotNull ShardingFunc shardingFunc) {
        ShardingFuncThreadLocal.getShardingFuncThreadLocal().set(shardingFunc);
        try {
            return this.accept(transaction, supplier);
        } finally {
            ShardingFuncThreadLocal.getShardingFuncThreadLocal().remove();
        }
    }

    // 难以支持，因为事务需要开启在future的最开始，只能说如果原本上下文存在事务可以使用此方案
    default <T> CompletableFuture<T> accept(@NotNull Transaction transaction,
                                            CompletableFuture<Pair<T, Message>> future) {
        throw new UnsupportedOperationException("not support");
    }

    default Logger getLogger() {
        return LoggerFactory.getLogger(LocalMessageManager.class);
    }
}
