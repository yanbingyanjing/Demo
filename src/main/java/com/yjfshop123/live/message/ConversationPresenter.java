package com.yjfshop123.live.message;

import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.interf.ConversationView;

import java.util.Observable;
import java.util.Observer;

public class ConversationPresenter implements Observer {

    private ConversationView view;

    public ConversationPresenter(ConversationView view){
        MessageEvent.getInstance().addObserver(this);
        this.view = view;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent){
            if (data instanceof IMConversationDB) {
                IMConversationDB imConversationDB = (IMConversationDB) data;
                view.updateMessage(imConversationDB);
            }
        }
    }
}
