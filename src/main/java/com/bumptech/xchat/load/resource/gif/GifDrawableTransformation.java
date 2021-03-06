package com.bumptech.xchat.load.resource.gif;

import android.graphics.Bitmap;

import com.bumptech.xchat.load.Transformation;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.bitmap.BitmapResource;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;

/**
 * An {@link Transformation} that wraps a transformation for a {@link Bitmap}
 * and can apply it to every frame of any {@link GifDrawable}.
 */
public class GifDrawableTransformation implements Transformation<GifDrawable> {
    private final Transformation<Bitmap> wrapped;
    private final BitmapPool bitmapPool;

    public GifDrawableTransformation(Transformation<Bitmap> wrapped, BitmapPool bitmapPool) {
        this.wrapped = wrapped;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<GifDrawable> transform(Resource<GifDrawable> resource, int outWidth, int outHeight) {
        GifDrawable drawable = resource.get();

        // The drawable needs to be initialized with the correct width and height in order for a view displaying it
        // to end up with the right dimensions. Since our transformations may arbitrarily modify the dimensions of
        // our gif, here we create a stand in for a frame and pass it to the transformation to see what the final
        // transformed dimensions will be so that our drawable can report the correct intrinsic width and height.
        Bitmap firstFrame = resource.get().getFirstFrame();
        Resource<Bitmap> bitmapResource = new BitmapResource(firstFrame, bitmapPool);
        Resource<Bitmap> transformed = wrapped.transform(bitmapResource, outWidth, outHeight);
        Bitmap transformedFrame = transformed.get();
        if (!transformedFrame.equals(firstFrame)) {
            return new GifDrawableResource(new GifDrawable(drawable, transformedFrame, wrapped));
        } else {
            return resource;
        }
    }

    @Override
    public String getId() {
        return wrapped.getId();
    }
}
