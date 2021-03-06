package com.yjfshop123.live.shop.ziying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.AddressListAdapter;
import com.yjfshop123.live.shop.ziying.adapter.WuliAdapter;
import com.yjfshop123.live.shop.ziying.model.AddressList;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.WuliuResponse;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WuliuActivity extends BaseActivityH {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.refrash)
    SmartRefreshLayout refrash;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private WuliAdapter adapter;
String order_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziying_wuliu);
        ButterKnife.bind(this);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //?????????????????????????????????
        params.height = SystemUtils.getStatusBarHeight(this);//?????????????????????????????????
        statusBarView.setLayoutParams(params);

        order_id=getIntent().getStringExtra("order_id");
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new WuliAdapter(this);
        list.setAdapter(adapter);
        refrash.setEnableLoadMore(false);
        refrash.setNoMoreData(false);
        refrash.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refrash.setNoMoreData(false);
                getShopData();

            }
        });


    }
    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }

    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }

    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    getShopData();

                }
            });
        }

    }

    public void showNoNetData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    getShopData();

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        getShopData();
    }

    /**
     * ???????????????
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }


    public void getShopData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("order_id",order_id);
        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("???????????????", response);
                hideLoading();
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);//??????false??????????????????
              initData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000, false, true);//??????false??????????????????
                Log.d("???????????????", error);
                hideLoading();
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                }

            }
        }, HttpUtil.ziying_shop_wuliu_url, paraMap);
    }

    public List<WuliuResponse.WuliuData> data;


    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        WuliuResponse homeFenlei = new Gson().fromJson(result, WuliuResponse.class);
        if (homeFenlei == null) return;
        if (!TextUtils.isEmpty(homeFenlei.code) && homeFenlei.code.equals("error")) {
            adapter.setCards(null);
            showNotData();
            return;
        }
        if (homeFenlei.list == null || homeFenlei.list.size() == 0) {
            refrash.setNoMoreData(true);
            adapter.setCards(null);
            showNotData();
            return;
        }

        data = homeFenlei.list;
        adapter.setCards(data);
    }

    int pageSize = 20;

}
