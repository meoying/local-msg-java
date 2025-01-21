package com.meoying.localmessage.api;

public interface Message{
    Long id();
    String topic();
    String msg();

    void setId(Long id);
}
