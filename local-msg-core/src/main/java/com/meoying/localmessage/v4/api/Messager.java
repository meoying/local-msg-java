package com.meoying.localmessage.v4.api;


import javax.validation.constraints.NotNull;

public interface Messager {
    @NotNull
    Message getMessage();
}
