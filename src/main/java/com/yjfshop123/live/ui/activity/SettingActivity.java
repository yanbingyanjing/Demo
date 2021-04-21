package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.App;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.LoginResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.setting.ApplyUnsealingActivity;
import com.yjfshop123.live.utils.RouterUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingActivity extends BaseActivityForNewUi implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.phoneLayout)
    LinearLayout phoneLayout;

    @BindView(R.id.locationLayout)
    LinearLayout locationLayout;

    @BindView(R.id.wxLayout)
    LinearLayout wxLayout;

    @BindView(R.id.qqLayout)
    LinearLayout qqLayout;

    @BindView(R.id.aboutUs)
    LinearLayout aboutUs;
    @BindView(R.id.onLineService)
    LinearLayout onLineService;
    @BindView(R.id.ac_set_exit)
    LinearLayout mExit;

    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.wxBind)
    TextView wxBind;
    @BindView(R.id.qqBind)
    TextView qqBind;

    @BindView(R.id.versionName)
    TextView versionName;

    @BindView(R.id.apply_unsealing)
    LinearLayout applyUnsealing;
    @BindView(R.id.clear_cache)
    LinearLayout clearCache;
    @BindView(R.id.trade)
    LinearLayout trade;
    @BindView(R.id.user_xieyi)
    LinearLayout userXieyi;
    @BindView(R.id.fix_user_info)
    LinearLayout fixUserInfo;

    private LoginResponse mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
        initEvent();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.set_set);
    }

    private void initEvent() {
        fixUserInfo.setOnClickListener(this);
        userXieyi.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        wxLayout.setOnClickListener(this);
        qqLayout.setOnClickListener(this);
        applyUnsealing.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        onLineService.setOnClickListener(this);
        mExit.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        versionName.setText(BuildConfig.VERSION_NAME);

        if (ActivityUtils.IS_VIDEO()) {
            mExit.setVisibility(View.GONE);
            aboutUs.setVisibility(View.GONE);
            onLineService.setVisibility(View.GONE);
            locationLayout.setVisibility(View.GONE);
        }

        OKHttpUtils.getInstance().getRequest("app/user/getBindingInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    JSONObject data = new JSONObject(result);
                    phoneNum.setText(data.getString("binding_mobile"));
//                    phoneNum.setTextColor(getResources().getColor(R.color.color_333333));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getUserInfo();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (mResponse == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.clear_cache:
                NToast.shortToast(this, getString(R.string.clear_success));
                break;
            case R.id.fix_user_info:
                intent.setClass(this, PersonalInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.user_xieyi:
                Intent intent1 = new Intent(this, HtmlWebViewActivity.class);
                intent1.putExtra("type", 0);
                startActivity(intent1);

                break;
            case R.id.apply_unsealing:
                intent.setClass(this, ApplyUnsealingActivity.class);
                startActivity(intent);
                break;
            case R.id.phoneLayout:
                intent.setClass(this, BindPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.locationLayout:
                intent.setClass(this, BindQQWXActivity.class);
                intent.putExtra("USER_INFO", mResponse);
                intent.putExtra("loginType", "location");
                startActivityForResult(intent, requestCode_);
                break;
            case R.id.wxLayout:
                intent.setClass(this, BindQQWXActivity.class);
                intent.putExtra("USER_INFO", mResponse);
                intent.putExtra("loginType", "weixin");
                startActivityForResult(intent, requestCode_);
                break;
            case R.id.qqLayout:
                intent.setClass(this, BindQQWXActivity.class);
                intent.putExtra("USER_INFO", mResponse);
                intent.putExtra("loginType", "qq");
                startActivityForResult(intent, requestCode_);
                break;
            case R.id.onLineService:
                intent.setClass(this, OnlineServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutUs:
                intent.setClass(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.ac_set_exit:
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.quit), getString(R.string.other_cancel),
                        getString(R.string.setting_logout), true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                LoadDialog.show(SettingActivity.this);
                                NLog.e("是否", "0");

                                TIMManager.getInstance().logout(new TIMCallBack() {
                                    @Override
                                    public void onError(int code, String desc) {
                                        OKHttpUtils.getInstance().getRequest("app/login/logout", "", new RequestCallback() {
                                            @Override
                                            public void onError(int errCode, String errInfo) {
                                                LoadDialog.dismiss(SettingActivity.this);
                                                NLog.e("1退出登录", "失败");
                                                NToast.shortToast(SettingActivity.this, errInfo);
                                            }

                                            @Override
                                            public void onSuccess(String result) {
                                                LoadDialog.dismiss(SettingActivity.this);
                                                NLog.e("1退出登录", "成功");
                                                BroadcastManager.getInstance(mContext).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                                                RouterUtil.logout(SettingActivity.this);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onSuccess() {
                                        OKHttpUtils.getInstance().getRequest("app/login/logout", "", new RequestCallback() {
                                            @Override
                                            public void onError(int errCode, String errInfo) {
                                                LoadDialog.dismiss(SettingActivity.this);
                                                NLog.e("2退出登录", "失败");
                                                NToast.shortToast(SettingActivity.this, errInfo);
                                            }

                                            @Override
                                            public void onSuccess(String result) {
                                                LoadDialog.dismiss(SettingActivity.this);
                                                NLog.e("2退出登录", "成功");
                                                BroadcastManager.getInstance(mContext).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                                                RouterUtil.logout(SettingActivity.this);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static int requestCode_ = 1202;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode_) {
            getUserInfo();
        }
    }

    private void getUserInfo() {
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    mResponse = JsonMananger.jsonToBean(result, LoginResponse.class);
                    if (!mResponse.getMobile().equals("") && mResponse.getMobile() != null) {
                        phoneNum.setText(mResponse.getMobile());
//                        phoneNum.setTextColor(getResources().getColor(R.color.color_333333));
                        phoneLayout.setClickable(false);
                    }
                    if (!mResponse.getWeixin().equals("") && mResponse.getWeixin() != null) {
                        wxBind.setText(mResponse.getWeixin());
//                        wxBind.setTextColor(getResources().getColor(R.color.color_333333));
                        //wxLayout.setClickable(false);
                    }
                    if (!mResponse.getQq().equals("") && mResponse.getQq() != null) {
                        qqBind.setText(mResponse.getQq());
//                        qqBind.setTextColor(getResources().getColor(R.color.color_333333));
                        //qqLayout.setClickable(false);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick(R.id.user_xieyi)
    public void onViewClicked() {
    }
}
