package com.yjfshop123.live.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class MediaManager {

    private static MediaPlayer mMediaPlayer;  //播放录音文件
//    private static boolean isPause = false;

    static {
        if(mMediaPlayer==null){
            mMediaPlayer=new MediaPlayer();
            mMediaPlayer.setOnErrorListener( new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }
    }


    /**
     * 播放音频
     * @param filePath
     * @param onCompletionListenter
     */
    public static int playSound(Context context, String filePath, MediaPlayer.OnCompletionListener onCompletionListenter){

        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener( new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else{
            mMediaPlayer.reset();
        }
        try {
            //详见“MediaPlayer”调用过程图
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListenter);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.isLooping ();

//            isPause=true;
            return mMediaPlayer.getDuration();
//            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 暂停
     */
    public synchronized static void pause(){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
//            isPause=true;
        }
    }

    //停止
    public synchronized static void stop(){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
//            isPause=false;
        }
    }

    /**
     * resume继续
     */
    public synchronized static void resume(){
        if(mMediaPlayer!=null /*&& isPause*/){
            mMediaPlayer.start();
//            isPause=false;
        }
    }

//    public static boolean isPause(){
//        return isPause;
//    }
//
//    public static void setPause(boolean isPause) {
//        MediaManager.isPause = isPause;
//    }

    /**
     * release释放资源
     */
    public static void release(){
        if(mMediaPlayer!=null){
//            isPause = false;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 重置
     */
    public synchronized static void reset(){
        if(mMediaPlayer!=null) {
//            mMediaPlayer.seekTo(0);
            mMediaPlayer.reset();
//            isPause = true;
        }
    }

    /**
     * 判断是否在播放视频
     * @return
     */
    public synchronized static boolean isPlaying(){
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }
}
