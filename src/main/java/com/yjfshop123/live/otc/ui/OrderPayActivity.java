package com.yjfshop123.live.otc.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.OtcOrderResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.TargetRewardActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.widget.ShowCommonDialog;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.utils.DateFormatUtil;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class OrderPayActivity extends BaseActivityH {
    OtcOrderResponse otcOrderResponse;
    String result;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.time_left)
    TextView timeLeft;
    @BindView(R.id.jine)
    TextView jine;
    @BindView(R.id.shoukuanren)
    TextView shoukuanren;
    @BindView(R.id.name_copy)
    ImageView nameCopy;
    @BindView(R.id.bank_name)
    TextView bankName;
    @BindView(R.id.bank_name_copy)
    ImageView bankNameCopy;
    @BindView(R.id.bank_create_name)
    TextView bankCreateName;
    @BindView(R.id.bank_create_name_copy)
    ImageView bankCreateNameCopy;
    @BindView(R.id.bank_account)
    TextView bankAccount;
    @BindView(R.id.bank_account_copy)
    ImageView bankAccountCopy;
    @BindView(R.id.bank_ll)
    LinearLayout bankLl;
    @BindView(R.id.shoukuanren_other)
    TextView shoukuanrenOther;
    @BindView(R.id.name_other_copy)
    ImageView nameOtherCopy;
    @BindView(R.id.shoukuan_account_other)
    TextView shoukuanAccount;
    @BindView(R.id.shoukuan_account_other_copy)
    ImageView shoukuanAccountCopy;
    @BindView(R.id.erweima)
    ImageView erweima;
    @BindView(R.id.other_ll)
    LinearLayout otherLl;
    @BindView(R.id.pay_btn)
    TextView payBtn;
    String payTypeSelect = BANK_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_order_pay);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        if (getIntent() != null) {
            result = getIntent().getStringExtra("result");
            if (!TextUtils.isEmpty(result)) {
                otcOrderResponse = new Gson().fromJson(result, OtcOrderResponse.class);
            }
        }

        if (otcOrderResponse != null) {
            jine.setText(otcOrderResponse.money);

            shoukuanren.setText(otcOrderResponse.user_name);
            shoukuanrenOther.setText(otcOrderResponse.user_name);
            if (!TextUtils.isEmpty(otcOrderResponse.pay_type)) {
                if (otcOrderResponse.pay_type.equals("card")) {
                    tvTitleCenter.setText(getString(R.string.yinhangka));
                    payTypeSelect = BANK_TYPE;
                    bankLl.setVisibility(View.VISIBLE);
                    otherLl.setVisibility(View.GONE);
                    bankName.setText(otcOrderResponse.bank);
                    bankCreateName.setText(otcOrderResponse.bank_branch);
                    bankAccount.setText(otcOrderResponse.pay_address);
                }
                if (otcOrderResponse.pay_type.equals("alipay")|| otcOrderResponse.pay_type.equals("wechat")) {
                    bankLl.setVisibility(View.GONE);
                    otherLl.setVisibility(View.VISIBLE);
                    shoukuanAccount.setText(otcOrderResponse.pay_address);
                    if (otcOrderResponse.pay_type.equals("alipay")) {
                        tvTitleCenter.setText(getString(R.string.tixian_ali));
                        payTypeSelect = ALIPAY_TYPE;
                    }
                    if (otcOrderResponse.pay_type.equals("wechat")) {
                        tvTitleCenter.setText(getString(R.string.setting_wx));
                        payTypeSelect = WECHAT_TYPE;
                    }
                }
            }
            if (!TextUtils.isEmpty(otcOrderResponse.expire_time)) {
                if (timer != null) timer.cancel();
                long timeLe =   DateFormatUtil.getDateLong(otcOrderResponse.expire_time)-System.currentTimeMillis();
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
                            timeLeft.setText(R.string.has_chaoshi);
                        }
                    };
                    timer.start();
                }
            }

        }
    }

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

    CountDownTimer timer;

    @OnClick({R.id.name_copy, R.id.bank_name_copy, R.id.bank_create_name_copy, R.id.bank_account_copy, R.id.name_other_copy, R.id.shoukuan_account_other_copy, R.id.erweima, R.id.pay_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.name_copy:
                SystemUtils.setClipboard(this, shoukuanren.getText().toString());
                break;
            case R.id.bank_name_copy:
                SystemUtils.setClipboard(this, bankName.getText().toString());
                break;
            case R.id.bank_create_name_copy:
                SystemUtils.setClipboard(this, bankCreateName.getText().toString());
                break;
            case R.id.bank_account_copy:
                SystemUtils.setClipboard(this, bankAccount.getText().toString());
                break;
            case R.id.name_other_copy:
                SystemUtils.setClipboard(this, shoukuanrenOther.getText().toString());
                break;
            case R.id.shoukuan_account_other_copy:
                SystemUtils.setClipboard(this, shoukuanAccount.getText().toString());
                break;
            case R.id.erweima:
                try {
                    List<String> images = new ArrayList<>();
                    images.add(otcOrderResponse.code_url);
                    Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                    intent.putExtra(Config.POSITION, 0);
                    intent.putExtra("Picture", JsonMananger.beanToJson(images));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pay_btn:
                dialogFragment.setTitle(getString(R.string.confir_pay_tips)
                );
                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirPay();
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");
                break;
        }
    }

    ShowCommonDialog dialogFragment = new ShowCommonDialog();

    public void confirPay() {
        if (otcOrderResponse == null) return;
        String body = "";
        if (TextUtils.isEmpty(otcOrderResponse.order_sn)) return;
        try {
            body = new JsonBuilder()
                    .put("order_sn", otcOrderResponse.order_sn)
                    .put("pay_type", payTypeSelect)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/payComplete", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(OrderPayActivity.this);
                NToast.shortToast(OrderPayActivity.this,errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(OrderPayActivity.this);
                DialogUitl.showSimpleHintDialog(OrderPayActivity.this, "", getString(R.string.confirm),
                        "",
                        getString(R.string.pay_complete_dengdai_fangxing),
                        true,
                        false,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                                setResult(1);
                                finish();
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                setResult(1);
                                finish();
                            }
                        });


            }
        });

    }
}
