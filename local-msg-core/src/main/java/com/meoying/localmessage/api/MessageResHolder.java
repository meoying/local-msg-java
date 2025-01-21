package com.meoying.localmessage.api;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class MessageResHolder<T> {

    private final T t;
    private final Message message;

    public MessageResHolder(T t, @NotNull Message message) {
        this.t = t;
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message can not be null");
        }
        this.message = message;
    }

    T getT() {
        return t;
    }

    @NotNull
    Message getMessage() {
        return message;
    }


}
