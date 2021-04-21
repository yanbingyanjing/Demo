package com.yjfshop123.live.utils;


import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateFormatUtil {

    private static SimpleDateFormat sFormat;
    private static SimpleDateFormat sFormat2;
    private static SimpleDateFormat sFormat3;
    private static SimpleDateFormat sFormat4;
    static {
        sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        sFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        sFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    public static String getCurTimeYMDString() {
        return sFormat3.format(new Date());
    }


    public static String getCurTimeString() {
        return sFormat.format(new Date());
    }

    public static String getVideoCurTimeString() {
        return sFormat2.format(new Date());
    }
    //获取时间的时间戳 北京时间
    public static long getDateLong(String time) {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date date;
        long l=0;
        try {
            date = dff.parse(time);
            l = date.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;

    }
    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static long dataOne(String time) {
        if(TextUtils.isEmpty(time))return 0;
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        Date date;
        long l=0;
        try {
            date = sdr.parse(time);
             l = date.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public static long dateDiff(String startTime, String endTime ) {
        if(TextUtils.isEmpty(endTime)){
            endTime=getCurTimeString();
        }
        if(TextUtils.isEmpty(startTime)){
            startTime=getCurTimeString();
        }
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = sFormat3;
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            System.out.println("时间相差：" + day + "天" + hour + "小时" + min
                    + "分钟" + sec + "秒。");
            if (day>=1) {
                return day;
            }else {
                if (day==0) {
                    return 0;
                }else {
                    return Math.abs(day);
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }


    public static String getNextDate(int days ) {
        Date date=  new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days); //今天的时间加一天
        date = calendar.getTime();
        return  sFormat3.format(date);
    }

}
