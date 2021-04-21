package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 广告展示页面 启动页之后打开
 */
public class AdvertActivity extends BaseActivityH {
    @BindView(R.id.splash_img)
    ImageView splashImg;
    @BindView(R.id.splash_btn)
    Button splashBtn;
    int isLogin = 0;
    private String mCodeId = "887402522";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);
        ButterKnife.bind(this);
        is_need_check_verfy = false;
        startApp();
        isLogin = UserInfoUtil.getInfoComplete();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)  splashBtn.getLayoutParams();
        //获取当前控件的布局对象
        params.topMargin = SystemUtils.getStatusBarHeight(this)+30;//设置当前控件布局的高度
        splashBtn.setLayoutParams(params);
        mHandler.postDelayed(runnable, 5000);
    }
    private void startApp() {
        String launch_img = UserInfoUtil.getLaunchImg();
        if (!TextUtils.isEmpty(launch_img)) {
            Glide.with(mContext)
                    .load(com.yjfshop123.live.utils.CommonUtils.getUrl(launch_img))
                    .transition(new DrawableTransitionOptions().crossFade(1500))
                    .into(splashImg);
            final String link = UserInfoUtil.getLaunchLink();
            splashImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(link)) {
                        Intent intent = new Intent("io.xchat.intent.action.webview");
                        intent.setPackage(mContext.getPackageName());
                        intent.putExtra("url", link);
                        startActivity(intent);
                    }
                }
            });
        }

    }


    protected Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActi();
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        mHandler.removeCallbacks(runnable);
    }
    private boolean is_login;

    private void startActi() {
        if (!isResume) {
            return;
        }
        if (isLogin == 0) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        } else {
            //已登录检查是否实名认证
            int status = UserInfoUtil.getAuthStatus();

            if (status == 0) {//未提交
                Intent intent = new Intent();
                intent.setClass(this, ShiMingRenZhengActivity.class);
                startActivity(intent);
            } else if (status == 10) { //审核失败
                Intent intent = new Intent();
                intent.setClass(this, ShiMingRenZhengActivity.class);
                startActivity(intent);
            } else if (status == 2) {//通过
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else if (status == 1) {//审核中
                Intent intent = new Intent(this, HomeActivity.class);

                startActivity(intent);
            }
            finish();
        }

    }
boolean isResume=false;
    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;

    }

    @OnClick(R.id.splash_btn)
    public void onViewClicked() {
        startActi();
    }

}
