package com.yjfshop123.live.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.CheckInstalledUtil;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 日期:2019/1/7
 * 描述: 支付页面(通用web页面)
 **/
public class PayWebViewActivity extends BaseActivity {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.webView)
    WebView webView;

    private String html = "";

    private WebSettings webSettings;

    private static final String UTF_8 = "UTF-8";

    private String option;
    private String payFlag = "";
    private String payResultStr = "";

    @Override
    protected void onResume() {
        super.onResume();
        if (!payResultStr.equals("")) {
            webView.loadUrl(payResultStr);
            if (option.equals("vip")) {
                EventBus.getDefault().post("vipRecharge",Config.EVENT_START);
            } else if (option.equals("MyJin")) {
                EventBus.getDefault().post("payRecharge",Config.EVENT_START);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_webview);
        ButterKnife.bind(this);
        setHeadVisibility(View.VISIBLE);

        tv_title_center.setText("支付中心");

        initWeb();
        initData();
    }

    private void initData() {
        html = getIntent().getStringExtra("html");
//        webView.loadData(html, "text/html", UTF_8);

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

//        webView.loadUrl(html);

        option = getIntent().getStringExtra("option");
    }

    @SuppressLint("JavascriptInterface")
    private void initWeb() {
        webSettings = webView.getSettings();


        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }


        // 支持javascript
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptObject(mContext), "androidPay");

        // 支持使用localStorage(H5页面的支持)
        webSettings.setDomStorageEnabled(true);

        // 支持数据库
//        webSettings.setDatabaseEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");

        // 支持缓存
//        webSettings.setAppCacheEnabled(false);
//        String appCaceDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
//        webSettings.setAppCachePath(appCaceDir);

        // 设置可以支持缩放
        webSettings.setUseWideViewPort(true);

        // 扩大比例的缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        // 隐藏缩放按钮
        webSettings.setDisplayZoomControls(false);

        // 自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        //支持通过JS打开新窗口(允许JS弹窗)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 隐藏滚动条
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.equals("about:blank")) {
                    return true;
                }

                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if (url.startsWith("alipays://")) {
                    payFlag = "alipays";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    try {
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        NToast.shortToast(PayWebViewActivity.this,"请检查是否安装支付应用");
                        e.getMessage();
                        finish();
                    }
                    return true;
                } else if (url.startsWith("weixin://wap/pay?")) {
                    payFlag = "weixin";
                    if (!CheckInstalledUtil.isWeChatAppInstalled(PayWebViewActivity.this)) {
                        NToast.shortToast(PayWebViewActivity.this, "请检查是否安装支付应用");
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        try {
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        } catch (Exception e) {
//                        NToast.shortToast(VipCenterActivity.this,"请检查是否安装支付应用");
                            e.getMessage();
                        }
                    }
                    return true;
                } else if (url.startsWith("https://wx.tenpay.com")) {
                    if (("4.4.3".equals(android.os.Build.VERSION.RELEASE)) || ("4.4.4".equals(android.os.Build.VERSION.RELEASE))) {
                        //兼容这两个版本设置referer无效的问题
//                        view.loadDataWithBaseURL("http://xchat.005d.com", "<script>window.location.href=\"" + url + "\";</script>", "text/html", "utf-8", null);
                        view.loadDataWithBaseURL(Const.getDomain(), "<script>window.location.href=\"" + url + "\";</script>", "text/html", "utf-8", null);
                    } else {
                        Map<String, String> extraHeaders = new HashMap<>();
//                        extraHeaders.put("Referer", "http://xchat.005d.com");
                        extraHeaders.put("Referer", Const.getDomain());
                        view.loadUrl(url, extraHeaders);
                    }
                    return true;
                } else if (url.contains("apph5/pay/paycomplete")) {
//                    Intent intent = new Intent("com.yjfshop123.live");
//                    sendBroadcast(intent);
//                    NToast.shortToast(PayWebViewActivity.this, getString(R.string.pay_success));
                    if (payFlag.equals("alipays")) {
                        webView.loadUrl(url);
                        if (option.equals("vip")) {
                            EventBus.getDefault().post("vipRecharge",Config.EVENT_START);
                        } else if (option.equals("MyJin")) {
                            EventBus.getDefault().post("payRecharge",Config.EVENT_START);
                        }
                    } else if (payFlag.equals("weixin")) {
                        payResultStr = url;
                        return true;
                    }
//                    finish();
                }else if(url.contains("eeeee")){
                    finish();
                }
//                finish();
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                super.onJsAlert(view, url, message, result);
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            handler.sendEmptyMessage(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/
                return true;
            }


            public void onProgressChanged(WebView view, int progress) {
                //当进度走到100的时候做自己的操作，我这边是弹出dialog
                if (progress == 100) {
                    webView.setVisibility(View.VISIBLE);

                }
            }

        });
    }

    /*protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if (webView.getVisibility() == View.VISIBLE) {
//            setHeadLayout();
//            return false;
//        }
        return super.onKeyDown(keyCode, event);
    }


    private class JavaScriptObject {

        private Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface
        public void pay(String object) {
            finish();
//            NToast.shortToast(mContext, object.toString());
        }
    }

}
