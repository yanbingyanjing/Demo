package com.yjfshop123.live.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;
import java.util.Locale;


public class VideoPreviewActivity extends BaseActivity implements View.OnClickListener, ITXVodPlayListener {

    protected TXCloudVideoView mTXCloudVideoView;

    private TXVodPlayer mTXVodPlayer;
    private TXVodPlayConfig mTXConfig = new TXVodPlayConfig();
    private boolean mPlaying = false;

    //点播相关
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private SeekBar mSeekBar;
    private TextView mTextCurrent, mTextTotal;
    private ImageView mPlayIcon;

    protected ImageView mBgImageView;

    private String mVideoPath;
    private String mVideoThumb;
    private int width = 0;
    private int height = 0;

    private ObjectAnimator mPlayBtnAnimator;

    public static void startActivity(Activity activity, String videoPath, String videoThumb) {
        Intent intent = new Intent(activity, VideoPreviewActivity.class);
        intent.putExtra("path", videoPath);
        intent.putExtra("thumb", videoThumb);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_pre);

        setHeadVisibility(View.GONE);

        mVideoPath = getIntent().getStringExtra("path");
        mVideoThumb = getIntent().getStringExtra("thumb");

        initView();

        mPhoneListener = new TXPhoneStateListener(mTXVodPlayer);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        mTXVodPlayer = new TXVodPlayer(this);

        Glide.with(this).asBitmap().load(mVideoThumb).listener(new LoadListen()).into(mBgImageView);

        initPlayBtnAnimator();
    }

    private void params(ImageView imgView, int with, int height){
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = with;
        params.height = height;
        imgView.setLayoutParams(params);
    }

    private void initView() {
        mTXCloudVideoView = findViewById(R.id.video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);
        mTextCurrent = findViewById(R.id.current);
        mTextTotal = findViewById(R.id.total);
        mPlayIcon = findViewById(R.id.player_iv_pause);
        mSeekBar = findViewById(R.id.seekbar);
        findViewById(R.id.player_v_pause).setOnClickListener(this);
        findViewById(R.id.closeBtn).setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextCurrent != null) {
                    mTextCurrent.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", progress / 3600, (progress%3600) / 60, progress % 60));
                }
                if (mTextTotal != null) {
                    mTextTotal.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", seekBar.getMax() / 3600, (seekBar.getMax()%3600) / 60, (seekBar.getMax()%3600) % 60));
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
        mBgImageView =  findViewById(R.id.player_iv_cover);
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
                mTextCurrent.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", progress / 3600, (progress%3600) / 60, progress % 60));
            }
            if (mTextTotal != null) {
                mTextTotal.setText(String.format(Locale.CHINA, "%02d:%02d:%02d", duration / 3600, (duration%3600) / 60, duration % 60));
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
                mTextCurrent.setText(String.format(Locale.CHINA, "%s","00:00:00"));
            }
            if (mTextTotal != null) {
                mTextTotal.setText(String.format(Locale.CHINA, "%s","00:00:00"));
            }

            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            mBgImageView.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            hidePlayBtn();
        }
    }

    private void report(int event) {
        switch (event) {
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME :
                //视频播放成功
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT :
                //网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
                break;
            case TXLiveConstants.PLAY_ERR_GET_RTMP_ACC_URL_FAIL :
                //获取加速拉流地址失败
                break;
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND :
                //播放文件不存在
                break;
            case TXLiveConstants.PLAY_ERR_HEVC_DECODE_FAIL :
                //H265解码失败
                break;
            case TXLiveConstants.PLAY_ERR_HLS_KEY :
                //HLS解码Key获取失败
                break;
            case TXLiveConstants.PLAY_ERR_GET_PLAYINFO_FAIL :
                //获取点播文件信息失败
                break;

        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {
        if(status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        } else  {
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
            switch(state){
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
    };
    private PhoneStateListener mPhoneListener = null;

    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    protected void startPlay() {
        mTXConfig.setCacheFolderPath(getInnerSDCardPath() + "/chatCache");
        mTXConfig.setMaxCacheItems(3);
        mTXVodPlayer.setPlayerView(mTXCloudVideoView);
        mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        if (width > height){
            mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        }else {
            mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        }
        mTXVodPlayer.setVodListener(this);
        mTXVodPlayer.setConfig(mTXConfig);
        mTXVodPlayer.setAutoPlay(true);
        mTXVodPlayer.setLoop(true);
        int result = mTXVodPlayer.startPlay(mVideoPath);

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
            case R.id.closeBtn:
                finish();
                break;
            case R.id.player_v_pause:
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
            default:
                break;
        }
    }

    private void initPlayBtnAnimator(){
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }
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

    private class LoadListen implements RequestListener<Bitmap> {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            startPlay();
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

            width = resource.getWidth();
            height = resource.getHeight();
            if (width > height){
//            params(mBgImageView, screenWidth_, (screenWidth_ * height / width));
                mBgImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else {
                mBgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            startPlay();
            return false;
        }
    }

}
