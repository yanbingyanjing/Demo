package com.yjfshop123.live.utils.update.all;

/**
 * Created by Horrarndoo on 2018/2/1.
 * <p>
 */

public class DataBean {
    private String content;
    private String apkurl;
    private boolean focus;
    private int version_code;
    private String version_name;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public void setVersionCode(int version_code) {
        this.version_code = version_code;
    }

    public int getVersionCode() {
        return version_code;
    }

    public String getVersionName() {
        return version_name;
    }

    public void setVersionName(String version_name) {
        this.version_name = version_name;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "content='" + content + '\'' +
                ", apkurl='" + apkurl + '\'' +
                ", version_code=" + version_code +
                ", version_name='" + version_name + '\'' +
                '}';
    }
}
