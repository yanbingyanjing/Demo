package com.yjfshop123.live;

import android.content.Context;

import com.yjfshop123.live.net.utils.NLog;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

/**
 *
 * vivo
 *
 */
public class PushMessageReceiverImpl extends OpenClientPushMessageReceiver {

    /**
     * TAG to Log
     */
    public static final String TAG = "PushMessageReceiverImpl";

    private static final long busiid = Const.getMetaDataLong("VIVO_BUSS_ID");

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        String customContentString = msg.getSkipContent();
        String notifyString = "通知点击 msgId " + msg.getMsgId() + " ;customContent=" + customContentString;

        NLog.e(TAG, notifyString);
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        String responseString = "onReceiveRegId regId = " + regId;

        NLog.e(TAG, responseString);

        TIMOfflinePushToken token = new TIMOfflinePushToken(busiid, regId);
        token.setBussid(busiid);
        token.setToken(regId);
        TIMManager.getInstance().setOfflinePushToken(token, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                NLog.e(TAG, "setOfflinePushToken failed, code: " + i + "|msg: " + s);
            }
            @Override
            public void onSuccess() {
                NLog.e(TAG, "setOfflinePushToken succ~");
            }
        });
    }
}
