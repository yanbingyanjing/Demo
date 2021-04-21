package com.yjfshop123.live.shop.pintuan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.shop.adapter.PintuanListAdapter;
import com.yjfshop123.live.shop.model.PintuanResponse;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailXuanPinActivity;
import com.yjfshop123.live.shop.ziying.ui.NewShopListActivity;
import com.yjfshop123.live.shop.ziying.ui.OrderListActivity;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.xuanpin.adapter.XuanPinAdapter;
import com.yjfshop123.live.xuanpin.ui.XuanPinActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PintuanListActivity extends BaseActivityForNewUi {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.m_swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    boolean isLoadingMore = false;
    private LinearLayoutManager mLinearLayoutManager;
    private PintuanListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xuanpin);
        ButterKnife.bind(this);
        setBlackColorTooBar();
        setCenterTitleText("拼团");
        setTooBarBack(R.drawable.bg_gradient_faf7ed_f4ecd7);
        StatusBarUtil.StatusBarLightMode(this);
        initSwipeRefresh();
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setText(getString(R.string.dingdan_manager));
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PintuanListActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new PintuanListAdapter(this);

        shimmerRecycler.setAdapter(adapter);
        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadData(true);
                    }
                }
            }
        });
        showLoading();
        loadData(false);
    }

    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    int page = 1;

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadData(true);
                }
            });
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
                    loadData(false);
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
                    loadData(false);
                }
            });
        }
        finishRefresh();
    }

    PintuanResponse data;
    List<PintuanResponse.PintuanData> listData = new ArrayList<>();

    private void loadData(boolean isFromRefresh) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        if (!isFromRefresh) {
            showLoading();
        }
        OKHttpUtils.getInstance().getRequest("app/pintuan/pintuanList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();

                isLoadingMore = false;
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                } else hideLoading();
                if (page > 1) {
                    page--;
                }
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                data = new Gson().fromJson(result, PintuanResponse.class);
                isLoadingMore = false;
                if (page == 1 && data != null && data.list.size() > 0) {
                    listData.clear();
                } else {
                    if (data.list == null || data.list.size() == 0) {
                        if (page == 1) {
                            showNotData();
                        }
                        if (page > 1)
                            page--;
                        return;
                    }

                }
                listData.addAll(data.list);
                adapter.setCards(listData);
                adapter.notifyDataSetChanged();

            }
        });
    }

}
