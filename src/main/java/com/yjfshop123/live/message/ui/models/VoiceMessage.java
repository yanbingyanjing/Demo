package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;

import org.json.JSONException;
import org.json.JSONObject;

public class VoiceMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    public VoiceMessage(long duration, String filePath, IMConversation imConversationDB){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);
        message.addElement(elem);

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

        message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[语音]"));
    }

}
