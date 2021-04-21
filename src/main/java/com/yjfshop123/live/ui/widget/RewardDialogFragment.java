package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.utils.CommonUtils;

public class RewardDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private String hintContent;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_reward_list;
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
        params.width = CommonUtils.dip2px(mContext, 280);
        params.height = CommonUtils.dip2px(mContext, 252);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        hintContent = bundle.getString("hintContent");

        mRootView.findViewById(R.id.dialog_reward_close).setOnClickListener(this);
        TextView dialog_reward_gz = mRootView.findViewById(R.id.dialog_reward_gz);
        dialog_reward_gz.setText(hintContent.replace("\\n", "\n"));
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getId() == R.id.dialog_reward_close){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
