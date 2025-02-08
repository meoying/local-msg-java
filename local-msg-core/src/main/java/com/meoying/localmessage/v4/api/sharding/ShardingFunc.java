package com.meoying.localmessage.v4.api.sharding;

public interface ShardingFunc {

    /**
     * return dataSource name ，entity name
     */
    MsgTable getSharding();
}
