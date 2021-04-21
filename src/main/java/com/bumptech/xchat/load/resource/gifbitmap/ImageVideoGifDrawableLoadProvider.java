package com.bumptech.xchat.load.resource.gifbitmap;

import android.graphics.Bitmap;

import com.bumptech.xchat.load.Encoder;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.ResourceEncoder;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.model.ImageVideoWrapper;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.load.resource.gif.GifDrawable;
import com.bumptech.xchat.provider.DataLoadProvider;
import com.bumptech.xchat.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.xchat.load.model.ImageVideoWrapper;
import com.bumptech.xchat.load.resource.file.FileToStreamDecoder;
import com.bumptech.xchat.load.resource.gif.GifDrawable;
import com.bumptech.xchat.provider.DataLoadProvider;

import java.io.File;
import java.io.InputStream;

/**
 * An {@link DataLoadProvider} that can load either an
 * {@link GifDrawable} or an {@link Bitmap} from either an
 * {@link InputStream} or an {@link android.os.ParcelFileDescriptor}.
 */
public class ImageVideoGifDrawableLoadProvider implements DataLoadProvider<ImageVideoWrapper, GifBitmapWrapper> {
    private final ResourceDecoder<File, GifBitmapWrapper> cacheDecoder;
    private final ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> sourceDecoder;
    private final ResourceEncoder<GifBitmapWrapper> encoder;
    private final Encoder<ImageVideoWrapper> sourceEncoder;

    public ImageVideoGifDrawableLoadProvider(DataLoadProvider<ImageVideoWrapper, Bitmap> bitmapProvider,
            DataLoadProvider<InputStream, GifDrawable> gifProvider, BitmapPool bitmapPool) {

        final GifBitmapWrapperResourceDecoder decoder = new GifBitmapWrapperResourceDecoder(
                bitmapProvider.getSourceDecoder(),
                gifProvider.getSourceDecoder(),
                bitmapPool
        );
        cacheDecoder = new FileToStreamDecoder<GifBitmapWrapper>(new GifBitmapWrapperStreamResourceDecoder(decoder));
        sourceDecoder = decoder;
        encoder = new GifBitmapWrapperResourceEncoder(bitmapProvider.getEncoder(), gifProvider.getEncoder());

        //TODO: what about the gif provider?
        sourceEncoder = bitmapProvider.getSourceEncoder();
    }

    @Override
    public ResourceDecoder<File, GifBitmapWrapper> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ImageVideoWrapper> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<GifBitmapWrapper> getEncoder() {
        return encoder;
    }
}
