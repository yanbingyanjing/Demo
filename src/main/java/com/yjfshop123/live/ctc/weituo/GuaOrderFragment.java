package com.yjfshop123.live.ctc.weituo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcPrceResponse;
import com.yjfshop123.live.model.PaySettingResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.otc.ui.OrderRecieveTypeSelectActivity;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;

import org.json.JSONException;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class GuaOrderFragment extends BaseFragment {
    @BindView(R.id.can_use)
    TextView can_use;
    @BindView(R.id.price_input)
    EditText priceInput;
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
    @BindView(R.id.fee)
    TextView fee;
    @BindView(R.id.can_sell_amount)
    TextView canSellAmount;
    @BindView(R.id.fee_amount)
    TextView feeAmount;
    @BindView(R.id.pay_logo)
    ImageView payLogo;
    @BindView(R.id.pay_des)
    TextView payDes;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.go_select)
    ImageView goSelect;
    @BindView(R.id.pay_type)
    LinearLayout payType;
    @BindView(R.id.confir_btn)
    TextView confirBtn;
    Unbinder unbinder;
    @BindView(R.id.unit)
    TextView unit;
    @BindView(R.id.current_egg_price)
    TextView current_egg_price;

    @BindView(R.id.daoshou)
    TextView daoshou;
    private String select_id;
    private String selectPayType;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_ctc_guadan;
    }


    @Override
    protected void initEvent() {

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

    private String discountStr;
    private String pricediscountStr;
    String egg_amount = "0";
    String jineAll = "0";

    protected void initAction() {
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

                String price = priceInput.getText().toString();
                String amount = mountInput.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    amount = "0";
                }
                if (amount.startsWith(".")) {
                    mountInput.setText("0");

                    return;
                }
                if (TextUtils.isEmpty(price)) {
                    price = "0";
                }
                if (price.startsWith(".")) {
                    priceInput.setText("0");
                    return;
                }
                if (buyType == 1) {
                    //按照数量
                    egg_amount = amount;
                    if (new BigDecimal(canUseEggCount).compareTo(new BigDecimal(egg_amount)) < 0) {
                        egg_amount = canUseEggCount;
                        mountInput.setText(egg_amount);
                        return;
                    }
                    jineAll = (new BigDecimal(price).multiply(new BigDecimal(egg_amount))).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                    valueDes.setText((!TextUtils.isEmpty(selectPayType) && selectPayType.equals(USDT_TYPE)) ? getString(R.string.total_usdt_value, jineAll) : getString(R.string.total_cny_value, jineAll));
                    feeAmount.setText(new BigDecimal(jineAll).multiply(new BigDecimal(data.fee_value)).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + ((!TextUtils.isEmpty(selectPayType) && selectPayType.equals(USDT_TYPE)) ? "USDT" : "元"));


                    BigDecimal feeValue = new BigDecimal(price).multiply(new BigDecimal(egg_amount)).multiply(new BigDecimal(data.fee_value + ""));
                    BigDecimal daozhang = new BigDecimal(price).multiply(new BigDecimal(egg_amount)).subtract(feeValue);
                    daoshou.setText(daozhang.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "元");

                } else {
                    jineAll = amount;
                    //按照金额
                    BigDecimal value = new BigDecimal(amount);
                    BigDecimal priceL = new BigDecimal(price);
                    BigDecimal reAmoount = BigDecimal.ZERO;
                    if (priceL.compareTo(BigDecimal.ZERO) > 0) {
                        reAmoount = value.divide(priceL, 4, BigDecimal.ROUND_DOWN);
                    }

                    if (new BigDecimal(canUseEggCount).compareTo(reAmoount) < 0) {
                        egg_amount = canUseEggCount;
                        mountInput.setText((priceL.multiply(new BigDecimal(canUseEggCount))).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                        return;
                    }
                    egg_amount = priceL.compareTo(BigDecimal.ZERO) > 0 ? value.divide(priceL, 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() : "0";
                    feeAmount.setText(new BigDecimal(egg_amount).multiply(new BigDecimal(data.fee_value)).setScale(4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "金蛋");

                    //if (priceL.compareTo(BigDecimal.ZERO) > 0)
                    valueDes.setText(getString(R.string.total_amount_value, egg_amount));
                }

            }
        });

        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pricediscountStr = s.toString();
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
                                if (s1.length() == 3) {
                                    priceInput.setText(pricediscountStr);
                                    try {
                                        String trim1 = priceInput.getText().toString().trim();
                                        priceInput.setSelection(trim1.length());
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
                String amount = mountInput.getText().toString();
                if (TextUtils.isEmpty(amount))  {
                    mountInput.setText("0");
                    amount = "0";
                }
                if (amount.startsWith("."))  {
                    mountInput.setText("0");
                    return;
                }
                String price = priceInput.getText().toString();
                if (TextUtils.isEmpty(price)) {
                    price = "0";
                }
                if (price.startsWith("."))  {
                    priceInput.setText("0");
                    return;
                }
                if (buyType == 1) {
                    //按照数量
                    jineAll = (new BigDecimal(amount).multiply(new BigDecimal(price))).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();

                    feeAmount.setText(new BigDecimal(jineAll).multiply(new BigDecimal(data.fee_value)).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + ((!TextUtils.isEmpty(selectPayType) && selectPayType.equals(USDT_TYPE)) ? "USDT" : "元"));
                    valueDes.setText((!TextUtils.isEmpty(selectPayType) && selectPayType.equals(USDT_TYPE)) ? getString(R.string.total_usdt_value, jineAll) : getString(R.string.total_cny_value, jineAll));

                    BigDecimal feeValue = new BigDecimal(price).multiply(new BigDecimal(amount)).multiply(new BigDecimal(data.fee_value + ""));
                    BigDecimal daozhang = new BigDecimal(price).multiply(new BigDecimal(amount)).subtract(feeValue);
                    daoshou.setText(daozhang.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "元");

                } else {
                    //按照金额
                    BigDecimal priceL = new BigDecimal(price);
                    BigDecimal value = new BigDecimal(amount);
                    egg_amount = priceL.compareTo(BigDecimal.ZERO) > 0 ? value.divide(priceL, 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() : "0";
                    feeAmount.setText(new BigDecimal(egg_amount).multiply(new BigDecimal(data.fee_value)).setScale(4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "金蛋");

                    //if (priceL.compareTo(BigDecimal.ZERO) > 0)
                    valueDes.setText(getString(R.string.total_amount_value, egg_amount));
                }

            }
        });
        initSwipeRefresh();
        getUserData();

        getShoukuanSettingData();
    }


    protected void updateViews(boolean isRefresh) {
        getUserData();
        getShoukuanSettingData();
    }


    private int buyType = 1;//1按数量  2按金额

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateViews(true);
                }
            });

        }
    }


    /**
     * 获取数据
     */
    public void getUserData() {

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


    /**
     * 获取收款信息
     */
    public void getShoukuanSettingData() {
        OKHttpUtils.getInstance().getRequest("app/trade/cardList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(getContext(), errInfo);
            }

            @Override
            public void onSuccess(String result) {
                initShoukuan(result);
            }
        });

    }

    private void initShoukuan(String result) {

        if (TextUtils.isEmpty(result))
            return;
        PaySettingResponse limitDataResponse = new Gson().fromJson(result, PaySettingResponse.class);

        if (TextUtils.isEmpty(select_id) && limitDataResponse != null && limitDataResponse.list != null && limitDataResponse.list.size() > 0) {
            add.setVisibility(View.GONE);
            goSelect.setVisibility(View.VISIBLE);
            payDes.setText(R.string.please_shoukuan_fangshi);

            selectPayType = limitDataResponse.list.get(0).type;
            if (selectPayType.equals(USDT_TYPE)) {
                valueDes.setText(getString(R.string.total_usdt_value, "-"));
            }

            String card = "(" + limitDataResponse.list.get(0).card + ")";
            add.setVisibility(View.GONE);
            goSelect.setVisibility(View.VISIBLE);
            select_id = limitDataResponse.list.get(0).id;
            unit.setText("¥");

            if (selectPayType.equals(BANK_TYPE)) {
                payDes.setText(getString(R.string.yinhangka) + card);
                Glide.with(this).load(R.mipmap.bank).into(payLogo);

                return;
            }
            if (selectPayType.equals(WECHAT_TYPE)) {
                payDes.setText(getString(R.string.setting_wx) + card);
                Glide.with(this).load(R.mipmap.weixin).into(payLogo);

                return;
            }
            if (selectPayType.equals(ALIPAY_TYPE)) {
                payDes.setText(getString(R.string.tixian_ali) + card);
                Glide.with(this).load(R.mipmap.zhifubao).into(payLogo);
            }
            if (selectPayType.equals(USDT_TYPE)) {
                payDes.setText("USDT" + card);
                Glide.with(this).load(R.mipmap.usdt).into(payLogo);
                unit.setText("USDT");
            }

        }
    }

    OtcPrceResponse data;
    String canUseEggCount = "0";

    private void init(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result))
            return;
        data = new Gson().fromJson(result, OtcPrceResponse.class);
        can_use.setText(getString(R.string.can_use_egg, data.gold_egg));

        current_egg_price.setText(getString(R.string.current_egg_price, NumUtil.clearZero(data.gold_price)));
        fee.setText(data.fee);
        // if (TextUtils.isEmpty(data.fee_value)) {
        canUseEggCount = data.gold_egg;
        // } else
        //   canUseEggCount = new BigDecimal(data.gold_egg).divide(new BigDecimal("1").add(new BigDecimal(data.fee_value)), 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        canSellAmount.setText(canUseEggCount + "金蛋");
        //feeAmount.setText(new BigDecimal(data.gold_egg).subtract(new BigDecimal(canUseEggCount)).stripTrailingZeros().toPlainString()+"金蛋");

    }


    @OnClick({R.id.max, R.id.qiehuan, R.id.pay_type, R.id.confir_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.max:
                if (buyType == 1) {
                    //按照数量
                    mountInput.setText(data.gold_egg);
                } else {
                    BigDecimal egg = new BigDecimal(canUseEggCount);
                    BigDecimal price = new BigDecimal(TextUtils.isEmpty(priceInput.getText().toString()) ? "0" : priceInput.getText().toString());
                    mountInput.setText(egg.multiply(price).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                }
                break;
            case R.id.qiehuan:
                jineAll = "0";
                egg_amount = "0";
                if (buyType == 1) {
                    buyType = 2;
                    qiehuanDes.setText(getString(R.string.an_amount_bsell));
                    mountInput.setHint(getString(R.string.input_sell_value));
                    if (!TextUtils.isEmpty(mountInput.getText().toString())) {
                        //不为空
                        mountInput.setText("");
                    }

                    valueDes.setText(getString(R.string.total_amount_value, "-"));
                } else if (buyType == 2) {
                    buyType = 1;
                    qiehuanDes.setText(getString(R.string.an_value_bsell));
                    mountInput.setHint(getString(R.string.input_sell_amount));
                    if (!TextUtils.isEmpty(mountInput.getText().toString())) {
                        //不为空
                        mountInput.setText("");
                    }
                    if (!TextUtils.isEmpty(selectPayType) && selectPayType.equals(USDT_TYPE)) {
                        valueDes.setText(getString(R.string.total_usdt_value, "-"));
                    } else
                        valueDes.setText(getString(R.string.total_cny_value, "-"));
                }
                break;
            case R.id.pay_type:
                Intent intent1 = new Intent(getContext(), OrderRecieveTypeSelectActivity.class);
                intent1.putExtra("select_id", select_id);
                startActivityForResult(intent1, 1);
                break;
            case R.id.confir_btn:
                if (TextUtils.isEmpty(mountInput.getText().toString())) {
                    if (buyType == 2) {
                        NToast.shortToast(getContext(), getString(R.string.input_sell_value));
                    }
                    if (buyType == 1) {
                        NToast.shortToast(getContext(), getString(R.string.input_sell_amount));
                    }
                    return;
                }
                if (TextUtils.isEmpty(select_id)) {
                    NToast.shortToast(getContext(), getString(R.string.please_shoukuan_fangshi));
                    return;
                }
                if (new BigDecimal(egg_amount).compareTo(new BigDecimal(data.gold_egg)) > 0) {
                    NToast.shortToast(getContext(), "没有足够的可用余额");
                    return;
                }
                confirPay();
                break;
        }
    }

    public void confirPay() {
        String body = "";

        try {
            body = new JsonBuilder()
                    .put("eggs", egg_amount)
                    .put("bank", select_id)
                    .put("minNum", "0")
                    .put("maxNum", egg_amount)
                    .put("price", priceInput.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(getContext());
        OKHttpUtils.getInstance().getRequest("app/trade/sellEgg", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getContext());
                NToast.shortToast(getContext(), errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
//                OtcTipsDialog dialogFragment = new OtcTipsDialog();
//                dialogFragment.setTitle("委托成功，等待审核");
//                dialogFragment.setOnClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//                dialogFragment.show(getSupportFragmentManager(), "OtcTipsDialog");
                NToast.shortToast(getContext(), "委托成功，等待审核");
                Intent intent = new Intent(getContext(), GuaDanManagerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                if (!TextUtils.isEmpty(selectPayType)) {
                    if (selectPayType.equals(USDT_TYPE)) {
                        if (!selectPayType.equals(data.getStringExtra("pay_type"))) {
                            selectPayType = data.getStringExtra("pay_type");
                            priceInput.setText("");
                            mountInput.setText("");
                        }
                    }
                    if (!selectPayType.equals(USDT_TYPE)) {
                        if (data.getStringExtra("pay_type").equals(USDT_TYPE)) {
                            selectPayType = data.getStringExtra("pay_type");
                            priceInput.setText("");
                            mountInput.setText("");
                        }else {
                            selectPayType = data.getStringExtra("pay_type");
                        }
                    }
                } else {
                    selectPayType = data.getStringExtra("pay_type");
                    if (selectPayType.equals(USDT_TYPE)) {
                        valueDes.setText(getString(R.string.total_usdt_value, "-"));
                    }
                }
                String card = "(" + data.getStringExtra("card") + ")";
                add.setVisibility(View.GONE);
                goSelect.setVisibility(View.VISIBLE);
                select_id = data.getStringExtra("select_id");
                unit.setText("¥");

                if (selectPayType.equals(BANK_TYPE)) {
                    payDes.setText(getString(R.string.yinhangka) + card);
                    Glide.with(this).load(R.mipmap.bank).into(payLogo);

                    return;
                }
                if (selectPayType.equals(WECHAT_TYPE)) {
                    payDes.setText(getString(R.string.setting_wx) + card);
                    Glide.with(this).load(R.mipmap.weixin).into(payLogo);

                    return;
                }
                if (selectPayType.equals(ALIPAY_TYPE)) {
                    payDes.setText(getString(R.string.tixian_ali) + card);
                    Glide.with(this).load(R.mipmap.zhifubao).into(payLogo);
                }
                if (selectPayType.equals(USDT_TYPE)) {
                    payDes.setText("USDT" + card);
                    Glide.with(this).load(R.mipmap.usdt).into(payLogo);
                    unit.setText("USDT");
                }

            }
        }
    }

}
