package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.InComeResponseResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.InComDetailAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSDateAlertDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 描述: 收益明细
 **/
public class InComeDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.viewDate)
    RelativeLayout viewDate;
    @BindView(R.id.viewEmpty)
    RelativeLayout viewEmpty;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.tvYear)
    TextView tvYear;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.tvIncome)
    TextView tvIncome;

    private String dateStr = "";
    private int page = 1;
    private boolean isLoadingMore = false;

    private InComDetailAdapter adapter;

    private List<InComeResponseResponse.ListBean> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);
        setHeadLayout();
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.income_detail);

        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(sdf.format(new Date()));
            c.setTime(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dateStr = c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH) + 1) > 9 ? (c.get(Calendar.MONTH) + 1) : "0" + (c.get(Calendar.MONTH) + 1)) + "-" + "01";
        tvYear.setText(c.get(Calendar.YEAR) + "");
        tvMonth.setText(((c.get(Calendar.MONTH) + 1) > 9 ? (c.get(Calendar.MONTH) + 1) : "0" + (c.get(Calendar.MONTH) + 1)) + "");
        initEvent();
        initData();
    }

    private void initEvent() {
        viewDate.setOnClickListener(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        adapter = new InComDetailAdapter(this, lists);
        shimmerRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipeRefresh();
    }

    private void initData() {
        getIncomeList();
    }

    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page=1;
                    getIncomeList();
                }
            });
        }

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
                        getIncomeList();
                    }
                }
            }
        });
    }

    private void getIncomeList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("date", dateStr)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/getIncomeList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1){
                    viewEmpty.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();

                isLoadingMore = false;
                if (page == 1 && lists.size() > 0) {
                    lists.clear();
                }

                try {
                    InComeResponseResponse responseResponse = JsonMananger.jsonToBean(result, InComeResponseResponse.class);
                    if(page==1) {
                        tvIncome.setText(responseResponse.getTotal() + "");
                    }

                    if (responseResponse.getList().size() > 0) {
                        lists.addAll(responseResponse.getList());
                    }

                    if (lists.size() > 0) {
                        viewEmpty.setVisibility(View.GONE);
                    } else {
                        viewEmpty.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.viewDate:
                final IOSDateAlertDialog dialog = new IOSDateAlertDialog(this).builder();
                dialog.setTitle("选择日期");
                dialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                dialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] dates = dialog.getDateStr().split("-");
                        if (dates.length > 1) {
                            dateStr = dialog.getDateStr();
                            tvYear.setText(dates[0]);
                            tvMonth.setText(dates[1]);
                        } else {
                            dateStr = dialog.getDateStr() + "-01" + "-01";
                            tvYear.setText(dates[0]);
                            tvMonth.setText("01");
                        }
                        page = 1;
                        getIncomeList();
                    }
                });
                dialog.show();
                break;
        }
    }
}
