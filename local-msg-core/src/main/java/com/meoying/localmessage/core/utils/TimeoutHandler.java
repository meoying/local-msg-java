package com.meoying.localmessage.core.utils;

import com.meoying.localmessage.core.logging.LogFactory;
import com.meoying.localmessage.core.logging.Logger;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class TimeoutHandler implements AutoCloseable {

    private Logger logger = LogFactory.getLogger(TimeoutHandler.class);
    private ScheduledExecutorService scheduler;
    private int i = 0;

    public TimeoutHandler(String tname) {
        scheduler = Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable, tname + i);
            // 设置为守护进程
            thread.setDaemon(true);
            i++;
            return thread;
        });
    }

    public TimeoutHandler() {
        this("TimeoutHandler-");
    }

    public void close() {
        try {
            scheduler.shutdown();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 为 CompletableFuture 添加超时处理，超时抛出 TimeoutException，
     * 返回的 CompletableFuture 不包含错误处理 exceptionally
     */
    public <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        CompletableFuture<T> timeoutFuture = new CompletableFuture<>();

        // 设置超时调度
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
            timeoutFuture.completeExceptionally(
                    new TimeoutException("Operation timed out after " + timeout + " " + unit)
            );
        }, timeout, unit);

        // 注册原始任务完成的回调
        future.whenComplete((result, error) -> {
            scheduledFuture.cancel(false); // 取消超时调度
            if (error != null) {
                timeoutFuture.completeExceptionally(error);
            } else {
                timeoutFuture.complete(result);
            }
        });

        return timeoutFuture;
    }

    public <T> CompletableFuture<T> withTimeoutNoTimeoutException(Supplier<T> supplier, long timeout,
                                                                        TimeUnit unit) {
        // 实际业务逻辑
        CompletableFuture<T> task = CompletableFuture.supplyAsync(supplier);
        return withTimeout(task, timeout, unit)
                .exceptionally(throwable -> {
                    logger.warn("timeout", throwable);
                    return null;
                });
    }

    public static class TimeoutException extends RuntimeException {
        public TimeoutException(String message) {
            super(message);
        }

        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
