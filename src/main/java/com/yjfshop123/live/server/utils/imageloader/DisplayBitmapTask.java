package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

import com.yjfshop123.live.net.utils.NLog;


/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class DisplayBitmapTask implements Runnable {
    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "Display image in ImageAware (loaded from %1$s) [%2$s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
    private final Bitmap bitmap;
    private final String imageUri;
    private final ImageAware imageAware;
    private final String memoryCacheKey;
    private final BitmapDisplayer displayer;
    private final ImageLoadingListener listener;
    private final ImageLoaderEngine engine;
    private final LoadedFrom loadedFrom;

    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine, LoadedFrom loadedFrom) {
        this.bitmap = bitmap;
        this.imageUri = imageLoadingInfo.uri;
        this.imageAware = imageLoadingInfo.imageAware;
        this.memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        this.displayer = imageLoadingInfo.options.getDisplayer();
        this.listener = imageLoadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = loadedFrom;
    }

    public void run() {
        if (this.imageAware.isCollected()) {
            NLog.d("ImageAware was collected by GC. Task is cancelled. [%s]", new Object[]{this.memoryCacheKey});
            this.listener.onLoadingCancelled(this.imageUri, this.imageAware.getWrappedView());
        } else if (this.isViewWasReused()) {
            NLog.d("ImageAware is reused for another image. Task is cancelled. [%s]", new Object[]{this.memoryCacheKey});
            this.listener.onLoadingCancelled(this.imageUri, this.imageAware.getWrappedView());
        } else {
            NLog.d("Display image in ImageAware (loaded from %1$s) [%2$s]", new Object[]{this.loadedFrom, this.memoryCacheKey});
            this.displayer.display(this.bitmap, this.imageAware, this.loadedFrom);
            this.engine.cancelDisplayTaskFor(this.imageAware);
            this.listener.onLoadingComplete(this.imageUri, this.imageAware.getWrappedView(), this.bitmap);
        }

    }

    private boolean isViewWasReused() {
        String currentCacheKey = this.engine.getLoadingUriForView(this.imageAware);
        return !this.memoryCacheKey.equals(currentCacheKey);
    }
}
