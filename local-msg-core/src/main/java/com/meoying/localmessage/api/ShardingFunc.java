package com.meoying.localmessage.api;

import com.meoying.localmessage.core.Pair;

public interface ShardingFunc {

    /**
     * return dataSource name ，entity name
     */
    Pair<String, String> getSharding();
}
