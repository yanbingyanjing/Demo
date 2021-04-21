package com.yjfshop123.live.ui.activity.yinegg;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.CtcListResopnse;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.model.LevelResponse;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.adapter.EggListAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class NewSilverListActivity extends BaseActivityH {
    String time = "";
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silver_egg_new_list);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            time = getIntent().getStringExtra("time");
            if(!TextUtils.isEmpty(time)&&time.length()>=10){
                time=time.substring(0,10);
            }
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.h_1_top_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.h_1_top_view).setLayoutParams(params);
        StatusBarUtil.StatusBarDarkMode(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        searchAdapter = new EggListAdapter(mContext);
        searchAdapter.setAccount(0);
        shimmerRecycler.setAdapter(searchAdapter);
        initSwipeRefresh();
        loadData();
    }

    private EggListAdapter searchAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @OnClick(R.id.btn_left)
    public void onViewClicked() {
        finish();
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
    }

    boolean isLoadingMore = false;

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

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("start_date", time)
                    .put("end_date", time)
                    .put("page", 1)
                    .put("accountant", 0)
                    .put("order_type", 88)
                    // .put("pagesize", "20")
                    .put("egg_type", 2)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/egg/tantDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                }
                //  initDatas(LevelResponse.testData);

            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
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
                    .put("start_date", time)
                    .put("end_date", time)
                    .put("accountant", 0)
                    .put("order_type", 88)
                    // .put("pagesize", "20")
                    .put("egg_type", 2)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/egg/tantDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                //模拟数据
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                loadMoreData(result);
                isLoadingMore = false;
                //模拟数据
                //loadMoreData(testData);

            }
        });

    }

    int page = 1;
    List<EggListDataResponse.EggListData> list = new ArrayList<>();

    public void loadMoreData(String result) {
        EggListDataResponse data = new Gson().fromJson(result, EggListDataResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        searchAdapter.notifyDataSetChanged();
    }


    private void initDatas(String result) {
        hideLoading();

        finishRefresh();
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (TextUtils.isEmpty(result)) return;
        EggListDataResponse data = new Gson().fromJson(result, EggListDataResponse.class);
        if (data.list == null) {
            showNotData();
            return;
        }
        if (data.list.size() > 0) {
            page = 1;
        } else {
            showNotData();
        }
        this.list = data.list;
        searchAdapter.setCards(this.list);
    }
}
