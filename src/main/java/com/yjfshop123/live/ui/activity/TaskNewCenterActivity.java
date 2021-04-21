package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.ui.adapter.TaskNewAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskNewCenterActivity extends BaseActivityH {


    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    private LinearLayoutManager mLinearLayoutManager;
    private TaskNewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setCenterTitleText(getString(R.string.task_center));
        setContentView(R.layout.activity_task_center);
        mContext=this;
        //setHeadRightTextVisibility(View.VISIBLE);
//        mHeadRightText.setText("宣传素材");
//        mHeadRightText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TaskNewCenterActivity.this, SucaiActivity.class);
//                startActivity(intent);
//            }
//        });
        ButterKnife.bind(this);
        StatusBarUtil.StatusBarDarkMode(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);
        // initSwipeRefresh();
        initSwipeRefresh();
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new TaskNewAdapter(mContext);
        shimmerRecycler.setAdapter(adapter);
        showLoading();
        //loadData();
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {

        OKHttpUtils.getInstance().getRequest("app/task/newTaskCenter", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                } else hideLoading();

            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                initData(result);
            }
        });
    }

    TaskNewResponse data;
    private List<TaskNewResponse.TaskCenterData> mList = new ArrayList<>();

    private void initData(String result) {

        if (TextUtils.isEmpty(result)) {
            return;
        }

        data = new Gson().fromJson(result, TaskNewResponse.class);
        mList.clear();
        TaskNewResponse.TaskCenterData taskCenterData = new TaskNewResponse.TaskCenterData();
        taskCenterData.type = 1;
        taskCenterData.data = data.task_center_icon;
        mList.add(taskCenterData);
        if (data.tasks != null) {
            for (int i = 0; i < data.tasks.length; i++) {
                TaskNewResponse.TaskCenterData taskCenterData1 = new TaskNewResponse.TaskCenterData();
                taskCenterData1.type = 2;
                taskCenterData1.data = data.tasks[i].title;
                mList.add(taskCenterData1);
                if (data.tasks[i].list != null && data.tasks[i].list.length > 0) {
                    for (int j = 0; j < data.tasks[i].list.length; j++) {
                        TaskNewResponse.TaskCenterData taskCenterData2 = new TaskNewResponse.TaskCenterData();
                        taskCenterData2.type = 3;
                        taskCenterData2.data = data.tasks[i].list[j];
                        mList.add(taskCenterData2);
                    }
                } else {
                    if (i == 0) {
                        TaskNewResponse.TaskCenterData taskCenterData5 = new TaskNewResponse.TaskCenterData();
                        taskCenterData5.type = 4;
                        taskCenterData5.data = "购买选品次日生效后，开启任务";
                        mList.add(taskCenterData5);
                    }
                }
            }
        } else {
            TaskNewResponse.TaskCenterData taskCenterData3 = new TaskNewResponse.TaskCenterData();
            taskCenterData3.type = 2;
            taskCenterData3.data = "每日任务";
            mList.add(taskCenterData3);
            TaskNewResponse.TaskCenterData taskCenterData5 = new TaskNewResponse.TaskCenterData();
            taskCenterData5.type = 4;
            taskCenterData5.data = "购买选品次日生效后，开启任务";
            mList.add(taskCenterData5);
            TaskNewResponse.TaskCenterData taskCenterData4 = new TaskNewResponse.TaskCenterData();
            taskCenterData4.type = 2;
            taskCenterData4.data = "其他任务";
            mList.add(taskCenterData4);
        }
        adapter.setCards(mList);
    }

//    @OnClick(R.id.task_ll)
//    public void onViewClicked() {
//        if(data!=null&&!data.is_complete){
//            setResult(10001);
//            finish();
//        }
//    }
}
