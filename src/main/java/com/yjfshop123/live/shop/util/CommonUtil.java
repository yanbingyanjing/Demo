package com.yjfshop123.live.shop.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class CommonUtil {
    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context context
     * @param pkgName 应用包名
     * @return true:已安装；false：未安装
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 跳转至商品详情
     *
     * @param activity Activity
     * @param url      商品详情
     */
    public static void gotoGoodsDetail(Activity activity, String url) {
        try {

            //测试商品url
            Intent intent21 = activity.getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
            intent21.setAction("android.intent.action.VIEW");
            activity.startActivity(intent21);

            Intent intent2 = activity.getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
            intent2.setAction("android.intent.action.VIEW");
            intent2.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
            Uri uri = Uri.parse(url);
            intent2.setData(uri);
            activity.startActivity(intent2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开优惠券页面
     * @param activity
     * @param url
     */
    public static void gotoCoupon(Activity activity, String url) {
        try {
            Intent intent2 = activity.getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
            intent2.setAction("android.intent.action.VIEW");
            intent2.setClassName("com.taobao.taobao", "com.taobao.browser.BrowserActivity");
            Uri uri = Uri.parse(url);
            intent2.setData(uri);
            activity.startActivity(intent2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开店铺主页
     * @param activity
     * @param url
     */
    public static void gotoShop(Activity activity, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用第三方浏览器打开
     * @param context
     * @param url 要浏览的资源地址
     */
    public static  void openBrowser(Context context,String url){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }


}