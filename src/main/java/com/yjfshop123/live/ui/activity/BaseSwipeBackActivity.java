package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.widget.SwipeBackLayout;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.VerfyTipsDialog;
import com.yjfshop123.live.utils.UserInfoUtil;

public abstract class BaseSwipeBackActivity extends BaseNewActivity {

    /**
     * 是否有状态栏(聊天界面无状态栏，输入框无法悬浮在键盘上)
     */
    public boolean isStatusBar = false;

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//应用内的禁止截屏
        super.onCreate(savedInstanceState);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                if (isStatusBar) {
                    //设置状态栏颜色跟标题栏一样
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.color_theme));
                } else {
                    //无状态栏
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    //SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 6.0之后才有
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isStatusBar) {
                    //透明状态栏
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSwipeBackLayout = new SwipeBackLayout(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this, SwipeBackLayout.EDGE_LEFT);
//        slide(true);
        mSwipeBackLayout.setEdgeSize(getResources().getDisplayMetrics().widthPixels * 1 / 10);
    }

    /*public void slide(boolean isSlide){
        if (isSlide){
            // 触摸边缘变为屏幕宽度的1/2
            mSwipeBackLayout.setEdgeSize(getResources().getDisplayMetrics().widthPixels * 1 / 4);
        }else {
            mSwipeBackLayout.setEdgeSize(0);
        }
    }*/

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    @Override
    public void finish() {
        super.finish();
       // overridePendingTransition(R.anim.hold, R.anim.slide_right_exit);
    }

}