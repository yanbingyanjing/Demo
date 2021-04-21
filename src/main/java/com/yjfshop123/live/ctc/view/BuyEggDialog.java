package com.yjfshop123.live.ctc.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcBuyOrderDetailActivity;
import com.yjfshop123.live.ctc.weituo.GuaOrderActivity;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.CtcListResopnse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.otc.view.OtcTipsDialog;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.math.BigDecimal;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;

public class BuyEggDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ctc_buy_egg;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    TextView price;
    TextView an_jine;
    TextView an_amount;
    EditText amount;
    TextView all_buy;
    TextView limit;
    TextView egg_num;
    TextView confir, unit;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unit = mRootView.findViewById(R.id.unit);
        price = mRootView.findViewById(R.id.price);
        an_jine = mRootView.findViewById(R.id.an_jine);
        an_amount = mRootView.findViewById(R.id.an_amount);
        amount = mRootView.findViewById(R.id.amount);
        all_buy = mRootView.findViewById(R.id.all_buy);
        limit = mRootView.findViewById(R.id.limit);
        egg_num = mRootView.findViewById(R.id.egg_num);
        confir = mRootView.findViewById(R.id.confir);


        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        initData();
    }

    private int buyType = 2;//1按数量  2按金额
    private String discountStr;
    String egg_amount = "0";

    private void initData() {
        if (data == null) return;
        if (!TextUtils.isEmpty(data.card_type) && data.card_type.equals(USDT_TYPE)) {
            unit.setText("USDT");
        } else unit.setText("¥");
        price.setText(data.price);
       // limit.setText(data.minNum + "个-" + data.maxNum + "个");
        limit.setText(data.amount);
        setAmountBuy();
        an_jine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyType == 2) return;
                buyType = 2;
                an_jine.setTextColor(getResources().getColor(R.color.color_BA965C));
                an_jine.setTextSize(16);
                amount.setHint(getString(R.string.input_buy_value));
                amount.setText("");

                an_amount.setTextColor(getResources().getColor(R.color.color_BA965C));
                an_amount.setTextSize(14);
            }
        });
        an_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmountBuy();
            }
        });
        all_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyType == 1) {
                    //按数量
                    amount.setText(data.amount);
                } else {
                    BigDecimal egg = new BigDecimal(data.amount);
                    BigDecimal price = new BigDecimal(data.price);
                    amount.setText(egg.multiply(price).setScale(4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                }
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
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
                                    amount.setText(discountStr);
                                    try {
                                        String trim1 = amount.getText().toString().trim();
                                        amount.setSelection(trim1.length());
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
                if (data == null || TextUtils.isEmpty(data.price)) return;
                String amountV = amount.getText().toString();
                if (TextUtils.isEmpty(amountV)) {
                    amountV = "0";
                }
                if (buyType == 1) {
                    //按照数量
                    egg_amount = amountV;
                    egg_num.setText(amountV);
                } else {
                    //按照金额
                    BigDecimal value = new BigDecimal(amountV);
                    BigDecimal priceL = new BigDecimal(data.price);
                    egg_amount = priceL.compareTo(BigDecimal.ZERO) > 0 ? value.divide(priceL, 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() : "0";
                    if (priceL.compareTo(BigDecimal.ZERO) > 0)
                        egg_num.setText(egg_amount);
                }

            }
        });

    }

    private void setAmountBuy() {
        if (buyType == 1) return;
        buyType = 1;
        an_amount.setTextColor(getResources().getColor(R.color.color_BA965C));
        an_amount.setTextSize(16);
        amount.setHint(getString(R.string.input_buy_amount));
        amount.setText("");
        an_jine.setTextColor(getResources().getColor(R.color.color_BA965C));
        an_jine.setTextSize(14);
    }

    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }

    public void setData(CtcListResopnse.CtcListData data) {
        this.data = data;
    }

    CtcListResopnse.CtcListData data;

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.confir) {
            if (TextUtils.isEmpty(amount.getText().toString())) {
                if (buyType == 2) {
                    NToast.shortToast(getContext(), getString(R.string.input_sell_value));
                }
                if (buyType == 1) {
                    NToast.shortToast(getContext(), getString(R.string.input_sell_amount));
                }


            }
            confirPay();
            return;

        }
    }

    public void confirPay() {
        String body = "";

        try {
            body = new JsonBuilder()
                    .put("eggs", egg_amount)
                    .put("order", data.order)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(getContext());
        OKHttpUtils.getInstance().getRequest("app/trade/buyEgg", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getContext());
                NToast.shortToast(mRootView.getContext(), errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
                NToast.shortToast(mRootView.getContext(), "下单成功");
                if (TextUtils.isEmpty(result)) return;
                ResultData resultData = new Gson().fromJson(result, ResultData.class);
                Intent intent = new Intent(mRootView.getContext(), CtcBuyOrderDetailActivity.class);
                intent.putExtra("order", resultData.order);
                mRootView.getContext().startActivity(intent);
            }
        });

    }

    public class ResultData {
        public String order;
    }

}
