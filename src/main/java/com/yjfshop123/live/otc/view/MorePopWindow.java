package com.yjfshop123.live.otc.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.widget.CommunityPopWindow;

public class MorePopWindow extends PopupWindow {

    @SuppressLint("InflateParams")
    public MorePopWindow(final Activity context, final CPWClickListener mCPWClickListener) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popupmwindows_otc_more, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(content);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);


        RelativeLayout order_list = content.findViewById(R.id.order_list);
        RelativeLayout shoukuan_type = content.findViewById(R.id.shoukuan_type);


        order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_1();
            }

        });

        order_list.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_2();
            }

        });

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private CPWClickListener mCPWClickListener;

    public interface CPWClickListener {
        void onCPWClick_1();
        void onCPWClick_2();
    }

    public void setOnCPWClickListener(CPWClickListener listener){
        this.mCPWClickListener = listener;
    }

}


