package com.meoying.localmessage.api;

public interface Message extends Id{
    String topic();
    String msg();
}
