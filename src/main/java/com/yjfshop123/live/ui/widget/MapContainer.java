package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * 屏蔽 内部view滑动跟 外面视图的冲突
 */
public class MapContainer extends FrameLayout {

    private ScrollView scrollView;

    public MapContainer(Context context) {
        super(context);
    }

    public MapContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
