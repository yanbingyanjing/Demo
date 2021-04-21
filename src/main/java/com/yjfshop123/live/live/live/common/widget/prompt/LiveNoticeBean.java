package com.yjfshop123.live.live.live.common.widget.prompt;

public class LiveNoticeBean {
    private int home_id;
    private String content;
    private boolean isClose;

    public LiveNoticeBean(int home_id, String content, boolean isClose) {
        this.home_id = home_id;
        this.content = content;
        this.isClose = isClose;
    }

    public int getHome_id() {
        return home_id;
    }

    public void setHome_id(int home_id) {
        this.home_id = home_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }
}
