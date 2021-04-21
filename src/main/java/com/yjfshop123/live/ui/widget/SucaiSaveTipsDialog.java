package com.yjfshop123.live.ui.widget;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;

public class SucaiSaveTipsDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;
    private int logo = R.mipmap.to_exchange_logo;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sucai_save_tips;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }

    public void setClick(View.OnClickListener mCallback) {
        this.mCallback = mCallback;
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
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);

    }


    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.btn_cancel) {
        } else if (i == R.id.btn_confirm) {
            if (mCallback != null) {
                mCallback.onClick(v);
            }
        }
    }

}

