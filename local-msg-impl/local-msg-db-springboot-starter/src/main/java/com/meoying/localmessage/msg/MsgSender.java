package com.meoying.localmessage.msg;

import com.meoying.localmessage.core.Result;

public interface MsgSender {

    Result<?> send(String topic, String msg);
}
