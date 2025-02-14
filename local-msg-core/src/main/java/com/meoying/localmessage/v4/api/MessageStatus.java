package com.meoying.localmessage.v4.api;

public enum MessageStatus {

    Init(0),
    Success(2),
    Fail(3);

    private final int code;

    MessageStatus(int i) {
        this.code = i;
    }

    public static MessageStatus valueOf(int i) {
        MessageStatus[] values = values();
        for (MessageStatus value : values) {
            if (value.code == i) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
}
