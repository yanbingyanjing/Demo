package com.yjfshop123.live.otc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcBuySellLimitResponse;
import com.yjfshop123.live.model.OtcPrceResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.fragment.BaseFragment;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class BuySellFragment extends BaseFragment {
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.mount_input)
    EditText mountInput;
    @BindView(R.id.max)
    TextView max;
    @BindView(R.id.value_des)
    TextView valueDes;
    @BindView(R.id.qiehuan_des)
    TextView qiehuanDes;
    @BindView(R.id.qiehuan)
    LinearLayout qiehuan;
    @BindView(R.id.pay_logo)
    ImageView payLogo;
    @BindView(R.id.pay_des)
    TextView payDes;
    @BindView(R.id.pay_type)
    LinearLayout payType;
    OtcPrceResponse data;
    Unbinder unbinder;
    @BindView(R.id.limit)
    TextView limit;
    @BindView(R.id.confir_btn)
    TextView confirBtn;
    private String selectPayType;
    OtcBuySellLimitResponse.LimitData limitData;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_buy_sell;
    }

    String egg_amount = "0";
    String jineAll = "0";
    private String discountStr;

    @Override
    protected void initAction() {
        price.setText(getString(R.string.cny_price, "-"));
        valueDes.setText(getString(R.string.total_cny_value, "-"));
        mountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                discountStr = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trim = s.toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    if (trim.contains(".")) {
                        String[] split = trim.split("\\.");
                        if (split.length > 1) {
                            String s1 = split[1];
                            if (!TextUtils.isEmpty(s1)) {
                                if (s1.length() == (buyType == 1 ? 5 : 3)) {
                                    mountInput.setText(discountStr);
                                    try {
                                        String trim1 = mountInput.getText().toString().trim();
                                        mountInput.setSelection(trim1.length());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                            }
                        }
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (data == null || TextUtils.isEmpty(data.gold_price)) return;
                String amount = mountInput.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    amount = "0";
                }
                if (amount.startsWith(".")) {
                    mountInput.setText("0");
                    return;
                }
                if (buyType == 1) {
                    //按照数量
                    egg_amount = amount;
                    jineAll = (new BigDecimal(data.gold_price).multiply(new BigDecimal(amount))).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                    valueDes.setText(getString(R.string.total_cny_value, jineAll));
                } else {
                    jineAll = amount;
                    //按照金额
                    BigDecimal value = new BigDecimal(amount);
                    BigDecimal price = new BigDecimal(data.gold_price);
                    egg_amount = value.divide(price, 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                    if (price.compareTo(BigDecimal.ZERO) > 0)
                        valueDes.setText(getString(R.string.total_amount_value, egg_amount));
                }

            }
        });
        getData();
        getLimiteData();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData();
        getLimiteData();
    }


    private int buyType = 1;//1按数量  2按金额


    /**
     * 获取数据
     */
    public void getData() {

        OKHttpUtils.getInstance().getRequest("app/trade/userEggs", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                NToast.shortToast(getContext(), errInfo);
            }

            @Override
            public void onSuccess(String result) {
                init(result);
            }
        });

    }

    String testData = "{\"alipay\":{\"buyMinNumber\":\"333.04\",\"buyMaxNumber\":\"66608.82\",\"sellMinNumber\":\"317.95\",\"sellMaxNumber\"" +
            ":\"63591.17\"},\"wechat\":{\"buyMinNumber\":\"337.54\",\"buyMaxNumber\":" +
            "\"67508.82\",\"sellMinNumber\":\"313.45\",\"sellMaxNumber\":\"62691.17\"}}";

    /**
     * 获取购买限制数据
     */
    public void getLimiteData() {
        OKHttpUtils.getInstance().getRequest("app/trade/moneyLimit", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(getContext(), errInfo);
            }

            @Override
            public void onSuccess(String result) {
                initLimit(result);

            }
        });

    }

    private void init(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result))
            return;
        data = new Gson().fromJson(result, OtcPrceResponse.class);
        price.setText(getString(R.string.cny_price, data.gold_price));
    }

    OtcBuySellLimitResponse limitDataResponse;

    private void initLimit(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result))
            return;
        limitDataResponse = new Gson().fromJson(result, OtcBuySellLimitResponse.class);
        if (limitDataResponse.card != null) {
            selectPayType = BANK_TYPE;
            limitData = limitDataResponse.card;
            setB();
            return;
        }
        if (limitDataResponse.alipay != null) {
            selectPayType = ALIPAY_TYPE;
            limitData = limitDataResponse.alipay;
            setB();
            return;
        }
        if (limitDataResponse.wechat != null) {
            selectPayType = WECHAT_TYPE;
            limitData = limitDataResponse.wechat;
            setB();
            return;
        }
        payDes.setText("暂无可选的付款方式");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.max, R.id.qiehuan, R.id.pay_type, R.id.confir_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.max:
                if (limitData == null) return;
                if (TextUtils.isEmpty(limitData.buyMaxNumber)) return;
                if (data == null) return;
                if (TextUtils.isEmpty(data.gold_price)) return;
                if (buyType == 1) {
                    //按照数量
                    BigDecimal max = new BigDecimal(limitData.buyMaxNumber);
                    BigDecimal price = new BigDecimal(data.gold_price);
                    mountInput.setText(max.divide(price, 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                } else {
                    mountInput.setText(limitData.buyMaxNumber);
                }
                break;
            case R.id.qiehuan:
                jineAll = "0";
                egg_amount = "0";
                if (buyType == 1) {
                    buyType = 2;
                    qiehuanDes.setText(getString(R.string.an_amount_bbuy));
                    mountInput.setHint(getString(R.string.input_buy_value));
                    if (!TextUtils.isEmpty(mountInput.getText().toString())) {
                        //不为空
                        mountInput.setText("");
                    }

                    valueDes.setText(getString(R.string.total_amount_value, "-"));
                } else if (buyType == 2) {
                    buyType = 1;
                    qiehuanDes.setText(getString(R.string.an_value_bbuy));
                    mountInput.setHint(getString(R.string.input_buy_amount));
                    if (!TextUtils.isEmpty(mountInput.getText().toString())) {
                        //不为空
                        mountInput.setText("");
                    }
                    valueDes.setText(getString(R.string.total_cny_value, "-"));
                }
                break;
            case R.id.pay_type:
                if (limitDataResponse == null) {
                    NToast.shortToast(getContext(), "为获取到支持的付款方式，请拉下刷新重新获取");
                    return;
                }
                Intent intent1 = new Intent(getActivity(), OrderPayTypeSelectActivity.class);
                intent1.putExtra("pay_type", selectPayType);
                intent1.putExtra("limit", new Gson().toJson(limitDataResponse));
                startActivityForResult(intent1, 1);
                break;
            case R.id.confir_btn:

                if (TextUtils.isEmpty(mountInput.getText().toString())) {
                    if (buyType == 2) {
                        NToast.shortToast(getContext(), getString(R.string.input_buy_value));
                    }
                    if (buyType == 1) {
                        NToast.shortToast(getContext(), getString(R.string.input_buy_amount));
                    }
                    return;
                }
                if (TextUtils.isEmpty(selectPayType)) {
                    NToast.shortToast(getContext(), getString(R.string.please_fukuan_fangshi));
                    return;
                }
                try {
                    if (limitData != null) {
                        if (!TextUtils.isEmpty(limitData.buyMinNumber) && new BigDecimal(jineAll).compareTo(new BigDecimal(limitData.buyMinNumber)) < 0) {
                            //小于最小购买限制
                            NToast.shortToast(getContext(), "单笔购买总金额（" + limitData.buyMinNumber + " - " + limitData.buyMaxNumber + "）");
                            return;
                        }
                        if (!TextUtils.isEmpty(limitData.buyMinNumber) && new BigDecimal(jineAll).compareTo(new BigDecimal(limitData.buyMaxNumber)) > 0) {
                            //大于最大购买限制
                            NToast.shortToast(getContext(), "单笔购买总金额（" + limitData.buyMinNumber + " - " + limitData.buyMaxNumber + "）");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), OrderConfirActivity.class);
                intent.putExtra("amount", egg_amount);
                intent.putExtra("price", data.gold_price);
                intent.putExtra("jineCny", jineAll);
                intent.putExtra("payType", selectPayType);
                startActivity(intent);
                break;
        }
    }

    public void setB() {

        if (selectPayType.equals(BANK_TYPE)) {
            payLogo.setVisibility(View.VISIBLE);
            payDes.setText(getString(R.string.yinhangka));
            Glide.with(getContext()).load(R.mipmap.bank).into(payLogo);
            if (limitDataResponse != null) {
                limitData = limitDataResponse.card;
            }
            limit.setText("限额："+limitData.buyMinNumber+"-"+limitData.buyMaxNumber+"元");
            return;
        }
        if (selectPayType.equals(WECHAT_TYPE)) {
            payLogo.setVisibility(View.VISIBLE);
            payDes.setText(getString(R.string.setting_wx));
            Glide.with(getContext()).load(R.mipmap.weixin).into(payLogo);
            if (limitDataResponse != null) {
                limitData = limitDataResponse.wechat;
            }
            limit.setText("限额："+limitData.buyMinNumber+"-"+limitData.buyMaxNumber+"元");
            return;
        }
        if (selectPayType.equals(ALIPAY_TYPE)) {
            payLogo.setVisibility(View.VISIBLE);
            payDes.setText(getString(R.string.tixian_ali));
            Glide.with(getContext()).load(R.mipmap.zhifubao).into(payLogo);
            if (limitDataResponse != null) {
                limitData = limitDataResponse.alipay;
            }
            limit.setText("限额："+limitData.buyMinNumber+"-"+limitData.buyMaxNumber+"元");
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                selectPayType = data.getStringExtra("pay_type");
                setB();
            }
        }
    }
}
