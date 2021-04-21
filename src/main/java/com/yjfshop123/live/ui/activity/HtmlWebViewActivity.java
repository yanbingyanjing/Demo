package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class HtmlWebViewActivity extends BaseActivityForNewUi {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.myWeb)
    WebView myWeb;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.status_bar_view_new)
    View statusBarViewNew;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    private int type = 0;//0 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmlweb);
        ButterKnife.bind(this);
        setHeadVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view_new).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view_new).setLayoutParams(params);
        initAction();
        initData();
    }


    private void initAction() {
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            centerTitle.setText(R.string.app_xieyi);
        } else {
            centerTitle.setText(getString(R.string.yingsi));
        }
        WebSettings webSettings = myWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWeb.setBackgroundColor(0);
    }

    private void initData() {
        if (type == 0) {
            OKHttpUtils.getInstance().getRequest("app/user/getUserProtocol", "", new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                }

                @Override
                public void onSuccess(String result) {
                    if (result == null) {
                        return;
                    }
                    try {
                        JSONObject data = new JSONObject(result);
                        String hrml = data.getString("user_protocol");
                        myWeb.loadDataWithBaseURL(null, hrml, "text/html", "UTF-8", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            OKHttpUtils.getInstance().getRequest("app/user/getPrivacyProtocol", "", new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                }

                @Override
                public void onSuccess(String result) {
                    if (result == null) {
                        return;
                    }
                    try {
                        JSONObject data = new JSONObject(result);
                        String hrml = data.getString("privacy_protocol");
                        myWeb.loadDataWithBaseURL(null, hrml, "text/html", "UTF-8", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
        hideKeyBord();
    }
}
