package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.ui.widget.ShowTargetDialog;
import com.yjfshop123.live.ui.widget.TargetProgressView;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.NumUtil;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetRewardActivity extends BaseActivityForNewUi {
    @BindView(R.id.npb)
    TargetProgressView npb;
    @BindView(R.id.recive_btn)
    TextView reciveBtn;

    @BindView(R.id.target_reward_ll)
    LinearLayout targetRewardLl;
    @BindView(R.id.reward_name)
    TextView rewardName;
    @BindView(R.id.reward_icon)
    ImageView rewardIcon;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.team_activity_num)
    TextView teamActivityNum;
    @BindView(R.id.chaju)
    TextView chaju;
    @BindView(R.id.need_activity)
    TextView needActivity;
    @BindView(R.id.view)
    LinearLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.jiangchi_target_REWARD));
        setContentView(R.layout.activity_target_reward);
        setTooBarBack(R.drawable.bg_my);
        setBlackColorTooBar();
        ButterKnife.bind(this);
        initSwipeRefresh();
        mEmptyLayout.setBackgroundResource(R.drawable.bg_my);
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setText(R.string.zhongjiangjilu);
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TargetRewardActivity.this, TargetRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        loadData();
    }

    @OnClick(R.id.recive_btn)
    public void onViewClicked() {


        if (data != null && targetProgress >= 100 && !isrecive) {
            ShowTargetDialog dialogFragment = new ShowTargetDialog();

            dialogFragment.setTitle(data.target_reward_des);

            dialogFragment.setLogo(data.target_reward_icon);
            dialogFragment.isRealReward(data.is_real_reward);
            dialogFragment.setOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data != null && data.is_real_reward) {
                        Intent intent = new Intent(TargetRewardActivity.this, TargetReciveDetailActivity.class);
                        startActivity(intent);
                        return;
                    }
                    receiveTargetReward();

                }
            });

            dialogFragment.show(getSupportFragmentManager(), "ShowTargetDialog");

        }
        if (data != null && targetProgress < 100) {
            NToast.shortToast(this, getString(R.string.haiweiwancheng));
        }
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

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }


    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    loadData();
                }
            });
        }
        finishRefresh();
    }

    public void showNoNetData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    loadData();
                }
            });
        }
        finishRefresh();
    }


    /**
     * "target_reward": "iphone",   //目标奖
     * "target_reward_icon": "pc/xxx",   //目标奖图片
     * "target_reward_count": 1,   //目标奖数量
     * "target_reward_value": 1,   //目标奖价值对应的金蛋或者银蛋数量
     * "target_reward_value_unit": "金蛋/银蛋",   //目标奖价值对应的单位（金蛋/银蛋）
     * "target_reward_progress": 45,   //目标奖进度
     * "target_reward_is_get": true,   //目标奖是否已领取
     * "target_reward_need_activity_num": 45,   //需要完成多少活跃度才能获得目标奖
     * *    "team_activity_num": 1,    //团队活跃度
     */
    String testData = "{\n" +
            "        \"target_reward_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "        \"target_reward\": \"iphone\",\n" +
            "        \"target_reward_count\": \"1\",\n" +
            "        \"target_reward_value\": \"11\",\n" +
            "        \"target_reward_value_unit\": \"金蛋\", \n" +
            "        \"target_reward_progress\": 45\n, " +
            "        \"target_reward_is_get\": true\n, " +
            "        \"is_real_reward\": true\n, " +
            "        \"target_reward_need_activity_num\": 45\n, " +
            "        \"team_activity_num\": 3\n " +
            "}";

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
                // initTargetRewardDatas(testData);
            }

            @Override
            public void onSuccess(String result) {

                initTargetRewardDatas(result);
            }
        });
    }

    float count;
    float targetProgress;
    boolean isrecive = true;
    String tartgetReward;
    TargetRewardResponse data;

    private void initTargetRewardDatas(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) {
            targetRewardLl.setVisibility(View.GONE);
            return;
        }

        targetRewardLl.setVisibility(View.VISIBLE);

        data = new Gson().fromJson(result, TargetRewardResponse.class);

        tartgetReward = data.target_reward;
        count = data.target_reward_count;
        targetProgress = data.target_reward_progress;
        isrecive = data.target_reward_is_get;
        npb.setProgress(targetProgress);
        rewardName.setText(data.target_reward);
        Glide.with(this).load(data.target_reward_icon).into(rewardIcon);
        teamActivityNum.setText(NumUtil.clearZero(data.team_activity_num));
        needActivity.setText(getString(R.string.target_bottom_pao, NumUtil.clearZero(data.target_reward_need_activity_num)));
        chaju.setText(getString(R.string.chaju, NumUtil.subtractNum(data.target_reward_need_activity_num, data.team_activity_num + "").contains("-") ? "0" : NumUtil.subtractNum(data.target_reward_need_activity_num, data.team_activity_num + "")));
        if (TextUtils.isEmpty(tartgetReward)) {
            targetRewardLl.setVisibility(View.VISIBLE);
        }
        reciveBtn.setEnabled(!isrecive);
        reciveBtn.setText(targetProgress >= 100 && isrecive ? getString(R.string.yilingqu) : getString(R.string.receive_reward));
        // rewardDes.setText(String.format(Locale.CHINA, "%s%d%s", targetProgress == 100 ? getString(R.string.target_complete) : getString(R.string.target_ing), count, tartgetReward));
        rewardName.setText(data.target_reward_value);

    }

    private void receiveTargetReward() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", data.id)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/prize/getPrize", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(TargetRewardActivity.this, getString(R.string.lingqu_fail));
                LoadDialog.dismiss(TargetRewardActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                loadData();
                LoadDialog.dismiss(TargetRewardActivity.this);


                    DialogUitl.showSimpleHintDialog(TargetRewardActivity.this, "", getString(R.string.confirm),
                            "",
                            getString(R.string.gongxi_lingqu_success) + data.target_reward_value,
                            true,
                            false,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {

                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                }
                            });

//                NToast.shortToast(TargetRewardActivity.this, getString(R.string.gongxi_lingqu_success) + data.target_reward_value_unit + data.target_reward_value);
//                loadData();
            }
        });
    }


}
