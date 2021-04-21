package com.yjfshop123.live.video.bean;



public class FilterBean {

    private int mImgSrc;
    private int mFilterSrc;
    private boolean mChecked;

    public FilterBean(int imgSrc, int filterSrc) {
        mImgSrc = imgSrc;
        mFilterSrc = filterSrc;
    }


    public FilterBean(int imgSrc, int filterSrc, boolean checked) {
        this(imgSrc, filterSrc);
        mChecked = checked;
    }

    public int getImgSrc() {
        return mImgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.mImgSrc = imgSrc;
    }

    public int getFilterSrc() {
        return mFilterSrc;
    }

    public void setFilterSrc(int filterSrc) {
        mFilterSrc = filterSrc;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
