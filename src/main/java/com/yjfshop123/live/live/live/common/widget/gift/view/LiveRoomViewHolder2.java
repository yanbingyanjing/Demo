package com.yjfshop123.live.live.live.common.widget.gift.view;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.presenter.LiveGiftAnimPresenter;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.ui.widget.MarqueeTextView;
import com.opensource.svgaplayer.SVGAImageView;


import pl.droidsonroids.gif.GifImageView;

public class LiveRoomViewHolder2 extends AbsViewHolder implements View.OnClickListener {

//    private TextView mVotesName;//映票名称
//    private TextView mVotes;
//    private TextView mGuardNum;//守护人数
//    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveGiftAnimPresenter mLiveGiftAnimPresenter;
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;

    private ImageView mRoomTimeIv;
    private TextView mRoomTimeTv;
    private FrameLayout mLiveNoticeFl;
    private MarqueeTextView mLiveNoticeContent;

    public LiveRoomViewHolder2(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @Override
    public void init() {
//        mVotesName = (TextView) findViewById(R.id.votes_name);
//        mVotes = (TextView) findViewById(R.id.votes);
//        mGuardNum = (TextView) findViewById(R.id.guard_num);
        mLiveNoticeFl = (FrameLayout) findViewById(R.id.live_notice_fl);
        mLiveNoticeContent = (MarqueeTextView) findViewById(R.id.live_notice_content);

//        mVotesName.setText(Constants.GOLD_COINS);

//        findViewById(R.id.btn_votes).setOnClickListener(this);
//        findViewById(R.id.btn_guard).setOnClickListener(this);
//        findViewById(R.id.live_notice_close).setOnClickListener(this);

//        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);

        LinearLayout mRoomTimeLl = (LinearLayout) findViewById(R.id.view_live_room_time_ll);
        if (mContext instanceof TCLiveBasePublisherActivity){
            mRoomTimeLl.setVisibility(View.VISIBLE);
            mRoomTimeIv = (ImageView) findViewById(R.id.view_live_room_record_ball);
            mRoomTimeTv = (TextView) findViewById(R.id.view_live_room_time);
        }else {
            mRoomTimeLl.setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.btn_votes).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_guard).setVisibility(View.INVISIBLE);
    }

    /**
     * 显示主播守护人数
     */
//    public void setGuardNum(int guardNum) {
//        if (mGuardNum != null) {
//            if (guardNum > 0) {
//                mGuardNum.setText("守护者" + guardNum + "人");
//            } else {
//                mGuardNum.setText("虚位以待");
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_votes) {
//            NToast.shortToast(mContext, "打开直播间贡献榜窗口");
        } else if (i == R.id.btn_guard) {
            if (mContext instanceof TCLiveBasePublisherActivity){
                ((TCLiveBasePublisherActivity) mContext).openGuardListWindow();
            }else {
                ((TCLivePlayerActivity) mContext).openGuardListWindow();
            }
        }else if (i == R.id.live_notice_close){
            mLiveNoticeFl.setVisibility(View.GONE);
        }
    }

    /**
     * 用户进入房间 金光一闪
     */
//    public void onEnterRoom(TCSimpleUserInfo userInfo, AttributeInfo attributeInfo) {
//        if (mLiveEnterRoomAnimPresenter != null) {
//
//            //开守护 或者VIP 炫酷方式 进入
//            boolean isVip = false;
//            if (attributeInfo.getIsVip().equals("1")){
//                isVip = true;
//            }
//            boolean isGuard = false;
//            if (attributeInfo.getIsGuard().equals("1")){
//                isGuard = true;
//            }
//
//            if (isVip || isGuard){
//                LiveEnterRoomBean bean = new LiveEnterRoomBean(userInfo.nickname, userInfo.headpic, isVip, isGuard, attributeInfo.getUser_level(), attributeInfo.getIdentityType());
//                mLiveEnterRoomAnimPresenter.enterRoom(bean);
//            }
//        }
//    }

    /**
     * 显示礼物动画
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
//        updateVotes(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * 显示主播映票数
     */
//    public void setVotes(String votes) {
//        if (mVotes != null) {
//            mVotes.setText(votes);
//        }
//    }
//
//    public String getVotes(){
//        if (mVotes != null){
//            String votesVal = mVotes.getText().toString().trim();
//            if (TextUtils.isEmpty(votesVal)) {
//                return "0";
//            }
//            return votesVal;
//        }
//        return "0";
//    }

//    /**
//     * 增加主播映票数
//     *
//     * @param deltaVal 增加的映票数量
//     */
//    public void updateVotes(String deltaVal) {
//        if (mVotes == null) {
//            return;
//        }
//        String votesVal = mVotes.getText().toString().trim();
//        if (TextUtils.isEmpty(votesVal)) {
//            setVotes(deltaVal);//空直接加上
//            return;
//        }
//        try {
//            double votes = Double.parseDouble(votesVal);
//            double addVotes = Double.parseDouble(deltaVal);
//            votes += addVotes;
//            mVotes.setText(StringUtil.format(votes));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void release() {
//        if (mLiveEnterRoomAnimPresenter != null) {
//            mLiveEnterRoomAnimPresenter.release();
//        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
    }

    public void clearData() {
//        if (mVotes != null) {
//            mVotes.setText("");
//        }
//        if (mGuardNum != null) {
//            mGuardNum.setText("");
//        }
//        if (mLiveEnterRoomAnimPresenter != null) {
//            mLiveEnterRoomAnimPresenter.cancelAnim();
//            mLiveEnterRoomAnimPresenter.resetAnimView();
//        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }
    }

    public ImageView getmRoomTimeIv(){
        return mRoomTimeIv;
    }

    public TextView getmRoomTimeTv(){
        return mRoomTimeTv;
    }

    public FrameLayout getLiveNoticeFl(){
        return mLiveNoticeFl;
    }

    public MarqueeTextView getLiveNoticeContent(){
        return mLiveNoticeContent;
    }

}

