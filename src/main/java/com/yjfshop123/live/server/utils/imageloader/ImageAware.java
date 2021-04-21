package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface ImageAware {

    int getWidth();

    int getHeight();

    ViewScaleType getScaleType();

    View getWrappedView();

    boolean isCollected();

    int getId();

    boolean setImageDrawable(Drawable var1);

    boolean setImageBitmap(Bitmap var1);

}
