package com.yjfshop123.live.message.ui.models;


import com.tencent.imsdk.TIMMessageOfflinePushSettings;

public class TIMMessageOfflinePush_ {

    public static TIMMessageOfflinePushSettings pushSettings(String title, String content){
        return  pushSettings(title, content, false);
    }

    public static TIMMessageOfflinePushSettings pushSettings(String title, String content, boolean isSound){
        //设置当前消息的离线推送配置
        TIMMessageOfflinePushSettings settings = new TIMMessageOfflinePushSettings();
        settings.setEnabled(true);
        settings.setDescr(content);
        //设置离线推送扩展信息
        /*JSONObject object = new JSONObject();
        try {
            object.put("level", 15);
            object.put("task", "TASK15");
            settings.setExt(object.toString().getBytes("utf-8"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        //设置在 Android 设备上收到消息时的离线配置
        TIMMessageOfflinePushSettings.AndroidSettings androidSettings = new TIMMessageOfflinePushSettings.AndroidSettings();
        androidSettings.setTitle("收到新消息");
        //推送自定义通知栏消息，接收方收到消息后单击通知栏消息会给应用回调（针对小米、华为离线推送）
        androidSettings.setNotifyMode(TIMMessageOfflinePushSettings.NotifyMode.Normal);//Custom
        //设置 Android 设备收到消息时的提示音，声音文件需要放置到 raw 文件夹
//        androidSettings.setSound(Uri.parse("android.resource://" + "com.yjfshop123.live" + "/" +R.raw.call));
        settings.setAndroidSettings(androidSettings);


        //设置在 iOS 设备上收到消息时的离线配置
        TIMMessageOfflinePushSettings.IOSSettings iosSettings = new TIMMessageOfflinePushSettings.IOSSettings();
        //ImSDK 2.5.3 之前的构造方式
        //TIMMessageOfflinePushSettings.IOSSettings iosSettings = settings.new IOSSettings();
        //开启 Badge 计数
        iosSettings.setBadgeEnabled(true);
        //设置 iOS 收到消息时没有提示音且不振动（ImSDK 2.5.3 新增特性）
        //iosSettings.setSound(TIMMessageOfflinePushSettings.IOSSettings.NO_SOUND_NO_VIBRATION);
        //设置 iOS 设备收到离线消息时的提示音
        if (isSound){
            iosSettings.setSound("call.caf");
        }
        settings.setIosSettings(iosSettings);
        return settings;
    }

}
