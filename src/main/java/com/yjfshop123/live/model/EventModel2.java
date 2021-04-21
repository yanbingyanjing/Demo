package com.yjfshop123.live.model;

public class EventModel2 {


    private String type;
    private long readCount_like;
    private long readCount_reply;

    public EventModel2(String type, long readCount_like, long readCount_reply) {
        this.type = type;
        this.readCount_like = readCount_like;
        this.readCount_reply = readCount_reply;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getReadCount_like() {
        return readCount_like;
    }

    public void setReadCount_like(long readCount_like) {
        this.readCount_like = readCount_like;
    }

    public long getReadCount_reply() {
        return readCount_reply;
    }

    public void setReadCount_reply(long readCount_reply) {
        this.readCount_reply = readCount_reply;
    }
}
