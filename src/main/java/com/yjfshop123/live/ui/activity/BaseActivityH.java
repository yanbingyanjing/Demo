package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ViewFlipper;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.VerfyTipsDialog;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivityH extends BaseNewActivity {

    protected Context mContext;
    private ViewFlipper mContentView;

    public boolean isRoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//应用内的禁止截屏
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base_h);
        mContext = getApplicationContext();
        mContentView = super.findViewById(R.id.layout_container_h);

        try {
            if (!isRoom){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //无状态栏
                    Window window = getWindow();
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
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //透明状态栏
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected View getRootView() {
        return mContentView;
    }
    protected List<IMConversationDB> mList = new ArrayList<>();
    protected EventModel2 eventModel2;
    protected SystemMessage systemMessage;

    public List<IMConversationDB> getData() {
        return mList;
    }

    public SystemMessage getSystemMessage() {
        return systemMessage;
    }

    public EventModel2 getEventModel2() {
        return eventModel2;
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

    protected void hideKeyBord() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

