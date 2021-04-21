package com.yjfshop123.live.otc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcBuySellLimitResponse;
import com.yjfshop123.live.model.PaySettingResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.otc.adapter.PayTypeSelectAdapter;
import com.yjfshop123.live.otc.setshoukuan.AliSetActivity;
import com.yjfshop123.live.otc.setshoukuan.BankSetActivity;
import com.yjfshop123.live.otc.setshoukuan.SelectTypeActivity;
import com.yjfshop123.live.otc.setshoukuan.UsdtSetActivity;
import com.yjfshop123.live.otc.setshoukuan.WeixinSetActivity;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class OrderRecieveTypeSelectActivity extends BaseActivityH {
    @BindView(R.id.right)
    TextView right;
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty)
    EmptyLayout mEmptyLayout;

    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    PayTypeSelectAdapter adapter;
    LinearLayoutManager mLinearLayoutManager;
    String select_id;
    //boolean is_edite = false;//是否处于编辑状态
    OtcBuySellLimitResponse limitDataResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_recieve_type_select);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("limit"))) {
            limitDataResponse = new Gson().fromJson(getIntent().getStringExtra("limit"), OtcBuySellLimitResponse.class);
        }
        if (getIntent() != null)
            select_id = getIntent().getStringExtra("select_id");
        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new PayTypeSelectAdapter(this);
        adapter.setSelectId(select_id);
        adapter.setLimit(limitDataResponse);
        adapter.setListener(new PayTypeSelectAdapter.OnItemClickListener() {
            @Override
            public void onclick(PaySettingResponse.PayData data,boolean is_edite) {
                if (!is_edite) {
                    if (data.type.equals(BANK_TYPE)) {
                        if (limitDataResponse != null && limitDataResponse.card == null) return;
                    }
                    if (data.type.equals(WECHAT_TYPE)) {
                        if (limitDataResponse != null && limitDataResponse.wechat == null) return;
                    }
                    if (data.type.equals(ALIPAY_TYPE)) {
                        if (limitDataResponse != null && limitDataResponse.alipay == null) return;
                    }
                    if (data.type.equals(USDT_TYPE)) {
                        if (limitDataResponse != null && limitDataResponse.usdt == null) return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("pay_type", data.type);
                    intent.putExtra("select_id", data.id);
                    intent.putExtra("card", data.card);
                    setResult(1, intent);
                    finish();
                } else {
                    // Intent intent = new Intent(OrderRecieveTypeSelectActivity.this,);
                    if (data.type.equals(BANK_TYPE)) {
                        Intent intent = new Intent(OrderRecieveTypeSelectActivity.this, BankSetActivity.class);
                        intent.putExtra("data", new Gson().toJson(data));
                        intent.putExtra("is_edite", true);
                        intent.putExtra("select_id", select_id);
                        intent.putExtra("id", data.id);
                        startActivity(intent);
                        return;
                    }
                    if (data.type.equals(WECHAT_TYPE)) {
                        Intent intent = new Intent(OrderRecieveTypeSelectActivity.this, WeixinSetActivity.class);
                        intent.putExtra("data", new Gson().toJson(data));
                        intent.putExtra("is_edite", true);
                        intent.putExtra("select_id", select_id);
                        intent.putExtra("id", data.id);
                        startActivity(intent);
                        return;
                    }
                    if (data.type.equals(ALIPAY_TYPE)) {
                        Intent intent = new Intent(OrderRecieveTypeSelectActivity.this, AliSetActivity.class);
                        intent.putExtra("data", new Gson().toJson(data));
                        intent.putExtra("is_edite", true);
                        intent.putExtra("select_id", select_id);
                        intent.putExtra("id", data.id);
                        startActivity(intent);
                        return;
                    }

                    if (data.type.equals(USDT_TYPE)) {
                        Intent intent = new Intent(OrderRecieveTypeSelectActivity.this, UsdtSetActivity.class);
                        intent.putExtra("data", new Gson().toJson(data));
                        intent.putExtra("is_edite", true);
                        intent.putExtra("id", data.id);
                        startActivity(intent);
                        return;
                    }
                }
            }
        });
        shimmerRecycler.setAdapter(adapter);

        initSwipeRefresh();
        showLoading();
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                is_edite = !is_edite;
//                if (is_edite) {
//                    right.setText(getString(R.string.cancel));
//                } else right.setText(getString(R.string.album_edit));
//                adapter.setBianjiType(is_edite);
//                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    loadData();
                }
            });
        }
        finishRefresh();
    }

    private void loadData() {

        String body = "";

            try {
                if (limitDataResponse == null) {
                body = new JsonBuilder()
                        .put("type", "c2c")
                        .build();
                }else{
                    body = new JsonBuilder()
                            .put("type", "otc")
                            .build();
                }
            } catch (JSONException e) {
            }

        OKHttpUtils.getInstance().getRequest("app/trade/cardList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(OrderRecieveTypeSelectActivity.this, errInfo);
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }
            }

            @Override
            public void onSuccess(String result) {

                initDatas(result);
            }
        });
    }

    PaySettingResponse data;

    private void initDatas(String result) {

        if (TextUtils.isEmpty(result)) {
            showNotData();
            return;
        }
        data = new Gson().fromJson(result, PaySettingResponse.class);
        if (data.list.size() <= 0) showNotData();
        else hideLoading();
        adapter.setCards(data.list);
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }


    @OnClick({R.id.confir_btn, R.id.right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confir_btn:
                Intent intent = new Intent(this, SelectTypeActivity.class);
                if (limitDataResponse != null) {
                    intent.putExtra("limit", new Gson().toJson(limitDataResponse));
                }

                startActivity(intent);
                break;
            case R.id.right:
                break;
        }
    }
}
