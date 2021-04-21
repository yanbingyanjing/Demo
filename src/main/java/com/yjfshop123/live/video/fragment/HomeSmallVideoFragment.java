package com.yjfshop123.live.video.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CommunityReplyItemDetailResponse;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.IVideoView;
import com.yjfshop123.live.ui.activity.CommunityReplyListActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.ReplyDialogActivity;
import com.yjfshop123.live.ui.adapter.CommunityReplyDetailAdapter2;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.adapter.HomeSmallVideoAdapter;
import com.yjfshop123.live.video.utils.TelephonyUtil;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeSmallVideoFragment extends BaseFragment implements ITXVodPlayListener, TelephonyUtil.OnTelephoneListener, IVideoView {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.player_iv_pause)
    ImageView mPlayerIvPause;

    private TXCloudVideoView mTXCloudVideoView;
    private ImageView mIvCover;

    private TXVodPlayer mTXVodPlayer;

    private boolean isVisibleToUser = false;
    private boolean isHidden = false;
    private int mScreenWidth;

    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;

    private HomeSmallVideoAdapter mAdapter;
    private int currentPosition = 0;
    private boolean isLoadingMore_ = false;

    private ObjectAnimator mPlayBtnAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_small_video;
    }

    @Override
    protected void initAction() {
        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();
        mScreenWidth = CommonUtils.getScreenWidth(mContext);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new HomeSmallVideoAdapter(mContext, datas, mScreenWidth);
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);

        initPlayBtnAnimator();
    }

    @Override
    protected void initEvent() {
        new GravityPagerSnapHelper(Gravity.BOTTOM, true, new GravitySnapHelper.SnapListener() {
            @Override
            public void onSnap(int position) {
                if (currentPosition == position) {
                    return;
                }
                currentPosition = position;
                startPlay(position);

                if (currentPosition == datas.size() - 1) {
                    if (!isLoadingMore_) {
                        isLoadingMore_ = true;
                        mPage++;
                        videoList();
                    }
                }

            }
        }).attachToRecyclerView(mRecyclerView);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTXVodPlayer.seek(seekBar.getProgress());
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });
    }

    private void startPlay(final int position){
        if (mTXVodPlayer != null) {
            mTXVodPlayer.seek(0);
            mTXVodPlayer.pause();
            mTXVodPlayer.stopPlay(true);
        }
        if (mIvCover != null){
            mIvCover.setVisibility(View.VISIBLE);
        }
        hidePlayBtn();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlay2(position);
            }
        }, 100);
    }

    private void startPlay2(int position) {
        if (null != mRecyclerView.findViewHolderForAdapterPosition(position)) {
            HomeSmallVideoAdapter.ViewHolder viewHolder = (HomeSmallVideoAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            mTXCloudVideoView = viewHolder.mVideo;
            mIvCover = viewHolder.mCover;
//            mPlayerIvPause = viewHolder.mPause;

            if (mTXVodPlayer == null){
                mTXVodPlayer = new TXVodPlayer(mContext);
                mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

                TXVodPlayConfig config = new TXVodPlayConfig();
                config.setCacheFolderPath(getInnerSDCardPath() + "/chatCache");
                config.setMaxCacheItems(10);
                mTXVodPlayer.setConfig(config);
                mTXVodPlayer.setAutoPlay(false);
            }

            //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
            int width = datas.get(position).getVideo_list().get(0).getExtra_info().getWidth();
            int height = datas.get(position).getVideo_list().get(0).getExtra_info().getHeight();
            if (width > height){
                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            }else {
                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            }
            mTXVodPlayer.setVodListener(this);

            String videoUrl = CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getObject());
            mTXVodPlayer.setPlayerView(mTXCloudVideoView);
            mTXVodPlayer.startPlay(videoUrl);

            viewHolder.mVPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTXVodPlayer == null) {
                        return;
                    }
                    if (mTXVodPlayer.isPlaying()) {
                        pause();

                        showPlayBtn();
                        if (mPlayBtnAnimator != null) {
                            mPlayBtnAnimator.start();
                        }
                    }else {
                        resume();
                    }
                }
            });
        }
    }

    private void initPlayBtnAnimator(){
        mPlayBtnAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayerIvPause,
                PropertyValuesHolder.ofFloat("scaleX", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mPlayBtnAnimator.setDuration(150);
        mPlayBtnAnimator.setInterpolator(new AccelerateInterpolator());
    }

    private void showPlayBtn() {
        if (mPlayerIvPause != null && mPlayerIvPause.getVisibility() != View.VISIBLE) {
            mPlayerIvPause.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayBtn() {
        if (mPlayerIvPause != null && mPlayerIvPause.getVisibility() == View.VISIBLE) {
            mPlayerIvPause.setVisibility(View.GONE);
        }
    }

    private String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        mPage = 1;
        videoList();
    }

    @Override
    protected void updateViews(boolean refresh) {
        if (!refresh){
            showLoading();
        }
        mPage = 1;
        videoList();
    }

    private List<VideoDynamicResponse.ListBean> datas = new ArrayList<>();
    private int mPage = 1;

    private void videoList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", mPage)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortVideo/getPopularVideoList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mPage == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                isLoadingMore_ = false;
                if (result == null){
                    return;
                }
                try {
                    VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);
                    if (mPage == 1) {
                        datas.clear();
                    }
                    datas.addAll(response.getList());
                    mAdapter.notifyDataSetChanged();

                    if (mPage == 1) {
                        startPlay(0);
                    } else {
                        //修复加载下页第一个视频无法播放问题
                        startPlay(currentPosition);
                    }

                    if (datas.size() == 0){
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (mPage == 1){
                        showNotData();
                    }
                }
            }
        });
    }

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        personalInfo();
        if (!isVisibleToUser){
            return;
        }
        if (isHidden){
            return;
        }
        resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    public void hiddenChanged(boolean hidden) {
        isHidden = hidden;
        if (hidden) {
            pause();
        }else {
            if (isVisibleToUser) {
                resume();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (this.isVisibleToUser) {
            resume();
        }else {
            pause();
        }
    }

    private void pause(){
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.pause();
        }
    }

    private void resume(){
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }

        stopPlay(true);
        mTXVodPlayer = null;

        TelephonyUtil.getInstance().uninitPhoneListener();
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
            //FIXBUG:不能修改为横屏，合唱会变为横向的
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放
            mIvCover.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            if (isVisibleToUser){
                mTXVodPlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            hidePlayBtn();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }

            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            int ableDuration = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
                mSeekBar.setSecondaryProgress(ableDuration);
                mSeekBar.setMax(duration);
            }
        } else if (event < 0) {

        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    @Override
    public void onRinging() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onOffhook() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onIdle() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(false);
        }
    }

    @Override
    public void avatar(int position) {
        if (!isLogin()){
            return;
        }
        if (datas.size() == 0) {
            return;
        }

        ActivityUtils.startUserHome(mContext, String.valueOf(datas.get(position).getUser_id()));
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    public void like(ImageView iv, TextView tv, int position) {
        if (!isLogin()){
            return;
        }
        if (datas.size() == 0) {
            return;
        }

        if (datas.get(position).getIs_like() == 0) {
            //点赞
            datas.get(position).setIs_like(1);
            iv.setImageResource(R.drawable.ic_short_video_yidianzan_p);
            datas.get(position).setLike_num(datas.get(position).getLike_num() + 1);

            CommunityDoLike.getInstance().shortVideoDoLike(datas.get(position).getDynamic_id(), false);
        } else {
            //取消点赞
            datas.get(position).setIs_like(0);
            iv.setImageResource(R.drawable.ic_short_video_dianzan_p);
            datas.get(position).setLike_num(datas.get(position).getLike_num() - 1);

            CommunityDoLike.getInstance().shortVideoUndoLike(datas.get(position).getDynamic_id(), false);
        }
        tv.setText(datas.get(position).getLike_num() + "");
    }

    private TextView video_reply_num;
    @Override
    public void reply(TextView tv, int position) {
        if (!isLogin()){
            return;
        }
        video_reply_num = tv;
        replyDialog();
    }

    @Override
    public void gift(int position) {
        if (!isLogin()){
            return;
        }
        if (datas.size() == 0) {
            return;
        }

        startGift(position);
    }

    @Override
    public void follow(final ImageView iv, int position) {
        if (!isLogin()){
            return;
        }
        if (datas.size() == 0) {
            return;
        }

        if (datas.get(position).getIs_follow() == 0) {
            //去关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", String.valueOf(datas.get(position).getUser_id()))
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    datas.get(currentPosition).setIs_follow(1);
                    iv.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void share(int position) {
        if (!isLogin()){
            return;
        }
        link = CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getObject());
        title = datas.get(position).getTitle();
        desc = datas.get(position).getContent();
        icon_link = CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getCover_img());
        showShareDialog();
    }
    @Override
    public void opShop(int position) {

    }
    private void startGift(int position) {
//        int prom_custom_uid = datas.get(position).getProm_custom_uid();
        int user_id = datas.get(position).getUser_id();
        int prom_custom_uid = user_id;

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))) {
            NToast.shortToast(mContext, "无法给自己送礼物~");
            return;
        }

        IMConversation imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(mi_platformId);

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(mName);
        imConversation.setUserAvatar(mAvatar);

        imConversation.setOtherPartyName(datas.get(position).getUser_nickname());
        imConversation.setOtherPartyAvatar(CommonUtils.getUrl(datas.get(position).getAvatar()));

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        Intent intent = new Intent(mContext, MessageListActivity.class);
        intent.putExtra("IMConversation", imConversation);
        intent.putExtra("GIFT", "GIFT");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    private int info_complete = 0;
    private String mi_tencentId;
    private String mi_platformId;
    private String mName;
    private String mAvatar;

    private void personalInfo() {
        info_complete = UserInfoUtil.getInfoComplete();
        mi_tencentId = UserInfoUtil.getMiTencentId();
        mi_platformId = UserInfoUtil.getMiPlatformId();
        mName = UserInfoUtil.getName();
        mAvatar = UserInfoUtil.getAvatar();
    }

    private boolean isLogin(){
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(mContext, LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
    }

    public void cleanLogin(){
        info_complete = 0;
    }

    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;
    private String link;//分享链接
    private String title;//分享标题
    private String desc;//分享描述
    private String icon_link;//分享ICON图
    public void showShareDialog() {
        if (link == null){
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.share_dialog2, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity(), R.style.BottomDialog).create();
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

            startShare();
        }
    };

    private void startShare() {
        ShareAction shareAction = new ShareAction(getActivity());
        String shareUrl = link;
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(new UMImage(mContext, icon_link));
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
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            //分享成功啦
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            //分享失败
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //分享取消了
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == 222) {
            replyContent = data.getStringExtra("replyContent");
            int type = data.getIntExtra("type", 0);
            if (type == 1) {
                if (TextUtils.isEmpty(replyContent)) {
                    reply_dialog_tv.setText("留下你的精彩评论吧");
                    reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_content_txt));
                } else {
                    reply_dialog_tv.setText(replyContent);
                    reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_title_txt));
                }
            } else if (type == 2) {
                if (TextUtils.isEmpty(replyContent) || TextUtils.isEmpty(replyContent.trim())) {
                    NToast.shortToast(mContext, "评论不能为空~");
                    return;
                }
                addReplay();
                reply_dialog_tv.setText("留下你的精彩评论吧");
                reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_content_txt));
            }
        }
    }

    private void addReplay(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", datas.get(currentPosition).getDynamic_id())
                    .put("parent_reply_id", 0)
                    .put("reviewed_user_id", 0)
                    .put("content", replyContent)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/addReply4Dynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "评论成功");

                int reply_num = datas.get(currentPosition).getReply_num() + 1;
                datas.get(currentPosition).setReply_num(reply_num);
                video_reply_num.setText(reply_num + "");
                if (reply_dialog_title != null) {
                    reply_dialog_title.setText(reply_num + "条评论");
                }

                replyContent = "";

                page = 1;
                replayList();
            }
        });
    }

    private int page = 1;
    private VerticalSwipeRefreshLayout replyRefresh;
    private RecyclerView recyclerView;
    private CommunityReplyDetailAdapter2 communityReplyDetailAdapter2;
    private boolean isLoadingMore = false;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView reply_dialog_tv;
    private String replyContent = "";
    private TextView reply_dialog_title;
    private RelativeLayout noDataLayout;

    private void replyDialog(){
        final Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog2);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.reply_dialog, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        reply_dialog_title = contentView.findViewById(R.id.reply_dialog_title);
        reply_dialog_title.setText(datas.get(currentPosition).getReply_num() + "条评论");

        replyRefresh = contentView.findViewById(R.id.reply_dialog_refresh);
        recyclerView = contentView.findViewById(R.id.reply_dialog_recycler_view);
        reply_dialog_tv = contentView.findViewById(R.id.reply_dialog_tv);
        noDataLayout = contentView.findViewById(R.id.noDataLayout);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        communityReplyDetailAdapter2 = new CommunityReplyDetailAdapter2(mContext, 2, true);
        recyclerView.setAdapter(communityReplyDetailAdapter2);
        initReplySwipeRefresh();

        page = 1;
        replayList();

        contentView.findViewById(R.id.reply_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.reply_dialog_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        replayList();
                    }
                }
            }
        });

        contentView.findViewById(R.id.reply_dialog_fl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReplyDialogActivity.class);
                intent.putExtra("replyContent", replyContent);
                startActivityForResult(intent, 111);
            }
        });

        communityReplyDetailAdapter2.setOptionClickListener(new CommunityReplyDetailAdapter2.OptionClickListener() {
            @Override
            public void optionClick() {
//                dialogMore();
            }

            @Override
            public void headAndNameClick(View view, int option) {
                ActivityUtils.startUserHome(mContext, String.valueOf(lists.get(option).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }

            @Override
            public void itemClick(View view, int option, int index) {
                Intent intent = new Intent(mContext, CommunityReplyListActivity.class);

                if (index == -1) {

                } else {
                    intent.putExtra("detail_index", index);
                    intent.putExtra("preview_index", option);
                }

                intent.putExtra("replyId", lists.get(option).getReply_id());
                intent.putExtra("TYPE", 2);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });
    }

    private void replayList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", datas.get(currentPosition).getDynamic_id())
                    .put("type", 1)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/replayList4Dynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                replyFinishRefresh();
                isLoadingMore = false;
                if (page == 1){
                    noDataLayout.setVisibility(View.VISIBLE);
                    replyRefresh.setVisibility(View.GONE);
                }

            }
            @Override
            public void onSuccess(String result) {
                replyFinishRefresh();
                isLoadingMore = false;
                try {
                    CommunityReplyItemDetailResponse response = JsonMananger.jsonToBean(result, CommunityReplyItemDetailResponse.class);
                    if (communityReplyDetailAdapter2 != null && recyclerView != null) {

                        if (page == 1) {
                            if (lists.size() > 0) {
                                lists.clear();
                            }
                        }

                        lists.addAll(response.getList());

                        if (lists.size() > 0) {
                            noDataLayout.setVisibility(View.GONE);
                            replyRefresh.setVisibility(View.VISIBLE);
                        } else {
                            noDataLayout.setVisibility(View.VISIBLE);
                            replyRefresh.setVisibility(View.GONE);
                        }

                        communityReplyDetailAdapter2.setCards(lists);
                        communityReplyDetailAdapter2.notifyDataSetChanged();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private List<CommunityReplyItemDetailResponse.ListBeanX> lists = new ArrayList<>();

    private void replyFinishRefresh() {
        if (replyRefresh != null) {
            replyRefresh.setRefreshing(false);
        }
    }

    private void initReplySwipeRefresh() {
        if (replyRefresh != null) {
            SwipeRefreshHelper.init(replyRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    replayList();
                }
            });
        }
    }
}
