package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.utils.CommonUtils;

public class PaddingItemDecoration2 extends RecyclerView.ItemDecoration {

    private int p_4;
    private int p_8;

    public PaddingItemDecoration2(Context context) {
        this.p_4 = CommonUtils.dip2px(context, 4);
        this.p_8 = CommonUtils.dip2px(context, 8);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = p_8;
        int position = parent.getChildLayoutPosition(view);

        if (position == 0 || position == 1){
            outRect.top = p_8;
        }

        if (position % 2==0){
            //左边 item
            outRect.left = p_8;
            outRect.right = p_4;
        }else {
            //右边 item
            outRect.left = p_4;
            outRect.right = p_8;
        }
    }
}