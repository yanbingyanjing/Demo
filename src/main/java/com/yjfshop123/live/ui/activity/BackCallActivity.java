package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.RebroadcastListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.adapter.BackCallAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import android.support.v7.widget.RecyclerView;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackCallActivity extends BaseActivity implements View.OnClickListener,
        EmptyLayout.OnRetryListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    private BackCallAdapter backCallAdapter;
    private GridLayoutManager mGridLayoutManager;
    private int width;
    private int page = 1;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backcall);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();

        page = 1;
        getMyVideoList();
    }

    private void initView() {
        width = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(mContext, 24)) / 2;

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText("我的录播");
        initSwipeRefresh();

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        backCallAdapter = new BackCallAdapter(width);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);

        shimmerRecycler.setAdapter(backCallAdapter);

        backCallAdapter.setOnItemClickListener(new BackCallAdapter.MyItemClickListener() {
            @Override
            public void onItemCancel(View view, int position) {
                videoId = String.valueOf(mList.get(position).getVideo_id());
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.other_ok), getString(R.string.other_cancel),
                        "取消展示？", true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(BackCallActivity.this);
                                cancelMyVideo();
                            }
                        });
            }

            @Override
            public void onItemDelete(View view, int position) {
                videoId = String.valueOf(mList.get(position).getVideo_id());
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.other_ok), getString(R.string.other_cancel),
                        "删除展示？", true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(BackCallActivity.this);
                                delRebroadcastLubo();
                            }
                        });
            }

            @Override
            public void onItemBackCall(View view, int position) {
                videoId = String.valueOf(mList.get(position).getVideo_id());
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.other_ok), getString(R.string.other_cancel),
                        "设置展示？", true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(BackCallActivity.this);
                                setMyVideo();
                            }
                        });
            }
        });

        /*shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        request(GETREBROADCASTLIST, true);
                    }
                }
            }
        });*/
    }

    /**
     * 删除回播
     */
    private void delRebroadcastLubo(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("video_id", videoId)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/delMyVideo", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BackCallActivity.this);
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BackCallActivity.this);
                getMyVideoList();
            }
        });
    }

    /**
     * 结束回播
     */
    private void cancelMyVideo(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("video_id", videoId)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/CancelMyVideo", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BackCallActivity.this);
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BackCallActivity.this);
                getMyVideoList();
            }
        });
    }

    /**
     * 设置回调
     */
    private void setMyVideo(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("video_id", videoId)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/setMyVideo", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BackCallActivity.this);
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BackCallActivity.this);
                getMyVideoList();
            }
        });
    }

    private String videoId;

    /**
     * 获取回播列表
     */
    private void getMyVideoList(){
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/getMyVideoList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    RebroadcastListResponse response = JsonMananger.jsonToBean(result, RebroadcastListResponse.class);
                    isLoadingMore = false;
                    if (page == 1){
                        if (mList.size() > 0){
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    backCallAdapter.setCards(mList);

                    backCallAdapter.notifyDataSetChanged();

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

    private List<RebroadcastListResponse.ListBean> mList = new ArrayList<>();

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
        getMyVideoList();
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
                    getMyVideoList();
                }
            });
        }
    }
}
