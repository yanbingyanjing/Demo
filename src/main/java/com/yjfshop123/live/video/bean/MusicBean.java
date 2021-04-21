package com.yjfshop123.live.video.bean;

public class MusicBean {
    private String title;
    private String localPath;
    private long duration;

    public MusicBean(String title, String localPath, long duration) {
        this.title = title;
        this.localPath = localPath;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
