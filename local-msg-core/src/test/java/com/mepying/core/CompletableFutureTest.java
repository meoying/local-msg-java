package com.mepying.core;


import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletableFutureTest {

    @Test
    public void testCompletableFuture() {
        // Your test code here
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            return "煮熟的米饭";
        });

        String join = cf.join();
        assertEquals("煮熟的米饭", join);
    }

    @Test
    public void testCompletableFuture1() {
        // Your test code here
        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            throw new RuntimeException("米饭糊了");
        }).exceptionally(e -> {
            System.out.println("捞起来重新蒸米饭");
            return "煮熟的米饭";
        });

        Object join = completableFuture.join();
        assertEquals("煮熟的米饭", join);
    }

    @Test
    public void testCompletableFuture2() {
        // Your test code here
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            return "煮熟的米饭";
        }).handle((r, e) -> {
            if (e != null) {
                System.out.println("捞起来重新蒸米饭");
            }
            return "重新煮熟的米饭";
        });

        Object join = cf.join();
        assertEquals("重新煮熟的米饭", join);
    }


    /**
     * whenComplete 无法修改原有结果
     */
    @Test
    public void testCompletableFuture3() {
        // Your test code here
        CompletableFuture<Object> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            throw new RuntimeException("米饭糊了");
        }).whenComplete((r, e) -> {
            if (e != null) {
                System.out.println("发现米饭糊了");
            }
        }).exceptionally(e -> "捞起来重新蒸米饭");

        Object join = cf.join();
        assertEquals("捞起来重新蒸米饭", join);
    }


    @Test
    public void testCompletableFuture4() {
        // Your test code here
        CompletableFuture<Object> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始蒸米饭");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("米饭糊了");
        }).whenComplete((r, e) -> {
            if (e != null) {
                System.out.println("发现米饭糊了");
            }
        }).exceptionally(e -> "捞起来重新蒸米饭");

        System.out.println("开始等待米饭");
        Object join = cf.join();
        assertEquals("捞起来重新蒸米饭", join);
    }
}
