package com.yjfshop123.live.ui.videolist.model;

public class ImpressBean {

    private String name;
    private String color;
    private int check;

    public ImpressBean(String name, String color, int check) {
        this.name = name;
        this.color = color;
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isChecked() {
        return this.check == 1;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
