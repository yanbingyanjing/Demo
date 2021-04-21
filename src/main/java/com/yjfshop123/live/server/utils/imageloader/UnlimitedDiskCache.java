package com.yjfshop123.live.server.utils.imageloader;

import java.io.File;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class UnlimitedDiskCache extends BaseDiskCache {
    public UnlimitedDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
    }
}
