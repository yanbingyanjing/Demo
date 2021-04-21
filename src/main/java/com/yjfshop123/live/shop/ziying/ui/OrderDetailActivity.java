package com.yjfshop123.live.shop.ziying.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.OrderDetailAdapter;
import com.yjfshop123.live.shop.ziying.adapter.SubmitBuyOrderAdapter;
import com.yjfshop123.live.shop.ziying.model.CreateOrderResponse;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.shop.ziying.model.OrderPayResponse;
import com.yjfshop123.live.shop.ziying.model.SubmitData;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.total_type;

public class OrderDetailActivity extends BaseActivityH {
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
    String cart_id;
    OrderDetailAdapter submitBuyOrderAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziying_order_detail);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);

        if (getIntent() != null) {
            cart_id = getIntent().getStringExtra("id");
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        submitBuyOrderAdapter = new OrderDetailAdapter(this);
        submitBuyOrderAdapter.setCards(submitDataList,"");
        list.setAdapter(submitBuyOrderAdapter);
        refrash.setEnableLoadMore(false);
        refrash.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getGoodsData();
            }
        });

        if (!TextUtils.isEmpty(cart_id)) {
            getGoodsData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getGoodsData() {
        LoadDialog.show(this);
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("id", cart_id);


        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                LoadDialog.dismiss(OrderDetailActivity.this);
                NLog.d("订单详情", response);
                // if (!form) dialog.dismiss();
                initData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                LoadDialog.dismiss(OrderDetailActivity.this);
                NToast.shortToast(OrderDetailActivity.this, error);
            }
        }, HttpUtil.ziying_shop_order_detail_url, paraMap);
    }


    OrderDetail homeFenlei;
    List<SubmitData> submitDataList = new ArrayList<>();

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;

        homeFenlei = new Gson().fromJson(result, OrderDetail.class);
        if (homeFenlei == null) return;
        if (homeFenlei.data == null) return;
        if(submitDataList!=null)submitDataList.clear();
        if (homeFenlei.data.logistics != null) {
            SubmitData submitData = new SubmitData();
            submitData.itemdata = homeFenlei.data.logistics;
            submitData.type = address_type;
            submitDataList.add(submitData);
        }
        String status="";
        if (homeFenlei.data.orderInfo != null) {
            SubmitData submitData = new SubmitData();
            submitData.itemdata = "商品信息";
            submitData.type = shop_type;
            submitDataList.add(submitData);
            status=homeFenlei.data.orderInfo.statusStr;
        }
        if (homeFenlei.data.goods != null) {
            for (int i = 0; i < homeFenlei.data.goods.length; i++) {
                SubmitData submitData2 = new SubmitData();
                submitData2.itemdata = homeFenlei.data.goods[i];
                submitData2.type = SubmitData.shop_item_type;
                submitDataList.add(submitData2);
            }
        }
        if (homeFenlei.data.orderInfo != null) {
            SubmitData submitData = new SubmitData();
            submitData.itemdata =homeFenlei.data.orderInfo;
            submitData.type = total_type;
            submitDataList.add(submitData);
        }
        if (homeFenlei.data.orderInfo != null) {
            SubmitData submitData = new SubmitData();
            submitData.itemdata = "订单历史";
            submitData.type = shop_type;
            submitDataList.add(submitData);
        }
        if (homeFenlei.data.history != null) {
            for (int i = 0; i < homeFenlei.data.history.length; i++) {
                SubmitData submitData2 = new SubmitData();
                submitData2.itemdata = homeFenlei.data.history[i];
                submitData2.type = SubmitData.history_type;
                submitDataList.add(submitData2);
            }
        }
        submitBuyOrderAdapter.setCards(submitDataList,status);
        if (submitBuyOrderAdapter != null) submitBuyOrderAdapter.notifyDataSetChanged();

    }


}
