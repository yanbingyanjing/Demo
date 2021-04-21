package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CoinRecordListResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.JiaoYiRecodeAdapter;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 描述:交易记录
 **/
public class JiaoYiRecodeActivity extends BaseActivity {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.noDataLay)
    RelativeLayout noDataLay;
    private boolean isLoadingMore = false;

    private LinearLayoutManager mLinearLayoutManager;

    private JiaoYiRecodeAdapter jiaoYiRecodeAdapter;
    private ArrayList<CoinRecordListResponse.ListBean> lists = new ArrayList<>();
    private int page = 1;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiaoyirecode);
        ButterKnife.bind(this);
        setHeadLayout();

        type = getIntent().getStringExtra("TYPE");

        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        if (type.equals("TX")){
            tv_title_center.setText("提现记录");
        }else {
            tv_title_center.setText(R.string.jiaoyi_jilu);
        }


        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        jiaoYiRecodeAdapter = new JiaoYiRecodeAdapter(this, lists, type);
        shimmerRecycler.setAdapter(jiaoYiRecodeAdapter);
        jiaoYiRecodeAdapter.notifyDataSetChanged();
//        shimmerRecycler.showShimmerAdapter();
//
        initSwipeRefresh();
//        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
//                int totalItemCount = mLinearLayoutManager.getItemCount();
//            }
//        });
        getData();
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
                    page = 1;
                    getData();
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
                        getData();
                    }
                }
            }
        });
    }

    private void getData(){
        String url;
        if (type.equals("TX")){
            url = "app/withdraw/getWithdrawList";
        }else {
            url = "app/user/getRechargeList";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1){
                    mSwipeRefresh.setVisibility(View.GONE);
                    noDataLay.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                isLoadingMore = false;

                try {
                    CoinRecordListResponse recordListResponse = JsonMananger.jsonToBean(result, CoinRecordListResponse.class);
                    if (page == 1 && lists.size() > 0) {
                        lists.clear();
                    }
                    lists.addAll(recordListResponse.getList());
                    jiaoYiRecodeAdapter.notifyDataSetChanged();

                    if (lists.size() == 0) {
                        mSwipeRefresh.setVisibility(View.GONE);
                        noDataLay.setVisibility(View.VISIBLE);
                    } else {
                        mSwipeRefresh.setVisibility(View.VISIBLE);
                        noDataLay.setVisibility(View.GONE);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
