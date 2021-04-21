package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.yjfshop123.live.R;

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    private boolean back_focused;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        back_focused = ta.getBoolean(R.styleable.MarqueeTextView_back_focused, false);
        ta.recycle();
        init();
    }

    private void init() {
        //设置文本超出部分模式
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //设置跑马灯重复次数，-1为无限重复
        this.setMarqueeRepeatLimit(-1);
        // 单行显示
        this.setSingleLine(true);
        this.setMaxLines(1);
    }

    // 焦点 聚焦
    @Override
    public boolean isFocused() {
        return true;
    }

    /*
     * Window与Window间焦点发生改变时的回调
     * */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (back_focused){
            if (hasWindowFocus){
                super.onWindowFocusChanged(hasWindowFocus);
            }
        }else {
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }

}



