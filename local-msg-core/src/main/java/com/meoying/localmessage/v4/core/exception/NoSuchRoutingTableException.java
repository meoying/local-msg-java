package com.meoying.localmessage.v4.core.exception;

public class NoSuchRoutingTableException extends BaseException {

    public NoSuchRoutingTableException(Throwable e) {
        super(e);
    }

    public NoSuchRoutingTableException(String msg) {
        super(msg);
    }

    public NoSuchRoutingTableException(String msg, Throwable e) {
        super(msg, e);
    }
}
