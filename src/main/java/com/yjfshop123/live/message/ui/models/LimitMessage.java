package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class LimitMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    //4限制消息条数
    public LimitMessage(int limitMessageNum, IMConversation imConversationDB){
        message = new TIMMessage();

        String data_ = "";
        JSONObject dataJson_ = new JSONObject();
        try{
            //type 4:限制消息类型
            //limitMessageNum 限制消息条数
            dataJson_.put("type", 4);
            dataJson_.put("limitMessageNum", limitMessageNum);
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
    }
}
