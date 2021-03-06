package com.yjfshop123.live;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.faceunity.authpack;
import com.ledong.lib.leto.Leto;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.authid.SignUtil;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareData;
import com.yjfshop123.live.utils.MyUtils;

import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.TTAdManagerHolder;
import com.yjfshop123.video_shooting.LibraryApp;
import com.yjfshop123.live.message.Foreground;
import com.yjfshop123.live.message.MessageEvent;
import com.yjfshop123.live.message.PushUtil;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.utils.imageloader.ImageLoader;
import com.yjfshop123.live.server.utils.imageloader.ImageLoaderConfiguration;
import com.yjfshop123.live.server.utils.imageloader.QueueProcessingType;
import com.yjfshop123.live.socket.ForegroundCallbacks;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.ConfigUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.faceunity.FURenderer;
import com.heytap.mcssdk.callback.PushAdapter;
import com.heytap.mcssdk.callback.PushCallback;
import com.huawei.android.pushagent.PushManager;
import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.listeners.IImageLoader;
import com.sh.sdk.shareinstall.ShareInstall;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMOfflinePushToken;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.imsdk.utils.IMFunc;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.ugc.TXUGCBase;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends LibraryApp {

    public static ConfigUtil configUtil;
    private static App instance;
    public static ShareData shareData;
    public static String currentWeizhi = "";

    protected static Handler handler;
    protected static int mainThreadId;
    public static boolean is_check_update = false;
    public static String channel_id = "official";
    public static String versionName = "1.0.0";

    public static App getInstance() {
        return instance;
    }

    static {
        //???????????????Header?????????
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("????????? %s"));//???????????????Header???????????? ???????????????Header
            }
        });
        //???????????????Footer?????????
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //???????????????Footer???????????? BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            webviewSetPath(this);
        }
        MyUtils.init(this);
        System.loadLibrary("nama");
        System.loadLibrary("FxckFU");
        instance = this;

        if (configUtil == null) {
            configUtil = ConfigUtil.ins(this);
        }
        NLog.setDebug(Const.IS_DEBUG);
        CrashReport.initCrashReport(getApplicationContext(), Const.buglyAppid, false);//?????????????????? ?????? false
        Foreground.init(this);

        SealAppContext.init(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm.lm")
                .schemaVersion(102)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        initImageLoader(getApplicationContext());

        channel_id = Const.getMetaDataString("CHANNEL_ID");
        versionName = CommonUtils.packageName(instance);
        ShareInstall.getInstance().init(getApplicationContext());

        /*if (SessionWrapper.isMainProcess(getInstance())) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    notification.doNotify(getInstance(), R.drawable.logo);*/
        //??????APP ????????????
//                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
//                        notification.doNotify(getInstance(), R.drawable.logo);
//                    }
                /*}
            });
        }*/

        configPandaEmoView();

        iSDKID();
        initUm();

        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.getInstance().setLicence(instance, Const.license_url, Const.license_key);
        TXUGCBase.getInstance().setLicence(instance, Const.license_ugc_url, Const.license_ugc_key);
        //??????
        FURenderer.initFURenderer(this);
        //SystemUtils.setClipboard(this,Arrays.toString(authpack.A()));
        initAppStatusListener();//WebSocket
        //???????????????
        //getData();
        TTAdManagerHolder.init(this);
        //?????????
        //???????????????
        Leto.init(this);
//        MenuActionSheetItem item = new MenuActionSheetItem("??????", "gy");
//        MenuActionSheetItem item1 = new MenuActionSheetItem("??????????????????url", "hqdqym");
//        MenuActionSheetItem item2 = new MenuActionSheetItem("?????????????????????????????????", "gotoTestPage");
//        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
//        sheetItems.add(item);
//        sheetItems.add(item1);
//        sheetItems.add(item2);
//        DCSDKInitConfig config1 = new DCSDKInitConfig.Builder()
//                .setCapsule(true)
//                .setMenuDefFontSize("16px")
//                .setMenuDefFontColor("#ff00ff")
//                .setMenuDefFontWeight("normal")
//                .setMenuActionSheetItems(sheetItems)
//                .setEnableBackground(true)//??????????????????
//                .build();
//        DCUniMPSDK.getInstance().initialize(this, config1, new DCUniMPSDK.IDCUNIMPPreInitCallback() {
//            @Override
//            public void onInitFinished(boolean b) {
//                Log.i("unimp","onInitFinished----"+b);
//            }
//        });
    }

    public static Context getContext() {
        return instance;
    }

    private void initUm() {
        //???????????????????????????
        PlatformConfig.setWeixin(Const.wx_app_id, Const.wx_app_secret);
        PlatformConfig.setQQZone(Const.qq_app_id, Const.qq_app_secret);
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMConfigure.init(getInstance(), Const.getMetaDataString("UMENG_APPKEY"), channel_id, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMShareAPI.get(instance).setShareConfig(config);
    }

    //????????????
    private void iSDKID() {
        // ????????????????????????????????????
        if (SessionWrapper.isMainProcess(getInstance())) {
            TIMSdkConfig config = new TIMSdkConfig(Const.im_app_id);
//            ????????????
//            config.enableLogPrint(true)
//                    .setLogLevel(TIMLogLevel.DEBUG)
//                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/AAAIM/");

//            config.setAccoutType(accountType);
            TIMManager.getInstance().init(getInstance(), config);

            //??????????????????
            TIMUserConfig userConfig = new TIMUserConfig()
                    //???????????????????????????????????????
                    .setUserStatusListener(new TIMUserStatusListener() {
                        @Override
                        public void onForceOffline() {
                            //????????????????????????
                            NLog.e("TAGTAG", "onForceOffline");

                            NToast.shortToast(instance, "???????????????????????????????????????~");
                            BroadcastManager.getInstance(getInstance()).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                        }

                        @Override
                        public void onUserSigExpired() {
                            //???????????????????????????????????? userSig ???????????? SDK
                            NLog.e("TAGTAG", "onUserSigExpired");

                            TIMManager.getInstance().login(UserInfoUtil.getMiTencentId(), UserInfoUtil.getSignature(), new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    })
                    //?????????????????????????????????
                    .setConnectionListener(new TIMConnListener() {
                        @Override
                        public void onConnected() {
                            NLog.e("TAGTAG", "onConnected");
                        }

                        @Override
                        public void onDisconnected(int code, String desc) {
                            NLog.e("TAGTAG", "onDisconnected");
                        }

                        @Override
                        public void onWifiNeedAuth(String name) {
                            NLog.e("TAGTAG", "onWifiNeedAuth");
                        }
                    });

            //????????????????????????
            userConfig = new TIMUserConfigMsgExt(userConfig)
                    //??????????????????
//                    .enableStorage(false)
                    //????????????????????????
                    .enableReadReceipt(true);
            //?????????????????????????????????????????????
            TIMManager.getInstance().setUserConfig(userConfig);

            MessageEvent.getInstance();
            PushUtil.getInstance();
        }
    }

    //????????????
    public static void initPush() {
        //????????????????????????
        TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
        settings.setEnabled(true);
        TIMManager.getInstance().setOfflinePushSettings(settings);


        //??????????????????????????????????????????????????????????????????
        if (IMFunc.isBrandXiaoMi()) {
            //????????????????????????
            //appid appkey
            MiPushClient.registerPush(getContext(), Const.getMetaDataString("MI_APPID"), Const.getMetaDataString("MI_APPKEY"));
        } else if (IMFunc.isBrandHuawei()) {
            //???????????????????????? token
            PushManager.requestToken(getContext());
        } else if (IMFunc.isBrandMeizu()) {
            //????????????
            //appid appkey
            com.meizu.cloud.pushsdk.PushManager.register(getContext(), Const.getMetaDataString("MEIZU_APPID"), Const.getMetaDataString("MEIZU_KEY"));
        } else if (IMFunc.isBrandOppo()) {
            /**
             * ??????OPush????????????
             * @param applicatoinContext??????????????????app???applicationcontet
             * @param appKey ?????????????????????????????????????????????AppKey?????????
             * @param appSecret ???AppSecret?????????
             * @param pushCallback SDK???????????????????????????????????????????????????????????????    	 * ???PushAdapter???????????????????????????????????????
             */
            //setPushCallback??????????????????callback
            com.heytap.mcssdk.PushManager.getInstance().register(getContext(), Const.getMetaDataString("OPPO_KEY"), Const.getMetaDataString("OPPO_SECRET"), mPushCallback);
        } else if (IMFunc.isBrandVivo()) {
            //VIVO ?????????
            PushClient.getInstance(getContext()).initialize();
            // ???????????????????????????????????????????????????:
            PushClient.getInstance(getContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    //??????????????????
                }
            });
        }
    }

    private static PushCallback mPushCallback = new PushAdapter() {
        @Override
        public void onRegister(int code, String s) {
            if (code == 0) {
                NLog.e("PushMessageService", "????????????" + "registerId:" + s);
                long bussid = Const.getMetaDataLong("OPPO_BUSS_ID");
                TIMOfflinePushToken token = new TIMOfflinePushToken(bussid, s);
                token.setBussid(bussid);
                token.setToken(s);
                TIMManager.getInstance().setOfflinePushToken(token, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        NLog.e("PushMessageService", "setOfflinePushToken failed, code: " + i + "|msg: " + s);
                    }

                    @Override
                    public void onSuccess() {
                        NLog.e("PushMessageService", "setOfflinePushToken succ~OPPO");
                    }
                });
            } else {
                NLog.e("PushMessageService", "????????????" + "code=" + code + ",msg=" + s);
            }
        }
    };

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
//        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(500 * 1024 * 1024); // 500 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * ????????????handler
     *
     * @return ??????handler
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * ???????????????id
     *
     * @return ?????????id
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }

    private void configPandaEmoView() {
        new PandaEmoManager.Builder()
                .with(getApplicationContext()) // ?????? Context
                .configFileName("emoji.xml")// ??????????????????
                .emoticonDir("face") // asset ?????????????????????????????????asset??????> configFileName ???????????????,?????????????????????
                .sourceDir("images") // ?????? emoji ??????????????????????????????emoticonDir ???????????????????????????,?????????????????????
                .showAddTab(false)//tab???????????????????????????
                .showStickers(false)//tab?????????????????????????????????
                .showSetTab(false)//tab???????????????????????????
                .defaultBounds(22)//emoji ???????????????????????????
                .cacheSize(100)//???????????????????????? LruCache ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                .defaultTabIcon(R.drawable.ic_default)//emoji??????Tab?????????
                .emojiColumn(7)//???????????????????????????
                .emojiRow(3)//???????????????????????????
                .stickerRow(2)//?????????????????????????????????
                .stickerColumn(4)//?????????????????????????????????
                .maxCustomStickers(30)//??????????????????????????????
                .imageLoader(new IImageLoader() {
                    @Override
                    public void displayImage(String path, ImageView imageView) { // ???????????????????????????????????????
                        Glide.with(getApplicationContext())
                                .load(path)
                                .into(imageView);
                    }
                })
                .build(); //?????? PandaEmoManager ??????
    }

    private void initAppStatusListener() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(instance)) {
                Intent goToSettings = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                goToSettings.setData(Uri.parse("package:" + getPackageName()));
                goToSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToSettings);
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                super.onAvailable(network);
                                //????????????
                                SocketUtil.getInstance().reconnect_();
                            }
                        });
            }
        } catch (Exception e) {
        }

        //???????????????
        ForegroundCallbacks.init(this).addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                NLog.d("TAGTAG_WebSocekt", "???????????????");
                //??????????????????
                SocketUtil.getInstance().reconnect_();
            }

            @Override
            public void onBecameBackground() {
                NLog.d("TAGTAG_WebSocekt", "???????????????");
            }
        });
    }

    @RequiresApi(api = 28)
    private void webviewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!getApplicationContext().getPackageName().equals(processName)) {//?????????????????????????????????
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }


}
