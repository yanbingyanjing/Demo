package com.yjfshop123.live.utils.update.all.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import com.yjfshop123.live.App;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.utils.update.all.DataBean;
import com.yjfshop123.live.utils.update.all.download.DownloadHelper;
import com.yjfshop123.live.utils.update.all.download.DownloadManager;
import com.yjfshop123.live.utils.update.all.download.OnDownloadListener;
import com.yjfshop123.live.utils.update.all.utils.AppUtils;
import com.yjfshop123.live.utils.update.all.utils.SpUtils;
import com.yjfshop123.live.utils.update.all.utils.StringUtils;

import org.json.JSONObject;

import java.io.File;


public class UpdateManager {
    private static volatile UpdateManager manager = null;

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
     * 最后一次保存cache的时间
     */
    private long mLastCacheSaveTime = 0;

    private UpdateManager() {
        mDownloadManager = DownloadManager.getInstance();
    }

    /**
     * 获取updateManager实例
     *
     * @return updateManager实例
     */
    public static UpdateManager getInstance() {
        if (manager == null) {
            synchronized (UpdateManager.class) {
                if (manager == null) {
                    manager = new UpdateManager();
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

    public void checkUpdate(OnCheckUpdateListener onCheckUpdateListener) {
        mOnCheckUpdateListener = onCheckUpdateListener;
        String url = "app/public/checkUpdate" + "?version=" + App.versionName;
        OKHttpUtils.getInstance().getRequest(url, "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    DataBean dataBean = parseJson(result);
                    if(checkUpdate(dataBean)){
                        //最后一次缓存的时间超过缓存文件有效期，或者最后一次缓存的apk不是最新版本的apk，删除缓存apk
                        if ((System.currentTimeMillis() - mLastCacheSaveTime > getCacheSaveValidTime()) || (getCacheApkVersionCode() != dataBean.getVersionCode())) {
                            setCacheApkVersionCode(dataBean.getVersionCode());
                        }
                        sendMessage(MSG_ON_FIND_NEW_VERSION, dataBean);
                    } else {
                        sendMessage(MSG_ON_NEWEST, null);
                        //当前已经是最新版本APK，清除本地已经缓存的apk安装包
                        clearCacheApkFile();
                    }
                } catch (Exception ex) {
                    //网络异常
                }
            }
        });
    }


    private boolean checkUpdate(DataBean response) {
        boolean b = false;
        String[] ver1 = response.getVersionName().split("\\.");
        String[] ver2 = BuildConfig.VERSION_NAME.split("\\.");
        if (ver1.length == ver2.length) {
            for (int i = 0; i < ver1.length; i++) {
                int onLine = Integer.parseInt(ver1[i]);
                int local = Integer.parseInt(ver2[i]);
                if (onLine > local) {
                    b = true;
                    break;
                }else if(onLine < local){
                    b = false;
                    break;
                }
            }
        }
        return b;
    }

    /**
     * 开始更新App
     * <p>
     * 此时开始正式下载更新Apk
     *
     * @param apkUrl           服务端最新apk文件url
     * @param onUpdateListener onUpdateListener
     */
    public void startToUpdate(String apkUrl, OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;

        if (StringUtils.isEmpty(mNewestVersionName) || mNewestVersionCode == 0)
            return;

        downloadNewestApkFile(apkUrl, mNewestVersionCode, mNewestVersionName);
    }

    /**
     * 设置缓存文件有效时间，单位：秒
     * <p>
     * 默认缓存有效期为7天
     *
     * @param cacheValidTime 缓存文件有效时间
     */
    public void setCacheSaveValidTime(long cacheValidTime) {
        SpUtils.putLong(SP_KEY_CACHE_VALID_TIME, cacheValidTime);
    }

    /**
     * 获取缓存文件有效时间，单位：秒
     * <p>
     * 默认缓存有效期为7天
     *
     * @return 缓存文件有效时间
     */
    public long getCacheSaveValidTime() {
        return SpUtils.getLong(SP_KEY_CACHE_VALID_TIME, 60 * 60 * 24 * 7);
    }

    /**
     * 设置缓存文件版本号
     *
     * @param versionCode cacheApk版本号
     */
    public void setCacheApkVersionCode(int versionCode) {
        SpUtils.putInt(SP_KEY_CACHE_APK_VERSION_CODE, versionCode);
    }

    /**
     * 获取缓存文件版本号
     *
     * @return 缓存文件版本号
     */
    public int getCacheApkVersionCode() {
        return SpUtils.getInt(SP_KEY_CACHE_APK_VERSION_CODE, 0);
    }

    /**
     * 取消更新
     */
    public void cancleUpdate() {
        //保留下载已完成的部分apk cache文件，cache文件最多保留7天
        mDownloadManager.pauseDownload();
    }

    /**
     * 清除已下载的APK缓存
     */
    public void clearCacheApkFile() {
        mDownloadManager.clearAllCacheFile();
    }

    /**
     * 下载最新版本的APK文件
     *
     * @param url               服务端最新apk文件url
     * @param newestVersionCode 最新版本APK版本号
     * @param newestVersionName 最新版本APK版本名称
     */
    private void downloadNewestApkFile(String url, int newestVersionCode, String
            newestVersionName) {
        String apkFileName = getApkNameWithVersionName(DownloadHelper.getUrlFileName(url),
                newestVersionName);

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
                        //取消升级时，调用download pause，保留已下载的部分apk文件
                        sendMessage(MSG_ON_CANCLE, null);
                    }

                    @Override
                    public void onCanceled() {
                        //为了保证断点续传，升级时，调用download pause，不使用cancle，onCancle不会被调用
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
     * 解析json数据
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
     * 获取带版本名称的apk文件名
     *
     * @param apkName apk原名
     * @return 带版本名称的apk文件名
     */
    private String getApkNameWithVersionName(String apkName, String versionName) {
        if (StringUtils.isEmpty(apkName))
            return apkName;

        if (!apkName.contains(".apk")){
            return apkName;
        }

        apkName = apkName.substring(apkName.lastIndexOf("/") + 1, apkName.indexOf("" + ".apk"));
        NLog.e("tag", "newApkName = " + apkName + "_v" + versionName + ".apk");
        return apkName + "_v" + versionName + ".apk";
    }

    /**
     * 安装 apk
     *
     * @param apkPath apk全路径
     */
    public void installApk(String apkPath) {
        if (StringUtils.isEmpty(apkPath)) {
            NLog.e("tag", "apkPath is null.");
            return;
        }

        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判断版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致  参数3 共享的文件
            Uri apkUri = FileProvider.getUriForFile(AppUtils.getContext(), BuildConfig.APPLICATION_ID + ".image_provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        AppUtils.getContext().startActivity(intent);
    }
}
