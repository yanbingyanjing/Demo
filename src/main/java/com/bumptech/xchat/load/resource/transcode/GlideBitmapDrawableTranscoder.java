package com.bumptech.xchat.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawableResource;
import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawableResource;

/**
 * An {@link ResourceTranscoder} that converts
 * {@link Bitmap}s into {@link android.graphics.drawable.BitmapDrawable}s.
 */
public class GlideBitmapDrawableTranscoder implements ResourceTranscoder<Bitmap, GlideBitmapDrawable> {
    private final Resources resources;
    private final BitmapPool bitmapPool;

    public GlideBitmapDrawableTranscoder(Context context) {
        this(context.getResources(), Glide.get(context).getBitmapPool());
    }

    public GlideBitmapDrawableTranscoder(Resources resources, BitmapPool bitmapPool) {
        this.resources = resources;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<GlideBitmapDrawable> transcode(Resource<Bitmap> toTranscode) {
        GlideBitmapDrawable drawable = new GlideBitmapDrawable(resources, toTranscode.get());
        return new GlideBitmapDrawableResource(drawable, bitmapPool);
    }

    @Override
    public String getId() {
        return "GlideBitmapDrawableTranscoder.com.bumptech.xchat.load.resource.transcode";
    }
}
