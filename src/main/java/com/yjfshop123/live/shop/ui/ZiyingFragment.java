package com.yjfshop123.live.shop.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.just.agentweb.AgentWeb;
import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ZiyingFragment extends BaseFragment {


    @BindView(R.id.x_web_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.x_webview)
    WebView mWebView;
    @BindView(R.id.control_iv)
    ImageView controlIv;
    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int setContentViewById() {
        return R.layout.fragment_ziyingshangcheng;
    }

    @Override
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
                // view.loadUrl(url);
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
                        getActivity().moveTaskToBack(true);
                    }
                }
                return false;
            }
        });
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.setWebChromeClient(new RongWebChromeClient());
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        this.mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        mWebView.addJavascriptInterface(this, "WebViewJavascriptBridge");
        isLoad = false;
        if (!isLoad) {
            NLog.d("是否夹杂商城", "1");
            isLoad = true;
            loadUrl();
        }
        NLog.d("ziyingfragment", "oncreate");
    }

    boolean isLoad = false;

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
        if (mWebView != null)
            this.mWebView.loadUrl("https://api.yjfshop.com/app/tong_duiba/createUrl", headers);
    }

    @Override
    protected void initEvent() {


    }

    @JavascriptInterface
    public void restartLogin(String s) {
        NLog.d("是否夹杂商城", "0");
        loadUrl();
    }

    boolean isLoading = false;

    private class RongWebChromeClient extends WebChromeClient {
        private RongWebChromeClient() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                // isLoading = false;
                // LoadDialog.dismiss(getContext());
               // if(mProgressBar!=null)mProgressBar.setVisibility(View.GONE);
            } else {
                //  if (!isLoading) {
                //     isLoading = true;
                //     LoadDialog.show(getContext());
                // }

//                if (mProgressBar.getVisibility() == View.GONE) {
//                    mProgressBar.setVisibility(View.GONE);
//                }
//
//                mProgressBar.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private class DataNow {
        public String url;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NLog.d("ziyingfragment", "onDestroyView");

        unbinder.unbind();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
