package com.yjfshop123.live.live.live.common.widget.gift.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.commondef.AttributeInfo;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveEnterRoomBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.presenter.LiveEnterRoomAnimPresenter;
import com.yjfshop123.live.live.live.common.widget.gift.presenter.LiveGiftAnimPresenter;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.widget.MarqueeTextView;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.TaskAlertDialog;
import com.opensource.svgaplayer.SVGAImageView;


import pl.droidsonroids.gif.GifImageView;

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mVotesName;//映票名称
    private TextView mVotes;
    private TextView mGuardNum;//守护人数
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveGiftAnimPresenter mLiveGiftAnimPresenter;
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;

    private ImageView mRoomTimeIv;
    private TextView mRoomTimeTv;
    private FrameLayout mLiveNoticeFl;
    private MarqueeTextView mLiveNoticeContent;

    //录制
    private FrameLayout mLiveRoomRecordingFL;
    private DrawableTextView mLiveRoomRecordingDTV;

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView) {
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
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        mLiveNoticeFl = (FrameLayout) findViewById(R.id.live_notice_fl);
        mLiveNoticeContent = (MarqueeTextView) findViewById(R.id.live_notice_content);

        //录制
        mLiveRoomRecordingFL = (FrameLayout) findViewById(R.id.live_room_recording_fl);
        mLiveRoomRecordingDTV = (DrawableTextView) findViewById(R.id.live_room_recording_dtv);

        mVotesName.setText(mContext.getString(R.string.my_jinbi));

        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        findViewById(R.id.live_notice_close).setOnClickListener(this);

        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);

        LinearLayout mRoomTimeLl = (LinearLayout) findViewById(R.id.view_live_room_time_ll);
        if (mContext instanceof TCLiveBasePublisherActivity){
            mRoomTimeLl.setVisibility(View.VISIBLE);
            mRoomTimeIv = (ImageView) findViewById(R.id.view_live_room_record_ball);
            mRoomTimeTv = (TextView) findViewById(R.id.view_live_room_time);
        }else {
            mRoomTimeLl.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 显示主播守护人数
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(mContext.getString(R.string.guard_7) + guardNum + mContext.getString(R.string.people));
            } else {
                mGuardNum.setText(mContext.getString(R.string.xwd));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_votes) {
            //打开直播间贡献榜窗口
            if (mContext instanceof TCLiveBasePublisherActivity){
                ((TCLiveBasePublisherActivity) mContext).contributeList();
            }else {
                ((TCLivePlayerActivity) mContext).contributeList();
            }
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
    public void onEnterRoom(TCSimpleUserInfo userInfo, AttributeInfo attributeInfo) {
        if (mLiveEnterRoomAnimPresenter != null) {

            //开守护 或者VIP 炫酷方式 进入
            boolean isVip = false;
            if (attributeInfo.getIsVip().equals("1")){
                isVip = true;
            }
            boolean isGuard = false;
            if (attributeInfo.getIsGuard().equals("1")){
                isGuard = true;
            }

            String mount = attributeInfo.getMount();
            if (/*isVip || */isGuard || !TextUtils.isEmpty(mount)){
                LiveEnterRoomBean bean = new LiveEnterRoomBean(userInfo.nickname, userInfo.headpic, isVip, isGuard,
                        attributeInfo.getUser_level(), attributeInfo.getIdentityType(), mount);
                mLiveEnterRoomAnimPresenter.enterRoom(bean);
            }
        }
    }

    /**
     * 显示礼物动画
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
//        updateVotes(bean.getLiveReceiveGiftBeanItem().getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * 显示主播映票数
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    public String getVotes(){
        if (mVotes != null){
            String votesVal = mVotes.getText().toString().trim();
            if (TextUtils.isEmpty(votesVal)) {
                return "0";
            }
            return votesVal;
        }
        return "0";
    }

    //增加主播映票数
    /*public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            setVotes(deltaVal);//空直接加上
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void release() {
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
    }

    public void clearData() {
        if (mVotes != null) {
            mVotes.setText("");
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
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

    //录制
    private int mDurationMin;//录制时间秒
    private boolean isRecording;//是否录制中

    //开始录制
    public void startRecording(){
        if (isRecording){
            NToast.shortToast(mContext, "直播正在录制中……");
            return;
        }

        final TaskAlertDialog priceAlertDialog = new TaskAlertDialog(mContext);
        priceAlertDialog.setData2();
        priceAlertDialog.setTiTle("录制时长/分钟");
        priceAlertDialog.builder();
        priceAlertDialog.setNegativeButton(mContext.getString(R.string.other_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        priceAlertDialog.setPositiveButton(mContext.getString(R.string.other_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int duration_min = Integer.parseInt(priceAlertDialog.getPriceStr());
                mDurationMin = duration_min * 60;
                if (mContext instanceof TCLiveBasePublisherActivity){
                    ((TCLiveBasePublisherActivity)mContext).startRecording(duration_min);
                }
            }
        });
        priceAlertDialog.show();
    }

    //进入录制
    public void inRecording(){
        mLiveRoomRecordingFL.setVisibility(View.VISIBLE);//显示录制进度
        isRecording = true;
//        mLiveRoomRecordingDTV.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_start));
        mLiveRoomRecordingDTV.setText(TCUtils.formattedTime(mDurationMin));
    }

    //更新录制
    public void updataRecording(){
        if (isRecording){
            mDurationMin--;
            if (mDurationMin < 0){
                finishRecording();
            }else {
                mLiveRoomRecordingDTV.setText(TCUtils.formattedTime(mDurationMin));
            }
        }
    }

    //录制完成
    private void finishRecording(){
        mLiveRoomRecordingFL.setVisibility(View.GONE);//关闭录制进度
        isRecording = false;
        NToast.shortToast(mContext, "录制完成");
//        mLiveRoomRecordingDTV.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_stop));
//        mLiveRoomRecordingDTV.setText("开始录制");
    }
}
