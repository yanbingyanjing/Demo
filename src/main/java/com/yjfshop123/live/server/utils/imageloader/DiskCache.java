package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface DiskCache {
    File getDirectory();

    File get(String var1);

    boolean save(String var1, InputStream var2, IoUtils.CopyListener var3) throws IOException;

    boolean save(String var1, Bitmap var2) throws IOException;

    boolean remove(String var1);

    void close();

    void clear();
}