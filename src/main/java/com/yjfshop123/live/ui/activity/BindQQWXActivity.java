package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.response.LoginResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.widget.switchbutton.SwitchButton;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BindQQWXActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.text_right)
    TextView text_right;

    @BindView(R.id.bind_type)
    TextView bind_type;
    @BindView(R.id.phoneTxt)
    EditText phoneTxt;

    @BindView(R.id.location_rl)
    RelativeLayout locationRl;
    @BindView(R.id.wq_rl)
    RelativeLayout wqRl;
    @BindView(R.id.location_switch)
    SwitchButton locationSwitch;

    private String str = "";
    private int open_position = 0;
    private LoginResponse mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_qq_wx);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private String type = "";

    private void initView() {
        type = getIntent().getStringExtra("loginType");
        mResponse = (LoginResponse) getIntent().getSerializableExtra("USER_INFO");

        if (type.equals("qq")) {
            tv_title_center.setVisibility(View.VISIBLE);
            tv_title_center.setText(R.string.bind_qq_title);
            text_right.setVisibility(View.VISIBLE);
            text_right.setText(R.string.bind_save);
            text_right.setOnClickListener(this);

            wqRl.setVisibility(View.VISIBLE);
            bind_type.setText(getString(R.string.bind_qq));
            phoneTxt.setInputType(InputType.TYPE_CLASS_PHONE);
            phoneTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            phoneTxt.setHint(getString(R.string.bind_input_qq));

            if (mResponse.getMobile() != null) {
                phoneTxt.setText(mResponse.getQq());
                phoneTxt.setSelection(mResponse.getQq().length());
            }
        } else if (type.equals("weixin")) {
            tv_title_center.setVisibility(View.VISIBLE);
            tv_title_center.setText(R.string.bind_weixin_title);
            text_right.setVisibility(View.VISIBLE);
            text_right.setText(R.string.bind_save);
            text_right.setOnClickListener(this);

            wqRl.setVisibility(View.VISIBLE);
            bind_type.setText(getString(R.string.bind_wx));
            phoneTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
            phoneTxt.setInputType(InputType.TYPE_CLASS_TEXT);
            phoneTxt.setHint(getString(R.string.bind_input_wx));

            phoneTxt.addTextChangedListener(new TextWatcher() {
                String tmp = "";
                String digits = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@.";
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tmp = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    if(str.equals(tmp)){
                        return;
                    }

                    StringBuffer sb = new StringBuffer();
                    for(int i = 0; i < str.length(); i++){
                        if(digits.indexOf(str.charAt(i)) >= 0){
                            sb.append(str.charAt(i));
                        }
                    }
                    tmp = sb.toString();
                    phoneTxt.setText(tmp);
                    phoneTxt.setSelection(tmp.length());
                }
            });

            if (mResponse.getWeixin() != null) {
                phoneTxt.setText(mResponse.getWeixin());
            }
        }else if (type.equals("location")){
            tv_title_center.setVisibility(View.VISIBLE);
            tv_title_center.setText(getString(R.string.setting_1));
            text_right.setVisibility(View.GONE);

            locationRl.setVisibility(View.VISIBLE);
            if (mResponse.getOpen_position() == 0) {
                locationSwitch.setChecked(false);
            } else {
                locationSwitch.setChecked(true);
            }
            locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    if (isCheck){
                        open_position = 1;
                    }else {
                        open_position = 0;
                    }
                    positionSetting();
                }
            });
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_right:
                if (TextUtils.isEmpty(phoneTxt.getText().toString().trim())) {
                    if (type.equals("qq")) {
                        NToast.shortToast(mContext, "请先输入QQ号");
                    }else {
                        NToast.shortToast(mContext, "请先输入微信号");
                    }
                    return;
                }

                if (type.equals("qq")) {
                    str = "{\"qq\":\"" + phoneTxt.getText().toString() + "\"}";
                } else if (type.equals("weixin")) {
                    str = "{\"weixin\":\"" + phoneTxt.getText().toString() + "\"}";
                }
                saveUserInfoByField();
                break;
        }
    }

    private void positionSetting(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("open_position", open_position)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/setting/positionSetting", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (open_position == 0) {
                    locationSwitch.setChecked(true);
                } else {
                    locationSwitch.setChecked(false);
                }
            }
            @Override
            public void onSuccess(String result) {
                if (open_position == 0) {
                    locationSwitch.setChecked(false);
                } else {
                    locationSwitch.setChecked(true);
                }
            }
        });
    }

    private void saveUserInfoByField(){
        OKHttpUtils.getInstance().getRequest("app/user/modifyUserInfo", str, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                finish();
            }
        });
    }

}
