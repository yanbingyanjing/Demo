package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PaddingItemDecoration3 extends RecyclerView.ItemDecoration {

    private int p_10;

    public PaddingItemDecoration3(Context context) {
//        this.paddingBetweenItems = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        this.p_10 = 0/*CommonUtils.dip2px(context, 5)*/;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.set(paddingBetweenItems1, 0, paddingBetweenItems1, paddingBetweenItems2);

        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
        int position = parent.getChildLayoutPosition(view);
        if (position == 0) {
            outRect.top = p_10;
        }
    }
}