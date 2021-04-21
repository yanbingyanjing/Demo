package com.yjfshop123.live.model;

public class ModelImg {
    public ModelImg(int position, String img) {
        this.position = position;
        this.img = img;
    }

    private int position;
    private String img;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
