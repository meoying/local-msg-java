package com.meoying.localmessage.core.utils.utils;

import com.meoying.localmessage.api.ShardingFunc;
import com.meoying.localmessage.core.threadlocal.AsyncSupportThreadLocal;
public class ShardingFuncThreadLocal {

    private static final ThreadLocal<ShardingFunc> tl = new AsyncSupportThreadLocal<>();

    public static ThreadLocal<ShardingFunc> getShardingFuncThreadLocal() {
        return tl;
    }

    public static void remove() {
        tl.remove();
    }
}