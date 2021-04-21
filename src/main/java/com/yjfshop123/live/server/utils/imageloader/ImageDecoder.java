package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface ImageDecoder {
    Bitmap decode(ImageDecodingInfo var1) throws IOException;
}
