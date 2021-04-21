package com.yjfshop123.live.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.yjfshop123.live.Const;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {

    /**	 *
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**	 *
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 得到屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        int height = wm.getDefaultDisplay().getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;// 屏幕宽度（像素）
        int height= dm.heightPixels; // 屏幕高度（像素）
        return height;

    }

    private static int width = 0;

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (width == 0){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;// 屏幕宽度（像素）
            return width;
        }
        return width;
    }

    /**
     * 得到状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        int statusHeight = 0;
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusHeight = frame.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {

        if (TextUtils.isEmpty(email))
            return false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }

    /**
     * 是否开启 GPS
     *
     * @param context
     * @return
     */
    public static boolean isOpenGPS(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNPEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGPSEnable || isNPEnable;


    }

    public static void openGPS(Context context) {

        Intent GPSIntent = new Intent();

        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");

        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));

        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


//    public static int dip2pix(Context context, int dips) {
//        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
//        return (dips * densityDpi) / 160;
//    }

    public static String getUrl(String url) {
        if(TextUtils.isEmpty(url))return "";
        if (!url.contains("http")) {
            url = (Const.IS_COS ? Const.domain_url : Const.domain_url2) + url;
        }
        return url;
    }

    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

}
