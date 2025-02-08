package com.meoying.localmessage.v4.api;


public class Message {

    private final String topic;
    private final String msg;
    private long id;

    public Message(Long id, String topic, String msg) {
        this(topic,msg);
        this.id = id;
    }

    public Message(String topic, String msg) {
        this.msg = msg;
        this.topic = topic;
    }


    public String topic() {
        return topic;
    }

    public String msg() {
        return msg;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        if (this.id == 0L) {
            this.id = id;
        }
    }
}
