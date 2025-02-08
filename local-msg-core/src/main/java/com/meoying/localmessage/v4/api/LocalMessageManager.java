package com.meoying.localmessage.v4.api;

import com.meoying.localmessage.v4.api.sharding.ShardingFunc;
import com.meoying.localmessage.v4.api.sharding.ShardingFuncThreadLocal;
import com.meoying.localmessage.v4.core.exception.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public interface LocalMessageManager extends TransactionSupport {

    Logger logger = LoggerFactory.getLogger(LocalMessageManager.class);

    void send(@NotNull Message message) throws MessageException;

    Long save(@NotNull Message message) throws MessageException;

    StopFunc fixMessage() throws MessageException;

    default <T extends Messager> T doWithLocalMessage(@NotNull Supplier<T> biz) {
        AtomicReference<Message> message = new AtomicReference<>();
        T result = doWithTransaction(() -> {
            T t = biz.get();
            message.set(t.getMessage());
            if (message.get() != null) {
                try {
                    Long save = save(message.get());
                    message.get().setId(save);
                } catch (MessageException e) {
                    logger.warn("write message to db failed {}, message:{}", e.getMessage(), message, e);
                }
            }
            return t;
        });

        if (message.get() != null) {
            try {
                send(message.get());
            } catch (MessageException e) {
                logger.warn("send message failed {}, message:{}", e.getMessage(), message, e);
            }
        }

        return result;
    }

    default <T extends Messager> T doWithShardingLocalMessage(@NotNull ShardingFunc shardingFunc,
                                                              @NotNull Supplier<T> biz) {
        return ShardingFuncThreadLocal.warp(shardingFunc, () -> doWithLocalMessage(biz));
    }

}
