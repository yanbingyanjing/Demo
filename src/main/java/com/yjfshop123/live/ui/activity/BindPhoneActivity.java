package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.MyCountDownTimer;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.text_right)
    TextView text_right;
    @BindView(R.id.phoneTxt)
    EditText phoneTxt;
    @BindView(R.id.codeTxt)
    EditText codeTxt;
    @BindView(R.id.getCode)
    Button getCode;

    private String phoneString;
    private MyCountDownTimer myCountDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindphone);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.bind_phone_title);
        text_right.setVisibility(View.VISIBLE);
        text_right.setText(R.string.bind_save);

        getCode.setOnClickListener(this);
        text_right.setOnClickListener(this);
        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = new MyCountDownTimer(60000, 1000,getCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getCode:
                phoneString = phoneTxt.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    return;
                }

                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("region", "86")
                            .put("mobile", phoneString)
                            .put("type", 1)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/sms/sendCode", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                    }
                    @Override
                    public void onSuccess(String result) {
                        myCountDownTimer.start();
                    }
                });
                break;
            case R.id.text_right:
                if (TextUtils.isEmpty(phoneTxt.getText().toString()) || TextUtils.isEmpty(codeTxt.getText().toString())) {
                    NToast.shortToast(mContext, R.string.bind_err_msg);
                    return;
                }
                String body_ = "";
                try {
                    body_ = new JsonBuilder()
                            .put("region", "86")
                            .put("mobile", phoneTxt.getText().toString())
                            .put("code", codeTxt.getText().toString())
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/user/bindingMobile", body_, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                    }
                    @Override
                    public void onSuccess(String result) {
                        NToast.shortToast(mContext, R.string.bind_success);
                    }
                });
                break;

        }
    }
}
