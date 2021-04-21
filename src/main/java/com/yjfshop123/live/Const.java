package com.yjfshop123.live;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

public class Const {
    public static final String mCodeIdVideo = "945862269";
    public static final boolean isTest = false;//是否是测试环境  true 测试环境  false 正式环境
    /**
     * NLog 开关
     */
    public static final boolean IS_DEBUG = true;

    //自营商城域名
    public static String ziying_shop_url = isTest ? "https://shop.yjfshop.com" : "https://shop.dgjlive.com";

    /*域名*/
    public static String getDomain() {
//        if (ActivityUtils.TYPE.equals("LIVE_1")){
//        }else if (ActivityUtils.TYPE.equals("LIVE_2")){
//        }else if (ActivityUtils.TYPE.equals("1V1")){
//        }else if (ActivityUtils.TYPE.equals("VIDEO")){
//        }else if (ActivityUtils.TYPE.equals("GAME")){
//        }
        //return "http://api-new.otccloud.club/otc-system";
        return isTest ? "https://api.yjfshop.com" : "https://api.dgjlive.com";
    }

    /*Socket 域名*/
    public static String getSocketDomain() {
//        if (ActivityUtils.TYPE.equals("LIVE_1")){
//        }else if (ActivityUtils.TYPE.equals("LIVE_2")){
//        }else if (ActivityUtils.TYPE.equals("1V1")){
//        }else if (ActivityUtils.TYPE.equals("VIDEO")){
//        }else if (ActivityUtils.TYPE.equals("GAME")){
//        }
        return isTest ? "wss://api.yjfshop.com/ws" : "wss://api.dgjlive.com/ws";
    }


    //交易所包名
    public static final String exchangePkg = "com.spark.apollo";
    //交易所下载安装包地址
    public static final String exchangeApkUrl = "https://www.dzzlyxc.cn/upload/app/bluedon.apk";

    //大淘客
    // public static String appKey = "5fc4a8f41bee8";
    // public static String appSecret = "869b14bd3edeabd2f2fc26cd9765ae3a";
//大淘客公司账户
    public static String appKey = "60014927561e6";
    public static String appSecret = "5f0edd07a93fbaf05fa31318937c416f";

    //穿山甲appid
    public static String ad_appid = "5130314";

    /*图片域名*/
    public static final String domain_url = isTest ? "https://zb-1302869529.cos.ap-shanghai.myqcloud.com/" : "https://dgj-1304660800.cos.ap-guangzhou.myqcloud.com/";
    public static final String domain_url2 = "";

    /*bugly(无需可忽略)*///TODO
    public static final String buglyAppid = "44d00d78eb";

    /*腾讯鉴权URL*/
    public static final String license_url
            = isTest ? "http://license.vod2.myqcloud.com/license/v1/76e960883291f3cf4241a82dd7c24f62/TXLiveSDK.licence"
            : "http://license.vod2.myqcloud.com/license/v1/e93b0536ff9fc284e2bfeafa9f35dc03/TXLiveSDK.licence";
    public static final String license_key = isTest ? "5b00dcf2e4de2820c946de8dba5a3c41" : "d1558599be48103bfdfef18fdca3c594";

    /*短视频鉴权URL*/
    public static final String license_ugc_url
            = isTest ? "http://license.vod2.myqcloud.com/license/v1/76e960883291f3cf4241a82dd7c24f62/TXUgcSDK.licence"
            : "http://license.vod2.myqcloud.com/license/v1/e93b0536ff9fc284e2bfeafa9f35dc03/TXUgcSDK.licence";
    public static final String license_ugc_key = isTest ? "5b00dcf2e4de2820c946de8dba5a3c41" : "d1558599be48103bfdfef18fdca3c594";

    /*微信*///TODO
    public static final String wx_app_id = "wx6208924221654f73";
    public static final String wx_app_secret = "07e256207a18d27309a4df30e6958298";

    /*QQ*///TODO
    public static final String qq_app_id = "101494794";
    public static final String qq_app_secret = "8f7363da82d1a2eea3f00e8d6ce802fa";

    /*云通信IM*/
    public static final int im_app_id = isTest ? 1400412154 : 1400474127;
    public static final String account_type = "36862";

    /*腾讯cos*/
    public static final String region = isTest ? "ap-shanghai" : "ap-guangzhou";
    public static final String bucket = isTest ? "zb-1302869529" : "dgj-1304660800";
    public static final String secretId = isTest ? "AKIDwf7IHz8Z8QReaJcOGvMWP21kY5ORh6Oo" : "AKIDkfTHBQMWgxzJW9pw597WH7iJ4at430nQ";
    public static final String secretKey = isTest ? "JNr64mDVXImlUxA65pKYg9MIkFakdJkz" : "UsRsBUcQ4f0Slw33mU3xuwoVxlMdB9VP";


    //人脸核身
    public static final int ruleId = 0;
    public static final String getBizTokenUrl = "faceid.tencentcloudapi.com";
    public static final String authUrl = "https://idasc.webank.com";
    public static final String version = "1.0.0";
    //-----人脸核身测试key和appid
    public static final String faceVerifykeyLicence = "NjImMkAmL34wj9LyJDXl9FK45VdqhMYINmpoAtzbDgUzX4gfsO/T7K+lS8thljU37FrurzI64QEn7UC6iyyG4RA+asnWQg0FxhnGdVkFMjus8lvhS1Y8ULmaT13NE/UjwHf0lGQr1uonF3TqjoJbtGD3GVvi5gBAJbxuIPbIu9AnarYX3o2OziBywFoLrhb1xUjzSnyJP1ejtC0G3FY/wBbcEJ8I60A+J1mt87ZgEnY+ijCnRdLgXCuPNWbTJTsODrc7jYmre6h9Ws32H1wqOYeDkpcop8meYqHcUQrQNpi5Z7+ewuPBoX9BFbD73tQu15r3oWCBU1GYxyh7WzgOnQ==";
    public static final String faceVerifyappid = "TIDAPUel";
    public static final String faceVerifySecret = "jGKb9FvDDdebdoX4ltUPf8dJe7R9MZXO6ptgwJzF23E84XJydwG3rXe1k4eGGWj9";

    //-----人脸核身正式key和appid
    //public static final String faceVerifykeyLicence ="MtXBhsxEsElhH0GvEgUhjExQMb4SKnekrODkNBbplgRc+xVAK91Fjesq7EpALRTiKlz4xAPndiN+5HEc82I9QQDuLwMK1IcjVuAH/pEkXx0VQORxkpCpH41QmhS4HYrpwGFkUqmWU0kAr6a7Y8xYe7P57/g8ZoAFo5+R6Bqwt/MnarYX3o2OziBywFoLrhb1xUjzSnyJP1ejtC0G3FY/wBbcEJ8I60A+J1mt87ZgEnY+ijCnRdLgXCuPNWbTJTsODrc7jYmre6h9Ws32H1wqOYeDkpcop8meYqHcUQrQNpi5Z7+ewuPBoX9BFbD73tQu15r3oWCBU1GYxyh7WzgOnQ==";
    //public static final String faceVerifyappid = "IDAmI1Xo";
    //public static final String faceVerifySecret = "xsfTUd3dCsDR41vGnfzndTZdGN9mefZDF5LtOUd8W0lvwhu2qQhj0ZJxACoOEqoA";

    /*抢聊自动刷新时间间隔*/
    public static final int vperiod_time = 30;

    public static final boolean IS_COS = true;//是否使用腾讯COS上传

    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = App.getInstance().getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "dachuyun";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_PATH = DCMI_PATH + "/" + DIR_NAME + "/video/";
    public static final String VIDEO_RECORD_TEMP_PATH = VIDEO_PATH + "recordParts";


    public static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
    public static final String REAL_PIC_PATH = SAVE_PIC_PATH + "/" + BuildConfig.APPLICATION_ID + "/xchat";
    public static String cropDir = REAL_PIC_PATH + "/ImageCrop";//裁剪图片暂存路径
    public static String voiceDir = REAL_PIC_PATH + "/SoundRecorder";//裁剪图片暂存路径
    public static String downloadDir = SAVE_PIC_PATH + "/download";//裁剪图片暂存路径

    public static float ratio = 1.4f;

    public static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = App.getInstance().getPackageManager()
                    .getApplicationInfo(App.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private static boolean getMetaDataBoolean(String key) {
        boolean res = false;
        try {
            ApplicationInfo appInfo = App.getInstance().getPackageManager()
                    .getApplicationInfo(App.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getBoolean(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static long getMetaDataLong(String key) {
        long res = 0;
        try {
            ApplicationInfo appInfo = App.getInstance().getPackageManager()
                    .getApplicationInfo(App.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
