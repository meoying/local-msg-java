package com.meoying.localmessage.repository.entity;

import javax.persistence.*;

@Entity
@Table(name = "LOCAL_MESSAGE")
public class LocalMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String topic;
    @Column
    private String msg;
    @Column
    private int status;
    @Column(name = "retry_count")
    private int retryCount;
    @Column(name = "data_chg_time")
    private long dataChgTime;

    public long getDataChgTime() {
        return dataChgTime;
    }

    public Long getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getStatus() {
        return status;
    }

    public String getTopic() {
        return topic;
    }

    public void setDataChgTime(long dataChgTime) {
        this.dataChgTime = dataChgTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
