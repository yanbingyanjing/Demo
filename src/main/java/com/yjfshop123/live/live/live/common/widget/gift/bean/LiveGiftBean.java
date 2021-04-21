package com.yjfshop123.live.live.live.common.widget.gift.bean;

import android.view.View;

public class LiveGiftBean {

    private String gift_uni_code;//id
    private String name;
    private String icon_img;//png
    private String effect_img;
    //暂时 gif svga为豪华礼物且是热门礼物
    private int style;// 礼物样式 1:图片 2:gif动图 3:svga动图
    private int ch_cat_id;// 0普通礼物 其他守护才能发
    private String coin;

    private boolean checked;
    private int page;
    private View view;

    public String getGift_uni_code() {
        return gift_uni_code;
    }

    public void setGift_uni_code(String gift_uni_code) {
        this.gift_uni_code = gift_uni_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(String icon_img) {
        this.icon_img = icon_img;
    }

    public String getEffect_img() {
        return effect_img;
    }

    public void setEffect_img(String effect_img) {
        this.effect_img = effect_img;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getCh_cat_id() {
        return ch_cat_id;
    }

    public void setCh_cat_id(int ch_cat_id) {
        this.ch_cat_id = ch_cat_id;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
