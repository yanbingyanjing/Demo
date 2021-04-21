package com.yjfshop123.live.socket;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.BaseSockActivity;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SocketUtil extends WebSocketAdapter {

    private final String TAG = "TAGTAG_WebSocekt";

    private static final int CONNECT_TIMEOUT = 5000;//连接超时时间
    private static final int FRAME_QUEUE_SIZE = 10;//设置帧队列最大值

    protected static SocketUtil sInstance;
    protected WebSocket mWebSocket;
    protected SocketHandler mSocketHandler;
    protected SockHeartBeatThread mSockHeartBeatThread;
    protected boolean isLogin = false;
    /**
     * 连接成功
     */
    public static final int WHAT_CONN = 0;
    /**
     * 连接失败
     */
    public static final int WHAT_DISCONN = 1;
    /**
     * 收到消息
     */
    public static final int WHAT_MESSAGE = 2;
    /**
     * 绑定失败
     */
    public static final int WHAT_BIND_ERROR = 3;

    private WsStatus mStatus;

    private SocketUtil() {
        mSocketHandler = new SocketHandler();
    }

    public static SocketUtil getInstance() {
        synchronized (SocketUtil.class) {
            if (sInstance == null) {
                sInstance = new SocketUtil();
            }
            return sInstance;
        }
    }

    public SocketUtil connect() {
        try {
            disconnect();
            mSockHeartBeatThread = new SockHeartBeatThread();
            mWebSocket = new WebSocketFactory().setVerifyHostname(false)
                    .createSocket(Const.getSocketDomain(), CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(this)//添加回调监听
                    .connectAsynchronously();//异步连接
            mStatus = WsStatus.CONNECTING;

            NLog.e(TAG, "开始连接");
        } catch (IOException e) {
            e.printStackTrace();
            NLog.e(TAG, "socket异常--->" + e.getMessage());
        }
        return this;
    }

    public void cancelSocketMsgListener() {
        if (mSocketHandler != null) {
            mSocketHandler.setSocketMsgListener(null);
        }
    }

    public void setMesgListener(SocketMsgListener listener) {
        if (mSocketHandler == null) {
            mSocketHandler = new SocketHandler();
        }
        mSocketHandler.setSocketMsgListener(listener);
    }

    boolean isBindSuccess = true;
    String client_id_need_connect = "";

    //################################################WebSock回调
    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);

        Message msg = Message.obtain();
        msg.what = WHAT_MESSAGE;
        msg.obj = text;
        if (mSocketHandler != null) {
            mSocketHandler.sendMessage(msg);
        }
        NLog.e(TAG, text);

        try {
            JSONObject jso = new JSONObject(text);
            String code = jso.getString("code");
            if (code.equals("otc") || code.equals("c2c")) {//如果是OTC  CTC通知
                EventBus.getDefault().post(text, Config.EVENT_OTC);
            }
            if (code.equals("task")) {//如果是任务通知
                EventBus.getDefault().post(text, Config.EVENT_task);
            }
            if (code.equals("connect_success")) {//绑定
                isBindSuccess = true;
                client_id_need_connect = "";
                final String client_id = jso.getJSONObject("data").getString("client_id");
                //绑定
                String live_id = BaseSockActivity.mLiveID;
                if (live_id == null) {
                    live_id = "";
                }
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("client_id", client_id)
                            .put("live_id", live_id)
                            .put("longitude", UserInfoUtil.getLongitude())
                            .put("latitude", UserInfoUtil.getLatitude())
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/login/bindClientId", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        if (errCode == 1001 && mSocketHandler != null) {
                            mSocketHandler.sendEmptyMessage(WHAT_BIND_ERROR);
                            return;
                        }
                        //绑定失败 退出登录
                        //  if (!TextUtils.isEmpty(errInfo) && errInfo.contains("网络请求超时")) {
                        isBindSuccess = false;
                        client_id_need_connect = client_id;
                        //     return;
                        //   }


//                        isBindSuccess=false;
//                        client_id_need_connect=client_id;
                    }

                    @Override
                    public void onSuccess(String result) {
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
            throws Exception {
        super.onConnected(websocket, headers);

        mStatus = WsStatus.CONNECT_SUCCESS;
        reconnectCount = 0;//连接成功, 初始化连接次数
        if (mSocketHandler != null) {
            mSocketHandler.sendEmptyMessage(WHAT_CONN);
        }
        if (mSockHeartBeatThread != null) {
            mSockHeartBeatThread.startHeartbeat();
        }
        NLog.e(TAG, "连接成功 - onConnected");
    }


    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception)
            throws Exception {
        super.onConnectError(websocket, exception);
        exception.printStackTrace();
        mStatus = WsStatus.CONNECT_FAIL;
        if (mSocketHandler != null) {
            mSocketHandler.sendEmptyMessage(WHAT_DISCONN);
        }
        stopHeart();
        reconnect();//连接错误，重连
        NLog.e(TAG, "连接错误 - onConnectError");
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

        mStatus = WsStatus.CONNECT_FAIL;
        if (mSocketHandler != null) {
            mSocketHandler.sendEmptyMessage(WHAT_DISCONN);
        }
        stopHeart();
        reconnect();//连接断开，重连
        NLog.e(TAG, "连接断开 - onDisconnected");
    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        super.onPongFrame(websocket, frame);
    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        super.onPingFrame(websocket, frame);
    }
    //################################################WebSock回调

    /**
     * 发送二进制数据
     */
    private void sendData(String data) {
        if (mWebSocket != null) {
            mWebSocket.sendBinary(strToByteArray(data));
        }
        if (isBindSuccess || TextUtils.isEmpty(client_id_need_connect)) return;
        String live_id = BaseSockActivity.mLiveID;
        if (live_id == null) {
            live_id = "";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("client_id", client_id_need_connect)
                    .put("live_id", live_id)
                    .put("longitude", UserInfoUtil.getLongitude())
                    .put("latitude", UserInfoUtil.getLatitude())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/login/bindClientId", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //绑定失败 退出登录
            }

            @Override
            public void onSuccess(String result) {
                isBindSuccess = true;
                client_id_need_connect = "";
            }
        });
    }

    private static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    /**
     * 关闭
     */
    public void close() {
        stopHeart();
        disconnect();
        if (mSocketHandler != null) {
            mSocketHandler.setSocketMsgListener(null);
            mSocketHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mWebSocket != null) {
            mWebSocket.disconnect();
            mWebSocket = null;
        }
    }

    /**
     * 停止心跳
     */
    private void stopHeart() {
        if (mSockHeartBeatThread != null) {
            mSockHeartBeatThread.stopHeartbeat();
            mSockHeartBeatThread.quit();
            mSockHeartBeatThread = null;
        }
    }

    public SocketUtil isLL(boolean isLogin) {
        this.isLogin = isLogin;
        if (!isLogin) {
            stopHeart();
        }
        return this;
    }

    /**
     * 失败重连次数
     */
    private static final int RECONNECT_COUNT = 15;
    /**
     * 重连次数
     */
    private int reconnectCount = 0;

    /**
     * 重连
     */
    private void reconnect() {
        if (!isLogin) {
            return;
        }

        if (!CommonUtils.isNetworkConnected(App.getInstance())) {
            reconnectCount = 0;
            NLog.e(TAG, "失败网络不可用");
            return;
        }

        if (reconnectCount > RECONNECT_COUNT) {
            return;
        }
        NLog.e(TAG, "mWebSocket != null" + (mWebSocket != null));
        if (mWebSocket != null
                && !mWebSocket.isOpen() //当前连接断开了
                && mStatus != WsStatus.CONNECTING) {//不是正在重连状态

            reconnectCount++;
            mStatus = WsStatus.CONNECTING;

            NLog.e(TAG, "第" + reconnectCount + "次重连 -- url:" + Const.getSocketDomain());
            connect();
        }
    }

    /**
     * 切换到前台
     * 网络发生改变时候重连
     */
    public void reconnect_() {
        reconnectCount = 0;
        reconnect();
    }

    private class SockHeartBeatThread extends HandlerThread {
        private Handler handler;
        private boolean running = false;

        public SockHeartBeatThread() {
            super("SockHeartBeatThread");
            this.start();
            handler = new Handler(this.getLooper());
        }

        private Runnable heartBeatRunnable = new Runnable() {
            @Override
            public void run() {
                //10秒一次 检验长连接
                sendData("{\"action\":\"ping\"}");
                NLog.e(TAG, "--ping");

                if (handler != null) {
                    handler.postDelayed(heartBeatRunnable, 10000);
                }
            }
        };

        public boolean running() {
            return running;
        }

        public void startHeartbeat() {
            running = true;
            handler.postDelayed(heartBeatRunnable, 1000);
        }

        public void stopHeartbeat() {
            running = false;
            handler.removeCallbacks(heartBeatRunnable);
        }
    }
}
