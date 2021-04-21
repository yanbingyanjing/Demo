package com.yjfshop123.live.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.im.IMMessageMgr;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.view.LiveRoomViewHolder2;
import com.yjfshop123.live.message.ChatPresenter;
import com.yjfshop123.live.message.ConversationFactory;
import com.yjfshop123.live.message.MessageFactory;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MediaModel;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.fragment.RoomGiftDialogFragment;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.opensource.svgaplayer.SVGAImageView;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

import static com.tencent.trtc.TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;
import static com.tencent.trtc.TRTCCloudDef.TRTC_BEAUTY_STYLE_SMOOTH;
import static com.tencent.trtc.TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_SMOOTH;

public class RoomActivity extends BaseActivityH implements ChatViewIF, IPermissions,
        IMMessageMgr.IMMessageListener{
    private ImageView room_receive;
    private TextView room_hint_1;
    private TextView room_hint_2;
    private TextView timeView;
    private FrameLayout room_fl;
    private ImageView roomYsq;

    private ChatPresenter chatPresenter;
    private String miTencentId;
    private String avatar;

    private boolean isCamera;//false语音 true视频
    private boolean isSend;//false接听 true发送
    public static int roomId = 0;
    private int cost = 0;

    private View switchcameraView;
    private ArrayList<RelativeLayout.LayoutParams> mFloatParamList;
    private RelativeLayout relativeLayout_tx;
    private TXCloudVideoView txCloudVideoView1;
    private TXCloudVideoView txCloudVideoView2;
    private TRTCCloud trtcCloud;

    /**
     * 视频 语音消息 ID
     */
    public static String roomdbId = null;
    public static boolean send = false;

//    roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
//    roomId 云通信的房间号
//    type 1 语音  2 视频
//    isHangup 默认 0 接收通话  1 挂断通话
    private IMConversation imConversation;
    private MediaModel mediaModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        isHangUpBefore = false;//TODO

        KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        if (showingLocked){
            /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );*/
            int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            getWindow().addFlags(flags);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
            getWindow().setAttributes(params);

            if (MzSystemUtils.isBrandMeizu(mContext)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_room);

        Intent data = getIntent();
        imConversation = (IMConversation) data.getSerializableExtra("IMConversation");
        mediaModel = (MediaModel) data.getSerializableExtra("mediaModel");
        isSend = data.getBooleanExtra("isSender", false);

        roomdbId = mediaModel.getRoomdbId();
        miTencentId = imConversation.getUserIMId();
        avatar = imConversation.getOtherPartyAvatar();
        roomId = mediaModel.getRoomId();
        cost = mediaModel.getCost();
        if (mediaModel.getMediaType() == 1){
            isCamera = false;
        }else {
            isCamera = true;
        }

        // 0 单聊  1 群聊  2 系统消息
        if (imConversation.getType() == 0){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.C2C);
        }else if (imConversation.getType() == 1){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.Group);
        }else if (imConversation.getType() == 2){
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.System);
        }

        leftMargin_26 = CommonUtils.dip2px(mContext, 26);
        topMargin_26 = CommonUtils.dip2px(mContext, 26);
        subWidth_120 = CommonUtils.dip2px(mContext, 120);
        subHeight_190 = CommonUtils.dip2px(mContext, 190);
        mScreenWidth = CommonUtils.getScreenWidth(this);
        mScreenHeight = CommonUtils.getScreenHeight(this);
        initFloatLayoutParams();
        relativeLayout_tx = findViewById(R.id.room_rl_txcl);
        txCloudVideoView1 = findViewById(R.id.txcloud_videoview1);
        txCloudVideoView2 = findViewById(R.id.txcloud_videoview2);
        trtcCloud = TRTCCloud.sharedInstance(this);
        trtcCloud.setListener(new TRTCCloudListenerImpl());

        //设置美颜、美白、红润效果级别。
        //TRTC_BEAUTY_STYLE_SMOOTH //平滑
        //TRTC_BEAUTY_STYLE_NATURE //自然
        int beautyProgress = TCUtils.filtNumber(9, 100, UserInfoUtil.getBeault());
        int whiteProgress = TCUtils.filtNumber(9, 100, UserInfoUtil.getWhite());
        int ruddyProgress = TCUtils.filtNumber(9, 100, UserInfoUtil.getRuddy());
//        trtcCloud.setBeautyStyle(TRTC_BEAUTY_STYLE_SMOOTH, beautyProgress, whiteProgress, ruddyProgress);
//        trtcCloud.setFilter(TCUtils.getFilterBitmap(getResources(), UserInfoUtil.getFilterId()));
        TXBeautyManager txBeautyManager = trtcCloud.getBeautyManager();
        txBeautyManager.setBeautyStyle(TRTC_BEAUTY_STYLE_SMOOTH);
        txBeautyManager.setBeautyLevel(beautyProgress);
        txBeautyManager.setWhitenessLevel(whiteProgress);
        txBeautyManager.setRuddyLevel(ruddyProgress);
        txBeautyManager.setFilter(TCUtils.getFilterBitmap(getResources(), UserInfoUtil.getFilterId()));

        send = isSend;
        if (isSend){
            enterRoom();
        }else {
            /*if (isCamera){
                //frontCamera（true：前置摄像头 false：后置摄像头
                trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                trtcCloud.startLocalPreview(true, txCloudVideoView1);
            }*/
        }
        if (isCamera){
            relativeLayout_tx.setBackgroundColor(Color.parseColor("#ff000000"));
        }

        room_fl = findViewById(R.id.room_fl);
        CircleImageView circleImageView = findViewById(R.id.room_civ);
        Glide.with(mContext)
                .load(CommonUtils.getUrl(avatar))
                .into(circleImageView);
        TextView room_name = findViewById(R.id.room_name);
        room_name.setText(imConversation.getOtherPartyName());

        room_hint_1 = findViewById(R.id.room_hint_1);
        room_hint_2 = findViewById(R.id.room_hint_2);
        timeView = findViewById(R.id.room_tv_setupTime);
        ImageView room_close = findViewById(R.id.room_close);
        room_receive = findViewById(R.id.room_receive);
        roomYsq = findViewById(R.id.room_ysq);

        if (isSend){
            room_hint_1.setText("对方还在等待中");
            if (cost == 0){
                room_hint_2.setVisibility(View.GONE);
            }else {
                room_hint_2.setText("消耗" + cost + getString(R.string.ql_cost));
            }
            room_receive.setVisibility(View.GONE);
        }else{
            if (isCamera){
                room_hint_1.setText("视频通话");
            }else{
                room_hint_1.setText("语音通话");
            }
            if (cost == 0){
                room_hint_2.setVisibility(View.GONE);
            }else {
                room_hint_2.setText("接听将收入：" + cost + getString(R.string.ql_cost));
            }
            room_receive.setVisibility(View.VISIBLE);
        }

        room_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_close();
            }
        });

        room_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgress();
                enterRoom();
            }
        });

        BroadcastManager.getInstance(mContext).addAction("HANG_UP", new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //挂断
                exitRoom();
            }
        });
        handler = new Handler();
        time = 0;

        onOutgoingCallRinging();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }

        BroadcastManager.getInstance(mContext).addAction("close_notice", new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra("type", 0);
                if (type == 12){
                    //弹框提醒
                    try{
                        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), intent.getStringExtra("content"),
                                true, new DialogUitl.SimpleCallback() {
                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        dialog.dismiss();
                                    }
                                });
                    }catch (Exception e){
                        NToast.shortToast(mContext, intent.getStringExtra("content"));
                    }
                    return;
                }

                if (type == 13){
                    //关闭视频
                    if (isCamera){
                        MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,2,1, cost, imConversation);
                        chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
                    }else {
                        MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,1,1, cost, imConversation);
                        chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
                    }
                    return;
                }

            }
        });

        txCloudVideoView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isB){
                    conversion();
                }
            }
        });

        txCloudVideoView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isB){
                    conversion();
                }
            }
        });

        switchcameraView = findViewById(R.id.room_switchcamera);
        switchcameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trtcCloud.switchCamera();
            }
        });

        txCloudVideoView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isB){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mTouchResult1 = false;
                            mStartX1 = mLastX1 = (int) event.getRawX();
                            mStartY1 = mLastY1 = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int left, top, right, bottom;
                            int dx = (int) event.getRawX() - mLastX1;
                            int dy = (int) event.getRawY() - mLastY1;
                            left = v.getLeft() + dx;
                            if (left < 0) {
                                left = 0;
                            }
                            right = left + v.getWidth();
                            if (right > mScreenWidth) {
                                right = mScreenWidth;
                                left = right - v.getWidth();
                            }
                            top = v.getTop() + dy;
                            if (top < 0) {
                                top = 0;
                            }
                            bottom = top + v.getHeight();
                            if (bottom > mScreenHeight) {
                                bottom = mScreenHeight;
                                top = bottom - v.getHeight();
                            }
                            v.layout(left, top, right, bottom);
                            mLastX1 = (int) event.getRawX();
                            mLastY1 = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                            v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                            relativeLayout_tx.bringChildToFront(v);
                            float endX = event.getRawX();
                            float endY = event.getRawY();
                            if (Math.abs(endX - mStartX1) > 5 || Math.abs(endY - mStartY1) > 5) {
                                //防止点击的时候稍微有点移动点击事件被拦截了
                                mTouchResult1 = true;
                            }
                            break;
                    }
                    return mTouchResult1;
                }else {
                    return false;
                }
            }
        });

        txCloudVideoView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isB){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mTouchResult2 = false;
                            mStartX2 = mLastX2 = (int) event.getRawX();
                            mStartY2 = mLastY2 = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int left, top, right, bottom;
                            int dx = (int) event.getRawX() - mLastX2;
                            int dy = (int) event.getRawY() - mLastY2;
                            left = v.getLeft() + dx;
                            if (left < 0) {
                                left = 0;
                            }
                            right = left + v.getWidth();
                            if (right > mScreenWidth) {
                                right = mScreenWidth;
                                left = right - v.getWidth();
                            }
                            top = v.getTop() + dy;
                            if (top < 0) {
                                top = 0;
                            }
                            bottom = top + v.getHeight();
                            if (bottom > mScreenHeight) {
                                bottom = mScreenHeight;
                                top = bottom - v.getHeight();
                            }
                            v.layout(left, top, right, bottom);
                            mLastX2 = (int) event.getRawX();
                            mLastY2 = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                            v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                            relativeLayout_tx.bringChildToFront(v);
                            float endX = event.getRawX();
                            float endY = event.getRawY();
                            if (Math.abs(endX - mStartX2) > 5 || Math.abs(endY - mStartY2) > 5) {
                                //防止点击的时候稍微有点移动点击事件被拦截了
                                mTouchResult2 = true;
                            }
                            break;
                    }
                    return mTouchResult2;
                }else {
                    return false;
                }
            }
        });

        roomYsq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver();
            }
        });

        PermissionsUtils.initPermission(mContext);
        PermissionsUtils.onResume(RoomActivity.this, RoomActivity.this);

        initMgr();

        if (isSend){
            isJT = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isJT){
                        room_close();
                    }
                }
            }, 60000);
        }
    }

    private boolean isJT = false;

    private void room_close(){
        LoadDialog.show(mContext);
        if (isCamera){
            MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,2,1, cost, imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        }else {
            MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,1,1, cost, imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        }
    }

    @Override
    public void allPermissions() {
        if (isCamera && !isSend){
            //frontCamera（true：前置摄像头 false：后置摄像头
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            trtcCloud.startLocalPreview(true, txCloudVideoView1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    //手指按下位置
    private int mStartX1, mStartY1, mLastX1, mLastY1;
    private int mStartX2, mStartY2, mLastX2, mLastY2;
    private int mScreenWidth, mScreenHeight;
    private boolean mTouchResult1 = false;
    private boolean mTouchResult2 = false;

    private int leftMargin_26;
    private int topMargin_26;
    private int subWidth_120;
    private int subHeight_190;

    private void initFloatLayoutParams() {
        mFloatParamList = new ArrayList<>();
        RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mFloatParamList.add(layoutParams0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(subWidth_120, subHeight_190);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.leftMargin = leftMargin_26;
        layoutParams.topMargin = topMargin_26;

        mFloatParamList.add(layoutParams);
    }

    private RelativeLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(subWidth_120, subHeight_190);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    private void videoView(){
//        cancelProgress();

        if (isGift){
            btnRoomGift.setVisibility(View.VISIBLE);
        }

        room_receive.setVisibility(View.GONE);
        room_hint_2.setVisibility(View.GONE);
        room_hint_1.setVisibility(View.GONE);
        room_fl.setVisibility(View.GONE);

        switchcameraView.setVisibility(View.VISIBLE);

        //显示通话时间
        if (time == 0){
            setupTime();
        }

        stopRing();
    }

    private void voiceView(){
//        cancelProgress();

        if (isGift){
            btnRoomGift.setVisibility(View.VISIBLE);
        }

        room_receive.setVisibility(View.GONE);
        room_hint_2.setVisibility(View.GONE);
        room_hint_1.setVisibility(View.GONE);

        //显示通话时间
        if (time == 0){
            setupTime();
        }

        stopRing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            handler.removeCallbacks(updateTimeRunnable);
        }catch (Exception e){
            e.printStackTrace();
        }

        //销毁 trtc 实例
        if (trtcCloud != null) {
            trtcCloud.setListener(null);
        }
        trtcCloud = null;
        TRTCCloud.destroySharedInstance();

        roomdbId = null;
        send = false;
        time = 0;

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

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }

        if (mIMMessageMgr != null){
            mIMMessageMgr.unInitialize();
        }
    }

    private void exitRoom(){
        if (trtcCloud != null) {
            trtcCloud.exitRoom();
        }

        //发消息 通知对方
        if (isSend){
            if (isCamera){
                update_video_voice("VIDEO", -1000);
            }else{
                update_video_voice("VOICE", -1000);
            }
        }
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
        LoadDialog.dismiss(mContext);
        exitRoom();
    }

    @Override
    public void onSuccess(TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10){
            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, true);
        }

        LoadDialog.dismiss(mContext);
        exitRoom();
    }


    //////////////////////////////////////////////
    private Runnable updateTimeRunnable;
    public static long time = 0;
    private final static long DELAY_TIME = 1000;
    protected Handler handler;

    public void setupTime() {
        try {
            if (updateTimeRunnable != null) {
                handler.removeCallbacks(updateTimeRunnable);
            }
            timeView.setVisibility(View.VISIBLE);
            updateTimeRunnable = new UpdateTimeRunnable(timeView);
            handler.post(updateTimeRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UpdateTimeRunnable implements Runnable {

        private TextView timeView;

        public UpdateTimeRunnable(TextView timeView) {
            this.timeView = timeView;
        }

        @Override
        public void run() {
            time++;

            //TODO
            if (isSend){
                if (time % 60 == 2){//每分钟五秒更新
                    if (isCamera){
                        update_video_voice("VIDEO", (time / 60 + 1));
                    }else{
                        update_video_voice("VOICE", (time / 60 + 1));
                    }
                }
                if (isHangUpBefore && (time % 60 == 0)){
                    hangUp();
                }
            }

            if (time >= 3600) {
                timeView.setText(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
            } else {
                timeView.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
            }
            handler.postDelayed(this, DELAY_TIME);
        }
    }

    private void hangUp(){
        if (isCamera){
            MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,2,1, cost, imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        }else {
            MediaMessage imageMessage = new MediaMessage(roomdbId, roomId,1,1, cost, imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        }
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
//                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
//                mMediaPlayer.setVolume(1.0f, 1.0f);
            } else {
//                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setScreenOnWhilePlaying(true);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            changeToSpeaker();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startVibrator();
    }
    private MediaPlayer mMediaPlayer;
    private AudioManager audioManager;
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

    /**
     * 切换到外放
     */
    private void changeToSpeaker(){
        if (audioManager != null){
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
        }
    }

    /**
     * 切换到听筒
     */
    private void changeToReceiver(){
        if (audioManager != null){
            audioManager.setSpeakerphoneOn(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
            //audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5, AudioManager.STREAM_VOICE_CALL);
        }
    }
    /////////////////////////////////////////////////////////////////////声音

    private class TRTCCloudListenerImpl extends TRTCCloudListener {

        public TRTCCloudListenerImpl() {
            super();
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            NLog.e("TAGTAG", "sdk callback onError");
            if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                exitRoom();
            }
        }

        @Override
        public void onEnterRoom(long elapsed) {
            NLog.e("TAGTAG", "加入房间成功");
        }

        @Override
        public void onExitRoom(int reason) {
            NLog.e("TAGTAG", "退出房间");
        }

        //有人加入房间时
        @Override
//        public void onUserEnter(String userId) {
        public void onRemoteUserEnterRoom(String userId) {
            //TRTC_VIDEO_RENDER_MODE_FIT 模式：优先保证视频内容全部显示。如果缩放后的视频尺寸与显示视窗尺寸不一致，未被填满的视窗区域将使用黑色填充。
            //TRTC_VIDEO_RENDER_MODE_FILL 模式：优先保证视窗被填满。如果缩放后的视频尺寸与显示视窗尺寸不一致，多出的视频将被截掉。
            NLog.e("TAGTAG", userId);

            if (isCamera){
                userid_other = userId;
                trtcCloud.setRemoteViewFillMode(userid_other, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                trtcCloud.startRemoteView(userid_other, txCloudVideoView2);

                conversion();
                videoView();
            }else {
                voiceView();
            }

            isJT = true;
        }

        //有人离开房间时
        @Override
        public void onUserExit(String userId, int reason) {
            if (trtcCloud != null && userId != null){
                trtcCloud.stopRemoteView(userId);
            }
        }

        @Override
        public void onDisConnectOtherRoom(int err, String errMsg) {
            DialogUitl.showSimpleHintDialog(RoomActivity.this, getString(R.string.prompt), "连接断开",
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            exitRoom();
                        }
                    });
        }

        @Override
        public void onNetworkQuality(TRTCCloudDef.TRTCQuality localQuality, ArrayList<TRTCCloudDef.TRTCQuality> remoteQuality) {
            for (TRTCCloudDef.TRTCQuality qualityInfo: remoteQuality) {
                updateNetworkQuality(qualityInfo.userId, qualityInfo.quality);
            }
        }
    }

    private void updateNetworkQuality(String userID, int quality) {
        if (TextUtils.isEmpty(userid_other)){
            return;
        }
        if (userid_other.equals(userID)){
            if (quality < TRTCCloudDef.TRTC_QUALITY_Excellent) {
                quality = TRTCCloudDef.TRTC_QUALITY_Excellent;
            }
            if (quality > TRTCCloudDef.TRTC_QUALITY_Down) {
                quality = TRTCCloudDef.TRTC_QUALITY_Down;
            }

            if (quality == TRTCCloudDef.TRTC_QUALITY_Down
                    ||quality == TRTCCloudDef.TRTC_QUALITY_Vbad
                    ||quality == TRTCCloudDef.TRTC_QUALITY_Bad) {
                NToast.shortToast(mContext, "网络质量弱");
            }
        }
    }

    private boolean isB = true;
    private String userid_other = null;

    private void conversion(){
        if (isCamera && !TextUtils.isEmpty(userid_other)){
            if (isB){
                isB = false;

                RelativeLayout.LayoutParams layoutParams0 = mFloatParamList.get(0);
                RelativeLayout.LayoutParams layoutParams1 = mFloatParamList.get(1);
                txCloudVideoView1.setLayoutParams(layoutParams1);
                txCloudVideoView2.setLayoutParams(layoutParams0);
                relativeLayout_tx.bringChildToFront(txCloudVideoView1);
            }else {
                isB = true;

                RelativeLayout.LayoutParams layoutParams0 = mFloatParamList.get(0);
                RelativeLayout.LayoutParams layoutParams1 = mFloatParamList.get(1);
                txCloudVideoView1.setLayoutParams(layoutParams0);
                txCloudVideoView2.setLayoutParams(layoutParams1);
                relativeLayout_tx.bringChildToFront(txCloudVideoView2);
            }
        }
    }

    private void enterRoom() {
        trtcCloud.startLocalAudio();//打开麦克风

        //TRTCParams 定义参考头文件TRTCCloudDef.java
        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = Const.im_app_id;
        trtcParams.userId = miTencentId;
        trtcParams.userSig = UserInfoUtil.getSignature();
        trtcParams.roomId = roomId; //输入你想进入的房间

        trtcCloud.setBGMVolume(100);
        trtcCloud.setMicVolumeOnMixing(100);

        TRTCCloudDef.TRTCNetworkQosParam trtcNetworkQosParam = new TRTCCloudDef.TRTCNetworkQosParam();
        trtcNetworkQosParam.preference = TRTC_VIDEO_QOS_PREFERENCE_SMOOTH;//流畅优先
//        trtcNetworkQosParam.preference = TRTC_VIDEO_QOS_PREFERENCE_CLEAR;//清晰优先
        trtcCloud.setNetworkQosParam(trtcNetworkQosParam);

        TRTCCloudDef.TRTCVideoEncParam smallParam = new TRTCCloudDef.TRTCVideoEncParam();
        smallParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
        smallParam.videoFps = 15;
        smallParam.videoBitrate = 600;
        smallParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.enableEncSmallVideoStream(false, smallParam);

        //TRTC_APP_SCENE_VIDEOCALL 对应视频通话场景
        //TRTC_APP_SCENE_LIVE 内部编码器和网络协议优化侧重性能和兼容性
//        trtcCloud.enterRoom(trtcParams, TRTC_APP_SCENE_LIVE);
        trtcCloud.enterRoom(trtcParams, TRTC_APP_SCENE_VIDEOCALL);

        if (isCamera && isSend){
            //frontCamera（true：前置摄像头 false：后置摄像头
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            trtcCloud.startLocalPreview(true, txCloudVideoView1);
        }
    }

    private boolean isReceiver = true;
    private void receiver(){
        if (isReceiver){
            //听筒
            isReceiver = false;
            changeToReceiver();
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
            roomYsq.setImageResource(R.drawable.videochat_icon_hands_free_act);
            NToast.shortToast(mContext, "已切换到听筒");
        }else {
            //扬声器
            isReceiver = true;
            changeToSpeaker();
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
            roomYsq.setImageResource(R.drawable.videochat_icon_hands_free);
            NToast.shortToast(mContext, "已切换到扬声器");
        }
    }


    //######################################################################################
    private int mtime = 0;
    public static String order_no = null;
    public static int rest_time = 0;//TODO
    private void update_video_voice(String type, long time){
        if (TextUtils.isEmpty(type)){
            return;
        }

        if (order_no == null){
            return;
        }

//        if (rest_time == -1){
//            //无限畅聊 -1
//            return;
//        }
//        if (rest_time == 0){
//            //结束通话
//        }

        if (type.equals("VOICE")){
            if (time == -1000){
                NLog.e("TAGTAG", "结束语音通话");
                closeChat();
            }else {//TODO
                NLog.e("TAGTAG", "语音通话>>" + time);
                mtime = (int)time;
                updChatDurationPerMin();
            }
        }else {
            if (time == -1000){
                NLog.e("TAGTAG", "结束视频通话");
                closeChat();
            }else {//TODO
                NLog.e("TAGTAG", "视频通话>>" + time);
                mtime = (int)time;
                updChatDurationPerMin();
            }
        }

        if (rest_time > 0 && rest_time <3){//TODO
            NToast.shortToast(mContext, "您好！剩余通话时长不足三分钟！");
        }
    }

    private void closeChat(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("order_no", order_no)
                    .put("duration_time", mtime)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/closeChat", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    private boolean isHangUpBefore = false;

    private void updChatDurationPerMin(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("order_no", order_no)
                    .put("duration_time", mtime)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/updChatDurationPerMin", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //错误结束挂断
                hangUp();
            }
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jso = new JSONObject(result);
                    rest_time = jso.getInt("rest_time");
                    if (rest_time == 0){
                        //挂断电话
                        isHangUpBefore = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //######################################################################################


    //######################################################################################gift
    private boolean isGift = true;//是否打开发送礼物
    private IMMessageMgr mIMMessageMgr;
    private LiveRoomViewHolder2 mLiveRoomViewHolder;
    private ImageView btnRoomGift;
    private void initMgr(){
        if (!isGift){
            return;
        }
        btnRoomGift = findViewById(R.id.btn_room_gift);
        btnRoomGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiftWindow();
            }
        });

        if (mIMMessageMgr == null) {
            mIMMessageMgr = new IMMessageMgr(mContext);
            mIMMessageMgr.setIMMessageListener(this);
        }

        mLiveRoomViewHolder = new LiveRoomViewHolder2(mContext, (ViewGroup)findViewById(R.id.container), (GifImageView)findViewById(R.id.gift_gif), (SVGAImageView)findViewById(R.id.gift_svga));
        mLiveRoomViewHolder.addToParent();
    }

    public void getWallet(){
        Intent intent = new Intent(mContext, MyWalletActivity1.class);
        intent.putExtra("currentItem", 0);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    //发送礼物
    public void sendGift(LiveReceiveGiftBean bean) {
        mIMMessageMgr.sendC2CCustomMessage(roomdbId, roomId, bean, imConversation, new IMMessageMgr.Callback() {
            @Override
            public void onError(int code, String errInfo) {

            }

            @Override
            public void onSuccess(Object... args) {

            }
        });
    }

    private void openGiftWindow() {
        RoomGiftDialogFragment fragment = new RoomGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", imConversation.getOtherPartyIMId());
        bundle.putString("isGuard", "0");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "RoomGiftDialogFragment");
    }

    @Override
    public void onRoomReceiveGift(int roomId, LiveReceiveGiftBean bean) {
        if (this.roomId == roomId){
            mLiveRoomViewHolder.showGiftMessage(bean);
        }
    }
}
