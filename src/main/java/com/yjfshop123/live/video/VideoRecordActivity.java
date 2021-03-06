package com.yjfshop123.live.video;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyLvJingFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyTieZhiFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.IBeauty;
import com.yjfshop123.live.live.live.common.widget.beautysetting.TCBeautyHelperNew;
import com.yjfshop123.live.live.live.common.widget.beautysetting.VideoMaterialMetaData;
import com.yjfshop123.live.live.live.common.widget.gift.utils.Constants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.StringUtil;
import com.yjfshop123.live.live.live.common.widget.gift.view.DrawableRadioButton2;
import com.yjfshop123.live.live.live.common.widget.gift.view.DrawableTextView;
import com.yjfshop123.live.live.live.common.widget.music.LiveMusicDialogFragment;
import com.yjfshop123.live.live.live.common.widget.music.SoundDialogFragment;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.utils.TimeUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.view.RecordProgressView;
import com.yjfshop123.live.video.view.VideoRecordBtnView;
import com.faceunity.FURenderer;
import com.faceunity.entity.Filter;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCPartsManager;
import com.tencent.ugc.TXUGCRecord;

import java.util.ArrayList;
import java.util.List;

public class VideoRecordActivity extends AbsActivity implements
        TXRecordCommon.ITXVideoRecordListener, //????????????????????????
        TXUGCRecord.VideoCustomProcessListener, //?????????????????????????????????????????????????????????
        FURenderer.OnTrackingStatusChangedListener,
        IBeauty,
        TXRecordCommon.ITXBGMNotify,
        IPermissions
{

    private static final String TAG = "VideoRecordActivity";
    private static final int MIN_DURATION = 5000;//??????????????????5s
    private static final int MAX_DURATION = 15000;//??????????????????15s
    //??????????????????
    private VideoRecordBtnView mVideoRecordBtnView;
    private View mRecordView;
    private ValueAnimator mRecordBtnAnimator;//????????????????????????????????????
    private Drawable mRecordDrawable;
    private Drawable mUnRecordDrawable;

    /****************************/
    private boolean mRecordStarted;//????????????????????????true ????????? false ????????????
    private boolean mRecordStoped;//?????????????????????
    private boolean mRecording;//????????????????????????true ????????? false ????????????
    private ViewGroup mRoot;
    private TXCloudVideoView mVideoView;//????????????
    private RecordProgressView mRecordProgressView;//???????????????
    private TextView mTime;//????????????
    private DrawableRadioButton2 mBtnFlash;//???????????????
    private TXUGCRecord mRecorder;//?????????
    private TXRecordCommon.TXUGCCustomConfig mCustomConfig;//??????????????????
    private boolean mFrontCamera = true;//????????????????????????
    private String mVideoPath;//?????????????????????
    private int mRecordSpeed;//????????????
    private View mGroup1;
    private View mGroup2;
    private View mGroup3;
    private View mGroup4;
    private View mBtnNext;//????????????????????????
    private Dialog mStopRecordDialog;//????????????????????????dialog
    private boolean mIsReachMaxRecordDuration;//??????????????????????????????
    private long mDuration;//?????????????????????
    private boolean mHasBgm;
    private boolean mBgmPlayStarted;//???????????????????????????????????????
    private long mRecordTime;

    private TCBeautyHelperNew mBeautyHepler;
    private BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi;
    private String proMeiFu;
    private String proMeiXing;
    private BeautyLvJingFragment.BeautyParams beautyParamsLvJing;
    private FURenderer mFURenderer;
    private boolean mOnFirstCreate = true;
    private Handler mHandler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_record;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //??????????????????
        mVideoRecordBtnView = (VideoRecordBtnView) findViewById(R.id.record_btn_view);
        mRecordView = findViewById(R.id.record_view);
        mUnRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_1);
        mRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_2);
        mRecordBtnAnimator = ValueAnimator.ofFloat(100, 0);
        mRecordBtnAnimator.setDuration(500);
        mRecordBtnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mVideoRecordBtnView != null) {
                    mVideoRecordBtnView.setRate((int) v);
                }
            }
        });
        mRecordBtnAnimator.setRepeatCount(-1);
        mRecordBtnAnimator.setRepeatMode(ValueAnimator.REVERSE);

        /****************************/
        mRoot = (ViewGroup) findViewById(R.id.root);
        mGroup1 = findViewById(R.id.group_1);
        mGroup2 = findViewById(R.id.group_2);
        mGroup3 = findViewById(R.id.group_3);
        mGroup4 = findViewById(R.id.group_4);
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
//        mVideoView.enableHardwareDecode(true); //?????????????????? ##
        mTime = findViewById(R.id.time);
        mRecordProgressView = (RecordProgressView) findViewById(R.id.record_progress_view);
        mRecordProgressView.setMaxDuration(MAX_DURATION);
        mRecordProgressView.setMinDuration(MIN_DURATION);
        mBtnFlash = (DrawableRadioButton2) findViewById(R.id.btn_flash);
        mBtnNext = findViewById(R.id.btn_next);
        initCameraRecord();
        PermissionsUtils.initPermission(mContext);
    }

    /**
     * ??????????????????
     */
    private void initCameraRecord() {
        mRecorder = TXUGCRecord.getInstance(App.getInstance());
        mRecorder.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
        mRecorder.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mRecordSpeed = TXRecordCommon.RECORD_SPEED_NORMAL;
        mRecorder.setRecordSpeed(mRecordSpeed);
        mRecorder.setAspectRatio(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);
        mCustomConfig = new TXRecordCommon.TXUGCCustomConfig();
        mCustomConfig.videoResolution = TXRecordCommon.VIDEO_RESOLUTION_1080_1920;
        mCustomConfig.minDuration = MIN_DURATION;
        mCustomConfig.maxDuration = MAX_DURATION;
        mCustomConfig.videoBitrate = 6500;
        mCustomConfig.videoGop = 3;
        mCustomConfig.videoFps = 20;
        mCustomConfig.isFront = mFrontCamera;
        mRecorder.setVideoRecordListener(this);
        mRecorder.setBGMNofify(this);

        initBeauty();
    }

    private void initBeauty(){
        mFURenderer = new FURenderer
                .Builder(mContext)
                .createEGLContext(false)
                .maxFaces(2)
                .setNeedFaceBeauty(true)
                .inputPropOrientation(0)
                .setOnTrackingStatusChangedListener(this)
                .build();
        mBeautyHepler = new TCBeautyHelperNew(mFURenderer);

        beautyParamsTieZhi = new BeautyTieZhiFragment.BeautyParams();
        String tiezhi_path = UserInfoUtil.getTiezhiPath();
        if (TextUtils.isEmpty(tiezhi_path)){
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video_none", "video_none", "", "");
        }else {
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video", tiezhi_path, "", "");
        }
        String miefu = UserInfoUtil.getMeifu();
        if (TextUtils.isEmpty(miefu)){
            proMeiFu = "0@@20@@20@@20@@20@@20";
        }else {
            proMeiFu = miefu;
        }
        String miexing = UserInfoUtil.getMeiXing();
        if (TextUtils.isEmpty(miexing)){
            proMeiXing = "0@@30@@30@@30@@30@@30@@30@@30@@30@@30";
        }else {
            proMeiXing = miexing;
        }
        int filterProgress = UserInfoUtil.getFilterProgress();
        String filterStr = UserInfoUtil.getFilterStr();
        beautyParamsLvJing = new BeautyLvJingFragment.BeautyParams();
        beautyParamsLvJing.mFilterProgress = filterProgress;
        if (TextUtils.isEmpty(filterStr)){
            beautyParamsLvJing.mFilter = Filter.Key.ORIGIN;
        }else {
            beautyParamsLvJing.mFilter = filterStr;
        }
        mBeautyHepler.init(beautyParamsTieZhi, proMeiFu, proMeiXing, beautyParamsLvJing);
    }

    @Override
    public void onTrackingStatusChanged(int status) {
        //status <= 0  ??????????????????
    }

    /**
     * ????????????
     */
    @Override
    public void onRecordEvent(int event, Bundle bundle) {
        if (event == TXRecordCommon.EVT_ID_PAUSE) {
            if (mRecordProgressView != null) {
                mRecordProgressView.clipComplete();
            }
        } else if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
            NToast.shortToast(mContext, getString(R.string.video_record_camera_failed));
        } else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
            NToast.shortToast(mContext, getString(R.string.video_record_audio_failed));
        }
    }

    /**
     * ???????????? ????????????
     */
    @Override
    public void onRecordProgress(long milliSecond) {
        if (mRecordProgressView != null) {
            mRecordProgressView.setProgress((int) milliSecond);
        }
        if (mTime != null) {
            mTime.setText(String.format("%.2f", milliSecond / 1000f) + "s");
        }
        mRecordTime = milliSecond;
        if (milliSecond >= MIN_DURATION) {
            if (mBtnNext != null && mBtnNext.getVisibility() != View.VISIBLE) {
                mBtnNext.setVisibility(View.VISIBLE);
            }
        }
        if (milliSecond >= MAX_DURATION) {
            if (!mIsReachMaxRecordDuration) {
                mIsReachMaxRecordDuration = true;
                if (mRecordBtnAnimator != null) {
                    mRecordBtnAnimator.cancel();
                }
                showProccessDialog();
            }
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
        hideProccessDialog();
        mRecordStarted = false;
        mRecordStoped = true;
        if (mRecorder != null) {
            mRecorder.toggleTorch(false);
            mRecorder.stopBGM();
            mDuration = mRecorder.getPartsManager().getDuration();
        }
        if (result.retCode < 0) {
            release();
            NToast.shortToast(mContext, getString(R.string.video_record_failed));
        } else {
            //??????????????????
            //TODO ???????????????????????????
            VideoEditActivity.forward(mContext, mDuration, mVideoPath, true, mHasBgm);
        }
        //finish();
    }

    //######################??????????????????##########
    /**
     * ???????????????????????????
     */
    @Override
    public int onTextureCustomProcess(int i, int i1, int i2) {
        Log.d("?????????????????????","1");
        if (mOnFirstCreate) {
            mFURenderer.onSurfaceCreated();
            mOnFirstCreate = false;
        }
        int texId = mFURenderer.onDrawFrame(i, i1, i2);
        return texId;
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onDetectFacePoints(float[] floats) {

    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onTextureDestroyed() {
        mFURenderer.onSurfaceDestroyed();
        mOnFirstCreate = true;
    }
    //######################??????????????????##########

    public void recordClick(View v) {
        if (mRecordStoped || !canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_start_record) {
            clickRecord();

        } else if (i == R.id.btn_camera) {
            clickCamera();

        } else if (i == R.id.btn_flash) {
            clickFlash();

        } else if (i == R.id.btn_beauty) {
            clickBeauty();

        } else if (i == R.id.btn_music) {
            clickMusic();

        } else if (i == R.id.btn_speed_1) {
            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOWEST);

        } else if (i == R.id.btn_speed_2) {
            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOW);

        } else if (i == R.id.btn_speed_3) {
            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_NORMAL);

        } else if (i == R.id.btn_speed_4) {
            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FAST);

        } else if (i == R.id.btn_speed_5) {
            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FASTEST);

        } else if (i == R.id.btn_upload) {
            clickUpload();

        } else if (i == R.id.btn_delete) {
            clickDelete();

        } else if (i == R.id.btn_next) {
            clickNext();

        }
    }

    /**
     * ???????????????
     */
    private void clickCamera() {
        if (mRecorder != null) {
            if (mBtnFlash != null && mBtnFlash.isChecked()) {
                mBtnFlash.doToggle();
                mRecorder.toggleTorch(mBtnFlash.isChecked());
            }
            mFrontCamera = !mFrontCamera;
            mRecorder.switchCamera(mFrontCamera);

            if (mFURenderer != null){
                /*???????????????*/
                mFURenderer.onCameraChange(mFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                        Camera.CameraInfo.CAMERA_FACING_BACK, 0);
            }
        }
    }

    /**
     * ???????????????
     */
    private void clickFlash() {
        if (mFrontCamera) {
            NToast.shortToast(mContext, getString(R.string.live_open_flash));
            return;
        }
        if (mBtnFlash != null) {
            mBtnFlash.doToggle();
            if (mRecorder != null) {
                mRecorder.toggleTorch(mBtnFlash.isChecked());
            }
        }
    }

    /**
     * ????????????
     */
    private void clickBeauty() {
        if (mBeautyHepler.isAddedMain()){
            mBeautyHepler.dismissMain();
        } else {
            mBeautyHepler.showMain(getFragmentManager(), "BeautyMainFragment");
        }
    }

    /**
     * ????????????
     */
    private void clickMusic() {
        LiveMusicDialogFragment fragmentMusic = new LiveMusicDialogFragment();
        fragmentMusic.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    /**
     * ?????????????????????????????????
     */
    private void clickUpload() {
        Intent intent = new Intent(mContext, VideoChooseActivity.class);
        intent.putExtra(Constants.VIDEO_DURATION, MAX_DURATION);
        startActivityForResult(intent, 0);
    }

    /**
     * ???????????????????????????
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            mVideoPath = data.getStringExtra(Constants.VIDEO_PATH);
            mDuration = data.getLongExtra(Constants.VIDEO_DURATION, 0);
            VideoEditActivity.forward(mContext, mDuration, mVideoPath, false, false);
            //TODO ???????????????????????????
            //finish();

        }
    }


    /**
     * ????????????
     */
    private void startCameraPreview() {
        if (mRecorder != null && mCustomConfig != null && mVideoView != null) {
            mRecorder.startCameraCustomPreview(mCustomConfig, mVideoView);
            if (!mFrontCamera) {
                mRecorder.switchCamera(false);
            }

            //????????????????????????
            mOnFirstCreate = true;
            mRecorder.setVideoProcessListener(this);
        }
    }

    /**
     * ????????????
     */
    private void stopCameraPreview() {
        if (mRecorder != null) {
            if (mRecording) {
                pauseRecord();
            }
            mRecorder.stopCameraPreview();
            mRecorder.setVideoProcessListener(null);
        }
    }

    /**
     * ????????????
     */
    private void clickRecord() {
        if (mRecordStarted) {
            if (mRecording) {
                pauseRecord();
            } else {
                resumeRecord();
            }
        } else {
            startRecord();
        }
    }

    /**
     * ????????????
     */
    private void startRecord() {
        if (mRecorder != null) {
            mVideoPath = StringUtil.generateVideoOutputPath();//?????????????????????
            int result = mRecorder.startRecord(mVideoPath, "");//???????????????????????????????????????
            if (result != TXRecordCommon.START_RECORD_OK) {
                if (result == TXRecordCommon.START_RECORD_ERR_NOT_INIT) {
                    NToast.shortToast(mContext, getString(R.string.video_record_tip_1));
                } else if (result == TXRecordCommon.START_RECORD_ERR_IS_IN_RECORDING) {
                    NToast.shortToast(mContext, getString(R.string.video_record_tip_2));
                } else if (result == TXRecordCommon.START_RECORD_ERR_VIDEO_PATH_IS_EMPTY) {
                    NToast.shortToast(mContext, getString(R.string.video_record_tip_3));
                } else if (result == TXRecordCommon.START_RECORD_ERR_API_IS_LOWER_THAN_18) {
                    NToast.shortToast(mContext, getString(R.string.video_record_tip_4));
                } else if (result == TXRecordCommon.START_RECORD_ERR_LICENCE_VERIFICATION_FAILED) {
                    NToast.shortToast(mContext, getString(R.string.video_record_tip_5));
                }
                return;
            }
        }
        mRecordStarted = true;
        mRecording = true;
        resumeBgm();
        startRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
            mGroup2.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * ????????????
     */
    private void pauseRecord() {
        if (mRecorder == null) {
            return;
        }
        pauseBgm();
        mRecorder.pauseRecord();
        mRecording = false;
        stopRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
            mGroup2.setVisibility(View.VISIBLE);
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager != null) {
            List<String> partList = partsManager.getPartsPathList();
            if (partList != null && partList.size() > 0) {
                if (mGroup3 != null && mGroup3.getVisibility() == View.VISIBLE) {
                    mGroup3.setVisibility(View.INVISIBLE);
                }
                if (mGroup4 != null && mGroup4.getVisibility() != View.VISIBLE) {
                    mGroup4.setVisibility(View.VISIBLE);
                }
            } else {
                if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
                    mGroup3.setVisibility(View.VISIBLE);
                }
                if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
                    mGroup4.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * ????????????
     */
    private void resumeRecord() {
        if (mRecorder != null) {
            int startResult = mRecorder.resumeRecord();
            if (startResult != TXRecordCommon.START_RECORD_OK) {
                NToast.shortToast(mContext, getString(R.string.video_record_failed));
                return;
            }
        }
        mRecording = true;
        resumeBgm();
        startRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
            mGroup2.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * ??????????????????
     */
    private void pauseBgm() {
        if (mRecorder != null) {
            mRecorder.pauseBGM();
        }
    }

    /**
     * ??????????????????
     */
    private void resumeBgm() {
        if (mRecorder == null) {
            return;
        }
        if (!mBgmPlayStarted) {
            if (mFilePath == null) {
                return;
            }
            int bgmDuration = mRecorder.setBGM(mFilePath);
            mRecorder.playBGMFromTime(0, bgmDuration);
            mRecorder.setBGMVolume(mBGMVolume);
            mRecorder.setMicVolume(mMicVolume);
            mBgmPlayStarted = true;
            mHasBgm = true;
        } else {
            mRecorder.resumeBGM();
        }
    }

    /**
     * ????????????????????????
     */
    private void startRecordBtnAnim() {
        if (mRecordView != null) {
            mRecordView.setBackground(mRecordDrawable);
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.start();
        }
    }

    /**
     * ????????????????????????
     */
    private void stopRecordBtnAnim() {
        if (mRecordView != null) {
            mRecordView.setBackground(mUnRecordDrawable);
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.cancel();
        }
        if (mVideoRecordBtnView != null) {
            mVideoRecordBtnView.reset();
        }
    }

    /**
     * ??????????????????
     */
    private void changeRecordSpeed(int speed) {
        if (mRecordSpeed == speed) {
            return;
        }
        mRecordSpeed = speed;
        if (mRecorder != null) {
            mRecorder.setRecordSpeed(speed);
        }
    }

    /**
     * ??????????????????
     */
    private void clickDelete() {
        DialogUitl.showSimpleDialog(mContext, getString(R.string.video_record_delete_last), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                doClickDelete();
            }
        });
    }

    /**
     * ??????????????????
     */
    private void doClickDelete() {
        if (!mRecordStarted || mRecording || mRecorder == null) {
            return;
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager == null) {
            return;
        }
        List<String> partList = partsManager.getPartsPathList();
        if (partList == null || partList.size() == 0) {
            return;
        }
        partsManager.deleteLastPart();
        int time = partsManager.getDuration();
        if (mTime != null) {
            mTime.setText(String.format("%.2f", time / 1000f) + "s");
        }
        mRecordTime = time;
        if (time < MIN_DURATION && mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
            mBtnNext.setVisibility(View.INVISIBLE);
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.deleteLast();
        }
        partList = partsManager.getPartsPathList();
        if (partList != null && partList.size() == 0) {
            if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
                mGroup2.setVisibility(View.VISIBLE);
            }
            if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
                mGroup3.setVisibility(View.VISIBLE);
            }
            if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
                mGroup4.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * ???????????????????????? onRecordComplete
     */
    public void clickNext() {
        stopRecordBtnAnim();
        if (mRecorder != null) {
            mRecorder.stopBGM();
            mRecorder.stopRecord();
            showProccessDialog();
        }
    }


    /**
     * ??????????????????????????????????????????
     */
    private void showProccessDialog() {
        if (mStopRecordDialog == null) {
            mStopRecordDialog = DialogUitl.loadingDialog(mContext, getString(R.string.video_processing));
            mStopRecordDialog.show();
        }
    }

    /**
     * ????????????????????????
     */
    private void hideProccessDialog() {
        if (mStopRecordDialog != null) {
            mStopRecordDialog.dismiss();
        }
        mStopRecordDialog = null;
    }


    @Override
    public void onBackPressed() {
        if (!canClick()) {
            return;
        }
        List<Integer> list = new ArrayList<>();
        if (mRecordTime > 0) {
            list.add(R.string.video_re_record);
        }
        list.add(R.string.video_exit);
        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.video_re_record) {
                    reRecord();
                } else if (tag == R.string.video_exit) {
                    exit();
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void reRecord() {
        if (mRecorder == null) {
            return;
        }
        mRecorder.pauseBGM();
        mHasBgm = false;
        mBgmPlayStarted = false;
        mRecorder.pauseRecord();
        mRecording = false;
        stopRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
            mGroup2.setVisibility(View.VISIBLE);
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager != null) {
            partsManager.deleteAllParts();
        }
        mRecorder.onDeleteAllParts();
        if (mTime != null) {
            mTime.setText("0.00s");
        }
        mRecordTime = 0;
        if (mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
            mBtnNext.setVisibility(View.INVISIBLE);
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.deleteAll();
        }
        if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
            mGroup3.setVisibility(View.VISIBLE);
        }
        if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
            mGroup4.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * ??????
     */
    private void exit() {
        release();
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        PermissionsUtils.onResume(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCameraPreview();
        if (mRecorder != null && mBtnFlash != null && mBtnFlash.isChecked()) {
            mBtnFlash.doToggle();
            mRecorder.toggleTorch(mBtnFlash.isChecked());
        }
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        NLog.e(TAG, "-------->onDestroy");
    }


    public void release() {
        if (mStopRecordDialog != null && mStopRecordDialog.isShowing()) {
            mStopRecordDialog.dismiss();
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.release();
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.cancel();
        }
        if (mRecorder != null) {
            mRecorder.toggleTorch(false);
            mRecorder.stopBGM();
            if (mRecordStarted) {
                mRecorder.stopRecord();
            }
            mRecorder.stopCameraPreview();
            mRecorder.setVideoProcessListener(null);
            mRecorder.setVideoRecordListener(null);
            TXUGCPartsManager getPartsManager = mRecorder.getPartsManager();
            if (getPartsManager != null) {
                getPartsManager.deleteAllParts();
            }
            mRecorder.release();
        }

        closeMusic();
        if (mRecorder != null){
            mRecorder.setBGMNofify(null);
        }

        mStopRecordDialog = null;
        mRecordProgressView = null;
        mRecordBtnAnimator = null;
        mRecorder = null;
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

    private FrameLayout mLiveMusicFl;
    private DrawableTextView mLiveMusicNmae;
    private TextView mLiveMusicTime, mLiveRoomMusicSound;
//    private boolean isStop = false;

    public void closeMusic(){
        mFilePath = null;
        mBgmPlayStarted = false;
        if (mRecorder != null){
            mRecorder.stopBGM();
        }
        if (mLiveMusicFl != null){
            mLiveMusicFl.setVisibility(View.GONE);
        }
//        isStop = false;
    }

    private void showMusic(String musicName){
        if (mLiveMusicFl == null){
            mLiveMusicFl = (FrameLayout) findViewById(R.id.live_room_music_fl);
        }
        mLiveMusicFl.setVisibility(View.VISIBLE);

//        isStop = false;
        if (mLiveMusicNmae == null){
            mLiveMusicNmae = (DrawableTextView) findViewById(R.id.live_room_music_name);
        }
        mLiveMusicNmae.setText(musicName);
        mLiveMusicNmae.setLeftDrawable(mContext.getResources().getDrawable(R.drawable.live_room_start));

        if (mLiveMusicTime == null){
            mLiveMusicTime = (TextView) findViewById(R.id.live_room_music_time);
        }

        if (mLiveRoomMusicSound == null){
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

    private void setLiveMusicTime(long l, long l1){
        if (mLiveMusicTime != null){
            mLiveMusicTime.setText(TimeUtil.format_((int) l) + "/" + TimeUtil.format_((int) l1));
        }
    }

    public void onBGMStart_(String filePath, String musicName){
        showMusic(musicName);
        mFilePath = filePath;
        mBgmPlayStarted = false;
    }

    @Override
    public void onBGMStart() {
    }

    @Override
    public void onBGMProgress(final long l, final long l1) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setLiveMusicTime(l, l1);
            }
        });
    }

    @Override
    public void onBGMComplete(int i) {
    }

    private String mFilePath = null;
    private int mMicVolume = 100;
    private int mBGMVolume = 100;

    public void setBGMVolume(int volume){
        mBGMVolume = volume;
        if (mRecorder != null){
            mRecorder.setBGMVolume(volume);
        }
    }

    public void setMicVolumeOnMixing(int volume){
        mMicVolume = volume;
        if (mRecorder != null){
            mRecorder.setMicVolume(volume);
        }
    }

    @Override
    public void allPermissions() {
        startCameraPreview();
    }
}

