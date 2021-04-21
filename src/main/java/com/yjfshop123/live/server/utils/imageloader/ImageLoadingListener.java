package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;
import android.view.View;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface ImageLoadingListener {
    void onLoadingStarted(String var1, View var2);

    void onLoadingFailed(String var1, View var2, FailReason var3);

    void onLoadingComplete(String var1, View var2, Bitmap var3);

    void onLoadingCancelled(String var1, View var2);
}
