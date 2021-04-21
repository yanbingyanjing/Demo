package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
    public SimpleBitmapDisplayer() {
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }
}

