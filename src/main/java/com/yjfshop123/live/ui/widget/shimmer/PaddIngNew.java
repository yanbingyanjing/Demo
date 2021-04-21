package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.utils.CommonUtils;

public class PaddIngNew extends RecyclerView.ItemDecoration {

    private int p_10;

    public PaddIngNew(Context context) {
//        this.paddingBetweenItems = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        this.p_10 = CommonUtils.dip2px(context, 7f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.set(paddingBetweenItems1, 0, paddingBetweenItems1, paddingBetweenItems2);

        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
        int position = parent.getChildLayoutPosition(view);

            outRect.left = p_10;

    }
}
