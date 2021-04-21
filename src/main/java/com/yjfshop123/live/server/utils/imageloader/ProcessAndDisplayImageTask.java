package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;

import com.yjfshop123.live.net.utils.NLog;


/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class ProcessAndDisplayImageTask implements Runnable {
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private final ImageLoaderEngine engine;
    private final Bitmap bitmap;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, Handler handler) {
        this.engine = engine;
        this.bitmap = bitmap;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
    }

    public void run() {
        NLog.d("PostProcess image before displaying [%s]", new Object[]{this.imageLoadingInfo.memoryCacheKey});
        BitmapProcessor processor = this.imageLoadingInfo.options.getPostProcessor();
        Bitmap processedBitmap = processor.process(this.bitmap);
        DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(processedBitmap, this.imageLoadingInfo, this.engine, LoadedFrom.MEMORY_CACHE);
        LoadAndDisplayImageTask.runTask(displayBitmapTask, this.imageLoadingInfo.options.isSyncLoading(), this.handler, this.engine);
    }
}
