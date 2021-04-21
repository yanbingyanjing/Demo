package com.yjfshop123.live.message.interf;


import com.yjfshop123.live.message.db.MessageDB;
import com.tencent.imsdk.TIMMessage;

public interface ChatViewIF extends MvpView {

    /**
     * 更新最新消息显示
     *
     * @param messageDB 最后一条消息
     */
    void updateMessage(MessageDB messageDB);

    void onSendMessageFail(int code, String desc, TIMMessage message);

    void onSuccess(TIMMessage message);

}