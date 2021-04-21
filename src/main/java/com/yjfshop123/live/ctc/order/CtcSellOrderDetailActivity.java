package com.yjfshop123.live.ctc.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.OtcOrderResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.widget.ShowCommonDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.DateFormatUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.net.utils.json.JsonMananger.jsonToBean;

/**
 * 自选区出售订单详情页面
 */
public class CtcSellOrderDetailActivity extends BaseActivityH {
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
    @BindView(R.id.cancel_btn)
    TextView cancelBtn;
    @BindView(R.id.pay_btn)
    TextView payBtn;
    @BindView(R.id.time_left)
    TextView timeLeft;
    @BindView(R.id.text_right)
    TextView text_right;

    OtcOrderResponse otcOrderResponse;
    String result;
    String order;
    @BindView(R.id.order_type)
    TextView order_type;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.name_ll)
    LinearLayout nameLl;
    @BindView(R.id.buyer_phone)
    TextView buyer_phone;
    @BindView(R.id.phone_ll)
    LinearLayout phoneLl;
    @BindView(R.id.copy)
    ImageView copy;
    @BindView(R.id.pay_type_logo_usdt)
    ImageView payTypeLogoUsdt;
    @BindView(R.id.fukuanpingzheng)
    LinearLayout fukuanpingzheng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_order_sell_detail);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        phoneLl.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            order = getIntent().getStringExtra("order");
//            if (TextUtils.isEmpty(order_type.getText().toString())
//                    || order_type.getText().toString().equals("已付款")) {
//
//            }

            getOrderData(false);
        }
        fukuanpingzheng.setVisibility(View.VISIBLE);
        initSwipeRefresh();
        cancelBtn.setVisibility(View.GONE);
        phoneLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otcOrderResponse != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + buyer_phone.getText().toString()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // SystemUtils.setClipboard(CtcSellOrderDetailActivity.this,buyer_phone.getText().toString());
                }
            }
        });
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

    public void getOrderData(boolean isFromRefresh) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("buy_order", order)
                    .build();
        } catch (JSONException e) {
        }
        if (!isFromRefresh)
            LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/c2cOrderDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                NToast.shortToast(CtcSellOrderDetailActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                if (TextUtils.isEmpty(result)) return;
                otcOrderResponse = new Gson().fromJson(result, OtcOrderResponse.class);
                if (otcOrderResponse != null) {
                    pic=otcOrderResponse.pics;
                    buyer_phone.setText(otcOrderResponse.buyer_phone);
                    jine.setText(otcOrderResponse.money);
                    seller.setText(otcOrderResponse.user_name);
                    price.setText(otcOrderResponse.price);
                    amount.setText(otcOrderResponse.eggs);
                    orderId.setText(otcOrderResponse.order);
                    createTime.setText(otcOrderResponse.create_time);

                    if (otcOrderResponse.status == 1) {
                        order_type.setText("等待买家付款");
                        payBtn.setText("我已收款");
                        payBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                    if (otcOrderResponse.status == 0) {

                        order_type.setText("已取消");
                        payBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                    if (otcOrderResponse.status == 2) {
                        payBtn.setVisibility(View.VISIBLE);
                        order_type.setText("买家已打款");
                        cancelBtn.setVisibility(View.GONE);
                    }
                    if (otcOrderResponse.status == 3) {
                        order_type.setText("已完成");
                        payBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                    if (otcOrderResponse.status == 4) {
                        order_type.setText("申诉中");
                        payBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    }

                    if (otcOrderResponse.status == 5) {
                        order_type.setText("待审核");
                        payBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.VISIBLE);
                    }
                    if (otcOrderResponse.status == 6) {
                        order_type.setText("审核不通过");
                        payBtn.setVisibility(View.GONE);

                    }
                    if (!TextUtils.isEmpty(otcOrderResponse.pay_type)) {
                        if (otcOrderResponse.pay_type.equals("card")) {
                            payType.setText(getString(R.string.yinhangka));
                            Glide.with(CtcSellOrderDetailActivity.this).load(R.mipmap.bank).into(payTypeLogo);
                        }
                        if (otcOrderResponse.pay_type.equals("alipay")) {
                            payType.setText(getString(R.string.tixian_ali));
                            Glide.with(CtcSellOrderDetailActivity.this).load(R.mipmap.zhifubao).into(payTypeLogo);
                        }
                        if (otcOrderResponse.pay_type.equals("wechat")) {
                            payType.setText(getString(R.string.setting_wx));
                            Glide.with(CtcSellOrderDetailActivity.this).load(R.mipmap.weixin).into(payTypeLogo);
                        }
                        if (otcOrderResponse.pay_type.equals(USDT_TYPE)) {
                            payTypeLogoUsdt.setVisibility(View.VISIBLE);
                            payTypeLogo.setVisibility(View.GONE);
                            payType.setText("USDT(ERC20)");
                            Glide.with(CtcSellOrderDetailActivity.this).load(R.mipmap.usdt).into(payTypeLogoUsdt);
                        } else {
                            payTypeLogoUsdt.setVisibility(View.GONE);
                            payTypeLogo.setVisibility(View.VISIBLE);
                        }
                    }
                    if (otcOrderResponse.status != 1)
                        return;
                    if (!TextUtils.isEmpty(otcOrderResponse.expire_time)) {
                        if (timer != null) timer.cancel();
                        long timeLe = DateFormatUtil.getDateLong(otcOrderResponse.expire_time) - System.currentTimeMillis();
                        if (timeLe <= 0) {
                            //超时
                            timeLeft.setText(R.string.has_chaoshi);
                        } else {
                            timer = new CountDownTimer(timeLe, 1000) {
                                /**
                                 * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                                 * @param millisUntilFinished
                                 */
                                @Override
                                public void onTick(long millisUntilFinished) {

                                    timeLeft.setText(millisUntilFinished / 60000 + "分" + (millisUntilFinished / 1000) % 60 + "秒");
                                }

                                /**
                                 * 倒计时完成时被调用
                                 */
                                @Override
                                public void onFinish() {
                                    getOrderData(false);
                                    timeLeft.setText(R.string.has_chaoshi);
                                }
                            };
                            timer.start();
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
    protected List<String> pic = new ArrayList<>();
    @OnClick({R.id.pay_btn, R.id.text_right,R.id.fukuanpingzheng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fukuanpingzheng:
                List<String> images = new ArrayList<>();
                if (pic != null&&pic.size()>0) {
                    try {
                        Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                        intent.putExtra(Config.POSITION, 0);
                        intent.putExtra("Picture", JsonMananger.beanToJson(pic));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }else {
                    NToast.shortToast(this,"暂未上传凭证");
                }
            break;
            case R.id.text_right:
                LoadDialog.show(CtcSellOrderDetailActivity.this);
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("user_id", otcOrderResponse.admin_user_id)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/user/getUserHomeByUid", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                        NToast.shortToast(mContext, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                        try {
                            UserHomeResponse mResponse = jsonToBean(result, UserHomeResponse.class);
                            IMConversation imConversation = new IMConversation();
                            imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

                            imConversation.setUserIMId(UserInfoUtil.getMiTencentId());
                            imConversation.setUserId(UserInfoUtil.getMiPlatformId());

                            imConversation.setOtherPartyIMId(String.valueOf(mResponse.getUser_info().getProm_custom_uid()));
                            imConversation.setOtherPartyId(String.valueOf(mResponse.getUser_info().getUser_id()));

                            imConversation.setUserName(UserInfoUtil.getName());
                            imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                            imConversation.setOtherPartyName(mResponse.getUser_info().getUser_nickname());
                            imConversation.setOtherPartyAvatar(mResponse.getUser_info().getAvatar());

                            imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());


                            Intent intent1 = new Intent(mContext, MessageListActivity.class);
                            intent1.putExtra("IMConversation", imConversation);
                            startActivity(intent1);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;

            case R.id.pay_btn:
                if (otcOrderResponse == null) {
                    NToast.shortToast(this, "缺少页面参数，请下拉刷新重新获取");
                    return;
                }
                //TODO 我已收付款
                dialogFragment.setTitle("请确认已收到付款");
                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirGet();
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            finish();
        }
    }

    ShowCommonDialog dialogFragment = new ShowCommonDialog();

    //我已收款
    public void confirGet() {
        if (otcOrderResponse == null) return;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("buy_order", otcOrderResponse.order)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/sellRelease", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                NToast.shortToast(CtcSellOrderDetailActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(CtcSellOrderDetailActivity.this);
                NToast.shortToast(CtcSellOrderDetailActivity.this, "已确认收款");
                finish();
            }
        });

    }
}
