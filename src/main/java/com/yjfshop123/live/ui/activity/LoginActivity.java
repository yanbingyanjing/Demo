package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sh.sdk.shareinstall.ShareInstall;
import com.sh.sdk.shareinstall.listener.OnReportRegisterListener;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.ILoginView;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.response.SendCodeResponse;
import com.yjfshop123.live.model.CountryListReponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.AuthResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.utils.AMUtils;
import com.yjfshop123.live.server.widget.ClearWriteEditText;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.DialogHomeAddFragment;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.MyCountDownTimer;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.UpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.App.is_check_update;

public class LoginActivity extends BaseActivityH implements ILoginView, MapLoiIml {
    @BindView(R.id.de_login_phone)
    ClearWriteEditText deLoginPhone;
    @BindView(R.id.de_login_password)
    ClearWriteEditText deLoginPassword;
    @BindView(R.id.sendCode)
    Button sendCode;
    @BindView(R.id.de_login_sign)
    Button deLoginSign;
    @BindView(R.id.de_register_invite)
    ClearWriteEditText deRegisterInvite;
    @BindView(R.id.invite_code_fl)
    FrameLayout inviteCodeFl;
    @BindView(R.id.country)
    TextView countryTx;

    //    @BindView(R.id.cityTxt)
//    TextView cityTxt;
//    @BindView(R.id.cityLayout)
//    LinearLayout cityLayout;
    private String phoneString;
    private String passwordString;
    private String inviteCodeString;
    private String user_id;
    private String trtc_user_sig;
    private int info_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ButterKnife.bind(this);
        initView();
        StatusBarUtil.StatusBarDarkMode(this);
            checkUpdate();
    }

    private void checkUpdate() {
        UpdateUtils updateUtils = new UpdateUtils(this);
        updateUtils.updateDiy();
    }

    private void initView() {


        String oldPhone = UserInfoUtil.getPhoneNumber();
        if (!TextUtils.isEmpty(oldPhone)) {
            deLoginPhone.setText(oldPhone);
            deLoginPhone.setSelection(oldPhone.length());
        }
        String oldcountryCode = UserInfoUtil.getcountryCode();
        String oldcountryCn = UserInfoUtil.getcountryCn();
        if (!TextUtils.isEmpty(oldcountryCode) && !TextUtils.isEmpty(oldcountryCn)) {
            countryCode = oldcountryCode;
            countryCn = oldcountryCn;
            countryTx.setText(countryCode);
        }
        startMap();
    }

    private MapUtil mapUtil = new MapUtil(this, this);

    private void startMap() {
        mapUtil.LoPoi();
    }

    public String latitude = "";
    public String longitude = "";
    public String address = "";

    @Override
    public void onMapSuccess(double latitude, double longitude, String address, String currentWeizhi) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.address = currentWeizhi;
    }

    @Override
    public void onMapFail(String msg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapUtil != null) mapUtil.destroy();
    }

    CountryListReponse countryListReponse;

    String countryCode = "+86";
    String countryCn = "中国";
    String[] countryList;

    private void getCountryData() {
        LoadDialog.show(LoginActivity.this);
        OKHttpUtils.getInstance().getRequest("app/login/getCountry", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                LoadDialog.dismiss(LoginActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(LoginActivity.this);
                if (TextUtils.isEmpty(result)) {
                    NToast.shortToast(mContext, "获取国家列表失败，请重试");
                    return;
                }
                countryListReponse = new Gson().fromJson(result, CountryListReponse.class);
                if (countryListReponse.list == null || countryListReponse.list.length == 0) {
                    NToast.shortToast(mContext, "获取国家列表失败，请重试");
                    return;
                }
                countryList = new String[countryListReponse.list.length];
                for (int i = 0; i < countryListReponse.list.length; i++) {
                    countryList[i] = countryListReponse.list[i].cn;
                }
                openCountry();

            }
        });

    }

    private void openCountry() {
        if (countryList != null) {
            new XPopup.Builder(this)
                    .maxHeight(ScreenUtils.getScreenHeight(this) * 2 / 3)
                    .asBottomList(getString(R.string.sign_choose_country), countryList,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    countryCode = countryListReponse.list[position].code;
                                    countryCn = countryListReponse.list[position].cn;
                                    countryTx.setText(countryListReponse.list[position].code);
                                }
                            })
                    .bindLayout(R.layout._xpopup_center_impl_list)
                    .bindItemLayout(R.layout._xpopup_adapter_text)
                    .show();
        } else {
            getCountryData();
        }
    }

    @OnClick({R.id.sendCode, R.id.de_login_sign, R.id.register, R.id.country})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.country:
                openCountry();
                break;
            case R.id.register:
                Intent intent3 = new Intent(LoginActivity.this, PhoneRegisterActivity.class);
                startActivity(intent3);
                break;
            case R.id.sendCode:
                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000, sendCode);
                phoneString = deLoginPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    deLoginPhone.setShakeAnimation();
                    return;
                }
                myCountDownTimer.start();

                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("region", "86")
                            .put("mobile", phoneString)
                            .put("country_code", countryCode)
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
                        NToast.shortToast(mContext, getString(R.string.send_phone_code_success));
                        try {
                            SendCodeResponse response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
//                            if (!response.isIs_reg()) {
//                                cityLayout.setVisibility(View.VISIBLE);
//                            } else {
//                                cityLayout.setVisibility(View.GONE);
//                            }
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.de_login_sign:
                phoneString = deLoginPhone.getText().toString().trim();
                passwordString = deLoginPassword.getText().toString().trim();
                inviteCodeString = deRegisterInvite.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    deLoginPhone.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(mContext, R.string.password_is_null);
                    deLoginPassword.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
                    deLoginPassword.setShakeAnimation();
                    return;
                }
                if (inviteCodeFl.getVisibility() == View.VISIBLE) {
                    if (TextUtils.isEmpty(inviteCodeString)) {
                        NToast.shortToast(mContext, R.string.invitecode_is_null);
                        deRegisterInvite.setShakeAnimation();
                        return;
                    }
                }

                LoadDialog.show(LoginActivity.this);
                String body_ = "";
                try {
                    JsonBuilder jsonBuilder = new JsonBuilder();
                    jsonBuilder.put("region", "86")
                            .put("mobile", phoneString)
                            .put("country_code", countryCode)
                            .put("code", passwordString);


                    if (!TextUtils.isEmpty(longitude))
                        jsonBuilder.put("longitude", String.valueOf(longitude));
                    if (!TextUtils.isEmpty(latitude))
                        jsonBuilder.put("latitude", String.valueOf(latitude));


                    if (!TextUtils.isEmpty(address)) {
                        String[] location = address.split(",");
                        if (location.length == 0) {
                            NLog.d("城市獲取", "城市未获取到");
                        } else if (location.length == 1) {
                            jsonBuilder.put("province_name", location[0]);
                            jsonBuilder.put("city_name", location[0]);
                            jsonBuilder.put("district_name", location[0]);
                        } else if (location.length == 2) {
                            jsonBuilder.put("province_name", location[0]);
                            jsonBuilder.put("city_name", location[1]);
                            jsonBuilder.put("district_name", location[1]);
                        } else if (location.length == 3) {
                            jsonBuilder.put("province_name", location[0]);
                            jsonBuilder.put("city_name", location[1]);
                            jsonBuilder.put("district_name", location[2]);
                        }
                    }


                    if (inviteCodeFl.getVisibility() == View.VISIBLE) {
                        if (!TextUtils.isEmpty(inviteCodeString)) {
                            jsonBuilder.put("invite_code", inviteCodeString);
                        }
                    }
                    body_ = jsonBuilder
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/login/mobileSmsLogin", body_, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                        LoadDialog.dismiss(LoginActivity.this);
                    }

                    @Override
                    public void onSuccess(String result) {
                        other_login(result);
                    }
                });
                break;
        }
    }

    String country;

    private void other_login(String data) {
        if (data == null) {
            LoadDialog.dismiss(this);
            return;
        }

        try {
            AuthResponse authResponse = JsonMananger.jsonToBean(data, AuthResponse.class);
            String nickName = authResponse.getUser_info().getUser_nickname();
            String portraitUri = authResponse.getUser_info().getAvatar();
            user_id = String.valueOf(authResponse.getUser_info().getUser_id());
            trtc_user_sig = authResponse.getUser_info().getTrtc_user_sig();
            String phone_number = authResponse.getUser_info().getMobile();
            String token = authResponse.getUser_info().getToken();
            info_complete = authResponse.getUser_info().getInfo_complete();
            country = authResponse.getUser_info().getCountry();
            UserInfoUtil.setMiTencentId(user_id);
            UserInfoUtil.setMiPlatformId(user_id);
            UserInfoUtil.setAvatar(portraitUri);
            UserInfoUtil.setName(nickName);
            UserInfoUtil.setSignature(trtc_user_sig);
            if (!TextUtils.isEmpty(phone_number)) {
                UserInfoUtil.setPhoneNumber(phone_number);
            }
            UserInfoUtil.setcountryCode(countryCode);
            UserInfoUtil.setcountryCn(countryCn);
            UserInfoUtil.setToken_InfoComplete(token, info_complete);

            //StatisticalTool.getInstance().startEngine();

            if (info_complete != 0) {
                OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(LoginActivity.this);
                    }
                    @Override
                    public void onSuccess(String result) {
                        initDatas(result);
                    }
                });
            } else {
                LoadDialog.dismiss(this);

                if (authResponse.getIs_weixin() == 1) {
                    WeixinShouquanActivity dialogFragment = new WeixinShouquanActivity();
                    dialogFragment.setCountry(country);
                    dialogFragment.show(getSupportFragmentManager(), "WeixinShouquanActivity");
//                    Intent intent = new Intent(LoginActivity.this, WeixinShouquanActivity.class);
//                    intent.putExtra("country", country);
//                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, WanShanZhiLiaoActivity.class);
                    intent.putExtra("country", country);
                    startActivity(intent);
                }

            }
        } catch (HttpException e) {
            e.printStackTrace();
            LoadDialog.dismiss(this);
        }
    }

    private void initDatas(String result) {
        if (result == null) {
            LoadDialog.dismiss(LoginActivity.this);
            return;
        }
        try {
            JSONObject data = new JSONObject(result);

            if (data.getInt("auth_status") == 1) {//审核中
                UserInfoUtil.setMiTencentId("");
                UserInfoUtil.setMiPlatformId("");
                UserInfoUtil.setToken_InfoComplete("", 0);
                UserInfoUtil.setFromUid("");
                UserInfoUtil.setIsRead(false);
                LoadDialog.dismiss(LoginActivity.this);
                NToast.shortToast(this, "实名审核中，请审核通过后再登陆");
                return;
            }

            UserInfoUtil.setUserInfo(
                    data.getInt("sex"),
                    data.getInt("daren_status"),
                    data.getInt("is_vip"),
                    data.getString("speech_introduction"),
                    data.getInt("auth_status"),
                    data.getString("province_name") + "," + data.getString("city_name") + "," + data.getString("district_name"),
                    data.getString("tags"),
                    data.getInt("age"),
                    data.getString("signature"));

            LoginHelper loginHelper = new LoginHelper(this);
            loginHelper.loginSDK(user_id, trtc_user_sig);
        } catch (JSONException e) {
            e.printStackTrace();
            LoadDialog.dismiss(LoginActivity.this);
        }
    }


    @Override
    public void onLoginSDKSuccess() {
        inLogin();
    }

    @Override
    public void onLoginSDKFailed(int i, String s) {
        inLogin();
    }

    private void inLogin() {
        App.initPush();
        ShareInstall.getInstance().reportRegister(new OnReportRegisterListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        BroadcastManager.getInstance(mContext).sendBroadcast(Config.LOGIN, Config.LOGINSUCCESS);

        LoadDialog.dismiss(this);
        // NToast.shortToast(mContext, R.string.login_success);

        onResumeChecKIsVerify();




        /*Intent intent;
        if (Const.style == 2){
            intent = new Intent(mContext, LiveHomeActivity.class);
        }else {
            intent = new Intent(mContext, HomeActivity.class);
        }
        intent.putExtra("DATA", "queryConverMsg_");
        startActivity(intent);*/
    }

    //检测是否有实名认证
    private boolean is_login;

    protected void onResumeChecKIsVerify() {
        is_login = UserInfoUtil.getInfoComplete() != 0;
        //未登录
        if (!is_login) return;
        //已登录检查是否实名认证
        int status = UserInfoUtil.getAuthStatus();

        if (status == 0) {//未提交
            Intent intent = new Intent();
            intent.setClass(this, ShiMingRenZhengActivity.class);
            intent.putExtra("country", country);
            startActivity(intent);
        } else if (status == 10) { //审核失败
            Intent intent = new Intent();
            intent.setClass(this, ShiMingRenZhengActivity.class);
            intent.putExtra("country", country);
            startActivity(intent);
        } else if (status == 2) {//通过
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (status == 1) {//审核中
//            Intent intent = new Intent(this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            NToast.shortToast(this, "实名审核中");
        }
        //finish();

    }

}
