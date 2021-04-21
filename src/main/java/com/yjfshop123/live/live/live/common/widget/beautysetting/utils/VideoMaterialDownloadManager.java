package com.yjfshop123.live.live.live.common.widget.beautysetting.utils;

import java.util.HashMap;
import java.util.Map;


public class VideoMaterialDownloadManager {
    private Map<String, VideoMaterialDownloadProgress> mDPMap;
    private static VideoMaterialDownloadManager instance = new VideoMaterialDownloadManager();

    public static VideoMaterialDownloadManager getInstance() {
        return instance;
    }

    private VideoMaterialDownloadManager() {
        mDPMap = new HashMap<>();
    }

    public VideoMaterialDownloadProgress get(String id, String url, boolean isUnpack) {
        VideoMaterialDownloadProgress downloadProgress = mDPMap.get(url);
        if (downloadProgress == null) {
            downloadProgress = new VideoMaterialDownloadProgress(id, url, isUnpack);
            mDPMap.put(url, downloadProgress);
        }
        return downloadProgress;
    }
}
