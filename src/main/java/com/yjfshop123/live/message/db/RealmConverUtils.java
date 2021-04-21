package com.yjfshop123.live.message.db;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class RealmConverUtils {

    private static RealmAsyncTask addConverTask;
    private static RealmAsyncTask  deleteConverTask;
    private static RealmAsyncTask  deleteAllConverTask;
    private static RealmAsyncTask  clerRedCountTask;
    private static RealmAsyncTask  clerRedCountTask_;
    private static RealmAsyncTask  deleteSystemConverTask;
    private static RealmAsyncTask  clerCommunityRedCountTask;

    public static String conversationId = null;

    //添加更改Conversation
    public static void addConverMsg(final IMConversationDB imConversationDB) {
        addConverTask =  Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IMConversationDB imConversationDB_ = realm.where(IMConversationDB.class).equalTo("conversationId", imConversationDB.getConversationId()).findFirst();
                if (imConversationDB_ == null){
                    if (conversationId == null || !conversationId.equals(imConversationDB.getConversationId())){
                        imConversationDB.setUnreadCount(1);
                    }else {
                        imConversationDB.setUnreadCount(0);
                    }
                    realm.copyToRealm(imConversationDB);
                }else{
                    imConversationDB_.setUserAvatar(imConversationDB.getUserAvatar());
                    imConversationDB_.setUserName(imConversationDB.getUserName());
                    imConversationDB_.setOtherPartyName(imConversationDB.getOtherPartyName());
                    imConversationDB_.setOtherPartyAvatar(imConversationDB.getOtherPartyAvatar());
                    imConversationDB_.setLastMessage(imConversationDB.getLastMessage());
                    imConversationDB_.setTimestamp(imConversationDB.getTimestamp());
                    long unreadCount = 0;
                    if (conversationId == null || !conversationId.equals(imConversationDB.getConversationId())){
                        unreadCount = imConversationDB_.getUnreadCount();
                        unreadCount ++;
                    }
                    imConversationDB_.setUnreadCount(unreadCount);
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
     * 去所有消息红点
     */
    public static void clerRedCount(final String mi_platformId){
        clerRedCountTask =   Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<IMConversationDB> realmResultd = realm.where(IMConversationDB.class).
                        notEqualTo("conversationId", "forum_notice_like" + mi_platformId).
                        notEqualTo("conversationId", "forum_notice_reply" + mi_platformId).
                        findAll();
                for (int i = 0; i < realmResultd.size(); i++) {
                    realmResultd.get(i).setUnreadCount(0);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //
            }
        });
    }

    /**
     *去除社区消息红点
     *
     * @param mi_platformId
     */
    public static void clerCommunityRedCount(final String mi_platformId){
        clerCommunityRedCountTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IMConversationDB imConversationDB_1 = realm.where(IMConversationDB.class).equalTo("conversationId", "forum_notice_like" + mi_platformId).findFirst();
                IMConversationDB imConversationDB_2 = realm.where(IMConversationDB.class).equalTo("conversationId", "forum_notice_reply" + mi_platformId).findFirst();

                if (imConversationDB_1 != null){
                    imConversationDB_1.setUnreadCount(0);
                }

                if (imConversationDB_2 != null){
                    imConversationDB_2.setUnreadCount(0);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //
            }
        });
    }

    /**
     * 去消息红点
     *
     * @param conversationId
     */
    public static void clerRedCount_(final String conversationId){
        clerRedCountTask_ =  Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IMConversationDB imConversationDB_ = realm.where(IMConversationDB.class).equalTo("conversationId", conversationId).findFirst();
                if (imConversationDB_ != null){
                    imConversationDB_.setUnreadCount(0);
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

    //删除会话
    public static void deleteConverMsg(final String conversationId){
        deleteConverTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IMConversationDB imConversationDB_ = realm.where(IMConversationDB.class).equalTo("conversationId",conversationId).findFirst();
                if (imConversationDB_ != null){
                    imConversationDB_.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                NLog.e("TAGTAG", "删除消息成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                NLog.e("TAGTAG", "删除消息失败");
            }
        });
    }

    /**
     * 清空 所有消息 不包括系统消息 社区消息
     */
    public static void deleteAllConverMsg(final String mi_platformId){
        deleteAllConverTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<IMConversationDB> realmResults = realm.where(IMConversationDB.class).
                        notEqualTo("conversationId", "system_notice" + mi_platformId).
                        notEqualTo("conversationId", "forum_notice_like" + mi_platformId).
                        notEqualTo("conversationId", "forum_notice_reply" + mi_platformId).
                        findAll();
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
     * 清空 系统消息 会话
     */
    public static void deleteSystemConverMsg(final String mi_platformId){
        deleteSystemConverTask = Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IMConversationDB imConversationDB = realm.where(IMConversationDB.class).equalTo("conversationId", "system_notice" + mi_platformId).findFirst();
                if (imConversationDB != null){
                    imConversationDB.deleteFromRealm();
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

    //onDestroy 时调用
    public static void cancel(){
        if (addConverTask != null && !addConverTask.isCancelled()){
            addConverTask.cancel();
        }

        if (deleteConverTask != null && !deleteConverTask.isCancelled()){
            deleteConverTask.cancel();
        }

        if (clerRedCountTask != null && !clerRedCountTask.isCancelled()){
            clerRedCountTask.cancel();
        }

        if (clerRedCountTask_ != null && !clerRedCountTask_.isCancelled()){
            clerRedCountTask_.cancel();
        }

        if (deleteAllConverTask != null && !deleteAllConverTask.isCancelled()){
            deleteAllConverTask.cancel();
        }

        if (deleteSystemConverTask != null && !deleteSystemConverTask.isCancelled()){
            deleteSystemConverTask.cancel();
        }

        if (clerCommunityRedCountTask != null && !clerCommunityRedCountTask.isCancelled()){
            clerCommunityRedCountTask.cancel();
        }
    }

}
