package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    /**
     * @param roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
     * @param roomId 云通信的房间号
     * @param type 1 语音  2 视频
     * @param isHangup 默认 0 接收通话  1 挂断通话
     * @param cost 费用
     * @param imConversationDB
     */
    public MediaMessage(String roomdbId, int roomId, int type, int isHangup, int cost, IMConversation imConversationDB){
        message = new TIMMessage();

        String data_ = "";
        JSONObject dataJson_ = new JSONObject();
        try{
            dataJson_.put("roomdbId", roomdbId);
            dataJson_.put("roomId", roomId);
            dataJson_.put("type", type);
            dataJson_.put("isHangup", isHangup);
            dataJson_.put("cost", cost);
            data_ = dataJson_.toString();
        }catch (JSONException e){
        }
        TIMCustomElem customElem_ = new TIMCustomElem();
        customElem_.setData(data_.getBytes());
        message.addElement(customElem_);


        String data = "";
        JSONObject dataJson = new JSONObject();
        try{
            dataJson.put("userIMId",imConversationDB.getUserIMId());
            dataJson.put("otherPartyIMId",imConversationDB.getOtherPartyIMId());
            dataJson.put("userId",imConversationDB.getUserId());
            dataJson.put("otherPartyId",imConversationDB.getOtherPartyId());
            dataJson.put("userName", imConversationDB.getUserName());
            dataJson.put("userAvatar", imConversationDB.getUserAvatar());
            dataJson.put("otherPartyName", imConversationDB.getOtherPartyName());
            dataJson.put("otherPartyAvatar", imConversationDB.getOtherPartyAvatar());
            data = dataJson.toString();
        }catch (JSONException e){
        }
        TIMCustomElem customElem = new TIMCustomElem();
        customElem.setData(data.getBytes());
        message.addElement(customElem);

        if (isHangup == 0){
            if (type == 1){
                message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[语音]", true));
            }else if (type == 2){
                message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[视频]", true));
            }
        }
    }
}
