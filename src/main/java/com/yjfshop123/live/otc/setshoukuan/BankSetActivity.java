package com.yjfshop123.live.otc.setshoukuan;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.PaySettingResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;

public class BankSetActivity extends BaseActivityH {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.bank)
    EditText bank;
    @BindView(R.id.sun_bank)
    EditText sunBank;
    @BindView(R.id.card)
    EditText card;
    PaySettingResponse.PayData data;
    String id,select_id;
    boolean is_edit = false;
    @BindView(R.id.delete)
    TextView delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_set);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        if (getIntent() != null) {
            select_id = getIntent().getStringExtra("select_id");
            id = getIntent().getStringExtra("id");
            is_edit = getIntent().getBooleanExtra("is_edite", false);
            if (is_edit) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("data"))) {
                data = new Gson().fromJson(getIntent().getStringExtra("data"), PaySettingResponse.PayData.class);
                name.setText(data.name);
                bank.setText(data.bank);
                sunBank.setText(data.sub_bank);
                card.setText(data.card);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(select_id) && !TextUtils.isEmpty(id) && select_id.equals(id)) {
                        NToast.longToast(BankSetActivity.this,"默认收款方式，请勿删除，如需删除，请重新选择默认收款方式后再进行此操作！");
                        return;
                    }
                    DialogUitl.showSimpleHintDialog(BankSetActivity.this, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                            "确认删除收款方式？", true, true,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {
                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    delete();
                                }
                            });
                }
            });
        }
    }

    public void delete() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("bank_id", id)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/delCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, "删除成功");
                finish();
            }
        });

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
        if (TextUtils.isEmpty(name.getText().toString())) {
            NToast.shortToast(this, getString(R.string.please_input_name));
            return;
        }
        if (TextUtils.isEmpty(bank.getText().toString())) {
            NToast.shortToast(this, getString(R.string.please_input_kaihuhang));
            return;
        }
        if (TextUtils.isEmpty(sunBank.getText().toString())) {
            NToast.shortToast(this, getString(R.string.please_input_zhihang));
            return;
        }
        if (TextUtils.isEmpty(card.getText().toString())) {
            NToast.shortToast(this, getString(R.string.please_input_bank_card));
            return;
        }
        confiry();

    }

    public void fix() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("bank_id", id)
                    .put("name", name.getText().toString())
                    .put("card", card.getText().toString())
                    .put("type", BANK_TYPE)
                    .put("bank", bank.getText().toString())
                    .put("sub_bank", sunBank.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/editCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, errInfo);

            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, "修改成功");
                finish();
            }
        });

    }

    public void confiry() {
        if (is_edit) {
            fix();
            return;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("name", name.getText().toString())
                    .put("card", card.getText().toString())
                    .put("type", BANK_TYPE)
                    .put("bank", bank.getText().toString())
                    .put("sub_bank", sunBank.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/addCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(BankSetActivity.this);
                NToast.shortToast(BankSetActivity.this, "添加成功");
                finish();
            }
        });

    }
}
