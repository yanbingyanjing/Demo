package com.yjfshop123.live.message;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 *
 * 日期:2019/3/12
 * 描述:
 **/
public class AudioUtils {


    /**
     * 获取音频文件时长
     *
     * @return 时长/微秒，可 /1000000D 得到秒
     * @throws Exception
     */
    public static long getRingDuring(String mUri) throws IOException {
        MediaPlayer meidaPlayer = new MediaPlayer();
        meidaPlayer.setDataSource(mUri);
        meidaPlayer.prepare();
        long time = meidaPlayer.getDuration()/1000;//获得了视频的时长（以毫秒为单位）
        return time;
    }

    /**
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        String str = "";
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        if (seconds > 0) {
            str = seconds + "\"";
        }
        if (minutes > 0) {
            str = minutes + ":" + seconds;
        }
        if (hours > 0) {
            str += hours + ":" + minutes + ":" + seconds;
        }
        return str;
    }
}
