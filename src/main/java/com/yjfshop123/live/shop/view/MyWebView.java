package com.yjfshop123.live.shop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollListener != null) {
            mScrollListener.onScrollChanged(t);
        }
    }

    public interface IScrollListener {
        void onScrollChanged(int scrollY);
    }

    private IScrollListener mScrollListener;

    public void setOnScrollListener(IScrollListener listener) {
        mScrollListener = listener;
    }
}

