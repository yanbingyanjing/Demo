package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sh.sdk.shareinstall.ShareInstall;

import com.sh.sdk.shareinstall.listener.AppGetInstallListener;
import com.sh.sdk.shareinstall.listener.OnReportRegisterListener;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.ILoginView;
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
import com.yjfshop123.live.ui.widget.SwipeBackLayout;
import com.yjfshop123.live.utils.MyCountDownTimer;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//import me.devilsen.czxing.Scanner;
//import me.devilsen.czxing.code.BarcodeFormat;
//import me.devilsen.czxing.code.BarcodeReader;
//import me.devilsen.czxing.code.CodeResult;
//import me.devilsen.czxing.util.BarCodeUtil;
//import me.devilsen.czxing.util.BitmapUtil;
//import me.devilsen.czxing.view.ScanActivityDelegate;
//import me.devilsen.czxing.view.ScanView;

public class PhoneRegisterActivity extends BaseActivityH implements ILoginView {
    @BindView(R.id.de_register_phone)
    ClearWriteEditText deRegisterPhone;
    @BindView(R.id.de_register_password)
    ClearWriteEditText deRegisterPassword;
    @BindView(R.id.sendCode)
    Button sendCode;
    @BindView(R.id.de_register_invite)
    ClearWriteEditText deRegisterInvite;
    @BindView(R.id.agree_btn)
    CheckBox agreeBtn;
    @BindView(R.id.user_agree)
    TextView userAgree;
    @BindView(R.id.country)
    TextView country;

    @BindView(R.id.de_register)
    Button deRegister;
    String fromUid;
    @BindView(R.id.scan)
    ImageView scan;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.one)
    LinearLayout one;
    private String phoneString;
    private String passwordString;
    private String inviteCodeString;
    private String user_id;
    private String trtc_user_sig;
    private int info_complete;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);
        ButterKnife.bind(this);
        StatusBarUtil.StatusBarDarkMode(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        //fromUid = UserInfoUtil.getInviteCode();
        country.setText("+86");

        ShareInstall.getInstance().getInstallParams(new AppGetInstallListener() {
            @Override
            public void onGetInstallFinish(String info) {
                NLog.e("TAGTAG", "ShareInstall_info = " + info);
                if (TextUtils.isEmpty(fromUid)) {
                    try {
                        JSONObject jso = new JSONObject(info);
                        UserInfoUtil.setFromUid(jso.getString("invite_code"));
                        fromUid = UserInfoUtil.getFromUid();
                        deRegisterInvite.setText(fromUid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        initView();
        EventBus.getDefault().register(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSwipeBackLayout = new SwipeBackLayout(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this, SwipeBackLayout.EDGE_LEFT);
//        slide(true);
        mSwipeBackLayout.setEdgeSize(getResources().getDisplayMetrics().widthPixels * 1 / 10);
    }


    private void initView() {
//        setHeadLayout();
//        TextView tv_title_center = findViewById(R.id.tv_title_center);
//        tv_title_center.setText(getString(R.string.register));
        agreeBtn.setChecked(true);
        deRegisterInvite.setText(fromUid);
//        deRegisterPhone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 11) {
//                    AMUtils.onInactive(mContext, deRegisterPhone);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        deRegisterPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 10) {
//                    AMUtils.onInactive(mContext, deRegisterPassword);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

    }

    public int CODE_SELECT_IMAGE = 1;
    public boolean is_register = false;

    @OnClick({R.id.sendCode, R.id.user_agree, R.id.de_register, R.id.scan, R.id.btn_left, R.id.country})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.country:
                openCountry();
                break;
            case R.id.btn_left:
                finish();
                break;
            case R.id.sendCode:
                MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000, sendCode);
                phoneString = deRegisterPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    deRegisterPhone.setShakeAnimation();
                    return;
                }
                myCountDownTimer.start();

                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("region", "86")
                            .put("country_code", countryCode)
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

                        try {
                            SendCodeResponse response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
                            is_register = response.isIs_reg();
                            if (!is_register) {
                                NToast.shortToast(mContext, getString(R.string.send_phone_code_success));
                            } else {
                                NToast.shortToast(mContext, getString(R.string.phone_has_register));
                            }
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.user_agree:
                Intent intent = new Intent(this, HtmlWebViewActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.de_register:
                phoneString = deRegisterPhone.getText().toString().trim();
                passwordString = deRegisterPassword.getText().toString().trim();
                inviteCodeString = deRegisterInvite.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    deRegisterPhone.setShakeAnimation();
                    return;
                }
//                if(is_register){
//                    NToast.shortToast(mContext, getString(R.string.phone_has_register));
//                    return;
//                }

                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(mContext, R.string.password_is_null);
                    deRegisterPassword.setShakeAnimation();
                    return;
                }

                if (passwordString.contains(" ")) {
                    NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
                    deRegisterPassword.setShakeAnimation();
                    return;
                }

                if (!is_register && TextUtils.isEmpty(inviteCodeString)) {
                    NToast.shortToast(mContext, R.string.invitecode_is_null);
                    deRegisterInvite.setShakeAnimation();
                    return;
                }
                LoadDialog.show(PhoneRegisterActivity.this);
                String body_ = "";
                try {
                    body_ = new JsonBuilder().put("region", "86")
                            .put("mobile", phoneString)
                            .put("country", countryCn)
                            .put("country_code", countryCode)
                            .put("code", passwordString).put("invite_code", inviteCodeString)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/login/mobileSmsLogin", body_, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                        LoadDialog.dismiss(PhoneRegisterActivity.this);
                    }

                    @Override
                    public void onSuccess(String result) {
                        other_login(result);
                    }
                });

                break;
            case R.id.scan:
//                Resources resources = getResources();
//                List<Integer> scanColors = Arrays.asList(resources.getColor(R.color.scan_side), resources.getColor(R.color.scan_partial), resources.getColor(R.color.scan_middle));
//
//                Scanner.with(this)
//                        .setMaskColor(resources.getColor(R.color.mask_color))   // 设置设置扫码框四周颜色
//                        .setBorderColor(resources.getColor(R.color.box_line))   // 扫码框边框颜色
//                        .setBorderSize(BarCodeUtil.dp2px(this, 200))            // 设置扫码框大小
////        .setBorderSize(BarCodeUtil.dp2px(this, 200), BarCodeUtil.dp2px(this, 100))     // 设置扫码框长宽（如果同时调用了两个setBorderSize方法优先使用上一个）
//                        .setCornerColor(resources.getColor(R.color.corner))     // 扫码框角颜色
//                        .setScanLineColors(scanColors)                          // 扫描线颜色（这是一个渐变颜色）
////        .setHorizontalScanLine()                              // 设置扫码线为水平方向（从左到右）
//                        .setScanMode(ScanView.SCAN_MODE_BIG)                   // 扫描区域 0：混合 1：只扫描框内 2：只扫描整个屏幕
////        .setBarcodeFormat(BarcodeFormat.EAN_13)                 // 设置扫码格式
//                        .setTitle("My Scan View")                               // 扫码界面标题
//                        .showAlbum(true)                                        // 显示相册(默认为true)
//                        .setScanNoticeText("扫描二维码")                         // 设置扫码文字提示
//                        .setFlashLightOnText("打开闪光灯")                       // 打开闪光灯提示
//                        .setFlashLightOffText("关闭闪光灯")                      // 关闭闪光灯提示
//                        //.setFlashLightOnDrawable(R.drawable.ic_highlight_blue_open_24dp)       // 闪光灯打开时的样式
//                        // .setFlashLightOffDrawable(R.drawable.ic_highlight_white_close_24dp)    // 闪光灯关闭时的样式
//                        .setFlashLightInvisible()                               // 不使用闪光灯图标及提示
//                        //.continuousScan()                                       // 连续扫码，不关闭扫码界面
//                        .setOnClickAlbumDelegate(new ScanActivityDelegate.OnClickAlbumDelegate() {
//                            @Override
//                            public void onClickAlbum(Activity activity) {       // 点击右上角的相册按钮
//                                Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                activity.startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
//                            }
//
//                            @Override
//                            public void onSelectData(int requestCode, Intent data) { // 选择图片返回的数据
//                                if (requestCode == CODE_SELECT_IMAGE) {
//                                    decodeImage(data);
//                                }
//                            }
//                        })
//                        .setOnScanResultDelegate(new ScanActivityDelegate.OnScanDelegate() { // 接管扫码成功的数据
//                            @Override
//                            public void onScanResult(Activity activity, String result, BarcodeFormat format) {
//                                EventBus.getDefault().post(result);
//                            }
//                        })
//                        .start();
                break;
        }
    }

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
                        LoadDialog.dismiss(PhoneRegisterActivity.this);
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
                    dialogFragment.setCountry(authResponse.getUser_info().getCountry());
                    dialogFragment.show(getSupportFragmentManager(), "WeixinShouquanActivity");
//                    Intent intent = new Intent(PhoneRegisterActivity.this, WeixinShouquanActivity.class);
//                    intent.putExtra("country", authResponse.getUser_info().getCountry());
//                    startActivity(intent);

                } else {
                    Intent intent = new Intent(PhoneRegisterActivity.this, WanShanZhiLiaoActivity.class);
                    intent.putExtra("country", authResponse.getUser_info().getCountry());
                    startActivity(intent);
                }
            }
            //if (info_complete != 0) {
//            OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
//                @Override
//                public void onError(int errCode, String errInfo) {
//                    NToast.shortToast(mContext, errInfo);
//                    LoadDialog.dismiss(PhoneRegisterActivity.this);
//                }
//
//                @Override
//                public void onSuccess(String result) {
//                    initDatas(result);
//                }
//            });
//            } else {
//                LoadDialog.dismiss(this);
//                startActivity(new Intent(PhoneRegisterActivity.this, WanShanZhiLiaoActivity.class));
//                finish();
//            }
        } catch (HttpException e) {
            e.printStackTrace();
            LoadDialog.dismiss(this);
        }
    }

    private void initDatas(String result) {
        if (result == null) {
            LoadDialog.dismiss(PhoneRegisterActivity.this);
            return;
        }
        try {
            JSONObject data = new JSONObject(result);
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
            LoadDialog.dismiss(PhoneRegisterActivity.this);
        }
    }

    CountryListReponse countryListReponse;
    int countryIndex = -1;
    String countryCode = "+86";
    String countryCn= "中国";
    String[] countryList;

    private void getCountryData() {
        LoadDialog.show(PhoneRegisterActivity.this);
        OKHttpUtils.getInstance().getRequest("app/login/getCountry", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                LoadDialog.dismiss(PhoneRegisterActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(PhoneRegisterActivity.this);
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
                    .maxHeight(ScreenUtils.getScreenHeight(this)*2/3)
                    .asBottomList(getString(R.string.sign_choose_country), countryList,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    countryIndex = position;
                                    countryCode=countryListReponse.list[position].code;
                                    countryCn=countryListReponse.list[position].cn;
                                    country.setText(countryListReponse.list[position].code);
                                }
                            })
                    .bindLayout(R.layout._xpopup_center_impl_list)
                    .bindItemLayout(R.layout._xpopup_adapter_text)
                    .show();
        } else {
            getCountryData();
        }
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void onReceiveMsg(String message) {
        String uid = getDataFromUrl(message, "from_uid");
        if (TextUtils.isEmpty(uid)) {
            NToast.shortToast(mContext, getString(R.string.no_get_invite_code));
        } else
            deRegisterInvite.setText(uid);
    }

    @Subscriber(tag = "no_erweima", mode = ThreadMode.MAIN)
    public void onReceiveMsg1() {

    }

    private void decodeImage(Intent intent) {
        Uri selectImageUri = intent.getData();
        if (selectImageUri == null) {
            return;
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        cursor.close();

        // 适当压缩图片

        // 这个方法比较耗时，推荐放到子线程执行
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bitmap = BitmapUtil.getDecodeAbleBitmap(picturePath);
//                CodeResult result = BarcodeReader.getInstance().read(bitmap);
//                if (result == null) {
//                    EventBus.getDefault().post("");
//                } else {
//                    EventBus.getDefault().post(result.getText());
//                }
//            }
//        }).start();


    }

    public static String getDataFromUrl(String url, String key) {
        String value = "";
        Uri uri = Uri.parse(url);
        value = uri.getQueryParameter(key);
        return TextUtils.isEmpty(value) ? "" : value;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        //NToast.shortToast(mContext, R.string.login_success);

        Intent intent = new Intent();
        intent.setClass(this, ShiMingRenZhengActivity.class);
        intent.putExtra("country",countryCn);
        startActivity(intent);
        finish();
        /*Intent intent;
        if (Const.style == 2){
            intent = new Intent(mContext, LiveHomeActivity.class);
        }else {
            intent = new Intent(mContext, HomeActivity.class);
        }
        intent.putExtra("DATA", "queryConverMsg_");
        startActivity(intent);*/
    }


}
