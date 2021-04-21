package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.utils.CommonUtils;

public class PaddingItemDecoration1 extends RecyclerView.ItemDecoration {

    private int p_1;
    private int p_2;

    public PaddingItemDecoration1(Context context, int p1, int p2) {
        this.p_1 = CommonUtils.dip2px(context, p1);
        this.p_2 = CommonUtils.dip2px(context, p2);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = p_2;
        int position = parent.getChildLayoutPosition(view);

        if (position == 0 || position == 1 || position == 2){
            outRect.top = p_2;
        }

        if (position % 3 == 0){
            //左
            outRect.left = p_2;
            outRect.right = p_1;
        } else if (position % 3 == 1){
            //中
            outRect.left = p_1;
            outRect.right = p_1;
        } else if (position % 3 == 2){
            //右
            outRect.left = p_1;
            outRect.right = p_2;
        }
    }
}