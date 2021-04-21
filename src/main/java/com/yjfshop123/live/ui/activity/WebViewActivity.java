package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.view.MyWebView;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WebViewActivity extends BaseAppCompatActivity {
    @BindView(R.id.x_web_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.x_webview)
    MyWebView mWebView;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout refresh;
    String mPrevUrl;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
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
        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            NToast.shortToast(this, "未获取到有效的页面");
            finish();
        }
        initAction();
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        if ( mWebView.canGoBack()) {
            mWebView.goBack();

        }else {
            finish();
        }
        SystemUtils.hideKeyBord(this);
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
        url=url+"?XX-Token="+mToken+"&XX-Device-Type=android";
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
        // Android 2.x
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        // Android 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType) {
            openFileChooser(uploadMsg, "", "filesystem");
        }

        // Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {

            mUploadHandler = new UploadHandler(new Controller());
            mUploadHandler.openFileChooser(uploadMsg, acceptType, capture);
        }

        // For Android 5.0+
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {

            openFileChooserImplForAndroid5(filePathCallback);
            return true;
        }

    };

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择图片");
        System.out.println("-----------调用");
        startActivityForResult(chooserIntent,
                FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }
    private UploadHandler mUploadHandler;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent intent) {
        if (requestCode == Controller.FILE_SELECTED) {
            // Chose a file from the file picker.
            if (mUploadHandler != null) {
                mUploadHandler.onResult(resultCode, intent);
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null
                    : intent.getData();
            System.out.println("-----------界面执行了回调" + (result == null));
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }


    class UploadHandler {
        /*
         * The Object used to inform the WebView of the file to upload.
         */
        private ValueCallback<Uri> mUploadMessage;
        private String mCameraFilePath;
        private boolean mHandled;
        private boolean mCaughtActivityNotFoundException;
        private Controller mController;

        public UploadHandler(Controller controller) {
            mController = controller;
        }

        public String getFilePath() {
            return mCameraFilePath;
        }

        boolean handled() {
            return mHandled;
        }

        public void onResult(int resultCode, Intent intent) {
            if (resultCode == Activity.RESULT_CANCELED
                    && mCaughtActivityNotFoundException) {
                // Couldn't resolve an activity, we are going to try again so
                // skip
                // this result.
                mCaughtActivityNotFoundException = false;
                return;
            }
            Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null
                    : intent.getData();

            // As we ask the camera to save the result of the user taking
            // a picture, the camera application does not return anything other
            // than RESULT_OK. So we need to check whether the file we expected
            // was written to disk in the in the case that we
            // did not get an intent returned but did get a RESULT_OK. If it
            // was,
            // we assume that this result has came back from the camera.
            if (result == null && intent == null
                    && resultCode == Activity.RESULT_OK) {
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    mController.getActivity().sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    result));
                }
            }
            mUploadMessage.onReceiveValue(result);
            mHandled = true;
            mCaughtActivityNotFoundException = false;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            final String imageMimeType = "image/*";
            final String videoMimeType = "video/*";
            final String audioMimeType = "audio/*";
            final String mediaSourceKey = "capture";
            final String mediaSourceValueCamera = "camera";
            final String mediaSourceValueFileSystem = "filesystem";
            final String mediaSourceValueCamcorder = "camcorder";
            final String mediaSourceValueMicrophone = "microphone";
            // According to the spec, media source can be 'filesystem' or
            // 'camera' or 'camcorder'
            // or 'microphone' and the default value should be 'filesystem'.
            String mediaSource = mediaSourceValueFileSystem;
            if (mUploadMessage != null) {
                // Already a file picker operation in progress.
                return;
            }
            mUploadMessage = uploadMsg;
            // Parse the accept type.
            String params[] = acceptType.split(";");
            String mimeType = params[0];
            if (capture.length() > 0) {
                mediaSource = capture;
            }
            if (capture.equals(mediaSourceValueFileSystem)) {
                // To maintain backwards compatibility with the previous
                // implementation
                // of the media capture API, if the value of the 'capture'
                // attribute is
                // "filesystem", we should examine the accept-type for a MIME
                // type that
                // may specify a different capture value.
                for (String p : params) {
                    String[] keyValue = p.split("=");
                    if (keyValue.length == 2) {
                        // Process key=value parameters.
                        if (mediaSourceKey.equals(keyValue[0])) {
                            mediaSource = keyValue[1];
                        }
                    }
                }
            }
            // Ensure it is not still set from a previous upload.
            mCameraFilePath = null;
            if (mimeType.equals(imageMimeType)) {
                if (mediaSource.equals(mediaSourceValueCamera)) {
                    // Specified 'image/*' and requested the camera, so go ahead
                    // and launch the
                    // camera directly.
                    startActivity(createCameraIntent());
                    return;
                } else {
                    // Specified just 'image/*', capture=filesystem, or an
                    // invalid capture parameter.
                    // In all these cases we show a traditional picker filetered
                    // on accept type
                    // so launch an intent for both the Camera and image/*
                    // OPENABLE.
                    Intent chooser = createChooserIntent(createCameraIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT,
                            createOpenableIntent(imageMimeType));
                    startActivity(chooser);
                    return;
                }
            } else if (mimeType.equals(videoMimeType)) {
                if (mediaSource.equals(mediaSourceValueCamcorder)) {
                    // Specified 'video/*' and requested the camcorder, so go
                    // ahead and launch the
                    // camcorder directly.
                    startActivity(createCamcorderIntent());
                    return;
                } else {
                    // Specified just 'video/*', capture=filesystem or an
                    // invalid capture parameter.
                    // In all these cases we show an intent for the traditional
                    // file picker, filtered
                    // on accept type so launch an intent for both camcorder and
                    // video/* OPENABLE.
                    Intent chooser = createChooserIntent(createCamcorderIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT,
                            createOpenableIntent(videoMimeType));
                    startActivity(chooser);
                    return;
                }
            } else if (mimeType.equals(audioMimeType)) {
                if (mediaSource.equals(mediaSourceValueMicrophone)) {
                    // Specified 'audio/*' and requested microphone, so go ahead
                    // and launch the sound
                    // recorder.
                    startActivity(createSoundRecorderIntent());
                    return;
                } else {
                    // Specified just 'audio/*', capture=filesystem of an
                    // invalid capture parameter.
                    // In all these cases so go ahead and launch an intent for
                    // both the sound
                    // recorder and audio/* OPENABLE.
                    Intent chooser = createChooserIntent(createSoundRecorderIntent());
                    chooser.putExtra(Intent.EXTRA_INTENT,
                            createOpenableIntent(audioMimeType));
                    startActivity(chooser);
                    return;
                }
            }
            // No special handling based on the accept type was necessary, so
            // trigger the default
            // file upload chooser.
            startActivity(createDefaultOpenableIntent());
        }

        private void startActivity(Intent intent) {
            try {
                mController.getActivity().startActivityForResult(intent,
                        Controller.FILE_SELECTED);
            } catch (ActivityNotFoundException e) {
                // No installed app was able to handle the intent that
                // we sent, so fallback to the default file upload control.
                try {
                    mCaughtActivityNotFoundException = true;
                    mController.getActivity().startActivityForResult(
                            createDefaultOpenableIntent(),
                            Controller.FILE_SELECTED);
                } catch (ActivityNotFoundException e2) {
                    // Nothing can return us a file, so file upload is
                    // effectively disabled.
                    Toast.makeText(mController.getActivity(),
                            "File uploads are disabled.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }

        private Intent createDefaultOpenableIntent() {
            // Create and return a chooser with the default OPENABLE
            // actions including the camera, camcorder and sound
            // recorder where available.
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            Intent chooser = createChooserIntent(createCameraIntent(),
                    createCamcorderIntent(), createSoundRecorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, i);
            return chooser;
        }

        private Intent createChooserIntent(Intent... intents) {
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
            chooser.putExtra(Intent.EXTRA_TITLE, "Choose file for upload");
            return chooser;
        }

        private Intent createOpenableIntent(String type) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(type);
            return i;
        }

        private Intent createCameraIntent() {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File externalDataDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File cameraDataDir = new File(externalDataDir.getAbsolutePath()
                    + File.separator + "browser-photos");
            cameraDataDir.mkdirs();
            mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
                    + System.currentTimeMillis() + ".jpg";
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(mCameraFilePath)));
            return cameraIntent;
        }

        private Intent createCamcorderIntent() {
            return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        }

        private Intent createSoundRecorderIntent() {
            return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        }
    }

    class Controller {

        final static int FILE_SELECTED = 4;

        Activity getActivity() {
            return WebViewActivity.this;
        }
    }

}
