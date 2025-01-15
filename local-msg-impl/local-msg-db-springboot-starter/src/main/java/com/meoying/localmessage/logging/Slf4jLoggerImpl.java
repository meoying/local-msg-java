package com.meoying.localmessage.logging;


import com.meoying.localmessage.core.logging.Logger;


public class Slf4jLoggerImpl implements Logger {

    private final org.slf4j.Logger log;

    public Slf4jLoggerImpl(org.slf4j.Logger logger) {
        log = logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void error(String s, Object... args) {
        log.error(s, args);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void debug(String s, Object... args) {
        log.debug(s, args);
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void trace(String s, Object... args) {
        log.trace(s, args);
    }

    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void info(String s, Object... args) {
        log.info(s, args);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Object... args) {
        log.warn(s, args);
    }


}
