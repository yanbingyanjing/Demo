package com.yjfshop123.live.otc.order;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcOrderResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 快捷区购买订单详情页面
 */
public class OrderDetailOkOrCancelActivity extends BaseActivityH {
    @BindView(R.id.jine)
    TextView jine;
    @BindView(R.id.seller)
    TextView seller;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.order_id)
    TextView orderId;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.pay_type_logo)
    ImageView payTypeLogo;
    @BindView(R.id.pay_type)
    TextView payType;
    @BindView(R.id.time_left)
    TextView timeLeft;
    OtcOrderResponse otcOrderResponse;
    String result;
    String order;
    @BindView(R.id.order_type)
    TextView orderType;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_order_ok_cancel);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        if (getIntent() != null) {
            order = getIntent().getStringExtra("order");
            orderType.setText(getIntent().getStringExtra("desc"));
            getOrderData(false);
        }
        initSwipeRefresh();
    }
    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getOrderData(true);
                }
            });

        }
    }
    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void getOrderData(boolean isfromrefresh) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("order", order)
                    .build();
        } catch (JSONException e) {
        }
        if(!isfromrefresh)
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/otcOrderDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                LoadDialog.dismiss(OrderDetailOkOrCancelActivity.this);
                NToast.shortToast(OrderDetailOkOrCancelActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                LoadDialog.dismiss(OrderDetailOkOrCancelActivity.this);
                if (TextUtils.isEmpty(result)) return;
                otcOrderResponse = new Gson().fromJson(result, OtcOrderResponse.class);
                if (otcOrderResponse != null) {
                    jine.setText(otcOrderResponse.money);
                    seller.setText(otcOrderResponse.user_name);
                    price.setText(otcOrderResponse.price);
                    amount.setText(otcOrderResponse.eggs);
                    orderId.setText(otcOrderResponse.order_sn);
                    createTime.setText(otcOrderResponse.create_time);

                    if (!TextUtils.isEmpty(otcOrderResponse.pay_type)) {
                        if (otcOrderResponse.pay_type.equals("card")) {
                            payType.setText(getString(R.string.yinhangka));
                            Glide.with(OrderDetailOkOrCancelActivity.this).load(R.mipmap.bank).into(payTypeLogo);
                        }
                        if (otcOrderResponse.pay_type.equals("alipay")) {
                            payType.setText(getString(R.string.tixian_ali));
                            Glide.with(OrderDetailOkOrCancelActivity.this).load(R.mipmap.zhifubao).into(payTypeLogo);
                        }
                        if (otcOrderResponse.pay_type.equals("wechat")) {
                            payType.setText(getString(R.string.setting_wx));
                            Glide.with(OrderDetailOkOrCancelActivity.this).load(R.mipmap.weixin).into(payTypeLogo);
                        }
                    }
                }
            }
        });

    }

    CountDownTimer timer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }


}
