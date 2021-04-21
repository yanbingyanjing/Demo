package com.yjfshop123.live.ui.widget;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.io.File;

public class DialogHomeAddFragment extends AbsDialogFragment {

    private View.OnClickListener mCallback;
    private String title;
    private int logo = R.mipmap.banner1;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_home_add;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog1;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }


    MainStartChooseCallback mMainStartChooseCallback;

    public void setListener(MainStartChooseCallback mMainStartChooseCallback) {
        this.mMainStartChooseCallback = mMainStartChooseCallback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        (mRootView.findViewById(R.id.live)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainStartChooseCallback != null) mMainStartChooseCallback.onLiveClick();
                dismiss();
            }
        });

        (mRootView.findViewById(R.id.video)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainStartChooseCallback != null) mMainStartChooseCallback.onVideoClick();
                dismiss();
            }
        });
        (mRootView.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        (mRootView.findViewById(R.id.shadow)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getY()<mRootView.findViewById(R.id.one).getTop()){
                        dismiss();
                        return true;
                    }
                }
                return true;
            }
        });

    }



}
