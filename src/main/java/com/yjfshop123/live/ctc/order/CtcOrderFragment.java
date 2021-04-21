package com.yjfshop123.live.ctc.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcOrderListResponse;
import com.yjfshop123.live.otc.adapter.OrderAdapter;
import com.yjfshop123.live.otc.order.OrderManagerActivity;
import com.yjfshop123.live.otc.order.OrderSellDetailActivity;
import com.yjfshop123.live.otc.order.OtcOrderManagerFragment;
import com.yjfshop123.live.otc.ui.OrderDetailActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CtcOrderFragment extends BaseFragment {
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_order_otc;
    }

    int type = -1;//0已取消，3已完成，-1进行中

    public void setType(int type) {
        this.type = type;
    }

    LinearLayoutManager mLinearLayoutManager;
    OrderAdapter xunZhangAdapter;

    @Override
    protected void initAction() {

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new OrderAdapter(mContext);
        xunZhangAdapter.setListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onclick(OtcOrderListResponse.OtcOrderData data) {
                if (data == null) return;
                if (data.order_type == 0) {
                    //买单详情
                    Intent intent = new Intent(getActivity(), CtcBuyOrderDetailActivity.class);
                    intent.putExtra("order", data.order);
                    startActivity(intent);

                } else {
                    //卖单详情
                    Intent intent = new Intent(getActivity(), CtcSellOrderDetailActivity.class);
                    intent.putExtra("order", data.order);
                    startActivity(intent);

                }
            }
        });
        shimmerRecycler.setAdapter(xunZhangAdapter);
        //showNotData();
        if (list == null || list.size() == 0) {
            getData(false);
        }else {
            hideLoading();
            if(xunZhangAdapter!=null)xunZhangAdapter.setCards(list);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (page == 1&&getUserVisibleHint())
            getData(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if (page == 1&&(list==null||list.size()==0))
                getData(false);
        }
    }

    boolean isLoadingMore = false;
    int page = 1;

    @Override
    protected void initEvent() {
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

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        page = 1;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }
        if (!isFromRefresh) {
            showLoading();
        }

        OKHttpUtils.getInstance().getRequest("app/trade/c2cOrderList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMore = false;

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                } else hideLoading();
                //测试数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                isLoadingMore = false;
                //模拟数据
                // loadData(testData);
                loadData(result);
            }
        });

    }

    List<OtcOrderListResponse.OtcOrderData> list;
    CtcOrderManagerActivity orderManagerActivity;

    public void setActivity(CtcOrderManagerActivity orderManagerActivity) {
        this.orderManagerActivity = orderManagerActivity;
    }
    CtcOrderManagerFragment ctcOrderManagerFragment;

    public void setFragment(CtcOrderManagerFragment ctcOrderManagerFragment) {
        this.ctcOrderManagerFragment = ctcOrderManagerFragment;
    }
    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        OtcOrderListResponse data = new Gson().fromJson(result, OtcOrderListResponse.class);
        if (data.list.size() > 0) {
            if (orderManagerActivity != null) {
                orderManagerActivity.setHongDian(View.VISIBLE);
            }
            if (ctcOrderManagerFragment != null) {
                ctcOrderManagerFragment.setHongDian(View.VISIBLE);
            }
            page = 1;
        } else {
            if(orderManagerActivity!=null){
                orderManagerActivity.setHongDian(View.INVISIBLE);
            }
            if (ctcOrderManagerFragment != null) {
                ctcOrderManagerFragment.setHongDian(View.INVISIBLE);
            }
            showNotData();
        }
        this.list = data.list;
        xunZhangAdapter.setCards(this.list);

    }

    /**
     * 获取数据
     */
    public void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/trade/c2cOrderList", body, new RequestCallback() {
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

    public void loadMoreData(String result) {
        OtcOrderListResponse data = new Gson().fromJson(result, OtcOrderListResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
