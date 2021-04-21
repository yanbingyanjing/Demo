package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sh.sdk.shareinstall.ShareInstall;
import com.sh.sdk.shareinstall.listener.OnReportRegisterListener;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.ILoginView;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.request.InitUserInfoRequest;
import com.yjfshop123.live.net.response.AuthResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeixinShouquanActivity extends AbsDialogFragment implements ILoginView, MapLoiIml {

    TextView shouquan;
    String country;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_weixin_shouquan;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return false;
    }


    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = CommonUtils.getScreenWidth(mContext) * 4 / 5;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        startMap();
        shouquan = mRootView.findViewById(R.id.shouquan);
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        shouquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInstalledUtil.isWeChatAppInstalled(getActivity())) {
                    loginType = "weixin";
                    shareLoginUmeng(getActivity(), SHARE_MEDIA.WEIXIN);
                } else {
                    NToast.shortToast(getActivity(), getString(R.string.login_wx_cliend));
                }
            }
        });
        mRootView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private MapUtil mapUtil = new MapUtil(getActivity(), this);

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
    public void onDestroyView() {
        super.onDestroyView();
        if(mapUtil!=null)   mapUtil.destroy();
    }

    String loginType = "";

    @OnClick(R.id.shouquan)
    public void onViewClicked() {

    }

    private InitUserInfoRequest initUserInfoRequest = new InitUserInfoRequest();

    /**
     * 友盟开始授权(登入)
     */
    public void shareLoginUmeng(final Activity activity, SHARE_MEDIA share_media_type) {
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(activity).getPlatformInfo(activity, share_media_type, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                NLog.e("TAGTAG", "onStart授权开始: ");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                //sdk是6.4.5的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因

                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");//名称
                String gender = map.get("gender");//性别
                String iconurl = map.get("iconurl");//头像地址

                int sex = 1;
                if (!TextUtils.isEmpty(gender)) {
                    switch (gender) {
                        case "男":
                            sex = 1;
                            break;
                        case "女":
                            sex = 2;
                            break;
                    }
                }
                initUserInfoRequest.setUser_nickname(name);
                initUserInfoRequest.setSex(sex);

                initUserInfoRequest.setAvatar(iconurl);
                if (!TextUtils.isEmpty(longitude))
                    initUserInfoRequest.setLongitude(String.valueOf(longitude));
                if (!TextUtils.isEmpty(latitude))
                    initUserInfoRequest.setLatitude(String.valueOf(latitude));

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
                initUserInfoRequest.setOpenid(openid);
                initUserInfoRequest.setUnionid(unionid);
                initUserInfoRequest.setApp_id(loginType);
                initUserInfoRequest.setAccess_token(access_token);
                initUserInfoRequest.setExpire_time(expires_in);

                LoadDialog.show(getActivity());

                try {
                    String json = null;
                    json = JsonMananger.beanToJson(initUserInfoRequest);
                    OKHttpUtils.getInstance().getRequest("app/login/initUserInfo", json, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            LoadDialog.dismiss(getActivity());
                        }

                        @Override
                        public void onSuccess(String result) {
                            other_login(result);
                        }
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(activity, "授权失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(activity, "授权取消", Toast.LENGTH_LONG).show();
            }
        });
    }


    String user_id;
    String trtc_user_sig;

    private void other_login(String data) {
        if (data == null) {
            LoadDialog.dismiss(getActivity());
            return;
        }

        try {
            AuthResponse authResponse = JsonMananger.jsonToBean(data, AuthResponse.class);
            if (!TextUtils.isEmpty(authResponse.getUser_info().getToken())) {
                if (authResponse.getUser_info().getInfo_complete() == 0) {
                    NToast.shortToast(mContext, "失败！请重新提交");
                    LoadDialog.dismiss(getActivity());
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
                        LoadDialog.dismiss(getActivity());
                    }

                    @Override
                    public void onSuccess(String result) {
                        initDatas(result);
                    }
                });


                // LoginHelper loginHelper = new LoginHelper(this);
                // loginHelper.loginSDK(String.valueOf(authResponse.getUser_info().getUser_id()), authResponse.getUser_info().getTrtc_user_sig());
            } else {
                LoadDialog.dismiss(getActivity());
            }
        } catch (HttpException e) {
            e.printStackTrace();
            LoadDialog.dismiss(getActivity());
        }
    }

    private void initDatas(String result) {
        if (result == null) {
            LoadDialog.dismiss(getActivity());
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
                LoadDialog.dismiss(getActivity());
                NToast.shortToast(getActivity(), "实名审核中，请审核通过后再登陆");
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
            LoadDialog.dismiss(getActivity());
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
        LoadDialog.dismiss(getActivity());

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
            intent.setClass(getActivity(), ShiMingRenZhengActivity.class);
            intent.putExtra("country", country);
            startActivity(intent);
            getActivity().finish();
        } else if (status == 10) { //审核失败
            Intent intent = new Intent();
            intent.setClass(getActivity(), ShiMingRenZhengActivity.class);
            intent.putExtra("country", country);
            startActivity(intent);
        } else if (status == 2) {//通过
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (status == 1) {//审核中
//            Intent intent = new Intent(this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            NToast.shortToast(getActivity(), "实名审核中");
        }
        //finish();

    }
}
