package com.bumptech.xchat.load.resource.transcode;

import android.graphics.Bitmap;

import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.xchat.load.resource.drawable.GlideDrawable;
import com.bumptech.xchat.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.xchat.load.resource.drawable.GlideDrawable;
import com.bumptech.xchat.load.resource.gif.GifDrawable;
import com.bumptech.xchat.load.resource.gifbitmap.GifBitmapWrapper;

/**
 * An {@link ResourceTranscoder} that can transcode either an
 * {@link Bitmap} or an {@link GifDrawable} into an
 * {@link android.graphics.drawable.Drawable}.
 */
public class GifBitmapWrapperDrawableTranscoder implements ResourceTranscoder<GifBitmapWrapper, GlideDrawable> {
    private final ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder;

    public GifBitmapWrapperDrawableTranscoder(
            ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder) {
        this.bitmapDrawableResourceTranscoder = bitmapDrawableResourceTranscoder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Resource<GlideDrawable> transcode(Resource<GifBitmapWrapper> toTranscode) {
        GifBitmapWrapper gifBitmap = toTranscode.get();
        Resource<Bitmap> bitmapResource = gifBitmap.getBitmapResource();

        final Resource<? extends GlideDrawable> result;
        if (bitmapResource != null) {
            result = bitmapDrawableResourceTranscoder.transcode(bitmapResource);
        } else {
            result = gifBitmap.getGifResource();
        }
        // This is unchecked but always safe, anything that extends a Drawable can be safely cast to a Drawable.
        return (Resource<GlideDrawable>) result;
    }

    @Override
    public String getId() {
        return "GifBitmapWrapperDrawableTranscoder.com.bumptech.xchat.load.resource.transcode";
    }
}
