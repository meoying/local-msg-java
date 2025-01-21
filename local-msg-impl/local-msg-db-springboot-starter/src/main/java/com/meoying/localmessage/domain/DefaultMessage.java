package com.meoying.localmessage.domain;

import com.meoying.localmessage.api.Message;

public class DefaultMessage implements Message {

    private final String topic;
    private final String msg;
    private long id;

    public DefaultMessage(Long id, String msg, String topic) {
        this(msg,topic);
        this.id = id;
    }

    public DefaultMessage(String msg, String topic) {
        this.msg = msg;
        this.topic = topic;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public String msg() {
        return msg;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(Long id) {
        if (this.id == 0L) {
            this.id = id;
        }
    }
}
