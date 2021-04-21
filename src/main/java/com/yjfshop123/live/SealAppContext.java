package com.yjfshop123.live;

import android.content.Context;
import android.content.Intent;

import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MediaModel;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;

import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.ui.activity.InducedActivity;
import com.yjfshop123.live.ui.activity.RoomActivity;


public class SealAppContext {

    private Context mContext;

    private static SealAppContext sealAppContext;

    public SealAppContext(Context mContext) {
        this.mContext = mContext;
    }

    public static void init(Context context) {
        if (sealAppContext == null) {
            synchronized (SealAppContext.class) {
                if (sealAppContext == null) {
                    sealAppContext = new SealAppContext(context);
                }
            }
        }
    }

    public static SealAppContext getInstance() {
        return sealAppContext;
    }

    private long time = 0;

    public void mediaMessage(IMConversationDB imConversationDB, MessageDB messageDB, boolean isSender){
        if (messageDB.getType() == 10){
            if (messageDB.getIsHangup() == 0){
                if (RoomActivity.roomdbId != null){
                    //当前正在通话 有通话消息 不做处理
                    return;
                }

                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime - time < 2){
                    return;
                }
                time = currentTime;

                Intent intent = new Intent(mContext, RoomActivity.class);

                IMConversation imConversation = new IMConversation();
                imConversation.setType(imConversationDB.getType());
                imConversation.setUserIMId(imConversationDB.getUserIMId());
                imConversation.setOtherPartyIMId(imConversationDB.getOtherPartyIMId());
                imConversation.setUserId(imConversationDB.getUserId());
                imConversation.setOtherPartyId(imConversationDB.getOtherPartyId());
                imConversation.setUserName(imConversationDB.getUserName());;
                imConversation.setUserAvatar(imConversationDB.getUserAvatar());
                imConversation.setOtherPartyName(imConversationDB.getOtherPartyName());
                imConversation.setOtherPartyAvatar(imConversationDB.getOtherPartyAvatar());
                imConversation.setConversationId(imConversationDB.getConversationId());
                MediaModel mediaModel = new MediaModel();
                mediaModel.setIsHangup(messageDB.getIsHangup());
                mediaModel.setMediaType(messageDB.getMediaType());
                mediaModel.setRoomdbId(messageDB.getRoomdbId());
                mediaModel.setRoomId(messageDB.getRoomId());
                mediaModel.setCost(messageDB.getCost());

                intent.putExtra("IMConversation", imConversation);
                intent.putExtra("isSender", isSender);
                intent.putExtra("mediaModel", mediaModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                //接到电话
                //不做数据库处理
            }else {
                if (messageDB == null){
                    return;
                }

                if (RoomActivity.roomdbId != null &&
                        RoomActivity.roomdbId.equals(messageDB.getRoomdbId())){
                    //当前正在通话 且 挂断ID 等于 通话ID

                    if (imConversationDB != null){
                        RealmConverUtils.addConverMsg(imConversationDB);
                    }

                    if (RoomActivity.send){
                        messageDB.setIsSender(1);
                    }else {
                        messageDB.setIsSender(0);
                    }
                    if (!isSender){
                        BroadcastManager.getInstance(mContext).sendBroadcast("HANG_UP");
                    }

                    messageDB.setIsSenderResult(1);
                    long time = RoomActivity.time;
                    if (time == 0){
                        messageDB.setText("");
                    }else {
                        if (time >= 3600) {
                            messageDB.setText(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
                        } else {
                            messageDB.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
                        }
                    }
                    RealmMessageUtils.addMessageMsg(messageDB, null);
                }else {
                    //当前未通话
                    //当前通话ID与挂断通话消息ID不同

                    if (imConversationDB != null){
                        RealmConverUtils.addConverMsg(imConversationDB);
                    }

                    if (RoomActivity.send){
                        messageDB.setIsSender(1);
                    }else {
                        messageDB.setIsSender(0);
                    }

                    messageDB.setIsSenderResult(1);
                    messageDB.setText("");
                    RealmMessageUtils.addMessageMsg(messageDB, null);
                }
            }
        }
    }

    public void systemMessage(MessageDB messageDB){
        if (messageDB == null){
            return;
        }

        if (RoomActivity.roomdbId == null){
            //当前通话已经结束不做处理
            return;
        }

        if (RoomActivity.roomId != messageDB.getRoomId()){
            //ID不同 不做处理
            return;
        }

        if (messageDB.getType() == 12 ){
            //弹框提醒
            BroadcastManager.getInstance(mContext).sendBroadcast("close_notice", 12, messageDB.getText());
            return;
        }

        if (messageDB.getType() == 13 ){
            //关闭视频
            BroadcastManager.getInstance(mContext).sendBroadcast("close_notice", 13, messageDB.getText());
            return;
        }
    }

    public void sendInduced(IMConversation imConversation, int type){
        Intent intent = new Intent(mContext, InducedActivity.class);
        intent.putExtra("IMConversation", imConversation);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
