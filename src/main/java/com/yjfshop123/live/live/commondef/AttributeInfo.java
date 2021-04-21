package com.yjfshop123.live.live.commondef;

public class AttributeInfo {

    public String identityType = "0";//身份类型 0普通 1主播 2管理员
    public String isVip = "0";//是否vip (1:是 0:否)
    public String isGuard = "0";// 是否守护 (1:是 0:否)
    public String user_level = "0";//用户财富等级 例："3"
    public String mount = "";//坐骑

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    public String getIsGuard() {
        return isGuard;
    }

    public void setIsGuard(String isGuard) {
        this.isGuard = isGuard;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }
}
