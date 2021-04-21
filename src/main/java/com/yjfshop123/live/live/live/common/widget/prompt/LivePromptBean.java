package com.yjfshop123.live.live.live.common.widget.prompt;

public class LivePromptBean {

    private String from_user_nickname;
    private String to_user_nickname;
    private String gift_name;
    private String icon_img;
    private String live_id;

    public LivePromptBean(String from_user_nickname, String to_user_nickname, String gift_name, String icon_img, String live_id) {
        this.from_user_nickname = from_user_nickname;
        this.to_user_nickname = to_user_nickname;
        this.gift_name = gift_name;
        this.icon_img = icon_img;
        this.live_id = live_id;
    }

    public String getFrom_user_nickname() {
        return from_user_nickname;
    }

    public void setFrom_user_nickname(String from_user_nickname) {
        this.from_user_nickname = from_user_nickname;
    }

    public String getTo_user_nickname() {
        return to_user_nickname;
    }

    public void setTo_user_nickname(String to_user_nickname) {
        this.to_user_nickname = to_user_nickname;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(String icon_img) {
        this.icon_img = icon_img;
    }

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }
}
