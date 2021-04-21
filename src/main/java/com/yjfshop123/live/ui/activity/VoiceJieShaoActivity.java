package com.yjfshop123.live.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.RecordingItem;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.utils.PermissionsChecker;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.service.RecordingService;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.TimeUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoiceJieShaoActivity extends BaseActivity implements View.OnClickListener, RecordingService.OnStopAudioListener, CosXmlUtils.OSSResultListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.record_audio_fab_record)
    ImageView mFabRecord;
    @BindView(R.id.record_audio_chronometer_time)
    Chronometer mChronometerTime;

    @BindView(R.id.playFlag)
    TextView playFlag;

    @BindView(R.id.playLayout)
    LinearLayout playLayout;
    @BindView(R.id.playSeekBar)
    SeekBar playSeekBar;

    @BindView(R.id.currentSecond)
    TextView currentSecond;
    @BindView(R.id.totalMinute)
    TextView totalMinute;
    @BindView(R.id.optionLayout)
    RelativeLayout optionLayout;

    @BindView(R.id.resetAudio)
    ImageView resetAudio;
    @BindView(R.id.playAudio)
    ImageView playAudio;
    @BindView(R.id.saveAudio)
    ImageView saveAudio;

    private boolean mStartRecording = true;
    long timeWhenPaused = 0;

    private static MediaPlayer mMediaPlayer = null;

    private Handler mHandler = new Handler();

    long minutes = 0;
    long seconds = 0;

    private boolean isPlaying = false;

    public static String audioPath = "";
    private String userId;
    public static long elpased;
    private UploadDialog uploadDialog;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private PermissionsChecker mPermissionsChecker;
    private boolean isRequireCheck;

    static final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String sss = getRingDuring(audioPath);
                    int ff = Integer.parseInt(sss);
                    String frt = TimeUtil.format(ff);
                    totalMinute.setText(frt);
                    hidShowView(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_jieshao);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
        mPermissionsChecker = new PermissionsChecker(this);
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.voice_jieshao);

        mFabRecord.setOnClickListener(this);

        resetAudio.setOnClickListener(this);
        playAudio.setOnClickListener(this);
        saveAudio.setOnClickListener(this);

//        userId = getIntent().getStringExtra("userId");
//        audioStr = getIntent().getStringExtra("audioStr");

        userId = UserInfoUtil.getMiTencentId();
        String audioStr = UserInfoUtil.getSpeechIntroduction();


        uploadDialog = new UploadDialog(this);

        utils = new CosXmlUtils(this);
        utils.setOssResultListener(this);

        if (!TextUtils.isEmpty(audioStr)) {
            audioPath = audioStr;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }).start();

        }

//        if (!audioPath.equals("") || audioPath != null) {
//            mFabRecord.setVisibility(View.GONE);
//            optionLayout.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void onBackPressed() {
        if (optionLayout != null && optionLayout.getVisibility() == View.VISIBLE) {

            DialogUitl.showSimpleHintDialog(this, "", "保存", "退出",
                    "是否保存录音？", true, false,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {
                            VoiceJieShaoActivity.super.onBackPressed();
                        }

                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            if (audioPath.startsWith("http://") || audioPath.startsWith("https://")) {
//                    NToast.shortToast(this, "您还没有重新录制语音介绍");
                                finish();
                                return;
                            }
                            paths = new ArrayList<>();
                            paths.add(audioPath);
                            uploadDialog.show(300);
                            uploadDialog.uploadVoice();

                            uploadData();
                        }
                    });
        } else {
            super.onBackPressed();
        }

    }

    RecordingService recordingService;
    private CosXmlUtils utils;

    private void onRecord(boolean start) {
        recordingService = new RecordingService();
        recordingService.setStopAudioListener(this);
//        Intent intent = new Intent(this, RecordingService.class);
        if (start) {

            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder/");
            if (!folder.exists()) {
                folder.mkdir();
            }
            mChronometerTime.setBase(SystemClock.elapsedRealtime());
            mChronometerTime.start();
//            this.startService(intent);
            recordingService.startRecording();
            Toast.makeText(this, R.string.voice_start, Toast.LENGTH_SHORT).show();
            mChronometerTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    Log.e("ZTY", "11111111");
                    // 如果从开始计时到现在超过了60s
                    if (SystemClock.elapsedRealtime() - chronometer.getBase() > 60 * 1000) {
                        onRecord(false);
                        chronometer.stop();
                    }
                }
            });

        } else {
            String[] time = mChronometerTime.getText().toString().split(":");
            int minute = Integer.parseInt(time[0]);
            int second = Integer.parseInt(time[1]);
            if (second < 10 && minute < 1) {
                Toast.makeText(this, R.string.voice_cancel, Toast.LENGTH_SHORT).show();
                mChronometerTime.stop();
                mChronometerTime.setBase(SystemClock.elapsedRealtime());//计时器清零
                mFabRecord.setImageDrawable(getResources().getDrawable(R.drawable.voice_pre));
                recordingService.stopRecording();
                return;
            }
            if (minute >= 1 || second >= 10) {
                mChronometerTime.stop();
                timeWhenPaused = 0;
                Toast.makeText(this, R.string.voice_over, Toast.LENGTH_SHORT).show();
//                this.stopService(intent);
                recordingService.stopRecording();
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                hidShowView(true);
                hidShowView(true);
            }
        }
    }

    public static String getRingDuring(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    private void allPermissionsGranted() {
        if (mStartRecording) {
            onRecord(mStartRecording);//开始录音
            mStartRecording = !mStartRecording;
            mFabRecord.setImageDrawable(getResources().getDrawable(R.drawable.voice_stop));
        } else {
            onRecord(mStartRecording);//结束录音
            mStartRecording = !mStartRecording;
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_audio_fab_record:
                try {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted(); // 全部权限都已获取
                    } else {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO});
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
//                    NToast.shortToast(this,"权限异常");
                    showMissingPermissionDialog();
//                    finish();
                }
//                if (mPermissionsChecker.lacksPermissions(permissions)) {
//                    requestPermissions(permissions); // 请求权限
//                } else {
//                    allPermissionsGranted(); // 全部权限都已获取
//                }
                break;
            case R.id.resetAudio:
                audioPath = "";
                elpased = 0;
                playSeekBar.setProgress(0);
                currentSecond.setText("00:00");
                mFabRecord.setImageDrawable(getResources().getDrawable(R.drawable.voice_pre));
                mChronometerTime.setBase(SystemClock.elapsedRealtime());//计时器清零
                hidShowView(false);
                isPlaying = !isPlaying;
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer = null;
                }
                playAudio.setImageDrawable(getResources().getDrawable(R.drawable.voice_play));
                isPlaying = !isPlaying;
                break;
            case R.id.playAudio:
                if (!isPlaying) {
                    playAudio.setImageDrawable(getResources().getDrawable(R.drawable.voice_stop));
                } else {
                    playAudio.setImageDrawable(getResources().getDrawable(R.drawable.voice_play));
                }
                onPlay(isPlaying);
                isPlaying = !isPlaying;
                break;
            case R.id.saveAudio:
                if (audioPath.startsWith("http://") || audioPath.startsWith("https://")) {
//                    NToast.shortToast(this, "您还没有重新录制语音介绍");
                    finish();
                    return;
                }
                paths = new ArrayList<>();
                paths.add(audioPath);
                uploadDialog.show(300);
                uploadDialog.uploadVoice();

                uploadData();

//                UploadOSSTask task = new UploadOSSTask(this, paths, userId, uploadDialog);
//                task.execute();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.sendEmptyMessage(1);
//                    }
//                }).start();
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                uploadDialog.uploadVoice(paths, userId);
//                    }
//                }, 1000);
                break;
        }
    }

    private void uploadData() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        utils.uploadData(audioPath, "audio", 3, userId, uploadDialog);
    }

//    String onLineVoicePath;

    ArrayList<String> paths;

    private void hidShowView(boolean flag) {
        if (flag) {
            mFabRecord.setVisibility(View.GONE);
            playFlag.setText(R.string.voice_play);
            mChronometerTime.setVisibility(View.GONE);

            playLayout.setVisibility(View.VISIBLE);
            optionLayout.setVisibility(View.VISIBLE);
//            playVideo();
        } else {
            mFabRecord.setVisibility(View.VISIBLE);
            playFlag.setText(R.string.voice_luyin);
            mChronometerTime.setVisibility(View.VISIBLE);

            playLayout.setVisibility(View.GONE);
            optionLayout.setVisibility(View.GONE);
        }
    }

    RecordingItem recordingItem = new RecordingItem();

    private void playVideo() {
//        SharedPreferences sharePreferences = getSharedPreferences("sp_name_audio", MODE_PRIVATE);
//        final String filePath = sharePreferences.getString("audio_path", "");
//        long elpased = sharePreferences.getLong("elpased", 0);
//        recordingItem.setFilePath(audioPath);
//        recordingItem.setLength((int) elpased);

        minutes = TimeUnit.MILLISECONDS.toMinutes(elpased);
        seconds = TimeUnit.MILLISECONDS.toSeconds(elpased) - TimeUnit.MINUTES.toSeconds(minutes);
        totalMinute.setText(String.format("%02d:%02d", minutes, minutes == 1 ? 0 : seconds));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onRecord(mStartRecording);
                }
                break;
        }
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            allPermissionsGranted();
        } else {
            showMissingPermissionDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("录音是否结束", "结束了");
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    // Play start/stop
    private void onPlay(boolean isPlaying) {
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if (mMediaPlayer == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            pausePlaying();
        }
    }

    private void resumePlaying() {
//        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

    private void pausePlaying() {
//        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    private void startPlaying() {
//        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(audioPath);
            mMediaPlayer.prepare();
            playSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                playAudio.setImageDrawable(getResources().getDrawable(R.drawable.voice_play));
            }
        });

        updateSeekBar();

        //keep screen on while playing audio
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopPlaying() {
//        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (recordingService != null)
            recordingService.stopRecording();
        playSeekBar.setProgress(playSeekBar.getMax());
        isPlaying = !isPlaying;

        currentSecond.setText(totalMinute.getText());
        playSeekBar.setProgress(playSeekBar.getMax());

        //allow the screen to turn off again once audio is finished playing
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //updating mSeekBar
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                playSeekBar.setProgress(mCurrentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) - TimeUnit.MINUTES.toSeconds(minutes);
                currentSecond.setText(String.format("%02d:%02d", minutes, seconds));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1);
    }

    @Override
    public void stopAudio() {
        playVideo();
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        String fullUrl = response.get(0).getFull_url();
        Intent intent = new Intent();
        intent.putExtra("url", fullUrl);
        setResult(14, intent);
        finish();
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        onBackPressed();
    }
}
