package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class GiftMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    //3礼物
    public GiftMessage(String giftName, String giftIconImg, String giftEffectImg, String giftCoinZh, IMConversation imConversationDB){
        message = new TIMMessage();

        String data_ = "";
        JSONObject dataJson_ = new JSONObject();
        try{
            //type 3礼物
            //giftName 礼物名称
            //giftIconImg 礼物图片
            //giftEffectImg 礼物gif图
            //giftCoinZh 礼物价值
            dataJson_.put("type", 3);
            dataJson_.put("giftName", giftName);
            dataJson_.put("giftIconImg", giftIconImg);
            dataJson_.put("giftEffectImg", giftEffectImg);
            dataJson_.put("giftCoinZh", giftCoinZh);
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

        message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[礼物]"));
    }


}
