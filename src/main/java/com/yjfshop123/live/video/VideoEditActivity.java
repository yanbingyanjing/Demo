package com.yjfshop123.live.video;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.utils.Constants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.StringUtil;
import com.yjfshop123.live.live.live.common.widget.music.LiveMusicDialogFragment;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.video.bean.MusicBean;
import com.yjfshop123.live.video.custom.VideoEditFilterViewHolder;
import com.yjfshop123.live.video.custom.VideoEditMusicViewHolder;
import com.yjfshop123.live.video.utils.VideoLocalUtil;
import com.yjfshop123.live.video.view.VideoEditCutViewHolder;
import com.yjfshop123.live.video.view.VideoProcessViewHolder;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VideoEditActivity extends AbsActivity implements
        VideoProcessViewHolder.ActionListener,//预处理控件点击取消回调
        TXVideoEditer.TXVideoProcessListener, //视频编辑前预处理进度回调
        TXVideoEditer.TXThumbnailListener, //视频编辑前预处理中生成每一帧缩略图回调
        TXVideoEditer.TXVideoPreviewListener,
        TXVideoEditer.TXVideoGenerateListener {

    private static final String TAG = "VideoEditActivity";
    private static final int STATUS_NONE = 0;
    private static final int STATUS_PLAY = 1;
    private static final int STATUS_PAUSE = 2;
    private static final int STATUS_PREVIEW_AT_TIME = 3;

    public static void forward(final Context context, final long videoDuration, final String videoPath, boolean fromRecord, boolean hasOriginBgm) {
//        Intent intent = new Intent(context, VideoEditActivity.class);
//        intent.putExtra(Constants.VIDEO_DURATION, videoDuration);
//        intent.putExtra(Constants.VIDEO_PATH, videoPath);
//        intent.putExtra(Constants.VIDEO_FROM_RECORD, fromRecord);
//        intent.putExtra(Constants.VIDEO_HAS_BGM, hasOriginBgm);
//        context.startActivity(intent);


        DialogUitl.showStringArrayDialog(context, new Integer[]{
                R.string.video_save_save,
                R.string.video_save_pub,
                R.string.video_save_save_and_pub
        }, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                switch (tag) {
                    case R.string.video_save_save:
                        ((VideoRecordActivity)context). release();
                        VideoLocalUtil.saveVideoInfo(context, videoPath, videoDuration);
                        break;
                    case R.string.video_save_pub:
                        ((VideoRecordActivity)context). release();
                        VideoPublishActivity.forward(context, videoPath, Constants.VIDEO_SAVE_PUB);
                        break;
                    case R.string.video_save_save_and_pub:
                        ((VideoRecordActivity)context). release();
                        VideoLocalUtil.saveVideoInfo(context, videoPath, videoDuration);
                        VideoPublishActivity.forward(context, videoPath,  Constants.VIDEO_SAVE_SAVE_AND_PUB);
                        break;
                }
                ((VideoRecordActivity)context).finish();
            }
        });
    }

    private ViewGroup mRoot;
    private View mGroup;
    private View mBtnNext;
    private View mBtnPlay;
    private ObjectAnimator mPlayBtnAnimator;//暂停按钮的动画
    private TXVideoEditer mVideoEditer;
    private List<Bitmap> mBitmapList;//视频每一帧的缩略图
    private long mVideoDuration;//视频总长度
    private String mOriginVideoPath;//原视频路径
    private boolean mFromRecord;
    private long mCutStartTime;//裁剪的起始时间
    private long mCutEndTime;//裁剪的结束时间
    private boolean mHasOriginBgm;//是否在录制的时候有背景音乐
    private VideoEditFilterViewHolder mFilterViewHolder;//滤镜
    private MusicBean mMusicBean;//背景音乐
    private VideoEditMusicViewHolder mVolumeViewHolder;//音量
    private VideoEditCutViewHolder mCutViewHolder;//裁剪
    private String mGenerateVideoPath;//生成视频的路径
    private VideoProcessViewHolder mVideoProcessViewHolder;//视频预处理进度条
    private VideoProcessViewHolder mVideoGenerateViewHolder;//视频生成进度条
    private int mSaveType;
    private boolean mPaused;//生命周期暂停
    private long mPreviewAtTime;
    private int mPLayStatus = STATUS_NONE;
    private MediaMetadataRetriever mMetadataRetriever;
    private MyHandler mHandler;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_edit;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mGroup = findViewById(R.id.group);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnPlay = findViewById(R.id.btn_play);
        //暂停按钮动画
        mPlayBtnAnimator = ObjectAnimator.ofPropertyValuesHolder(mBtnPlay,
                PropertyValuesHolder.ofFloat("scaleX", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mPlayBtnAnimator.setDuration(150);
        mPlayBtnAnimator.setInterpolator(new AccelerateInterpolator());
        mSaveType = Constants.VIDEO_SAVE_SAVE_AND_PUB;
        Intent intent = getIntent();
        mVideoDuration = intent.getLongExtra(Constants.VIDEO_DURATION, 0);
        mOriginVideoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        mFromRecord = intent.getBooleanExtra(Constants.VIDEO_FROM_RECORD, false);
        mHasOriginBgm = intent.getBooleanExtra(Constants.VIDEO_HAS_BGM, false);
        if (mVideoDuration <= 0 || TextUtils.isEmpty(mOriginVideoPath)) {
            NToast.shortToast(mContext, "状态异常，停止编辑");
            deleteOriginVideoFile();
            finish();
            return;
        }
        mVideoEditer = new TXVideoEditer(mContext);
        mVideoEditer.setVideoPath(mOriginVideoPath);
        mVideoEditer.setVideoProcessListener(this);
        mVideoEditer.setVideoBitrate(10000);
//        TODO
//        mVideoEditer.setThumbnailListener(this);
        mVideoEditer.setTXVideoPreviewListener(this);
        mVideoEditer.setVideoGenerateListener(this);
        mCutStartTime = 0;
        mCutEndTime = mVideoDuration;
        //###
        mVideoEditer.setCutFromTime(0, mVideoDuration);
        mVideoEditer.deleteAllEffect();
        mMusicBean = null;
        //###
        startPreProcess();
    }

    private void deleteOriginVideoFile() {
        if (mFromRecord && !TextUtils.isEmpty(mOriginVideoPath)) {
            File file = new File(mOriginVideoPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 显示开始播放按钮
     */
    private void showPlayBtn() {
        if (mBtnPlay != null && mBtnPlay.getVisibility() != View.VISIBLE) {
            mBtnPlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏开始播放按钮
     */
    private void hidePlayBtn() {
        if (mBtnPlay != null && mBtnPlay.getVisibility() == View.VISIBLE) {
            mBtnPlay.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 点击切换播放和暂停
     */
    private void clickTogglePlay() {
        switch (mPLayStatus) {
            case STATUS_PLAY:
                mPLayStatus = STATUS_PAUSE;
                if (mVideoEditer != null) {
                    mVideoEditer.pausePlay();
                }
                break;
            case STATUS_PAUSE:
                mPLayStatus = STATUS_PLAY;
                if (mVideoEditer != null) {
                    mVideoEditer.resumePlay();
                }
                break;
            case STATUS_PREVIEW_AT_TIME:
                mPLayStatus = STATUS_PLAY;
                if (mVideoEditer != null) {
                    if (mPreviewAtTime > mCutStartTime && mPreviewAtTime < mCutEndTime) {
                        mVideoEditer.startPlayFromTime(mPreviewAtTime, mCutEndTime);
                    } else {
                        mVideoEditer.startPlayFromTime(mCutStartTime, mCutEndTime);
                    }
                }
                break;
        }
        if (mPLayStatus == STATUS_PAUSE) {
            showPlayBtn();
            if (mPlayBtnAnimator != null) {
                mPlayBtnAnimator.start();
            }
        } else {
            hidePlayBtn();
        }
    }


    /**
     * 开启视频预览
     */
    private void startVideoPreview() {
        if (mVideoEditer == null) {
            return;
        }
        FrameLayout layout = (FrameLayout) findViewById(R.id.video_container);
        TXVideoEditConstants.TXPreviewParam param = new TXVideoEditConstants.TXPreviewParam();
        param.videoView = layout;
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE;
        mVideoEditer.initWithPreview(param);
        startPlay();
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mVideoEditer != null) {
            mPLayStatus = STATUS_PLAY;
            mVideoEditer.startPlayFromTime(mCutStartTime, mCutEndTime);
            hidePlayBtn();
        }
    }

    /**
     * 预览播放回调
     */
    @Override
    public void onPreviewProgress(int time) {
        if (mPLayStatus == STATUS_PLAY && mCutViewHolder != null) {
            mCutViewHolder.onVideoProgressChanged(time);
        }
    }

    /**
     * 预览播放回调
     */
    @Override
    public void onPreviewFinished() {
        if (mPLayStatus == STATUS_PLAY) {
            startPlay();//播放结束后，重新开始播放
        }
    }

    /**
     * 生成视频进度回调
     */
    @Override
    public void onGenerateProgress(float progress) {
        if (mVideoGenerateViewHolder != null) {
            mVideoGenerateViewHolder.setProgress((int) (progress * 100));
        }
    }

    /**
     * 生成视频结束回调
     */
    @Override
    public void onGenerateComplete(TXVideoEditConstants.TXGenerateResult result) {
        NLog.e(TAG, "onGenerateComplete------->");
        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
            NLog.e(TAG, "onGenerateComplete------->生成视频成功");
            NToast.shortToast(mContext, "生成视频成功");
            switch (mSaveType) {
                case Constants.VIDEO_SAVE_SAVE://仅保存
                    saveGenerateVideoInfo();
                    break;
                case Constants.VIDEO_SAVE_PUB://仅发布
                    VideoPublishActivity.forward(mContext, mGenerateVideoPath, mSaveType);
                    break;
                case Constants.VIDEO_SAVE_SAVE_AND_PUB://保存并发布
                    saveGenerateVideoInfo();
                    VideoPublishActivity.forward(mContext, mGenerateVideoPath, mSaveType);
                    break;
            }
            finish();
        } else {
            NToast.shortToast(mContext, "生成视频失败");
            if (mVideoGenerateViewHolder != null) {
                mVideoGenerateViewHolder.removeFromParent();
            }
            if (mBtnNext != null) {
                mBtnNext.setEnabled(true);
            }
        }
    }

    /**
     * 把新生成的视频保存到ContentProvider,在选择上传的时候能找到
     */
    private void saveGenerateVideoInfo() {
        VideoLocalUtil.saveVideoInfo(mContext, mGenerateVideoPath, mCutEndTime - mCutStartTime);
    }

    public void videoEditClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_music) {
            clickMusic();

        } else if (i == R.id.btn_music_volume) {
            clickMusicVolume();

        } else if (i == R.id.btn_filter) {
            clickFilter();

        } else if (i == R.id.btn_cut) {
            clickCut();

        } else if (i == R.id.btn_special) {
            clickSpecial();

        } else if (i == R.id.btn_next) {
            clickNext();

        } else if (i == R.id.group) {
            clickTogglePlay();

        }
    }

    /**
     * 点击音乐
     */
    private void clickMusic() {
        LiveMusicDialogFragment fragmentMusic = new LiveMusicDialogFragment();
        fragmentMusic.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    public void onBGMStart_(String bgmPath, String musicName){
        if (mVideoEditer != null) {
            if (TextUtils.isEmpty(bgmPath)) {
                return;
            }
            long bgmDuration = 0;
            if (mMetadataRetriever == null) {
                mMetadataRetriever = new MediaMetadataRetriever();
            }
            try {
                mMetadataRetriever.setDataSource(bgmPath);
                String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                bgmDuration = Long.parseLong(duration);
            } catch (Exception e) {
                bgmDuration = 0;
                e.printStackTrace();
            }
            if (bgmDuration == 0) {
                return;
            }
            mVideoEditer.setBGM(bgmPath);
            mVideoEditer.setBGMVolume(0.8f);
            if (mHasOriginBgm) {
                mVideoEditer.setVideoVolume(0);
            }
            mMusicBean = new MusicBean(musicName, bgmPath, bgmDuration);
            if (mVolumeViewHolder != null) {
                mVolumeViewHolder.setMusicBean(mMusicBean);
            }
            mVideoEditer.stopPlay();
            startPlay();
        }
    }

    /**
     * 点击音量
     */
    private void clickMusicVolume() {
        hideGroup();
        if (mVolumeViewHolder == null) {
            mVolumeViewHolder = new VideoEditMusicViewHolder(mContext, mRoot, mHasOriginBgm);
            mVolumeViewHolder.setActionListener(new VideoEditMusicViewHolder.ActionListener() {
                @Override
                public void onHide() {
                    showGroup();
                }

                @Override
                public void onOriginalVolumeChanged(float value) {
                    if (mVideoEditer != null) {
                        mVideoEditer.setVideoVolume(value);
                    }
                }

                @Override
                public void onBgmVolumeChanged(float value) {
                    if (mVideoEditer != null) {
                        mVideoEditer.setBGMVolume(value);
                    }
                }

                @Override
                public void onBgmCancelClick() {
                    if (mVideoEditer != null) {
                        mVideoEditer.setVideoVolume(0.8f);
                        mVideoEditer.setBGM(null);
                        mVideoEditer.stopPlay();
                        startPlay();
                    }
                    mMusicBean = null;
                }

                @Override
                public void onBgmCutTimeChanged(long startTime, long endTime) {
                    if (mVideoEditer != null) {
                        mVideoEditer.setBGMStartTime(startTime, endTime);
                    }
                }
            });
            mVolumeViewHolder.addToParent();
        }
        mVolumeViewHolder.setMusicBean(mMusicBean);
        mVolumeViewHolder.show();
    }

    /**
     * 点击滤镜
     */
    private void clickFilter() {
        hideGroup();
        if (mFilterViewHolder == null) {
            mFilterViewHolder = new VideoEditFilterViewHolder(mContext, mRoot);
            mFilterViewHolder.setActionListener(new VideoEditFilterViewHolder.ActionListener() {
                @Override
                public void onHide() {
                    showGroup();
                }

                @Override
                public void onFilterChanged(Bitmap bitmap) {
                    if (mVideoEditer != null) {
                        mVideoEditer.setFilter(bitmap);
                    }
                }
            });
            mFilterViewHolder.addToParent();
        }
        mFilterViewHolder.show();
    }

    private void showCutViewHolder(boolean showSpecial) {
        if (mCutViewHolder == null) {
            mCutViewHolder = new VideoEditCutViewHolder(mContext, mRoot, mVideoDuration);
            mCutViewHolder.setActionListener(new VideoEditCutViewHolder.ActionListener() {
                @Override
                public void onHide() {
                    showGroup();
                    if (mPLayStatus != STATUS_PLAY) {
                        clickTogglePlay();
                    }
                }

                @Override
                public void onSeekChanged(long currentTimeMs) {
                    previewAtTime(currentTimeMs);
                }

                @Override
                public void onCutTimeChanged(long startTime, long endTime) {
                    mCutStartTime = startTime;
                    mCutEndTime = endTime;
                    if (mVideoEditer != null) {
                        mVideoEditer.setCutFromTime(startTime, endTime);
                    }
                }

                @Override
                public void onSpecialStart(int effect, long currentTimeMs) {
                    if (mVideoEditer != null) {
                        if (mPLayStatus == STATUS_NONE || mPLayStatus == STATUS_PREVIEW_AT_TIME) {
                            mVideoEditer.startPlayFromTime(mPreviewAtTime, mCutEndTime);
                        } else if (mPLayStatus == STATUS_PAUSE) {
                            mVideoEditer.resumePlay();
                        }
                        mPLayStatus = STATUS_PLAY;
                        mVideoEditer.startEffect(effect, currentTimeMs);
                    }
                    hidePlayBtn();
                }

                @Override
                public void onSpecialEnd(int effect, long currentTimeMs) {
                    if (mVideoEditer != null) {
                        mVideoEditer.pausePlay();
                        mPLayStatus = STATUS_PAUSE;
                        mVideoEditer.stopEffect(effect, currentTimeMs);
                    }
                    showPlayBtn();
                }

                @Override
                public void onSpecialCancel(long currentTimeMs) {
                    if (mVideoEditer != null) {
                        mVideoEditer.deleteLastEffect();
                        previewAtTime(currentTimeMs);
                    }
                }
            });
            mCutViewHolder.addToParent();
        }
        mCutViewHolder.show(showSpecial);
    }


    private void previewAtTime(long currentTimeMs) {
        if (mVideoEditer != null) {
            mVideoEditer.pausePlay();
            mVideoEditer.previewAtTime(currentTimeMs);
        }
        mPLayStatus = STATUS_PREVIEW_AT_TIME;
        mPreviewAtTime = currentTimeMs;
        showPlayBtn();
    }

    /**
     * 点击裁剪
     */
    private void clickCut() {
        hideGroup();
        showCutViewHolder(false);
//        mVideoEditer.setReverse(true);//TODO
    }

    /**
     * 点击特效
     */
    private void clickSpecial() {
        hideGroup();
        showCutViewHolder(true);
    }

    /**
     * 点击下一步，生成视频
     */
    private void clickNext() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.video_save_save,
                R.string.video_save_pub,
                R.string.video_save_save_and_pub
        }, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                switch (tag) {
                    case R.string.video_save_save:
                        mSaveType = Constants.VIDEO_SAVE_SAVE;
                        break;
                    case R.string.video_save_pub:
                        mSaveType = Constants.VIDEO_SAVE_PUB;
                        break;
                    case R.string.video_save_save_and_pub:
                        mSaveType = Constants.VIDEO_SAVE_SAVE_AND_PUB;
                        break;
                }
                startGenerateVideo();
            }
        });
    }

    /**
     * 开始生成视频
     */
    private void startGenerateVideo() {
        NLog.e(TAG, "startGenerateVideo------->生成视频");
        if (mVideoEditer == null) {
            return;
        }
        mBtnNext.setEnabled(false);
        mVideoEditer.stopPlay();
        mVideoEditer.cancel();
        mVideoGenerateViewHolder = new VideoProcessViewHolder(mContext, mRoot, "视频生成中");
        mVideoGenerateViewHolder.setActionListener(new VideoProcessViewHolder.ActionListener() {
            @Override
            public void onCancelProcessClick() {
                exit();
            }
        });
        mVideoGenerateViewHolder.addToParent();
        mVideoEditer.setCutFromTime(mCutStartTime, mCutEndTime);
        mGenerateVideoPath = StringUtil.generateVideoOutputPath();
        mVideoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mGenerateVideoPath);
    }

    private void showGroup() {
        if (mGroup != null && mGroup.getVisibility() != View.VISIBLE) {
            mGroup.setVisibility(View.VISIBLE);
        }
    }

    private void hideGroup() {
        if (mGroup != null && mGroup.getVisibility() == View.VISIBLE) {
            mGroup.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        DialogUitl.showSimpleDialog(mContext, "是否退出视频编辑", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                exit();
            }
        });
    }

    private void exit() {
        if (mCutViewHolder != null && mCutViewHolder.isShowed()) {
            mCutViewHolder.hide();
            return;
        }
        if (mVolumeViewHolder != null && mVolumeViewHolder.isShowed()) {
            mVolumeViewHolder.hide();
            return;
        }
        if (mFilterViewHolder != null && mFilterViewHolder.isShowed()) {
            mFilterViewHolder.hide();
            return;
        }
        release();
        deleteOriginVideoFile();
        VideoEditActivity.super.onBackPressed();
    }

    private void release() {
        if (mHandler != null) {
            mHandler.release();
        }
        if (mMetadataRetriever != null) {
            mMetadataRetriever.release();
        }
        if (mFilterViewHolder != null) {
            mFilterViewHolder.release();
        }
        if (mVolumeViewHolder != null) {
            mVolumeViewHolder.release();
        }
        if (mCutViewHolder != null) {
            mCutViewHolder.release();
        }
        if (mVideoEditer != null) {
            mVideoEditer.setBGM(null);//TODO
            mVideoEditer.stopPlay();
            mVideoEditer.cancel();
            mVideoEditer.setVideoProcessListener(null);
            mVideoEditer.setThumbnailListener(null);
            mVideoEditer.setTXVideoPreviewListener(null);
            mVideoEditer.setVideoGenerateListener(null);
        }
        if (mVideoProcessViewHolder != null) {
            mVideoProcessViewHolder.setActionListener(null);
        }
        if (mVideoGenerateViewHolder != null) {
            mVideoGenerateViewHolder.setActionListener(null);
        }
        if (mBitmapList != null) {
            for (Bitmap bitmap : mBitmapList) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            mBitmapList.clear();
        }
        mHandler = null;
        mMetadataRetriever = null;
        mFilterViewHolder = null;
        mVideoEditer = null;
        mVolumeViewHolder = null;
        mCutViewHolder = null;
        mVideoProcessViewHolder = null;
        mVideoGenerateViewHolder = null;
        mBitmapList = null;
        mMusicBean = null;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mVideoEditer != null && mPLayStatus == STATUS_PLAY) {
            mVideoEditer.pausePlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mVideoEditer != null && mPLayStatus == STATUS_PLAY) {
                mVideoEditer.resumePlay();
            }
        }
        mPaused = false;
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        NLog.e(TAG, "VideoEditActivity------->onDestroy");
    }

    /**
     * 开始预处理
     */
    private void startPreProcess() {
        mVideoProcessViewHolder = new VideoProcessViewHolder(mContext, mRoot, getString(R.string.video_process_1));
        mVideoProcessViewHolder.addToParent();
        mVideoProcessViewHolder.setActionListener(this);
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }

        try {
            TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(mContext).getVideoFileInfo(mOriginVideoPath);
            if (mHandler != null) {
                if (info == null) {
                    mHandler.sendEmptyMessage(MyHandler.ERROR);
                } else {
                    mHandler.sendEmptyMessage(MyHandler.SUCCESS);
                }
            }
        } catch (Exception e) {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(MyHandler.ERROR);
            }
        }

//        TODO
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(mContext).getVideoFileInfo(mOriginVideoPath);
//                    if (mHandler != null) {
//                        if (info == null) {
//                            mHandler.sendEmptyMessage(MyHandler.ERROR);
//                        } else {
//                            mHandler.sendEmptyMessage(MyHandler.SUCCESS);
//                        }
//                    }
//                } catch (Exception e) {
//                    if (mHandler != null) {
//                        mHandler.sendEmptyMessage(MyHandler.ERROR);
//                    }
//                }
//            }
//        }).start();
    }

    /**
     * 执行预处理
     */
    private void doPreProcess() {
        try {
            if (mVideoEditer != null) {
                mBitmapList = new ArrayList<>();
                int thumbnailCount = (int) Math.floor(mVideoDuration / 1000f);
                TXVideoEditConstants.TXThumbnail thumbnail = new TXVideoEditConstants.TXThumbnail();
                thumbnail.count = thumbnailCount;
                thumbnail.width = 60;
                thumbnail.height = 100;
                mVideoEditer.getThumbnail(thumbnail.count, thumbnail.width, thumbnail.height, false, this);

//                TODO
//                mVideoEditer.setThumbnail(thumbnail);
//                mVideoEditer.processVideo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            processFailed();
        }

    }


    /**
     * 录制结束后，视频预处理进度回调
     */
    @Override
    public void onProcessProgress(float progress) {
        int p = (int) (progress * 100);
        if (p > 0 && p <= 100) {
            if (mVideoProcessViewHolder != null) {
                mVideoProcessViewHolder.setProgress(p);
            }
        }
    }

    /**
     * 录制结束后，视频预处理的回调
     */
    @Override
    public void onProcessComplete(TXVideoEditConstants.TXGenerateResult result) {
        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
            NLog.e(TAG, "视频预处理----->完成");
            startVideoPreview();
            if (mVideoProcessViewHolder != null) {
                mVideoProcessViewHolder.removeFromParent();
                mVideoProcessViewHolder.setActionListener(null);
            }
            mVideoProcessViewHolder = null;
        } else {
            NLog.e(TAG, "视频预处理错误------->" + result.descMsg);
            processFailed();
        }
    }

    /**
     * 制结束后，获取缩略图的回调
     */
    @Override
    public void onThumbnail(int i, long l, Bitmap bitmap) {
        if (mBitmapList != null) {
            mBitmapList.add(new SoftReference<>(bitmap).get());
        }
    }


    /**
     * 点击取消预处理的按钮
     */
    @Override
    public void onCancelProcessClick() {
        NToast.shortToast(mContext, getString(R.string.video_process_cancel));
        deleteOriginVideoFile();
        release();
        finish();
    }


    /**
     * 预处理失败
     */
    private void processFailed() {
        deleteOriginVideoFile();
        NToast.shortToast(mContext, R.string.video_process_failed);
        release();
        finish();
    }


    public List<Bitmap> getBitmapList() {
        return mBitmapList;
    }


    private static class MyHandler extends Handler {

        private static final int SUCCESS = 1;
        private static final int ERROR = 0;

        private VideoEditActivity mVideoEditActivity;

        public MyHandler(VideoEditActivity recordActivity) {
            mVideoEditActivity = new WeakReference<>(recordActivity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mVideoEditActivity != null) {
                switch (msg.what) {
                    case SUCCESS:
                        mVideoEditActivity.doPreProcess();
                        break;
                    case ERROR:
                        mVideoEditActivity.processFailed();
                        break;
                }
            }
        }

        void release() {
            removeCallbacksAndMessages(null);
            mVideoEditActivity = null;
        }
    }
}

