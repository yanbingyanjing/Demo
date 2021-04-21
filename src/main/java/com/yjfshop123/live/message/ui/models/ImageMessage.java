package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    public ImageMessage(String path, boolean isOri, IMConversation imConversationDB){
        message = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        elem.setLevel(isOri ? 0 : 1);
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

        message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[图片]"));
    }

}
