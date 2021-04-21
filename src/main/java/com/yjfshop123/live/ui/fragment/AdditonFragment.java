package com.yjfshop123.live.ui.fragment;

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
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.adapter.AdditionAdapter;
import com.yjfshop123.live.ui.adapter.MedalingAdapter;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AdditonFragment extends BaseFragment {

    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;
    private AdditionAdapter xunZhangAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_medaling;
    }

    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new AdditionAdapter(mContext);
        shimmerRecycler.setAdapter(xunZhangAdapter);
        showNotData();
        showLoading();
        getData(false);
    }

    int type = 1;

    public void setType(int type) {
        this.type = type;
    }

    boolean isLoadingMore = false;

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

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }


    String testData = "{\n" +
            "      \"list\": [\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        },\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +

            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"addition_origin\": \"体验选品\",//选品名称\n" +
            "        \"addition_user\": 11,//每日收益\n" +
            "        \"addition_amount\":10,//兑换所需金蛋数量\n" +
            "        \"addition_released\":44,//总释放天数\n" +
            "        \"addition_day_income\":3,//已释放天数\n" +
            "        \"start_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        }\n" +
            "      ]\n" +
            "}";

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        page=1;
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

        OKHttpUtils.getInstance().getRequest("app/egg/addition", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMore = false;
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }
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

        OKHttpUtils.getInstance().getRequest("app/egg/addition", body, new RequestCallback() {
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

    int page = 1;
    List<AdditionResponse.AdditionData> list;

    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        AdditionResponse data = new Gson().fromJson(result, AdditionResponse.class);
        if (data.list == null) return;
        if (data.list.size() > 0) {
            page = 1;
        } else showNotData();
        this.list = data.list;
        xunZhangAdapter.setCards(this.list);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void loadMoreData(String result) {
        AdditionResponse data = new Gson().fromJson(result, AdditionResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
