package com.bumptech.xchat.load.resource.bitmap;

import android.graphics.Bitmap;

import com.bumptech.xchat.load.DecodeFormat;
import com.bumptech.xchat.load.Encoder;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.ResourceEncoder;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.model.StreamEncoder;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.provider.DataLoadProvider;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.model.StreamEncoder;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.provider.DataLoadProvider;

import java.io.File;
import java.io.InputStream;

/**
 * An {@link DataLoadProvider} that provides decoders and encoders for decoding and caching
 * {@link Bitmap}s using {@link InputStream} data.
 */
public class StreamBitmapDataLoadProvider implements DataLoadProvider<InputStream, Bitmap> {
    private final StreamBitmapDecoder decoder;
    private final BitmapEncoder encoder;
    private final StreamEncoder sourceEncoder;
    private final FileToStreamDecoder<Bitmap> cacheDecoder;

    public StreamBitmapDataLoadProvider(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        sourceEncoder = new StreamEncoder();
        decoder = new StreamBitmapDecoder(bitmapPool, decodeFormat);
        encoder = new BitmapEncoder();
        cacheDecoder = new FileToStreamDecoder<Bitmap>(decoder);
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<InputStream, Bitmap> getSourceDecoder() {
        return decoder;
    }

    @Override
    public Encoder<InputStream> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
