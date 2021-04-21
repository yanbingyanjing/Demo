package com.yjfshop123.live.live.live.common.widget.gift.utils;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.utils.DateFormatUtil;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {
    private static DecimalFormat sDecimalFormat;
    private static DecimalFormat sDecimalFormat2;
     private static Pattern sPattern;
    private static Pattern sIntPattern;
    private static Random sRandom;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        sRandom = new Random();
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

    /**
     * 判断字符串中是否包含中文
     */
    public static boolean isContainChinese(String str) {
        Matcher m = sPattern.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }

    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

    /**
     * 设置视频输出路径
     */
    public static String  generateVideoOutputPath() {
        String outputDir = Const.VIDEO_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String videoName = DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999);
        return outputDir + "android_" + videoName + ".mp4";
    }
    /**
     * 设置视频输出路径
     */
    public static String generateVideoRecordTempOutputPath() {
        String outputDir = Const.VIDEO_RECORD_TEMP_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputDir ;
    }
}
