package com.yjfshop123.live.live.live.play;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.other.ContributeListFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveGuardDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveUserDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ShopDialogFragment;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;
import java.util.Locale;


public class TCVodPlayerActivity extends BaseActivityH implements View.OnClickListener, ITXVodPlayListener {

    protected TXCloudVideoView mTXCloudVideoView;

    private TXVodPlayer mTXVodPlayer;
    private TXVodPlayConfig mTXConfig = new TXVodPlayConfig();

    protected Handler mHandler = new Handler();

    private ImageView mHeadIcon;
    private TextView mtvPuserName;

    private String mPusherAvatar;
    private boolean mPlaying = false;

    private String mPusherNickname;
    protected String mPusherId;
    protected String mPlayUrl;
    protected String mLiveID;

    private String mTotalCoinNum;
    private String mWatchNum;

    private String mUserId;

    //点播相关
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private SeekBar mSeekBar;
    private ImageView mPlayIcon;
    private TextView mTextCurrent, mTextTotal;
    private TextView mVotes;
    private TextView mGuardNum;

    protected ImageView mBgImageView;

    private String mCoverUrl;
//    private String mTitle;

    private ObjectAnimator mPlayBtnAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_vod_play);

        Intent intent = getIntent();
//        mTitle = intent.getStringExtra(TCConstants.ROOM_TITLE);
        mCoverUrl = intent.getStringExtra(TCConstants.COVER_PIC);

        mPusherId = intent.getStringExtra("USER_ID");
        mPlayUrl = intent.getStringExtra("VIDEO_URL");
        mPusherNickname = intent.getStringExtra("USER_NICKNAME");
        mPusherAvatar = intent.getStringExtra("AVATAR");
        mTotalCoinNum = intent.getStringExtra("TOTAL_COIN_NUM");
        mWatchNum = intent.getStringExtra("WATCH_NUM");
        mLiveID = intent.getStringExtra("LIVE_ID");

        mUserId = UserInfoUtil.getMiTencentId();

        initView();
        mVotes.setText(mTotalCoinNum);
        setGuardNum();

        mCoverUrl = getIntent().getStringExtra(TCConstants.COVER_PIC);
        Glide.with(this)
                .load(mCoverUrl)
                .into(mBgImageView);

        mPhoneListener = new TXPhoneStateListener(mTXVodPlayer);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        mTXVodPlayer = new TXVodPlayer(this);
        initPlayBtnAnimator();
        startPlay();
    }

    public void setGuardNum() {
        if (mGuardNum != null) {
            if (!TextUtils.isEmpty(mWatchNum) && !mWatchNum.equals("0")) {
                mGuardNum.setText(mContext.getString(R.string.guard_7) + mWatchNum + mContext.getString(R.string.people));
            } else {
                mGuardNum.setText(mContext.getString(R.string.xwd));
            }
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        report(event);
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            int ableDuration = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
                mSeekBar.setSecondaryProgress(ableDuration);
            }

            if (mTextCurrent != null) {
                mTextCurrent.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, progress % 60));
            }
            if (mTextTotal != null) {
                mTextTotal.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            showErrorAndQuit("网络异常，请检查网络");

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay(false);
            mVideoPause = false;

            if (mTextCurrent != null) {
                mTextCurrent.setText(String.format(Locale.CHINA, "%s", "00:00:00"));
            }
            if (mTextTotal != null) {
                mTextTotal.setText(String.format(Locale.CHINA, "%s", "00:00:00"));
            }

            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            mBgImageView.setVisibility(View.VISIBLE);
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            mBgImageView.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            hidePlayBtn();
        }
    }

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

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        } else {
            mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        }
    }

    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXVodPlayer> mPlayer;

        public TXPhoneStateListener(TXVodPlayer player) {
            mPlayer = new WeakReference<TXVodPlayer>(player);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXVodPlayer player = mPlayer.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (player != null) player.setMute(true);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (player != null) player.setMute(true);
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (player != null) player.setMute(false);
                    break;
            }
        }
    }

    ;
    private PhoneStateListener mPhoneListener = null;

    private void initView() {
        //左上直播信息
        mtvPuserName = (TextView) findViewById(R.id.tv_broadcasting_name);
        mtvPuserName.setText(TCUtils.getLimitString(mPusherNickname, 10));
        TextView tv_host_id = findViewById(R.id.tv_host_id);
        tv_host_id.setText("ID:" + mPusherId);
        mHeadIcon = (ImageView) findViewById(R.id.iv_head_icon);
        showHeadIcon(mHeadIcon, mPusherAvatar);

        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);
        mTextCurrent = (TextView) findViewById(R.id.current);
        mTextTotal = (TextView) findViewById(R.id.total);
        mPlayIcon = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextCurrent != null) {
                    mTextCurrent.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, progress % 60));
                }
                if (mTextTotal != null) {
                    mTextTotal.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", seekBar.getMax() / 3600, (seekBar.getMax() % 3600) / 60, (seekBar.getMax() % 3600) % 60));
                }
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

        mBgImageView = (ImageView) findViewById(R.id.background);
    }

    private void initPlayBtnAnimator() {
        mPlayBtnAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayIcon,
                PropertyValuesHolder.ofFloat("scaleX", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mPlayBtnAnimator.setDuration(150);
        mPlayBtnAnimator.setInterpolator(new AccelerateInterpolator());
    }

    private void showPlayBtn() {
        if (mPlayIcon != null && mPlayIcon.getVisibility() != View.VISIBLE) {
            mPlayIcon.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayBtn() {
        if (mPlayIcon != null && mPlayIcon.getVisibility() == View.VISIBLE) {
            mPlayIcon.setVisibility(View.GONE);
        }
    }

    private void showHeadIcon(ImageView view, String avatar) {
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(mContext)
                    .load(avatar)
                    .into(view);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.splash_logo)
                    .into(view);
        }

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

    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    protected void startPlay() {
        mTXConfig.setCacheFolderPath(getInnerSDCardPath() + "/luckCache");
        mTXConfig.setMaxCacheItems(3);
        mBgImageView.setVisibility(View.VISIBLE);
        mTXVodPlayer.setPlayerView(mTXCloudVideoView);
        mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXVodPlayer.setVodListener(this);
        mTXVodPlayer.setConfig(mTXConfig);
        mTXVodPlayer.setAutoPlay(true);
        mTXVodPlayer.setLoop(true);
        int result;
        result = mTXVodPlayer.startPlay(mPlayUrl);

        if (0 != result) {
            stopPlay(true);
            finish();
        } else {
            mPlaying = true;
        }
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setVodListener(null);
            mTXVodPlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }

    protected void showErrorAndQuit(final String errorMsg) {
        stopPlay(true);

        DialogUitl.showSimpleHintDialog(mContext, mContext.getString(R.string.prompt), mContext.getString(R.string.dialog_reload), getString(R.string.cancel),
                errorMsg, true, false,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        finish();
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vod_back:
                finish();
                break;
            case R.id.play_bb:
                if (mPlaying) {
                    if (mVideoPause) {
                        mTXVodPlayer.resume();

                        hidePlayBtn();
                    } else {
                        mTXVodPlayer.pause();

                        showPlayBtn();
                        if (mPlayBtnAnimator != null) {
                            mPlayBtnAnimator.start();
                        }
                    }
                    mVideoPause = !mVideoPause;
                } else {

                    hidePlayBtn();
                    startPlay();
                }
                break;
            case R.id.btn_shop:
                String url = Const.getDomain() + "/apph5/shop/goodslist";
                ShopDialogFragment fragment = new ShopDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("USERID", mPusherId);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "ShopDialogFragment");
                break;
            case R.id.btn_votes:
                ContributeListFragment contributeListFragment = new ContributeListFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("live_id", mLiveID);
                contributeListFragment.setArguments(bundle1);
                contributeListFragment.show(getSupportFragmentManager(), "ContributeListFragment");
                break;
            case R.id.btn_guard:
                LiveGuardDialogFragment liveGuardDialogFragment = new LiveGuardDialogFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("liveUid", mLiveID);
                bundle2.putInt("guardNum", Integer.parseInt(mWatchNum));
                liveGuardDialogFragment.setArguments(bundle2);
                liveGuardDialogFragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
//        if (!mVideoPause) {
//            mTXVodPlayer.resume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.pause();
        }
//        mTXVodPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay(true);
        mTXVodPlayer = null;
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        mPhoneListener = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
