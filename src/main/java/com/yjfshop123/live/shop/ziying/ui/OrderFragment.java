package com.yjfshop123.live.shop.ziying.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.adapter.ShopAdapter;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.NewShopAdapter;
import com.yjfshop123.live.shop.ziying.adapter.OrderLIstAdapter;
import com.yjfshop123.live.shop.ziying.model.BaseResponse;
import com.yjfshop123.live.shop.ziying.model.OrderListResponse;
import com.yjfshop123.live.shop.ziying.model.OrderPayResponse;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

public class OrderFragment extends BaseFragment implements OrderLIstAdapter.OrderDeal {
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.refrash)
    SmartRefreshLayout refrash;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private OrderLIstAdapter adapter;

    String order_status = "all";

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_ziying_order_list;
    }

    public void setStatus(String order_status) {
        this.order_status = order_status;
    }

    @Override
    protected void initAction() {
        NLog.d("shopsubfragment", "  initAction");
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new OrderLIstAdapter(getContext());
        adapter.setOrderDeal(this);
        adapter.setCards(data);
        list.setAdapter(adapter);
        dialog = DialogUitl.loadingDialog(getContext(), "处理中");

        refrash.setNoMoreData(false);
        refrash.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refrash.setNoMoreData(false);
                page = 0;
                getOrderData();

            }
        });
        refrash.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getMoreData();

            }
        });
        if (data == null || data.size() == 0) {
            showLoading();
            getOrderData();
        } else {
            hideLoading();
            if (adapter != null) adapter.setCards(data);
        }

    }

    public void getOrderData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("order_status", order_status);
        paraMap.put("page", page + "");
        paraMap.put("pageSize", pageSize + "");

        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                hideLoading();
                initOrderData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                if (refrash != null) refrash.finishRefresh(1000, false, true);//传入false表示刷新失败
                Log.d("获取的数据", error);

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNetError();
                }else      hideLoading();

            }
        }, HttpUtil.ziying_shop_order_list_url, paraMap);
    }

    public List<OrderListResponse.OrderItem> data = new ArrayList<>();
    int page = 0;

    private void initOrderData(String result) {
        if (TextUtils.isEmpty(result)) return;
        OrderListResponse homeFenlei = new Gson().fromJson(result, OrderListResponse.class);
        if (homeFenlei == null) return;
        if (!TextUtils.isEmpty(homeFenlei.code) && homeFenlei.code.equals("error")) {
            data.clear();
            adapter.setCards(null);
            showNotData();
            return;
        }
        if (homeFenlei.orderList == null || homeFenlei.orderList.size() == 0) {
            refrash.setNoMoreData(true);
            data = homeFenlei.orderList;
            adapter.setCards(data);
            showNotData();
            return;
        }

        data = homeFenlei.orderList;
        adapter.setCards(data);
    }

    int pageSize = 20;

    public void getMoreData() {
        TreeMap<String, String> paraMap = new TreeMap<>();

        paraMap.put("page", (page + 1) + "");
        paraMap.put("order_status", order_status);
        paraMap.put("pageSize", pageSize + "");

        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取更多的数据", response);
                refrash.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                initMoreData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取更多的数据", error);
                refrash.finishLoadMore(1000, false, true);//传入false表示加载失败
            }
        }, HttpUtil.ziying_shop_order_list_url, paraMap);
    }

    private void initMoreData(String result) {
        if (TextUtils.isEmpty(result)) return;
        OrderListResponse homeFenlei = new Gson().fromJson(result, OrderListResponse.class);
        if (homeFenlei == null) return;
        if (homeFenlei.orderList == null) return;
        page++;
        data.addAll(homeFenlei.orderList);
        if (homeFenlei.orderList.size() < pageSize) {
            refrash.setNoMoreData(true);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        refrash.setNoMoreData(false);
        showLoading();
        getOrderData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        NLog.d("shopsubfragment", isVisibleToUser + "  setUserVisibleHint");

    }

    String id;

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NLog.d("shopsubfragment", "  onDestroyView");
    }

    Dialog dialog;

    @Override
    public void toPay(final OrderListResponse.OrderItem bean, final int index) {
        DialogUitl.showSimpleDialog(getContext(),
                getString(R.string.confir_pay_order_address),
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog1, String content) {
                        TreeMap<String, String> paraMap = new TreeMap<>();
                        paraMap.put("buy_type", bean.buy_type);
                        paraMap.put("order_no", bean.orderNumber);
                        dialog.show();
                        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(int what, String response) {
                                dialog.dismiss();
                                NLog.d("购买第五步", response);
                                OrderPayResponse orderPayResponse = new Gson().fromJson(response, OrderPayResponse.class);
                                if (orderPayResponse != null && !TextUtils.isEmpty(orderPayResponse.code) && orderPayResponse.code.equals("success")) {
                                    NToast.shortToast(getActivity(), getString(R.string.fukuan_success));
                                 getOrderData();
                                } else {
                                    NToast.shortToast(getActivity(), orderPayResponse.msg);
                                }

                            }

                            @Override
                            public void onFailure(int what, String error) {
                                dialog.dismiss();
                                NToast.shortToast(getActivity(), error);
                            }
                        }, HttpUtil.ziying_shop_order_pay_url, paraMap);

                    }
                });
    }

    @Override
    public void cancelOrder(final OrderListResponse.OrderItem bean, final int index) {
        DialogUitl.showSimpleDialog(getContext(),
                getString(R.string.confir_cancel_order_address),
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog1, String content) {
                        TreeMap<String, String> paraMap = new TreeMap<>();
                        paraMap.put("order_id", bean.id);
                        dialog.show();
                        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(int what, String response) {
                                dialog.dismiss();
                                NLog.d("取消订单", response);
                                BaseResponse orderPayResponse = new Gson().fromJson(response, BaseResponse.class);
                                if (orderPayResponse != null && !TextUtils.isEmpty(orderPayResponse.code) && orderPayResponse.code.equals("success")) {
                                    NToast.shortToast(getActivity(), getString(R.string.quxiao_success));
                                    getOrderData();
                                } else {
                                    NToast.shortToast(getActivity(), orderPayResponse.msg);
                                }

                            }

                            @Override
                            public void onFailure(int what, String error) {
                                dialog.dismiss();
                                NToast.shortToast(getActivity(), error);
                            }
                        }, HttpUtil.ziying_shop_order_cancel_url, paraMap);
                    }
                });

    }

    @Override
    public void shouhuo(final OrderListResponse.OrderItem bean, final int index) {

        DialogUitl.showSimpleDialog(getContext(),
                getString(R.string.confir_order_has_shouhuo_address),
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog1, String content) {
                        TreeMap<String, String> paraMap = new TreeMap<>();
                        paraMap.put("order_id", bean.id);
                        dialog.show();
                        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(int what, String response) {
                                dialog.dismiss();
                                NLog.d("确认收货订单", response);
                                BaseResponse orderPayResponse = new Gson().fromJson(response, BaseResponse.class);
                                if (orderPayResponse != null && !TextUtils.isEmpty(orderPayResponse.code) && orderPayResponse.code.equals("success")) {
                                    NToast.shortToast(getActivity(), getString(R.string.shouhuo_success));
                                    getOrderData();
                                } else {
                                    NToast.shortToast(getActivity(), orderPayResponse.msg);
                                }

                            }

                            @Override
                            public void onFailure(int what, String error) {
                                dialog.dismiss();
                                NToast.shortToast(getActivity(), error);
                            }
                        }, HttpUtil.ziying_shop_order_confir_shouhuo_url, paraMap);
                    }
                });

    }

    @Override
    public void wuliu(OrderListResponse.OrderItem bean, int index) {
        Intent intent=new Intent(getContext(),WuliuActivity.class);
        intent.putExtra("order_id",bean.id);
        startActivity(intent);
    }
}
