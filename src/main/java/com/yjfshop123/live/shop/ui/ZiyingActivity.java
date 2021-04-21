package com.yjfshop123.live.shop.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.shop.view.MyWebView;
import com.yjfshop123.live.ui.activity.BaseAppCompatActivity;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ZiyingActivity extends BaseAppCompatActivity {
    @BindView(R.id.x_web_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.x_webview)
    MyWebView mWebView;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout refresh;
    String mPrevUrl;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.control_iv)
    ImageView controlIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziying_shop);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.setStatusBarColor(this, R.color.white);

        initAction();
    }

    protected void initAction() {

        this.mWebView.setVerticalScrollbarOverlay(true);
        this.mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.mWebView.getSettings().setLoadWithOverviewMode(true);

        this.mWebView.getSettings().setJavaScriptEnabled(true); // 启用js
        this.mWebView.getSettings().setBlockNetworkImage(false); // 解决图片不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        this.mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.mWebView.setScrollContainer(true);
        this.mWebView.setVerticalScrollBarEnabled(true);
        this.mWebView.setHorizontalScrollBarEnabled(true);

        //
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 11) {
            this.mWebView.getSettings().setDisplayZoomControls(false);
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.mWebView.setWebContentsDebuggingEnabled(true);
//        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                NLog.e("连接：", url);
                mWebView.loadUrl(url);
                return true;

            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                NLog.e("1连接：", url);
                return super.shouldInterceptRequest(view, url);
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键时的操作
                        mWebView.goBack(); // 后退
                        return true; // 已处理
                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                    }
                }
                return false;
            }
        });
        this.mWebView.getSettings().

                setSupportZoom(true);
        this.mWebView.setWebChromeClient(new

                RongWebChromeClient());
        this.mWebView.getSettings().

                setDomStorageEnabled(true);
        this.mWebView.getSettings().

                setDefaultTextEncodingName("utf-8");
        this.mWebView.getSettings().

                setTextSize(WebSettings.TextSize.NORMAL);
        //mWebView.addJavascriptInterface(this, "WebViewJavascriptBridge");

        loadUrl();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mWebView != null && mWebView.canGoBack()) { // 表示按返回键时的操作
                    refresh.setRefreshing(false);
                } else
                    loadUrl();
            }
        });

        mWebView.setOnScrollListener(new MyWebView.IScrollListener() {
            @Override
            public void onScrollChanged(int scrollY) {
                if (scrollY == 0) {
                    //开启下拉刷新
                    refresh.setEnabled(true);
                } else {
                    //关闭下拉刷新
                    refresh.setEnabled(false);
                }
            }
        });

    }

    private void loadUrl() {

        Map<String, String> headers = new HashMap();
        String mToken = UserInfoUtil.getToken();
        if (TextUtils.isEmpty(mToken)) {
            headers.put("XX-Token", "");
        } else {
            headers.put("XX-Token", mToken);
        }
        headers.put("XX-Device-Type", "android");
        headers.put("XX-Api-Version", App.versionName);
        headers.put("XX-Store-Channel", App.channel_id);
        if (mWebView != null) {
            this.mWebView.clearHistory();
            this.mWebView.loadUrl(url, headers);
        }
    }

//    private void loadLoginUrl() {
//
//        Map<String, String> headers = new HashMap();
//        String mToken = UserInfoUtil.getToken();
//        if (TextUtils.isEmpty(mToken)) {
//            headers.put("XX-Token", "");
//        } else {
//            headers.put("XX-Token", mToken);
//        }
//        headers.put("XX-Device-Type", "android");
//        headers.put("XX-Api-Version", App.versionName);
//        headers.put("XX-Store-Channel", App.channel_id);
//        if (mWebView != null) {
//            // this.mWebView.clearHistory();
//            this.mWebView.loadUrl(loginUrl, headers);
//            this.mWebView.loadUrl(loginUrl, headers);
//        }
//    }

    String url = "";
    // String loginUrl = "https://api.yjfshop.com/app/tong_duiba/createUrl";

    public void backClick() {
        if (mWebView.canGoBack()) { // 表示按返回键时的操作
            mWebView.goBack(); // 后退
        } else {
            finish();
        }
    }

//    @JavascriptInterface
//    public void restartLogin(String s) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loadLoginUrl();
//            }
//        });
//
//    }

    boolean isLoading = false;

    private class RongWebChromeClient extends WebChromeClient {
        private RongWebChromeClient() {
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            tvTitleCenter.setText(title);
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                // isLoading = false;
                // LoadDialog.dismiss(getContext());
                refresh.setRefreshing(false);
                if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
            } else {
                //  if (!isLoading) {
                //     isLoading = true;
                //     LoadDialog.show(getContext());
                // }

                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                mProgressBar.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }
    }


}
