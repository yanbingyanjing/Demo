package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class BaseActivityForNewUi extends BaseSwipeBackActivity {

    protected Context mContext;
    protected FrameLayout root_view;
    private FrameLayout mContentView;
    protected RelativeLayout mHeadLayout;
    protected ImageView mBtnLeft;
    protected TextView mHeadRightText;

    protected int screenWidth_;
    public boolean isShow = false;
    protected TextView tv_title_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base_new);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
        mContext = this;
        // 初始化公共头部
        mContentView = super.findViewById(R.id.layout_container);
        mHeadLayout = super.findViewById(R.id.layout_head);
        mHeadRightText = super.findViewById(R.id.text_right);
        mBtnLeft = super.findViewById(R.id.btn_left);
        tv_title_center = findViewById(R.id.tv_title_center);
        root_view = findViewById(R.id.root_view);
        if (isStatusBar) {
            findViewById(R.id.status_bar_view).setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)   findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        screenWidth_ = CommonUtils.getScreenWidth(this);
        StatusBarUtil.StatusBarDarkMode(this);
    }

    protected void setCenterTitleText(String title) {
        setHeadLayout();
        tv_title_center.setText(title);
    }
    protected void setTooBarBack(int drawable) {
        root_view.setBackgroundResource(drawable);
    }
    protected void setContent(int drawable) {
        mContentView.setBackgroundResource(drawable);
    }

    protected void setBlackColorTooBar() {
        StatusBarUtil.StatusBarLightMode(this);
        tv_title_center.setTextColor(getResources().getColor(R.color.color_333333));
        mHeadRightText.setTextColor(getResources().getColor(R.color.color_333333));
        mBtnLeft.setImageResource(R.mipmap.black_back);

    }
    @Override
    protected View getRootView() {
        return mContentView;
    }

    @Override
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    /**
     * 设置头部背景
     */
    public void setHeadLayout() {
        findViewById(R.id.status_bar_view).setVisibility(View.VISIBLE);
    }

    /**
     * 设置头部是否可见
     *
     * @param visibility
     */
    public void setHeadVisibility(int visibility) {
        mHeadLayout.setVisibility(visibility);
        findViewById(R.id.status_bar_view).setVisibility(visibility);
        findViewById(R.id.status_line).setVisibility(visibility);
    }

    /**
     * 设置左边是否可见
     *
     * @param visibility
     */
    public void setHeadLeftButtonVisibility(int visibility) {
        mBtnLeft.setVisibility(visibility);
    }


    /**
     * 设置右边文本是否可见
     *
     * @param visibility
     */
    public void setHeadRightTextVisibility(int visibility) {
        mHeadRightText.setVisibility(visibility);
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    public void hideKeyBord() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 点击右按钮
     */
    public void onHeadRightButtonClick(View v) {

    }

    public ImageView getHeadLeftButton() {
        return mBtnLeft;
    }

    public void setHeadLeftButton(ImageView leftButton) {
        this.mBtnLeft = leftButton;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus() && event.getAction() == MotionEvent.ACTION_UP) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
