package com.yjfshop123.live.socket;

public interface SocketMsgListener {

    //连接成功
    void onConnect();

    //连接断开
    void onDisConnect();

    //收到消息
    void onMessage(String content);
}
