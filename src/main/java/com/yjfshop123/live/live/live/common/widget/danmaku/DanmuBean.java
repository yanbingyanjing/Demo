package com.yjfshop123.live.live.live.common.widget.danmaku;

public class DanmuBean {

    private String uid;
    private String userNiceName;
    private String avatar;
    private String content;

    public DanmuBean(String uid, String userNiceName, String avatar, String content) {
        this.uid = uid;
        this.userNiceName = userNiceName;
        this.avatar = avatar;
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
