package com.meoying.localmessage.api;


import com.meoying.localmessage.core.Pair;
import com.meoying.localmessage.core.Result;
import com.meoying.localmessage.core.logging.LogFactory;
import com.meoying.localmessage.core.logging.Logger;
import jakarta.validation.constraints.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public interface LocalMessageManagerV1 {


    /**
     * not throw exception ,
     * need to double-check the local message status ,
     * must message status is {@link MessageStatus#Init} .
     */
    Result<?> send(@NotNull Message message);

    /**
     * not throw exception
     */
    Result<String> save(@NotNull Message message);

    /**
     * not throw exception
     */
    Result<AtomicBoolean> fixMessage();


    default <T> T accept(@NotNull TransactionV1 transaction,
                         @NotNull Supplier<Pair<T, @NotNull Message>> supplier) {
        Logger logger = getLogger();
        Pair<T, Message> p = transaction.doWithTransaction(() -> {
            AtomicReference<String> idHolder = new AtomicReference<>("");
            CompletableFuture<Pair<T, Message>> errorProcessingMessage =
                    CompletableFuture.supplyAsync(supplier).whenComplete((pair, throwable) -> {
                        if (throwable == null) {
                            Message message = pair.getRight();
                            if (message != null) {
                                Result<String> save = save(message);
                                if (!save.isSuccess()) {
                                    logger.warn("write message to db failed{}, message:{}", save.getMsg(), message);
                                } else {
                                    idHolder.set(save.getData());
                                }
                            }
                        }
                    });
            Pair<T, Message> join = errorProcessingMessage.join();
            join.getRight().setId(idHolder.get());
            return join;
        });
        Message right = p.getRight();
        if (right != null) {
            Result<?> send = send(right);
            if (!send.isSuccess()) {
                logger.warn("send message failed{}, message:{}", send.getMsg(), right);
            }
        }

        return p.getLeft();
    }

    // 难以支持，因为事务需要开启在future的最开始，只能说如果原本上下文存在事务可以使用此方案
    default <T> CompletableFuture<T> accept(@NotNull TransactionV1 transaction,
                                            CompletableFuture<Pair<T, Message>> future) {
        throw new UnsupportedOperationException("not support");
    }

    default Logger getLogger() {
        return LogFactory.getLogger(LocalMessageManagerV1.class);
    }
}
