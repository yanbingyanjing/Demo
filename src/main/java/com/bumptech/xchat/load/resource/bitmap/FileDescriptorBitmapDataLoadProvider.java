package com.bumptech.xchat.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.bumptech.xchat.load.DecodeFormat;
import com.bumptech.xchat.load.Encoder;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.ResourceEncoder;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.NullEncoder;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.provider.DataLoadProvider;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.provider.DataLoadProvider;

import java.io.File;

/**
 * An {@link DataLoadProvider} that provides classes for decoding and encoding
 * {@link Bitmap}s from {@link ParcelFileDescriptor} data.
 */
public class FileDescriptorBitmapDataLoadProvider implements DataLoadProvider<ParcelFileDescriptor, Bitmap> {
    private final ResourceDecoder<File, Bitmap> cacheDecoder;
    private final FileDescriptorBitmapDecoder sourceDecoder;
    private final BitmapEncoder encoder;
    private final Encoder<ParcelFileDescriptor> sourceEncoder;

    public FileDescriptorBitmapDataLoadProvider(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        cacheDecoder = new FileToStreamDecoder<Bitmap>(new StreamBitmapDecoder(bitmapPool, decodeFormat));
        sourceDecoder = new FileDescriptorBitmapDecoder(bitmapPool, decodeFormat);
        encoder = new BitmapEncoder();
        sourceEncoder = NullEncoder.get();
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ParcelFileDescriptor, Bitmap> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ParcelFileDescriptor> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
