package com.yjfshop123.live.shop.ziying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.AddressListAdapter;
import com.yjfshop123.live.shop.ziying.adapter.NewShopAdapter;
import com.yjfshop123.live.shop.ziying.model.AddressList;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressListActivity extends BaseActivityH {
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
    @BindView(R.id.add_address)
    TextView addAddress;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private AddressListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziying_address_list);
        ButterKnife.bind(this);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);


        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new AddressListAdapter(this);
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
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }


    public void getShopData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                hideLoading();
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                initData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000, false, true);//传入false表示刷新失败
                Log.d("获取的数据", error);
                hideLoading();
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                }

            }
        }, HttpUtil.ziying_shop_address_url, paraMap);
    }

    public DefaultAddress.AddressData[] data;


    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        AddressList homeFenlei = new Gson().fromJson(result, AddressList.class);
        if (homeFenlei == null) return;
        if (!TextUtils.isEmpty(homeFenlei.code) && homeFenlei.code.equals("error")) {
            adapter.setCards(null);
            showNotData();
            return;
        }
        if (homeFenlei.list == null || homeFenlei.list.length == 0) {
            refrash.setNoMoreData(true);
            adapter.setCards(null);
            showNotData();
            return;
        }

        data = homeFenlei.list;
        adapter.setCards(data);
    }

    int pageSize = 20;

    @OnClick(R.id.add_address)
    public void onViewClicked() {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("is_has_default", false);
        startActivity(intent);
    }
}
