package com.meoying.localmessage.v4.api.sharding;


import java.util.function.Supplier;

public class ShardingFuncThreadLocal {

    private static ThreadLocal<ShardingFunc> threadLocal = new ThreadLocal<>();

    public static void set(ShardingFunc shardingFunc) {
        threadLocal.set(shardingFunc);
    }

    public static ShardingFunc get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static <T> T warp(ShardingFunc shardingFunc, Supplier<T> biz) {
        try {
            set(shardingFunc);
            return biz.get();
        } finally {
            remove();
        }
    }
}