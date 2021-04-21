package com.yjfshop123.live.live.live.common.widget.gift.bean;

import android.text.TextUtils;

import javax.annotation.Nullable;

public class LiveReceiveGiftBean {

    private String uid;//用户ID
    private String avatar;//用户头像
    private String userNiceName;//用户昵称

    private LiveReceiveGiftBeanItem liveReceiveGiftBeanItem;

    private int lianCount = 1;//连发数量
    private String mKey;//uid + giftId + giftCount

    //#################################################################
    @Nullable
    private String gifPath;

    @Nullable
    public String getGifPath() {
        return gifPath;
    }

    public void setGifPath(@Nullable String gifPath) {
        this.gifPath = gifPath;
    }
    //#################################################################

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public int getLianCount() {
        return lianCount;
    }

    public void setLianCount(int lianCount) {
        this.lianCount = lianCount;
    }

    public LiveReceiveGiftBeanItem getLiveReceiveGiftBeanItem() {
        return liveReceiveGiftBeanItem;
    }

    public void setLiveReceiveGiftBeanItem(LiveReceiveGiftBeanItem liveReceiveGiftBeanItem) {
        this.liveReceiveGiftBeanItem = liveReceiveGiftBeanItem;
    }

    public String getKey() {
        if (TextUtils.isEmpty(mKey)) {
            mKey = this.uid + getLiveReceiveGiftBeanItem().getGiftId() + getLiveReceiveGiftBeanItem().getGiftCount();
        }
        return mKey;
    }
}
