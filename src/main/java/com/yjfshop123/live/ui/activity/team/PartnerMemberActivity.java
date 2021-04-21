package com.yjfshop123.live.ui.activity.team;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.model.PartnerMemberResponse;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.adapter.EggListAdapter;
import com.yjfshop123.live.ui.adapter.PartnerMemberAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的--我的团队--城市合伙人--合伙人会员明细
 */
public class PartnerMemberActivity extends BaseActivityForNewUi implements EmptyLayout.OnRetryListener {

    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.user_total)
    TextView userTotal;
    @BindView(R.id.total_activity_num)
    TextView totalActivityNum;


    private PartnerMemberAdapter searchAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    int page = 1;
    List<PartnerMemberResponse.PartnerMember> list = new ArrayList<>();
    public boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.member_list));
        setContentView(R.layout.activity_partner_member);
        ButterKnife.bind(this);
        initSwipeRefresh();
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        searchAdapter = new PartnerMemberAdapter(mContext);
        shimmerRecycler.setAdapter(searchAdapter);
        showNotData();
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        getMoreData();
                    }
                }
            }
        });
        getData(false);
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData(true);
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

    /**
     * 获取数据
     */
    public void getData(boolean isRefresh) {
page=1;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        if (!isRefresh) showLoading();
        OKHttpUtils.getInstance().getRequest("app/partner/members", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }

                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadData(testData);
                loadData(result);
            }
        });

    }

    /**
     * 获取数据
     */
    public void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/partner/members", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadMoreData(testData);
                loadMoreOver();
            }

            @Override
            public void onSuccess(String result) {
                loadMoreData(result);
                loadMoreOver();
                //模拟数据
                //  ((EggListByOrderTypeFragment) mContent).loadMoreData(testData);

            }
        });

    }

    public void loadMoreOver() {
        isLoadingMore = false;
    }

    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        PartnerMemberResponse data = new Gson().fromJson(result, PartnerMemberResponse.class);
        if (data.list == null) return;
        if (data.list.size() > 0) {
            page = 1;
        } else showNotData();
        this.list = data.list;
        searchAdapter.setCards(this.list);
        userTotal.setText(data.member_count+"");
        totalActivityNum.setText(data.total_activity_num+"");

    }

    public void loadMoreData(String result) {
        PartnerMemberResponse data = new Gson().fromJson(result, PartnerMemberResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }

        this.list.addAll(data.list);
        searchAdapter.notifyDataSetChanged();
//        userTotal.setText(data.member_count+"");
//        totalActivityNum.setText(data.total_activity_num+"");
    }

    @Override
    public void onRetry() {
        getData(false);
    }
}
