package com.yjfshop123.live.socket;

import android.os.Handler;
import android.os.Message;

import com.yjfshop123.live.App;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;

public class SocketHandler extends Handler {

    private SocketMsgListener mListener;

    public SocketHandler() {
        super(App.getContext().getApplicationContext().getMainLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        if (mListener == null) {
            return;
        }
        switch (msg.what) {
            case SocketUtil.WHAT_CONN://连接成功
                mListener.onConnect();
                break;
            case SocketUtil.WHAT_MESSAGE://收到消息
                mListener.onMessage((String) msg.obj);
                break;
            case SocketUtil.WHAT_DISCONN://连接断开
                mListener.onDisConnect();
                break;
            case SocketUtil.WHAT_BIND_ERROR://绑定失败
                BroadcastManager.getInstance(App.getInstance()).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                break;
        }
    }

    public void setSocketMsgListener(SocketMsgListener listener) {
        mListener = listener;
    }
}
