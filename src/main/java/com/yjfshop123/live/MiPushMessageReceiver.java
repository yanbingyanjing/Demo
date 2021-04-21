package com.yjfshop123.live;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.ui.activity.SplashActivity;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {

    private final String TAG = "MiPushMessageReceiver";
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    private long mBussId = Const.getMetaDataLong("MI_BUSS_ID");

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        NLog.e(TAG,"onReceivePassThroughMessage is called. " + message.toString());
        NLog.e(TAG, "getSimpleDate()" + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
        NLog.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        NLog.e(TAG,"onNotificationMessageClicked is called. " + message.toString());
        NLog.e(TAG, "getSimpleDate()" + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
//        MiPushClient.clearNotification(context);

        NLog.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);

        Intent i = new Intent(context, SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        NLog.e(TAG,"onNotificationMessageArrived is called. " + message.toString());
        NLog.e(TAG, "getSimpleDate()" + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        NLog.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        NLog.e(TAG, "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);

        NLog.e(TAG, "cmd: " + command + " | arg1: " + cmdArg1 + " | arg2: " + cmdArg2
                + " | result: " + message.getResultCode() + " | reason: " + message.getReason());

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }

        NLog.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        NLog.e(TAG, "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);

        NLog.e(TAG, "cmd: " + command + " | arg: " + cmdArg1
                + " | result: " + message.getResultCode() + " | reason: " + message.getReason());

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;

                TIMOfflinePushToken token = new TIMOfflinePushToken(mBussId, mRegId);
                token.setBussid(mBussId);
                token.setToken(mRegId);
                TIMManager.getInstance().setOfflinePushToken(token, new TIMCallBack() {
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
        }

        NLog.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);

    }

    /*@SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }*/

}
