package com.yjfshop123.live.message;

import android.text.SpannableStringBuilder;

import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tencent.imsdk.TIMElemType.Custom;
import static com.tencent.imsdk.TIMElemType.Sound;


public class ConversationFactory {

    private ConversationFactory() {

    }

    public static IMConversationDB getMessage(TIMMessage message, boolean isSend){
        if (message == null){
            return null;
        }

        //管理员消息
        /*TIMConversation timConversation = message.getConversation();
        if (timConversation != null){
            String peer = timConversation.getPeer();
            if (peer != null && peer.equals("administrator")){
                return system(message);
            }
        }*/

        String sender = message.getSender();
        if (sender != null && sender.equals("administrator")){
            return system(message);
        }

        if (message.getElementCount() < 2){
            if (message.getElementCount() == 1 && message.getElement(0).getType() == Custom){
                return textCustom(message, isSend);
            }else {
                return null;
            }
        }

        //Elem 顺序:目前文件和语音 Elem 不一定会按照添加顺序传输
        if (message.getElement(0).getType() == Sound ||
                message.getElement(1).getType() == Sound){
            return voice(message, isSend);
        }
        //

        if (message.getElement(1).getType() != Custom){
            return null;
        }
        switch (message.getElement(0).getType()){
            case Text:
                return text(message, isSend);
            case Image:
                return img(message, isSend);
            /*case Sound:
                return voice(message, isSend);*/
            case Video:
                return video(message, isSend);
            case Custom:
                return custom(message, isSend);
            /*case GroupTips:
                return new GroupTipMessage(message);
            case File:
                return new FileMessage(message);
            case Custom:
                return new CustomMessage(message);
            case UGC:
                return new VideoMessage(message);*/
            default:
                return null;
        }
    }

    private static IMConversationDB custom(TIMMessage message, boolean isSend){
        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                int type = jsonObj.getInt("type");
                // 1语音 2视频 3礼物 4聊天条数限制
                // 5提示充值VIP 6提示金蛋充值 7诱导提示视频通话 8诱导提示语音通话
                if (type == 1 || type == 2){
                    return media(message, isSend);
                } else if (type == 3){
                    return gift(message, isSend);
                } else {
                    return null;
                }
            }catch (IOException | JSONException e_){
            }
        }
        return null;
    }

    private static  IMConversationDB gift(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();
        imConversationDB.setTimestamp(message.timestamp());

        /*TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String giftName = jsonObj.getString("giftName");
                imConversationDB.setLastMessage("[" + giftName + "]");
            }catch (IOException | JSONException e_){

            }
        }*/
        imConversationDB.setLastMessage("[礼物]");

        TIMElem timElem = message.getElement(1);
        if (timElem.getType() == Custom){
            //解剖自定义内容
            TIMCustomElem elem = (TIMCustomElem) timElem;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userIMId = jsonObj.getString("userIMId");
                String userId = jsonObj.getString("userId");
                String otherPartyIMId = jsonObj.getString("otherPartyIMId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                String userName = jsonObj.getString("userName");
                String userAvatar = jsonObj.getString("userAvatar");
                String otherPartyName = jsonObj.getString("otherPartyName");
                String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

                if (isSend){
                    imConversationDB.setUserIMId(userIMId);
                    imConversationDB.setUserId(userId);
                    imConversationDB.setOtherPartyIMId(otherPartyIMId);
                    imConversationDB.setOtherPartyId(otherPartyId);
                    imConversationDB.setUserName(userName);
                    imConversationDB.setUserAvatar(userAvatar);
                    imConversationDB.setOtherPartyName(otherPartyName);
                    imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
                }else {//接收 兑换 他人更自己
                    imConversationDB.setUserIMId(otherPartyIMId);
                    imConversationDB.setUserId(otherPartyId);
                    imConversationDB.setOtherPartyIMId(userIMId);
                    imConversationDB.setOtherPartyId(userId);
                    imConversationDB.setUserName(otherPartyName);
                    imConversationDB.setUserAvatar(otherPartyAvatar);
                    imConversationDB.setOtherPartyName(userName);
                    imConversationDB.setOtherPartyAvatar(userAvatar);
                }
                imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
            }catch (IOException | JSONException e){

            }
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        return imConversationDB;
    }

    private static  IMConversationDB system(TIMMessage message){
        long timestamp = message.timestamp();
        IMConversationDB imConversationDB = new IMConversationDB();
        imConversationDB.setTimestamp(timestamp);

        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);

                String code = jsonObj.getString("code");
                if (code.equals("system_notice")){
                    imConversationDB.setType(2);// 0 单聊  1 群聊  2 系统消息 3 弹框提醒 4关闭视频
                    String subject = jsonObj.getString("subject");
                    String content = jsonObj.getString("content");

                    String mi_platformId = UserInfoUtil.getMiPlatformId();
                    imConversationDB.setConversationId("system_notice" + mi_platformId);
                    imConversationDB.setLastMessage(content);
                    imConversationDB.setOtherPartyName(subject);
                    imConversationDB.setUserIMId(mi_platformId);
                }else if(code.equals("trtc_notice")){
                    //弹框提醒
                    imConversationDB.setType(3);
                }else if(code.equals("trtc_close")){
                    //关闭视频
                    imConversationDB.setType(4);
                }else if (code.equals("forum_notice")){
                    //社区消息
                    //{"code":"forum_notice","subject":"社区消息","content":{"type":"reply","method":"plus"}}
                    //content:{"type":"like/reply","method":"plus/minus"}
                    JSONObject jso_ = jsonObj.getJSONObject("content");
                    String type_ = jso_.getString("type");
                    if (type_.equals("like")){
                        imConversationDB.setType(5);// 0 单聊  1 群聊  2 系统消息 3 弹框提醒 4关闭视频 => 5社区消息顶 6社区消息回复
                        String mi_platformId = UserInfoUtil.getMiPlatformId();
                        imConversationDB.setConversationId("forum_notice_like" + mi_platformId);
                        imConversationDB.setUserIMId(mi_platformId);
                    }else if (type_.equals("reply")){
                        imConversationDB.setType(6);// 0 单聊  1 群聊  2 系统消息 3 弹框提醒 4关闭视频 => 5社区消息顶 6社区消息回复
                        String mi_platformId = UserInfoUtil.getMiPlatformId();
                        imConversationDB.setConversationId("forum_notice_reply" + mi_platformId);
                        imConversationDB.setUserIMId(mi_platformId);
                    }
                }else if (code.equals("gift_notice")){
                    JSONObject contentJso = jsonObj.getJSONObject("content");
                    int style = contentJso.getInt("style");
                    if (style == 1){
//                        JSONObject contentJso2 = contentJso.getJSONObject("content");
//                        String from_username = contentJso2.getString("from_username");
//                        String to_username = contentJso2.getString("to_username");
//                        String gift_name = contentJso2.getString("gift_name");
//                        String img_url = contentJso2.getString("img_url");
//                        SealAppContext.getInstance().floatingMessage(from_username + "@@" +
//                                to_username + "@@" + gift_name + "@@" + img_url);
                        return null;
                    }else {
                        return null;
                    }
                }else if (code.equals("new_user_notice")){
                    JSONObject contentJso = jsonObj.getJSONObject("content");
                    int style = contentJso.getInt("style");
                    if (style == 1){
//                        JSONObject contentJso2 = contentJso.getJSONObject("content");
//
//                        String user_id = contentJso2.getString("user_id");
//                        String prom_custom_uid = contentJso2.getString("prom_custom_uid");
//                        String user_nickname = contentJso2.getString("user_nickname");
//                        String avatar = contentJso2.getString("avatar");
//                        String content = contentJso2.getString("content");
//                        String sex = contentJso2.getString("sex");
//
//                        SealAppContext.getInstance().floatingMessage2(
//                                user_id + "@@" +
//                                        prom_custom_uid + "@@" +
//                                        user_nickname + "@@" +
//                                        avatar + "@@" +
//                                        content + "@@" +
//                                        sex);
                        return null;
                    }else {
                        return null;
                    }
                }else{
                    return null;
                }
            }catch (IOException | JSONException e_){
            }
        }

        imConversationDB.setUnreadCount(1);

        return imConversationDB;
    }

    private static  IMConversationDB media(TIMMessage message, boolean isSend){
        long timestamp = message.timestamp();
        long systemTime = System.currentTimeMillis() / 1000;
        if ((systemTime - timestamp) > 30){
            return null;
        }

        IMConversationDB imConversationDB = new IMConversationDB();
        imConversationDB.setTimestamp(message.timestamp());

        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);

//                String roomdbId = jsonObj.getString("roomdbId");
//                int roomId = jsonObj.getInt("roomId");
//                int cost = jsonObj.getInt("cost");

                int type = jsonObj.getInt("type");
                int isHangup = jsonObj.getInt("isHangup");

                if (isHangup == 1){
                    if (type == 1){
                        imConversationDB.setLastMessage("[语音通话]");
                    }else{
                        imConversationDB.setLastMessage("[视频通话]");
                    }
                }
            }catch (IOException | JSONException e_){

            }
        }

        TIMElem timElem = message.getElement(1);
        if (timElem.getType() == Custom){
            //解剖自定义内容
            TIMCustomElem elem = (TIMCustomElem) timElem;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userIMId = jsonObj.getString("userIMId");
                String userId = jsonObj.getString("userId");
                String otherPartyIMId = jsonObj.getString("otherPartyIMId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                String userName = jsonObj.getString("userName");
                String userAvatar = jsonObj.getString("userAvatar");
                String otherPartyName = jsonObj.getString("otherPartyName");
                String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

                if (isSend){
                    imConversationDB.setUserIMId(userIMId);
                    imConversationDB.setUserId(userId);
                    imConversationDB.setOtherPartyIMId(otherPartyIMId);
                    imConversationDB.setOtherPartyId(otherPartyId);
                    imConversationDB.setUserName(userName);
                    imConversationDB.setUserAvatar(userAvatar);
                    imConversationDB.setOtherPartyName(otherPartyName);
                    imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
                }else {//接收 兑换 他人更自己
                    imConversationDB.setUserIMId(otherPartyIMId);
                    imConversationDB.setUserId(otherPartyId);
                    imConversationDB.setOtherPartyIMId(userIMId);
                    imConversationDB.setOtherPartyId(userId);
                    imConversationDB.setUserName(otherPartyName);
                    imConversationDB.setUserAvatar(otherPartyAvatar);
                    imConversationDB.setOtherPartyName(userName);
                    imConversationDB.setOtherPartyAvatar(userAvatar);
                }
                imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
            }catch (IOException | JSONException e){

            }
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        return imConversationDB;
    }

    private static  IMConversationDB text(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();

        imConversationDB.setTimestamp(message.timestamp());

        List<TIMElem> elems = new ArrayList<>();
        for (int i = 0; i < message.getElementCount(); ++i){
            elems.add(message.getElement(i));
        }

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i){
            switch (elems.get(i).getType()){
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
                case Custom:
                    //解剖自定义内容
                    TIMCustomElem elem = (TIMCustomElem) elems.get(i);
                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        String userIMId = jsonObj.getString("userIMId");
                        String userId = jsonObj.getString("userId");
                        String otherPartyIMId = jsonObj.getString("otherPartyIMId");
                        String otherPartyId = jsonObj.getString("otherPartyId");

                        String userName = jsonObj.getString("userName");
                        String userAvatar = jsonObj.getString("userAvatar");
                        String otherPartyName = jsonObj.getString("otherPartyName");
                        String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

                        if (isSend){
                            imConversationDB.setUserIMId(userIMId);
                            imConversationDB.setUserId(userId);
                            imConversationDB.setOtherPartyIMId(otherPartyIMId);
                            imConversationDB.setOtherPartyId(otherPartyId);
                            imConversationDB.setUserName(userName);
                            imConversationDB.setUserAvatar(userAvatar);
                            imConversationDB.setOtherPartyName(otherPartyName);
                            imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
                        }else {//接收 兑换 他人更自己
                            imConversationDB.setUserIMId(otherPartyIMId);
                            imConversationDB.setUserId(otherPartyId);
                            imConversationDB.setOtherPartyIMId(userIMId);
                            imConversationDB.setOtherPartyId(userId);
                            imConversationDB.setUserName(otherPartyName);
                            imConversationDB.setUserAvatar(otherPartyAvatar);
                            imConversationDB.setOtherPartyName(userName);
                            imConversationDB.setOtherPartyAvatar(userAvatar);
                        }
                        imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
                    }catch (IOException | JSONException e){

                    }
                    break;
            }
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        imConversationDB.setLastMessage(stringBuilder.toString());
        return imConversationDB;
    }

    private static  IMConversationDB textCustom(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();
        imConversationDB.setTimestamp(message.timestamp());

        //解剖自定义内容
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        try{
            String str = new String(elem.getData(), "UTF-8");
            JSONObject jsonObj = new JSONObject(str).getJSONObject("data");
            String userIMId = jsonObj.getString("userIMId");
            String userId = jsonObj.getString("userId");
            String otherPartyIMId = jsonObj.getString("otherPartyIMId");
            String otherPartyId = jsonObj.getString("otherPartyId");

            String userName = jsonObj.getString("userName");
            String userAvatar = jsonObj.getString("userAvatar");
            String otherPartyName = jsonObj.getString("otherPartyName");
            String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

            if (isSend){
                imConversationDB.setUserIMId(userIMId);
                imConversationDB.setUserId(userId);
                imConversationDB.setOtherPartyIMId(otherPartyIMId);
                imConversationDB.setOtherPartyId(otherPartyId);
                imConversationDB.setUserName(userName);
                imConversationDB.setUserAvatar(userAvatar);
                imConversationDB.setOtherPartyName(otherPartyName);
                imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
            }else {//接收 兑换 他人更自己
                imConversationDB.setUserIMId(otherPartyIMId);
                imConversationDB.setUserId(otherPartyId);
                imConversationDB.setOtherPartyIMId(userIMId);
                imConversationDB.setOtherPartyId(userId);
                imConversationDB.setUserName(otherPartyName);
                imConversationDB.setUserAvatar(otherPartyAvatar);
                imConversationDB.setOtherPartyName(userName);
                imConversationDB.setOtherPartyAvatar(userAvatar);
            }
            imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());

            imConversationDB.setLastMessage(jsonObj.getJSONObject("msgBody").getString("content"));
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        return imConversationDB;
    }

    private static  IMConversationDB img(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();

        imConversationDB.setTimestamp(message.timestamp());

        TIMElem timElem = message.getElement(1);
        if (timElem.getType() == Custom){
            //解剖自定义内容
            TIMCustomElem elem = (TIMCustomElem) timElem;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userIMId = jsonObj.getString("userIMId");
                String userId = jsonObj.getString("userId");
                String otherPartyIMId = jsonObj.getString("otherPartyIMId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                String userName = jsonObj.getString("userName");
                String userAvatar = jsonObj.getString("userAvatar");
                String otherPartyName = jsonObj.getString("otherPartyName");
                String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

                if (isSend){
                    imConversationDB.setUserIMId(userIMId);
                    imConversationDB.setUserId(userId);
                    imConversationDB.setOtherPartyIMId(otherPartyIMId);
                    imConversationDB.setOtherPartyId(otherPartyId);
                    imConversationDB.setUserName(userName);
                    imConversationDB.setUserAvatar(userAvatar);
                    imConversationDB.setOtherPartyName(otherPartyName);
                    imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
                }else {//接收 兑换 他人更自己
                    imConversationDB.setUserIMId(otherPartyIMId);
                    imConversationDB.setUserId(otherPartyId);
                    imConversationDB.setOtherPartyIMId(userIMId);
                    imConversationDB.setOtherPartyId(userId);
                    imConversationDB.setUserName(otherPartyName);
                    imConversationDB.setUserAvatar(otherPartyAvatar);
                    imConversationDB.setOtherPartyName(userName);
                    imConversationDB.setOtherPartyAvatar(userAvatar);
                }
                imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
            }catch (IOException | JSONException e){

            }
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        imConversationDB.setLastMessage("[图片]");
        return imConversationDB;
    }

    private static  IMConversationDB voice(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();

        imConversationDB.setTimestamp(message.timestamp());

        TIMCustomElem timCustomElem = null;
        for (int i = 0; i < message.getElementCount(); i++) {
            TIMElem timElem = message.getElement(i);
            if (timElem.getType() == Custom){
                timCustomElem = (TIMCustomElem) timElem;
            }
        }

        try{
            String str = new String(timCustomElem.getData(), "UTF-8");
            JSONObject jsonObj = new JSONObject(str);
            String userIMId = jsonObj.getString("userIMId");
            String userId = jsonObj.getString("userId");
            String otherPartyIMId = jsonObj.getString("otherPartyIMId");
            String otherPartyId = jsonObj.getString("otherPartyId");

            String userName = jsonObj.getString("userName");
            String userAvatar = jsonObj.getString("userAvatar");
            String otherPartyName = jsonObj.getString("otherPartyName");
            String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

            if (isSend){
                imConversationDB.setUserIMId(userIMId);
                imConversationDB.setUserId(userId);
                imConversationDB.setOtherPartyIMId(otherPartyIMId);
                imConversationDB.setOtherPartyId(otherPartyId);
                imConversationDB.setUserName(userName);
                imConversationDB.setUserAvatar(userAvatar);
                imConversationDB.setOtherPartyName(otherPartyName);
                imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
            }else {//接收 兑换 他人更自己
                imConversationDB.setUserIMId(otherPartyIMId);
                imConversationDB.setUserId(otherPartyId);
                imConversationDB.setOtherPartyIMId(userIMId);
                imConversationDB.setOtherPartyId(userId);
                imConversationDB.setUserName(otherPartyName);
                imConversationDB.setUserAvatar(otherPartyAvatar);
                imConversationDB.setOtherPartyName(userName);
                imConversationDB.setOtherPartyAvatar(userAvatar);
            }
            imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
        }catch (IOException | JSONException e){

        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        imConversationDB.setLastMessage("[语音]");
        return imConversationDB;
    }

    private static  IMConversationDB video(TIMMessage message, boolean isSend){
        IMConversationDB imConversationDB = new IMConversationDB();

        imConversationDB.setTimestamp(message.timestamp());

        TIMElem timElem = message.getElement(1);
        if (timElem.getType() == Custom){
            //解剖自定义内容
            TIMCustomElem elem = (TIMCustomElem) timElem;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userIMId = jsonObj.getString("userIMId");
                String userId = jsonObj.getString("userId");
                String otherPartyIMId = jsonObj.getString("otherPartyIMId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                String userName = jsonObj.getString("userName");
                String userAvatar = jsonObj.getString("userAvatar");
                String otherPartyName = jsonObj.getString("otherPartyName");
                String otherPartyAvatar = jsonObj.getString("otherPartyAvatar");

                if (isSend){
                    imConversationDB.setUserIMId(userIMId);
                    imConversationDB.setUserId(userId);
                    imConversationDB.setOtherPartyIMId(otherPartyIMId);
                    imConversationDB.setOtherPartyId(otherPartyId);
                    imConversationDB.setUserName(userName);
                    imConversationDB.setUserAvatar(userAvatar);
                    imConversationDB.setOtherPartyName(otherPartyName);
                    imConversationDB.setOtherPartyAvatar(otherPartyAvatar);
                }else {//接收 兑换 他人更自己
                    imConversationDB.setUserIMId(otherPartyIMId);
                    imConversationDB.setUserId(otherPartyId);
                    imConversationDB.setOtherPartyIMId(userIMId);
                    imConversationDB.setOtherPartyId(userId);
                    imConversationDB.setUserName(otherPartyName);
                    imConversationDB.setUserAvatar(otherPartyAvatar);
                    imConversationDB.setOtherPartyName(userName);
                    imConversationDB.setOtherPartyAvatar(userAvatar);
                }
                imConversationDB.setConversationId(imConversationDB.getUserId() + "@@" +imConversationDB.getOtherPartyId());
            }catch (IOException | JSONException e){

            }
        }

        imConversationDB.setType(0);//0单聊
        imConversationDB.setUnreadCount(1);

        imConversationDB.setLastMessage("[视频]");
        return imConversationDB;
    }



}
