package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.TaskResponse;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskCenterActivity extends BaseActivityForNewUi {

    @BindView(R.id.day_task_img)
    ImageView dayTaskImg;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.banner)
    ImageView banner;
    @BindView(R.id.iscomplete)
    TextView iscomplete;
    @BindView(R.id.name)
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.task_center));
        setContentView(R.layout.activity_task_center);

        ButterKnife.bind(this);
        // initSwipeRefresh();
        initSwipeRefresh();
        loadData();
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
        OKHttpUtils.getInstance().getRequest("app/task/taskCenter", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {

                initData(result);
            }
        });
    }
    TaskResponse data;
    private void initData(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) {
            return;
        }

         data = new Gson().fromJson(result, TaskResponse.class);
        time.setText(data.time + "秒");
        name.setText(data.name);
        Glide.with(this).load(data.task_center_icon).into(banner);
        Glide.with(this).load(data.task_icon).into(dayTaskImg);

        if (data.is_complete) {
            iscomplete.setText(R.string.has_complete);
            iscomplete.setBackgroundResource(R.drawable.bg_gradient_fae2ae_b28d51_11);
        } else {
            iscomplete.setText(R.string.weiwancheng);
            iscomplete.setBackgroundResource(R.drawable.bg_333230_11);
        }
    }

    @OnClick(R.id.task_ll)
    public void onViewClicked() {
        if(data!=null&&!data.is_complete){
                setResult(10001);
                finish();
        }
    }
}
