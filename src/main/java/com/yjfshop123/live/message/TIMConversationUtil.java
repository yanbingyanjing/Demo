package com.yjfshop123.live.message;

import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;

import java.util.List;

//0.0
public class TIMConversationUtil {

    public static void lastMessage(){
        List<TIMConversation> TIMSessions = TIMManagerExt.getInstance().getConversationList();
        for (int i = 0; i < TIMSessions.size(); i++) {
            TIMConversationExt ext = new TIMConversationExt(TIMSessions.get(i));
            TIMMessage item = ext.getLastMsg();

            final IMConversationDB imConversationDB = ConversationFactory.getMessage(item, false);
            final MessageDB messageDB = MessageFactory.getMessage(item, false);
            if (messageDB != null && messageDB.getType() == 10){

                /*if (messageDB.getIsHangup() != 0){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, false);
                        }
                    }, 1000);
                }else {
                    SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, false);
                }*/

                continue;
            }

            if (messageDB != null && (messageDB.getType() == 12 || messageDB.getType() == 13)){
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SealAppContext.getInstance().systemMessage(messageDB);
                    }
                }, 1000);*/
                continue;
            }

            if (imConversationDB != null && messageDB != null) {
                RealmMessageUtils.addMessageMsg(messageDB, imConversationDB);
            } else if (imConversationDB != null) {
                RealmConverUtils.addConverMsg(imConversationDB);
            } else if (messageDB != null){
                RealmMessageUtils.addMessageMsg(messageDB, null);
            }
        }
    }
}
