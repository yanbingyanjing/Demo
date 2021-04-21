package com.yjfshop123.live.live.live.push.camera;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.IMLVBLiveRoomListener;
import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyLvJingFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyTieZhiFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.IBeauty;
import com.yjfshop123.live.live.live.common.widget.beautysetting.TCBeautyHelperNew;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.TCUserAvatarListAdapter;
import com.yjfshop123.live.live.live.common.widget.TCVideoWidget;
import com.yjfshop123.live.live.live.common.widget.TCVideoWidgetList;
import com.yjfshop123.live.live.live.common.widget.TCVideoWidgetPK;
import com.yjfshop123.live.live.live.common.widget.beautysetting.VideoMaterialMetaData;
import com.yjfshop123.live.live.live.common.widget.chat.LiveChatListDialogFragment;
import com.yjfshop123.live.live.live.common.widget.function.LiveFunctionClickListener;
import com.yjfshop123.live.live.live.common.widget.function.LiveFunctionDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.view.DrawableTextView;
import com.yjfshop123.live.live.live.common.widget.other.ContributeListFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveLinkMicListDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveUserDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ManagerDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ManagerListFragment;
import com.yjfshop123.live.live.live.common.widget.other.RotaryTableFragment;
import com.yjfshop123.live.live.live.common.widget.other.ShopDialogFragment;
import com.yjfshop123.live.live.live.common.widget.music.LiveMusicDialogFragment;
import com.yjfshop123.live.live.live.common.widget.music.SoundDialogFragment;
import com.yjfshop123.live.live.live.common.widget.pk.LiveLinkMicPkPresenter;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.LivingUserResponse;
import com.yjfshop123.live.live.response.MegUserInfo;
import com.yjfshop123.live.live.response.UserInfo4LiveResponse;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.TimeUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.faceunity.entity.Filter;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yjfshop123.live.xuanpin.view.ShopSelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TCLivePublisherActivity extends TCLiveBasePublisherActivity implements LiveFunctionClickListener, IBeauty {

    private TCBeautyHelperNew mBeautyHepler;

    //观众头像列表控件
    private RecyclerView mUserAvatarList;

    //主播信息
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;

    private boolean mFlashOn = false;

    //连麦主播
    private boolean mPendingRequest = false;
    private TCVideoWidgetList mPlayerList;
    private List<AnchorInfo> mPusherList = new ArrayList<>();
    private TCVideoWidget.OnRoomViewListener mListener = new TCVideoWidget.OnRoomViewListener() {
        @Override
        public void onKickUser(String userID) {
            if (userID != null) {
                for (AnchorInfo item : mPusherList) {
                    if (userID.equalsIgnoreCase(item.userID)) {
                        onAnchorExit(item);
                        break;
                    }
                }
                mLiveRoom.kickoutJoinAnchor(userID);
            }
        }
    };
//    private long mStartPushPts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TCUserMgr.getInstance().uploadLogs(TCConstants.ELK_ACTION_CAMERA_PUSH, TCUserMgr.getInstance().getUserId(), 0, "摄像头推流", null);
//        mStartPushPts = System.currentTimeMillis();
    }

    protected void initView() {
        setContentView(R.layout.activity_publish);
        super.initView();
        btn_shop = findViewById(R.id.btn_shop);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);

        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, zbUserId, zbUserId, mLiveID);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        TextView tv_broadcasting_name = findViewById(R.id.tv_broadcasting_name);
        tv_broadcasting_name.setText(TCUtils.getLimitString(zbName, 10));
        TextView tv_host_id = findViewById(R.id.tv_host_id);
        tv_host_id.setText("ID:" + zbUserId);
        ImageView headIcon = findViewById(R.id.iv_head_icon);
        showHeadIcon(headIcon, zbAvatar);
        mBgImageView = findViewById(R.id.background);
//        if (pureAudio){
//            mBgImageView.setVisibility(View.VISIBLE);
//            Glide.with(mContext)
//                    .load(zbAvatar)
//                    .into(mBgImageView);
//        }else {
//            mBgImageView.setVisibility(View.GONE);
//        }

        mMemberCount = findViewById(R.id.tv_member_counts);
        mMemberCount.setText("0");

        mBeautyHepler = new TCBeautyHelperNew(mFURenderer);
        mPlayerList = new TCVideoWidgetList(this, mListener);

        findViewById(R.id.btn_link_mic).setOnClickListener(this);
        mLinkMicIcon = findViewById(R.id.link_mic_icon);
        mLinkMicTip = findViewById(R.id.link_mic_tip);
        btnFunction = findViewById(R.id.btn_function);
        btnFunction.setOnClickListener(this);
        findViewById(R.id.btn_live_msg).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_message_input).setOnClickListener(this);
        findViewById(R.id.btn_shop).setOnClickListener(this);

        //pk
        ViewGroup mContainer3 = findViewById(R.id.container3);
        int widthPixels = mWidthPixels / 2;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidthPixels, (int) (widthPixels * Const.ratio));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.topMargin = mTopMargin;
        mContainer3.setLayoutParams(layoutParams);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mContainer3, true);
        loadShopData();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SocketUtil.getInstance().close();
//            }
//        },60000);

    }
    private Button btn_shop;
    private void loadShopData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
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
                }

            }
        });
    }

    private BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi;
    private String proMeiFu;
    private String proMeiXing;
    private BeautyLvJingFragment.BeautyParams beautyParamsLvJing;

    protected void startPublishImpl() {
        mTXCloudVideoView.setVisibility(View.VISIBLE);

        mLiveRoom.startLocalPreview(true, mTXCloudVideoView, mFURenderer);
//        mLiveRoom.setPauseImage(BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish));

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
            proMeiFu = "0@@100@@100@@50@@20@@100";
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

        /*BeautyDialogFragment.BeautyParams beautyParams = mBeautyHepler.getParams();
        mLiveRoom.setBeautyStyle(beautyParams.mBeautyStyle,
                TCUtils.filtNumber(9, 100, beautyParams.mBeautyProgress),
                TCUtils.filtNumber(9, 100, beautyParams.mWhiteProgress),
                TCUtils.filtNumber(9, 100, beautyParams.mRuddyProgress));
        mLiveRoom.setFaceSlimLevel(TCUtils.filtNumber(9, 100, beautyParams.mFaceLiftProgress));
        mLiveRoom.setEyeScaleLevel(TCUtils.filtNumber(9, 100, beautyParams.mBigEyeProgress));*/

        super.startPublishImpl();
    }

    protected void startPublish() {
        if (TCUtils.checkRecordPermission(this)) {
            startPublishImpl();
        }
    }

    protected void stopPublish() {
        NLog.d("断开了连麦","1");
        stopLinkMicPK();
        super.stopPublish();
    }

    protected void onCreateRoomSucess() {
        super.onCreateRoomSucess();
        mAvatarListAdapter.setLiveID(mLiveID);
        startRecordAnimation();
    }

    @Override
    public void onAnchorEnter(final AnchorInfo pusherInfo) {
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
                videoView.stopLoading(true); //推流成功，stopLoading 大主播显示出踢人的button
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

            }
        }); //开启远端视频渲染
    }

    @Override
    public void onAnchorExit(AnchorInfo pusherInfo) {
        onDoAnchorExit(pusherInfo);
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

    //####################################################################################################
    //                             主播收到观众连麦
    //####################################################################################################
    @Override
    public void onRequestJoinAnchor(final AnchorInfo pusherInfo) {
//
        final String user_id = pusherInfo.userID;
        //主播关闭了连麦
        //主播正在和他人pk连麦中
        //请稍后，主播正在处理其它人的连麦请求
        if (mIsBeingLinkMic || mPendingRequest) {
            responseJoinAnchor(user_id, false);
            return;
        }

        //主播端连麦人数超过最大限制
        if (mPusherList.size() >= 3) {
            responseJoinAnchor(user_id, false);
            return;
        }

        //已在连麦中
        for (AnchorInfo item : mPusherList) {
            if (user_id.equalsIgnoreCase(item.userID)) {
                responseJoinAnchor(user_id, false);
                break;
            }
        }

        final Dialog dialog = DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "接受", "拒绝",
                "观众 " + pusherInfo.userName + ": 向您发起连麦请求", true, false,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        //主播拒绝了您的连麦请求
                        responseJoinAnchor(user_id, false);
                        mPendingRequest = false;
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        //主播处理连麦请求
                        responseJoinAnchor(user_id, true);
                        dialog.dismiss();
                        mPendingRequest = false;
                    }
                });

        mPendingRequest = true;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                mPendingRequest = false;
            }
        }, 8000);
    }

    private void responseJoinAnchor(String user_id, boolean agree) {
        if (mLiveID == null) {
            return;
        }
        //type(1:同意 2:拒绝)
        String type;
        if (agree) {
            type = "1";
        } else {
            type = "2";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveID)
                    .put("viewer_uid", user_id)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/replyLianmai", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NLog.e("连麦中 主播0",errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NLog.e("连麦中 主播1",result);
            }
        });
    }
    //####################################################################################################
    //                             主播收到观众连麦
    //####################################################################################################

    /**
     * 加载主播头像
     *
     * @param view   view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(mContext)
                    .load(avatar)
                    .into(view);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.splash_logo)
                    .into(view);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveUserDialogFragment fragment = new LiveUserDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", zbUserId);
                bundle.putString("meUserId", zbUserId);
                bundle.putString("liveUid", zbUserId);
                bundle.putString("liveID", mLiveID);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        if (mArrayListChatEntity == null || mArrayListChatEntity.size() < 0) {
            return;
        }
        if (mArrayListChatEntity.get(position).getType() == TCConstants.IMCMD_SYSTEM) {
            return;
        }
        LiveUserDialogFragment fragment = new LiveUserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", mArrayListChatEntity.get(position).getUser_id());
        bundle.putString("meUserId", zbUserId);
        bundle.putString("liveUid", zbUserId);
        bundle.putString("liveID", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
    }

    private ObjectAnimator mObjAnim;

    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {
        ImageView iv = mLiveRoomViewHolder.getmRoomTimeIv();
        if (iv != null) {
            mObjAnim = ObjectAnimator.ofFloat(iv, "alpha", 1f, 0f, 1f);
            mObjAnim.setDuration(1000);
            mObjAnim.setRepeatCount(-1);
            mObjAnim.start();

            //直播时间
            if (mBroadcastTimer == null) {
                mBroadcastTimer = new Timer(true);
                mBroadcastTimerTask = new BroadcastTimerTask();
                mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
            }
        }
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {

        if (null != mObjAnim)
            mObjAnim.cancel();

        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

    @Override
    public void closeMain() {
        if (!isReady) {
            if (mLiveReadyViewHolder != null) {
                mLiveReadyViewHolder.show();
            }
        }
    }

    @Override
    public void closeTieZhi(BeautyTieZhiFragment.BeautyParams params) {
        if (!isReady) {
            if (mLiveReadyViewHolder != null) {
                mLiveReadyViewHolder.show();
            }
        }
    }

    @Override
    public void closeMeiYan(String proMeiFu, String proMeiXing) {
        if (!isReady) {
            if (mLiveReadyViewHolder != null) {
                mLiveReadyViewHolder.show();
            }
        }
    }

    @Override
    public void closeLvJing(BeautyLvJingFragment.BeautyParams params) {
        if (!isReady) {
            if (mLiveReadyViewHolder != null) {
                mLiveReadyViewHolder.show();
            }
        }
    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mTCSwipeAnimationController.isMoving()) {

                        if (mLiveRoomViewHolder != null) {
                            TextView tv = mLiveRoomViewHolder.getmRoomTimeTv();
                            if (tv != null) {
                                tv.setText(TCUtils.formattedTime(mSecond));
                            }
                            mLiveRoomViewHolder.updataRecording();
                        }

//                        if (mHeartLayout != null) {
//                            mHeartLayout.addFavor();
//                        }
                    }
                }
            });
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

        stopRecordAnimation();

        mPlayerList.recycleVideoView();
        mPlayerList = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        mLiveID = null;
        otherLiveID = null;
//        long endPushPts = System.currentTimeMillis();
//        long diff = (endPushPts - mStartPushPts) / 1000 ;
//        TCUserMgr.getInstance().uploadLogs(TCConstants.ELK_ACTION_CAMERA_PUSH_DURATION, TCUserMgr.getInstance().getUserId(), diff, "摄像头推流时长", null);
    }
    ShopSelectDialog fragment = new ShopSelectDialog();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                showComfirmDialog();
                break;
            case R.id.btn_shop:
//                String url = Const.getDomain() + "/apph5/shop/goodslist";
//                ShopDialogFragment fragment_ = new ShopDialogFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("url", url);
//                bundle.putString("USERID", zbUserId);
//                fragment_.setArguments(bundle);
//                fragment_.show(getSupportFragmentManager(), "ShopDialogFragment");
                if (fragment != null) {
                    fragment.setOnclick(new ShopSelectDialog.Onclick() {
                        @Override
                        public void OnClick(XuanPInResopnse.ItemData itemData) {
                            fragment.dismiss();
                            Intent intent = new Intent(TCLivePublisherActivity.this, NewShopDetailActivity.class);
                            intent.putExtra("goods_id", itemData.goods_url);
                            startActivity(intent);
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "ShopSelectDialog");
                }
                break;
            case R.id.btn_link_mic:
                int open_lianmai;
                if (mLinkMicEnable) {
                    open_lianmai = 1;
                } else {
                    open_lianmai = 0;
                }
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("open_lianmai", open_lianmai)//1：开放 0:不开放
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/setting/lianmaiSetting", body, new com.yjfshop123.live.Interface.RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        mLinkMicEnable = !mLinkMicEnable;
                        showLinkMicEnable();
                    }
                });
                break;
            case R.id.btn_function:
                btnFunction.setBackgroundResource(R.mipmap.icon_live_func_1);
                LiveFunctionDialogFragment fragment = new LiveFunctionDialogFragment();
                fragment.setFunctionClickListener(this);
                fragment.show(getSupportFragmentManager(), "LiveFunctionDialogFragment");
                break;
            case R.id.btn_live_msg:
                openChatListWindow();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void openChatListWindow() {
        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mLiveUid", zbUserId);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveChatListDialogFragment");
    }

    public void openChatRoomWindow(IMConversationDB mIMConversationDB) {
        Intent intent;
        IMConversation imConversation = new IMConversation();
        intent = new Intent(mContext, MessageListActivity.class);

        imConversation.setUserName(zbName);
        imConversation.setUserAvatar(zbAvatar);

        imConversation.setType(mIMConversationDB.getType());
        imConversation.setUserIMId(mIMConversationDB.getUserIMId());
        imConversation.setOtherPartyIMId(mIMConversationDB.getOtherPartyIMId());
        imConversation.setUserId(mIMConversationDB.getUserId());
        imConversation.setOtherPartyId(mIMConversationDB.getOtherPartyId());

        imConversation.setOtherPartyName(mIMConversationDB.getOtherPartyName());
        imConversation.setOtherPartyAvatar(mIMConversationDB.getOtherPartyAvatar());
        imConversation.setConversationId(mIMConversationDB.getConversationId());
        intent.putExtra("IMConversation", imConversation);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

        RealmConverUtils.clerRedCount_(mIMConversationDB.getConversationId());
    }

    private ImageView mLinkMicIcon;
    private TextView mLinkMicTip;
    private Button btnFunction;

    @Override
    public void showLinkMicEnable() {
        if (mLinkMicEnable) {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageResource(R.mipmap.icon_live_link_mic_1);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_5);
            }
        } else {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageResource(R.mipmap.icon_live_link_mic);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_4);
            }
        }
    }

    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case TCConstants.LIVE_FUNC_BEAUTY://美颜
                beauty();
                break;
            case TCConstants.LIVE_FUNC_CAMERA://切换镜头
                if (mLiveRoom != null) {
                    mLiveRoom.switchCamera();
                }
                break;
            case TCConstants.LIVE_FUNC_FLASH://切换闪光灯
                if (mLiveRoom == null || !mLiveRoom.enableTorch(!mFlashOn)) {
                    NToast.shortToast(mContext, "后置摄像头才可以打开闪光灯哦~");
                    return;
                }
                mFlashOn = !mFlashOn;
                break;
            case TCConstants.LIVE_FUNC_MUSIC://伴奏
                LiveMusicDialogFragment fragmentMusic = new LiveMusicDialogFragment();
                fragmentMusic.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
                break;
            case TCConstants.LIVE_FUNC_SHARE://分享
                showShareDialog();
                break;
            case TCConstants.LIVE_FUNC_GAME://游戏
                RotaryTableFragment fragment = new RotaryTableFragment();
                fragment.show(getSupportFragmentManager(), "RotaryTableFragment");
                break;
            case TCConstants.LIVE_FUNC_RED_PACK://红包
                NToast.shortToast(mContext, "红包");
                break;
            case TCConstants.LIVE_FUNC_LINK_MIC://主播连麦
                linkMicStyle = 1;
                startPKDialog();
                break;
            case TCConstants.LIVE_FUNC_LINK_PK://主播PK
                linkMicStyle = 2;
                startPKDialog();
                break;
            case TCConstants.LIVE_FUNC_LINK_RECOR://录制直播
                //TODO
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.startRecording();
                }
                break;
        }
    }

    public void onBGMStart_(final String filePath, final String musicName) {
        showMusic(musicName);
        mLiveRoom.playBGM(filePath);
    }

    @Override
    public void onBGMStart() {
//        NLog.e("TAGTAG", "onBGMStart");
    }

    @Override
    public void onBGMProgress(final long l, final long l1) {
//        NLog.e("TAGTAG", "onBGMProgress>" + l + ">>" + l1);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setLiveMusicTime(l, l1);
            }
        });
    }

    @Override
    public void onBGMComplete(int i) {
//        NLog.e("TAGTAG", "onBGMComplete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                closeMusic();
            }
        });
    }

    public void setBtnFunctionDark() {
        if (btnFunction != null) {
            btnFunction.setBackgroundResource(R.mipmap.icon_live_func_0);
        }
    }

    @Override
    protected void showErrorAndQuit(int errorCode) {
        super.showErrorAndQuit(errorCode);
    }

    @Override
    public void onEnterLive(MegUserInfo bean) {
        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(bean.getUser_id(), bean.getNickname(), bean.getAvatar());
        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;
        super.onEnterLive(bean);
        mMemberCount.setText(String.format(Locale.CHINA, "%d", lMemberCount));
    }

    @Override
    public void onExitLive(MegUserInfo bean) {
        mAvatarListAdapter.removeItem(bean.getUser_id());
        super.onExitLive(bean);
        mMemberCount.setText(String.format(Locale.CHINA, "%d", lMemberCount));
    }

    @Override
    public void triggerSearch(String query, Bundle appSearchData) {
        super.triggerSearch(query, appSearchData);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//是否选择，没选择就不会继续
            if (requestCode == REQUEST_CODE_SELECT) {
                if (data != null && pureAudio) {
//                    String path = data.getStringArrayListExtra("result").get(0);
//                    Glide.with(mContext)
//                            .load(path)
//                            .into(mBgImageView);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        showErrorAndQuit(TCConstants.ERROR_5);
                        return;
                    }
                }
                startPublishImpl();
                break;
            default:
                break;
        }
    }

    public void exit() {
        super.exit();
        SocketUtil.getInstance().cancelSocketMsgListener();
        finish();
    }

    public void beauty() {
        if (mBeautyHepler.isAddedMain()) {
            mBeautyHepler.dismissMain();
        } else {
            mBeautyHepler.showMain(getFragmentManager(), "BeautyMainFragment");
            if (!isReady) {
                if (mLiveReadyViewHolder != null) {
                    mLiveReadyViewHolder.hide();
                }
            }
        }
    }

    private int linkMicStyle = 1;//1 自由连麦 2PK
    private boolean mIsBeingLinkMic = false;//false空闲 true忙碌

    private void startPKDialog() {
        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveUid", zbUserId);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }

    //发起连麦或者PK申请
    public void linkMicAnchorApply(LivingUserResponse.LiveListBean bean) {
        if (mPusherList != null && mPusherList.size() > 0) {
            NToast.shortToast(mContext, "当前正在连麦，无法进行pk连麦");
            return;
        }
        if (!mIsBeingLinkMic) {
            switch (linkMicStyle) {
                case 1:
                    otherLiveID = bean.getLive_id();
                    linkMicPK();
                    break;
                case 2:
                    otherLiveID = bean.getLive_id();
                    applyPK();
                    break;
            }
        } else {
            NToast.shortToast(mContext, "(连麦或PK)中,请关闭(连麦或PK)后再试");
        }
    }

    //####################################################################################################
    //                             主播间自由连麦
    //####################################################################################################
    private boolean isPostDelayed = false;

    private void linkMicPK() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", mLiveID)
                    .put("accept_live_id", otherLiveID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/applyLianmai4Live", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                mIsBeingLinkMic = false;
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "等待主播接受……");
                isPostDelayed = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPostDelayed) {
                            onZBAccept("2", null);
                        }
                    }
                }, 10000);
            }
        });
    }

    @Override
    public void onZBAccept(String type, AnchorInfo anchorInfo) {
        isPostDelayed = true;
        if (type.equals("1")) {
            delayed();

            NToast.shortToast(mContext, "主播接受了您的连麦请求，开始连麦");
            mIsBeingLinkMic = true;
            mAnchorInfo = anchorInfo;
            //渲染UI
            isPKLaunch = true;
            startRemoteViewPK("", false);
        } else {
            NToast.shortToast(mContext, "主播拒绝了你得连麦~");
            mIsBeingLinkMic = false;
        }
    }

    private synchronized void stopLinkMicPK() {
        if (!mIsBeingLinkMic) {
            return;
        }
        stopRemoteViewPK();

        if (mAnchorInfo != null) {
            String launch_live_id;
            String accept_live_id;
            if (isPKLaunch) {
                launch_live_id = mLiveID;
                accept_live_id = otherLiveID;
            } else {
                launch_live_id = otherLiveID;
                accept_live_id = mLiveID;
            }
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("launch_live_id", launch_live_id)
                        .put("accept_live_id", accept_live_id)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/live/closeLianmai4Live", body, new com.yjfshop123.live.Interface.RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                }

                @Override
                public void onSuccess(String result) {
                }
            });
        }
    }

    @Override
    public void onRequestRoomAA(final AnchorInfo anchorInfo) {
        if (mPusherList != null && mPusherList.size() > 0) {
            //主播正在连麦，无法进行pk连麦
            responseRoomAA(false);
            return;
        }

        //主播关闭了连麦
        //请稍后，主播正在处理其它人的连麦请求
        //主播正在和他人pk连麦中
        if (mPendingRequest || mIsBeingLinkMic) {
            responseRoomAA(false);
            return;
        }

        final Dialog dialog = DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "接受", "拒绝",
                "主播 " + anchorInfo.userName + ": 向您发起连麦请求", true, false,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        //对方主播拒绝了您的连麦请求
                        responseRoomAA(false);
                        mPendingRequest = false;
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        delayed();

                        dialog.dismiss();

                        mPendingRequest = false;
                        mIsBeingLinkMic = true;
                        mAnchorInfo = anchorInfo;

                        //主播处理连麦请求
                        responseRoomAA(true);
                    }
                });

        mPendingRequest = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                mPendingRequest = false;
            }
        }, 8000);
    }

    private void responseRoomAA(boolean agree) {
        if (mLiveID == null) {
            return;
        }
        //type(1:同意 2:拒绝)
        String type;
        if (agree) {
            type = "1";
        } else {
            type = "2";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", otherLiveID)
                    .put("accept_live_id", mLiveID)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/replyLianmai4Live", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mIsBeingLinkMic = false;
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    mAnchorInfo.accelerateURL = data.getJSONObject("launch_b_live_quick_url").getString("rtmp");
                    isPKLaunch = false;
                    startRemoteViewPK("", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onQuitRoomAA() {
        //收到对方退出跨房连麦
        stopRemoteViewPK();
    }
    //####################################################################################################
    //                             主播间自由连麦
    //####################################################################################################


    //###############################################################################
    //                                  PK
    //###############################################################################
    private void applyPK() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", mLiveID)
                    .put("accept_live_id", otherLiveID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/applyPK", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, "主播拒绝了您的PK");
                mIsBeingLinkMic = false;
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "等待主播接受PK……");
                isPostDelayed = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPostDelayed) {
                            onZBAccept("2", null);
                        }
                    }
                }, 10000);
            }
        });
    }

    @Override
    public void onPKLiveMsg(String type, AnchorInfo anchorInfo, String pkRestTime) {
        isPostDelayed = true;
        if (type.equals("1")) {
            delayed();

            NToast.shortToast(mContext, "开始PK");
            mIsBeingLinkMic = true;
            mAnchorInfo = anchorInfo;
            isPKLaunch = true;
            startRemoteViewPK("", true);
            rlRoot.setBackgroundResource(R.mipmap.bg_live_pk);

            if (mLiveLinkMicPkPresenter != null) {
                int pkTime = 0;
                if (!TextUtils.isEmpty(pkRestTime)) {
                    pkTime = Integer.parseInt(pkRestTime);
                }
                mLiveLinkMicPkPresenter.anchorStartPK(otherLiveID, isPKLaunch, pkTime);
            }
        } else if (type.equals("10")) {
            //渲染UI
            mIsBeingLinkMic = true;
            mAnchorInfo = anchorInfo;
            startRemoteViewPK("", true);
            rlRoot.setBackgroundResource(R.mipmap.bg_live_pk);

            if (mLiveLinkMicPkPresenter != null) {
                int pkTime = 0;
                if (!TextUtils.isEmpty(pkRestTime)) {
                    pkTime = Integer.parseInt(pkRestTime);
                }
                mLiveLinkMicPkPresenter.anchorStartPK(otherLiveID, isPKLaunch, pkTime);
            }
        } else {
            NToast.shortToast(mContext, "主播拒绝了您的PK");
            mIsBeingLinkMic = false;
        }
    }

    @Override
    public void onRequestRoomPK(final AnchorInfo anchorInfo) {
        if (mPusherList != null && mPusherList.size() > 0) {
            //主播正在连麦，无法进行pk连麦
            responseRoomPK(false);
            return;
        }

        //主播关闭了连麦
        //请稍后，主播正在处理其它人的连麦请求
        //主播正在和他人pk连麦中
        if (mPendingRequest || mIsBeingLinkMic) {
            responseRoomPK(false);
            return;
        }

        final Dialog dialog = DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "接受", "拒绝",
                "主播 " + anchorInfo.userName + " 向您发起PK请求", true, false,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        responseRoomPK(false);
                        mPendingRequest = false;
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        delayed();

                        dialog.dismiss();
                        mPendingRequest = false;
                        isPKLaunch = false;

                        mIsBeingLinkMic = true;

                        mAnchorInfo = anchorInfo;
                        responseRoomPK(true);
                    }
                });

        mPendingRequest = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                mPendingRequest = false;
            }
        }, 8000);
    }

    private void responseRoomPK(boolean agree) {
        if (mLiveID == null) {
            return;
        }
        //type(1:同意 2:拒绝)
        String type;
        if (agree) {
            type = "1";
        } else {
            type = "2";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("launch_live_id", otherLiveID)
                    .put("accept_live_id", mLiveID)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/replyPK", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mIsBeingLinkMic = false;
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
            }
        });
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
        NLog.d("断开了连麦","2");
        stopLinkMicPK();
    }
    //###############################################################################
    //                                  PK
    //###############################################################################

    public void stopRemoteViewPK() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsBeingLinkMic = false;
                if (mAnchorInfo != null) {
                    mLiveRoom.stopRemoteView(mAnchorInfo);
                }
                if (tcVideoWidgetPK != null) {
                    tcVideoWidgetPK.close();
                }
                if (pureAudio) {
                    mLiveRoom.changeOneself(true, 0, 0, mBgImageView);
                } else {
                    mLiveRoom.changeOneself(true, 0, 0, null);
                }
                if (mLiveLinkMicPkPresenter != null) {
                    mLiveLinkMicPkPresenter.onLinkMicPkClose_();
                }
                if (isPKLaunch) {
                    isPKLaunch = false;
                }
                rlRoot.setBackgroundColor(Color.parseColor("#ff263862"));
            }
        });
    }

    //否是否为P连麦 发起者
    private TCVideoWidgetPK tcVideoWidgetPK;
    private AnchorInfo mAnchorInfo;

    private void startRemoteViewPK(final String avatar, final boolean isPK) {
        int widthPixels = mWidthPixels / 2;
        if (pureAudio) {
            mLiveRoom.changeOneself(false, widthPixels, mTopMargin, mBgImageView);
        } else {
            mLiveRoom.changeOneself(false, widthPixels, mTopMargin, null);
        }

        tcVideoWidgetPK = new TCVideoWidgetPK(this, widthPixels, mTopMargin, isPK, new TCVideoWidgetPK.OnRoomViewListener() {
            @Override
            public boolean onKickUser() {
                if (ISDELAYED) {
                    NToast.shortToast(mContext, "请稍候");
                    return true;
                }
                NLog.d("断开了连麦","2");
                stopLinkMicPK();
                return false;
            }
        });
        tcVideoWidgetPK.startLoading();

        mLiveRoom.startRemoteView(mAnchorInfo, tcVideoWidgetPK.getVideoView(), new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                tcVideoWidgetPK.stopLoading();
                if (pureAudio) {
                    tcVideoWidgetPK.audioLive(mContext, CommonUtils.getUrl(avatar));
                }
            }

            @Override
            public void onError(int errCode, String errInfo) {
                NLog.d("断开了连麦 mLiveRoom.startRemoteView",errInfo);
                NLog.d("断开了连麦","4");
                //TODO 此处强制不断开连麦
               // stopLinkMicPK();
                if (mAnchorInfo != null) {
                    mLiveRoom.stopRemoteView(mAnchorInfo);
                }
                startRemoteViewPK(avatar,isPK);
            }

            @Override
            public void onEvent(int event, Bundle param) {

            }
        });
    }

    public void manager(UserInfo4LiveResponse response) {
        ManagerDialogFragment fragment = new ManagerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", String.valueOf(response.getUser_info().getUser_id()));//用户ID
        bundle.putString("meUserId", zbUserId);//自己ID
        bundle.putString("liveUid", zbUserId);//主播ID
        bundle.putString("mLiveId", mLiveID);//直播间ID
        bundle.putString("is_banspeech", String.valueOf(response.getUser_info().getIs_banspeech()));//是否被禁言
        bundle.putString("is_manager", String.valueOf(response.getUser_info().getIs_manager()));
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ManagerDialogFragment");
    }

    public void managerList() {
        ManagerListFragment fragment = new ManagerListFragment();
        fragment.show(getSupportFragmentManager(), "ManagerListFragment");
    }

    public void contributeList() {
        ContributeListFragment fragment = new ContributeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("live_id", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ContributeListFragment");
    }

    //######################################################music
    //######################################################music
    private FrameLayout mLiveMusicFl;
    private DrawableTextView mLiveMusicNmae;
    private TextView mLiveMusicTime, mLiveRoomMusicClose, mLiveRoomMusicSound;
    private boolean isStop = false;

    public void closeMusic() {
        mLiveRoom.stopBGM();
        if (mLiveMusicFl != null) {
            mLiveMusicFl.setVisibility(View.GONE);
        }
        isStop = false;
    }

    private void showMusic(String musicName) {
        if (mLiveMusicFl == null) {
            mLiveMusicFl = (FrameLayout) findViewById(R.id.live_room_music_fl);
        }
        mLiveMusicFl.setVisibility(View.VISIBLE);

        isStop = false;
        if (mLiveMusicNmae == null) {
            mLiveMusicNmae = (DrawableTextView) findViewById(R.id.live_room_music_name);
        }
        mLiveMusicNmae.setText(musicName);
        mLiveMusicNmae.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_start));
        mLiveMusicNmae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStop) {
                    isStop = false;
                    mLiveRoom.resumeBGM();
                    mLiveMusicNmae.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_start));
                } else {
                    isStop = true;
                    mLiveRoom.pauseBGM();
                    mLiveMusicNmae.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_stop));
                }
            }
        });

        if (mLiveMusicTime == null) {
            mLiveMusicTime = (TextView) findViewById(R.id.live_room_music_time);
        }

        if (mLiveRoomMusicClose == null) {
            mLiveRoomMusicClose = (TextView) findViewById(R.id.live_room_music_close);
        }
        mLiveRoomMusicClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMusic();
            }
        });

        if (mLiveRoomMusicSound == null) {
            mLiveRoomMusicSound = (TextView) findViewById(R.id.live_room_music_sound);
        }
        mLiveRoomMusicSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundDialogFragment fragment = new SoundDialogFragment();
                fragment.show(getSupportFragmentManager(), "SoundDialogFragment");
            }
        });
    }

    private void setLiveMusicTime(long l, long l1) {
        if ((l / 1000) == (l1 / 1000)) {
            closeMusic();
            return;
        }
        if (mLiveMusicTime != null) {
            mLiveMusicTime.setText(TimeUtil.format_((int) l) + "/" + TimeUtil.format_((int) l1));
        }
    }
    //######################################################music
    //######################################################music

    private void delayed() {
        ISDELAYED = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ISDELAYED = false;
            }
        }, 8000);
    }
}
