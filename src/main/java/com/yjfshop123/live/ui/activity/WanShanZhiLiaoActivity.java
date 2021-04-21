package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sh.sdk.shareinstall.listener.AppGetInstallListener;
import com.sh.sdk.shareinstall.listener.OnReportRegisterListener;
import com.yjfshop123.live.App;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Interface.ILoginView;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.request.InitUserInfoRequest;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.AuthResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSAgeAlertDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.GlideOptions;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.sh.sdk.shareinstall.ShareInstall;

import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WanShanZhiLiaoActivity extends BaseActivity implements View.OnClickListener, CosXmlUtils.OSSResultListener, MapLoiIml, ILoginView {

    public static final int REQUEST_CODE_SELECT = 1005;

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.setAutor)
    CircleImageView setAutor;
    @BindView(R.id.nickName)
    EditText nickName;
    @BindView(R.id.ageLayout)
    RelativeLayout ageLayout;
    @BindView(R.id.cityLayout)
    RelativeLayout cityLayout;
    @BindView(R.id.checkMan)
    RadioButton checkMan;
    @BindView(R.id.checkGirl)
    RadioButton checkGirl;
    @BindView(R.id.registNow)
    Button registNow;
    @BindView(R.id.yonghuxieyi)
    TextView yonghuxieyi;
    @BindView(R.id.yinsizhengce)
    TextView yinsizhengce;
    @BindView(R.id.ageTxt)
    TextView ageTxt;
    @BindView(R.id.cityTxt)
    TextView cityTxt;

    private String mi_tencentId;
    private String path_;
    private String localPath;
    private String fromUid;

    private InitUserInfoRequest initUserInfoRequest = new InitUserInfoRequest();

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanshanzhiliao);

        ButterKnife.bind(this);
        setHeadLayout();
        tv_title_center.setText(getString(R.string.wszl));
        country = getIntent().getStringExtra("country");
        fromUid = UserInfoUtil.getFromUid();
        ShareInstall.getInstance().getInstallParams(new AppGetInstallListener() {
            @Override
            public void onGetInstallFinish(String info) {
                if (TextUtils.isEmpty(fromUid)) {
                    try {
                        JSONObject jso = new JSONObject(info);
                        fromUid = jso.getString("from_uid");
                        UserInfoUtil.setFromUid(fromUid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                NLog.e("TAGTAG", "ShareInstall_info = " + info);
            }
        });

        initEvent();
    }

    private void initEvent() {
        setAutor.setOnClickListener(this);
        ageLayout.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        registNow.setOnClickListener(this);
        yonghuxieyi.setOnClickListener(this);
        yinsizhengce.setOnClickListener(this);

        checkMan.setOnClickListener(this);
        checkGirl.setOnClickListener(this);


        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        uploadDialog = new UploadDialog(this);

        mi_tencentId = UserInfoUtil.getMiTencentId();
        // path_ = UserInfoUtil.getAvatar();
        //  int age = UserInfoUtil.getAge();
        //String name = UserInfoUtil.getName();

        if (!TextUtils.isEmpty(path_)) {
            Glide.with(this).load(path_).apply(GlideOptions.wangShanGlideptions()).into(setAutor);
        }
        // nickName.setText(name);
        // ageTxt.setText(age == 0 ? "19" : String.valueOf(age));

        startMap();

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

    }

    private MapUtil mapUtil = new MapUtil(this, this);

    private void startMap() {
        mapUtil.LoPoi();
    }

    public  String latitude = "";
    public  String longitude = "";
    public  String address = "";

    @Override
    public void onMapSuccess(double latitude, double longitude, String address, String currentWeizhi) {
        this.latitude = latitude + "";
        this.longitude = longitude + "";
        this.address = currentWeizhi;
        if (TextUtils.isEmpty(currentWeizhi) || currentWeizhi.equals(",,")) {
            cityTxt.setText("");
        } else {
            cityTxt.setText(currentWeizhi);
        }
    }

    @Override
    public void onMapFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapUtil.destroy();
    }

    private void tickPhoto() {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#B28D51"))
                // 返回图标ResId
                .backResId(R.drawable.head_back)
                // 标题
                .title(getString(R.string.per_1))
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#B28D51"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 400, 400)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();

        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, 1005);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setAutor:
                tickPhoto();
                break;
            case R.id.ageLayout:
                final IOSAgeAlertDialog ageAlertDialog = new IOSAgeAlertDialog(this);
                ageAlertDialog.builder(ageTxt.getText().toString()).setTitle(getString(R.string.wszl_age));
                ageAlertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                ageAlertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ageTxt.setText(ageAlertDialog.getAgeStr());
                    }
                });
                ageAlertDialog.show();
                break;
            case R.id.cityLayout:
                final IOSCityAlertDialog alertDialog = new IOSCityAlertDialog(this);
                alertDialog.builder().setTitle(getString(R.string.wszl_city));
                alertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                alertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String city = alertDialog.getCityStr();
                        cityTxt.setText(city);
                    }
                });
                alertDialog.show();
                break;
            case R.id.checkMan:
                checkGirl.setChecked(false);
                showDialog();
                break;
            case R.id.checkGirl:
                checkMan.setChecked(false);
                showDialog();
                break;
            case R.id.registNow:
//                uploadDialog.show(300);
//                uploadDialog.uploadPhoto(imgs.size());

                if (TextUtils.isEmpty(path_) && TextUtils.isEmpty(localPath)) {
                    NToast.shortToast(this, R.string.wszl_touxiang);
                    return;
                }

                if (TextUtils.isEmpty(nickName.getText())) {
                    NToast.shortToast(this, R.string.wszl_input_nick);
                    return;
                }

                String nickNameTv = nickName.getText().toString().trim();
                if (TextUtils.isEmpty(nickNameTv)) {
                    NToast.shortToast(this, "昵称不能为空格");
                    return;
                }

                if (TextUtils.isEmpty(ageTxt.getText()) || ageTxt.getText().toString().equals("0")) {
                    NToast.shortToast(this, R.string.wszl_input_age);
                    return;
                }
//                if (TextUtils.isEmpty(cityTxt.getText())) {
//                    NToast.shortToast(this, R.string.wszl_input_city);
//                    return;
//                }
                if (!checkMan.isChecked() && !checkGirl.isChecked()) {
                    NToast.shortToast(this, R.string.wszl_input_sex);
                    return;
                }

//                if (TextUtils.isEmpty(localPath)) {
//                    register();
//                } else {
                uploadDialog = new UploadDialog(this);
                uploadDialog.show(300);
                uploadDialog.uploadPhoto(1);
                uploadData();
                // }
                break;
            case R.id.yonghuxieyi:
                Intent intent = new Intent(this, HtmlWebViewActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.yinsizhengce:
                Intent intent2 = new Intent(this, HtmlWebViewActivity.class);
                intent2.putExtra("type", 1);
                startActivity(intent2);
                break;
        }
    }

    private void register() {
        try {
            LoadDialog.show(this);
            initUserInfoRequest.setUser_nickname(nickName.getText().toString());
            if (checkMan.isChecked()) {
                initUserInfoRequest.setSex(1);
            } else if (checkGirl.isChecked()) {
                initUserInfoRequest.setSex(2);
            } else {
                initUserInfoRequest.setSex(0);
            }
            initUserInfoRequest.setAge(Integer.parseInt(ageTxt.getText().toString()));
            initUserInfoRequest.setAvatar(path_);

            if (!TextUtils.isEmpty(country) && !country.equals("中国")) {
                initUserInfoRequest.setProvince_name(country);
                initUserInfoRequest.setCity_name(country);
                initUserInfoRequest.setDistrict_name(country);
            } else {
                if (!TextUtils.isEmpty(address)) {
                    String[] location = address.split(",");
                    if (location.length == 0) {
                        NLog.d("城市獲取", "城市未获取到");
                    } else if (location.length == 1) {
                        initUserInfoRequest.setProvince_name(location[0]);
                        initUserInfoRequest.setCity_name(location[0]);
                        initUserInfoRequest.setDistrict_name(location[0]);
                    } else if (location.length == 2) {
                        initUserInfoRequest.setProvince_name(location[0]);
                        initUserInfoRequest.setCity_name(location[1]);
                        initUserInfoRequest.setDistrict_name(location[1]);
                    } else if (location.length == 3) {
                        initUserInfoRequest.setProvince_name(location[0]);
                        initUserInfoRequest.setCity_name(location[1]);
                        initUserInfoRequest.setDistrict_name(location[2]);
                    }
                }
            }
            if (!TextUtils.isEmpty(longitude))
                initUserInfoRequest.setLongitude(String.valueOf(longitude));
            if (!TextUtils.isEmpty(latitude))
                initUserInfoRequest.setLatitude(String.valueOf(latitude));
            initUserInfoRequest.setFrom_uid(fromUid);

            String json = JsonMananger.beanToJson(initUserInfoRequest);
            OKHttpUtils.getInstance().getRequest("app/login/initUserInfo", json, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                    LoadDialog.dismiss(WanShanZhiLiaoActivity.this);
                }

                @Override
                public void onSuccess(String result) {
                    other_login(result);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            LoadDialog.dismiss(this);
            NToast.shortToast(mContext, "操作失败，请重新尝试");
        }
    }

    String user_id;
    String trtc_user_sig;

    private void other_login(String data) {
        if (data == null) {
            LoadDialog.dismiss(this);
            return;
        }

        try {
            AuthResponse authResponse = JsonMananger.jsonToBean(data, AuthResponse.class);
            if (!TextUtils.isEmpty(authResponse.getUser_info().getToken())) {
                if (authResponse.getUser_info().getInfo_complete() == 0) {
                    NToast.shortToast(mContext, "失败！请重新提交");
                    LoadDialog.dismiss(this);
                    return;
                }

                String nickName = authResponse.getUser_info().getUser_nickname();
                String portraitUri = authResponse.getUser_info().getAvatar();
                user_id = String.valueOf(authResponse.getUser_info().getUser_id());
                trtc_user_sig = authResponse.getUser_info().getTrtc_user_sig();
                String phone_number = authResponse.getUser_info().getMobile();
                String token = authResponse.getUser_info().getToken();
                int info_complete = authResponse.getUser_info().getInfo_complete();
                int age = authResponse.getUser_info().getAge();

                UserInfoUtil.setMiTencentId(user_id);
                UserInfoUtil.setMiPlatformId(user_id);
                UserInfoUtil.setAvatar(portraitUri);
                UserInfoUtil.setName(nickName);
                UserInfoUtil.setSignature(trtc_user_sig);
                if (!TextUtils.isEmpty(phone_number)) {
                    UserInfoUtil.setPhoneNumber(phone_number);
                }
                UserInfoUtil.setToken_InfoComplete(token, 1);
                OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(WanShanZhiLiaoActivity.this);
                    }

                    @Override
                    public void onSuccess(String result) {
                        initDatas(result);
                    }
                });
//                UserInfoUtil.setUserInfo(authResponse.getUser_info().getSex(),
//                        0,
//                        authResponse.getUser_info().getIs_vip(),
//                        "",
//                        0,
//                        initUserInfoRequest.getProvince_name() + "," + initUserInfoRequest.getCity_name() + "," + initUserInfoRequest.getDistrict_name(),
//                        "",
//                        age,
//                        "");

                updateUserExtra();

                // LoginHelper loginHelper = new LoginHelper(this);
                // loginHelper.loginSDK(String.valueOf(authResponse.getUser_info().getUser_id()), authResponse.getUser_info().getTrtc_user_sig());
            } else {
                LoadDialog.dismiss(this);
            }
        } catch (HttpException e) {
            e.printStackTrace();
            LoadDialog.dismiss(this);
        }
    }

    private void initDatas(String result) {
        if (result == null) {
            LoadDialog.dismiss(WanShanZhiLiaoActivity.this);
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
                LoadDialog.dismiss(WanShanZhiLiaoActivity.this);
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
            LoadDialog.dismiss(WanShanZhiLiaoActivity.this);
        }
    }

    private void showDialog() {
        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.wszl_miaoshu),
                true, new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    localPath = pathList.get(0);
                    NLog.d("获取图片", localPath);
                    Glide.with(WanShanZhiLiaoActivity.this).load(localPath).into(setAutor);
                }
                break;
        }
    }

    private void uploadData() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.uploadData(localPath, "image", 6, mi_tencentId, uploadDialog);
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                uploadDialog.dissmis();
                register();
            } else if (msg.what == 0x234) {
                NToast.shortToast(mContext, " 头像上传失败，请重新提交");
            }
        }
    };

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        path_ = response.get(0).getObject();
        if (path_.contains(BuildConfig.APPLICATION_ID)) {
            handler.sendEmptyMessage(0x234);
            return;
        }
        handler.sendEmptyMessage(0x123);
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

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
        LoadDialog.dismiss(this);

        App.initPush();
        ShareInstall.getInstance().reportRegister(new OnReportRegisterListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        onResumeChecKIsVerify();
        //  NToast.shortToast(mContext, R.string.login_success);
//
//        BroadcastManager.getInstance(mContext).sendBroadcast(Config.LOGIN, Config.LOGINSUCCESS);
//        Intent intent=new Intent(this,HomeActivity.class);
//        startActivity(intent);
//        finish();

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
            finish();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, 0);
    }

    private void updateUserExtra() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("device_brand", android.os.Build.BRAND)
                    .put("device_model", android.os.Build.MODEL)
                    .put("device_os_version", android.os.Build.VERSION.RELEASE)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/updateUserExtra", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
            }
        });
    }
}
