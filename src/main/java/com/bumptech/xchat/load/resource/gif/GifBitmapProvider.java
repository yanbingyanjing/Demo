package com.bumptech.xchat.load.resource.gif;


import android.graphics.Bitmap;

import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;

class GifBitmapProvider implements GifDecoder.BitmapProvider {
    private final BitmapPool bitmapPool;

    public GifBitmapProvider(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Bitmap obtain(int width, int height, Bitmap.Config config) {
        return bitmapPool.getDirty(width, height, config);
    }

    @Override
    public void release(Bitmap bitmap) {
        if (!bitmapPool.put(bitmap)) {
            bitmap.recycle();
        }
    }
}
