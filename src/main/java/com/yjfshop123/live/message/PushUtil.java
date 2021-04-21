package com.yjfshop123.live.message;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.IMConversationDB;

import java.util.Observable;
import java.util.Observer;

public class PushUtil implements Observer {

    //    private static int pushNum = 0;
    private final int pushId = 0X123;

    private static PushUtil instance = new PushUtil();

    private PushUtil() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }

    @SuppressLint("WrongConstant")
    private void PushNotify(IMConversationDB imConversationDB) {

        if (Foreground.get().isForeground()) {
            return;
        }

        if (imConversationDB == null) {
            return;
        }

        String senderStr, contentStr;
        senderStr = imConversationDB.getOtherPartyName();
        contentStr = imConversationDB.getLastMessage();

        NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(App.getContext().NOTIFICATION_SERVICE);

        Intent intent = ActivityUtils.getHome(App.getContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibrate = {0, 500, 1000, 1500};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            Notification.Builder notification = new Notification
                    .Builder(App.getContext(), "消息")
                    .setContentTitle(senderStr)
                    .setContentText(contentStr)
                    .setTicker(senderStr + ":" + contentStr) //收到通知提示语
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.splash_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.splash_logo))
                    .setContentIntent(pendingIntent)
                    .setChannelId(App.getContext().getPackageName());

            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(
                    App.getContext().getPackageName(),
                    "消息",
                    NotificationManager.IMPORTANCE_MAX
            );
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrate);
            channel.setImportance(NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
            Notification notify = notification.build();
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(pushId, notification.build());
        } else {
            @SuppressLint("WrongConstant")
            Notification.Builder notification = new Notification
                    .Builder(App.getContext())
                    .setContentTitle(senderStr)
                    .setContentText(contentStr)
                    .setTicker(senderStr + ":" + contentStr) //收到通知提示语
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.splash_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.splash_logo))
                    .setVibrate(vibrate)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
            Notification notify = notification.build();
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(pushId, notification.build());

        }
    }

    public static void resetPushNum() {
//        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(App.getContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof IMConversationDB) {
                IMConversationDB imConversationDB = (IMConversationDB) data;
                PushNotify(imConversationDB);
            }
        }
    }
}

