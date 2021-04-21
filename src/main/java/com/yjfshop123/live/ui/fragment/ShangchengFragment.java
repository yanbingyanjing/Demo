package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ShangchengFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.top_status)
    View topStatus;
    @BindView(R.id.webview)
    LinearLayout webview;
    AgentWeb.PreAgentWeb preAgentWeb;
    @BindView(R.id.empty)
    EmptyLayout empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    AgentWeb mAgentWeb;


    @Override
    protected int setContentViewById() {
        return R.layout.fragment_shangcheng;
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initEvent() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topStatus.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        topStatus.setLayoutParams(params);

        preAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) webview, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready();
        mAgentWeb = preAgentWeb.get();

        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        //mAgentWeb.getWebCreator().getWebView()  获取WebView .
        mAgentWeb.getWebCreator().getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               // view.loadUrl(url);
                NLog.e("连接：",url);
                return false;
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                NLog.e("1连接：",url);

                return super.shouldInterceptRequest(view, url);
            }
        });
//		mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();
        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //优先使用网络
        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //将图片调整到适合webview的大小
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        //支持内容重新布局
        mAgentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //支持自动加载图片
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        //当webview调用requestFocus时为webview设置节点
        mAgentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        //自适应屏幕
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        //开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 javascript 来操作这些数据。）
        mAgentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        //支持缩放
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(true);

        //允许webview对文件的操作
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mAgentWeb.getWebCreator().getWebView().canGoBack()) { // 表示按返回键时的操作
                        mAgentWeb.getWebCreator().getWebView().goBack(); // 后退
                        // webview.goForward();//前进
                        return true; // 已处理
                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().moveTaskToBack(true);
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        initData();
    }

    protected void initData() {
        showLoading();
        OKHttpUtils.getInstance().getRequest("app/shop/getUrl", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout.getVisibility() == View.VISIBLE)
                    showNotData();
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                if (!TextUtils.isEmpty(result)) {
                    DataNow dataNow = new Gson().fromJson(result, DataNow.class);
                    if (!TextUtils.isEmpty(dataNow.url)) {
                        webview.setVisibility(View.VISIBLE);
                        mAgentWeb = preAgentWeb.go(dataNow.url);
                    }
                } else {
                    if (mEmptyLayout.getVisibility() == View.VISIBLE)
                        showNotData();
                }
            }
        });
    }

    private class DataNow {
        public String url;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        if (mAgentWeb != null && mAgentWeb.getWebCreator().getWebView() != null) {
            mAgentWeb.getWebCreator().getWebView().loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            mAgentWeb.getWebCreator().getWebView().clearHistory();
            mAgentWeb.getWebCreator().getWebView().destroy();

            mAgentWeb.clearWebCache();
            mAgentWeb.destroy();

        }
        super.onDestroy();
    }
}
