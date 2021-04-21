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
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
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
        CrashReport.initCrashReport(getApplicationContext(), Const.buglyAppid, false);//腾讯错误统计 线上 false
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
        //打开APP 消息处理
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
        //美颜
        FURenderer.initFURenderer(this);
        //SystemUtils.setClipboard(this,Arrays.toString(authpack.A()));
        initAppStatusListener();//WebSocket
        //穿山甲广告
        //getData();
        TTAdManagerHolder.init(this);
        //小游戏
        //初始化启动
        Leto.init(this);
//        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");
//        MenuActionSheetItem item1 = new MenuActionSheetItem("获取当前页面url", "hqdqym");
//        MenuActionSheetItem item2 = new MenuActionSheetItem("跳转到宿主原生测试页面", "gotoTestPage");
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
//                .setEnableBackground(true)//开启后台运行
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
        //友盟社会化分享登录
        PlatformConfig.setWeixin(Const.wx_app_id, Const.wx_app_secret);
        PlatformConfig.setQQZone(Const.qq_app_id, Const.qq_app_secret);
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMConfigure.init(getInstance(), Const.getMetaDataString("UMENG_APPKEY"), channel_id, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMShareAPI.get(instance).setShareConfig(config);
    }

    //登录之前
    private void iSDKID() {
        // 判断仅在主线程进行初始化
        if (SessionWrapper.isMainProcess(getInstance())) {
            TIMSdkConfig config = new TIMSdkConfig(Const.im_app_id);
//            测试日志
//            config.enableLogPrint(true)
//                    .setLogLevel(TIMLogLevel.DEBUG)
//                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/AAAIM/");

//            config.setAccoutType(accountType);
            TIMManager.getInstance().init(getInstance(), config);

            //基本用户配置
            TIMUserConfig userConfig = new TIMUserConfig()
                    //设置用户状态变更事件监听器
                    .setUserStatusListener(new TIMUserStatusListener() {
                        @Override
                        public void onForceOffline() {
                            //被其他终端踢下线
                            NLog.e("TAGTAG", "onForceOffline");

                            NToast.shortToast(instance, "您在其他设备登录，被迫下线~");
                            BroadcastManager.getInstance(getInstance()).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                        }

                        @Override
                        public void onUserSigExpired() {
                            //用户签名过期了，需要刷新 userSig 重新登录 SDK
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
                    //设置连接状态事件监听器
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

            //消息扩展用户配置
            userConfig = new TIMUserConfigMsgExt(userConfig)
                    //禁用消息存储
//                    .enableStorage(false)
                    //开启消息已读回执
                    .enableReadReceipt(true);
            //将用户配置与通讯管理器进行绑定
            TIMManager.getInstance().setUserConfig(userConfig);

            MessageEvent.getInstance();
            PushUtil.getInstance();
        }
    }

    //登录之后
    public static void initPush() {
        //全局离线推送配置
        TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
        settings.setEnabled(true);
        TIMManager.getInstance().setOfflinePushSettings(settings);


        //判断厂商品牌，根据不同厂商选择不同的推送服务
        if (IMFunc.isBrandXiaoMi()) {
            //注册小米推送服务
            //appid appkey
            MiPushClient.registerPush(getContext(), Const.getMetaDataString("MI_APPID"), Const.getMetaDataString("MI_APPKEY"));
        } else if (IMFunc.isBrandHuawei()) {
            //请求华为推送设备 token
            PushManager.requestToken(getContext());
        } else if (IMFunc.isBrandMeizu()) {
            //魅族推送
            //appid appkey
            com.meizu.cloud.pushsdk.PushManager.register(getContext(), Const.getMetaDataString("MEIZU_APPID"), Const.getMetaDataString("MEIZU_KEY"));
        } else if (IMFunc.isBrandOppo()) {
            /**
             * 注册OPush推送服务
             * @param applicatoinContext必须传入当前app的applicationcontet
             * @param appKey 在开发者网站上注册时生成的，与AppKey相对应
             * @param appSecret 与AppSecret相对应
             * @param pushCallback SDK操作的回调，如不需要使用所有回调接口，可传    	 * 入PushAdapter，选择需要的回调接口来重写
             */
            //setPushCallback接口也可设置callback
            com.heytap.mcssdk.PushManager.getInstance().register(getContext(), Const.getMetaDataString("OPPO_KEY"), Const.getMetaDataString("OPPO_SECRET"), mPushCallback);
        } else if (IMFunc.isBrandVivo()) {
            //VIVO 初始化
            PushClient.getInstance(getContext()).initialize();
            // 当需要打开推送服务时，调用以下代码:
            PushClient.getInstance(getContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    //开关状态处理
                }
            });
        }
    }

    private static PushCallback mPushCallback = new PushAdapter() {
        @Override
        public void onRegister(int code, String s) {
            if (code == 0) {
                NLog.e("PushMessageService", "注册成功" + "registerId:" + s);
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
                NLog.e("PushMessageService", "注册失败" + "code=" + code + ",msg=" + s);
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
     * 获取全局handler
     *
     * @return 全局handler
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * 获取主线程id
     *
     * @return 主线程id
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }

    private void configPandaEmoView() {
        new PandaEmoManager.Builder()
                .with(getApplicationContext()) // 传递 Context
                .configFileName("emoji.xml")// 配置文件名称
                .emoticonDir("face") // asset 下存放表情的目录路径（asset——> configFileName 之间的路径,结尾不带斜杠）
                .sourceDir("images") // 存放 emoji 表情资源文件夹路径（emoticonDir 图片资源之间的路径,结尾不带斜杠）
                .showAddTab(false)//tab栏是否显示添加按钮
                .showStickers(false)//tab栏是否显示贴图切换按键
                .showSetTab(false)//tab栏是否显示设置按钮
                .defaultBounds(22)//emoji 表情显示出来的宽高
                .cacheSize(100)//加载资源到内存时 LruCache 缓存大小，最小必须大于表情总数或者两页表情的数（否则会在显示时前面资源被回收）
                .defaultTabIcon(R.drawable.ic_default)//emoji表情Tab栏图标
                .emojiColumn(7)//单页显示表情的列数
                .emojiRow(3)//单页显示表情的行数
                .stickerRow(2)//单页显示贴图表情的行数
                .stickerColumn(4)//单页显示贴图表情的列数
                .maxCustomStickers(30)//允许添加的收藏表情数
                .imageLoader(new IImageLoader() {
                    @Override
                    public void displayImage(String path, ImageView imageView) { // 加载贴图表情的图片加载接口
                        Glide.with(getApplicationContext())
                                .load(path)
                                .into(imageView);
                    }
                })
                .build(); //构建 PandaEmoManager 单利
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
                                //网络切换
                                SocketUtil.getInstance().reconnect_();
                            }
                        });
            }
        } catch (Exception e) {
        }

        //从后台切回
        ForegroundCallbacks.init(this).addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                NLog.d("TAGTAG_WebSocekt", "切换到前台");
                //应用回到前台
                SocketUtil.getInstance().reconnect_();
            }

            @Override
            public void onBecameBackground() {
                NLog.d("TAGTAG_WebSocekt", "切换到后台");
            }
        });
    }

    @RequiresApi(api = 28)
    private void webviewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!getApplicationContext().getPackageName().equals(processName)) {//判断不等于默认进程名称
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
