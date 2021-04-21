package com.yjfshop123.live.shop.ziying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.shop.pintuan.PintuanListActivity;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.NewShopAdapter;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewShopListActivity extends BaseActivityH {
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
    @BindView(R.id.text_right)
    TextView textRight;
    @BindView(R.id.banner)
    SelectableRoundedImageView banner;
    private GridLayoutManager mLinearLayoutManager;
    private NewShopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shop_list);
        ButterKnife.bind(this);
        CollapsingToolbarLayout.LayoutParams paramsSS = (CollapsingToolbarLayout.LayoutParams) banner.getLayoutParams();
        //获取当前控件的布局对象
        paramsSS.width = SystemUtils.getScreenWidth(this) - SystemUtils.dip2px(this, 30);//设置当前控件布局的高度
        paramsSS.height = paramsSS.width * 2 / 5;
        banner.setLayoutParams(paramsSS);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewShopListActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        mLinearLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new NewShopAdapter(this);
        list.setAdapter(adapter);
        refrash.setNoMoreData(false);
        refrash.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refrash.setNoMoreData(false);
                page = 0;
                loadData();
                getShopData();

            }
        });
        refrash.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getMoreData();

            }
        });
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadDialog.show(NewShopListActivity.this);
                OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(NewShopListActivity.this);
//                        Intent intent = new Intent(NewShopListActivity.this, PintuanListActivity.class);
//                        startActivity(intent);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(NewShopListActivity.this);
                        try {
                            if (TextUtils.isEmpty(result)) {
//                                Intent intent = new Intent(NewShopListActivity.this, PintuanListActivity.class);
//                                startActivity(intent);
                                return;
                            }
                            UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                            if (data.jump != null) {
                                if (data.jump.pintuan_is_h5 == 1) {
                                    Intent intent = new Intent(NewShopListActivity.this, WebViewActivity.class);
                                    if (!TextUtils.isEmpty(data.jump.pintuan_h5)) {
                                        intent.putExtra("url", data.jump.pintuan_h5);
                                        startActivity(intent);
                                    }
                                } else {
//                                    Intent intent = new Intent(NewShopListActivity.this, PintuanListActivity.class);
//                                    startActivity(intent);
                                }
                            } else {
//                                Intent intent = new Intent(NewShopListActivity.this, PintuanListActivity.class);
//                                startActivity(intent);
                            }
                        } catch (Exception e) {
//                            Intent intent = new Intent(NewShopListActivity.this, PintuanListActivity.class);
//                            startActivity(intent);
                        }
                    }
                });
            }

        });
        showLoading();
        loadData();
        getShopData();
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
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


    private void loadData() {

        OKHttpUtils.getInstance().getRequest("app/task/newTaskCenter", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                } else hideLoading();

            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                TaskNewResponse task = new Gson().fromJson(result, TaskNewResponse.class);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                        .error(R.drawable.imageloader)// 加载失败的图片  
                        .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

                Glide.with(NewShopListActivity.this)
                        .load(CommonUtils.getUrl(task.task_center_icon))// 图片地址  
                        .apply(options)// 参数  
                        .into(banner);// 需要显示的ImageView控件
            }
        });
    }


    public void getShopData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("cid", "0");
        paraMap.put("search_key", "");
        paraMap.put("page", page + "");
        paraMap.put("goods_type", "1");
        paraMap.put("order_by", "goods_id desc");
        paraMap.put("pageSize", pageSize + "");
        paraMap.put("filter", "");
        paraMap.put("area_id", "0");
        paraMap.put("shop_id", "");
        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                if (refrash != null) refrash.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                hideLoading();
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
        }, HttpUtil.ziying_shop_list_url, paraMap);
    }

    public List<ZiyingShopList.Data> data;
    int page = 0;

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ZiyingShopList homeFenlei = new Gson().fromJson(result, ZiyingShopList.class);
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

    public void getMoreData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("cid", "0");
        paraMap.put("search_key", "");
        paraMap.put("page", (page + 1) + "");
        paraMap.put("goods_type", "1");
        paraMap.put("order_by", "goods_id desc");
        paraMap.put("pageSize", pageSize + "");
        paraMap.put("filter", "");
        paraMap.put("area_id", "0");
        paraMap.put("shop_id", "");

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
        }, HttpUtil.ziying_shop_list_url, paraMap);
    }

    private void initMoreData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ZiyingShopList homeFenlei = new Gson().fromJson(result, ZiyingShopList.class);
        if (homeFenlei == null) return;
        if (homeFenlei.list == null) return;
        page++;
        data.addAll(homeFenlei.list);
        if (homeFenlei.list.size() < pageSize) {
            refrash.setNoMoreData(true);
        }
        adapter.notifyDataSetChanged();
    }

}
