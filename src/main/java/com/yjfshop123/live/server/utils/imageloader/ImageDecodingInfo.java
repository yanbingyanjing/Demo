package com.yjfshop123.live.server.utils.imageloader;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class ImageDecodingInfo {
    private final String imageKey;
    private final String imageUri;
    private final String originalImageUri;
    private final ImageSize targetSize;
    private final ImageScaleType imageScaleType;
    private final ViewScaleType viewScaleType;
    private final ImageDownloader downloader;
    private final Object extraForDownloader;
    private final boolean considerExifParams;
    private final BitmapFactory.Options decodingOptions;

    public ImageDecodingInfo(String imageKey, String imageUri, String originalImageUri, ImageSize targetSize, ViewScaleType viewScaleType, ImageDownloader downloader, DisplayImageOptions displayOptions) {
        this.imageKey = imageKey;
        this.imageUri = imageUri;
        this.originalImageUri = originalImageUri;
        this.targetSize = targetSize;
        this.imageScaleType = displayOptions.getImageScaleType();
        this.viewScaleType = viewScaleType;
        this.downloader = downloader;
        this.extraForDownloader = displayOptions.getExtraForDownloader();
        this.considerExifParams = displayOptions.isConsiderExifParams();
        this.decodingOptions = new BitmapFactory.Options();
        this.copyOptions(displayOptions.getDecodingOptions(), this.decodingOptions);
    }

    private void copyOptions(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inDensity = srcOptions.inDensity;
        destOptions.inDither = srcOptions.inDither;
        destOptions.inInputShareable = srcOptions.inInputShareable;
        destOptions.inJustDecodeBounds = srcOptions.inJustDecodeBounds;
        destOptions.inPreferredConfig = srcOptions.inPreferredConfig;
        destOptions.inPurgeable = srcOptions.inPurgeable;
        destOptions.inSampleSize = srcOptions.inSampleSize;
        destOptions.inScaled = srcOptions.inScaled;
        destOptions.inScreenDensity = srcOptions.inScreenDensity;
        destOptions.inTargetDensity = srcOptions.inTargetDensity;
        destOptions.inTempStorage = srcOptions.inTempStorage;
        if (Build.VERSION.SDK_INT >= 10) {
            this.copyOptions10(srcOptions, destOptions);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            this.copyOptions11(srcOptions, destOptions);
        }

    }

    @TargetApi(10)
    private void copyOptions10(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inPreferQualityOverSpeed = srcOptions.inPreferQualityOverSpeed;
    }

    @TargetApi(11)
    private void copyOptions11(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inBitmap = srcOptions.inBitmap;
        destOptions.inMutable = srcOptions.inMutable;
    }

    public String getImageKey() {
        return this.imageKey;
    }

    public String getImageUri() {
        return this.imageUri;
    }

    public String getOriginalImageUri() {
        return this.originalImageUri;
    }

    public ImageSize getTargetSize() {
        return this.targetSize;
    }

    public ImageScaleType getImageScaleType() {
        return this.imageScaleType;
    }

    public ViewScaleType getViewScaleType() {
        return this.viewScaleType;
    }

    public ImageDownloader getDownloader() {
        return this.downloader;
    }

    public Object getExtraForDownloader() {
        return this.extraForDownloader;
    }

    public boolean shouldConsiderExifParams() {
        return this.considerExifParams;
    }

    public BitmapFactory.Options getDecodingOptions() {
        return this.decodingOptions;
    }
}
