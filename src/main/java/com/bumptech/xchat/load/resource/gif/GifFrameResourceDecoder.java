package com.bumptech.xchat.load.resource.gif;

import android.graphics.Bitmap;

import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.bitmap.BitmapResource;
import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;

class GifFrameResourceDecoder implements ResourceDecoder<GifDecoder, Bitmap> {
    private final BitmapPool bitmapPool;

    public GifFrameResourceDecoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<Bitmap> decode(GifDecoder source, int width, int height) {
        Bitmap bitmap = source.getNextFrame();
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    @Override
    public String getId() {
        return "GifFrameResourceDecoder.com.bumptech.xchat.load.resource.gif";
    }
}
