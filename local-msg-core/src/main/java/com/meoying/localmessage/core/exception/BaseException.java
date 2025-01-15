package com.meoying.localmessage.core.exception;

public class BaseException extends RuntimeException{
    public BaseException(String msg){
        super(msg);
    }
    public BaseException(Throwable e){
        super(e);
    }
    public BaseException(String msg, Throwable e){
        super(msg,e);
    }

}
