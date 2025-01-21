package com.meoying.localmessage.core.exception;

public class NoSuchMessageException extends BaseException {

    public NoSuchMessageException(Throwable e) {
        super(e);
    }

    public NoSuchMessageException(String msg) {
        super(msg);
    }

    public NoSuchMessageException(String msg, Throwable e) {
        super(msg, e);
    }
}
