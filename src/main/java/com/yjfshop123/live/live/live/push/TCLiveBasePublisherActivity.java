package com.yjfshop123.live.live.live.push;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Interface.ALCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.BaseSockActivity;
import com.yjfshop123.live.live.IMLVBLiveRoomListener;
import com.yjfshop123.live.live.MLVBLiveRoom;
import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.yjfshop123.live.live.commondef.AttributeInfo;
import com.yjfshop123.live.live.live.common.utils.TXPhoneStateListener;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.TCInputTextMsgDialog;
import com.yjfshop123.live.live.live.common.widget.TCSwipeAnimationController;
import com.yjfshop123.live.live.live.common.widget.TCUserAvatarListAdapter;
import com.yjfshop123.live.live.live.common.widget.danmaku.DanmuBean;
import com.yjfshop123.live.live.live.common.widget.danmaku.LiveDanmuPresenter;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBeanItem;
import com.yjfshop123.live.live.live.common.widget.gift.view.LiveRoomViewHolder;
import com.yjfshop123.live.live.live.common.widget.other.LiveGuardDialogFragment;
import com.yjfshop123.live.live.live.common.widget.like.TCHeartLayout;
import com.yjfshop123.live.live.live.common.widget.pk.LiveLinkMicPkPresenter;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptBean;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptPresenter;
import com.yjfshop123.live.live.live.common.widget.ready.DetailViewHolder;
import com.yjfshop123.live.live.live.common.widget.ready.LiveReadyViewHolder;
import com.yjfshop123.live.live.live.list.TCChatEntity;
import com.yjfshop123.live.live.live.list.TCChatMsgListAdapter;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.response.LiveViewerResponse;
import com.yjfshop123.live.live.response.LivingUserResponse;
import com.yjfshop123.live.live.response.MegGift;
import com.yjfshop123.live.live.response.MegUserInfo;
import com.yjfshop123.live.live.response.UserInfo4LiveResponse;
import com.yjfshop123.live.message.Foreground;
import com.yjfshop123.live.model.KoulingData;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.NoticeResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.response.PromDataResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.faceunity.FURenderer;
import com.opensource.svgaplayer.SVGAImageView;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class TCLiveBasePublisherActivity extends BaseSockActivity implements
        IMLVBLiveRoomListener,
        View.OnClickListener,
        TCInputTextMsgDialog.OnTextSendListener,
        CosXmlUtils.OSSResultListener,
        AdapterView.OnItemClickListener,
        FURenderer.OnTrackingStatusChangedListener,
        TXLivePusher.OnBGMNotify,
        ALCallback {

    protected static final String TAG = "TAGTAG";

    protected RelativeLayout rlRoot;
    protected int mWidthPixels;
    protected int mHeightPixels;
    protected int mTopMargin;
    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;

    //消息列表
    private ListView mListViewMsg;
    private TCInputTextMsgDialog mInputTextMsgDialog;
    protected ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    protected long lTotalMemberCount = 0;
    protected long lMemberCount = 0;

    public long mSecond = 0;

    //点赞动画
    protected TCHeartLayout mHeartLayout;

    //弹幕
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LivePromptPresenter mLivePromptPresenter;

    protected TCSwipeAnimationController mTCSwipeAnimationController;

    //电话打断
    private PhoneStateListener mPhoneListener = null;

    public String zbName;
    public String zbAvatar;
    public String zbUserId;

    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//主播与主播PK逻辑
    public boolean pureAudio;

    protected FURenderer mFURenderer;
    protected boolean mLinkMicEnable = false;

    protected ImageView mBgImageView;
    protected TXCloudVideoView mTXCloudVideoView;
    protected TCUserAvatarListAdapter mAvatarListAdapter;
    protected TextView mMemberCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mWidthPixels = CommonUtils.getScreenWidth(this);
        mHeightPixels = CommonUtils.getScreenHeight(this);
        mTopMargin = CommonUtils.dip2px(mContext, 120);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pureAudio = getIntent().getBooleanExtra("pureAudio", false);

        zbName = UserInfoUtil.getName();
        zbAvatar = UserInfoUtil.getAvatar();
        zbUserId = UserInfoUtil.getMiTencentId();

        mFURenderer = new FURenderer
                .Builder(mContext)
                .createEGLContext(false)
                .maxFaces(2)
                .setNeedFaceBeauty(true)
                .inputPropOrientation(0)
                .setOnTrackingStatusChangedListener(this)
                .build();

        initView();

        startPublish();

        mPhoneListener = new TXPhoneStateListener(mLiveRoom);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Foreground.get().setListen(this);
    }

    protected void initView() {

        rlRoot = findViewById(R.id.rl_root);
        rlRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event, null);
            }
        });

        controllLayer = findViewById(R.id.rl_controllLayer);
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(controllLayer);

        mListViewMsg = findViewById(R.id.im_msg_listview);
        mListViewMsg.setOnItemClickListener(this);
        mHeartLayout = findViewById(R.id.heart_layout);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity, findViewById(R.id.im_msg_new));
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, (ViewGroup)findViewById(R.id.danmakuView));
        }
        if (mLivePromptPresenter == null) {
            mLivePromptPresenter = new LivePromptPresenter(mContext, (ViewGroup)findViewById(R.id.promptView));
        }

        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, (ViewGroup)findViewById(R.id.container), (GifImageView)findViewById(R.id.gift_gif), (SVGAImageView)findViewById(R.id.gift_svga));
        mLiveRoomViewHolder.addToParent();

        controllLayer.setVisibility(View.INVISIBLE);
        mLiveReadyViewHolder = new LiveReadyViewHolder(mContext, (ViewGroup)findViewById(R.id.container2), mLiveRoom);
        mLiveReadyViewHolder.addToParent();
    }

    public LiveRoomViewHolder mLiveRoomViewHolder;
    public LiveReadyViewHolder mLiveReadyViewHolder;
    private RelativeLayout controllLayer;
    public boolean isReady = false;
    public boolean isDetail = false;

    protected void startPublish() {
        startPublishImpl();
    }

    protected void onCreateRoomSucess() {
        if (mShare_meidia != SHARE_MEDIA.MORE) {
            share();
        }

        String body_ = "";
        try {
            body_ = new JsonBuilder()
                    .put("scene_id", 2)//场景（1：直播列表页顶部 2：直播房间页顶部）
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/ad/getNotice", body_, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mLiveRoomViewHolder.getLiveNoticeFl().setVisibility(View.GONE);
            }
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    try {
                        NoticeResponse response = JsonMananger.jsonToBean(result, NoticeResponse.class);
                        String text = response.getContent().getText();
                        if (TextUtils.isEmpty(text)){
                            return;
                        }
                        mLiveRoomViewHolder.getLiveNoticeFl().setVisibility(View.VISIBLE);
                        mLiveRoomViewHolder.getLiveNoticeContent().setText(text);

                        final String link = response.getContent().getLink();
                        mLiveRoomViewHolder.getLiveNoticeContent().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(link)) {
                                    Intent intent = new Intent("io.xchat.intent.action.webview");
                                    intent.setPackage(mContext.getPackageName());
                                    intent.putExtra("url", link);
                                    startActivity(intent);
                                }
                            }
                        });
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/getLiveViewer", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NLog.e(TAG, "获取观众列表失败：" + errInfo);
            }
            @Override
            public void onSuccess(String result) {
                try{
                    LiveViewerResponse liveViewerResponse = JsonMananger.jsonToBean(result, LiveViewerResponse.class);

                    int size = liveViewerResponse.getList().size();
                    if (lMemberCount < size){
                        lMemberCount = size - 1;
                        lTotalMemberCount = lMemberCount;
                        mMemberCount.setText(String.format(Locale.CHINA,"%d",lMemberCount));
                    }

                    for (int i = 0; i < size; i++) {
                        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(
                                String.valueOf(liveViewerResponse.getList().get(i).getUser_id()),
                                liveViewerResponse.getList().get(i).getUser_nickname(),
                                CommonUtils.getUrl(liveViewerResponse.getList().get(i).getAvatar()));
                        if (mAvatarListAdapter != null){
                            mAvatarListAdapter.addItem(userInfo);
                        }
                    }
                }catch (Exception e){
                }
            }
        });
    }

    protected void startPublishImpl() {
        mLiveRoom.setBGMNofify(this);
        mLiveRoom.setListener(this);
        mLiveRoom.setCameraMuteImage(R.drawable.pause_publish);
//        createRoom();
    }

    private boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    private int mWatchNum = 0;

    public void createRoom(String result, SHARE_MEDIA shareMeidia){
        if (result == null){
            return;
        }
        String t_live_url = "";
        try {
            JSONObject data = new JSONObject(result);

            t_live_url = data.getString("t_live_url");

            mLinkMicEnable = data.getJSONObject("live_user_info").getInt("open_lianmai") == 1 ? false : true;
            mLiveID = data.getString("live_id");
            mShare_meidia = shareMeidia;

            mCoin = data.getJSONObject("live_user_info").getString("total_coin_num");
            mLiveRoomViewHolder.setVotes(mCoin);
            mWatchNum = data.getJSONObject("live_user_info").getInt("watch_num");
            mLiveRoomViewHolder.setGuardNum(mWatchNum);

            lTotalMemberCount = data.getInt("viewer_num");
            lMemberCount = data.getInt("viewer_num");
            mMemberCount.setText(String.format(Locale.CHINA,"%d",lMemberCount));

            addOneself(data.getJSONObject("live_warning").getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startCountDown();
        showLinkMicEnable();

        isReady = true;

        controllLayer.setVisibility(View.VISIBLE);
        if (mLiveReadyViewHolder != null){
            mLiveReadyViewHolder.hide();

            mLiveReadyViewHolder.removeFromParent();
            mLiveReadyViewHolder = null;
        }

        mLiveRoom.createRoom(mLiveID, t_live_url, new IMLVBLiveRoomListener.CreateRoomCallback() {
            @Override
            public void onSuccess(String roomId) {
                NLog.e(TAG,String.format("创建直播间%s成功", roomId));
                onCreateRoomSucess();
            }

            @Override
            public void onError(int errCode, String e) {
                NLog.e(TAG,String.format("创建直播间错误, code=%s,error=%s", errCode, e));

//                LiveRoomErrorCode.ERROR_LICENSE_INVALID  [LiveRoom] 推流失败[license 校验失败]
//                LiveRoomErrorCode.ERROR_PUSH [LiveRoom] 推流失败[TXLivePusher未初始化，请确保已经调用startLocalPreview]
//                TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL  推流失败[打开摄像头失败]
//                TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL 推流失败[打开麦克风失败]
//                TXLiveConstants.PUSH_ERR_NET_DISCONNECT || event == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS [LivePusher] 推流失败[网络断开]

                if (errCode == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || errCode == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS){

                    NLog.d("断开连麦","直播间一场2");
                    showErrorAndQuit(TCConstants.ERROR_8);

                }else {
                    showErrorAndQuit(TCConstants.ERROR_6);
                }
            }
        });
    }

    public void showLinkMicEnable() {

    }

    protected void stopPublish() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                NLog.e(TAG, "exitRoom Success");
            }

            @Override
            public void onError(int errCode, String e) {
                NLog.e(TAG, "exitRoom failed, errorCode = " + errCode + " errMessage = " + e);
            }
        });

        mLiveRoom.setListener(null);
    }

    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                NToast.shortToast(mContext, getString(R.string.sq_3));
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

//        TCChatEntity entity = new TCChatEntity(zbName, mAttributeInfo, TCConstants.IMCMD_PAILN_TEXT, zbUserId);
//        entity.setContent(msg);
//        notifyMsg(entity);

        if (danmuOpen) {
//            if (mLiveDanmuPresenter != null) {
//                mLiveDanmuPresenter.showDanmu(new DanmuBean(zbUserId, zbName, zbAvatar, msg));
//            }
            mLiveRoom.sendRoomMsg(TCConstants.IMCMD_DANMU, msg, null);
        } else {
            mLiveRoom.sendRoomMsg(TCConstants.IMCMD_PAILN_TEXT, msg, null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onLivePrompt(LivePromptBean bean) {
        if (mLivePromptPresenter != null) {
            mLivePromptPresenter.showDanmu(bean);
        }
    }

    @Override
    public void onRequestJoinAnchor(AnchorInfo pusherInfo) {
    }

    @Override
    public void likeMsg(MegUserInfo bean){
        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }

        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount("");
        TCChatEntity entity = new TCChatEntity(bean.getNickname(), attributeInfo, TCConstants.IMCMD_PAILN_TEXT, bean.getUser_id());
        entity.setContent("点了个赞");
        notifyMsg(entity);
    }

    @Override
    public void guardianMsg(MegUserInfo bean, String total_watch_num){
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount("");
        TCChatEntity entity = new TCChatEntity(bean.getNickname(), attributeInfo, TCConstants.IMCMD_PAILN_TEXT, bean.getUser_id());
        entity.setContent("守护了主播");
        notifyMsg(entity);

        if (!TextUtils.isEmpty(total_watch_num)){
            mWatchNum = Integer.parseInt(total_watch_num);
            if (mLiveRoomViewHolder != null){
                mLiveRoomViewHolder.setGuardNum(mWatchNum);
            }
        }
    }

    @Override
    public void allCoinChange(String coin){
        mCoin = coin;
        if (mLiveRoomViewHolder != null){
            mLiveRoomViewHolder.setVotes(coin);
        }
    }

    private String mCoin = "0";

    @Override
    public void onTxtMsg(boolean danmu, MegUserInfo bean, String content){
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount("");
        TCChatEntity entity = new TCChatEntity(bean.getNickname(), attributeInfo, TCConstants.IMCMD_PAILN_TEXT, bean.getUser_id());
        entity.setContent(content);
        notifyMsg(entity);

        if (danmu && mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.showDanmu(new DanmuBean(bean.getUser_id(), bean.getNickname(), bean.getAvatar(), content));
        }
    }

    @Override
    public void onEnterLive(MegUserInfo bean) {
        lTotalMemberCount++;
        lMemberCount++;
        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(bean.getUser_id(), bean.getNickname(), bean.getAvatar());
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount(bean.getMount());
        TCChatEntity entity = new TCChatEntity(userInfo.nickname, attributeInfo, TCConstants.IMCMD_ENTER_LIVE, userInfo.userid);
        entity.setContent("加入直播");
        notifyMsg(entity);
        if (mLiveRoomViewHolder != null){
            mLiveRoomViewHolder.onEnterRoom(userInfo, attributeInfo);
        }
    }

    @Override
    public void onExitLive(MegUserInfo bean) {
        if (lMemberCount > 0){
            lMemberCount--;
        } else {
            NLog.e(TAG, "接受多次退出请求，目前人数为负数");
        }
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount("");
        TCChatEntity entity = new TCChatEntity(bean.getNickname(), attributeInfo, TCConstants.IMCMD_EXIT_LIVE, bean.getUser_id());
        entity.setContent("退出直播");
        notifyMsg(entity);
    }

    @Override
    public void onGiftMsg(MegUserInfo beanMeg, MegGift beanGift) {
        if (mLiveRoomViewHolder != null){
            LiveReceiveGiftBeanItem item = new LiveReceiveGiftBeanItem();
            item.setGiftId(beanGift.getGift_uni_code());
            item.setGiftName(beanGift.getName());
            item.setGiftIcon(CommonUtils.getUrl(beanGift.getIcon_img()));
            item.setGifUrl(CommonUtils.getUrl(beanGift.getEffect_img()));
            item.setStyle(Integer.parseInt(beanGift.getStyle()));
            item.setGiftCount(Integer.parseInt(beanGift.getNumber()));
            item.setVotes("0");
            LiveReceiveGiftBean bean = new LiveReceiveGiftBean();
            bean.setLiveReceiveGiftBeanItem(item);
            bean.setGifPath("");
            bean.setUid(beanMeg.getUser_id());
            bean.setAvatar(beanMeg.getAvatar());
            bean.setUserNiceName(beanMeg.getNickname());
            mLiveRoomViewHolder.showGiftMessage(bean);
            AttributeInfo attributeInfo = new AttributeInfo();
            attributeInfo.setIdentityType(beanMeg.getIdentity_type());
            attributeInfo.setIsGuard(beanMeg.getIs_watch());
            attributeInfo.setIsVip(beanMeg.getIs_vip());
            attributeInfo.setUser_level(beanMeg.getLevel());
            attributeInfo.setMount("");
            //聊天消息中插入 送礼物 提示
            TCChatEntity entity = new TCChatEntity(beanMeg.getNickname(), attributeInfo, TCConstants.IMCMD_GIFT, beanMeg.getUser_id());
            entity.setContent(getString(R.string.live_send_gift_1) + bean.getLiveReceiveGiftBeanItem().getGiftCount() + "个" + bean.getLiveReceiveGiftBeanItem().getGiftName());
            notifyMsg(entity);
        }
    }

    @Override
    public void liverInvalidLive(){
        showErrorAndQuit(TCConstants.ERROR_8);
    }

    @Override
    public void onError(int errorCode, String errorMessage, Bundle extraInfo) {
        /*if (errorCode == MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE) { // IM 被强制下线。
            Intent intent = new Intent();
            intent.setAction(TCConstants.EXIT_APP);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            showErrorAndQuit(errorCode, errorMessage);
        }*/
    }

    public void showDetailDialog() {
        DetailViewHolder detailViewHolder = new DetailViewHolder(mContext, (ViewGroup)findViewById(R.id.container2),
                zbName, zbAvatar, TCUtils.formattedTime(mSecond), String.format(Locale.CHINA, "%d", lTotalMemberCount),
                mCoin);
        isDetail = true;
        detailViewHolder.addToParent();
    }

    /**
     * 显示确认消息
     */
    public void showComfirmDialog() {
        if (ISDELAYED){
            NToast.shortToast(mContext, "请稍候");
            return;
        }
        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                "当前正在直播，是否退出直播？", true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        closeMusic();
                        mLiveRoom.setBGMNofify(null);
                        stopPublish();
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.clearData();
                        }

                        showDetailDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isReady && !isDetail){
            showComfirmDialog();
        }else {
            exit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }

        if (mLivePromptPresenter != null) {
            mLivePromptPresenter.release();
            mLivePromptPresenter.reset();
        }

        if (mCountDownText != null) {
            mCountDownText.clearAnimation();
        }

        if (mLiveLinkMicPkPresenter != null){
            mLiveLinkMicPkPresenter.release();
        }

        stopPublish();

        if(mPhoneListener != null){
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mPhoneListener = null;
        }

        Foreground.get().setListen(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                showComfirmDialog();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            default:
                //mLayoutFaceBeauty.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    //TODO
    protected void showErrorAndQuit(int errorCode) {
        //ERROR_5 = 1005;//获取权限失败
        //ERROR_6 = 1006;//创建直播间失败
        //ERROR_7 = 1007;//关闭直播间
        //ERROR_8 = 1008;//主播 网络异常直播间已经关闭

        switch (errorCode){
            case TCConstants.ERROR_5:
                NToast.shortToast(mContext, "获取权限失败");
                closeMusic();
                mLiveRoom.setBGMNofify(null);
                stopPublish();
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.clearData();
                }
                exit();
                break;
            case TCConstants.ERROR_6:
                NToast.shortToast(mContext, "创建直播间失败");
                closeMusic();
                mLiveRoom.setBGMNofify(null);
                stopPublish();
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.clearData();
                }
                exit();
                break;
            case TCConstants.ERROR_7:
                NToast.shortToast(mContext, "违规 直播被迫关闭");
                closeMusic();
                mLiveRoom.setBGMNofify(null);
                stopPublish();
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.clearData();
                }
                exit();
                break;
            case TCConstants.ERROR_8:
                //图片提示
                if (mBgImageView != null){
                    mBgImageView.setVisibility(View.VISIBLE);
                }
                if (mTXCloudVideoView != null){
                    mTXCloudVideoView.setVisibility(View.GONE);
                }
                stopPublish();
                break;
        }
    }

    private void notifyMsg(final TCChatEntity entity) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(entity.getType() == TCConstants.PRAISE) {
//                    if(mArrayListChatEntity.contains(entity))
//                        return;
//                }

                if (mArrayListChatEntity.size() > 1000) {
                    while (mArrayListChatEntity.size() > 900) {
                        mArrayListChatEntity.remove(0);
                    }
                }

                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
//            }
//        });
    }
    AlertDialog mDialog;
    protected void showShareDialog() {
        View view = getLayoutInflater().inflate(R.layout.share_dialog2, null);
          mDialog = new AlertDialog.Builder(this, R.style.BottomDialog2).create();
        mDialog.show();

        Window window =mDialog.getWindow();
        if (window != null) {
            window.setContentView(view);
            window.setWindowAnimations(R.style.BottomDialog_Animation);
            WindowManager.LayoutParams mParams = window.getAttributes();
            mParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.BOTTOM);
            window.setAttributes(mParams);
        }

        TextView btn_wx = view.findViewById(R.id.btn_share_wx);
        TextView btn_circle = view.findViewById(R.id.btn_share_circle);
        TextView btn_qq = view.findViewById(R.id.btn_share_qq);
        TextView btn_qzone = view.findViewById(R.id.btn_share_qzone);
        TextView btn_wb = view.findViewById(R.id.btn_share_wb);
        TextView btn_cancel = view.findViewById(R.id.btn_share_cancle);

        btn_wx.setOnClickListener(mShareBtnClickListen);
        btn_circle.setOnClickListener(mShareBtnClickListen);
        btn_qq.setOnClickListener(mShareBtnClickListen);
        btn_qzone.setOnClickListener(mShareBtnClickListen);
        btn_wb.setOnClickListener(mShareBtnClickListen);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });


        TextView btn_share_kouling = view.findViewById(R.id.btn_share_kouling);

        btn_share_kouling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                koulingshare();
            }
        });
    }
    private void koulingshare() {
        String body = "";
        LoadDialog.show(this);
        try {
            body = new JsonBuilder()
                    .put("title", "大公鸡直播视商")
                    .put("type", "live")
                    .put("dynamic_id", mLiveID )
                    .build();
        } catch (JSONException e) {
        }
//        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        OKHttpUtils.getInstance().getRequest("app/share/kouling", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(TCLiveBasePublisherActivity.this, errInfo);
                LoadDialog.dismiss(TCLiveBasePublisherActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(TCLiveBasePublisherActivity.this);
                if (result == null) {
                    return;
                }
                try {
                    KoulingData urlData = new Gson().fromJson(result,KoulingData.class);
                    SystemUtils.setClipboardForKouling(TCLiveBasePublisherActivity.this,urlData.text);
                    Intent share_intent = new Intent();
                    share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                    share_intent.setType("text/plain");  //设置分享内容的类型
                    share_intent.putExtra(Intent.EXTRA_TEXT, urlData.text);//分享出去的内容
                   startActivity( Intent.createChooser(share_intent, "大公鸡直播视商"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private View.OnClickListener mShareBtnClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_share_wx:
                    mShare_meidia = SHARE_MEDIA.WEIXIN;
                    break;
                case R.id.btn_share_circle:
                    mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
                    break;
                case R.id.btn_share_qq:
                    mShare_meidia = SHARE_MEDIA.QQ;
                    break;
                case R.id.btn_share_qzone:
                    mShare_meidia = SHARE_MEDIA.QZONE;
                    break;
                case R.id.btn_share_wb:
                    mShare_meidia = SHARE_MEDIA.SINA;
                    break;
                default:
                    break;
            }

            share();
        }
    };

    private void share(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", zbUserId)
                    .put("live_id", mLiveID)
                    .put("vod_type", 1)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/promotion/getLiveShare", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    PromDataResponse response = JsonMananger.jsonToBean(result, PromDataResponse.class);
                    String link = response.getShare_link().getLink();
                    String title = response.getShare_link().getTitle();
                    String desc = response.getShare_link().getDesc();
                    String icon_link = CommonUtils.getUrl(response.getShare_link().getIcon_url());
                    link=link+"&type=live&dynamic_id="+mLiveID;
                    startShare(title, link, icon_link, desc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startShare(String title, String link, String icon_link, String desc) {
        ShareAction shareAction = new ShareAction(this);
        String shareUrl = link;
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(new UMImage(this, icon_link));
        web.setTitle(title);
        web.setDescription(desc);
//        shareAction.withText(desc);
        shareAction.withMedia(web);
        shareAction.setCallback(umShareListener);
        shareAction.setPlatform(mShare_meidia).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {
            if(mDialog!=null)mDialog.dismiss();

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if(mDialog!=null)mDialog.dismiss();

//            NToast.shortToast(mContext, platform + " 分享成功啦");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if(mDialog!=null)mDialog.dismiss();

//            NToast.shortToast(mContext, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if(mDialog!=null)mDialog.dismiss();

//            NToast.shortToast(mContext, "分享取消了");
        }
    };

    private void addOneself(String content){
        //系统通知
        TCChatEntity entity = new TCChatEntity(TCConstants.IMCMD_SYSTEM);
        entity.setContent(content);
        notifyMsg(entity);

    }

    @Override
    public void triggerSearch(String query, Bundle appSearchData) {
        super.triggerSearch(query, appSearchData);
    }

    public void exit(){
        hideKeyBord();
    }

    public void beauty(){

    }

    public void setBtnFunctionDark(){

    }

    public MLVBLiveRoom getmLiveRoom() {
        return mLiveRoom;
    }

    //###
    public static final int REQUEST_CODE_SELECT = 10006;
    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    private String path;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT){
            if (data != null) {
                path = data.getStringArrayListExtra("result").get(0);
                if (mLiveReadyViewHolder != null){
                    mLiveReadyViewHolder.loadPath(path);
                }
            }
        }else {
            /** attention to this below ,must add this**/
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadData(){
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.uploadData(path, "image", 21, zbUserId, uploadDialog);
    }

    public void startRecording(int duration_min){
        String durationMin = String.valueOf(duration_min);
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveID)
                    .put("duration_min", durationMin)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/startRecording", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                NToast.shortToast(mContext, "开始录制……");
                if (mLiveRoomViewHolder != null){
                    mLiveRoomViewHolder.inRecording();
                }
            }
        });
    }

    public void initOss(){
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        uploadDialog = new UploadDialog(this);

        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#B28D51"))
                // 返回图标ResId
                .backResId(R.drawable.head_back)
                // 标题
                .title(getString(R.string.per_1))
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#B28D51"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(!pureAudio)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();
        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_CODE_SELECT);
    }

    public void uploadOss(){
        uploadData();
    }

    @Override
    public void ossResult(final ArrayList<OssImageResponse> response) {
        Handler handler = new Handler(mContext.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String url = response.get(0).getObject();
                if (url.contains(BuildConfig.APPLICATION_ID)) {
                    NToast.shortToast(mContext, "上传错误");
                    return;
                }
                if (mLiveReadyViewHolder != null){
                    mLiveReadyViewHolder.startLive(url);
                }
            }
        });
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {
    }

    //###
    protected TextView mCountDownText;
    protected int mCountDownCount = 3;

    protected void startCountDown() {
        ViewGroup parent = findViewById(R.id.container2);
        mCountDownText = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_count_down, parent, false);
        parent.addView(mCountDownText);
        mCountDownText.setText(String.valueOf(mCountDownCount));
        ScaleAnimation animation = new ScaleAnimation(3, 1, 3, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setRepeatCount(2);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewGroup parent = (ViewGroup) mCountDownText.getParent();
                if (parent != null) {
                    parent.removeView(mCountDownText);
                    mCountDownText = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mCountDownCount--;
                mCountDownText.setText(String.valueOf(mCountDownCount));
            }
        });
        mCountDownText.startAnimation(animation);
    }

    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", zbUserId);
        bundle.putInt("guardNum", mWatchNum);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    public void linkMicAnchorApply(LivingUserResponse.LiveListBean bean){

    }

    public void manager(UserInfo4LiveResponse response){

    }

    public void managerList(){

    }

    public void contributeList(){

    }

    @Override
    public void onTrackingStatusChanged(int status) {
        //status <= 0  未检测到人脸
    }

    public void onBGMStart_(String filePath, String musicName){

    }

    @Override
    public void onBGMStart() {

    }

    @Override
    public void onBGMProgress(long l, long l1) {

    }

    @Override
    public void onBGMComplete(int i) {

    }

    public void closeMusic(){

    }

    public void stopRemoteViewPK(){

    }

    @Override
    public void onLiveNotice(String message) {
        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.wszl_zhidao), getString(R.string.cancel),
                message, true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void closeLive(){
        closeMusic();
        mLiveRoom.setBGMNofify(null);
        stopPublish();
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }
        showDetailDialog();
    }

    protected boolean ISDELAYED = false;

    @Override
    public void onActivityPaused() {
        if (mLiveRoom != null){
            mLiveRoom.pausePush();
        }
    }

    @Override
    public void onActivityResumed() {
        if (mLiveRoom != null){
            mLiveRoom.resumePush();
        }
    }
}
