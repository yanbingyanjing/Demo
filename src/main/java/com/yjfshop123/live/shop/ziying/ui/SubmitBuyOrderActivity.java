package com.yjfshop123.live.shop.ziying.ui;

import android.app.Activity;
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
import com.yjfshop123.live.shop.ziying.adapter.SubmitBuyOrderAdapter;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.CreateOrderResponse;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.shop.ziying.model.OrderPayResponse;
import com.yjfshop123.live.shop.ziying.model.SubmitData;
import com.yjfshop123.live.shop.ziying.view.IOSZiyingCityDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_type;

public class SubmitBuyOrderActivity extends BaseActivityH {
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
    String buy_type = "buyNow";
    SubmitBuyOrderAdapter submitBuyOrderAdapter;
    @BindView(R.id.zongji)
    TextView zongji;
    @BindView(R.id.submit_order)
    TextView submitOrder;
    private LinearLayoutManager mLinearLayoutManager;
    private String pintuan_id;
    private String kind;
    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confir_ziying_order);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);
        dialog = DialogUitl.loadingDialog(this, "下单中");
        if (getIntent() != null) {
            cart_id = getIntent().getStringExtra("cart_id");
            pintuan_id = getIntent().getStringExtra("pintuan_id");
            kind=getIntent().getStringExtra("kind");
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        submitBuyOrderAdapter = new SubmitBuyOrderAdapter(this);
        submitBuyOrderAdapter.setCards(submitDataList);
        submitBuyOrderAdapter.setKind(kind);
        submitBuyOrderAdapter.setRemarkListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null)
                    remark = s.toString();
            }
        });
        submitBuyOrderAdapter.setPhoneListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null)
                    chongzhi_phone = s.toString();
            }
        });
        submitBuyOrderAdapter.setOnItemClickListener(new SubmitBuyOrderAdapter.MyItemClickListener() {
            @Override
            public void onItemClickAddress(View view, int position) {
                Intent intent = new Intent(SubmitBuyOrderActivity.this, AddressListActivity.class);
                startActivity(intent);
            }
        });
        list.setAdapter(submitBuyOrderAdapter);
        refrash.setEnableLoadMore(false);
        refrash.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (submitDataList == null || submitDataList.size() == 0) {
                    getAddressData();
                    if (!TextUtils.isEmpty(cart_id)) {
                        getGoodsData();
                    }
                } else {
                    if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                }
            }
        });


        if (!TextUtils.isEmpty(cart_id)) {
            getGoodsData();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        getAddressData();


    }

    public void getGoodsData() {
        LoadDialog.show(this);
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("cart_id", cart_id);
        paraMap.put("buy_type", buy_type);

        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                NLog.d("购买第二步获取商品信息", response);
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                LoadDialog.dismiss(SubmitBuyOrderActivity.this);
                // if (!form) dialog.dismiss();
                initData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                LoadDialog.dismiss(SubmitBuyOrderActivity.this);
                NToast.shortToast(SubmitBuyOrderActivity.this, error);
            }
        }, HttpUtil.ziying_shop_buy_now_sencond_url, paraMap);
    }

    String remark = "";
    String chongzhi_phone = "";
    OrderGoodsResponse homeFenlei;
    List<SubmitData> submitDataList = new ArrayList<>();

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        homeFenlei = new Gson().fromJson(result, OrderGoodsResponse.class);
        if (homeFenlei == null) return;
        if (homeFenlei.goods_list == null) return;
        zongji.setText(homeFenlei.order_total_price);
        for (int i = 0; i < homeFenlei.goods_list.length; i++) {
            //店铺名字
            SubmitData submitData = new SubmitData();
            submitData.itemdata = homeFenlei.goods_list[i].shop_name;
            submitData.type = shop_type;
            submitDataList.add(submitData);
            if (homeFenlei.goods_list[i].goods != null && homeFenlei.goods_list[i].goods.goods_list != null) {
                //商品
                for (int j = 0; j < homeFenlei.goods_list[i].goods.goods_list.length; j++) {
                    SubmitData submitData2 = new SubmitData();
                    submitData2.itemdata = homeFenlei.goods_list[i].goods.goods_list[j];
                    submitData2.type = SubmitData.shop_item_type;
                    submitDataList.add(submitData2);
                }
            }
            //配送
            SubmitData submitData4 = new SubmitData();
            submitData4.itemdata = homeFenlei.goods_list[i];
            submitData4.type = SubmitData.remark_type;
            submitDataList.add(submitData4);
            //总计
            SubmitData submitData5 = new SubmitData();
            submitData5.itemdata = homeFenlei.goods_list[i];
            submitData5.type = SubmitData.total_type;
            submitDataList.add(submitData5);
        }
        submitBuyOrderAdapter.setCards(submitDataList);
        if (submitBuyOrderAdapter != null) submitBuyOrderAdapter.notifyDataSetChanged();

    }

    public void getAddressData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                NLog.d("购买第三步获取默认地址", response);
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                initAddressData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);
                if (submitDataList != null && submitDataList.size() > 0 && submitDataList.get(0).type != address_type) {
                    SubmitData submitData = new SubmitData();
                    submitData.itemdata = null;
                    submitData.type = address_type;
                    submitDataList.add(0, submitData);
                    submitBuyOrderAdapter.setCards(submitDataList);
                }
                if (submitDataList != null && submitDataList.size() == 0) {
                    SubmitData submitData = new SubmitData();
                    submitData.itemdata = null;
                    submitData.type = address_type;
                    submitDataList.add(0, submitData);
                    submitBuyOrderAdapter.setCards(submitDataList);
                }
                NToast.shortToast(SubmitBuyOrderActivity.this, "地址获取失败");
            }
        }, HttpUtil.ziying_shop_buy_now_thrd_url, paraMap);
    }

    DefaultAddress.AddressData currentAddress;

    private void initAddressData(String result) {
        if (TextUtils.isEmpty(result)) return;
        SubmitData submitData = new SubmitData();
        submitData.type = address_type;
        DefaultAddress defaultAddress = new Gson().fromJson(result, DefaultAddress.class);
        if (submitDataList != null && submitDataList.size() > 0 && submitDataList.get(0).type == address_type) {
            submitDataList.remove(0);
            submitBuyOrderAdapter.setCards(submitDataList);
        }
        if (defaultAddress != null && !TextUtils.isEmpty(defaultAddress.code) && defaultAddress.code.equals("error")) {
            submitDataList.add(0, submitData);
            currentAddress = null;
            submitBuyOrderAdapter.setCards(submitDataList);
            return;
        }

        if (defaultAddress.data != null) {
            currentAddress = defaultAddress.data;
            submitData.itemdata = defaultAddress.data;
            submitDataList.add(0, submitData);
            submitBuyOrderAdapter.setCards(submitDataList);
        }
    }

    @OnClick(R.id.submit_order)
    public void onViewClicked() {
        if (currentAddress == null) {
            NToast.shortToast(this, getString(R.string.please_select_address));
            return;
        }
        if(!TextUtils.isEmpty(kind)&&kind.equals("huafei")){
            if(TextUtils.isEmpty(chongzhi_phone)){
                NToast.shortToast(this, "请输入要充值的手机号");
                return;
            }

        }
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("buy_type", buy_type);
        paraMap.put("cart_id", cart_id);
        paraMap.put("remark", remark);
        paraMap.put("client", "app");
        if(!TextUtils.isEmpty(kind)&&kind.equals("huafei")){
            paraMap.put("mobile",chongzhi_phone);
        }
        if (!TextUtils.isEmpty(pintuan_id))
            paraMap.put("pintuan_id", pintuan_id);

        dialog.show();
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                NLog.d("购买第四步创建订单", response);
                if (!response.startsWith("{")) {
                    dialog.dismiss();

                    NToast.shortToast(mContext, getString(R.string.xiadan_fail));
                    return;
                }
                CreateOrderResponse addCartResponse = new Gson().fromJson(response, CreateOrderResponse.class);
                if (TextUtils.isEmpty(addCartResponse.code) || addCartResponse.code.equals("error")) {
                    dialog.dismiss();
                    NToast.shortToast(mContext, getString(R.string.xiadan_fail)+":"+addCartResponse.msg);
                    return;
                }

                // NToast.shortToast(mContext, getString(R.string.xiadan_success));
                toPay(addCartResponse);

            }

            @Override
            public void onFailure(int what, String error) {
                dialog.dismiss();
                NToast.shortToast(SubmitBuyOrderActivity.this, error);
            }
        }, HttpUtil.ziying_shop_order_create_url, paraMap);
    }

    private void toPay(final CreateOrderResponse addCartResponse) {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("buy_type", addCartResponse.buy_type);
        paraMap.put("order_no", addCartResponse.order_no);
        if (!TextUtils.isEmpty(pintuan_id))
            paraMap.put("pintuan_id", pintuan_id);

        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                dialog.dismiss();
                NLog.d("购买第五步付款", response);
                OrderPayResponse orderPayResponse = new Gson().fromJson(response, OrderPayResponse.class);
                if (orderPayResponse != null && !TextUtils.isEmpty(orderPayResponse.code) && orderPayResponse.code.equals("success")) {
                    NToast.shortToast(SubmitBuyOrderActivity.this, getString(R.string.xiadan_success));
                } else if (orderPayResponse != null && !TextUtils.isEmpty(orderPayResponse.code) && orderPayResponse.code.equals("error")) {
                    NToast.shortToast(SubmitBuyOrderActivity.this, orderPayResponse.msg);
                } else {
                    NToast.shortToast(SubmitBuyOrderActivity.this, getString(R.string.xiadan_fail));
                }
                Intent intent = new Intent(SubmitBuyOrderActivity.this, OrderDetailActivity.class);
                intent.putExtra("id", addCartResponse.order_id);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(int what, String error) {
                dialog.dismiss();
                NToast.shortToast(SubmitBuyOrderActivity.this, error);
            }
        }, HttpUtil.ziying_shop_order_pay_url, paraMap);
    }


}
