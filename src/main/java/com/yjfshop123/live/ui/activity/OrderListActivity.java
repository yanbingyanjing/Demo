package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.ui.adapter.EggListAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSDateTotalDialog;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.DateFormatUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.model.EggListDataResponse.testData;
import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_TRADE;

/**
 * 我的----交易明细
 */
public class OrderListActivity extends BaseActivityForNewUi implements EmptyLayout.OnRetryListener {
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.start_time)
    LinearLayout startTime;
    @BindView(R.id.end_time_tv)
    TextView endTimeTv;
    @BindView(R.id.end_time)
    LinearLayout endTime;

    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    IOSDateTotalDialog startdialog;
    IOSDateTotalDialog enddialog;
    String startTimeRequest = "";
    String endTimeRequest = DateFormatUtil.getCurTimeYMDString();
    @BindView(R.id.days)
    TextView days;

    private EggListAdapter searchAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    int page = 1;
    List<EggListDataResponse.EggListData> list = new ArrayList<>();
    public boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.trade_list));
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        initSwipeRefresh();
        showNotData();
        days.setText(getString(R.string.select_days, "-"));

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
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        searchAdapter = new EggListAdapter(mContext);

        shimmerRecycler.setAdapter(searchAdapter);
        getData(false);
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page=1;
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

        if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
            if (mSwipeRefresh != null) {
                SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
            }
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

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("start_date", startTimeRequest)
                    .put("end_date", endTimeRequest)
                    .put("page", 1)
                    .put("accountant", 0)
                    .put("order_type", 0)
                    .put("egg_type", EGG_TRADE)
                    .build();
        } catch (JSONException e) {
        }

        if (!isRefresh) showLoading();
        OKHttpUtils.getInstance().getRequest("app/egg/tantDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }else hideLoading();
                //模拟数据
                //loadData(testData);
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
                    .put("start_date", startTimeRequest)
                    .put("end_date", endTimeRequest)
                    .put("page", page)
                    .put("accountant", 0)
                    .put("order_type", 0)
                    .put("egg_type", EGG_TRADE)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/egg/tantDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadMoreData(testData);
                page--;
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
        EggListDataResponse data = new Gson().fromJson(result, EggListDataResponse.class);
        if (data.list == null) return;
        if (data.list.size() > 0) {
            page = 1;
        } else showNotData();
        this.list = data.list;
        searchAdapter.setCards(this.list);

    }

    public void loadMoreData(String result) {
        EggListDataResponse data = new Gson().fromJson(result, EggListDataResponse.class);
        if (data.list == null|| data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        searchAdapter.notifyDataSetChanged();
    }

    boolean isSetStart = false;
    boolean isSetEnd = false;

    @OnClick({R.id.start_time, R.id.end_time, R.id.confir})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_time:
                if (startdialog == null) {
                    startdialog = new IOSDateTotalDialog(Objects.requireNonNull(this)).builder();
                    startdialog.setTitle(getString(R.string.select_date));
                    startdialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    startdialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isSetStart = true;
                            startTimeTv.setText(startdialog.getDateStr());
                            if (startdialog != null && !TextUtils.isEmpty(startdialog.getDateStr()) && enddialog != null && !TextUtils.isEmpty(enddialog.getDateStr()))
                                days.setText(getString(R.string.select_days, (DateFormatUtil.dateDiff(startdialog.getDateStr(), enddialog.getDateStr()) + 1) + ""));


                        }
                    });
                }
                startdialog.show();
                break;
            case R.id.end_time:
                if (enddialog == null) {
                    enddialog = new IOSDateTotalDialog(Objects.requireNonNull(this)).builder();
                    enddialog.setTitle(getString(R.string.select_date));
                    enddialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    enddialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isSetEnd = true;
                            endTimeTv.setText(enddialog.getDateStr());
                            if (startdialog != null && !TextUtils.isEmpty(startdialog.getDateStr()) && enddialog != null && !TextUtils.isEmpty(enddialog.getDateStr()))
                                days.setText(getString(R.string.select_days, (DateFormatUtil.dateDiff(startdialog.getDateStr(), enddialog.getDateStr()) + 1) + ""));

                        }
                    });
                }
                enddialog.show();
                break;
            case R.id.confir:
                if (isSetEnd) endTimeRequest = enddialog.getDateStr();
                if (isSetStart) startTimeRequest = startdialog.getDateStr();
                long startLong = DateFormatUtil.dataOne(startTimeRequest);
                long endLong = DateFormatUtil.dataOne(endTimeRequest);
                if (startLong > endLong) {
                    //如果开始时间大于结束时间，则第一个开始时间的日期设置为请求参数end_date的值
                    backup = startTimeRequest;
                    startTimeRequest = endTimeRequest;
                    endTimeRequest = backup;
                }
                getData(false);
                break;
        }
    }

    String backup;

    @Override
    public void onRetry() {
        getData(false);
    }
}
