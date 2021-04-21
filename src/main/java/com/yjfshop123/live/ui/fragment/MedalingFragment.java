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
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.adapter.MedalingAdapter;
import com.yjfshop123.live.ui.adapter.XunZhangConfigAdapter;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 选品中金蛋
 */
public class MedalingFragment extends BaseFragment {

    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;
    private MedalingAdapter xunZhangAdapter;
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

        xunZhangAdapter = new MedalingAdapter(mContext);
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
            "        \"medal_des\": \"体验选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "        \"effective_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\":2,//数据唯一id\n" +
            "        \"medal_des\": \"一级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-29 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"3级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":4,//数据唯一id\n" +
            "        \"medal_des\": \"4级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":5,//数据唯一id\n" +
            "        \"medal_des\": \"5级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"6级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"7级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"8级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"9级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"10级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"11级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"12级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"13级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"14级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"15级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"16级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"17级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"18级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"19级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"20级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
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

        OKHttpUtils.getInstance().getRequest("app/medal/myMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                isLoadingMore = false;
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNetError();
                }
                //测试数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                loadData(result);
                //模拟数据
                //loadData(testData);

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

        OKHttpUtils.getInstance().getRequest("app/medal/myMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                //模拟数据
               // loadMoreData(testData);
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;

                loadMoreData(result);
                //模拟数据
               // loadMoreData(testData);

            }
        });

    }

    int page = 1;
    List<MyXunZhangResponse.MyXunZhangData> list;

    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        MyXunZhangResponse data = new Gson().fromJson(result, MyXunZhangResponse.class);
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
        MyXunZhangResponse data = new Gson().fromJson(result, MyXunZhangResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }        this.list.addAll(data.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
