package com.meoying.localmessage.core.logging;


import com.meoying.localmessage.core.exception.LoggingException;
import com.meoying.localmessage.core.logging.nologging.NoLoggingImpl;
import com.meoying.localmessage.core.logging.stdout.StdOutImpl;

import java.lang.reflect.Constructor;

public class LogFactory {

    public static final String MARKER = "BASE";

    private static Constructor<? extends Logger> logConstructor;

    static {
        tryImplementation(LogFactory::useNoLogging);
    }

    private LogFactory() {

    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LoggingException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    public static synchronized void useCustomLogging(Class<? extends Logger> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useStdOutLogging() {
        setImplementation(StdOutImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable ignore) {
            }
        }
    }

    private static void setImplementation(Class<? extends Logger> implClass) {
        try {
            Constructor<? extends Logger> candidate = implClass.getConstructor(String.class);
            Logger logger = candidate.newInstance(LogFactory.class.getName());
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LoggingException("Error setting Log implementation.  Cause: " + t, t);
        }
    }
}
