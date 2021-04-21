package com.yjfshop123.live.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.shop.adapter.ShopAdapter;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.ui.fragment.BaseFragment;


import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

public class ShopSubFragment extends BaseFragment {
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.refrash)
    SmartRefreshLayout refreshLayout;
    private GridLayoutManager mLinearLayoutManager;
    private ShopAdapter adapter;


    @Override
    protected int setContentViewById() {
        return R.layout.fragment_shop_sub;
    }

    @Override
    protected void initAction() {
        NLog.d("shopsubfragment",  "  initAction");
        mLinearLayoutManager = new GridLayoutManager(getContext(), 2);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new ShopAdapter(getContext());
        list.setAdapter(adapter);
        refreshLayout.setNoMoreData(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.setNoMoreData(false);
                getData();

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(pageId)) {
                    refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败

                } else {
                    getMoreData();
                }
            }
        });
        if (data == null || data.size() == 0) {
            if(refreshLayout!=null)  refreshLayout.autoRefresh();
            getData();
        }else {
            if(adapter!=null)adapter.setCards(data);
        }

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {

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

    public void getData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.2.3");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("pageId", "1");
        paraMap.put("cids", id);

        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
               if(refreshLayout!=null) refreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                initData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                if(refreshLayout!=null)  refreshLayout.finishRefresh(1000, false, true);//传入false表示刷新失败
                Log.d("获取的数据", error);

            }
        }, HttpUtil.shop_url, paraMap);
    }

    public List<ShopList.ShopData> data;
    String pageId = "";

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ShopList homeFenlei = new Gson().fromJson(result, ShopList.class);
        if (homeFenlei == null) return;
        if (homeFenlei.data == null) return;
        if (homeFenlei.data.list == null) return;
        data = homeFenlei.data.list;
        pageId = homeFenlei.data.pageId;
        adapter.setCards(data);
    }


    public void getMoreData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.2.3");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("pageId", pageId);
        paraMap.put("cids", id);

        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取更多的数据", response);
                refreshLayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                initMoreData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取更多的数据", error);
                refreshLayout.finishLoadMore(1000, false, true);//传入false表示加载失败
            }
        }, HttpUtil.shop_url, paraMap);
    }

    private void initMoreData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ShopList homeFenlei = new Gson().fromJson(result, ShopList.class);
        if (homeFenlei == null) return;
        if (homeFenlei.data == null) return;
        if (homeFenlei.data.list == null) return;
        data.addAll(homeFenlei.data.list);
        if (homeFenlei.data.list.size() < 100) {
            refreshLayout.setNoMoreData(true);
        }
        pageId = homeFenlei.data.pageId;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NLog.d("shopsubfragment",  "  onDestroyView");
    }
}
