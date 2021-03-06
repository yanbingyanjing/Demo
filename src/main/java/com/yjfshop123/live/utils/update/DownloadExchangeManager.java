package com.yjfshop123.live.utils.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import com.yjfshop123.live.App;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.utils.update.all.DataBean;
import com.yjfshop123.live.utils.update.all.download.DownloadHelper;
import com.yjfshop123.live.utils.update.all.download.DownloadManager;
import com.yjfshop123.live.utils.update.all.download.OnDownloadListener;
import com.yjfshop123.live.utils.update.all.update.OnCheckUpdateListener;
import com.yjfshop123.live.utils.update.all.update.OnUpdateListener;
import com.yjfshop123.live.utils.update.all.update.UpdateManager;
import com.yjfshop123.live.utils.update.all.utils.AppUtils;
import com.yjfshop123.live.utils.update.all.utils.SpUtils;
import com.yjfshop123.live.utils.update.all.utils.StringUtils;

import org.json.JSONObject;

import java.io.File;

import static com.yjfshop123.live.Const.exchangePkg;

public class DownloadExchangeManager {
    private static volatile DownloadExchangeManager manager = null;

    public static final String SP_KEY_CACHE_VALID_TIME = "sp_key_cache_valid_time";
    public static final String SP_KEY_CACHE_APK_VERSION_CODE = "sp_key_cache_apk_version_code";

    private static final int MSG_ON_START = 1;
    private static final int MSG_ON_PROGRESS = 2;
    private static final int MSG_ON_DOWNLOAD_FINISH = 3;
    private static final int MSG_ON_FAILED = 4;
    private static final int MSG_ON_CANCLE = 5;
    private static final int MSG_ON_FIND_NEW_VERSION = 6;
    private static final int MSG_ON_NEWEST = 7;
    private static final int MSG_ON_UPDATE_EXCEPTION = 8;

    private DownloadManager mDownloadManager;

    private OnUpdateListener mOnUpdateListener;
    private OnCheckUpdateListener mOnCheckUpdateListener;

    private int mNewestVersionCode;
    private String mNewestVersionName;
    private String mNewVersionContent;

    /**
     * ??????????????????cache?????????
     */
    private long mLastCacheSaveTime = 0;

    private DownloadExchangeManager() {
        mDownloadManager = DownloadManager.getInstance();
    }

    /**
     * ??????updateManager??????
     *
     * @return updateManager??????
     */
    public static DownloadExchangeManager getInstance() {
        if (manager == null) {
            synchronized (DownloadExchangeManager.class) {
                if (manager == null) {
                    manager = new DownloadExchangeManager();
                }
            }
        }
        return manager;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ON_START:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onStartUpdate();
                    break;

                case MSG_ON_PROGRESS:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onProgress((Integer) msg.obj);
                    break;

                case MSG_ON_DOWNLOAD_FINISH:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onApkDownloadFinish((String) msg.obj);
                    installApk((String) msg.obj);
                    break;

                case MSG_ON_FAILED:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onUpdateFailed();
                    break;

                case MSG_ON_CANCLE:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onUpdateCanceled();
                    break;

                case MSG_ON_UPDATE_EXCEPTION:
                    if (mOnUpdateListener != null)
                        mOnUpdateListener.onUpdateException();
                    break;

                case MSG_ON_FIND_NEW_VERSION:
                    DataBean dataBean = (DataBean) msg.obj;

                    mNewestVersionCode = dataBean.getVersionCode();
                    mNewestVersionName = dataBean.getVersionName();
                    mNewVersionContent = dataBean.getContent();

                    if (mOnCheckUpdateListener != null)
                        mOnCheckUpdateListener.onFindNewVersion(dataBean);
                    break;

                case MSG_ON_NEWEST:
                    if (mOnCheckUpdateListener != null)
                        mOnCheckUpdateListener.onNewest();
                    break;
            }
        }
    };


    //?????????????????????????????????
    public boolean checkAppInstalled(Context context) {
        if (exchangePkg == null || exchangePkg.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(exchangePkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;//true???????????????false????????????
        }
    }

    /**
     * ????????????App
     * <p>
     * ??????????????????????????????Apk
     *
     * @param apkUrl           ???????????????apk??????url
     * @param onUpdateListener onUpdateListener
     */
    public void startToUpdate(String apkUrl, OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;

        downloadNewestApkFile(apkUrl);
    }

    /**
     * ?????????????????????????????????????????????
     * <p>
     * ????????????????????????7???
     *
     * @param cacheValidTime ????????????????????????
     */
    public void setCacheSaveValidTime(long cacheValidTime) {
        SpUtils.putLong(SP_KEY_CACHE_VALID_TIME, cacheValidTime);
    }

    /**
     * ?????????????????????????????????????????????
     * <p>
     * ????????????????????????7???
     *
     * @return ????????????????????????
     */
    public long getCacheSaveValidTime() {
        return SpUtils.getLong(SP_KEY_CACHE_VALID_TIME, 60 * 60 * 24 * 7);
    }

    /**
     * ???????????????????????????
     *
     * @param versionCode cacheApk?????????
     */
    public void setCacheApkVersionCode(int versionCode) {
        SpUtils.putInt(SP_KEY_CACHE_APK_VERSION_CODE, versionCode);
    }

    /**
     * ???????????????????????????
     *
     * @return ?????????????????????
     */
    public int getCacheApkVersionCode() {
        return SpUtils.getInt(SP_KEY_CACHE_APK_VERSION_CODE, 0);
    }

    /**
     * ????????????
     */
    public void cancleUpdate() {
        //??????????????????????????????apk cache?????????cache??????????????????7???
        mDownloadManager.pauseDownload();
    }

    /**
     * ??????????????????APK??????
     */
    public void clearCacheApkFile() {
        mDownloadManager.clearAllCacheFile();
    }

    /**
     * ?????????????????????APK??????
     *
     * @param url               ???????????????apk??????url
     */
    private void downloadNewestApkFile(String url) {
        String apkFileName = getApkNameWithVersionName(DownloadHelper.getUrlFileName(url));

        sendMessage(MSG_ON_START, null);

        mDownloadManager.startDownload(url, apkFileName, new
                OnDownloadListener() {
                    @Override
                    public void onException() {
                        sendMessage(MSG_ON_UPDATE_EXCEPTION, null);
                    }

                    @Override
                    public void onProgress(int progress) {
                        sendMessage(MSG_ON_PROGRESS, progress);
                    }

                    @Override
                    public void onSuccess() {
                        mLastCacheSaveTime = System.currentTimeMillis();
                        sendMessage(MSG_ON_DOWNLOAD_FINISH, mDownloadManager.getDownloadFilePath());
                    }

                    @Override
                    public void onFailed() {
                        mLastCacheSaveTime = System.currentTimeMillis();
                        sendMessage(MSG_ON_FAILED, null);
                    }

                    @Override
                    public void onPaused() {
                        mLastCacheSaveTime = System.currentTimeMillis();
                        //????????????????????????download pause???????????????????????????apk??????
                        sendMessage(MSG_ON_CANCLE, null);
                    }

                    @Override
                    public void onCanceled() {
                        //?????????????????????????????????????????????download pause????????????cancle???onCancle???????????????
                        mLastCacheSaveTime = System.currentTimeMillis();
                        sendMessage(MSG_ON_CANCLE, null);
                    }
                });
    }

    private void sendMessage(int msgWhat, Object o) {
        Message msg = Message.obtain();
        msg.what = msgWhat;
        msg.obj = o;
        mHandler.sendMessage(msg);
    }

    /**
     * ??????json??????
     *
     * @param jsonData
     * @return
     */
    private DataBean parseJson(String jsonData) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject dataObject = new JSONObject(jsonData);
            dataBean.setContent(dataObject.getString("update_msg"));
            dataBean.setApkurl(dataObject.getString("sdk_url"));
            dataBean.setVersionCode(dataObject.getInt("update_status"));
            dataBean.setFocus(dataObject.getInt("update_status") == 2 ? true : false);
            dataBean.setVersionName(dataObject.getString("version"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBean;
    }

    /**
     * ????????????????????????apk?????????
     *
     * @param apkName apk??????
     * @return ??????????????????apk?????????
     */
    private String getApkNameWithVersionName(String apkName ) {
        if (StringUtils.isEmpty(apkName))
            return apkName;

        if (!apkName.contains(".apk")){
            return apkName;
        }

        apkName = apkName.substring(apkName.lastIndexOf("/") + 1, apkName.indexOf("" + ".apk"));
        NLog.e("tag", "newApkName = " + apkName + ".apk");
        return apkName + ".apk";
    }

    /**
     * ?????? apk
     *
     * @param apkPath apk?????????
     */
    public void installApk(String apkPath) {
        if (StringUtils.isEmpty(apkPath)) {
            NLog.e("tag", "apkPath is null.");
            return;
        }

        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // ???????????????Activity???????????????Activity,?????????????????????
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //?????????????????????7.0??????
            //??????1 ?????????, ??????2 Provider???????????? ??????????????????????????????  ??????3 ???????????????
            Uri apkUri = FileProvider.getUriForFile(AppUtils.getContext(), BuildConfig.APPLICATION_ID + ".image_provider", file);
            //???????????????????????????????????????????????????Uri??????????????????
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        AppUtils.getContext().startActivity(intent);
    }
}
