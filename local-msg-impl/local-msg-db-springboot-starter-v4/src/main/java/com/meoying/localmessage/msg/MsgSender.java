package com.meoying.localmessage.msg;

import com.meoying.localmessage.v4.core.Result;

public interface MsgSender {

    Result<?> send(String topic, String msg);
}
