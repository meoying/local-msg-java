package com.meoying.localmessage.core.exception;

public class LoggingException extends BaseException {

    public LoggingException(Throwable e) {
        super(e);
    }

    public LoggingException(String msg) {
        super(msg);
    }

    public LoggingException(String msg, Throwable e) {
        super(msg, e);
    }
}
