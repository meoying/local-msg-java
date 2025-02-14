package com.meoying.localmessage.msg;

import com.meoying.localmessage.v4.core.Result;

public class TestMsgSender implements MsgSender {

    private static final double threshold = 0.3;

    @Override
    public Result<?> send(String topic, String msg) {
        double random = Math.random();

        if (random < threshold) {
            return Result.fail("-1", "发送失败");
        }
        return Result.success("发送成功", 0);
    }
}
