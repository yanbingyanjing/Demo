package com.yjfshop123.live.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.ui.activity.TargetReciveDetailActivity;
import com.yjfshop123.live.ui.activity.TargetRewardActivity;

public class ShowImgDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;
    private int logo = R.mipmap.to_exchange_logo;
    public static  void   show(Activity context, String title){
//        ShowImgDialog dialogFragment = new ShowImgDialog();
//
//        dialogFragment.setTitle(title);
//        dialogFragment.setOnClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//
//        dialogFragment.show(context.get(), "ShowTargetDialog");
    }
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_show_img;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!TextUtils.isEmpty(title))
            ((TextView) mRootView.findViewById(R.id.title)).setText(title);
        ((TextView) mRootView.findViewById(R.id.title)).setTextColor(getContext().getResources().getColor(titleColorlor));
        ((ImageView) mRootView.findViewById(R.id.logoutLayout)).setImageResource(logo);
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        mRootView.findViewById(R.id.cancel).setOnClickListener(this);
        mRootView.setBackgroundResource(bg);
        mRootView.findViewById(R.id.line_one).setBackgroundResource(lineColorlor);
        mRootView.findViewById(R.id.line_two).setBackgroundResource(lineColorlor);
    }

    int bg = R.drawable.bg_dialog_show_img;

    public void setBgDrawable(int bg) {
        this.bg = bg;
    }
    int titleColorlor=R.color.white;
    int lineColorlor=R.color.color_201f1d;
    public void setLineColorlor(int lineColorlor) {
        this.lineColorlor = lineColorlor;
    }
    public void setTitleColor(int titleColorlor) {
        this.titleColorlor = titleColorlor;
    }
    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogo(int res) {

        logo = res;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.cancel) {
        } else if (i == R.id.confir) {
            if (mCallback != null) {
                mCallback.onClick(v);
            }
        }
    }

}
