package com.yjfshop123.live;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.yjfshop123.live.net.utils.NLog;
import com.huawei.android.pushagent.api.PushEventReceiver;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;

public class HwPushMessageReceiver extends PushEventReceiver {
    private final String TAG = "HwPushMessageReceiver";

    private long mBussId = Const.getMetaDataLong("HUAWEI_BUSS_ID");




    @Override
    public void onToken(Context context, String token, Bundle extras){
        String belongId = extras.getString("belongId");
        String content = "onToken， 获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        NLog.e(TAG, content);

        TIMOfflinePushToken param = new TIMOfflinePushToken(mBussId, token);
        param.setBussid(mBussId);
        param.setToken(token);
        TIMManager.getInstance().setOfflinePushToken(param, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                NLog.e(TAG, "setOfflinePushToken failed, code: " + i + "|msg: " + s);
            }
            @Override
            public void onSuccess() {
                NLog.i(TAG, "setOfflinePushToken succ");
            }
        });
    }


    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "onPushMsg， 收到一条Push消息： " + new String(msg, "UTF-8");
            NLog.e(TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "onEvent， 收到通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            NLog.e(TAG, content);
        } else if (Event.PLUGINRSP.equals(event)) {
            final int TYPE_LBS = 1;
            final int TYPE_TAG = 2;
            int reportType = extras.getInt(BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = extras.getBoolean(BOUND_KEY.PLUGINREPORTRESULT, false);
            String message = "";
            if (TYPE_LBS == reportType) {
                message = "LBS report result :";
            } else if(TYPE_TAG == reportType) {
                message = "TAG report result :";
            }
            NLog.e(TAG, message + isSuccess);
        }
        super.onEvent(context, event, extras);
    }
}

