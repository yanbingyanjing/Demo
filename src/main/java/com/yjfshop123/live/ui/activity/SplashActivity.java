package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.sh.sdk.shareinstall.ShareInstall;

import com.sh.sdk.shareinstall.listener.AppGetWakeUpListener;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.ILoginView;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareData;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.AdLaunchScreenResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.TTAdManagerHolder;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.fragment.SmallVideoFragmentNew;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivityH implements IPermissions, ILoginView {

    @BindView(R.id.splash_container)
    FrameLayout mSplashContainer;
    @BindView(R.id.splash_img)
    ImageView splashImg;
    @BindView(R.id.one)
    TextView one;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.splash_btn)
    Button splashBtn;
    private Context mContext;

    private boolean isResume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        is_need_check_verfy = false;
        mContext = this;
        StatusBarUtil.StatusBarDarkMode(this);


        PermissionsUtils.initPermission(mContext);
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) splashBtn.getLayoutParams();
        //获取当前控件的布局对象
        params.topMargin = (int) (SystemUtils.getStatusBarHeight(this) * 1.2);//设置当前控件布局的高度
        splashBtn.setLayoutParams(params);
        //mHandler.postDelayed(runnable, 5000);
        isLoginNew = UserInfoUtil.getInfoComplete();
        splashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActi();
            }
        });
        App.shareData=null;
        ShareInstall.getInstance().getWakeUpParams(getIntent(), new AppGetWakeUpListener() {
            @Override
            public void onGetWakeUpFinish(String info) {
                NLog.e("TAGTAG", "ShareInstall_info = " + info);
                try {
                    App.shareData = new Gson().fromJson(info, ShareData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private String mCodeId = "887416882";
    //private String mCodeId = "887417738";

    private static final int AD_TIME_OUT = 6000;
    private boolean mIsExpress = false; //是否请求模板广告
    private TTAdNative mTTAdNative;
    int isLoginNew = 0;

    private void startApp() {
        if(isStartCheck)return;

        NLog.d(TAG, UserInfoUtil.getInfoComplete() + "");
        isStartCheck=true;
        if (UserInfoUtil.getInfoComplete() != 0) {
            OKHttpUtils.getInstance().getRequest("app/user/checkUserLogin", "", new com.yjfshop123.live.Interface.RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                   if(errCode==1001){
                       UserInfoUtil.setMiTencentId("");
                       UserInfoUtil.setMiPlatformId("");
                       UserInfoUtil.setToken_InfoComplete("", 0);
                       UserInfoUtil.setFromUid("");
                       isLoginNew=0;
                       goToMain();
                   }else {
                       LoginHelper loginHelper = new LoginHelper(SplashActivity.this);
                       loginHelper.loginSDK(UserInfoUtil.getMiTencentId(), UserInfoUtil.getSignature());
                   }
                }

                @Override
                public void onSuccess(String result) {
                    LoginHelper loginHelper = new LoginHelper(SplashActivity.this);
                    loginHelper.loginSDK(UserInfoUtil.getMiTencentId(), UserInfoUtil.getSignature());
                }
            });

        } else {
            goToMain();
        }
    }

    private void goToMain() {
        if (!isResume) {
            return;
        }
        loadSplashAd();
        // mHandler.postDelayed(runnable, 1000);
    }

    long timeleft = 5000;
    protected Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            splashBtn.setText("跳过(" + (int) (timeleft / 1000) + ")");
            if (timeleft > 0) {
                timeleft = timeleft - 1000;
                mHandler.postDelayed(runnable, 1000);
            } else
                startActi();
        }
    };

//    private void startActi() {
//        Intent intent = new Intent(this, AdverActivityNew.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        // isResume = false;
        // mHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        //  mHandler.postDelayed(runnable, 1000);
        PermissionsUtils.onResumeSplash(this, this);
    }

    @Override
    public void allPermissions() {
        createFloder();
        startApp();

        //app/public/configIssue
        OKHttpUtils.getInstance().getRequest("app/ad/adLaunchScreen", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    AdLaunchScreenResponse response = JsonMananger.jsonToBean(result, AdLaunchScreenResponse.class);
                    if (response.getList().size() == 0) {
                        return;
                    }
                    UserInfoUtil.setLaunch(response.getList().get(0).getImg(), response.getList().get(0).getLink());
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, false);
    }

    @Override
    public void onLoginSDKSuccess() {
        App.initPush();
        goToMain();
    }

    private boolean isLogin = false;

    @Override
    public void onLoginSDKFailed(int i, String s) {
        //登录失败 重复登录一次
        if (isLogin) {
            NToast.shortToast(mContext, "登录失败");
            return;
        }
        isLogin = true;
        goToMain();
    }

    private void createFloder() {
        File file0 = new File(Const.REAL_PIC_PATH);
        if (!file0.isDirectory()) {
            file0.mkdirs();
        }
        File file = new File(Const.voiceDir);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        File file1 = new File(Const.cropDir);
        if (!file1.isDirectory()) {
            file1.mkdirs();
        }
        File file2 = new File(Const.downloadDir);
        if (!file2.isDirectory()) {
            file2.mkdirs();
        }

    }

    private static final String TAG = "AdvertActivity";
    boolean isStartCheck = false;

    private void startActi() {

        if (!isResume) {
            return;
        }
        if (isLoginNew == 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //已登录检查是否实名认证
            int status = UserInfoUtil.getAuthStatus();

            if (status == 0) {//未提交
                Intent intent = new Intent();
                intent.setClass(this, ShiMingRenZhengActivity.class);
                startActivity(intent);
            } else if (status == 10) { //审核失败
                Intent intent = new Intent();
                intent.setClass(this, ShiMingRenZhengActivity.class);
                startActivity(intent);
            } else if (status == 2) {//通过
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else if (status == 1) {//审核中
                Intent intent = new Intent(this, ShiMingRenZhengActivity.class);

                startActivity(intent);
            }
            finish();
        }

    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        Log.d("splashLoadAd", "开始请求广告");
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            //float expressViewWidth = UIUtils.getScreenWidthDp(this);
            //float expressViewHeight = UIUtils.getHeight(this);
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    //view宽高等于图片的宽高
                    .setExpressViewAcceptedSize(1080, 1920)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    // .setImageAcceptedSize(1080, 1920)
                    //.setImageAcceptedSize((int)(SystemUtils.sp2px(this,SystemUtils.getScreenWidth(this))),(int)(SystemUtils.sp2px(this,SystemUtils.getScreenHeight(this, false))))
                     .setImageAcceptedSize(SystemUtils.getScreenWidth(this) , SystemUtils.getScreenHeight(this,false) )
                    .build();
        }

        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                NLog.d(TAG, String.valueOf(message));
                // showToast(message);
                startActi();
            }

            @Override
            @MainThread
            public void onTimeout() {
                NLog.d(TAG, "onTimeout");
                startActi();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view);
                    if (mHandler != null && runnable != null) mHandler.removeCallbacks(runnable);
                    timeleft = 5000;
                    mHandler.postDelayed(runnable, 0);
                    splashBtn.setVisibility(View.VISIBLE);
                    top.setVisibility(View.GONE);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    ad.setNotAllowSdkCountdown();
                } else {
                    startActi();
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        startActi();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        startActi();
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {


                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {

                        }
                    });
                }
            }
        }, AD_TIME_OUT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null && runnable != null) mHandler.removeCallbacks(runnable);
        if (mSplashContainer != null) mSplashContainer.removeAllViews();
    }
}
