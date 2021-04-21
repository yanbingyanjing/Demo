package com.yjfshop123.live.ui.activity.yinegg;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.GoldPriceResponse;
import com.yjfshop123.live.model.NewSilverEggResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.adapter.GoldPriceAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GoldPriceActivity extends BaseActivityH {

    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.text_right)
    TextView textRight;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.two)
    LinearLayout two;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private GoldPriceAdapter searchAdapter;

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_gold_price);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);

        ButterKnife.bind(this);
        StatusBarUtil.StatusBarDarkMode(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(mLinearLayoutManager);
        searchAdapter = new GoldPriceAdapter(mContext);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    mSwipeRefresh.setEnabled(true);
                } else {
                    mSwipeRefresh.setEnabled(false);
                }
            }
        });
        list.setAdapter(searchAdapter);

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        initSwipeRefresh();
        loadOrderData();

    }


    NewSilverEggResponse newSilverEggResponse;


    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadOrderData();
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

                    loadOrderData();
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
                    loadOrderData();
                }
            });
        }
        finishRefresh();
    }

    private void loadOrderData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/egg/price", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, getString(R.string.get_data_fail));
                // loadData(UnlockSilverEggResponse.testData);
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                } else {
                    hideLoading();
                }
            }

            @Override
            public void onSuccess(String result) {
                loadData(result);
            }
        });
    }

    private void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/egg/price", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                loadMoreData(result);
            }
        });
    }

    List<GoldPriceResponse.UpEntity> orderData;

    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        GoldPriceResponse data = new Gson().fromJson(result, GoldPriceResponse.class);
        price.setText(data.price);
        if (data.list == null || data.list.size() == 0) {
            showNotData();
            return;
        }

        this.orderData = data.list;
        searchAdapter.setCards(this.orderData);

    }

    public void loadMoreData(String result) {
        GoldPriceResponse data = new Gson().fromJson(result, GoldPriceResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }

        this.orderData.addAll(data.list);
        searchAdapter.notifyDataSetChanged();
    }

    int page = 1;
    public boolean isLoadingMore = false;

    @OnClick(R.id.btn_left)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
        }
    }
}
