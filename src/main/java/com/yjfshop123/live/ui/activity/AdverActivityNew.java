package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.yjfshop123.live.R;
import com.yjfshop123.live.utils.CommonUtils;

import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.TTAdManagerHolder;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.utils.TToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdverActivityNew extends BaseActivityH {

    @BindView(R.id.splash_btn)
    Button splashBtn;
    int isLogin = 0;
    @BindView(R.id.splash_container)
    FrameLayout mSplashContainer;
    private String mCodeId = "887416882";
    private static final int AD_TIME_OUT =5000;
    private boolean mIsExpress = false; //是否请求模板广告
    private TTAdNative mTTAdNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_new);
        ButterKnife.bind(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)  mSplashContainer.getLayoutParams();
        //获取当前控件的布局对象
        params.topMargin = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        mSplashContainer.setLayoutParams(params);
        isLogin = UserInfoUtil.getInfoComplete();
        //mHandler.postDelayed(runnable, 5000);
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        loadSplashAd();
    }



    protected Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActi();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        if(mHandler!=null&&runnable!=null)
        mHandler.removeCallbacks(runnable);
    }

    private boolean is_login;

    private void startActi() {

        if (!isResume) {
            return;
        }
        if (isLogin == 0) {
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

    boolean isResume = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSplashContainer!=null) mSplashContainer.removeAllViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;

    }

    @OnClick(R.id.splash_btn)
    public void onViewClicked() {
        startActi();
    }

    private static final String TAG = "AdvertActivity";

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
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
                    .setImageAcceptedSize(1080, 1920)
                    .build();
        }

        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, String.valueOf(message));
               // showToast(message);
                startActi();
            }

            @Override
            @MainThread
            public void onTimeout() {
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
                if (view != null && mSplashContainer != null && !AdverActivityNew.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
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
                        startActi();                    }
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

    private void showToast(String msg) {

    }
}
