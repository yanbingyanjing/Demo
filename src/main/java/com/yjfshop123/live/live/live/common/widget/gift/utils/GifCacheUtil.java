package com.yjfshop123.live.live.live.common.widget.gift.utils;

import com.yjfshop123.live.net.utils.MD5;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.utils.update.all.download.DownloadManager;
import com.yjfshop123.live.utils.update.all.download.OnDownloadListener;

import java.io.File;

public class GifCacheUtil {

    private static DownloadManager mDownloadManager;

    public static void getFile(String url, final CommonCallback<File> commonCallback) {
        if (commonCallback == null) {
            return;
        }
        String fileName = MD5.encrypt(url);
        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.startDownload(url, fileName, new
                OnDownloadListener() {
                    @Override
                    public void onException() {
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onSuccess() {
                        commonCallback.callback(new File(mDownloadManager.getDownloadFilePath()));
                    }

                    @Override
                    public void onFailed() {
                        commonCallback.callback(null);
                    }

                    @Override
                    public void onPaused() {
                    }

                    @Override
                    public void onCanceled() {
                        commonCallback.callback(null);
                    }
                });
    }

}

