package com.bumptech.xchat.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.DecodeFormat;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;

import java.io.IOException;

/**
 * An {@link ResourceDecoder} for decoding {@link Bitmap}s from
 * {@link ParcelFileDescriptor} data.
 */
public class FileDescriptorBitmapDecoder implements ResourceDecoder<ParcelFileDescriptor, Bitmap> {
    private final VideoBitmapDecoder bitmapDecoder;
    private final BitmapPool bitmapPool;
    private DecodeFormat decodeFormat;

    public FileDescriptorBitmapDecoder(Context context) {
        this(Glide.get(context).getBitmapPool(), DecodeFormat.DEFAULT);
    }

    public FileDescriptorBitmapDecoder(Context context, DecodeFormat decodeFormat) {
        this(Glide.get(context).getBitmapPool(), decodeFormat);
    }

    public FileDescriptorBitmapDecoder(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this(new VideoBitmapDecoder(), bitmapPool, decodeFormat);
    }

    public FileDescriptorBitmapDecoder(VideoBitmapDecoder bitmapDecoder, BitmapPool bitmapPool,
            DecodeFormat decodeFormat) {
        this.bitmapDecoder = bitmapDecoder;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;
    }

    @Override
    public Resource<Bitmap> decode(ParcelFileDescriptor source, int width, int height) throws IOException {
        Bitmap bitmap = bitmapDecoder.decode(source, bitmapPool, width, height, decodeFormat);
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    @Override
    public String getId() {
        return "FileDescriptorBitmapDecoder.com.bumptech.xchat.load.data.bitmap";
    }
}
