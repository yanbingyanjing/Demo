package com.yjfshop123.live.ui.widget;

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

public class CommunityPopWindow extends PopupWindow {

    @SuppressLint("InflateParams")
    public CommunityPopWindow(final Activity context, final CPWClickListener mCPWClickListener) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popupwindow_community, null);

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


        RelativeLayout popupwindow_community_rl_1 = content.findViewById(R.id.popupwindow_community_rl_1);
        RelativeLayout popupwindow_community_rl_2 = content.findViewById(R.id.popupwindow_community_rl_2);
        RelativeLayout popupwindow_community_rl_3 = content.findViewById(R.id.popupwindow_community_rl_3);
        RelativeLayout popupwindow_community_rl_4 = content.findViewById(R.id.popupwindow_community_rl_4);

        popupwindow_community_rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_1();
            }

        });

        popupwindow_community_rl_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_2();
            }

        });

        popupwindow_community_rl_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_3();
            }

        });

        popupwindow_community_rl_4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mCPWClickListener.onCPWClick_4();
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
        void onCPWClick_3();
        void onCPWClick_4();
    }

    public void setOnCPWClickListener(CPWClickListener listener){
        this.mCPWClickListener = listener;
    }

}


