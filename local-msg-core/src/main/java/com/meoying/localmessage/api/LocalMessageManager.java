package com.meoying.localmessage.api;


import com.meoying.localmessage.core.Result;
import jakarta.validation.constraints.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public interface LocalMessageManager {

    Result<?> send(@NotNull Message message);

    Result<?> save(@NotNull Message message);

    Result<AtomicBoolean> fixMessage();

    default void accept(Transaction transaction, CompletableFuture<Message> future) {
      throw new UnsupportedOperationException("not support");
    }
}
