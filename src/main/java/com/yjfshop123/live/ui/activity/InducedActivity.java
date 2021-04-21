package com.yjfshop123.live.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.utils.ClickUtil;
import com.yjfshop123.live.message.ChatPresenter;
import com.yjfshop123.live.message.ConversationFactory;
import com.yjfshop123.live.message.MessageFactory;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.LaunchChatResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.bumptech.glide.Glide;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;

import java.io.IOException;

public class InducedActivity extends BaseActivityH implements ChatViewIF, IPermissions {

    private ChatPresenter chatPresenter;
    private IMConversation imConversation;
    private int type_v_;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_induced);
        mContext = this;

        Intent data = getIntent();
        imConversation = (IMConversation) data.getSerializableExtra("IMConversation");

        // 0 单聊  1 群聊  2 系统消息
        if (imConversation.getType() == 0){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.C2C);
        }else if (imConversation.getType() == 1){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.Group);
        }else if (imConversation.getType() == 2){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.System);
        }

        CircleImageView induced_civ = findViewById(R.id.induced_civ);
        String avatar = imConversation.getOtherPartyAvatar();
        Glide.with(mContext)
                .load(avatar)
                .into(induced_civ);
        TextView induced_name = findViewById(R.id.induced_name);
        induced_name.setText(imConversation.getOtherPartyName());

        type = data.getIntExtra("type", 0);
        TextView induced_describe = findViewById(R.id.induced_describe);
        if (type == 1){
            induced_describe.setText("邀请你进行视频通话");
        }else {
            induced_describe.setText("邀请你进行语音通话");
        }

        findViewById(R.id.induced_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        });

        findViewById(R.id.induced_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()){
                    return;
                }

                PermissionsUtils.onResume(InducedActivity.this, InducedActivity.this);
            }
        });


        onOutgoingCallRinging();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }

        PermissionsUtils.initPermission(mContext);
    }

    @Override
    public void allPermissions() {
        if (type == 1){
            type_v_ = 3;
            launchChat();
        }else {
            type_v_ = 2;
            launchChat();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    private boolean canClick() {
        return ClickUtil.canClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////////
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        // 退出此页面后应设置成正常模式，否则按下音量键无法更改其他音频类型的音量
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setMode(AudioManager.MODE_NORMAL);
        }
        if(mMediaPlayer != null){
            mMediaPlayer = null;
        }
    }

    private void quit(){
        stopRing();
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepScreenLongLight(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock == null) {
            createPowerManager();
        }
        keepScreenLongLight(this, true);
    }

    @Override
    public void updateMessage(MessageDB messageDB) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        //消息发送失败 异常结束
        quit();
    }

    @Override
    public void onSuccess(TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10){
            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, true);
        }

        quit();
    }

    /////////////////////////////////////////////////////////////////////声音
    protected void startRing() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try {
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
//            RLog.e(TAG, "Ringtone not found : " + uri);
            try {
                uri = RingtoneManager.getValidRingtoneUri(this);
                mMediaPlayer.setDataSource(this, uri);
                mMediaPlayer.prepareAsync();
            } catch (IOException e1) {
                e1.printStackTrace();
//                RLog.e(TAG, "Ringtone not found: " + uri);
            }catch (IllegalStateException el) {
                el.printStackTrace();
//                Log.i(MEDIAPLAYERTAG,"startRing--IllegalStateException");
            }
        }
    }

    @SuppressLint("MissingPermission")
    protected void stopRing() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            }
            if (mVibrator != null) {
                mVibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //震动
    protected void startVibrator() {
        if (mVibrator == null) {
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            mVibrator.cancel();
        }
        mVibrator.vibrate(new long[]{500, 1000}, 0);
    }

    //声音
    private void onOutgoingCallRinging() {
        try {
            initMp();
            AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.call);
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            // 设置 MediaPlayer 播放的声音用途
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
//                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
//                mMediaPlayer.setVolume(1.0f, 1.0f);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setScreenOnWhilePlaying(true);
            final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.setSpeakerphoneOn(false);
                // 设置此值可在拨打时控制响铃音量
                am.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                am.setMode(AudioManager.STREAM_MUSIC);
                // 设置拨打时响铃音量默认值
                am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5, AudioManager.STREAM_VOICE_CALL);
//                am.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.STREAM_MUSIC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startVibrator();
    }
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    protected PowerManager powerManager;
    protected PowerManager.WakeLock wakeLock;

    private void initMp() {
        if(mMediaPlayer==null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        if (mp != null) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //不熄屏
    @SuppressLint("InvalidWakeLockTag")
    protected void createPowerManager() {
        if (powerManager == null) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "RoomActivity");
        }
    }

    private static void keepScreenLongLight(Activity activity, boolean isOpenLight) {
        try{
            Window window = activity.getWindow();
            if (isOpenLight) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }catch (Exception e){}
    }
    /////////////////////////////////////////////////////////////////////声音

    private void launchChat(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("accept_uid", imConversation.getOtherPartyIMId())
                    .put("type", type_v_)
                    .put("robot_id", imConversation.getOtherPartyId())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/launchChat", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                shoeHintDialog(errInfo, errCode);
            }
            @Override
            public void onSuccess(String result) {
                try {
                    LaunchChatResponse response = JsonMananger.jsonToBean(result, LaunchChatResponse.class);
                    response_(response);
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void response_(LaunchChatResponse response){
        //-1 无限聊天 >0剩余可聊时间
        //0 没钱了
        RoomActivity.order_no = null;
        RoomActivity.rest_time = 0;
        int rest_time = response.getRest_time();
//        if (rest_time == 0){
//            shoeHintDialog("金蛋不足", 201);
//        }else {
            RoomActivity.order_no = response.getOrder_no();
            RoomActivity.rest_time = rest_time;
            MediaMessage imageMessage;
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        1,0, response.getSpeech_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        2,0, response.getVideo_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }
      //  }

            /*RoomActivity.order_no = response.getData().getOrder_no();
            MediaMessage imageMessage;
            int home_id = response.getData().getHome_id();
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        1,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        2,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }*/
    }

    private void shoeHintDialog(String msg, int code){
        if (code == 201){
            PromptPopupDialog.newInstance(this,
                    "",
                    msg,
                    getString(R.string.recharge))
                    .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            Intent intent = new Intent(mContext, MyWalletActivity1.class);
                            intent.putExtra("currentItem",0);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    }).setLayoutRes(R.layout.recharge_popup_dialog).show();
        }else{
            DialogUitl.showSimpleHintDialog(this, getString(R.string.prompt), msg,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        }
    }

}
