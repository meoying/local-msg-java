package com.meoying.localmessage.core.logging;


public interface Logger {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    boolean isInfoEnabled();

    void error(String s, Throwable e);

    default void error(String s) {
        error(s, (Object[]) null);
    }

    void error(String s, Object... args);

    default void debug(String s) {
        debug(s, (Object[]) null);
    }

    void debug(String s, Object... args);

    default void trace(String s) {
        trace(s, (Object[]) null);
    }

    void trace(String s, Object... args);

    default void info(String s) {
        info(s, (Object[]) null);
    }

    void info(String s, Object... args);

    default void warn(String s) {
        warn(s, (Object[]) null);
    }

    void warn(String s, Object... args);

}
