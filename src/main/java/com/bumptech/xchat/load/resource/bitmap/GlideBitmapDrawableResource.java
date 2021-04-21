package com.bumptech.xchat.load.resource.bitmap;

import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.drawable.DrawableResource;
import com.bumptech.xchat.util.Util;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;

/**
 * A resource wrapper for {@link GlideBitmapDrawable}.
 */
public class GlideBitmapDrawableResource extends DrawableResource<GlideBitmapDrawable> {
    private final BitmapPool bitmapPool;

    public GlideBitmapDrawableResource(GlideBitmapDrawable drawable, BitmapPool bitmapPool) {
        super(drawable);
        this.bitmapPool = bitmapPool;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(drawable.getBitmap());
    }

    @Override
    public void recycle() {
        bitmapPool.put(drawable.getBitmap());
    }
}
