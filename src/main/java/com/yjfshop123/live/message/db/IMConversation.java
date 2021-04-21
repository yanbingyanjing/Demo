package com.yjfshop123.live.message.db;

import java.io.Serializable;

public class IMConversation implements Serializable {

    private String conversationId;// 会话id主键 otherPartyId + "@@"
    private int type;// 0 单聊  1 群聊  2 系统消息
    private String userIMId;// 用户(云通信)id
    private String otherPartyIMId;//  对方单聊:用户(云通信)id  群聊:群(云通信)id
    private String userId;//  用户id
    private String otherPartyId;// 对方用户id

    private String userName;
    private String userAvatar;
    private String otherPartyName;
    private String otherPartyAvatar;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public void setOtherPartyName(String otherPartyName) {
        this.otherPartyName = otherPartyName;
    }

    public String getOtherPartyAvatar() {
        return otherPartyAvatar;
    }

    public void setOtherPartyAvatar(String otherPartyAvatar) {
        this.otherPartyAvatar = otherPartyAvatar;
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

    public String getUserIMId() {
        return userIMId;
    }

    public void setUserIMId(String userIMId) {
        this.userIMId = userIMId;
    }

    public String getOtherPartyIMId() {
        return otherPartyIMId;
    }

    public void setOtherPartyIMId(String otherPartyIMId) {
        this.otherPartyIMId = otherPartyIMId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherPartyId() {
        return otherPartyId;
    }

    public void setOtherPartyId(String otherPartyId) {
        this.otherPartyId = otherPartyId;
    }

}
