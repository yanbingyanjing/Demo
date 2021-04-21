package com.yjfshop123.live.live.live.play;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.ALCallback;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.BaseSockActivity;
import com.yjfshop123.live.live.IMLVBLiveRoomListener;
import com.yjfshop123.live.live.MLVBLiveRoom;
import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.yjfshop123.live.live.commondef.AttributeInfo;
import com.yjfshop123.live.live.commondef.MLVBCommonDef;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyLvJingFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyTieZhiFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.IBeauty;
import com.yjfshop123.live.live.live.common.widget.beautysetting.TCBeautyHelperNew;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.utils.TCFrequeControl;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.TCInputTextMsgDialog;
import com.yjfshop123.live.live.live.common.widget.TCSwipeAnimationController;
import com.yjfshop123.live.live.live.common.widget.TCUserAvatarListAdapter;
import com.yjfshop123.live.live.live.common.widget.TCVideoWidget;
import com.yjfshop123.live.live.live.common.widget.TCVideoWidgetList;
import com.yjfshop123.live.live.live.common.widget.beautysetting.VideoMaterialMetaData;
import com.yjfshop123.live.live.live.common.widget.danmaku.DanmuBean;
import com.yjfshop123.live.live.live.common.widget.danmaku.LiveDanmuPresenter;
import com.yjfshop123.live.live.live.common.widget.gift.LiveGiftDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBeanItem;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.common.widget.gift.view.LiveRoomViewHolder;
import com.yjfshop123.live.live.live.common.widget.other.ContributeListFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveGuardBuyDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveGuardDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveUserDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ManagerDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.RotaryTableFragment;
import com.yjfshop123.live.live.live.common.widget.other.ShopDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.SuperManagerDialogFragment;
import com.yjfshop123.live.live.live.common.widget.like.TCHeartLayout;
import com.yjfshop123.live.live.live.common.widget.pk.LiveLinkMicPkPresenter;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptBean;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptPresenter;
import com.yjfshop123.live.live.live.list.TCChatEntity;
import com.yjfshop123.live.live.live.list.TCChatMsgListAdapter;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.JoinLiveResponse;
import com.yjfshop123.live.live.response.LiveViewerResponse;
import com.yjfshop123.live.live.response.MegGift;
import com.yjfshop123.live.live.response.MegUserInfo;
import com.yjfshop123.live.live.response.UserInfo4LiveResponse;
import com.yjfshop123.live.message.Foreground;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.KoulingData;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.NoticeResponse;
import com.yjfshop123.live.net.response.PromDataResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.ui.activity.MyWalletActivity1;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.faceunity.FURenderer;
import com.faceunity.entity.Filter;
import com.opensource.svgaplayer.SVGAImageView;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yjfshop123.live.xuanpin.view.ShopSelectDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;


public class TCLivePlayerActivity extends BaseSockActivity implements
        IMLVBLiveRoomListener, View.OnClickListener,
        TCInputTextMsgDialog.OnTextSendListener,
        OnItemClickListener<Integer>,
        AdapterView.OnItemClickListener,
        IBeauty,
        FURenderer.OnTrackingStatusChangedListener,
        ALCallback {

    private static final String TAG = "TAGTAG";

    private RelativeLayout rlPlayRoot;
    protected TXCloudVideoView mTXCloudVideoView;
    private TCInputTextMsgDialog mInputTextMsgDialog;
    private ListView mListViewMsg;
    private LinearLayout gouwuche_big;
    private ImageView shop_img, close;
    private TextView shop_title_new, shop_price;

    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    private TextView mMemberCount;
    private Button btn_shop;
    private boolean mPlaying = false;

    protected String mUserId = "";
    protected String mNickname = "";
    protected String mHeadPic = "";

    //头像列表控件
    private RecyclerView mUserAvatarList;
    private TCUserAvatarListAdapter mAvatarListAdapter;

    //点赞动画
    private TCHeartLayout mHeartLayout;
    //点赞频率控制
    private TCFrequeControl mLikeFrequeControl;

    //弹幕
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LivePromptPresenter mLivePromptPresenter;

    //手势动画
    private TCSwipeAnimationController mTCSwipeAnimationController;
    protected FrameLayout mBgImageView;

    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;


    //连麦相关
    private static final long LINKMIC_INTERVAL = 3 * 1000;

    private boolean mIsBeingLinkMic = false;

    private ImageView mBtnLinkMic;
    private Button mBtnSwitchCamera;
    private Button mBtnBeauty;
    private ImageView mFollowTv;

    private long mLastLinkMicTime = 0;
    private List<AnchorInfo> mPusherList = new ArrayList<>();
    private TCVideoWidgetList mPlayerList;

    //美颜
    private TCBeautyHelperNew mBeautyHepler;

    private boolean isHeart = false;

    private LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//主播与主播PK逻辑
    private int mWidthPixels;
    private int mTopMargin;
    private FURenderer mFURenderer;
    ShopSelectDialog fragment = new ShopSelectDialog();
    private String zhubo_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mWidthPixels = CommonUtils.getScreenWidth(this);
        mTopMargin = CommonUtils.dip2px(mContext, 120);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_play);

        initData();
        if (TextUtils.isEmpty(mNickname)) {
            mNickname = mUserId;
        }

        mFURenderer = new FURenderer
                .Builder(mContext)
                .createEGLContext(false)
                .maxFaces(2)
                .setNeedFaceBeauty(true)
                .inputPropOrientation(0)
                .setOnTrackingStatusChangedListener(this)
                .build();

        initView();
        startPlay();

//        Glide.with(mContext)
//                .load(mCoverUrl)
//                .into(mBgImageView);

        mPlayerList = new TCVideoWidgetList(this, null);
        Foreground.get().setListen(this);
    }

    private JoinLiveResponse mJoinLiveResponse;
    protected String mPusherId;//房间创建者ID
    protected String mPlayUrl;//房间创建者的拉流地址
    private String mPusherNickname;//主播昵称
    private int mCurrentMemberCount = 0;//观众人数
    private int mFollow = 0; // 是否关注，1关注 0未关注
    private String mPusherAvatar;//主播头像地址
    private int mWatchNum = 0;

    private String mCoverUrl = "";
    private String mTitle = ""; //标题
    public boolean pureAudio;

    private int vod_type = 1;
    private String mISGuard = "0";
    private String is_patrol;

    private void initData() {
        mUserId = UserInfoUtil.getMiTencentId();
        mNickname = UserInfoUtil.getName();
        mHeadPic = UserInfoUtil.getAvatar();

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TCConstants.ROOM_TITLE);//房间标题
        mCoverUrl = intent.getStringExtra(TCConstants.COVER_PIC);//封面图
        pureAudio = intent.getBooleanExtra("pureAudio", false);
        zhubo_user_id = intent.getStringExtra("zhubo_user_id");//主播的id用来获取主播的商品列表

        try {
            String result = intent.getStringExtra("LivePlay");
            mJoinLiveResponse = JsonMananger.jsonToBean(result, JoinLiveResponse.class);

            mPusherId = String.valueOf(mJoinLiveResponse.getLive_user().getUser_id());//房间创建者ID
            mPlayUrl = mJoinLiveResponse.getB_live_url().getHttp_flv();//房间创建者的拉流地址
            mPusherNickname = mJoinLiveResponse.getLive_user().getUser_nickname();//主播昵称
            mCurrentMemberCount = mJoinLiveResponse.getViewer_num();//观众数
            mFollow = mJoinLiveResponse.getIs_follow();//关注
            mPusherAvatar = CommonUtils.getUrl(mJoinLiveResponse.getLive_user().getAvatar());//主播头像地址
            mWatchNum = mJoinLiveResponse.getLive_user().getWatch_num();//守护数
            vod_type = intent.getIntExtra("vod_type", 1);
            mLiveID = intent.getStringExtra("live_id");
            is_patrol = mJoinLiveResponse.getIs_patrol();

            mISGuard = String.valueOf(mJoinLiveResponse.getViewer_user().getIs_watch());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnchorEnter(AnchorInfo pusherInfo) {
        if (!mIsBeingLinkMic) {
            return;
        }
        onAnchorEnter_(pusherInfo);
    }

    private void onAnchorEnter_(final AnchorInfo pusherInfo) {
        if (pusherInfo == null || pusherInfo.userID == null) {
            return;
        }

        final TCVideoWidget videoView = mPlayerList.applyVideoView(pusherInfo.userID);
        if (videoView == null) {
            return;
        }

        if (mPusherList != null) {
            boolean exist = false;
            for (AnchorInfo item : mPusherList) {
                if (pusherInfo.userID.equalsIgnoreCase(item.userID)) {
                    exist = true;
                    break;
                }
            }
            if (exist == false) {
                mPusherList.add(pusherInfo);
            }
        }

        videoView.startLoading();
        mLiveRoom.startRemoteView(pusherInfo, videoView.videoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                videoView.stopLoading(false); //推流成功，stopLoading 小主播隐藏踢人的button
                if (pureAudio) {
                    videoView.audioLive(mContext, pusherInfo.userAvatar);
                }
            }

            @Override
            public void onError(int errCode, String errInfo) {
                videoView.stopLoading(false);
                onDoAnchorExit(pusherInfo);
            }

            @Override
            public void onEvent(int event, Bundle param) {
                report(event);
            }
        }); //开启远端视频渲染
    }

    @Override
    public void onAnchorExit(AnchorInfo pusherInfo) {
        if (pusherInfo.userID.equals(mUserId)) {
            stopLinkMic();
        } else {
            onDoAnchorExit(pusherInfo);
        }
    }

    private void onDoAnchorExit(AnchorInfo pusherInfo) {
        if (mPusherList != null) {
            Iterator<AnchorInfo> it = mPusherList.iterator();
            while (it.hasNext()) {
                AnchorInfo item = it.next();
                if (pusherInfo.userID.equalsIgnoreCase(item.userID)) {
                    it.remove();
                    break;
                }
            }
        }

        mLiveRoom.stopRemoteView(pusherInfo);//关闭远端视频渲染
        mPlayerList.recycleVideoView(pusherInfo.userID);
    }

    @Override
    public void onLivePrompt(LivePromptBean bean) {
        if (mLivePromptPresenter != null) {
            mLivePromptPresenter.showDanmu(bean);
        }
    }

    @Override
    public void onRequestJoinAnchor(AnchorInfo anchorInfo) {
    }

    @Override
    public void onRequestRoomAA(AnchorInfo anchorInfo) {
    }

    @Override
    public void onRequestRoomPK(AnchorInfo anchorInfo) {
    }

    @Override
    public void onPKLiveMsg(String type, AnchorInfo anchorInfo, String pkRestTime) {
        rlPlayRoot.setBackgroundResource(R.mipmap.bg_live_pk);
        if (mLiveLinkMicPkPresenter != null) {
            int pkTime = 0;
            if (!TextUtils.isEmpty(pkRestTime)) {
                pkTime = Integer.parseInt(pkRestTime);
            }
            mLiveLinkMicPkPresenter.onEnterRoomPkStart(0, 0, pkTime, 0);
        }
    }

    @Override
    public void onPKLiveFinish(String self_get_coin, String other_get_coin, String pk_punish_rest_time) {
        long leftGift = 0;
        long rightGift = 0;
        int pkRestTime = 0;
        if (!TextUtils.isEmpty(self_get_coin)) {
            leftGift = Long.parseLong(self_get_coin);
        }
        if (!TextUtils.isEmpty(other_get_coin)) {
            rightGift = Long.parseLong(other_get_coin);
        }
        if (!TextUtils.isEmpty(pk_punish_rest_time)) {
            pkRestTime = Integer.parseInt(pk_punish_rest_time);
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.inPunishment(leftGift, rightGift, pkRestTime);
        }
    }

    @Override
    public void updateGift(String self_get_coin, String other_get_coin) {
        long leftGift = 0;
        long rightGift = 0;
        if (!TextUtils.isEmpty(self_get_coin)) {
            leftGift = Long.parseLong(self_get_coin);
        }
        if (!TextUtils.isEmpty(other_get_coin)) {
            rightGift = Long.parseLong(other_get_coin);
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onEnterRoomPkStart(leftGift, rightGift);
        }
    }

    @Override
    public void liveClosePK() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose_();
        }
    }

    @Override
    public void onQuitRoomAA() {
    }

    @Override
    public void likeMsg(MegUserInfo bean) {
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
    public void guardianMsg(MegUserInfo bean, String total_watch_num) {
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount("");
        TCChatEntity entity = new TCChatEntity(bean.getNickname(), attributeInfo, TCConstants.IMCMD_PAILN_TEXT, bean.getUser_id());
        entity.setContent("守护了主播");
        notifyMsg(entity);

        if (!TextUtils.isEmpty(total_watch_num)) {
            mWatchNum = Integer.parseInt(total_watch_num);
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.setGuardNum(mWatchNum);
            }
        }
    }

    @Override
    public void onKickoutPeople(String user_id) {
        if (mUserId.equals(user_id)) {
            showErrorAndQuit(TCConstants.ERROR_2);
        }
    }

    @Override
    public void liveClose() {
        showErrorAndQuit(TCConstants.ERROR_3);
    }

    @Override
    public void allCoinChange(String coin) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setVotes(coin);
        }
    }

    @Override
    public void onTxtMsg(boolean danmu, MegUserInfo bean, String content) {
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
        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(bean.getUser_id(), bean.getNickname(), bean.getAvatar());
        mAvatarListAdapter.addItem(userInfo);
        if (!bean.getUser_id().equals(mUserId)) {
            mCurrentMemberCount++;
        }
        mMemberCount.setText(String.format(Locale.CHINA, "%d", mCurrentMemberCount));
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setIdentityType(bean.getIdentity_type());
        attributeInfo.setIsGuard(bean.getIs_watch());
        attributeInfo.setIsVip(bean.getIs_vip());
        attributeInfo.setUser_level(bean.getLevel());
        attributeInfo.setMount(bean.getMount());
        TCChatEntity entity = new TCChatEntity(userInfo.nickname, attributeInfo, TCConstants.IMCMD_ENTER_LIVE, userInfo.userid);
        entity.setContent("加入直播");
        notifyMsg(entity);
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onEnterRoom(userInfo, attributeInfo);
        }
    }

    @Override
    public void onExitLive(MegUserInfo bean) {
        if (mCurrentMemberCount > 0) {
            mCurrentMemberCount--;
        } else {
            NLog.e(TAG, "接受多次退出请求，目前人数为负数");
        }
        mMemberCount.setText(String.format(Locale.CHINA, "%d", mCurrentMemberCount));
        mAvatarListAdapter.removeItem(bean.getUser_id());
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
        if (mLiveRoomViewHolder != null) {
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
    public void onError(int errorCode, String errorMessage, Bundle extraInfo) {
        if (errorCode == MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE) { // IM 被强制下线。
            Intent intent = new Intent();
            intent.setAction(TCConstants.EXIT_APP);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            showErrorAndQuit(errorCode);
        }
    }

    /**
     * @param event
     */
    private void report(int event) {
        switch (event) {
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                //视频播放成功
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                //网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
                break;
            case TXLiveConstants.PLAY_ERR_GET_RTMP_ACC_URL_FAIL:
                //获取加速拉流地址失败
                break;
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                //播放文件不存在
                break;
            case TXLiveConstants.PLAY_ERR_HEVC_DECODE_FAIL:
                //H265解码失败
                break;
            case TXLiveConstants.PLAY_ERR_HLS_KEY:
                //HLS解码Key获取失败
                break;
            case TXLiveConstants.PLAY_ERR_GET_PLAYINFO_FAIL:
                //获取点播文件信息失败
                break;

        }
    }

    protected void initView() {
        rlPlayRoot = (RelativeLayout) findViewById(R.id.rl_play_root);
        rlPlayRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event, TCLivePlayerActivity.this);
            }
        });
        btn_shop = findViewById(R.id.btn_shop);
        RelativeLayout controllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(controllLayer);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);
        mListViewMsg = (ListView) findViewById(R.id.im_msg_listview);
        mListViewMsg.setOnItemClickListener(this);
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        gouwuche_big = findViewById(R.id.gouwuche_big);
        shop_img = findViewById(R.id.shop_img);
        close = findViewById(R.id.close);
        shop_title_new = findViewById(R.id.shop_title_new);
        shop_price = findViewById(R.id.shop_price);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gouwuche_big.setVisibility(View.GONE);
            }
        });
        gouwuche_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firtShop != null) {
                    Intent intent = new Intent(TCLivePlayerActivity.this, NewShopDetailActivity.class);
                    intent.putExtra("goods_id", firtShop.goods_url);
                    startActivity(intent);
                }
            }
        });
        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, mPusherId, mUserId, mLiveID);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        ImageView headIcon = findViewById(R.id.iv_head_icon);
        TextView tv_broadcasting_name = findViewById(R.id.tv_broadcasting_name);
        tv_broadcasting_name.setText(TCUtils.getLimitString(mPusherNickname, 10));
        TextView tv_host_id = findViewById(R.id.tv_host_id);
        tv_host_id.setText("ID:" + mPusherId);
        showHeadIcon(headIcon, mPusherAvatar);

        mMemberCount = findViewById(R.id.tv_member_counts);
//        mCurrentMemberCount++;
        mMemberCount.setText(String.format(Locale.CHINA, "%d", mCurrentMemberCount));

        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity, findViewById(R.id.im_msg_new));
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, (ViewGroup) findViewById(R.id.danmakuView));
        }
        if (mLivePromptPresenter == null) {
            mLivePromptPresenter = new LivePromptPresenter(mContext, (ViewGroup) findViewById(R.id.promptView));
        }
        mBgImageView = findViewById(R.id.background);

        mBtnLinkMic = findViewById(R.id.btn_linkmic);
        mBtnLinkMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBeingLinkMic == false) {
                    long curTime = System.currentTimeMillis();
                    if (curTime < mLastLinkMicTime + LINKMIC_INTERVAL) {
                        //NToast.shortToast(mContext, "太频繁啦，休息一下！");
                    } else {
                        mLastLinkMicTime = curTime;
                        startLinkMic();
                    }
                } else {
                    DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                            "退出连麦", true, true,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {

                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    dialog.dismiss();

                                    stopLinkMic();
                                    startPlay();
                                }
                            });
                }
            }
        });

        mBeautyHepler = new TCBeautyHelperNew(mFURenderer);
        mBtnBeauty = findViewById(R.id.btn_beauty);

        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, (ViewGroup) findViewById(R.id.container), (GifImageView) findViewById(R.id.gift_gif), (SVGAImageView) findViewById(R.id.gift_svga));
        mLiveRoomViewHolder.addToParent();
        NLog.d("直播数据", new Gson().toJson(mJoinLiveResponse));
        mLiveRoomViewHolder.setVotes(String.valueOf(mJoinLiveResponse.getLive_user().getTotal_coin_num()));
        mLiveRoomViewHolder.setGuardNum(mWatchNum);

        findViewById(R.id.btn_message_input).setOnClickListener(this);
        mBtnSwitchCamera = findViewById(R.id.btn_switch_cam);
        mBtnSwitchCamera.setOnClickListener(this);
        mBtnBeauty.setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
        findViewById(R.id.btn_play_msg).setOnClickListener(this);
        findViewById(R.id.btn_shop).setOnClickListener(this);
        findViewById(R.id.btn_rotary).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        Button btnPatrol = findViewById(R.id.btn_patrol);
        if (TextUtils.isEmpty(is_patrol) || is_patrol.equals("0")) {
            btnPatrol.setVisibility(View.GONE);
        } else {
            btnPatrol.setVisibility(View.VISIBLE);
            btnPatrol.setOnClickListener(this);
        }
        mFollowTv = findViewById(R.id.layout_live_pusher_info_follow);
        if (mFollow == 1) {
            mFollowTv.setVisibility(View.GONE);
        } else {
            mFollowTv.setVisibility(View.VISIBLE);
        }
        mFollowTv.setOnClickListener(this);

        addOneself();

        //pk
        ViewGroup mContainer3 = findViewById(R.id.container3);
        int widthPixels = mWidthPixels / 2;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidthPixels, (int) (widthPixels * Const.ratio));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.topMargin = mTopMargin;
        mContainer3.setLayoutParams(layoutParams);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mContainer3, false);
        loadShopData();
    }

    private void loadShopData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .put("uid", zhubo_user_id)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/medal/goods", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                XuanPInResopnse datas = new Gson().fromJson(result, XuanPInResopnse.class);

                if (datas != null && datas.list != null && datas.list.size() > 0) {
                    btn_shop.setVisibility(View.VISIBLE);
                    shop_title_new.setText(datas.list.get(0).title);
                    firtShop = datas.list.get(0);
                    shop_price.setText(NumUtil.clearZero(datas.list.get(0).egg_price));
                    Glide.with(TCLivePlayerActivity.this).load(CommonUtils.getUrl(datas.list.get(0).pic)).into(shop_img);
                    gouwuche_big.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    XuanPInResopnse.ItemData firtShop;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mArrayListChatEntity == null || mArrayListChatEntity.size() < 0) {
            return;
        }
        if (mArrayListChatEntity.get(position).getType() == TCConstants.IMCMD_SYSTEM) {
            return;
        }
        LiveUserDialogFragment fragment = new LiveUserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", mArrayListChatEntity.get(position).getUser_id());
        bundle.putString("meUserId", mUserId);
        bundle.putString("liveUid", mPusherId);
        bundle.putString("liveID", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
    }

    @Override
    public void onItemClick(Integer bean, int position) {

        if (isHeart) {
            if (mHeartLayout != null) {
                mHeartLayout.addFavor();
            }
        } else {
            isHeart = true;
            //点赞发送请求限制
            if (mLikeFrequeControl == null) {
                mLikeFrequeControl = new TCFrequeControl();
                mLikeFrequeControl.init(2, 1);
            }
//            TCSimpleUserInfo userInfo = new TCSimpleUserInfo(mUserId, mNickname, mHeadPic);
//            handlePraiseMsg(userInfo, mAttributeInfo);
            if (mLikeFrequeControl.canTrigger()) {
                //点赞消息
                mLiveRoom.sendRoomMsg(TCConstants.IMCMD_PRAISE, "", null);
            }
        }
    }

    private LiveRoomViewHolder mLiveRoomViewHolder;

    private void showHeadIcon(ImageView view, String avatar) {
        if (!TextUtils.isEmpty(avatar))
            Glide.with(mContext)
                    .load(avatar)
                    .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveUserDialogFragment fragment = new LiveUserDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", mPusherId);
                bundle.putString("meUserId", mUserId);
                bundle.putString("liveUid", mPusherId);
                bundle.putString("liveID", mLiveID);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
            }
        });
    }

    protected void startPlay() {
        if (mPlaying) return;
        mLiveRoom.setListener(this);
        mLiveRoom.enterRoom(vod_type, mLiveID, mPlayUrl, mTXCloudVideoView, new IMLVBLiveRoomListener.EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                showErrorAndQuit(TCConstants.ERROR_4);
                //进入LiveRoom失败
            }

            @Override
            public void onSuccess() {
                //进入LiveRoom成功
//                if (!pureAudio){
//                    mBgImageView.setVisibility(View.GONE);
//                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

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
                                        if (TextUtils.isEmpty(text)) {
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

                        mLiveRoom.sendRoomMsg(TCConstants.IMCMD_ENTER_LIVE, "", new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                            }

                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject data = new JSONObject(result);
                                    NLog.e(TAG, result);
                                    String is_pk = data.getString("is_pk");
                                    if (is_pk.equals("1")) {
                                        rlPlayRoot.setBackgroundResource(R.mipmap.bg_live_pk);
                                        long self_get_coin = data.getJSONObject("pk_info").getLong("self_get_coin");
                                        long other_get_coin = data.getJSONObject("pk_info").getLong("other_get_coin");
                                        int pk_rest_time = data.getJSONObject("pk_info").getInt("pk_rest_time");
                                        int pk_punish_rest_time = data.getJSONObject("pk_info").getInt("pk_punish_rest_time");
                                        int pkTime = 0;
                                        int isPkEnd = 0;
                                        //isPkEnd 0PK状态 1惩罚状态
                                        if (pk_rest_time > 0) {
                                            pkTime = pk_rest_time;
                                            isPkEnd = 0;
                                        } else {
                                            pkTime = pk_punish_rest_time;
                                            isPkEnd = 1;
                                        }
                                        if (mLiveLinkMicPkPresenter != null) {
                                            mLiveLinkMicPkPresenter.onEnterRoomPkStart(self_get_coin, other_get_coin, pkTime, isPkEnd);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                                try {
                                    LiveViewerResponse liveViewerResponse = JsonMananger.jsonToBean(result, LiveViewerResponse.class);
                                    int size = liveViewerResponse.getList().size();
                                    if (mCurrentMemberCount < size) {
                                        mCurrentMemberCount = size - 1;
                                        mMemberCount.setText(String.format(Locale.CHINA, "%d", mCurrentMemberCount));
                                    }

                                    for (int i = 0; i < size; i++) {
                                        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(
                                                String.valueOf(liveViewerResponse.getList().get(i).getUser_id()),
                                                liveViewerResponse.getList().get(i).getUser_nickname(),
                                                CommonUtils.getUrl(liveViewerResponse.getList().get(i).getAvatar()));
                                        mAvatarListAdapter.addItem(userInfo);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });

                    }
                }, 1000);
            }
        });
        mPlaying = true;
    }

    private void stopPlay() {
        LoadDialog.show(this);
        if (mPlaying && mLiveRoom != null) {
            mLiveRoom.exitRoom(new IMLVBLiveRoomListener.ExitRoomCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
//                    NLog.e(TAG, "exit room error : "+errInfo);
                    LoadDialog.dismiss(TCLivePlayerActivity.this);
                    stopLinkMic();
                    mLiveID = null;
                    exit();
                }

                @Override
                public void onSuccess() {
//                    NLog.e(TAG, "exit room success ");
                    LoadDialog.dismiss(TCLivePlayerActivity.this);
                    stopLinkMic();
                    mLiveID = null;
                    exit();
                }
            });
            mPlaying = false;
            mLiveRoom.setListener(null);
        }
    }

    private void exit() {
        SocketUtil.getInstance().cancelSocketMsgListener();
        finish();
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    //TODO
    private void showErrorAndQuit(int errorCode) {
        //ERROR_1 = 1001;//观众拉流失败
        //ERROR_2 = 1002;//观众被T出直播间
        //ERROR_3 = 1003;//观众收到 主播关闭直播间
        //ERROR_4 = 1004;//观众 进入直播间失败
        switch (errorCode) {
            case TCConstants.ERROR_1:
//                NToast.shortToast(mContext, "网络异常，请检查设置");
                break;
            case TCConstants.ERROR_3:
                //图片提示
                mBgImageView.setVisibility(View.VISIBLE);
                stopLinkMic();
                break;
            case TCConstants.ERROR_2:
                NToast.shortToast(mContext, "不好意思，您被管理踢出直播间");
                showComfirmDialog();
                break;
            case TCConstants.ERROR_4:
                NToast.shortToast(mContext, "进入直播间失败");
                showComfirmDialog();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                showComfirmDialog();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_share:
                showShareDialog();
                break;
            case R.id.btn_gift:
                openGiftWindow();
                break;
            case R.id.btn_rotary:
                RotaryTableFragment fragmentRotary = new RotaryTableFragment();
                fragmentRotary.show(getSupportFragmentManager(), "RotaryTableFragment");
                break;
            case R.id.btn_shop:
//                String url = Const.getDomain() + "/apph5/shop/goodslist";
//                ShopDialogFragment fragment = new ShopDialogFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("url", url);
//                bundle.putString("USERID", mPusherId);
//                fragment.setArguments(bundle);
//                fragment.show(getSupportFragmentManager(), "ShopDialogFragment");
                if (fragment != null) {
                    fragment.setUserId(zhubo_user_id);
                    fragment.setOnclick(new ShopSelectDialog.Onclick() {
                        @Override
                        public void OnClick(XuanPInResopnse.ItemData itemData) {
                            fragment.dismiss();
                            Intent intent = new Intent(TCLivePlayerActivity.this, NewShopDetailActivity.class);
                            intent.putExtra("goods_id", itemData.goods_url);
                            startActivity(intent);
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "ShopSelectDialog");
                }
                break;
            case R.id.btn_beauty:
                if (mBeautyHepler.isAddedMain()) {
                    mBeautyHepler.dismissMain();
                } else {
                    mBeautyHepler.showMain(getFragmentManager(), "BeautyMainFragment");
                }
                break;
            case R.id.btn_switch_cam:
                if (mIsBeingLinkMic) {
                    mLiveRoom.switchCamera();
                }
                break;
            case R.id.btn_play_msg:
                openChatRoomWindow();
                break;
            case R.id.layout_live_pusher_info_follow:
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("be_user_id", mPusherId)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/follow/add", body, new com.yjfshop123.live.Interface.RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        mFollowTv.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.btn_patrol:
                SuperManagerDialogFragment smFragment = new SuperManagerDialogFragment();
                Bundle bundleSM = new Bundle();
                bundleSM.putString("mLiveId", mLiveID);
                smFragment.setArguments(bundleSM);
                smFragment.show(getSupportFragmentManager(), "SuperManagerDialogFragment");
                break;
            default:
                break;
        }
    }

    private void openChatRoomWindow() {
        String mi_tencentId = mUserId;
        String mi_platformId = mUserId;

        String prom_custom_uid = mPusherId;
        String user_id = mPusherId;

        if (mi_tencentId.equals(prom_custom_uid)) {
            //自己不能跟自己聊天
            NToast.shortToast(mContext, getString(R.string.not_me_chat));
            return;
        }

        if (prom_custom_uid.equals(user_id)) {
            //真人
        }

        IMConversation imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(mi_platformId);

        imConversation.setOtherPartyIMId(prom_custom_uid);
        imConversation.setOtherPartyId(user_id);

        imConversation.setUserName(mNickname);
        imConversation.setUserAvatar(mHeadPic);

        imConversation.setOtherPartyName(mPusherNickname);
        imConversation.setOtherPartyAvatar(mPusherAvatar);

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        Intent intent = new Intent(mContext, MessageListActivity.class);
        intent.putExtra("IMConversation", imConversation);
        startActivity(intent);
    }

    private void openGiftWindow() {
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", mPusherId);
        bundle.putString("isGuard", mISGuard);
        bundle.putString("live_id", mLiveID);
        bundle.putInt("vod_type", vod_type);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGiftDialogFragment");
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

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }

//        stopPlay();

        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.release();
        }

        mPlayerList.recycleVideoView();
        mPlayerList = null;
//        stopLinkMic();

//        TCLiveRoomMgr.getDestroyLiveRoom();
//        mLiveID = null;

        Foreground.get().setListen(null);
    }

    private void notifyMsg(final TCChatEntity entity) {
        if (mArrayListChatEntity.size() > 1000) {
            while (mArrayListChatEntity.size() > 900) {
                mArrayListChatEntity.remove(0);
            }
        }

        mArrayListChatEntity.add(entity);
        mChatMsgListAdapter.notifyDataSetChanged();
    }

    private void addOneself() {
        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(mUserId, mNickname, mHeadPic);
        mAvatarListAdapter.addItem(userInfo);

        //系统通知
        TCChatEntity entity_ = new TCChatEntity(TCConstants.IMCMD_SYSTEM);
        entity_.setContent(mJoinLiveResponse.getLive_warning().getContent());
        notifyMsg(entity_);

//        TCChatEntity entity = new TCChatEntity(userInfo.nickname, mAttributeInfo, TCConstants.IMCMD_ENTER_LIVE, userInfo.userid);
//        entity.setContent("加入直播");
//        notifyMsg(entity);

//        if (mLiveRoomViewHolder != null){
//            mLiveRoomViewHolder.onEnterRoom(userInfo, mAttributeInfo);
//        }
    }

    /**
     * TextInputDialog发送回调
     *
     * @param msg       文本信息
     * @param danmuOpen 是否打开弹幕
     */
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

//        TCChatEntity entity = new TCChatEntity(mNickname, mAttributeInfo, TCConstants.IMCMD_PAILN_TEXT, mUserId);
//        entity.setContent(msg);
//        notifyMsg(entity);

        if (danmuOpen) {
//            if (mLiveDanmuPresenter != null) {
//                mLiveDanmuPresenter.showDanmu(new DanmuBean(mUserId, mNickname, mHeadPic, msg));
//            }
            mLiveRoom.sendRoomMsg(TCConstants.IMCMD_DANMU, msg, null);
        } else {
            mLiveRoom.sendRoomMsg(TCConstants.IMCMD_PAILN_TEXT, msg, null);
        }
    }
    AlertDialog mDialog;
    private void showShareDialog() {
        View view = getLayoutInflater().inflate(R.layout.share_dialog2, null);
        mDialog = new AlertDialog.Builder(this, R.style.BottomDialog2).create();
        mDialog.show();

        Window window = mDialog.getWindow();
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
                NToast.shortToast(TCLivePlayerActivity.this, errInfo);
                LoadDialog.dismiss(TCLivePlayerActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(TCLivePlayerActivity.this);
                if (result == null) {
                    return;
                }
                try {
                    KoulingData urlData = new Gson().fromJson(result,KoulingData.class);
                    SystemUtils.setClipboardForKouling(TCLivePlayerActivity.this,urlData.text);
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

            String body = "";
            try {
                body = new JsonBuilder()
                        .put("id", mPusherId)
                        .put("live_id", mLiveID)
                        .put("vod_type", vod_type)
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
    };

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        NLog.d("result", "onActivityResult");
    }

    private BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi;
    private String proMeiFu;
    private String proMeiXing;
    private BeautyLvJingFragment.BeautyParams beautyParamsLvJing;

    private void joinPusher() {
        TCVideoWidget videoView = mPlayerList.getFirstRoomView();
        videoView.setUsed(true);
        videoView.userID = mUserId;
        if (pureAudio) {
            videoView.audioLive(mContext, mHeadPic);
        }

        mLiveRoom.startLocalPreview(true, videoView.videoView, mFURenderer);
        mLiveRoom.setCameraMuteImage(R.drawable.pause_publish);

        //###
        beautyParamsTieZhi = new BeautyTieZhiFragment.BeautyParams();
        String tiezhi_path = UserInfoUtil.getTiezhiPath();
        if (TextUtils.isEmpty(tiezhi_path)) {
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video_none", "video_none", "", "");
        } else {
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video", tiezhi_path, "", "");
        }
        String miefu = UserInfoUtil.getMeifu();
        if (TextUtils.isEmpty(miefu)) {
            proMeiFu = "0@@20@@20@@20@@20@@20";
        } else {
            proMeiFu = miefu;
        }
        String miexing = UserInfoUtil.getMeiXing();
        if (TextUtils.isEmpty(miexing)) {
            proMeiXing = "0@@30@@30@@30@@30@@30@@30@@30@@30@@30";
        } else {
            proMeiXing = miexing;
        }
        int filterProgress = UserInfoUtil.getFilterProgress();
        String filterStr = UserInfoUtil.getFilterStr();
        beautyParamsLvJing = new BeautyLvJingFragment.BeautyParams();
        beautyParamsLvJing.mFilterProgress = filterProgress;
        if (TextUtils.isEmpty(filterStr)) {
            beautyParamsLvJing.mFilter = Filter.Key.ORIGIN;
        } else {
            beautyParamsLvJing.mFilter = filterStr;
        }
        mBeautyHepler.init(beautyParamsTieZhi, proMeiFu, proMeiXing, beautyParamsLvJing);
        //###

        /*BeautyDialogFragment.BeautyParams params = mBeautyHepler.getParams();
        mLiveRoom.setBeautyStyle(params.mBeautyStyle,
                TCUtils.filtNumber(9, 100, params.mBeautyProgress),
                TCUtils.filtNumber(9, 100, params.mWhiteProgress),
                TCUtils.filtNumber(9, 100, params.mRuddyProgress));
        mLiveRoom.setFaceSlimLevel(TCUtils.filtNumber(9, 100, params.mFaceLiftProgress));
        mLiveRoom.setEyeScaleLevel(TCUtils.filtNumber(9, 100, params.mBigEyeProgress));*/

        mLiveRoom.joinAnchor(mSelfPushUrl, new IMLVBLiveRoomListener.JoinAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                stopLinkMic();
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess() {
                mIsBeingLinkMic = true;

                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("live_id", mLiveID)
                            .put("viewer_uid", mUserId)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/live/viewerJoinLianmaiSuccess", body, new com.yjfshop123.live.Interface.RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        stopLinkMic();
                        NToast.shortToast(mContext, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            //获取房间里所有正在推流的成员
                            JSONObject data = new JSONObject(result);
                            JSONArray jsa = data.getJSONArray("viewer_b_live_quick_list");

                            updateAnchors(jsa);

                            mBtnLinkMic.setEnabled(true);
                            mIsBeingLinkMic = true;
                            if (mBtnSwitchCamera != null && !pureAudio) {
                                mBtnSwitchCamera.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void startLinkMic() {
        if (mIsBeingLinkMic) {
            return;
        }

        mBtnLinkMic.setEnabled(false);
        mBtnLinkMic.setImageResource(R.mipmap.icon_live_link_mic_1);

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/applyLianmai", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mBtnLinkMic.setEnabled(true);
                NToast.shortToast(mContext, errInfo);
                mIsBeingLinkMic = false;
                mBtnLinkMic.setImageResource(R.mipmap.lianmai_new);
                if (mBtnBeauty != null) {
                    mBtnBeauty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "等待主播接受......");
                isPostDelayed = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPostDelayed) {
                            onXZBAccept("2", null, null);
                        }
                    }
                }, 10000);
            }
        });
    }

    private boolean isPostDelayed = false;

    private String mSelfPushUrl;

    @Override
    public void onXZBAccept(String type, String selfPushUrl, String accelerateURL) {
        NLog.e("连麦中", type + "  " + selfPushUrl + " " + accelerateURL);
        isPostDelayed = true;
        if (type.equals("1")) {
            mSelfPushUrl = selfPushUrl;
            mLiveRoom.startPlayAccelerateURL(accelerateURL);

            NToast.shortToast(mContext, "主播接受了您的连麦请求，开始连麦");
            if (TCUtils.checkRecordPermission(TCLivePlayerActivity.this)) {
                NLog.e("连麦中", "33");
                joinPusher();
            }

            if (mBtnBeauty != null && !pureAudio) {
                mBtnBeauty.setVisibility(View.VISIBLE);
            }
        } else {
            mBtnLinkMic.setEnabled(true);
            NToast.shortToast(mContext, "主播拒绝了你得连麦~");
            mIsBeingLinkMic = false;
            mBtnLinkMic.setImageResource(R.mipmap.lianmai_new);
            if (mBtnBeauty != null) {
                mBtnBeauty.setVisibility(View.GONE);
            }
        }
    }

    private void updateAnchors(JSONArray jsa) {
        if (jsa == null || jsa.length() == 0) {
            return;
        }
        NLog.e("所有推流", new Gson().toJson(jsa));
        try {
            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);
                String userID = jso.getJSONObject("user_info").getString("user_id");
                if (!userID.equalsIgnoreCase(mUserId)) {
                    String userName = jso.getJSONObject("user_info").getString("nickname");
                    String userAvatar = jso.getJSONObject("user_info").getString("avatar");
                    String rtmp = jso.getJSONObject("b_live_quick_url").getString("rtmp");
                    AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, rtmp);
                    onAnchorEnter_(anchorInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void stopLinkMic() {
        if (!mIsBeingLinkMic) return;

        mIsBeingLinkMic = false;

        //启用连麦Button
        if (mBtnLinkMic != null) {
            mBtnLinkMic.setEnabled(true);
            mBtnLinkMic.setImageResource(R.mipmap.lianmai_new);
        }

        //隐藏切换摄像头Button
        if (mBtnSwitchCamera != null) {
            mBtnSwitchCamera.setVisibility(View.GONE);
        }

        if (mBtnBeauty != null) {
            mBtnBeauty.setVisibility(View.GONE);
        }

        mLiveRoom.stopLocalPreview();
        mLiveRoom.quitJoinAnchor(mUserId);

        if (mPlayerList != null) {
            mPlayerList.recycleVideoView(mUserId);
            mPusherList.clear();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                joinPusher();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showComfirmDialog();
    }

    private void showComfirmDialog() {
        if (mIsBeingLinkMic) {
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "关闭连麦", "继续连麦",
                    "连麦中，关闭连麦？", true, true,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {

                        }

                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            stopLinkMic();
                        }
                    });
        } else {
            stopPlay();
        }

//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.clearData();
//        }
//        exit();

        /*DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                mPusherNickname + " " + msg, true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        stopPlay();

                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.clearData();
                        }

                        exit();
                    }
                });*/
    }

    public void addgz(String userid) {
        if (userid.equals(mPusherId)) {
            mFollowTv.setVisibility(View.GONE);
        }
    }

    public MLVBLiveRoom getmLiveRoom() {
        return mLiveRoom;
    }

    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", mPusherId);
        bundle.putInt("guardNum", mWatchNum);
        bundle.putString("isGuard", mISGuard);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    public void openBuyGuardWindow() {
        LiveGuardBuyDialogFragment fragment = new LiveGuardBuyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", mPusherId);
        bundle.putString("mLiveId", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardBuyDialogFragment");
    }

    public void changeTXCVV(boolean isD) {
        if (mTXCloudVideoView == null) {
            return;
        }

        if (isD) {
            rlPlayRoot.setBackgroundColor(Color.parseColor("#ff263862"));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mTXCloudVideoView.setLayoutParams(layoutParams);
        } else {
            int widthPixels = mWidthPixels / 2;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidthPixels, (int) (widthPixels * Const.ratio));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.topMargin = mTopMargin;
//            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            mTXCloudVideoView.setLayoutParams(layoutParams);
        }
    }

    public void getWallet() {
        Intent intent = new Intent(mContext, MyWalletActivity1.class);
        intent.putExtra("currentItem", 0);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    public void manager(UserInfo4LiveResponse response) {
        ManagerDialogFragment fragment = new ManagerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", String.valueOf(response.getUser_info().getUser_id()));//用户ID
        bundle.putString("meUserId", mUserId);//自己ID
        bundle.putString("liveUid", mPusherId);//主播ID
        bundle.putString("mLiveId", mLiveID);//直播间ID
        bundle.putString("is_banspeech", String.valueOf(response.getUser_info().getIs_banspeech()));//是否被禁言
        bundle.putString("is_manager", String.valueOf(response.getUser_info().getIs_manager()));
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ManagerDialogFragment");
    }

    public void contributeList() {
        ContributeListFragment fragment = new ContributeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("live_id", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ContributeListFragment");
    }

    @Override
    public void closeMain() {

    }

    @Override
    public void closeTieZhi(BeautyTieZhiFragment.BeautyParams params) {

    }

    @Override
    public void closeMeiYan(String proMeiFu, String proMeiXing) {

    }

    @Override
    public void closeLvJing(BeautyLvJingFragment.BeautyParams params) {

    }

    @Override
    public void onTrackingStatusChanged(int status) {
        //status <= 0  未检测到人脸
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
    public void closeLive() {
        showComfirmDialog();
    }

    @Override
    public void onActivityPaused() {
        if (mLiveRoom != null) {
            mLiveRoom.pausePush();
        }
    }

    @Override
    public void onActivityResumed() {
        if (mLiveRoom != null) {
            mLiveRoom.resumePush();
        }
    }

}
