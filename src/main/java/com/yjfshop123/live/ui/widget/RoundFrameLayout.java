package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yjfshop123.live.utils.CommonUtils;

/**
 * 圆角 容器
 */
public class RoundFrameLayout extends FrameLayout {


    public RoundFrameLayout(Context context) {
        super(context);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), CommonUtils.dip2px(getContext(), 8), CommonUtils.dip2px(getContext(), 8), Path.Direction.CW);
        if (Build.VERSION.SDK_INT >= 28){
            canvas.clipPath(path);
        }else {
            canvas.clipPath(path, Region.Op.REPLACE);
        }
        super.dispatchDraw(canvas);
    }
}
