package com.yjfshop123.live.message.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageDB extends RealmObject {

    @PrimaryKey
    private String messageId;// 消息id主键

    private String conversationId = ""; // 会话id
    private int type = 0; // 0 文字  1 图片 2 语音  3 视频  4 礼物 10媒体(语音视频通话) 11系统消息 12弹框提醒 13关闭视频
    private long timestamp; // 时间戳
    private int isSender = 0;  // 是否为发送方 0接收方 1发送方

    private int isSenderResult = 0; // 是否发送成功 0失败 1成功
    private int isShowTime = 0;    // 是否显示时间 0不显示 1显示

    private String text = ""; // 文字

    private String imageThumbnailURL = "";   // 图片缩略图地址
    private String imageOriginalURL = "";    // 图片原图地址
    private int imageW = 0;             // 图片宽
    private int imageH = 0;             // 图片高

    private String voicePath = "";          // 语音路径
    private long voiceDuration = 0;      // 语音时长

    private String videoThumbnailPath = ""; // 视频缩略图地址
    private int videoThumbnailW = 0;     // 视频缩略图宽
    private int videoThumbnailH = 0;     // 视频缩略图高
    private String videoPath = "";           // 视频地址
    private int videoDuration = 0;        // 视频时长


    private String roomdbId;// 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
    private int roomId; //云通信的房间号
    private int mediaType; //1 语音  2 视频
    private int isHangup;// 默认 0 接收通话  1 挂断通话
    private int cost;//费用

    private String giftName;//礼物名称
    private String giftIconImg;//礼物图片
    private String giftEffectImg;//礼物gif图
    private String giftCoinZh;//礼物价值

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftIconImg() {
        return giftIconImg;
    }

    public void setGiftIconImg(String giftIconImg) {
        this.giftIconImg = giftIconImg;
    }

    public String getGiftEffectImg() {
        return giftEffectImg;
    }

    public void setGiftEffectImg(String giftEffectImg) {
        this.giftEffectImg = giftEffectImg;
    }

    public String getGiftCoinZh() {
        return giftCoinZh;
    }

    public void setGiftCoinZh(String giftCoinZh) {
        this.giftCoinZh = giftCoinZh;
    }

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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIsSender() {
        return isSender;
    }

    public void setIsSender(int isSender) {
        this.isSender = isSender;
    }

    public int getIsSenderResult() {
        return isSenderResult;
    }

    public void setIsSenderResult(int isSenderResult) {
        this.isSenderResult = isSenderResult;
    }

    public int getIsShowTime() {
        return isShowTime;
    }

    public void setIsShowTime(int isShowTime) {
        this.isShowTime = isShowTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageThumbnailURL() {
        return imageThumbnailURL;
    }

    public void setImageThumbnailURL(String imageThumbnailURL) {
        this.imageThumbnailURL = imageThumbnailURL;
    }

    public String getImageOriginalURL() {
        return imageOriginalURL;
    }

    public void setImageOriginalURL(String imageOriginalURL) {
        this.imageOriginalURL = imageOriginalURL;
    }

    public int getImageW() {
        return imageW;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public int getImageH() {
        return imageH;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public long getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(long voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    public int getVideoThumbnailW() {
        return videoThumbnailW;
    }

    public void setVideoThumbnailW(int videoThumbnailW) {
        this.videoThumbnailW = videoThumbnailW;
    }

    public int getVideoThumbnailH() {
        return videoThumbnailH;
    }

    public void setVideoThumbnailH(int videoThumbnailH) {
        this.videoThumbnailH = videoThumbnailH;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }
}
