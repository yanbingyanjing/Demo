package com.yjfshop123.live.utils;


import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.yjfshop123.live.server.widget.LoadDialog;

/**
 *
 * 日期:2018/12/28
 * 描述:
 **/
public class HandlerUtils {

    private Context context;

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                LoadDialog.show(context);
            } else {
                LoadDialog.dismiss(context);
            }
        }
    };

    public HandlerUtils(Context context) {
        this.context = context;
    }

    public void show() {
        handler.sendEmptyMessage(1);
    }

    public void dismiss() {
        handler.sendEmptyMessage(2);
    }

}
