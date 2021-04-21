package com.yjfshop123.live.ui.videolist.model;


public abstract class BaseItem {

    public static final int VIEW_TYPE_BOTTOM = 0;
    public static final int VIEW_TYPE_PICTURE = 1;
    public static final int VIEW_TYPE_VIDEO = 2;

    private final int mViewType;

    public BaseItem(int viewType) {
        mViewType = viewType;
    }

    public int getViewType() {
        return mViewType;
    }
}
