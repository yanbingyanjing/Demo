package com.yjfshop123.live.message;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.model.VIPEvent;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tencent.imsdk.TIMElemType.Custom;
import static com.tencent.imsdk.TIMElemType.Sound;

public class MessageFactory {

    private MessageFactory() {

    }

    public static MessageDB getMessage(TIMMessage message, boolean isSend){
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

    private static MessageDB custom(TIMMessage message, boolean isSend){
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
                }else if (type == 3){
                    return gift(message, isSend);
                }else if (type == 4){
                    int limitMessageNum = jsonObj.getInt("limitMessageNum");
                    UserInfoUtil.setLimitMessageNum(limitMessageNum);
                    UserInfoUtil.setUserMessageNum(0);
                    return null;
                }else if (type == 5){
                    //在聊天页直接提示，不在聊天页面保存下次进入提示
                    //message.getMsgId()@@true
                    String vipMessage = UserInfoUtil.getVIPMessage();
                    if (!TextUtils.isEmpty(vipMessage)){
                        String [] vipMessages = UserInfoUtil.getVIPMessage().split("@@");
                        if (!vipMessages[0].equals(message.getMsgId())){
                            UserInfoUtil.setVIPMessage(message.getMsgId() + "@@" +"false");
                            EventBus.getDefault().post(new VIPEvent(1),Config.EVENT_START);
                        }else {}
                    }else {
                        UserInfoUtil.setVIPMessage(message.getMsgId() + "@@" +"false");
                        EventBus.getDefault().post(new VIPEvent(1),Config.EVENT_START);
                    }
                    return null;
                }else if (type == 6){
                    String goldMessage = UserInfoUtil.getGoldMessage();
                    if (!TextUtils.isEmpty(goldMessage)){
                        String [] goldMessages = UserInfoUtil.getGoldMessage().split("@@");
                        if (!goldMessages[0].equals(message.getMsgId())){
                            UserInfoUtil.setGoldMessage(message.getMsgId() + "@@" +"false");
                            EventBus.getDefault().post(new VIPEvent(2),Config.EVENT_START);
                        }else {}
                    }else {
                        UserInfoUtil.setGoldMessage(message.getMsgId() + "@@" +"false");
                        EventBus.getDefault().post(new VIPEvent(2),Config.EVENT_START);
                    }
                    return null;
                }else if(type == 7){
                    induced((TIMCustomElem) message.getElement(1), isSend, 1);
                    return null;
                }else if(type == 8){
                    induced((TIMCustomElem) message.getElement(1), isSend, 2);
                    return null;
                }else {
                    return null;
                }
            }catch (IOException | JSONException e_){
            }
        }
        return null;
    }

    private static void induced(TIMCustomElem elem, boolean isSend, int type){
        if (isSend){
            return;
        }
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

            IMConversation imConversation = new IMConversation();
            if (isSend){
//                imConversation.setUserIMId(userIMId);
//                imConversation.setUserId(userId);
//                imConversation.setOtherPartyIMId(otherPartyIMId);
//                imConversation.setOtherPartyId(otherPartyId);
//                imConversation.setUserName(userName);
//                imConversation.setUserAvatar(userAvatar);
//                imConversation.setOtherPartyName(otherPartyName);
//                imConversation.setOtherPartyAvatar(otherPartyAvatar);
            }else {//接收 兑换 他人更自己
                imConversation.setUserIMId(otherPartyIMId);
                imConversation.setUserId(otherPartyId);
                imConversation.setOtherPartyIMId(userIMId);
                imConversation.setOtherPartyId(userId);
                imConversation.setUserName(otherPartyName);
                imConversation.setUserAvatar(otherPartyAvatar);
                imConversation.setOtherPartyName(userName);
                imConversation.setOtherPartyAvatar(userAvatar);
            }
            imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息
            imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());
            if (!isSend){
                SealAppContext.getInstance().sendInduced(imConversation, type);
            }
        }catch (IOException | JSONException e){
        }
    }

    private static MessageDB gift(TIMMessage message, boolean isSend){
        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);

                String giftName = jsonObj.getString("giftName");
                String giftIconImg = jsonObj.getString("giftIconImg");
                String giftEffectImg = jsonObj.getString("giftEffectImg");
                String giftCoinZh = jsonObj.getString("giftCoinZh");

                messageDB.setGiftName(giftName);
                messageDB.setGiftIconImg(giftIconImg);
                messageDB.setGiftEffectImg(giftEffectImg);
                messageDB.setGiftCoinZh(giftCoinZh);
            }catch (IOException | JSONException e_){
            }
        }

        TIMElem timElem_1 = message.getElement(1);
        if (timElem_1.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_1;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userId = jsonObj.getString("userId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                if (isSend){
                    messageDB.setConversationId(userId + "@@" + otherPartyId);
                }else {
                    messageDB.setConversationId(otherPartyId + "@@" + userId);
                }
            }catch (IOException | JSONException e_){

            }
        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(4);// 0 文字  1 图片 2 语音  3 视频   4 礼物 10媒体(语音视频通话)
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);

        return messageDB;
    }

    private static MessageDB system(TIMMessage message){
        long timestamp = message.timestamp();

        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(timestamp);

        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);

                String code = jsonObj.getString("code");
                if (code.equals("system_notice")){
                    messageDB.setType(11);// 0 文字  1 图片 2 语音  3 视频 10媒体(语音视频通话) 11系统消息 12弹框提醒 13关闭视频
                    String content = jsonObj.getString("content");
                    messageDB.setText(content);
                    String mi_platformId = UserInfoUtil.getMiPlatformId();
                    messageDB.setConversationId("system_notice" + mi_platformId);
                }else if(code.equals("trtc_notice")){
                    //弹框提醒
                    int home_id = jsonObj.getInt("home_id");
                    String content = jsonObj.getString("content");
                    messageDB.setRoomId(home_id);
                    messageDB.setText(content);
                    messageDB.setType(12);
                }else if(code.equals("trtc_close")){
                    //关闭视频
                    int home_id = jsonObj.getInt("home_id");
                    String content = jsonObj.getString("content");
                    messageDB.setRoomId(home_id);
                    messageDB.setText(content);
                    messageDB.setType(13);
                }else {
                    //{"code":"forum_notice","subject":"社区消息","content":{"type":"reply","method":"plus"}}
                    //content:{"type":"like/reply","method":"plus/minus"}
                    return null;
                }
            }catch (IOException | JSONException e_){
            }
        }

        messageDB.setMessageId(message.getMsgId());
        //是否为发送方 0接收方 1发送方
        messageDB.setIsSender(0);

        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);

        return messageDB;
    }


    private static MessageDB media(TIMMessage message, boolean isSend){
        long timestamp = message.timestamp();
        long systemTime = System.currentTimeMillis() / 1000;
        if ((systemTime - timestamp) > 30){
            return null;
        }

        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(timestamp);

        TIMElem timElem_0 = message.getElement(0);
        if (timElem_0.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_0;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);

                String roomdbId = jsonObj.getString("roomdbId");
                int roomId = jsonObj.getInt("roomId");
                int type = jsonObj.getInt("type");
                int isHangup = jsonObj.getInt("isHangup");
                int cost = jsonObj.getInt("cost");

                messageDB.setRoomdbId(roomdbId);
                messageDB.setRoomId(roomId);
                messageDB.setMediaType(type);
                messageDB.setIsHangup(isHangup);
                messageDB.setCost(cost);
            }catch (IOException | JSONException e_){
                return null;
            }
        }

        TIMElem timElem_1 = message.getElement(1);
        if (timElem_1.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem_1;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userId = jsonObj.getString("userId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                if (isSend){
                    messageDB.setConversationId(userId + "@@" + otherPartyId);
                }else {
                    messageDB.setConversationId(otherPartyId + "@@" + userId);
                }
            }catch (IOException | JSONException e_){

            }
        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(10);// 0 文字  1 图片 2 语音  3 视频 10媒体(语音视频通话)
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);

        return messageDB;
    }

    private static  MessageDB text(TIMMessage message, boolean isSend){
        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

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
                    TIMCustomElem elem = (TIMCustomElem) elems.get(i);
                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        String userId = jsonObj.getString("userId");
                        String otherPartyId = jsonObj.getString("otherPartyId");

                        if (isSend){
                            messageDB.setConversationId(userId + "@@" + otherPartyId);
                        }else {
                            messageDB.setConversationId(otherPartyId + "@@" + userId);
                        }
                    }catch (IOException | JSONException e){

                    }
                    break;
            }
        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(0);// 0 文字  1 图片 2 语音  3 视频
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);

        messageDB.setText(stringBuilder.toString());
        return messageDB;
    }

    private static  MessageDB textCustom(TIMMessage message, boolean isSend){
        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        try{
            String str = new String(elem.getData(), "UTF-8");
            JSONObject jsonObj = new JSONObject(str).getJSONObject("data");
            String userId = jsonObj.getString("userId");
            String otherPartyId = jsonObj.getString("otherPartyId");

            if (isSend){
                messageDB.setConversationId(userId + "@@" + otherPartyId);
            }else {
                messageDB.setConversationId(otherPartyId + "@@" + userId);
            }

            messageDB.setText(jsonObj.getJSONObject("msgBody").getString("content"));
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(0);// 0 文字  1 图片 2 语音  3 视频
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);
        return messageDB;
    }

    private static MessageDB img(TIMMessage message, boolean isSend){
        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

        TIMImageElem e = (TIMImageElem) message.getElement(0);
        String path = null;
        String imageThumbnailURL = null;
        String imageOriginalURL = null;
        int imageW = 0;
        int imageH = 0;
        switch (message.status()){
            case Sending:
            case SendFail:
                path = e.getPath();
                break;
            case SendSucc:
                for(final TIMImage image : e.getImageList()) {
                    if (image.getType() == TIMImageType.Thumb){
                        imageThumbnailURL = image.getUrl();
                        imageW = (int) image.getWidth();
                        imageH = (int) image.getHeight();
                    }
                    if (image.getType() == TIMImageType.Original){
                        imageOriginalURL = image.getUrl();
                        imageW = (int) image.getWidth();
                        imageH = (int) image.getHeight();
                    }
                }
                break;
        }

        if (TextUtils.isEmpty(imageThumbnailURL)){
            messageDB.setImageThumbnailURL(path);
        }else{
            messageDB.setImageThumbnailURL(imageThumbnailURL);
        }
        if (TextUtils.isEmpty(imageOriginalURL)){
            messageDB.setImageOriginalURL(path);
        }else{
            messageDB.setImageOriginalURL(imageOriginalURL);
        }
        messageDB.setImageW(imageW);
        messageDB.setImageH(imageH);


        TIMElem timElem = message.getElement(1);
        if (timElem.getType() == Custom){
            TIMCustomElem elem = (TIMCustomElem) timElem;
            try{
                String str = new String(elem.getData(), "UTF-8");
                JSONObject jsonObj = new JSONObject(str);
                String userId = jsonObj.getString("userId");
                String otherPartyId = jsonObj.getString("otherPartyId");

                if (isSend){
                    messageDB.setConversationId(userId + "@@" + otherPartyId);
                }else {
                    messageDB.setConversationId(otherPartyId + "@@" + userId);
                }
            }catch (IOException | JSONException e_){

            }
        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(1);// 0 文字  1 图片 2 语音  3 视频
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);
        return messageDB;
    }

    private static MessageDB voice(TIMMessage message, boolean isSend){
        MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

        TIMSoundElem timSoundElem = null;
        TIMCustomElem timCustomElem = null;
        for (int i = 0; i < message.getElementCount(); i++) {
            TIMElem timElem = message.getElement(i);
            if (timElem.getType() == Sound){
                timSoundElem = (TIMSoundElem) timElem;
            }else if (timElem.getType() == Custom){
                timCustomElem = (TIMCustomElem) timElem;
            }
        }

        messageDB.setVoiceDuration(timSoundElem.getDuration());
        String path = timSoundElem.getPath();
        String absolutePath = FileUtil.createVoiceFile_(message.getMsgId());
        timSoundElem.getSoundToFile(absolutePath, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess() {
            }
        });
        if (!TextUtils.isEmpty(path)){
            messageDB.setVoicePath(path);
        }else{
            messageDB.setVoicePath(absolutePath);
        }

        try{
            String str = new String(timCustomElem.getData(), "UTF-8");
            JSONObject jsonObj = new JSONObject(str);
            String userId = jsonObj.getString("userId");
            String otherPartyId = jsonObj.getString("otherPartyId");

            if (isSend){
                messageDB.setConversationId(userId + "@@" + otherPartyId);
            }else {
                messageDB.setConversationId(otherPartyId + "@@" + userId);
            }
        }catch (IOException | JSONException e_){

        }

        messageDB.setMessageId(message.getMsgId());
        messageDB.setType(2);// 0 文字  1 图片 2 语音  3 视频
        // 是否为发送方 0接收方 1发送方
        if (message.isSelf()){
            messageDB.setIsSender(1);
        }else {
            messageDB.setIsSender(0);
        }
        messageDB.setIsSenderResult(1);
        messageDB.setIsShowTime(1);
        return messageDB;
    }

    private static MessageDB video(TIMMessage message, boolean isSend){
        final MessageDB messageDB = new MessageDB();
        messageDB.setTimestamp(message.timestamp());

        TIMVideoElem e = (TIMVideoElem) message.getElement(0);

        switch (message.status()){
            case Sending:
            case SendFail:
                String videoThumbnailPath_ = e.getSnapshotPath();
                TIMSnapshot snapshot_ = e.getSnapshotInfo();
//                String uuid_ = snapshot_.getUuid();
//                String type_ = snapshot_.getType();
                long videoThumbnailH_ = snapshot_.getHeight();
                long videoThumbnailW_ = snapshot_.getWidth();
                String videoPath_ = e.getVideoPath();
                TIMVideo video_ = e.getVideoInfo();
//                String videoType_ = video_.getType();
//                String videoUuid_ = video_.getUuid();
                long duaration_ = video_.getDuaration();

                messageDB.setVideoThumbnailPath(videoThumbnailPath_);
                messageDB.setVideoThumbnailW((int) videoThumbnailW_);
                messageDB.setVideoThumbnailH((int) videoThumbnailH_);
                messageDB.setVideoPath(videoPath_);
                messageDB.setVideoDuration((int) duaration_);

                //###############################################################
                TIMElem timElem = message.getElement(1);
                if (timElem.getType() == Custom){
                    TIMCustomElem elem = (TIMCustomElem) timElem;
                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        String userId = jsonObj.getString("userId");
                        String otherPartyId = jsonObj.getString("otherPartyId");

                        if (isSend){
                            messageDB.setConversationId(userId + "@@" + otherPartyId);
                        }else {
                            messageDB.setConversationId(otherPartyId + "@@" + userId);
                        }
                    }catch (IOException | JSONException e_){

                    }
                }

                messageDB.setMessageId(message.getMsgId());
                messageDB.setType(3);// 0 文字  1 图片 2 语音  3 视频
                // 是否为发送方 0接收方 1发送方
                if (message.isSelf()){
                    messageDB.setIsSender(1);
                }else {
                    messageDB.setIsSender(0);
                }
                messageDB.setIsSenderResult(1);
                messageDB.setIsShowTime(1);
                return messageDB;
                //###############################################################
            case SendSucc:
                TIMSnapshot snapshot = e.getSnapshotInfo();
//                String uuid = snapshot.getUuid();
//                String type = snapshot.getType();
                long videoThumbnailH = snapshot.getHeight();
                long videoThumbnailW = snapshot.getWidth();
                String videoThumbnailPath = FileUtil.createVoiceFile_img("image_" + message.getMsgId());
                //下载视频截图，并保存到 snapshotSavePath
                snapshot.getImage(videoThumbnailPath, new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
                    }
                    @Override
                    public void onSuccess() {
                    }
                });

                messageDB.setVideoThumbnailPath(videoThumbnailPath);
                messageDB.setVideoThumbnailW((int) videoThumbnailW);
                messageDB.setVideoThumbnailH((int) videoThumbnailH);

                //获取视频信息
                TIMVideo video = e.getVideoInfo();
//                String videoType = video.getType();
//                String videoUuid = video.getUuid();
                long duaration = video.getDuaration();
                String videoPath = FileUtil.createVoiceFile_video("video_" + message.getMsgId());

                messageDB.setVideoDuration((int) duaration);
                messageDB.setVideoPath(videoPath);

                //###############################################################
                TIMElem timElem_ = message.getElement(1);
                if (timElem_.getType() == Custom){
                    TIMCustomElem elem = (TIMCustomElem) timElem_;
                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        String userId = jsonObj.getString("userId");
                        String otherPartyId = jsonObj.getString("otherPartyId");

                        if (isSend){
                            messageDB.setConversationId(userId + "@@" + otherPartyId);
                        }else {
                            messageDB.setConversationId(otherPartyId + "@@" + userId);
                        }
                    }catch (IOException | JSONException e_){

                    }
                }

                messageDB.setMessageId(message.getMsgId());
                messageDB.setType(3);// 0 文字  1 图片 2 语音  3 视频
                // 是否为发送方 0接收方 1发送方
                if (message.isSelf()){
                    messageDB.setIsSender(1);
                }else {
                    messageDB.setIsSender(0);
                }
                messageDB.setIsSenderResult(1);
                messageDB.setIsShowTime(1);
                //###############################################################

                video.getVideo(videoPath, new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
//                        NLog.e("TAGTAG", desc);
                    }
                    @Override
                    public void onSuccess() {
                        RealmMessageUtils.addMessageMsg(messageDB, null);
                    }
                });
                break;
        }
        return null;
    }
}
