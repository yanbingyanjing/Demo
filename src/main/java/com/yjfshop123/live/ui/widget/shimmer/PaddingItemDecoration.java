package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.utils.CommonUtils;

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    private int type;

    private int p_4;
    private int p_8;

    public PaddingItemDecoration(Context context, int type) {
        this.type = type;

//        this.paddingBetweenItems = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());

        this.p_4 = CommonUtils.dip2px(context, 4);
        this.p_8 = CommonUtils.dip2px(context, 8);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.set(paddingBetweenItems1, 0, paddingBetweenItems1, paddingBetweenItems2);

        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = p_8;
        int position = parent.getChildLayoutPosition(view);
        if (position > 0) {
            if (type == 1) {//大图
                outRect.left = p_8;
                outRect.right = p_8;
            }else if (type == 2) {//中图
                if (position % 2==0){
                    //右边 item
                    outRect.left = p_4;
                    outRect.right = p_8;
                }else {
                    //左边 item
                    outRect.left = p_8;
                    outRect.right = p_4;
                }
            }else if (type == 3) {//小图
                outRect.bottom = 0;
                outRect.left = 0;
            }
        }
    }
}