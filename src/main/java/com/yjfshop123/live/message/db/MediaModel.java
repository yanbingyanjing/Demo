package com.yjfshop123.live.message.db;

import java.io.Serializable;

public class MediaModel implements Serializable {

    private String roomdbId;// 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
    private int roomId; //云通信的房间号
    private int mediaType; //1 语音  2 视频
    private int isHangup;// 默认 0 接收通话  1 挂断通话
    private int cost;//费用

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getRoomdbId() {
        return roomdbId;
    }

    public void setRoomdbId(String roomdbId) {
        this.roomdbId = roomdbId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getIsHangup() {
        return isHangup;
    }

    public void setIsHangup(int isHangup) {
        this.isHangup = isHangup;
    }
}
