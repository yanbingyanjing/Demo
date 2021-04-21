package com.yjfshop123.live.otc.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;

public class OtcTipsDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_otc_tips;
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
        //    ((ImageView) mRootView.findViewById(R.id.logoutLayout)).setImageResource(logo);
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        if (showImg) mRootView.findViewById(R.id.logoutLayout).setVisibility(View.VISIBLE);
        else mRootView.findViewById(R.id.logoutLayout).setVisibility(View.GONE);
    }

    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    boolean showImg = true;

    public void setShowImg(boolean showImg) {
        this.showImg = showImg;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.confir) {
            if (mCallback != null) {
                mCallback.onClick(v);
            }
        }
    }

}
