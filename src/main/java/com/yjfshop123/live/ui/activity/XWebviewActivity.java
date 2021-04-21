package com.yjfshop123.live.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.game.adapter.GamePopWindow;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XWebviewActivity extends BaseActivity implements CosXmlUtils.OSSResultListener{

    private String mPrevUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mWebViewTitle;
    private String zbUserId;
    private String mToken;

    //TODO Game
    private ImageView mControlIV;
    private boolean isGame = false;

    public XWebviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        //TODO Game
        Intent intent = this.getIntent();
        String url = intent.getStringExtra("url");
        if (url.contains("gameLogin")) {
            isStatusBar = true;
        }

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.x_ac_webview);

        zbUserId = UserInfoUtil.getMiTencentId();

        //TODO Game
        if (url.contains("gameLogin")) {
            setHeadVisibility(View.GONE);
            mControlIV = findViewById(R.id.control_iv);
            mControlIV.setVisibility(View.VISIBLE);
            isGame = true;
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            setHeadVisibility(View.VISIBLE);
        }

        mWebViewTitle = findViewById(R.id.tv_title_center);

        this.mWebView = this.findViewById(R.id.x_webview);
        this.mProgressBar = findViewById(R.id.x_web_progressbar);

        this.mWebView.setVerticalScrollbarOverlay(true);
        this.mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.mWebView.getSettings().setLoadWithOverviewMode(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);

        this.mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.mWebView.setScrollContainer(true);
        this.mWebView.setVerticalScrollBarEnabled(true);
        this.mWebView.setHorizontalScrollBarEnabled(true);
        //
        this.mWebView.addJavascriptInterface(new JavaScriptObject(), "android");
        //
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 11) {
            this.mWebView.getSettings().setDisplayZoomControls(false);
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.mWebView.setWebContentsDebuggingEnabled(true);
//        }

        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.setWebViewClient(new RongWebviewClient());
        this.mWebView.setWebChromeClient(new RongWebChromeClient());
        this.mWebView.setDownloadListener(new RongWebViewDownLoadListener());
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        this.mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        Uri data = intent.getData();

        //##
        Map<String, String> headers = new HashMap();
        mToken = UserInfoUtil.getToken();
        if (TextUtils.isEmpty(mToken)) {
            headers.put("XX-Token", "");
        } else {
            headers.put("XX-Token", mToken);
        }
        headers.put("XX-Device-Type", "android");
        headers.put("XX-Api-Version", App.versionName);
        headers.put("XX-Store-Channel", App.channel_id);
        //##

        if (url != null) {
            this.mPrevUrl = url;
            this.mWebView.loadUrl(url, headers);
        } else if (data != null) {
            this.mPrevUrl = data.toString();
            this.mWebView.loadUrl(data.toString());
        }

        control();
        initOss();
    }

    private String javaScriptContent;
    private class JavaScriptObject {
        @JavascriptInterface
        public void sendMessage(String str) {
            Message message = new Message();
            message.what = 1000;
            message.obj = str;
            mHandler.sendMessage(message);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000){
                try {
                    String str = (String) msg.obj;
                    JSONObject jso = new JSONObject(str);
                    String callback = jso.getString("callback");
                    if (callback.equals("receiveImgUrl")){
                        //上传图片
                        javaScriptContent = str;
                        chooseImg();
                    }else if (callback.equals("openURL")){
                        //打开外部浏览器
                        String url = jso.getJSONObject("param").getString("url");

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }else if (callback.equals("canOpenURL")){
                        //例 打开QQ 微信
                        String url = jso.getJSONObject("param").getString("url");
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        ComponentName componentName = intent.resolveActivity(getPackageManager());
                        boolean result;
                        if (componentName != null){
                            result = true;
                        }else {
                            result = false;
                        }
                        JSONObject jso_ = new JSONObject();
                        jso_.put("result", result);
                        jso.put("data", jso_);
                        mWebView.loadUrl("javascript:canOpenURL(" + jso.toString() + ")");
                    }else if (callback.equals("closeWeb")){
                        //关闭 webview
                        finish();
                    }else if (callback.equals("openWeb")){
                        //开启新webView
                        String url = jso.getJSONObject("param").getString("url");
                        if (!TextUtils.isEmpty(url)) {
                            Intent intent = new Intent("io.xchat.intent.action.webview");
                            intent.setPackage(getPackageName());
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    }else if (callback.equals("openNative")){
                        String name = jso.getJSONObject("param").getString("name");
                        if (name.equals("RealNameAuthentication")) {
                            //实名认证
                            Intent intent = new Intent();
                            intent.setClass(mContext, ShiMingRenZhengActivity.class);
                            startActivity(intent);
                        }else if (name.equals("TalentCertification")){
                            //达人认证
                            Intent intent = new Intent();
                            intent.setClass(mContext, DaRenRenZhengActivity.class);
                            startActivity(intent);
                        }else if (name.equals("WithdrawalVC")){
                            Intent intent = new Intent();
                            intent.setClass(mContext, TiXianActivity1.class);
                            intent.putExtra("type", 3);
                            startActivity(intent);
                        }
                    }else if (callback.equals("showLoading")){
                        LoadDialog.show(XWebviewActivity.this);
                    }else if (callback.equals("hideLoading")){
                        LoadDialog.dismiss(XWebviewActivity.this);
                    }else if (callback.equals("alertPrompt")){
                        String title = jso.getJSONObject("param").getString("title");
                        DialogUitl.showSimpleHintDialog(XWebviewActivity.this, getString(R.string.prompt), getString(R.string.wszl_zhidao), getString(R.string.cancel),
                                title, true, true,
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {
                                    }
                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        dialog.dismiss();
                                    }
                                });
                    }else if (callback.equals("getUserInfo")){
                        JSONObject jso_ = new JSONObject();
                        if (TextUtils.isEmpty(mToken)) {
                            jso_.put("XX-Token", "");
                        } else {
                            jso_.put("XX-Token", mToken);
                        }
                        jso_.put("XX-Device-Type", "android");
                        jso_.put("XX-Api-Version", App.versionName);
                        jso_.put("XX-Store-Channel", App.channel_id);
                        jso_.put("userid", zbUserId);
                        jso_.put("domain", Const.getDomain() + "/");
                        jso_.put("cosdomain", Const.domain_url);
                        jso_.put("coin_zh", "金蛋");
                        jso.put("data", jso_);
                        mWebView.loadUrl("javascript:getUserInfo(" + jso.toString() + ")");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void onHeadLeftButtonClick(View v) {
        if (mWebView.canGoBack()) {
            this.mWebView.goBack();
        }else {
            if (!isGame){
                finish();
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                this.mWebView.goBack();
                return true;
            }else {
                if (isGame){
                    return true;
                }else {
                    return super.onKeyDown(keyCode, event);
                }
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean checkIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);
        return apps.size() > 0;
    }

    private class RongWebViewDownLoadListener implements DownloadListener {
        private RongWebViewDownLoadListener() {
        }

        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            if (checkIntent(XWebviewActivity.this, intent)) {
                startActivity(intent);
                if (uri.getScheme().equals("file") && uri.toString().endsWith(".txt")) {
                    finish();
                }
            }

        }
    }

    private class RongWebChromeClient extends WebChromeClient {
        private RongWebChromeClient() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                mProgressBar.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }

        public void onReceivedTitle(WebView view, String title) {
            if (mWebViewTitle != null /*&& TextUtils.isEmpty(mWebViewTitle.getText())*/) {
                mWebViewTitle.setVisibility(View.VISIBLE);
                mWebViewTitle.setText(title);
            }

        }
    }

    private class RongWebviewClient extends WebViewClient {
        private RongWebviewClient() {
        }

        @SuppressLint("WrongConstant")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mPrevUrl != null) {
                if (!mPrevUrl.equals(url)) {
                    if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://") && !url.toLowerCase().startsWith("file:///android_asset")) {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
//                        intent.setFlags(268435456);
//                        intent.setFlags(536870912);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(PendingIntent.FLAG_NO_CREATE);

                        try {
                            startActivity(intent);
                        } catch (Exception var6) {
                            var6.printStackTrace();
                        }

                        return true;
                    } else {
                        mPrevUrl = url;
                        mWebView.loadUrl(url);
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                mPrevUrl = url;
                mWebView.loadUrl(url);
                return true;
            }
        }
    }

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    private static final int REQUEST_CODE_SELECT = 10006;
    private String mPath;

    private void initOss(){
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        uploadDialog = new UploadDialog(this);

        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    private void chooseImg(){
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
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();
        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_CODE_SELECT);
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        String url = response.get(0).getObject();
        if (url.contains(BuildConfig.APPLICATION_ID)) {
            //上传错误
            mWebView.loadUrl("javascript:getErrorData(" + javaScriptContent + ")");
            return;
        }

        //上传成功 url
        try {
            JSONObject jso_ = new JSONObject();
            jso_.put("imgUri", url);
            JSONObject jso = new JSONObject(javaScriptContent);
            jso.put("data", jso_);
            mWebView.loadUrl("javascript:receiveImgUrl(" + jso.toString() + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    private void uploadData(){
        if (uploadOssUtils.responses != null){
            uploadOssUtils.responses.clear();
        }
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.uploadData(mPath, "image", 5, zbUserId, uploadDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT){
            if (data != null) {
                mPath = data.getStringArrayListExtra("result").get(0);
                uploadData();
            }
        }
    }

    //TODO Game
    private boolean isOnPause;

    @Override
    protected void onResume() {
        super.onResume();
        if (isGame){
            getSwipeBackLayout().setEdgeSize(0);//禁止侧滑
        }
        try {
            if (isOnPause) {
                if (mWebView != null) {
                    mWebView.onResume();
                }
                isOnPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mWebView != null) {
                mWebView.onPause();
                isOnPause = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {

            mWebView.clearCache(true);
            mWebView.clearHistory();

            mWebView.removeAllViews();
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }

        isOnPause = false;

        super.onDestroy();
    }

    private boolean touchResult = false;
    private int mStartX1, mStartY1, mLastX1, mLastY1;
    private int mScreenHeight, subWidth_28;

    private void control(){
        if (mControlIV == null){
            return;
        }
        mScreenHeight = CommonUtils.getScreenHeight(this) - CommonUtils.dip2px(mContext, 100);
        subWidth_28 = CommonUtils.dip2px(mContext, 40);
        mControlIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow();
            }
        });
        mControlIV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchResult = false;
                        mStartX1 = mLastX1 = (int) event.getRawX();
                        mStartY1 = mLastY1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int left, top, right, bottom;
                        int dx = (int) event.getRawX() - mLastX1;
                        int dy = (int) event.getRawY() - mLastY1;
                        left = v.getLeft() + dx;
                        if (left < 0) {
                            left = 0;
                        }
                        right = left + v.getWidth();
                        if (right > screenWidth_) {
                            right = screenWidth_;
                            left = right - v.getWidth();
                        }
                        top = v.getTop() + dy;
                        if (top < 0) {
                            top = 0;
                        }
                        bottom = top + v.getHeight();
                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        mLastX1 = (int) event.getRawX();
                        mLastY1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(subWidth_28, subWidth_28);
                        layoutParams.setMargins(v.getLeft(), v.getTop(), 0, 0);
                        //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                        v.setLayoutParams(layoutParams);
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (Math.abs(endX - mStartX1) > 5 || Math.abs(endY - mStartY1) > 5) {
                            //防止点击的时候稍微有点移动点击事件被拦截了
                            touchResult = true;
                        }
                        break;
                }
                return touchResult;
            }
        });
    }

    private void popWindow(){
        GamePopWindow gamePopWindow = new GamePopWindow(mContext, new OnItemClickListener() {
            @Override
            public void onItemClick(Object bean, int position) {
                switch (position){
                    case 1:
                        Intent intent = new Intent(mContext, MyWalletActivity1.class);
                        intent.putExtra("currentItem", 0);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        break;
                    case 2:
                        finish();
//                        if (mWebView.canGoBack()) {
//                            mWebView.goBack();
//                        }else {
//                            finish();
//                        }
                        break;
                    case 3:
                        if (mWebView != null) {
                            mWebView.reload();
                        }
                        break;
                }
            }
        });
        gamePopWindow.showPopupWindow(mControlIV);
    }
}
