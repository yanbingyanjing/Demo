package com.yjfshop123.live.otc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcOrderResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;
//一键买币的确认界面
public class OrderConfirActivity extends BaseActivityH {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.layout_container)
    FrameLayout layoutContainer;
    @BindView(R.id.jine)
    TextView jine;
    @BindView(R.id.pay_type)
    TextView payType;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.confir_btn)
    TextView confirBtn;
    private String amountGold = "0";
    private String jineCny = "0";
    private String priceCny = "0";
    private String payTypeSelect = BANK_TYPE;
    private String select_id = BANK_TYPE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confir);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        if (getIntent() != null) {
            amountGold = getIntent().getStringExtra("amount");
            jineCny = getIntent().getStringExtra("jineCny");
            priceCny = getIntent().getStringExtra("price");
            payTypeSelect = getIntent().getStringExtra("payType");
            select_id= getIntent().getStringExtra("select_id");
        }
        jine.setText(jineCny + " CNY");
        price.setText(priceCny + "CNY");
        amount.setText(amountGold + getString(R.string.gold_egg));
        if (!TextUtils.isEmpty(payTypeSelect)) {
            if (payTypeSelect.equals(BANK_TYPE)) {
                payType.setText(R.string.yinhangka);
            }
            if (payTypeSelect.equals(WECHAT_TYPE)) {
                payType.setText(R.string.setting_wx);
            }
            if (payTypeSelect.equals(ALIPAY_TYPE)) {
                payType.setText(R.string.tixian_ali);
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
    @OnClick(R.id.confir_btn)
    public void onViewClicked() {
        if (TextUtils.isEmpty(amountGold) || TextUtils.isEmpty(jineCny)) {
            NToast.shortToast(this, "下单信息缺失，请重新回到下单页面下单！");
            return;
        }
      // cancelbuy();
        buy();
    }


    public void buy() {

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("pay_type", payTypeSelect)
                    .put("eggs", amountGold)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/pay", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(OrderConfirActivity.this);
                NToast.shortToast(OrderConfirActivity.this,errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(OrderConfirActivity.this);
                if(TextUtils.isEmpty(result)){
                    finish();
                    return;
                }
                OtcOrderResponse   otcOrderResponse = new Gson().fromJson(result, OtcOrderResponse.class);
                Intent intent=new Intent(OrderConfirActivity.this,OrderDetailActivity.class);
                intent.putExtra("order",otcOrderResponse.order);
                startActivity(intent);
                finish();
            }
        });

    }
}
