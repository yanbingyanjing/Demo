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

public class MorePopWindow extends PopupWindow {

    @SuppressLint("InflateParams")
    public MorePopWindow(final Activity context, final VoiceClickListener mVoiceClickListener,
                         final VideoClickListener mVideoClickListener) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popupwindow_add, null);

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


        RelativeLayout pop_yy_task_rl = content.findViewById(R.id.pop_yy_task_rl);
        RelativeLayout pop_sp_task_rl = content.findViewById(R.id.pop_sp_task_rl);

        pop_yy_task_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorePopWindow.this.dismiss();
                mVoiceClickListener.onVoiceClick();
            }

        });

        pop_sp_task_rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MorePopWindow.this.dismiss();
                mVideoClickListener.onVideoClick();

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

    private VideoClickListener mVideoClickListener;

    public interface VideoClickListener {
        void onVideoClick();
    }

    public void setOnVideoClickListener(VideoClickListener listener){
        this.mVideoClickListener = listener;
    }


    private VoiceClickListener mVoiceClickListener;

    public interface VoiceClickListener {
        void onVoiceClick();
    }

    public void setOnVoiceClickListener(VoiceClickListener listener){
        this.mVoiceClickListener = listener;
    }
}

