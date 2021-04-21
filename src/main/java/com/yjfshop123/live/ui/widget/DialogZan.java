package com.yjfshop123.live.ui.widget;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;

public class DialogZan extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_zan;
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
                DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.findViewById(R.id.logoutLayout).getLayoutParams();
//获取当前控件的布局对象
        params.width = width*4/5;
        params.height = params.width  / 2;//设置当前控件布局的高度
        mRootView.findViewById(R.id.logoutLayout).setLayoutParams(params);//将设置好的布局参数应用到控件中
        if (!TextUtils.isEmpty(title))
            ((TextView) mRootView.findViewById(R.id.title)).setText(title);
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
    }


    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
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
        if (i == R.id.cancel) {
        } else if (i == R.id.confir) {
            if (mCallback != null) {
                mCallback.onClick(v);
            }
        }
    }

}

