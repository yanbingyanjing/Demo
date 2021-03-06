package com.yjfshop123.live.ui.widget.shimmer;


import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import com.yjfshop123.live.R;


/**
 *
 * 下拉刷新帮助类
 */
public class SwipeRefreshHelper {

    private SwipeRefreshHelper() {
        throw new AssertionError();
    }

    /**
     * 初始化，关联AppBarLayout，处理滑动冲突
     *
     * @param refreshLayout
     * @param appBar
     * @param listener
     */
    public static void init(final SwipeRefreshLayout refreshLayout, AppBarLayout appBar, SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setColorSchemeResources(R.color.color_style);
//        refreshLayout.setColorSchemeResources(R.color.color_theme,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_purple);
        refreshLayout.setOnRefreshListener(listener);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });
    }

    /**
     * 初始化
     *
     * @param refreshLayout
     * @param listener
     */
    public static void init(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setColorSchemeResources(R.color.color_style);
//        refreshLayout.setColorSchemeResources(R.color.color_theme,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_purple);
        refreshLayout.setOnRefreshListener(listener);
    }

    /**
     * 使能刷新
     *
     * @param refreshLayout
     * @param isEnable
     */
    public static void enableRefresh(SwipeRefreshLayout refreshLayout, boolean isEnable) {
        if (refreshLayout != null) {
            refreshLayout.setEnabled(isEnable);
        }
    }

    /**
     * 控制刷新
     *
     * @param refreshLayout
     * @param isRefresh
     */
    public static void controlRefresh(SwipeRefreshLayout refreshLayout, boolean isRefresh) {
        if (refreshLayout != null) {
            if (isRefresh != refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(isRefresh);
            }
        }
    }
}


