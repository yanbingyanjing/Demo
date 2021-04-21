package com.yjfshop123.live.ui.widget;

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

public class ShowTargetDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;
    private String logo;
    private String top_title;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_show_target;
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
        if (!TextUtils.isEmpty(top_title))
            ((TextView) mRootView.findViewById(R.id.top_title)).setText(top_title);
        if (!TextUtils.isEmpty(title))
            ((TextView) mRootView.findViewById(R.id.title)).setText(title);
        if (isRealReward) {
            ((TextView) mRootView.findViewById(R.id.confir)).setText(getString(R.string.receive_reward_detail));
            if (!TextUtils.isEmpty(logo))
                Glide.with(this).load(logo).into((ImageView) mRootView.findViewById(R.id.logoutLayout));
        }
        if (!isRealReward) {
            if (!TextUtils.isEmpty(title)) {
                if (title.contains(getString(R.string.gold_egg)))
                    Glide.with(this).load(R.mipmap.to_exchange_logo).into((ImageView) mRootView.findViewById(R.id.logoutLayout));
                else if (title.contains(getString(R.string.yin_egg)))
                    Glide.with(this).load(R.mipmap.tip_silver_egg).into((ImageView) mRootView.findViewById(R.id.logoutLayout));
            }

        }
        if (!title.contains(getString(R.string.gold_egg)) && !title.contains(getString(R.string.yin_egg))) {

        }
        //    ((ImageView) mRootView.findViewById(R.id.logoutLayout)).setImageResource(logo);
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
    }

    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopTitle(String top_title) {
        this.top_title = top_title;
    }

    public void setLogo(String res) {

        logo = res;
    }

    boolean isRealReward = false;

    public void isRealReward(boolean isRealReward) {

        this.isRealReward = isRealReward;
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
