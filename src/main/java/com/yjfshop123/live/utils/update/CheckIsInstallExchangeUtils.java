package com.yjfshop123.live.utils.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.update.all.DataBean;
import com.yjfshop123.live.utils.update.all.update.OnCheckUpdateListener;
import com.yjfshop123.live.utils.update.all.update.OnUpdateListener;
import com.yjfshop123.live.utils.update.all.update.UpdateManager;

import static com.yjfshop123.live.Const.exchangePkg;

/**
 * 更新下载交易所   提示dialog
 */
public class CheckIsInstallExchangeUtils {
    private Activity context;
    private DownloadExchangeManager mUpdateManager;
    private static InstallExchangeDialog dialog;


    public CheckIsInstallExchangeUtils(Activity context) {
        if (dialog != null && dialog.getDialog().isShowing()) {
            return;
        }
        this.context = context;
        mUpdateManager = DownloadExchangeManager.getInstance();
        dialog = new InstallExchangeDialog(context).builder();
    }

    public void updateDiy() {
        if (dialog != null && dialog.getDialog().isShowing()) {
            return;
        }
        if (!mUpdateManager.checkAppInstalled(context)) {
            buildNewVersionDialog();
        }else {
            mUpdateManager.clearCacheApkFile();
        }
    }

    /**
     * 创建下载交易所apk alert dialog
     */
    private void buildNewVersionDialog() {
        dialog.setFocus(false)
//                .setFocus(false)
                .setPositiveButton(context.getString(R.string.download_now), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Const.exchangeApkUrl.contains(".apk")) {
                            //APP下载
                            mUpdateManager.startToUpdate(Const.exchangeApkUrl, mOnUpdateListener);
                        } else {
                            //浏览器下载
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(Const.exchangeApkUrl);
                            intent.setData(content_url);
                            context.startActivity(intent);
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(null);
        dialog.show();
    }



    private OnUpdateListener mOnUpdateListener = new OnUpdateListener() {
        @Override
        public void onStartUpdate() {
            dialog.show();
        }

        @Override
        public void onProgress(int progress) {
            dialog.setProgress(progress);
        }

        @Override
        public void onApkDownloadFinish(String apkPath) {
//            SmartToast.showText("newest apk download finish. apkPath: " + apkPath);
            Log.e("tag", "newest apk download finish. apkPath: " + apkPath);
            dialog.cancel();
            dialog = null;
            //所有的更新全部在updateManager中完成，Activity在这里只是做一些界面上的处理
        }

        @Override
        public void onUpdateFailed() {
            NToast.shortToast(context, "下载失败，请检查网络环境");
            dialog.cancel();
        }

        @Override
        public void onUpdateCanceled() {
//            SmartToast.showText("update cancled.");
            dialog.cancel();
        }

        @Override
        public void onUpdateException() {
//            SmartToast.showText("update exception.");
            NToast.shortToast(context, "下载失败，请检查网络环境");
            dialog.cancel();
        }
    };

}
