package com.yjfshop123.live.message.db;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class RealmMessageUtils {

    private static RealmAsyncTask addMessageConverTask;
    private static RealmAsyncTask deleteAllMessageTask;
    private static RealmAsyncTask deleteMessageTask;
    private static RealmAsyncTask deleteCoverMessageTask;
    private static RealmAsyncTask deleteAllSystemMessageTask;

    public static void addMessageMsg(final MessageDB messageDB, final IMConversationDB imConversationDB) {
        addMessageConverTask =  Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean isMess = false;
                MessageDB messageDB_ = realm.where(MessageDB.class).equalTo("messageId", messageDB.getMessageId()).findFirst();
                if (messageDB_ == null){
                    realm.copyToRealm(messageDB);
                    isMess = true;
                }else {
                    messageDB_.setIsSenderResult(messageDB.getIsSenderResult());
                }


                if (imConversationDB != null && isMess){
                    IMConversationDB imConversationDB_ = realm.where(IMConversationDB.class).equalTo("conversationId", imConversationDB.getConversationId()).findFirst();
                    if (imConversationDB_ == null){
                        if (RealmConverUtils.conversationId == null || !RealmConverUtils.conversationId.equals(imConversationDB.getConversationId())){
                            imConversationDB.setUnreadCount(1);
                        }else {
                            imConversationDB.setUnreadCount(0);
                        }
                        realm.copyToRealm(imConversationDB);
                    }else{
                        if (imConversationDB.getType() != 2){
                            //系统消息 无这些参数
                            imConversationDB_.setUserAvatar(imConversationDB.getUserAvatar());
                            imConversationDB_.setUserName(imConversationDB.getUserName());
                            imConversationDB_.setOtherPartyAvatar(imConversationDB.getOtherPartyAvatar());
                        }

                        imConversationDB_.setOtherPartyName(imConversationDB.getOtherPartyName());
                        imConversationDB_.setLastMessage(imConversationDB.getLastMessage());
                        imConversationDB_.setTimestamp(imConversationDB.getTimestamp());
                        long unreadCount = 0;
                        if (RealmConverUtils.conversationId == null || !RealmConverUtils.conversationId.equals(imConversationDB.getConversationId())){
                            unreadCount = imConversationDB_.getUnreadCount();
                            unreadCount ++;
                        }
                        imConversationDB_.setUnreadCount(unreadCount);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                NLog.e("TAGTAG", "添加更改消息成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                NLog.e("TAGTAG", "添加更改消息失败");
            }
        });
    }

    /**
     * 清空 所有消息 不包括系统消息
     */
    public static void deleteAllMessageMsg(final String mi_platformId){
        deleteAllMessageTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MessageDB> realmResults = realm.where(MessageDB.class).notEqualTo("conversationId", "system_notice" + mi_platformId).findAll();
                realmResults.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    /**
     * 删除所有系统消息
     */
    public static void deleteAllSystemMsg(final String mi_platformId){
        deleteAllSystemMessageTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MessageDB> realmResults = realm.where(MessageDB.class).equalTo("conversationId", "system_notice" + mi_platformId).findAll();
                if (realmResults != null){
                    realmResults.deleteAllFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    /**
     * 删除 对应ID的消息
     */
    public static void deleteMessageMsg(final String messageId){
        deleteMessageTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageDB messageDB_ = realm.where(MessageDB.class).equalTo("messageId", messageId).findFirst();
                if (messageDB_ != null){
                    messageDB_.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    /**
     * 删除会话的所有消息
     *
     * @param conversationId
     */
    public static void deleteCoverMessageMsg(final String conversationId){
        deleteCoverMessageTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MessageDB> realmResults = realm.where(MessageDB.class).equalTo("conversationId", conversationId).findAll();
                for (int i = 0; i < realmResults.size(); i++) {
                    realmResults.get(i).deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    public static void cancel(){
        if (addMessageConverTask != null && !addMessageConverTask.isCancelled()){
            addMessageConverTask.cancel();
        }

        if (deleteAllMessageTask != null && !deleteAllMessageTask.isCancelled()){
            deleteAllMessageTask.cancel();
        }

        if (deleteMessageTask != null && !deleteMessageTask.isCancelled()){
            deleteMessageTask.cancel();
        }

        if (deleteCoverMessageTask != null && !deleteCoverMessageTask.isCancelled()){
            deleteCoverMessageTask.cancel();
        }

        if (deleteAllSystemMessageTask != null && !deleteAllSystemMessageTask.isCancelled()){
            deleteAllSystemMessageTask.cancel();
        }
    }

}
