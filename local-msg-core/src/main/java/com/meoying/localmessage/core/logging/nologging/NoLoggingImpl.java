package com.meoying.localmessage.core.logging.nologging;


import com.meoying.localmessage.core.logging.Logger;

public class NoLoggingImpl implements Logger {

    public NoLoggingImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void error(String s, Throwable e) {
        // Do Nothing
    }

    @Override
    public void error(String s) {
        // Do Nothing
    }

    @Override
    public void error(String s, Object... args) {

    }

    @Override
    public void debug(String s) {
        // Do Nothing
    }

    @Override
    public void debug(String s, Object... args) {

    }

    @Override
    public void trace(String s) {
        // Do Nothing
    }

    @Override
    public void trace(String s, Object... args) {

    }

    @Override
    public void info(String s) {
        // Do Nothing
    }

    @Override
    public void info(String s, Object... args) {

    }

    @Override
    public void warn(String s) {
        // Do Nothing
    }

    @Override
    public void warn(String s, Object... args) {

    }
}
