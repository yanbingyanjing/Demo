package com.yjfshop123.live.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private TimeUtil() {
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        if (calendar.before(inputTime)) {
            //今天23:59在输入时间之前，解决一些时间误差，把当天时间显示到这里
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "年" + "MM" + "月" + "dd" + "日");
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return "昨天";
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "月" + "d" + "日");
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "年" + "MM" + "月" + "dd" + "日");
                return sdf.format(currenTimeZone);

            }

        }

    }

    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (!calendar.after(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "年" + "MM" + "月" + "dd" + "日");
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return "昨天" + " " + sdf.format(currenTimeZone);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "月" + "d" + "日" + " HH:mm");
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "年" + "MM" + "月" + "dd" + "日" + " HH:mm");
                return sdf.format(currenTimeZone);
            }

        }

    }


    private static String getString(int t) {
        String m = "";
        if (t > 0) {
            if (t < 10) {
                m = "0" + t;
            } else {
                m = t + "";
            }
        } else {
            m = "00";
        }
        return m;
    }

    /**
     * @param t 毫秒
     * @return
     * @author Peter（张春玲）
     */
    public static String format(int t) {//00:00:00
        if (t < 60000) {
            return "00:00:" + (t % 60000) / 1000 + "";
        } else if ((t >= 60000) && (t < 3600000)) {
            return "00:" + getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        } else {
            return getString(t / 3600000) + ":" + getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        }
    }

    public static String format_(int t) {//00:00
        if (t < 60000) {
            return "00:" + (t % 60000) / 1000 + "";
        } else if ((t >= 60000) && (t < 3600000)) {
            return getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        } else {
            return getString(t / 3600000) + ":" + getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        }
    }


}
