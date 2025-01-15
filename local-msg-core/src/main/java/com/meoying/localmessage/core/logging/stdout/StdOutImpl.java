package com.meoying.localmessage.core.logging.stdout;

import com.meoying.localmessage.core.logging.Logger;

public class StdOutImpl implements Logger {
    public StdOutImpl(String clazz) {
    }

    private static String format(String s, Object[] args) {
        if (args != null) {
            for (Object arg : args) {
                s = s.replaceFirst("\\{}", arg != null ? arg.toString() : "null");
            }
        }
        return s;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        System.err.println(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        System.err.println(s);
    }

    @Override
    public void error(String s, Object... args) {
        s = format(s, args);
        System.err.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.println(s);
    }

    @Override
    public void debug(String s, Object... args) {
        s = format(s, args);
        System.out.println(s);
    }

    @Override
    public void trace(String s) {
        System.out.println(s);
    }

    @Override
    public void trace(String s, Object... args) {
        s = format(s, args);
        System.out.println(s);
    }

    @Override
    public void info(String s) {
        System.out.println(s);
    }

    @Override
    public void info(String s, Object... args) {
        s = format(s, args);
        System.out.println(s);
    }

    @Override
    public void warn(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s, Object... args) {
        s = format(s, args);
        System.out.println(s);
    }
}
