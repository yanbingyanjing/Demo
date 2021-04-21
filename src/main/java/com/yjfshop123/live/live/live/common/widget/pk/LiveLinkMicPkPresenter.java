package com.yjfshop123.live.live.live.common.widget.pk;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.MLVBLiveRoom;
import com.yjfshop123.live.live.live.common.widget.gift.utils.StringUtil;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;


/**
 *
 * 主播与主播PK逻辑
 */
public class LiveLinkMicPkPresenter /*implements View.OnClickListener */{

//    private static final int WHAT_PK_WAIT_RECEIVE = 0;//收到pk申请等待 what
//    private static final int WHAT_PK_WAIT_SEND = 1;//发送pk申请等待 what
    private static final int WHAT_PK_TIME = 2;//pk时间变化 what
//    private static final int LINK_MIC_COUNT_MAX = 10;
//    private static final int LINK_MIC_COUNT_MAX_2 = 8;

//    private static final int PK_TIME_MAX = 1000 * 60 * 5;//pk时间 5分钟
//    private static final int PK_TIME_MAX_2 = 1000 * 60;//惩罚时间 1分钟

    private Context mContext;
//    private View mRoot;
    private boolean mIsAnchor;//自己是否是主播
//    private boolean mIsApplyDialogShow;//是否显示了申请PK的弹窗
//    private boolean mAcceptPk;//是否接受连麦
    private boolean mIsPk;//是否已经Pk了
//    private String mApplyUid;//对手Pk的主播的uid
//    private String mLiveUid;//自己主播的uid
    private String mLiveID;//对方主播直播间ID
//    private ProgressTextView mLinkMicWaitProgress;
    private int mPkWaitCount;//接收Pk弹窗等待倒计时Live
    private int mPkTimeCount;//pk时间
    private PopupWindow mPkPopWindow;
    private Handler mHandler;
    private LiveLinkMicPkViewHolder mLiveLinkMicPkViewHolder;
    private String mPkTimeString1;
    private String mPkTimeString2;
    private boolean mIsPkEnd;//pk是否结束，进入惩罚时间
//    private boolean mPkSend;//pk请求是否已经发送
//    private int mPkSendWaitCount;//发送pk请求后的等待时间
    private ViewGroup mPkContainer;
    private MLVBLiveRoom mLiveRoom;
//    private boolean isSendWait = false;

    public LiveLinkMicPkPresenter(Context context, ViewGroup parentView, boolean isAnchor/*, View root*/) {
        mContext = context;
        mPkContainer = parentView;
        mIsAnchor = isAnchor;
//        mRoot = root;
        mPkTimeString1 = mContext.getString(R.string.live_pk_time_1);
        mPkTimeString2 = mContext.getString(R.string.live_pk_time_2);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
//                    case WHAT_PK_WAIT_RECEIVE:
//                        onApplyPkWait();
//                        break;
//                    case WHAT_PK_WAIT_SEND:
//                        onSendPkWait();
//                        break;
                    case WHAT_PK_TIME:
                        changePkTime();
                        break;
                }
            }
        };

        if (mContext instanceof TCLiveBasePublisherActivity){
            mLiveRoom = ((TCLiveBasePublisherActivity)mContext).getmLiveRoom();
        }
    }

//    public void setLiveUid(String liveUid) {
//        mLiveUid = liveUid;
//    }

    /**
     * 申请pk弹窗倒计时
     */
//    private void onApplyPkWait() {
//        mPkWaitCount--;
//        if (mPkWaitCount >= 0) {
//            if (mLinkMicWaitProgress != null) {
//                mLinkMicWaitProgress.setProgress(mPkWaitCount);
//                if (mHandler != null) {
//                    long now = SystemClock.uptimeMillis();
//                    long next = now + (1000 - now % 1000);
//                    mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_RECEIVE, next);
//                }
//            }
//        } else {
//            if (mPkPopWindow != null) {
//                mPkPopWindow.dismiss();
//            }
//        }
//    }

    /**
     * 发送pk申请后等待倒计时
     */
//    private void onSendPkWait() {
//        mPkSendWaitCount--;
//        if (mPkSendWaitCount >= 0) {
//            nextSendPkWaitCountDown();
//        } else {
//            hideSendPkWait();
//            if (mIsAnchor) {
//                ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(true);
//            }
//        }
//    }

    /**
     * 进入下一次pk申请等待倒计时
     */
//    private void nextSendPkWaitCountDown() {
//        if (mLiveLinkMicPkViewHolder != null) {
//            mLiveLinkMicPkViewHolder.setPkWaitProgress(mPkSendWaitCount);
//        }
//        if (mHandler != null) {
//            long now = SystemClock.uptimeMillis();
//            long next = now + (1000 - now % 1000);
//            mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_SEND, next);
//        }
//    }

    /**
     * 隐藏pk申请等待
     */
//    private void hideSendPkWait() {
//        mPkSend = false;
//        if (mHandler != null) {
//            mHandler.removeMessages(WHAT_PK_WAIT_SEND);
//        }
//        if (mLiveLinkMicPkViewHolder != null) {
//            isSendWait = false;
//            mLiveLinkMicPkViewHolder.setPkWaitProgressVisible(false);
//        }
//    }

//    public boolean getSendWait(){
//        return isSendWait;
//    }

    /**
     * 进入下一次pk倒计时
     */
    private void nextPkTimeCountDown() {
        if (mHandler != null) {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mHandler.sendEmptyMessageAtTime(WHAT_PK_TIME, next);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            String s = mIsPkEnd ? mPkTimeString2 : mPkTimeString1;
            mLiveLinkMicPkViewHolder.setTime(s + " " + StringUtil.getDurationText(mPkTimeCount));
        }
    }

    /**
     * pk时间倒计时
     */
    private void changePkTime() {
        mPkTimeCount -= 1000;
        if (mPkTimeCount > 0) {
            nextPkTimeCountDown();
        } else {
            if (mIsPkEnd) {
                onLinkMicPkClose();
                if (mIsAnchor) {
//                    ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(true);
                }
            }else {
                onLinkMicPkEnd();
            }
        }
    }

    /**
     * TODO
     * TODO
     * TODO
     * 主播开始PK
     */
    public void anchorStartPK(String live_id, boolean isLaunch, int pkTime){
        mLiveID = live_id;
        isPK_PKLaunch = isLaunch;
        mIsPk = true;
        mPkTimeCount = pkTime * 1000;
        onLinkMicPkStart();
    }

    //连麦后 PK发起者
    private boolean isPK_PKLaunch = false;
//    protected Handler mHandler_ = new Handler();

    /**
     * 发起主播PK申请——主播调用
     */
//    public void applyLinkMicPk(String pkUid) {
//        if (mPkSend) {
//            //您已申请，请稍等
//            NToast.shortToast(mContext, R.string.link_mic_apply_waiting);
//            return;
//        }
//        if (mIsPk) {
//            //你已经在PK中
//            NToast.shortToast(mContext, R.string.live_link_mic_cannot_pk);
//            return;
//        }
//        mPkSend = true;
//
//        if (mLiveRoom != null){
//            mLiveRoom.requestRoomPK(pkUid, true, new IMLVBLiveRoomListener.RequestRoomPKCallback() {
//
//                @Override
//                public void onAccept(AnchorInfo anchorInfo) {
//                    mIsPk = true;
//                    isPK_PKLaunch = true;
//                    onLinkMicPkStart();
//                    mApplyUid = anchorInfo.userID;
//
//                    int isPkEnd = 0;
//                    if (mIsPkEnd){
//                        isPkEnd = 1;
//                    }
//                    GiftRatio giftRatio = new GiftRatio();
//                    giftRatio.setLeftGift(mLeftGift);
//                    giftRatio.setRightGift(mRightGift);
//                    giftRatio.setPkTime(PK_TIME_MAX);
//                    giftRatio.setIsPkEnd(isPkEnd);
//                    ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
//
//                    mHandler_.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            //上报开始pk——发起者调用
//                            mLiveRoom.startPK(mLiveUid, mApplyUid, new IMLVBLiveRoomListener.RequestCallback() {
//                                @Override
//                                public void onError(int errCode, String errInfo) {
//                                    NLog.e("TAGTAG>startPK", errInfo);
//                                }
//
//                                @Override
//                                public void onSuccess(String result) {
//                                    NLog.e("TAGTAG>startPK", result);
//                                }
//                            });
//                        }
//                    });
//                }
//
//                @Override
//                public void onReject(String reason) {
//                    onLinkMicPkRefuse();
//                    NToast.shortToast(mContext, reason);
//                }
//
//                @Override
//                public void onTimeOut() {
//                    onLinkMicPkRefuse();
//                    NToast.shortToast(mContext, R.string.link_mic_anchor_not_response_2);
//                }
//
//                @Override
//                public void onError(int errCode, String errInfo) {
//                    onLinkMicPkRefuse();
//                    NToast.shortToast(mContext, "连麦请求发生错误，" + errInfo);
//                }
//            });
//        }
//        NToast.shortToast(mContext, R.string.link_mic_apply_pk);
//
//        if (mLiveLinkMicPkViewHolder == null) {
//            mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
//            mLiveLinkMicPkViewHolder.addToParent();
//        }
//        isSendWait = true;
//        mLiveLinkMicPkViewHolder.setPkWaitProgressVisible(true);
//        mPkSendWaitCount = LINK_MIC_COUNT_MAX;
//        nextSendPkWaitCountDown();
//        if (mIsAnchor) {
//            ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(false);
//        }
//    }

//    private void onLinkMicPkRefuse() {
//        hideSendPkWait();
//        if (mIsAnchor) {
//            ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(true);
//        }
//    }

    /**
     * 主播与主播PK  主播收到其他主播发过来的PK申请的回调——主播调用
     */
//    public void onLinkMicPkApply(AnchorInfo anchorInfo, int isBeingLinkMic) {
//        if (mLiveRoom == null){
//            return;
//        }
//        if (isBeingLinkMic != 2){
//            mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "请先连麦");
//            return;
//        }
//        if (!mIsAnchor) {
//            mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "非法错误");
//            return;
//        }
//        if (anchorInfo == null) {
//            mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "非法错误");
//            return;
//        }
//        if (!TextUtils.isEmpty(mApplyUid) && mApplyUid.equals(anchorInfo.userID)) {
//            return;
//        }
//        if (!mIsPk && !mIsApplyDialogShow) {
//            mApplyUid = anchorInfo.userID;
//            showApplyDialog(anchorInfo);
//        } else {
//            mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "主播正忙，请稍候……");
//        }
//    }

    /**
     * 显示申请PK的弹窗——主播调用
     */
//    private void showApplyDialog(final AnchorInfo anchorInfo) {
//        mIsApplyDialogShow = true;
//        mAcceptPk = false;
//        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_link_mic_pk_wait, null);
//        mLinkMicWaitProgress = v.findViewById(R.id.pk_wait_progress);
//        v.findViewById(R.id.btn_refuse).setOnClickListener(this);
//        v.findViewById(R.id.btn_accept).setOnClickListener(this);
//        mPkWaitCount = LINK_MIC_COUNT_MAX_2;
//        mPkPopWindow = new PopupWindow(v, CommonUtils.dip2px(mContext, 280), ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        mPkPopWindow.setBackgroundDrawable(new ColorDrawable());
//        mPkPopWindow.setOutsideTouchable(true);
//        mPkPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                if (mHandler != null) {
//                    mHandler.removeMessages(WHAT_PK_WAIT_RECEIVE);
//                }
//                if (mAcceptPk) {
//                    mLiveRoom.responseRoomPK(anchorInfo.userID, true, true, "");
//                    mIsPk = true;
//                    onLinkMicPkStart();
//
//                    int isPkEnd = 0;
//                    if (mIsPkEnd){
//                        isPkEnd = 1;
//                    }
//                    GiftRatio giftRatio = new GiftRatio();
//                    giftRatio.setLeftGift(mLeftGift);
//                    giftRatio.setRightGift(mRightGift);
//                    giftRatio.setPkTime(PK_TIME_MAX);
//                    giftRatio.setIsPkEnd(isPkEnd);
//                    ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
//                } else {
//                    if (mPkWaitCount < 0) {
//                        mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "对方主播正忙……");
//                    } else {
//                        mLiveRoom.responseRoomPK(anchorInfo.userID, true, false, "对方主播拒绝了您的PK请求");
//                    }
//                    mApplyUid = null;
//                }
//                mIsApplyDialogShow = false;
//                mLinkMicWaitProgress = null;
//                mPkPopWindow = null;
//            }
//        });
//        mPkPopWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
//        if (mHandler != null) {
//            long now = SystemClock.uptimeMillis();
//            long next = now + (1000 - now % 1000);
//            mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_RECEIVE, next);
//        }
//    }

//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.btn_refuse) {
//            refuseLinkMic();
//
//        } else if (i == R.id.btn_accept) {
//            acceptLinkMic();
//
//        }
//    }

    /**
     * 拒绝PK
     */
//    private void refuseLinkMic() {
//        if (mPkPopWindow != null) {
//            mPkPopWindow.dismiss();
//        }
//    }

    /**
     * 接受PK
     */
//    private void acceptLinkMic() {
//        mAcceptPk = true;
//        if (mPkPopWindow != null) {
//            mPkPopWindow.dismiss();
//        }
//    }

    /**
     * 主播收到礼物
     *
     * @param jb
     */
//    public void updateJB(String jb) {
//        if (!mIsPk){//非PK状态不做处理
//            return;
//        }
//        if (mIsPkEnd){//惩罚状态不做处理
//            return;
//        }
//        if (jb == null) {
//            return;
//        }
//        try {
//            int jb_i = Integer.parseInt(jb);
//            mLeftGift = mLeftGift + jb_i;
//
//            if (mLiveLinkMicPkViewHolder != null) {
//                mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
//            }
//
//            //触发1v1主播消息
//            if (mLiveRoom != null && mApplyUid != null){
//                mLiveRoom.noticeGiftCount(mApplyUid, mLeftGift);
//            }
//
//            int isPkEnd = 0;
//            if (mIsPkEnd){
//                isPkEnd = 1;
//            }
//            GiftRatio giftRatio = new GiftRatio();
//            giftRatio.setLeftGift(mLeftGift);
//            giftRatio.setRightGift(mRightGift);
//            giftRatio.setPkTime(mPkTimeCount);
//            giftRatio.setIsPkEnd(isPkEnd);
//            ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private long mLeftGift = 0;
    private long mRightGift = 0;

    /**
     * TODO
     * TODO
     * TODO
     * 更新PK金蛋数
     *
     * @param rightGift
     */
    public void onEnterRoomPkStart(long leftGift, long rightGift) {
        mLeftGift = leftGift;
        mRightGift = rightGift;
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
        }

//        int isPkEnd = 0;
//        if (mIsPkEnd){
//            isPkEnd = 1;
//        }
//        GiftRatio giftRatio = new GiftRatio();
//        giftRatio.setLeftGift(mLeftGift);
//        giftRatio.setRightGift(mRightGift);
//        giftRatio.setPkTime(mPkTimeCount);
//        giftRatio.setIsPkEnd(isPkEnd);
//        ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
    }

    /**
     * 有新用户加入直播间 -主播调用
     */
//    public void joinStudio(){
//        if (!mIsPk){//非PK状态不做处理
//            return;
//        }
//
//        int isPkEnd = 0;
//        if (mIsPkEnd){
//            isPkEnd = 1;
//        }
//        GiftRatio giftRatio = new GiftRatio();
//        giftRatio.setLeftGift(mLeftGift);
//        giftRatio.setRightGift(mRightGift);
//        giftRatio.setPkTime(mPkTimeCount);
//        giftRatio.setIsPkEnd(isPkEnd);
//        ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
//    }

    /**
     *观众调用
     *
     * //TODO
     * //TODO
     * //TODO
     * @param leftGift 自己方礼物金蛋
     * @param rightGift 对手方礼物金蛋
     * @param pkTime 剩余时间 isPkEnd:0 PK剩余时间  isPkEnd:1 惩罚剩余时间
     * @param isPkEnd 0PK状态 1惩罚状态 2结束PK状态
     */
    public void onEnterRoomPkStart(long leftGift, long rightGift, int pkTime, int isPkEnd) {
        if (isPkEnd == 2){//PK 惩罚 结束了
            onLinkMicPkClose();
            return;
        }

        mLeftGift = leftGift;
        mRightGift = rightGift;

        if (mIsPk){//已经在PK更新 leftGift rightGift
            if (mLiveLinkMicPkViewHolder != null) {
                mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
            }
        }else {
            //未PK情况
            if (isPkEnd == 0){//PK
                mIsPk = true;
                mPkTimeCount = pkTime * 1000;
                onLinkMicPkStart();
                if (mLiveLinkMicPkViewHolder != null) {
                    mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
                }
//                if (pkTime >= PK_TIME_MAX){//老观众收到PK 开始PK
//                    onLinkMicPkStart();
//                }else {//新加入观众收到PK
//                    mIsPk = true;
//                    mIsPkEnd = false;
//                    if (mLiveLinkMicPkViewHolder == null) {
//                        mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
//                        mLiveLinkMicPkViewHolder.addToParent();
//                    }
//                    mLiveLinkMicPkViewHolder.showTime();
//                    mLiveLinkMicPkViewHolder.onEnterRoomPkStart();
//                    mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
//                    mPkTimeCount = pkTime;
//                    nextPkTimeCountDown();
//                }
            }else if (isPkEnd == 1) {//新加入用户 收到PK惩罚
                mIsPk = true;
                mIsPkEnd = true;
                if (mLiveLinkMicPkViewHolder == null) {
                    mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
                    mLiveLinkMicPkViewHolder.addToParent();
                }
                mLiveLinkMicPkViewHolder.showTime();
                mLiveLinkMicPkViewHolder.onEnterRoomPkStart();
                mLiveLinkMicPkViewHolder.onProgressChanged(mLeftGift, mRightGift);
                mPkTimeCount = pkTime * 1000;
                nextPkTimeCountDown();

                if (mLeftGift == mRightGift){
                    mLiveLinkMicPkViewHolder.end(0);
                }else if (mLeftGift > mRightGift){
                    mLiveLinkMicPkViewHolder.end(1);
                }else {
                    mLiveLinkMicPkViewHolder.end(-1);
                }
            }

            if(mContext instanceof TCLivePlayerActivity){
                ((TCLivePlayerActivity) mContext).changeTXCVV(false);
            }
        }
    }

    /**
     * 主播与主播PK 所有人收到PK开始的回调
     */
    private void onLinkMicPkStart() {
        mIsPk = true;
//        hideSendPkWait();
        mIsPkEnd = false;
        if (mLiveLinkMicPkViewHolder == null) {
            mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
            mLiveLinkMicPkViewHolder.addToParent();
        }
        mLiveLinkMicPkViewHolder.startAnim();
        mLiveLinkMicPkViewHolder.showTime();
//        mPkTimeCount = PK_TIME_MAX;
        nextPkTimeCountDown();
        if (mIsAnchor) {
//            ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(false);
        }
    }

    /**
     * 主播与主播PK PK结果的回调
     */
    private void onLinkMicPkEnd() {
        if (mIsPkEnd) {
            return;
        }
        mIsPkEnd = true;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PK_TIME);
        }
//        if (mLiveLinkMicPkViewHolder != null) {

            //上报结束pk——发起者调用
            if (mLiveRoom != null && mIsAnchor){
                //pk结果（0平局，1发起方胜，2接受方胜）
//                String pk_result;
//                if (mLeftGift == mRightGift){
//                    pk_result = "0";
//                }else if (mLeftGift > mRightGift){
//                    pk_result = "1";
//                }else {
//                    pk_result = "2";
//                }
                //TODO
                //TODO
                //TODO
                //完成PK
                mLiveRoom.finishPK(mLiveID, isPK_PKLaunch);
            }

            //-1自己的主播输 0平  1自己的主播赢
            //平局直接结束 否则进入惩罚时间
//            if (mLeftGift == mRightGift) {
//                mLiveLinkMicPkViewHolder.end(0);
//                mLiveLinkMicPkViewHolder.hideTime();
//                if (mHandler != null) {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onLinkMicPkClose();
//                            if (mIsAnchor) {
//                                ((TCLiveBasePublisherActivity) mContext).setPkBtnVisible(true);
//                            }
//                        }
//                    }, 3000);
//                }
//            } else {
//                if (mLeftGift > mRightGift) {
//                    mLiveLinkMicPkViewHolder.end(1);
//                } else {
//                    mLiveLinkMicPkViewHolder.end(-1);
//                }
//                mPkTimeCount = PK_TIME_MAX_2;//进入惩罚时间
//                nextPkTimeCountDown();
//
//                if (mIsAnchor){
//                    GiftRatio giftRatio = new GiftRatio();
//                    giftRatio.setLeftGift(mLeftGift);
//                    giftRatio.setRightGift(mRightGift);
//                    giftRatio.setPkTime(mPkTimeCount);
//                    giftRatio.setIsPkEnd(1);
//                    ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
//                }
//            }
//        }
    }

    /**
     * TODO
     * TODO
     * TODO
     * 全员进入惩罚阶段
     */
    public void inPunishment(long leftGift, long rightGift, int pkRestTime){
        if (mLiveLinkMicPkViewHolder == null){
            return;
        }

        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PK_TIME);
        }
        mIsPkEnd = true;

        mLeftGift = leftGift;
        mRightGift = rightGift;

        if (mLeftGift == mRightGift) {
            mLiveLinkMicPkViewHolder.end(0);
        } else if (mLeftGift > mRightGift){
            mLiveLinkMicPkViewHolder.end(1);
        }else {
            mLiveLinkMicPkViewHolder.end(-1);
        }

        mPkTimeCount = pkRestTime * 1000;//进入惩罚时间
        nextPkTimeCountDown();
    }

    public void onLinkMicPkClose() {
        if (mIsAnchor){
            //TODO
            //TODO
            //TODO
            //结束PK
            mLiveRoom.closePK(mLiveID, isPK_PKLaunch);
            ((TCLiveBasePublisherActivity)mContext).stopRemoteViewPK();

//            GiftRatio giftRatio = new GiftRatio();
//            giftRatio.setLeftGift(mLeftGift);
//            giftRatio.setRightGift(mRightGift);
//            giftRatio.setPkTime(mPkTimeCount);
//            giftRatio.setIsPkEnd(2);
//            ((TCLiveBasePublisherActivity)mContext).sendRoomCustomMsg_PK(giftRatio);
        }else {
            onLinkMicPkClose_();
        }
    }

    /**
     * 主播与主播PK 断开连麦PK的回调
     */
    public void onLinkMicPkClose_() {
        if(mContext instanceof TCLivePlayerActivity){
            ((TCLivePlayerActivity) mContext).changeTXCVV(true);
        }

        mLeftGift = 0;
        mRightGift = 0;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mPkPopWindow != null) {
            mPkPopWindow.dismiss();
        }
        mPkPopWindow = null;
        mIsPk = false;
        mIsPkEnd = false;
        isPK_PKLaunch = false;
//        hideSendPkWait();
//        mApplyUid = null;
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.removeFromParent();
            mLiveLinkMicPkViewHolder.release();
        }
        mLiveLinkMicPkViewHolder = null;
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.release();
        }
        mLiveLinkMicPkViewHolder = null;
    }

    /**
     * 清除所有数据
     */
    public void clearData() {
        mLeftGift = 0;
        mRightGift = 0;
//        mIsApplyDialogShow = false;
//        mAcceptPk = false;
        mIsPk = false;
//        mApplyUid = null;
//        mLiveUid = null;
        mPkWaitCount = 0;
        mPkTimeCount = 0;
        mIsPkEnd = false;
//        mPkSend = false;
//        mPkSendWaitCount = 0;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.release();
            mLiveLinkMicPkViewHolder.removeFromParent();
        }
        mLiveLinkMicPkViewHolder = null;
    }
}

