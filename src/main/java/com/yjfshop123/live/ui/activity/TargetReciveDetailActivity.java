package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetReciveDetailActivity extends BaseActivityForNewUi {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.submit)
    TextView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.receive_reward_detail));
        setContentView(R.layout.activity_target_recive_detail);
        setTooBarBack(R.color.color_F8F8F8);
        setBlackColorTooBar();
        ButterKnife.bind(this);


    }

    private void submit() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mobile", phone.getText().toString())
                    .put("name", name.getText().toString())
                    .put("address", address.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/prize/address", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(TargetReciveDetailActivity.this, errInfo);
                LoadDialog.dismiss(TargetReciveDetailActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(TargetReciveDetailActivity.this);
                NToast.shortToast(TargetReciveDetailActivity.this, getString(R.string.tips_lianxini));
                finish();
            }
        });
    }

    @OnClick({R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if(TextUtils.isEmpty(name.getText().toString())){
                    NToast.shortToast(this,getString(R.string.please_input_your_name));
                    return;
                }
                if(TextUtils.isEmpty(phone.getText().toString())){
                    NToast.shortToast(this,getString(R.string.hint_input_contact_phone));
                    return;
                }
                if(TextUtils.isEmpty(address.getText().toString())){
                    NToast.shortToast(this,getString(R.string.please_select_your_city));
                    return;
                }
                submit();
                break;
        }
    }
}
