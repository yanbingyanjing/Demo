package com.yjfshop123.live.live.live.common.widget.other;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.ui.activity.DaRenRenZhengActivity;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private String mPrevUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mWebViewTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_shop;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = CommonUtils.dip2px(mContext, 320);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        mWebViewTitle = (TextView) findViewById(R.id.shop_title);
        mWebView = (WebView)findViewById(R.id.shop_webview);
        mProgressBar = (ProgressBar)findViewById(R.id.shop_web_progressbar);

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

        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.setWebViewClient(new RongWebviewClient());
        this.mWebView.setWebChromeClient(new RongWebChromeClient());
        this.mWebView.setDownloadListener(new RongWebViewDownLoadListener());
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mPrevUrl = bundle.getString("url");
        String userId = bundle.getString("USERID");

        //##
        Map<String, String> headers = new HashMap();
        String token = UserInfoUtil.getToken();
        if (TextUtils.isEmpty(token)) {
            headers.put("XX-Token", "");
        } else {
            headers.put("XX-Token", token);
        }
        headers.put("XX-Device-Type", "android");
        headers.put("XX-Api-Version", App.versionName);
        headers.put("XX-Store-Channel", App.channel_id);
        headers.put("XX-UserId", userId);
        //##

        if (mPrevUrl != null) {
            this.mWebView.loadUrl(mPrevUrl, headers);
        }
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.dialog_live_user_home) {
            dismiss();

            Intent intent = new Intent(mContext, MMDetailsActivityNew.class);
            intent.putExtra("user_id", Integer.parseInt(userId));
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        } */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
                        ComponentName componentName = intent.resolveActivity(getActivity().getPackageManager());
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
                        dismiss();
                    }else if (callback.equals("openWeb")){
                        //开启新webView
                        String url = jso.getJSONObject("param").getString("url");
                        if (!TextUtils.isEmpty(url)) {
                            Intent intent = new Intent("io.xchat.intent.action.webview");
                            intent.setPackage(getActivity().getPackageName());
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
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

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
            if (checkIntent(getActivity(), intent)) {
                startActivity(intent);
                if (uri.getScheme().equals("file") && uri.toString().endsWith(".txt")) {
                    dismiss();
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
                    if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
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
}
