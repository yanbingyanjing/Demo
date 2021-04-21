package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.yjfshop123.live.net.utils.NLog;


/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class LruDiskCache implements DiskCache {
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";
    protected DiskLruCache cache;
    private File reserveCacheDir;
    protected final FileNameGenerator fileNameGenerator;
    protected int bufferSize;
    protected Bitmap.CompressFormat compressFormat;
    protected int compressQuality;

    public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        this(cacheDir, (File)null, fileNameGenerator, cacheMaxSize, 0);
    }

    public LruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize, int cacheMaxFileCount) throws IOException {
        this.bufferSize = 32768;
        this.compressFormat = DEFAULT_COMPRESS_FORMAT;
        this.compressQuality = 100;
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir argument must be not null");
        } else if (cacheMaxSize < 0L) {
            throw new IllegalArgumentException("cacheMaxSize argument must be positive number");
        } else if (cacheMaxFileCount < 0) {
            throw new IllegalArgumentException("cacheMaxFileCount argument must be positive number");
        } else if (fileNameGenerator == null) {
            throw new IllegalArgumentException("fileNameGenerator argument must be not null");
        } else {
            if (cacheMaxSize == 0L) {
                cacheMaxSize = 9223372036854775807L;
            }

            if (cacheMaxFileCount == 0) {
                cacheMaxFileCount = 2147483647;
            }

            this.reserveCacheDir = reserveCacheDir;
            this.fileNameGenerator = fileNameGenerator;
            this.initCache(cacheDir, reserveCacheDir, cacheMaxSize, cacheMaxFileCount);
        }
    }

    private void initCache(File cacheDir, File reserveCacheDir, long cacheMaxSize, int cacheMaxFileCount) throws IOException {
        try {
            this.cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize, cacheMaxFileCount);
        } catch (IOException var7) {
            NLog.e(var7);
            if (reserveCacheDir != null) {
                this.initCache(reserveCacheDir, (File)null, cacheMaxSize, cacheMaxFileCount);
            }

            if (this.cache == null) {
                throw var7;
            }
        }

    }

    public File getDirectory() {
        return this.cache.getDirectory();
    }

    public File get(String imageUri) {
        DiskLruCache.Snapshot snapshot = null;

        Object var4;
        try {
            snapshot = this.cache.get(this.getKey(imageUri));
            File var3 = snapshot == null ? null : snapshot.getFile(0);
            return var3;
        } catch (IOException var8) {
            NLog.e(var8);
            var4 = null;
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }

        }

        return (File)var4;
    }

    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        DiskLruCache.Editor editor = this.cache.edit(this.getKey(imageUri));
        if (editor == null) {
            return false;
        } else {
            OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), this.bufferSize);
            boolean copied = false;

            try {
                copied = IoUtils.copyStream(imageStream, os, listener, this.bufferSize);
            } finally {
                IoUtils.closeSilently(os);
                if (copied) {
                    editor.commit();
                } else {
                    editor.abort();
                }

            }

            return copied;
        }
    }

    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        DiskLruCache.Editor editor = this.cache.edit(this.getKey(imageUri));
        if (editor == null) {
            return false;
        } else {
            OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), this.bufferSize);
            boolean savedSuccessfully = false;

            try {
                savedSuccessfully = bitmap.compress(this.compressFormat, this.compressQuality, os);
            } finally {
                IoUtils.closeSilently(os);
            }

            if (savedSuccessfully) {
                editor.commit();
            } else {
                editor.abort();
            }

            return savedSuccessfully;
        }
    }

    public boolean remove(String imageUri) {
        try {
            return this.cache.remove(this.getKey(imageUri));
        } catch (IOException var3) {
            NLog.e(var3);
            return false;
        }
    }

    public void close() {
        try {
            this.cache.close();
        } catch (IOException var2) {
            NLog.e(var2);
        }

        this.cache = null;
    }

    public void clear() {
        try {
            this.cache.delete();
        } catch (IOException var3) {
            NLog.e(var3);
        }

        try {
            this.initCache(this.cache.getDirectory(), this.reserveCacheDir, this.cache.getMaxSize(), this.cache.getMaxFileCount());
        } catch (IOException var2) {
            NLog.e(var2);
        }

    }

    private String getKey(String imageUri) {
        return this.fileNameGenerator.generate(imageUri);
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    static {
        DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    }
}
