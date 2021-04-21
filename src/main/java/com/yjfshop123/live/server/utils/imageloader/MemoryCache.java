package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface MemoryCache {
    boolean put(String var1, Bitmap var2);

    Bitmap get(String var1);

    Bitmap remove(String var1);

    Collection<String> keys();

    void clear();
}
