package com.yjfshop123.live.live.live.push.screen;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.tencent.rtmp.TXLog;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TCScreenRecordService extends Service {

    private static final String TAG = TCScreenRecordService.class.getSimpleName();

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    public TCScreenRecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private NotificationManager notificationManager;
    private String notificationId = "channelId";
    private String notificationName = "channelName";
    private final int pushId = 0X234;

    @Override
    public void onCreate() {
        super.onCreate();


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(pushId, getNotification());

        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));
    }

    private Notification getNotification() {

        Intent intent = new Intent(this, TCScreenRecordActivity.class);
        intent.putExtra("pureAudio", false);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("正在进行录制")
                .setContentText("视频录制中……");
        //设置Notification的ChannelID,否则不能正常显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
        reset();
    }

    public void reset(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    public class ExitBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                TXLog.d(TAG, "service broadcastReceiver receive exit app msg");
                //唤醒activity提示推流结束
                Intent restartIntent = new Intent(getApplicationContext(), TCScreenRecordActivity.class);
                restartIntent.putExtra("pureAudio", false);
                restartIntent.setAction(TCConstants.EXIT_APP);
//                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(restartIntent);
            }
        }
    }
}
