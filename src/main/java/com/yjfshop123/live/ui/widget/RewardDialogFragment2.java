package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.ui.activity.RewardActivity;
import com.yjfshop123.live.utils.CommonUtils;

public class RewardDialogFragment2 extends AbsDialogFragment implements View.OnClickListener {

    private RewardActivity rewardActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_reward_list2;
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
        params.height = CommonUtils.dip2px(mContext, 296);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mContext instanceof RewardActivity){
            rewardActivity = (RewardActivity) mContext;
        }

        mRootView.findViewById(R.id.dialog_reward_close).setOnClickListener(this);
        mRootView.findViewById(R.id.dialog_reward_list2_1).setOnClickListener(this);
        mRootView.findViewById(R.id.dialog_reward_list2_2).setOnClickListener(this);
        mRootView.findViewById(R.id.dialog_reward_list2_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_reward_close:
                break;
            case R.id.dialog_reward_list2_1:
                if (rewardActivity != null){
                    rewardActivity.showShareDialog();
                }
                break;
            case R.id.dialog_reward_list2_2:
                if (rewardActivity != null){
                    rewardActivity.saveCode();
                }
                break;
            case R.id.dialog_reward_list2_3:
                if (rewardActivity != null){
                    rewardActivity.copy();
                }
                break;
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

