package com.yjfshop123.live.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.faceunity.FURenderer;
import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;


import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.yjfshop123.live.live.commondef.MLVBCommonDef.LiveRoomErrorCode;

public class MLVBLiveRoomImpl extends MLVBLiveRoom {

    protected static final String TAG = "TAGTAG";

    protected static final int              LIVEROOM_ROLE_NONE      = 0;//默认
    protected static final int              LIVEROOM_ROLE_PUSHER    = 1;//push
    protected static final int              LIVEROOM_ROLE_PLAYER    = 2;//play

    protected static MLVBLiveRoomImpl mInstance = null;

    protected Context                       mAppContext;
    protected IMLVBLiveRoomListener         mListener = null;
    protected int                           mSelfRoleType           = LIVEROOM_ROLE_NONE;

    protected boolean                       mJoinPusher             = false;

    protected TXLivePlayer mTXLivePlayer;

    protected TXLivePlayConfig mTXLivePlayConfig;
    protected Handler                       mListenerHandler;
    protected Handler                       mHandler;
    protected String                        mLiveId;//直播间ID
    protected String                        mMixedPlayURL;
    protected TXLivePusher mTXLivePusher;
    protected TXLivePushListenerImpl        mTXLivePushListener;
    protected HashMap<String, PlayerItem>   mPlayers = new LinkedHashMap<>();

    private static final int                LIVEROOM_CAMERA_PREVIEW = 0;
    private static final int                LIVEROOM_SCREEN_PREVIEW = 1;
    private int                             mPreviewType = LIVEROOM_CAMERA_PREVIEW;

    protected boolean                       mScreenAutoEnable       = true;

    private int vod_type = 1; //1:直播 2:录播

    private FURenderer mFURenderer;
    private boolean mOnFirstCreate = true;
    private boolean mFrontCamera = true;

    private boolean isCreateRoom = false;
    private boolean isJoinAnchor = false;

    public static MLVBLiveRoom sharedInstance(Context context) {
        synchronized (MLVBLiveRoomImpl.class) {
            if (mInstance == null) {
                mInstance = new MLVBLiveRoomImpl(context);
            }
            return mInstance;
        }
    }

    protected MLVBLiveRoomImpl(Context context) {
        mAppContext = context.getApplicationContext();
        mHandler = new Handler(mAppContext.getMainLooper());
        mListenerHandler = new Handler(mAppContext.getMainLooper());

        mTXLivePlayConfig = new TXLivePlayConfig();
        mTXLivePlayer = new TXLivePlayer(context);
        mTXLivePlayConfig.setAutoAdjustCacheTime(true);
        mTXLivePlayConfig.setMaxAutoAdjustCacheTime(2.0f);
        mTXLivePlayConfig.setMinAutoAdjustCacheTime(2.0f);
        mTXLivePlayConfig.enableAEC(true);//回声消除
        mTXLivePlayer.setConfig(mTXLivePlayConfig);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mTXLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(final int event, final Bundle param) {
                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

                    //拉流失败
                    if (mMixedPlayURL != null && mMixedPlayURL.length() > 0) {
                        int playType = getPlayType(mMixedPlayURL);
                        mTXLivePlayer.startPlay(mMixedPlayURL, playType);
                    }
                    String msg = "[LivePlayer] 拉流失败[" + param.getString(TXLiveConstants.EVT_DESCRIPTION) + "]";
                    NLog.e(TAG, msg);
                    callbackOnThread(mListener, "onError", TCConstants.ERROR_1, msg, param);

                } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
                    int width = param.getInt(TXLiveConstants.EVT_PARAM1, 0);
                    int height = param.getInt(TXLiveConstants.EVT_PARAM2, 0);
                    if (width > 0 && height > 0) {
                        float ratio = (float) height / width;
                        //pc上混流后的宽高比为4:5，这种情况下填充模式会把左右的小主播窗口截掉一部分，用适应模式比较合适
                        if (ratio > 1.3f) {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        } else {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        }
                    }
                } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END){
                    if (vod_type == 2){
                        if (mTXLivePlayer != null && mMixedPlayURL != null){
                            int playType = getPlayType(mMixedPlayURL);
                            mTXLivePlayer.startPlay(mMixedPlayURL, playType);
                        }
                    }
                }else {
                    if (vod_type == 2){
                        if (mTXLivePlayer != null && mTXLivePlayer.isPlaying()){
//                            NLog.e(TAG, "播放>>>>>>>>>" + event);
                            TT = true;
                        }else {
                            if (mMixedPlayURL != null && TT){
                                TT = false;
                                int playType = getPlayType(mMixedPlayURL);
                                mTXLivePlayer.startPlay(mMixedPlayURL, playType);
                            }
//                            NLog.e(TAG, "停止>>>>>>>>>" + event);
                        }
                    }
                }
            }

            @Override
            public void onNetStatus(Bundle status) {

            }
        });
    }

    private boolean TT = false;

    @Override
    public void setListener(IMLVBLiveRoomListener listener) {
        mListener = listener;
    }

    /**
     * 开始推流 主播
     *
     * @param live_id
     * @param selfPushUrl
     * @param callback
     */
    @Override
    public void createRoom(final String live_id, String selfPushUrl , final IMLVBLiveRoomListener.CreateRoomCallback callback) {
        isCreateRoom = false;
        mSelfRoleType = LIVEROOM_ROLE_PUSHER;
        mLiveId = live_id;
        //开始推流
        startPushStream(selfPushUrl, TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, new StandardCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                callbackOnThread(callback, "onError", errCode, errInfo);
            }

            @Override
            public void onSuccess() {
                //推流中，可能会重复收到PUSH_EVT_PUSH_BEGIN事件，onSuccess可能会被回调多次，如果已经创建的房间，直接返回
                if (isCreateRoom) {
                    return;
                }
                isCreateRoom = true;

                if (mTXLivePusher != null) {
                    TXLivePushConfig config = mTXLivePusher.getConfig();
                    config.setVideoEncodeGop(2);
                    mTXLivePusher.setConfig(config);
                }

                mJoinPusher = true;

                //发起直播成功后
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("live_id", mLiveId)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/live/launchLiveSuccess", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        callbackOnThread(callback, "onError", errCode, errInfo);
                    }
                    @Override
                    public void onSuccess(String result) {
                        callbackOnThread(callback, "onSuccess", mLiveId);
                    }
                });
            }
        });
    }

    /**
     * 进入房间（观众调用）
     *
     * @param vod_type
     * @param live_id
     * @param mixedPlayURL
     * @param view
     * @param callback
     */
    @Override
    public void enterRoom(int vod_type, String live_id, String mixedPlayURL, TXCloudVideoView view, IMLVBLiveRoomListener.EnterRoomCallback callback) {
        mSelfRoleType = LIVEROOM_ROLE_PLAYER;

        mLiveId = live_id;
        mMixedPlayURL = mixedPlayURL;
        this.vod_type = vod_type;

        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        if (mMixedPlayURL != null && mMixedPlayURL.length() > 0) {
            int playType = getPlayType(mMixedPlayURL);
            mTXLivePlayer.setPlayerView(view);
            mTXLivePlayer.startPlay(mMixedPlayURL, playType);

            callbackOnThread(callback, "onSuccess");
        } else {
            callbackOnThread(callback, "onError", LiveRoomErrorCode.ERROR_PLAY, "[LiveRoom] 未找到CDN播放地址");
        }
    }

    /**
     * 离开房间
     *
     * @param callback 离开房间的结果回调
     */
    @Override
    public void exitRoom(final IMLVBLiveRoomListener.ExitRoomCallback callback) {
        if (mSelfRoleType == LIVEROOM_ROLE_PUSHER){
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/live/closeLive", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    callbackOnThread(callback, "onError", errCode, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    callbackOnThread(callback, "onSuccess");
                }
            });
        }else {
            //1:直播 2:录播
            String url;
            if (vod_type == 2){
                url = "app/live/leaveLive4Lubo";
            }else {
                url = "app/live/leaveLive";
            }
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    callbackOnThread(callback, "onError", errCode, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    callbackOnThread(callback, "onSuccess");
                }
            });
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //结束本地推流
                if (mPreviewType == LIVEROOM_CAMERA_PREVIEW) {
                    stopLocalPreview();
                } else {
                    stopScreenCapture();
                }
                unInitLivePusher();

                //结束所有加速流的播放
                cleanPlayers();

                //5. 结束普通流播放
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.stopPlay(true);
                    mTXLivePlayer.setPlayerView(null);
                }
            }
        });

        mJoinPusher = false;
        mSelfRoleType = LIVEROOM_ROLE_NONE;
    }

    /**
     * 观众请求连麦
     *
     * @param accelerateURL
     */
    @Override
    public void startPlayAccelerateURL(String accelerateURL) {
        if (!TextUtils.isEmpty(accelerateURL)) {
            mTXLivePlayer.stopPlay(true);
            mTXLivePlayer.startPlay(accelerateURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
        } else {
            NLog.e(TAG, "观众连麦获取不到加速流地址");
        }
    }

    /**
     * 观众进入连麦状态
     *
     * @param callback
     */
    @Override
    public void joinAnchor(String selfPushUrl, final IMLVBLiveRoomListener.JoinAnchorCallback callback) {
        isJoinAnchor = false;
        startPushStream(selfPushUrl, TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER, new StandardCallback() {
            @Override
            public void onError(final int code, final String info) {
                callbackOnThread(callback, "onError", code, info);
            }

            @Override
            public void onSuccess() {
                if (isJoinAnchor) {
                    return;
                }
                isJoinAnchor = true;

                //推流成功
                mJoinPusher = true;
                callbackOnThread(callback, "onSuccess");
            }
        });
    }

    /**
     * 观众退出连麦
     */
    @Override
    public void quitJoinAnchor(String userID) {
        NLog.e(TAG, "API -> quitJoinAnchor");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //1. 结束本地预览
                if (mPreviewType == LIVEROOM_CAMERA_PREVIEW) {
                    stopLocalPreview();
                } else {
                    stopScreenCapture();
                }
                unInitLivePusher();

                //2. 结束所有加速流的播放
                cleanPlayers();

                //3. 结束播放大主播的加速流，改为播放普通流
                mTXLivePlayer.stopPlay(true);
                String mixedPlayUrl = mMixedPlayURL;
                if (mixedPlayUrl != null && mixedPlayUrl.length() > 0) {
                    int playType = getPlayType(mixedPlayUrl);
                    mTXLivePlayer.startPlay(mixedPlayUrl, playType);
                }
            }
        });

        mJoinPusher = false;

        //关闭连麦（观众-主播）
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveId)
                    .put("viewer_uid", userID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/closeLianmai", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                NLog.e(TAG, "结束连麦成功");
            }
        });
    }

    /**
     * 主播踢人
     *
     * @param userID
     */
    @Override
    public void kickoutJoinAnchor(String userID) {
        //关闭连麦（观众-主播）
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveId)
                    .put("viewer_uid", userID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/closeLianmai", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                NLog.e(TAG, "结束连麦成功");
            }
        });
    }

    /**
     * 完成PK
     *
     * @param live_id
     * @param isLaunch
     */
    @Override
    public void finishPK(String live_id, boolean isLaunch) {
        String launch_live_id;
        String accept_live_id;
        if (isLaunch){
            launch_live_id = mLiveId;
            accept_live_id = live_id;
        }else {
            launch_live_id = live_id;
            accept_live_id = mLiveId;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", launch_live_id)
                    .put("accept_live_id", accept_live_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/finishPK", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 关闭PK
     *
     * @param live_id
     * @param isLaunch
     */
    @Override
    public void closePK(String live_id, boolean isLaunch) {
        String launch_live_id;
        String accept_live_id;
        if (isLaunch){
            launch_live_id = mLiveId;
            accept_live_id = live_id;
        }else {
            launch_live_id = live_id;
            accept_live_id = mLiveId;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", launch_live_id)
                    .put("accept_live_id", accept_live_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/closePK", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 开启本地视频的预览画面
     *
     * @param frontCamera
     * @param view
     * @param fuRenderer
     */
    @Override
    public void startLocalPreview(boolean frontCamera, TXCloudVideoView view, FURenderer fuRenderer) {
        NLog.e(TAG, "API -> startLocalPreview:" + frontCamera);
        mFURenderer = fuRenderer;
        initLivePusher(frontCamera);
        if (mTXLivePusher != null) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            mTXLivePusher.startCameraPreview(view);
        }
        mPreviewType = LIVEROOM_CAMERA_PREVIEW;
        oneselfTXCVV = view;
    }

    private TXCloudVideoView oneselfTXCVV;
    public void changeOneself(boolean isD, int widthPixels, int topMargin, ImageView mBgImageView){
        if (isD){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            oneselfTXCVV.setLayoutParams(layoutParams);
            if (mBgImageView != null){
                mBgImageView.setLayoutParams(layoutParams);
            }
        }else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPixels, (int)(widthPixels * Const.ratio));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.topMargin = topMargin;
//            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            oneselfTXCVV.setLayoutParams(layoutParams);
            if (mBgImageView != null){
                mBgImageView.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * 停止本地视频采集及预览
     */
    @Override
    public void stopLocalPreview() {
        NLog.e(TAG, "API -> stopLocalPreview");
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
        }
    }

    /**
     * 启动渲染远端视频画面
     *
     * @param anchorInfo
     * @param view
     * @param callback
     */
    @Override
    public void startRemoteView(final AnchorInfo anchorInfo, final TXCloudVideoView view, final IMLVBLiveRoomListener.PlayCallback callback) {
        //主线程启动播放
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayers.containsKey(anchorInfo.userID)) {
                    PlayerItem pusherPlayer = mPlayers.get(anchorInfo.userID);
                    NLog.d("断开了连麦","5  "+ pusherPlayer.player.isPlaying());
                    if (pusherPlayer.player.isPlaying()) {
                        //已经在播放
                        return;
                    } else {
                        pusherPlayer = mPlayers.remove(anchorInfo.userID);
                        pusherPlayer.destroy();
                    }
                }

                /*if (mSelfRoleType == LIVEROOM_ROLE_PUSHER) {
                    if (mPlayers.size() == 0) {
                        if (mTXLivePusher != null) {
                            if (isPK) {
                                //PK
                                mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER, true, true);
                                TXLivePushConfig config = mTXLivePusher.getConfig();
                                config.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                                config.setAutoAdjustBitrate(false);
                                config.setVideoBitrate(800);
                                mTXLivePusher.setConfig(config);
                            } else {
                                //连麦，设置推流Quality为VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER
                                mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER, true, false);
                            }
                        }
                    }
                }*/
                if (mSelfRoleType == LIVEROOM_ROLE_PUSHER
                        && mTXLivePusher != null
                        && mPlayers.size() == 0){
                    mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER, true, false);
                }

                NLog.d("断开了连麦","6  ");

                TXLivePlayer player = new TXLivePlayer(mAppContext);
                view.setVisibility(View.VISIBLE);
                player.setPlayerView(view);
                player.enableHardwareDecode(true);
                player.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

                PlayerItem anchorPlayer = new PlayerItem(view, anchorInfo, player);
                mPlayers.put(anchorInfo.userID, anchorPlayer);

                player.setPlayListener(new ITXLivePlayListener() {
                    @Override
                    public void onPlayEvent(final int event, final Bundle param) {
                        NLog.d("断开了连麦","7  "+event);
                        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                            if (mSelfRoleType == LIVEROOM_ROLE_PUSHER) {

                            }
                            callbackOnThread(callback, "onBegin");
                        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END || event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT){
                            callbackOnThread(callback, "onError", event, "[LivePlayer] 播放异常[" + param.getString(TXLiveConstants.EVT_DESCRIPTION) + "]");
                        } else {
                            callbackOnThread(callback, "onEvent", event, param);
                        }
                    }

                    @Override
                    public void onNetStatus(Bundle status) {

                    }
                });

                int result = player.startPlay(anchorInfo.accelerateURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                if (result != 0){
                    NLog.e(TAG, String.format("[BaseRoom] 播放成员 {%s} 地址 {%s} 失败", anchorInfo.userID, anchorInfo.accelerateURL));
                }
            }
        });
    }

    /**
     * 停止渲染远端视频画面
     *
     * @param anchorInfo
     */
    @Override
    public void stopRemoteView(final AnchorInfo anchorInfo) {
        NLog.e(TAG, "API -> stopRemoteView:" + anchorInfo.userID);
        if (anchorInfo == null || anchorInfo.userID == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayers.containsKey(anchorInfo.userID)){
                    PlayerItem pusherPlayer = mPlayers.remove(anchorInfo.userID);
                    pusherPlayer.destroy();
                }

                if (mSelfRoleType == LIVEROOM_ROLE_PUSHER) {
                    if (mPlayers.size() == 0) {
                        //没有播放流了，切换推流回直播模式
                        if (mTXLivePusher != null) {
                            mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
                            TXLivePushConfig config = mTXLivePusher.getConfig();
                            config.setVideoEncodeGop(2);
                            mTXLivePusher.setConfig(config);
                        }
                    }
                }
            }
        });
    }

    /**
     * 启动录屏。
     *
     */
    public synchronized void startScreenCapture() {
        NLog.e(TAG, "API -> startScreenCapture");
        initLivePusher(true);
        if (mTXLivePusher != null) {
            mTXLivePusher.startScreenCapture();
        }
        mPreviewType = LIVEROOM_SCREEN_PREVIEW;
    }

    /**
     * 结束录屏。
     *
     */
    public synchronized void stopScreenCapture() {
        NLog.e(TAG, "API -> stopScreenCapture");
        if (mTXLivePusher != null) {
            mTXLivePusher.stopScreenCapture();
        }
    }

    /**
     * 是否屏蔽本地音频
     *
     * @param mute true:屏蔽 false:开启
     */
    @Override
    public void muteLocalAudio(boolean mute) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setMute(mute);
        }
    }

    /**
     * 设置指定用户是否静音
     *
     * @param userID 对方的用户标识
     * @param mute   true:静音 false:非静音
     */
    @Override
    public void muteRemoteAudio(String userID, boolean mute) {
        /*if (mPlayers.containsKey(userID)){
            PlayerItem pusherPlayer = mPlayers.get(userID);
            pusherPlayer.player.setMute(mute);
        } else if (userID.equals(mAnchorId)) {
            //主播
            mTXLivePlayer.setMute(mute);
        }*/
    }

    /**
     * 设置所有远端用户是否静音
     *
     * @param mute true:静音 false:非静音
     */
    @Override
    public void muteAllRemoteAudio(boolean mute) {
        for (Map.Entry<String, PlayerItem> entry : mPlayers.entrySet()) {
            entry.getValue().player.setMute(mute);
        }
        if (mTXLivePlayer != null && mTXLivePlayer.isPlaying()) {
            mTXLivePlayer.setMute(mute);
        }
    }

    /**
     * 切换摄像头
     */
    @Override
    public void switchCamera() {
        if (mTXLivePusher != null) {
            mTXLivePusher.switchCamera();
        }

        if (mFURenderer != null){
            mFrontCamera = !mFrontCamera;
            /*切换摄像头*/
            mFURenderer.onCameraChange(mFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                    Camera.CameraInfo.CAMERA_FACING_BACK, 0);
        }

        /*if (mTXLivePusher == null){
            return;
        }
        if (mFrontCamera){
            mTXLivePusher.setMirror(true);
        }else {
            mTXLivePusher.setMirror(false);
        }*/
    }

    /**
     * 设置摄像头缩放因子（焦距）
     *
     * @param distance 取值范围 1 - 5 ，当为1的时候为最远视角（正常镜头），当为5的时候为最近视角（放大镜头），这里最大值推荐为5，超过5后视频数据会变得模糊不清
     *
     * @return false：调用失败；true：调用成功
     */
    @Override
    public boolean setZoom(int distance) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setZoom(distance);
        }
        return false;
    }

    /**
     * 开关闪光灯
     *
     * @param enable true：开启；false：关闭
     *
     * @return false：调用失败；true：调用成功
     */
    @Override
    public boolean enableTorch(boolean enable) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.turnOnFlashLight(enable);
        }
        return false;
    }

    /**
     * 主播屏蔽摄像头期间需要显示的等待图片
     *
     * 当主播屏蔽摄像头，或者由于 App 切入后台无法使用摄像头的时候，我们需要使用一张等待图片来提示观众“主播暂时离开，请不要走开”。
     *
     * @param bitmap 位图
     */
    @Override
    public void setCameraMuteImage(Bitmap bitmap) {
        if (mTXLivePusher != null) {
            TXLivePushConfig config = mTXLivePusher.getConfig();
            config.setPauseImg(bitmap);
            config.setPauseImg(60, 10);
            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXLivePusher.setConfig(config);
        }
    }

    /**
     * 主播屏蔽摄像头期间需要显示的等待图片
     *
     * 当主播屏蔽摄像头，或者由于 App 切入后台无法使用摄像头的时候，我们需要使用一张等待图片来提示观众“主播暂时离开，请不要走开”。
     *
     * @param id 设置默认显示图片的资源文件
     */
    @Override
    public void setCameraMuteImage(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(mAppContext.getResources(), id);
        if (mTXLivePusher != null) {
            TXLivePushConfig config = mTXLivePusher.getConfig();
            config.setPauseImg(bitmap);
            config.setPauseImg(60, 10);
            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXLivePusher.setConfig(config);
        }
    }

    @Override
    public void resumePush(){
        if (mTXLivePusher != null){
            mTXLivePusher.resumePusher();
        }
    }

    @Override
    public void pausePush(){
        if (mTXLivePusher != null){
            mTXLivePusher.pausePusher();
        }
    }

    /**
     * 设置美颜、美白、红润效果级别
     *
     * @param beautyStyle    美颜风格，三种美颜风格：0 ：光滑；1：自然；2：朦胧
     * @param beautyLevel    美颜级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     * @param whitenessLevel 美白级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     * @param ruddinessLevel 红润级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     */
    @Override
    public boolean setBeautyStyle(int beautyStyle, int beautyLevel, int whitenessLevel, int ruddinessLevel) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setBeautyFilter(beautyStyle, beautyLevel, whitenessLevel, ruddinessLevel);
        }
        return false;
    }

    /**
     * 设置指定素材滤镜特效
     *
     * @param image 指定素材，即颜色查找表图片。注意：一定要用 png 格式！！！
     */
    @Override
    public void setFilter(Bitmap image) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFilter(image);
        }
    }

    /**
     * 设置滤镜浓度
     *
     * @param concentration 从0到1，越大滤镜效果越明显，默认取值0.5
     */
    @Override
    public void setFilterConcentration(float concentration) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setSpecialRatio(concentration);
        }
    }

    /**
     * 添加水印，height 不用设置，sdk 内部会根据水印宽高比自动计算 height
     *
     * @param image      水印图片 null 表示清除水印
     * @param x          归一化水印位置的 X 轴坐标，取值[0,1]
     * @param y          归一化水印位置的 Y 轴坐标，取值[0,1]
     * @param width      归一化水印宽度，取值[0,1]
     */
    @Override
    public void setWatermark(Bitmap image, float x, float y, float width) {
        if (mTXLivePusher != null) {
            TXLivePushConfig config = mTXLivePusher.getConfig();
            config.setWatermark(image, x, y, width);
            mTXLivePusher.setConfig(config);
        }
    }

    /**
     * 设置动效贴图
     *
     * @param filePath 动态贴图文件路径
     */
    @Override
    public void setMotionTmpl(String filePath) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setMotionTmpl(filePath);
        }
    }

    /**
     * 设置绿幕文件
     *
     * 目前图片支持jpg/png，视频支持mp4/3gp等Android系统支持的格式
     *
     * @param file 绿幕文件位置，支持两种方式：
     *             1.资源文件放在assets目录，path直接取文件名
     *             2.path取文件绝对路径
     *
     * @return false：调用失败；true：调用成功
     * @note API要求18
     */
    @Override
    public boolean setGreenScreenFile(String file) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setGreenScreenFile(file);
        }
        return false;
    }

    /**
     * 设置大眼效果
     *
     * @param level 大眼等级取值为 0 ~ 9。取值为0时代表关闭美颜效果。默认值：0
     */
    @Override
    public void setEyeScaleLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setEyeScaleLevel(level);
        }
    }

    /**
     * 设置V脸（特权版本有效，普通版本设置此参数无效）
     *
     * @param level V脸级别取值范围 0 ~ 9。数值越大，效果越明显。默认值：0
     */
    @Override
    public void setFaceVLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceVLevel(level);
        }
    }

    /**
     * 设置瘦脸效果
     *
     * @param level 瘦脸等级取值为 0 ~ 9。取值为0时代表关闭美颜效果。默认值：0
     */
    @Override
    public void setFaceSlimLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceSlimLevel(level);
        }
    }

    /**
     * 设置短脸（特权版本有效，普通版本设置此参数无效）
     *
     * @param level 短脸级别取值范围 0 ~ 9。 数值越大，效果越明显。默认值：0
     */
    @Override
    public void setFaceShortLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceShortLevel(level);
        }
    }

    /**
     * 设置下巴拉伸或收缩（特权版本有效，普通版本设置此参数无效）
     *
     * @param chinLevel 下巴拉伸或收缩级别取值范围 -9 ~ 9。数值越大，拉伸越明显。默认值：0
     */
    @Override
    public void setChinLevel(int chinLevel) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setChinLevel(chinLevel);
        }
    }

    /**
     * 设置瘦鼻（特权版本有效，普通版本设置此参数无效）
     *
     * @param noseSlimLevel 瘦鼻级别取值范围 0 ~ 9。数值越大，效果越明显。默认值：0
     */
    @Override
    public void setNoseSlimLevel(int noseSlimLevel) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setNoseSlimLevel(noseSlimLevel);
        }
    }

    /**
     * 调整曝光
     *
     * @param value 曝光比例，表示该手机支持最大曝光调整值的比例，取值范围从-1 - 1。
     *              负数表示调低曝光，-1是最小值；正数表示调高曝光，1是最大值；0表示不调整曝光
     */
    public void setExposureCompensation(float value) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setExposureCompensation(value);
        }
    }

    /**
     * 发送自定义文本消息
     *
     * @param cmd
     * @param message
     * @param callback
     */
    @Override
    public void sendRoomMsg(int cmd, String message, final IMLVBLiveRoomListener.RequestCallback callback) {
        if (cmd == TCConstants.IMCMD_ENTER_LIVE){
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/live/joinLiveSuccess", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mAppContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    if (callback != null){
                        callbackOnThread(callback, "onSuccess", result);
                    }
                }
            });
        }else if (cmd == TCConstants.IMCMD_PAILN_TEXT){
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .put("msg_text", message)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("push/liveim/sendGroupMsg4Live", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mAppContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                }
            });
        }else if (cmd == TCConstants.IMCMD_DANMU){
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .put("msg_text", message)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("push/liveim/sendBulletScreenMsg4Live", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mAppContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                }
            });
        }else if (cmd == TCConstants.IMCMD_PRAISE){
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("live_id", mLiveId)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/live/addLike", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                }
                @Override
                public void onSuccess(String result) {
                }
            });
        }
    }


    /**
     * 播放背景音乐
     *
     * @param path 背景音乐文件路径
     * @return true：播放成功；false：播放失败
     */
    @Override
    public boolean playBGM(String path) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.playBGM(path);
        }
        return false;
    }

    @Override
    public void setBGMNofify(TXLivePusher.OnBGMNotify var1) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setBGMNofify(var1);
        }
    }

    /**
     * 停止播放背景音乐
     */
    @Override
    public void stopBGM() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopBGM();
        }
    }

    /**
     * 暂停播放背景音乐
     */
    @Override
    public void pauseBGM() {
        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }
    }

    /**
     * 继续播放背景音乐
     */
    @Override
    public void resumeBGM() {
        if (mTXLivePusher != null) {
            mTXLivePusher.resumeBGM();
        }
    }

    /**
     * 获取音乐文件总时长
     *
     * @param path 音乐文件路径，如果 path 为空，那么返回当前正在播放的 music 时长
     * @return 成功返回时长，单位毫秒，失败返回-1
     */
    @Override
    public int getBGMDuration(String path) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.getMusicDuration(path);
        }
        return 0;
    }

    /**
     * 设置麦克风的音量大小，播放背景音乐混音时使用，用来控制麦克风音量大小
     *
     * @param volume : 音量大小，100为正常音量，建议值为0 - 200
     */
    @Override
    public void setMicVolumeOnMixing(int volume) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setMicVolume(volume/100.0f);
        }
    }

    /**
     * 设置背景音乐的音量大小，播放背景音乐混音时使用，用来控制背景音音量大小
     *
     * @param volume 音量大小，100为正常音量，建议值为0 - 200，如果需要调大背景音量可以设置更大的值
     */
    @Override
    public void setBGMVolume(int volume) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setBGMVolume(volume/100.0f);
        }
    }

    /**
     * 设置混响效果
     *
     * @param reverbType 混响类型，详见
     *                      {@link TXLiveConstants#REVERB_TYPE_0 } (关闭混响)
     *                      {@link TXLiveConstants#REVERB_TYPE_1 } (KTV)
     *                      {@link TXLiveConstants#REVERB_TYPE_2 } (小房间)
     *                      {@link TXLiveConstants#REVERB_TYPE_3 } (大会堂)
     *                      {@link TXLiveConstants#REVERB_TYPE_4 } (低沉)
     *                      {@link TXLiveConstants#REVERB_TYPE_5 } (洪亮)
     *                      {@link TXLiveConstants#REVERB_TYPE_6 } (磁性)
     */
    @Override
    public void setReverbType(int reverbType) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setReverb(reverbType);
        }
    }

    /**
     * 设置变声类型
     *
     * @param voiceChangerType 变声类型，详见 TXVoiceChangerType
     */
    @Override
    public void setVoiceChangerType(int voiceChangerType) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setVoiceChangerType(voiceChangerType);
        }
    }

    /**
     * 设置背景音乐的音调。
     *
     * 该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放。
     *
     * @param pitch 音调，0为正常音量，范围是 -1 - 1。
     */
    public void setBgmPitch(float pitch) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setBGMPitch(pitch);
        }
    }

    protected void startPushStream(String url, int videoQuality, StandardCallback callback){
        //在主线程开启推流
        if (mTXLivePusher != null && mTXLivePushListener != null) {
            mTXLivePushListener.setCallback(callback);
            mTXLivePusher.setVideoQuality(videoQuality, false, false);
            int ret = mTXLivePusher.startPusher(url);
            if (ret == -5) {
                String msg = "[LiveRoom] 推流失败[license 校验失败]";
                NLog.e(TAG, msg);
                if (callback != null) callback.onError(LiveRoomErrorCode.ERROR_LICENSE_INVALID, msg);
            }
        } else {
            String msg = "[LiveRoom] 推流失败[TXLivePusher未初始化，请确保已经调用startLocalPreview]";
            NLog.e(TAG, msg);
            if (callback != null) callback.onError(LiveRoomErrorCode.ERROR_PUSH, msg);
        }
    }

    protected void cleanPlayers() {
        synchronized (this) {
            for (Map.Entry<String, PlayerItem> entry : mPlayers.entrySet()) {
                entry.getValue().destroy();
            }
            mPlayers.clear();
        }
    }

    private int getPlayType(String playUrl) {
        int playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        if (playUrl.startsWith("rtmp://")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
            playType = vod_type == 2 ? TXLivePlayer.PLAY_TYPE_VOD_FLV : TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        }else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".mp4")) {
            playType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
        }else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".m3u8")) {
            playType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
        }
        return playType;
    }

    private void initLivePusher(boolean frontCamera) {
        if (mTXLivePusher == null) {
            TXLivePushConfig config = new TXLivePushConfig();
            //config.enablePureAudioPush(pureAudio);// true 为启动纯音频推流，而默认值是 false；
            config.setTouchFocus(false);//自动对焦
            config.setFrontCamera(frontCamera);
//            config.enableAEC(true);//回声消除
//            config.enableANS(true);//噪声抑制
            config.enableScreenCaptureAutoRotate(mScreenAutoEnable);// 是否开启屏幕自适应
            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXLivePusher = new TXLivePusher(mAppContext);
            mTXLivePusher.setConfig(config);
//            mTXLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 5, 3, 2);
            mTXLivePushListener = new TXLivePushListenerImpl();
            mTXLivePusher.setPushListener(mTXLivePushListener);
//            mTXLivePusher.setMirror(true);

            if (mFURenderer == null){
                return;
            }
            mOnFirstCreate = true;
            mFrontCamera = true;
            mTXLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int i, int i1, int i2) {
                    if (mOnFirstCreate) {
                        NLog.d(TAG, "onTextureCustomProcess: create");
                        mFURenderer.onSurfaceCreated();
                        mOnFirstCreate = false;
                    }
                    int texId = mFURenderer.onDrawFrame(i, i1, i2);
                    return texId;
                }

                @Override
                public void onDetectFacePoints(float[] floats) {

                }

                @Override
                public void onTextureDestoryed() {
                    NLog.d(TAG, "onTextureDestoryed: t:" + Thread.currentThread().getId());
                    mFURenderer.onSurfaceDestroyed();
                    mOnFirstCreate = true;
                }
            });
        }
    }

    protected void unInitLivePusher() {
        if (mTXLivePusher != null) {
            mTXLivePushListener = null;
            mTXLivePusher.setPushListener(null);
            if (mPreviewType == LIVEROOM_CAMERA_PREVIEW) {
                mTXLivePusher.stopCameraPreview(true);
            } else {
                mTXLivePusher.stopScreenCapture();
            }
            mTXLivePusher.stopPusher();
            mTXLivePusher = null;
        }
    }

    private class TXLivePushListenerImpl implements ITXLivePushListener {
        private StandardCallback mCallback = null;

        public void setCallback(StandardCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onPushEvent(final int event, final Bundle param) {
            if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN /*|| event == TXLiveConstants.PUSH_EVT_CONNECT_SUCC*/) {
                NLog.e(TAG, "推流成功");
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
                String msg = "[LivePusher] 推流失败[打开摄像头失败]";
                NLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
                String msg = "[LivePusher] 推流失败[打开麦克风失败]";
                NLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || event == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
                String msg = "[LivePusher] 推流失败[网络断开]";
                NLog.e(TAG,msg);
                callbackOnThread(mCallback, "onError", event, msg);
            }
        }

        @Override
        public void onNetStatus(Bundle status) {

        }
    }

    private void callbackOnThread(final Object object, final String methodName, final Object... args) {
        if (object == null || methodName == null || methodName.length() == 0) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                Class objClass = object.getClass();
                while (objClass != null) {
                    Method[] methods = objClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName() == methodName) {
                            try {
                                method.invoke(object, args);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                    objClass = objClass.getSuperclass();
                }
            }
        });
    }

    private void callbackOnThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    private  class PlayerItem {
        public TXCloudVideoView view;
        public AnchorInfo       anchorInfo;
        public TXLivePlayer player;

        public PlayerItem(TXCloudVideoView view, AnchorInfo anchorInfo, TXLivePlayer player) {
            this.view = view;
            this.anchorInfo = anchorInfo;
            this.player = player;
        }

        public void resume(){
            this.player.resume();
        }

        public void pause(){
            this.player.pause();
        }

        public void destroy(){
            this.player.stopPlay(true);
            this.view.onDestroy();
        }
    }

    public interface StandardCallback {
        /**
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void onError(int errCode, String errInfo);

        void onSuccess();
    }

}
