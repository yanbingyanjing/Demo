package com.yjfshop123.live.message;

import android.os.Handler;

import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;

import java.util.List;
import java.util.Observable;

public class MessageEvent extends Observable implements TIMMessageListener/*, TIMMessageRevokedListener*/ {

    private volatile static MessageEvent instance;
    private Handler mHandler = new Handler();

    private MessageEvent(){
        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);
//        TIMManager.getInstance().setMessageRevokedListener(this);
    }

    public static MessageEvent getInstance(){
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        for (TIMMessage item:list){
            if (item.getConversation().getType() == TIMConversationType.Group){
                setChanged();
                notifyObservers(item);
            }else {
                final IMConversationDB imConversationDB = ConversationFactory.getMessage(item, false);
                final MessageDB messageDB = MessageFactory.getMessage(item, false);
                if (imConversationDB == null && messageDB == null){
                    setChanged();
                    notifyObservers(item);
                }else {
                    if (messageDB != null && messageDB.getType() == 10){

                        if (messageDB.getIsHangup() != 0){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, false);
                                }
                            }, 1000);
                        }else {
                            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, false);
                        }

                        continue;
                    }

                    if (messageDB != null && (messageDB.getType() == 12 || messageDB.getType() == 13)){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SealAppContext.getInstance().systemMessage(messageDB);
                            }
                        }, 1000);
                        continue;
                    }

                    if (imConversationDB != null && messageDB != null) {
                        RealmMessageUtils.addMessageMsg(messageDB, imConversationDB);

                        setChanged();
                        notifyObservers(imConversationDB);
                        notifyObservers(messageDB);
                    } else if (imConversationDB != null) {
                        RealmConverUtils.addConverMsg(imConversationDB);

                        setChanged();
                        notifyObservers(imConversationDB);
                    } else if (messageDB != null){
                        RealmMessageUtils.addMessageMsg(messageDB, null);

                        setChanged();
                        notifyObservers(messageDB);
                    }

                    /*if (imConversationDB != null){
                        RealmConverUtils.addConverMsg(imConversationDB);

                        setChanged();
                        notifyObservers(imConversationDB);
                    }

                    if (messageDB != null){
                        RealmMessageUtils.addMessageMsg(messageDB);

                        setChanged();
                        notifyObservers(messageDB);
                    }*/
                }

            }
        }
        return false;
    }

    /**
     * 清理消息监听（用于退出登录）
     */
    public void clear(){
        instance = null;
    }

    /*@Override
    public void onMessageRevoked(TIMMessageLocator timMessageLocator) {
        setChanged();
        notifyObservers(timMessageLocator);
    }*/
}
