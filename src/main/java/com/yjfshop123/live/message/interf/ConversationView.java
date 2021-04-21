package com.yjfshop123.live.message.interf;


import com.yjfshop123.live.message.db.IMConversationDB;


public interface ConversationView extends MvpView {

    /**
     * 更新最新消息显示
     *
     * @param imConversationDB 最后一条消息
     */
    void updateMessage(IMConversationDB imConversationDB);
}
