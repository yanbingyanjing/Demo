package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.SMDLResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.adapter.MyVideoAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyVideoActivity extends BaseActivity implements View.OnClickListener,
        EmptyLayout.OnRetryListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    private MyVideoAdapter adapter;
    private GridLayoutManager mGridLayoutManager;
    private int width;
    private int page = 1;
    private boolean isLoadingMore = false;

    private int mDynamicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_backcall);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();

        page = 1;
        myDynamicList();
    }

    private void initView() {
        width = (screenWidth_ - CommonUtils.dip2px(mContext, 24)) / 2;

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText("我的视频");
        initSwipeRefresh();

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        adapter = new MyVideoAdapter(width);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);
        shimmerRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyVideoAdapter.MyItemClickListener() {
            @Override
            public void onItemDelete(View view, int position) {

                mDynamicID = mList.get(position).getDynamic_id();
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.other_ok), getString(R.string.other_cancel),
                        "删除视频？", true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(MyVideoActivity.this);
                                myDynamicDelete();
                            }
                        });


            }

            @Override
            public void onItemOpen(View view, int position) {
                ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mGridLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if(!isLoadingMore){
                        isLoadingMore = true;
                        page ++ ;
                        myDynamicList();
                    }
                }
            }
        });
    }

    private void myDynamicDelete(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", mDynamicID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/myDynamicDelete", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(MyVideoActivity.this);
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(MyVideoActivity.this);
                page = 1;
                myDynamicList();
            }
        });
    }

    private void myDynamicList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/myDynamicList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                if (result == null){
                    if (page == 1){
                        showNotData();
                    }
                    return;
                }

                try {
                    SMDLResponse response = JsonMananger.jsonToBean(result, SMDLResponse.class);
                    isLoadingMore = false;
                    if (page == 1){
                        if (mList.size() > 0){
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    adapter.setCards(mList);
                    adapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1){
                        showNotData();
                    }
                }
            }
        });
    }

    private List<SMDLResponse.ListBean> mList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
    }

    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNetError() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    @Override
    public void onRetry() {
        page = 1;
        myDynamicList();
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    myDynamicList();
                }
            });
        }
    }
}

