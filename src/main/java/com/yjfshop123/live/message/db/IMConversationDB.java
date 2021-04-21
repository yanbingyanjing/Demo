package com.yjfshop123.live.message.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IMConversationDB extends RealmObject {

    @PrimaryKey
    private String conversationId;// 会话id主键 userId + "@@" + otherPartyId
    private int type;// 0 单聊  1 群聊  2 系统消息 3 弹框提醒 4关闭视频
    private String userIMId;// 用户(云通信)id
    private String otherPartyIMId;//  对方单聊:用户(云通信)id  群聊:群(云通信)id
    private String userId;//  用户id
    private String otherPartyId;// 对方用户id
    private long unreadCount;// 未读消息
    private long timestamp;// 时间戳
    private String lastMessage; // 最后一条消息

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

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
