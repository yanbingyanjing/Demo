package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.utils.imageloader.FailReason.FailType;
import com.yjfshop123.live.server.utils.imageloader.ImageDownloader.Scheme;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class LoadAndDisplayImageTask implements Runnable, IoUtils.CopyListener {
    private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
    private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
    private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
    private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
    private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
    private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_DISK_CACHE = "Load image from disk cache [%s]";
    private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disk cache [%s]";
    private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
    private static final String LOG_CACHE_IMAGE_ON_DISK = "Cache image on disk [%s]";
    private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK = "Process image before cache on disk [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
    private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";
    private static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
    private static final String ERROR_POST_PROCESSOR_NULL = "Post-processor returned null [%s]";
    private static final String ERROR_PROCESSOR_FOR_DISK_CACHE_NULL = "Bitmap processor for disk cache returned null [%s]";
    private final ImageLoaderEngine engine;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;
    private final ImageLoaderConfiguration configuration;
    private final ImageDownloader downloader;
    private final ImageDownloader networkDeniedDownloader;
    private final ImageDownloader slowNetworkDownloader;
    private final ImageDecoder decoder;
    final String uri;
    private final String memoryCacheKey;
    final ImageAware imageAware;
    private final ImageSize targetSize;
    final DisplayImageOptions options;
    final ImageLoadingListener listener;
    final ImageLoadingProgressListener progressListener;
    private final boolean syncLoading;
    private LoadedFrom loadedFrom;

    public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler) {
        this.loadedFrom = LoadedFrom.NETWORK;
        this.engine = engine;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
        this.configuration = engine.configuration;
        this.downloader = this.configuration.downloader;
        this.networkDeniedDownloader = this.configuration.networkDeniedDownloader;
        this.slowNetworkDownloader = this.configuration.slowNetworkDownloader;
        this.decoder = this.configuration.decoder;
        this.uri = imageLoadingInfo.uri;
        this.memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        this.imageAware = imageLoadingInfo.imageAware;
        this.targetSize = imageLoadingInfo.targetSize;
        this.options = imageLoadingInfo.options;
        this.listener = imageLoadingInfo.listener;
        this.progressListener = imageLoadingInfo.progressListener;
        this.syncLoading = this.options.isSyncLoading();
    }

    public void run() {
        if (!this.waitIfPaused()) {
            if (!this.delayIfNeed()) {
                ReentrantLock loadFromUriLock = this.imageLoadingInfo.loadFromUriLock;
                NLog.d("Start display image task [%s]", new Object[]{this.memoryCacheKey});
                if (loadFromUriLock.isLocked()) {
                    NLog.d("Image already is loading. Waiting... [%s]", new Object[]{this.memoryCacheKey});
                }

                loadFromUriLock.lock();

                Bitmap bmp;
                try {
                    this.checkTaskNotActual();
                    bmp = this.configuration.memoryCache.get(this.memoryCacheKey);
                    if (bmp != null && !bmp.isRecycled()) {
                        this.loadedFrom = LoadedFrom.MEMORY_CACHE;
                        NLog.d("...Get cached bitmap from memory after waiting. [%s]", new Object[]{this.memoryCacheKey});
                    } else {
                        bmp = this.tryLoadBitmap();
                        if (bmp == null) {
                            return;
                        }

                        this.checkTaskNotActual();
                        this.checkTaskInterrupted();
                        if (this.options.shouldPreProcess()) {
                            NLog.d("PreProcess image before caching in memory [%s]", new Object[]{this.memoryCacheKey});
                            bmp = this.options.getPreProcessor().process(bmp);
                            if (bmp == null) {
                                NLog.e("Pre-processor returned null [%s]", new Object[]{this.memoryCacheKey});
                            }
                        }

                        if (bmp != null && this.options.isCacheInMemory()) {
                            NLog.d("Cache image in memory [%s]", new Object[]{this.memoryCacheKey});
                            this.configuration.memoryCache.put(this.memoryCacheKey, bmp);
                        }
                    }

                    if (bmp != null && this.options.shouldPostProcess()) {
                        NLog.d("PostProcess image before displaying [%s]", new Object[]{this.memoryCacheKey});
                        bmp = this.options.getPostProcessor().process(bmp);
                        if (bmp == null) {
                            NLog.e("Post-processor returned null [%s]", new Object[]{this.memoryCacheKey});
                        }
                    }

                    this.checkTaskNotActual();
                    this.checkTaskInterrupted();
                } catch (LoadAndDisplayImageTask.TaskCancelledException var7) {
                    this.fireCancelEvent();
                    return;
                } finally {
                    loadFromUriLock.unlock();
                }

                DisplayBitmapTask var3 = new DisplayBitmapTask(bmp, this.imageLoadingInfo, this.engine, this.loadedFrom);
                runTask(var3, this.syncLoading, this.handler, this.engine);
            }
        }
    }

    private boolean waitIfPaused() {
        AtomicBoolean pause = this.engine.getPause();
        if (pause.get()) {
            synchronized (this.engine.getPauseLock()) {
                if (pause.get()) {
                    NLog.d("ImageLoader is paused. Waiting...  [%s]", new Object[]{this.memoryCacheKey});

                    try {
                        this.engine.getPauseLock().wait();
                    } catch (InterruptedException var5) {
                        NLog.e("Task was interrupted [%s]", new Object[]{this.memoryCacheKey});
                        return true;
                    }

                    NLog.d(".. Resume loading [%s]", new Object[]{this.memoryCacheKey});
                }
            }
        }

        return this.isTaskNotActual();
    }

    private boolean delayIfNeed() {
        if (this.options.shouldDelayBeforeLoading()) {
            NLog.d("Delay %d ms before loading...  [%s]", new Object[]{this.options.getDelayBeforeLoading(), this.memoryCacheKey});

            try {
                Thread.sleep((long) this.options.getDelayBeforeLoading());
            } catch (InterruptedException var2) {
                NLog.e("Task was interrupted [%s]", new Object[]{this.memoryCacheKey});
                return true;
            }

            return this.isTaskNotActual();
        } else {
            return false;
        }
    }

    private Bitmap tryLoadBitmap() throws LoadAndDisplayImageTask.TaskCancelledException {
        Bitmap bitmap = null;

        try {
            File imageFile = this.configuration.diskCache.get(this.uri);
            if (imageFile != null && imageFile.exists() && imageFile.length() > 0L) {
                NLog.d("Load image from disk cache [%s]", new Object[]{this.memoryCacheKey});
                this.loadedFrom = LoadedFrom.DISC_CACHE;
                this.checkTaskNotActual();
                bitmap = this.decodeImage(Scheme.FILE.wrap(imageFile.getAbsolutePath()));
            }

            if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                NLog.d("Load image from network [%s]", new Object[]{this.memoryCacheKey});
                this.loadedFrom = LoadedFrom.NETWORK;
                String imageUriForDecoding = this.uri;
                if (this.options.isCacheOnDisk() && this.tryCacheImageOnDisk()) {
                    imageFile = this.configuration.diskCache.get(this.uri);
                    if (imageFile != null) {
                        imageUriForDecoding = Scheme.FILE.wrap(imageFile.getAbsolutePath());
                    }
                }

                this.checkTaskNotActual();
                bitmap = this.decodeImage(imageUriForDecoding);
                if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                    this.fireFailEvent(FailType.DECODING_ERROR, (Throwable) null);
                }
            }
        } catch (IllegalStateException var4) {
            this.fireFailEvent(FailType.NETWORK_DENIED, (Throwable) null);
        } catch (LoadAndDisplayImageTask.TaskCancelledException var5) {
            throw var5;
        } catch (IOException var6) {
            NLog.e(var6);
            this.fireFailEvent(FailType.IO_ERROR, var6);
        } catch (OutOfMemoryError var7) {
            NLog.e(var7);
            this.fireFailEvent(FailType.OUT_OF_MEMORY, var7);
        } catch (Throwable var8) {
            NLog.e(var8);
            this.fireFailEvent(FailType.UNKNOWN, var8);
        }

        return bitmap;
    }

    private Bitmap decodeImage(String imageUri) throws IOException {
        ViewScaleType viewScaleType = this.imageAware.getScaleType();
        ImageDecodingInfo decodingInfo = new ImageDecodingInfo(this.memoryCacheKey, imageUri, this.uri, this.targetSize, viewScaleType, this.getDownloader(), this.options);
        return this.decoder.decode(decodingInfo);
    }

    private boolean tryCacheImageOnDisk() throws LoadAndDisplayImageTask.TaskCancelledException {
        NLog.d("Cache image on disk [%s]", new Object[]{this.memoryCacheKey});

        boolean loaded;
        try {
            loaded = this.downloadImage();
            if (loaded) {
                int width = this.configuration.maxImageWidthForDiskCache;
                int height = this.configuration.maxImageHeightForDiskCache;
                if (width > 0 || height > 0) {
                    NLog.d("Resize image in disk cache [%s]", new Object[]{this.memoryCacheKey});
                    this.resizeAndSaveImage(width, height);
                }
            }
        } catch (IOException var4) {
            NLog.e(var4);
            loaded = false;
        }

        return loaded;
    }

    private boolean downloadImage() throws IOException {
        InputStream is = this.getDownloader().getStream(this.uri, this.options.getExtraForDownloader());
        if (is == null) {
            NLog.e("No stream for image [%s]", new Object[]{this.memoryCacheKey});
            return false;
        } else {
            boolean var2;
            try {
                var2 = this.configuration.diskCache.save(this.uri, is, this);
            } finally {
                IoUtils.closeSilently(is);
            }

            return var2;
        }
    }

    private boolean resizeAndSaveImage(int maxWidth, int maxHeight) throws IOException {
        boolean saved = false;
        File targetFile = this.configuration.diskCache.get(this.uri);
        if (targetFile != null && targetFile.exists()) {
            ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
            DisplayImageOptions specialOptions = (new DisplayImageOptions.Builder()).cloneFrom(this.options).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
            ImageDecodingInfo decodingInfo = new ImageDecodingInfo(this.memoryCacheKey, ImageDownloader.Scheme.FILE.wrap(targetFile.getAbsolutePath()), this.uri, targetImageSize, ViewScaleType.FIT_INSIDE, this.getDownloader(), specialOptions);
            Bitmap bmp = this.decoder.decode(decodingInfo);
            if (bmp != null && this.configuration.processorForDiskCache != null) {
                NLog.d("Process image before cache on disk [%s]", new Object[]{this.memoryCacheKey});
                bmp = this.configuration.processorForDiskCache.process(bmp);
                if (bmp == null) {
                    NLog.e("Bitmap processor for disk cache returned null [%s]", new Object[]{this.memoryCacheKey});
                }
            }

            if (bmp != null) {
                saved = this.configuration.diskCache.save(this.uri, bmp);
                bmp.recycle();
            }
        }

        return saved;
    }

    public boolean onBytesCopied(int current, int total) {
        return this.syncLoading || this.fireProgressEvent(current, total);
    }

    private boolean fireProgressEvent(final int current, final int total) {
        if (!this.isTaskInterrupted() && !this.isTaskNotActual()) {
            if (this.progressListener != null) {
                Runnable r = new Runnable() {
                    public void run() {
                        LoadAndDisplayImageTask.this.progressListener.onProgressUpdate(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView(), current, total);
                    }
                };
                runTask(r, false, this.handler, this.engine);
            }

            return true;
        } else {
            return false;
        }
    }

    private void fireFailEvent(final FailReason.FailType failType, final Throwable failCause) {
        if (!this.syncLoading && !this.isTaskInterrupted() && !this.isTaskNotActual()) {
            Runnable r = new Runnable() {
                public void run() {
                    if (LoadAndDisplayImageTask.this.options.shouldShowImageOnFail()) {
                        if (LoadAndDisplayImageTask.this.options.getDisplayer() != null) {
                            Bitmap bitmap = LoadAndDisplayImageTask.this.options.drawableToBitmap(LoadAndDisplayImageTask.this.options.getImageOnFail(LoadAndDisplayImageTask.this.configuration.resources));
                            if (bitmap != null) {
                                LoadAndDisplayImageTask.this.options.getDisplayer().display(bitmap, LoadAndDisplayImageTask.this.imageAware, LoadedFrom.DISC_CACHE);
                            }
                        } else {
                            LoadAndDisplayImageTask.this.imageAware.setImageDrawable(LoadAndDisplayImageTask.this.options.getImageOnFail(LoadAndDisplayImageTask.this.configuration.resources));
                        }
                    }

                    LoadAndDisplayImageTask.this.listener.onLoadingFailed(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView(), new FailReason(failType, failCause));
                }
            };
            runTask(r, false, this.handler, this.engine);
        }
    }

    private void fireCancelEvent() {
        if (!this.syncLoading && !this.isTaskInterrupted()) {
            Runnable r = new Runnable() {
                public void run() {
                    LoadAndDisplayImageTask.this.listener.onLoadingCancelled(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView());
                }
            };
            runTask(r, false, this.handler, this.engine);
        }
    }

    private ImageDownloader getDownloader() {
        ImageDownloader d;
        if (this.engine.isNetworkDenied()) {
            d = this.networkDeniedDownloader;
        } else if (this.engine.isSlowNetwork()) {
            d = this.slowNetworkDownloader;
        } else {
            d = this.downloader;
        }

        return d;
    }

    private void checkTaskNotActual() throws LoadAndDisplayImageTask.TaskCancelledException {
        this.checkViewCollected();
        this.checkViewReused();
    }

    private boolean isTaskNotActual() {
        return this.isViewCollected() || this.isViewReused();
    }

    private void checkViewCollected() throws LoadAndDisplayImageTask.TaskCancelledException {
        if (this.isViewCollected()) {
            throw new LoadAndDisplayImageTask.TaskCancelledException();
        }
    }

    private boolean isViewCollected() {
        if (this.imageAware.isCollected()) {
            NLog.d("ImageAware was collected by GC. Task is cancelled. [%s]", new Object[]{this.memoryCacheKey});
            return true;
        } else {
            return false;
        }
    }

    private void checkViewReused() throws LoadAndDisplayImageTask.TaskCancelledException {
        if (this.isViewReused()) {
            throw new LoadAndDisplayImageTask.TaskCancelledException();
        }
    }

    private boolean isViewReused() {
        String currentCacheKey = this.engine.getLoadingUriForView(this.imageAware);
        boolean imageAwareWasReused = !this.memoryCacheKey.equals(currentCacheKey);
        if (imageAwareWasReused) {
            NLog.d("ImageAware is reused for another image. Task is cancelled. [%s]", new Object[]{this.memoryCacheKey});
            return true;
        } else {
            return false;
        }
    }

    private void checkTaskInterrupted() throws LoadAndDisplayImageTask.TaskCancelledException {
        if (this.isTaskInterrupted()) {
            throw new LoadAndDisplayImageTask.TaskCancelledException();
        }
    }

    private boolean isTaskInterrupted() {
        if (Thread.interrupted()) {
            NLog.d("Task was interrupted [%s]", new Object[]{this.memoryCacheKey});
            return true;
        } else {
            return false;
        }
    }

    String getLoadingUri() {
        return this.uri;
    }

    static void runTask(Runnable r, boolean sync, Handler handler, ImageLoaderEngine engine) {
        if (sync) {
            r.run();
        } else if (handler == null) {
            engine.fireCallback(r);
        } else {
            handler.post(r);
        }

    }

    class TaskCancelledException extends Exception {
        TaskCancelledException() {
        }
    }
}
