package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ActivityNumResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.ChoujiangFragment;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活跃度界面
 */
public class ActivityNumActivity extends BaseActivityForNewUi {
    @BindView(R.id.person_activity)
    TextView personActivity;
    @BindView(R.id.team_activity)
    TextView teamActivity;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.tips)
    TextView tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_activity);
        ButterKnife.bind(this);
        setCenterTitleText(getString(R.string.active_num));
        initSwipeRefresh();
        loadData();
        mSwipeRefresh.setRefreshing(true);
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

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {
                try {
                    UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                    String str = "";

                    if (!TextUtils.isEmpty(data.geren_activity)) {
                        str = str + data.geren_activity + "\n\n";

                    }
                    if (!TextUtils.isEmpty(data.tuandui_activity)) {
                        str = str + data.tuandui_activity +  "\n\n";
                    }

                    if (!TextUtils.isEmpty(data.yingxiong_activity)) {
                        str = str + data.yingxiong_activity ;

                    }
                    tips.setText(str);
                } catch (Exception e) {

                }

            }
        });
        OKHttpUtils.getInstance().getRequest("app/prize/getActivity", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }


    ActivityNumResponse activityNumResponse;

    private void initDatas(String result) {
        finishRefresh();
        if (!TextUtils.isEmpty(result)) {
            activityNumResponse = new Gson().fromJson(result, ActivityNumResponse.class);
            if (activityNumResponse != null) {
                personActivity.setText(NumUtil.dealNum(activityNumResponse.person_activity_num));
                teamActivity.setText(NumUtil.dealNum(activityNumResponse.team_activity_num));
            }
        }
    }

}
