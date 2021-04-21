package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MyWalletResponse;
import com.yjfshop123.live.net.response.PromotionWalletResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TiXianActivity1 extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.tvCoin)
    TextView tvCoin;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.etAccount)
    EditText etAccount;
    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.tvWithdrawal)
    TextView tvWithdrawal;
    @BindView(R.id.tvWithdrawalInstructionsMoney)
    TextView tvWithdrawalInstructionsMoney;

    @BindView(R.id.tx_jl_tv)
    TextView txJLTV;

    private int mCoin = 0;
    private int inputMoney = 0;
    private String mAccount;

    private int type;//提现类型 1：可提现余额 2：邀请奖励

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tixian1);
        ButterKnife.bind(this);
        setHeadLayout();

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.tixian_shenqing);

        tvWithdrawal.setOnClickListener(this);
        etInput.addTextChangedListener(watcher);

        txJLTV.setOnClickListener(this);
        txJLTV.setVisibility(View.VISIBLE);

        type = getIntent().getIntExtra("type", 1);
        if (type == 1){
            getMyWallet();
        }else {
            getMyPromotionWallet();
        }

        if (type == 3){
            txJLTV.setVisibility(View.GONE);
        }
    }

    private void getMyWallet(){
        OKHttpUtils.getInstance().getRequest("app/user/myWallet", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    MyWalletResponse response = JsonMananger.jsonToBean(result, MyWalletResponse.class);
                    mCoin = response.getWithdraw_coin();
                    tvCoin.setText(String.valueOf(mCoin));
                    tvMoney.setText(getString(R.string.tx_3) + response.getWithdraw_money() + getString(R.string.rmb));
                    tvWithdrawalInstructionsMoney.setText(response.getProportion_desc().replace("\\n", "\n"));
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getWithdraw(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("coin", inputMoney)
                    .put("withdraw_account", mAccount)
                    .put("type", type)//1：余额 2：奖励
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/withdraw", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                DialogUitl.showSimpleHintDialog(mContext,
                        getString(R.string.prompt),
                        getString(R.string.callkit_voip_ok),
                        getString(R.string.other_cancel),
                        "提现成功",
                        true,
                        true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

    //我的推营销广钱包
    private void getMyPromotionWallet(){
        String url;
        if (type == 2){
            url = "app/promotion/myWallet";
        }else {
            url = "app/guild/guildWallet";
        }
        OKHttpUtils.getInstance().getRequest(url, "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    PromotionWalletResponse response = JsonMananger.jsonToBean(result, PromotionWalletResponse.class);
                    mCoin = response.getBonus_coin();
                    tvCoin.setText(String.valueOf(mCoin));
                    tvMoney.setText(getString(R.string.tx_3) + response.getBonus_money() + getString(R.string.rmb));
                    tvWithdrawalInstructionsMoney.setText(response.getProportion_desc().replace("\\n", "\n"));
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!editable.toString().equals("")) {
                try {
                    inputMoney = Integer.parseInt(editable.toString());
                    if (inputMoney > mCoin) {
                        inputMoney = mCoin;
                        etInput.setText(String.valueOf(inputMoney));
//                        NToast.shortToast(TiXianActivity1.this, getString(R.string.tixian_price));
                    }
                } catch (Exception ex) {
                    inputMoney = 0;
//                    NToast.shortToast(TiXianActivity1.this, getString(R.string.tixian_nums));
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvWithdrawal:
                mAccount = etAccount.getText().toString();
                if (inputMoney == 0 || TextUtils.isEmpty(mAccount)) {
                    NToast.shortToast(this, getString(R.string.tixian_tips));
                } else {
                    getWithdraw();
                }
                break;
            case R.id.tx_jl_tv:
                Intent intent = new Intent(mContext, JiaoYiRecodeActivity.class);
                intent.putExtra("TYPE", "TX");
                startActivity(intent);
                break;
        }
    }
}
