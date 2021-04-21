package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhutuiActivity extends BaseActivityForNewUi {
    @BindView(R.id.reward_name)
    TextView rewardName;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.help_reward));
        setContentView(R.layout.activity_zhutui);
        ButterKnife.bind(this);
        initSwipeRefresh();
        rewardName.setEnabled(false);
        loadData();
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setText(R.string.zhongjiangjilu);
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZhutuiActivity.this, TargetRecordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        }
    }

    public void showLoading() {

        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {

        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/user/helpReward", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
                finishRefresh();

            }

            @Override
            public void onSuccess(String result) {

                initTargetRewardDatas(result);
            }
        });
    }

    private void recieveData() {
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/user/RecievehelpReward", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
                LoadDialog.dismiss(ZhutuiActivity.this);
                NToast.shortToast(ZhutuiActivity.this, getString(R.string.lingqu_fail));
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(ZhutuiActivity.this);
                initTRecieveDatas(result);
            }
        });
    }

    private void initTargetRewardDatas(String result) {

        if (TextUtils.isEmpty(result)) {
            return;
        }
        data = new Gson().fromJson(result, ZhuTuiData.class);
        if (Integer.parseInt(data.progress) < 100) {
            rewardName.setText(data.progress + "%");
        } else {
            rewardName.setEnabled(true);
        }

    }

    private void initTRecieveDatas(String result) {

        if (TextUtils.isEmpty(result)) {
            return;
        }
        ZhuTuiRecieveData data = new Gson().fromJson(result, ZhuTuiRecieveData.class);
        NToast.shortToast(ZhutuiActivity.this, getString(R.string.gongxi_lingqu_zhutui_success, data.value_unit, data.value));

    }

    ZhuTuiData data;

    @OnClick(R.id.reward_name)
    public void onViewClicked() {
        recieveData();
    }

    public class ZhuTuiData {
        public String progress;
    }

    public class ZhuTuiRecieveData {
        /**
         * "value":"50",//领取的奖励数量
         * " ":"金蛋"//奖励的单位 金蛋/银蛋
         */
        public String value;
        public String value_unit;
    }
}
