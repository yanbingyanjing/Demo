package com.yjfshop123.live.message.ui.models;

import com.yjfshop123.live.message.db.IMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;

import org.json.JSONException;
import org.json.JSONObject;


public class VideoMessage {

    public TIMMessage getMessage() {
        return message;
    }

    private TIMMessage message;

    public VideoMessage(long duration, String videoPath, String snapshotPath, long vHeight, long vWidth, IMConversation imConversationDB){
        message = new TIMMessage();

        //构建一个视频 Elem
        TIMVideoElem elem = new TIMVideoElem();
        TIMVideo video = new TIMVideo();
        video.setType("mp4");
        video.setDuaration(duration);
        elem.setVideo(video);
        elem.setVideoPath(videoPath);
        //添加视频截图
//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
//        elem.setSnapshotPath(FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
        elem.setSnapshotPath(snapshotPath);
        TIMSnapshot snapshot = new TIMSnapshot();
        snapshot.setType("jpg");
        snapshot.setHeight(vHeight);
        snapshot.setWidth(vWidth);
        elem.setSnapshot(snapshot);
        //将短视频 Elem 添加到消息中
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

        message.setOfflinePushSettings(TIMMessageOfflinePush_.pushSettings(imConversationDB.getUserName(), "[小视频]"));
    }

}
