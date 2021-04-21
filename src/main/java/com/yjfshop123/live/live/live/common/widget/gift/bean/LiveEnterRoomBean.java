package com.yjfshop123.live.live.live.common.widget.gift.bean;

public class LiveEnterRoomBean {

    public String identityType = "0";
    private String userName;
    private String avatar;
    private boolean isVip;
    private boolean isGuard;
    private String user_level;
    private String mount;

    public LiveEnterRoomBean(String userName, String avatar, boolean isVip, boolean isGuard, String user_level, String identityType, String mount) {
        this.userName = userName;
        this.avatar = avatar;
        this.isVip = isVip;
        this.isGuard = isGuard;
        this.user_level = user_level;
        this.identityType = identityType;
        this.mount = mount;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public boolean isGuard() {
        return isGuard;
    }

    public void setGuard(boolean guard) {
        isGuard = guard;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }
}
