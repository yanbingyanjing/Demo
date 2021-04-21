package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
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
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.model.XunZhangConfigResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.adapter.XunZhangConfigAdapter;
import com.yjfshop123.live.ui.widget.ShowImgDialog;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class XunZhangConfigFragment extends BaseFragment implements OnItemClickListener {
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;
    @BindView(R.id.nested)
    NestedScrollView nested;
    Unbinder unbinder1;
    private XunZhangConfigAdapter xunZhangAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_xunzhang;
    }

    ShowImgDialog dialogFragment = new ShowImgDialog();

    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new XunZhangConfigAdapter(mContext, this);
        xunZhangAdapter.setBuyOnClick(new XunZhangConfigAdapter.BuyOnClisk() {
            @Override
            public void onClick(final XunZhangConfigResponse.XunZhangConfigData bean) {

                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyMedal(bean.id+"");
                    }
                });
                dialogFragment.setLineColorlor(R.color.color_0D000050);
                dialogFragment.setTitleColor(R.color.color_000000);
                dialogFragment.setTitle(bean.buy_tips_des);
                dialogFragment.setBgDrawable(R.drawable.bg_ffffff_14);
                dialogFragment.show(getChildFragmentManager(), "ShowImgDialog");
            }
        });
        shimmerRecycler.setAdapter(xunZhangAdapter);
        showNotData();
    }

    /**
     * 获取数据
     */
    public void buyMedal(String id) {

        LoadDialog.show(getContext());
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("medal_id", id)
                    .put("count", "1")
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/medal/buyMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                LoadDialog.dismiss(getContext());
                NToast.shortToast(getContext(), errInfo);
                //模拟数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
                ((MyFragmentNewTwo) fragment).getUserInfoData();
                NToast.shortToast(getContext(), "购买成功");
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData(false);
    }

    MyFragmentNewTwo fragment;

    public void setFragment(MyFragmentNewTwo fragment) {
        this.fragment = fragment;
    }


    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {

        if (isFromRefresh) {
        } else {
            showLoading();
        }


        OKHttpUtils.getInstance().getRequest("app/medal/config", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (isFromRefresh) {
                    if (fragment != null)
                        ((MyFragmentNewTwo) fragment).hideLoading();
                } else {
                    if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                        showNotData();
                    }
                }

                //模拟数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                if (isFromRefresh) {
                    if (fragment != null)
                        ((MyFragmentNewTwo) fragment).hideLoading();
                } else {
                    hideLoading();
                }
                //模拟数据
                //loadData(testData);
                loadData(result);
            }
        });

    }


    public void loadData(String result) {

        hideLoading();
        if (TextUtils.isEmpty(result)) {
            nested.setVisibility(View.VISIBLE);
            showNotData();
            return;
        } else nested.setVisibility(View.GONE);

        XunZhangConfigResponse data = new Gson().fromJson(result, XunZhangConfigResponse.class);
        if (data.list == null) {
            nested.setVisibility(View.VISIBLE);
            showNotData();
            return;
        }
        if (data.list.length > 0) {
            nested.setVisibility(View.GONE);
        } else {
            nested.setVisibility(View.VISIBLE);
            showNotData();
        }
        xunZhangAdapter.setCards(data.list, data.gold_egg);

    }


    @Override
    protected void initEvent() {
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        NLog.d("重试", "获取数据");
        getData(false);
    }

    @Override
    public void onItemClick(Object bean, int position) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
