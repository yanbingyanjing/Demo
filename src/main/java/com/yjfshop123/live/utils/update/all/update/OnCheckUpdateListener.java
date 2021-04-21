package com.yjfshop123.live.utils.update.all.update;


import com.yjfshop123.live.utils.update.all.DataBean;

/**
 * Created by Horrarndoo on 2018/2/1.
 * <p>
 */

public interface OnCheckUpdateListener {
    /**
     * 发现新版本
     *
     */
    void onFindNewVersion(DataBean bean);

    /**
     * 当前版本已是最新版本
     */
    void onNewest();
}
