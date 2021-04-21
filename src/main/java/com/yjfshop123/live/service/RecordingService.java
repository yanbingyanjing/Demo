package com.yjfshop123.live.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.ui.activity.VoiceJieShaoActivity;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * 录音的 Service
 * <p>
 * Created by developerHaoz on 2017/8/12.
 */

public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private static String mFileName = null;
    private static String mFilePath = null;

    private static MediaRecorder mRecorder = null;

    private static long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private TimerTask mIncrementTimerTask = null;

    private OnStopAudioListener stopAudioListener;

    public interface OnStopAudioListener {
        void stopAudio();
    }

    public void setStopAudioListener(OnStopAudioListener stopAudioListener) {
        this.stopAudioListener = stopAudioListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setOutputFile(mFilePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioChannels(1);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ){
                mRecorder.setAudioSamplingRate(11025);
            }
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath() {
        int count = 0;
        File f;
        do {
            count++;
            mFileName = "/MyRecording" + "_" + (System.currentTimeMillis()) + ".mp3";
            mFilePath = Const.voiceDir;
            mFilePath += mFileName;
            f = new File(mFilePath);
        } while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        try {
            mRecorder.stop();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mRecorder.release();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        getSharedPreferences("sp_name_audio", MODE_PRIVATE)
//                .edit()
//                .putString("audio_path", mFilePath)
//                .putLong("elpased", mElapsedMillis)
//                .apply();

        VoiceJieShaoActivity.audioPath = mFilePath;
        VoiceJieShaoActivity.elpased = mElapsedMillis;

        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;

        stopAudioListener.stopAudio();
    }

}
