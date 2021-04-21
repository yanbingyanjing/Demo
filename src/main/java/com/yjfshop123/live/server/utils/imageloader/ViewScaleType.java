package com.yjfshop123.live.server.utils.imageloader;

import android.widget.ImageView;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public enum ViewScaleType {
    FIT_INSIDE,
    CROP;

    private ViewScaleType() {
    }

    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (imageView.getScaleType()) {
            case FIT_CENTER:
            case FIT_XY:
            case FIT_START:
            case FIT_END:
            case CENTER_INSIDE:
                return FIT_INSIDE;
            case MATRIX:
            case CENTER:
            case CENTER_CROP:
            default:
                return CROP;
        }
    }
}