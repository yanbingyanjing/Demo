package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;

public class ApplySuccessDialogFragment  extends AbsDialogFragment implements View.OnClickListener {

    private MainStartChooseCallback mCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_apply_success;
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
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        if(!TextUtils.isEmpty(content))
        ((TextView) mRootView.findViewById(R.id.content)).setText(content);
    }
    String content;
    public void setContent(String content) {
        this.content=content;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
       if(getActivity()!=null) getActivity().finish();
        dismiss();
    }

}
