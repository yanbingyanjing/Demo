package com.yjfshop123.live.live.live.common.widget.gift.bean;

/**
 * 发送的消息
 */
public class LiveReceiveGiftBeanItem {

    private String giftId;//礼物的ID
    private String giftName;//礼物的名称
    private String giftIcon;//礼物的icon图
    private String gifUrl;//gif礼物 URL
    private int style;// 礼物样式 1:图片 2:gif动图 3:svga动图

    private int giftCount;//礼物的数量-针对普通png礼物  gif礼物都为1不可多发
    private String votes;//礼物总金蛋（礼物价值 多个礼物 * 礼物个数）

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftIcon() {
        return giftIcon;
    }

    public void setGiftIcon(String giftIcon) {
        this.giftIcon = giftIcon;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }
}
