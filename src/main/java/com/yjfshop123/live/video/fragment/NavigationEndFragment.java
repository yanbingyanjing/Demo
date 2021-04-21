package com.yjfshop123.live.video.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.CallSettingActivity;
import com.yjfshop123.live.ui.activity.MyWalletActivity1;
import com.yjfshop123.live.ui.activity.OnlineServiceActivity;
import com.yjfshop123.live.ui.activity.RewardActivity;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.video.activity.SmallVideoActivity;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;

import butterknife.BindView;

public class NavigationEndFragment extends BaseFragment implements View.OnClickListener {

    private SmallVideoActivity smallVideoActivity;

    @BindView(R.id.myWallet)
    FrameLayout myWallet;
    @BindView(R.id.shiMingLayout)
    FrameLayout shiMingLayout;
    @BindView(R.id.beautyLayout)
    FrameLayout beautyLayout;
//    @BindView(R.id.tieziLayout)
//    FrameLayout tieziLayout;
    @BindView(R.id.shareLayout)
    FrameLayout shareLayout;
    @BindView(R.id.serviceLayout)
    FrameLayout serviceLayout;
    @BindView(R.id.settingLayout)
    FrameLayout settingLayout;
    @BindView(R.id.logoutLayout)
    FrameLayout logoutLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof SmallVideoActivity) {
            smallVideoActivity = (SmallVideoActivity) getActivity();
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_navigation_end;
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initEvent() {
        myWallet.setOnClickListener(this);
        shiMingLayout.setOnClickListener(this);
        beautyLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
//        tieziLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.settingLayout:
                intent.setClass(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.myWallet:
                intent.setClass(getActivity(), MyWalletActivity1.class);
                getActivity().startActivity(intent);
                break;
            case R.id.shiMingLayout:
                intent.setClass(getActivity(), ShiMingRenZhengActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.shareLayout:
                intent.setClass(getActivity(), RewardActivity.class);
                getActivity().startActivity(intent);
                break;
//            case R.id.tieziLayout:
//                intent.setClass(getActivity(), PostListActivity.class);
//                getActivity().startActivity(intent);
//                break;
            case R.id.beautyLayout:
                intent.setClass(getActivity(), CallSettingActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.serviceLayout:
                intent.setClass(getActivity(), OnlineServiceActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.logoutLayout:
                DialogUitl.showSimpleHintDialog(getActivity(),
                        getString(R.string.prompt),
                        getString(R.string.quit),
                        getString(R.string.other_cancel),
                        getString(R.string.setting_logout),
                        true,
                        true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(getActivity());

                                TIMManager.getInstance().logout(new TIMCallBack() {
                                    @Override
                                    public void onError(int code, String desc) {
                                        LoadDialog.dismiss(getActivity());
                                        if (smallVideoActivity != null){
                                            smallVideoActivity.logout();
                                        }
                                    }
                                    @Override
                                    public void onSuccess() {
                                        LoadDialog.dismiss(getActivity());
                                        if (smallVideoActivity != null){
                                            smallVideoActivity.logout();
                                        }
                                    }
                                });
                            }
                        });
                break;
        }

        if (smallVideoActivity != null){
            smallVideoActivity.closeDrawer();
        }
    }
}
