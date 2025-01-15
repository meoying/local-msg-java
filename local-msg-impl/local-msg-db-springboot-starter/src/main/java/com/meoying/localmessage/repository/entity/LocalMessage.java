package com.meoying.localmessage.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOCAL_MESSAGE")
public class LocalMessage {

    @Id
    private String id;
    @Column
    private String topic;
    @Column
    private String msg;
    @Column
    private String status;
    @Column(name = "retry_count")
    private int retryCount;
    @Column(name = "data_chg_time")
    private long dataChgTime;


    public long getDataChgTime() {
        return dataChgTime;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getStatus() {
        return status;
    }

    public String getTopic() {
        return topic;
    }

    public void setDataChgTime(long dataChgTime) {
        this.dataChgTime = dataChgTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
