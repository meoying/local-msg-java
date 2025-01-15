package com.mepying.core.utils;

import com.meoying.localmessage.core.utils.TimeoutHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeoutHandlerTest {

    private static TimeoutHandler timeoutHandler;

    @BeforeAll
    public static void beforeAll() {
        timeoutHandler = new TimeoutHandler();
    }

    @AfterAll
    public static void afterAll() {
        timeoutHandler.close();
    }

    @Test
    public void testTimeOut() {

        // Your test code here
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "煮熟的米饭";
        });

        CompletableFuture<String> stringCompletableFuture =
                timeoutHandler.withTimeout(cf, 3, TimeUnit.SECONDS).exceptionally(e -> {
                    System.out.println("半熟的米饭");
                    return "半熟的米饭";
                });


        Object join = stringCompletableFuture.join();
        assertEquals("半熟的米饭", join);
    }

    @Test
    public void testTimeOut1() {

        // Your test code here
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "煮熟的米饭";
        });

        CompletableFuture<String> stringCompletableFuture = timeoutHandler.withTimeout(cf, 3, TimeUnit.SECONDS);


        try {
            Object join = stringCompletableFuture.join();
        } catch (CompletionException e) {
            assertEquals("com.meoying.localmessage.core.utils.TimeoutHandler$TimeoutException: Operation timed out " +
                    "after 3 SECONDS", e.getMessage());
        }
    }
}
