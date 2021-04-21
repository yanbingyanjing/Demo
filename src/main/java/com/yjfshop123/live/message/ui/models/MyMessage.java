package com.yjfshop123.live.message.ui.models;

import java.util.HashMap;
import java.util.UUID;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.commons.models.IUser;


public class MyMessage implements IMessage {

    private String msgId;
    private String text;
    private String timeString;
    private int type;
    private IUser user;
    private String mediaFilePath;
    private long duration;
    private String progress;

    private int mediaType = 0;
    private int imageW = 0;
    private int imageH = 0;

    private String videoThumbnailPath = "";
    private int videoThumbnailW = 0;
    private int videoThumbnailH = 0;

    private String giftName;
    private String giftIconImg;
    private String giftEffectImg;
    private String giftCoinZh;

    private MessageStatus mMsgStatus = MessageStatus.CREATED;

    public MyMessage(String text, int type, String msgId) {
        this.text = text;
        this.type = type;
        if (msgId == null){
            this.msgId = String.valueOf(UUID.randomUUID().getLeastSignificantBits());
        }else{
            this.msgId = msgId;
        }
    }

    @Override
    public String getMsgId() {
        return this.msgId;
    }

    @Override
    public IUser getFromUser() {
        if (user == null) {
            return new DefaultUser("0", "user", null);
        }
        return user;
    }

    public void setUserInfo(IUser user) {
        this.user = user;
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    @Override
    public HashMap<String, String> getExtras() {
        return null;
    }

    public void setMediaType(int mediaType){
        this.mediaType = mediaType;
    }

    @Override
    public int getMediaType() {
        return mediaType;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    @Override
    public int getImageW() {
        return imageW;
    }

    @Override
    public int getImageH() {
        return imageH;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    public void setVideoThumbnailW(int videoThumbnailW) {
        this.videoThumbnailW = videoThumbnailW;
    }

    public void setVideoThumbnailH(int videoThumbnailH) {
        this.videoThumbnailH = videoThumbnailH;
    }

    @Override
    public int getVideoThumbnailW() {
        return videoThumbnailW;
    }

    @Override
    public int getVideoThumbnailH() {
        return videoThumbnailH;
    }

    @Override
    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    @Override
    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName){
        this.giftName = giftName;
    }

    @Override
    public String getGiftIconImg() {
        return giftIconImg;
    }

    public void setGiftIconImg(String giftIconImg){
        this.giftIconImg = giftIconImg;
    }

    @Override
    public String getGiftEffectImg() {
        return giftEffectImg;
    }

    public void setGiftEffectImg(String giftEffectImg){
        this.giftEffectImg = giftEffectImg;
    }

    @Override
    public String getGiftCoinZh() {
        return giftCoinZh;
    }

    public void setGiftCoinZh(String giftCoinZh){
        this.giftCoinZh = giftCoinZh;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Override
    public String getTimeString() {
        return timeString;
    }

    public void setType(int type) {
        if (type >= 0 && type <= 12) {
            throw new IllegalArgumentException("Message type should not take the value between 0 and 12");
        }
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    /**
     * Set Message status. After sending Message, change the status so that the progress bar will dismiss.
     * @param messageStatus {@link cn.jiguang.imui.commons.models.IMessage.MessageStatus}
     */
    public void setMessageStatus(MessageStatus messageStatus) {
        this.mMsgStatus = messageStatus;
    }

    @Override
    public MessageStatus getMessageStatus() {
        return this.mMsgStatus;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getMediaFilePath() {
        return mediaFilePath;
    }
}