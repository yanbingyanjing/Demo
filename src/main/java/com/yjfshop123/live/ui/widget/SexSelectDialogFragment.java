package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;

public class SexSelectDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private MainStartChooseCallback mCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sex_select;
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
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.findViewById(R.id.man).setOnClickListener(this);
        mRootView.findViewById(R.id.woman).setOnClickListener(this);
        mRootView.findViewById(R.id.cancel).setOnClickListener(this);
    }

    public void setMainStartChooseCallback(MainStartChooseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        if(!canClick()){
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.cancel) {
        } else if (i == R.id.man) {
            if (mCallback != null) {
                mCallback.onLiveClick();
            }
        } else if (i == R.id.woman) {
            if (mCallback != null) {
                mCallback.onVideoClick();
            }
        }
    }

}
