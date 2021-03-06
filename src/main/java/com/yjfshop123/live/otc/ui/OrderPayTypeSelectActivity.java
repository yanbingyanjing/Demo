package com.yjfshop123.live.otc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.OtcBuySellLimitResponse;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

//支付方式选择界面
public class OrderPayTypeSelectActivity extends BaseActivityH {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.zhifubao_select)
    ImageView zhifubaoSelect;
    @BindView(R.id.weixin_select)
    ImageView weixinSelect;
    @BindView(R.id.bank_select)
    ImageView bankSelect;
    @BindView(R.id.zhifubao_ll)
    LinearLayout zhifubaoLl;
    @BindView(R.id.weixin_ll)
    LinearLayout weixinLl;
    @BindView(R.id.bank_ll)
    LinearLayout bankLl;
    OtcBuySellLimitResponse limitDataResponse;
    @BindView(R.id.one)
    View one;
    @BindView(R.id.two)
    View two;
    @BindView(R.id.alipay_zanbuzhichi)
    TextView alipayZanbuzhichi;
    @BindView(R.id.weichat_zanbuzhichi)
    TextView weichatZanbuzhichi;
    @BindView(R.id.bank_zanbuzhichi)
    TextView bankZanbuzhichi;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.three)
    View three;
    @BindView(R.id.usdt_zanbuzhichi)
    TextView usdtZanbuzhichi;
    @BindView(R.id.usdt_select)
    ImageView usdtSelect;
    @BindView(R.id.usdt_ll)
    LinearLayout usdtLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_pay_type_select);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        if (getIntent() != null) {
            String payType = getIntent().getStringExtra("pay_type");
            if (payType.equals(BANK_TYPE)) {
                bankSelect.setVisibility(View.VISIBLE);
                weixinSelect.setVisibility(View.GONE);
                zhifubaoSelect.setVisibility(View.GONE);
            }
            if (payType.equals(WECHAT_TYPE)) {
                bankSelect.setVisibility(View.GONE);
                weixinSelect.setVisibility(View.VISIBLE);
                zhifubaoSelect.setVisibility(View.GONE);
            }
            if (payType.equals(ALIPAY_TYPE)) {
                bankSelect.setVisibility(View.GONE);
                weixinSelect.setVisibility(View.GONE);
                zhifubaoSelect.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("limit"))) {
                limitDataResponse = new Gson().fromJson(getIntent().getStringExtra("limit"), OtcBuySellLimitResponse.class);
                if (limitDataResponse.wechat == null) {
                    weichatZanbuzhichi.setVisibility(View.VISIBLE);
                }

                if (limitDataResponse.alipay == null) {
                    alipayZanbuzhichi.setVisibility(View.VISIBLE);
                }

                if (limitDataResponse.card == null) {
                    bankZanbuzhichi.setVisibility(View.VISIBLE);
                }
                if (limitDataResponse.usdt == null) {
                    usdtZanbuzhichi.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    @OnClick({R.id.zhifubao_ll, R.id.weixin_ll, R.id.bank_ll})
    public void onViewClicked(View view) {
        Intent inten = new Intent();
        switch (view.getId()) {
            case R.id.zhifubao_ll:
                if (limitDataResponse != null && limitDataResponse.alipay == null) return;
                inten.putExtra("pay_type", ALIPAY_TYPE);
                setResult(1, inten);
                finish();
                break;
            case R.id.weixin_ll:
                if (limitDataResponse != null && limitDataResponse.wechat == null) return;

                inten.putExtra("pay_type", WECHAT_TYPE);
                setResult(1, inten);
                finish();
                break;
            case R.id.bank_ll:
                if (limitDataResponse != null && limitDataResponse.card == null) return;

                inten.putExtra("pay_type", BANK_TYPE);
                setResult(1, inten);
                finish();
                break;
        }
    }
}
