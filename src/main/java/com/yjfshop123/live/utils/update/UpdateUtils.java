package com.yjfshop123.live.utils.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.update.all.DataBean;
import com.yjfshop123.live.utils.update.all.update.OnCheckUpdateListener;
import com.yjfshop123.live.utils.update.all.update.OnUpdateListener;
import com.yjfshop123.live.utils.update.all.update.UpdateManager;

import static com.yjfshop123.live.App.is_check_update;

public class UpdateUtils {

    private Activity context;
    private UpdateManager mUpdateManager;
    private static UpdateDialog dialog;


    public UpdateUtils(Activity context) {
        if (dialog != null && dialog.getDialog().isShowing()) {
            return;
        }
        this.context = context;
        mUpdateManager = UpdateManager.getInstance();
        dialog = new UpdateDialog(context).builder();
    }

    public void updateDiy() {
        if (dialog != null && dialog.getDialog().isShowing()) {
            return;
        }
        mUpdateManager.checkUpdate(new OnCheckUpdateListener() {
            @Override
            public void onFindNewVersion(DataBean dataBean) {
                buildNewVersionDialog(dataBean);
            }

            @Override
            public void onNewest() {
                dialog.cancel();
            }
        });
    }

    /**
     * 创建发现新版本apk alert dialog
     */
    private void buildNewVersionDialog(final DataBean dataBean) {
        is_check_update=true;
        dialog.setTitle("新版本更新")
                .setFocus(dataBean.isFocus())
//                .setFocus(false)
                .setVersion("v" + dataBean.getVersionName())
                .setMsg(dataBean.getContent().replace("\\n", "\n"))
                .setPositiveButton("立即更新", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String apkUrl = dataBean.getApkurl();
                        if (apkUrl.contains(".apk")){
                            //APP下载
                            mUpdateManager.startToUpdate(dataBean.getApkurl(), mOnUpdateListener);
                        }else {
                            //浏览器下载
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(apkUrl);
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
            dialog=null;
            //所有的更新全部在updateManager中完成，Activity在这里只是做一些界面上的处理
        }

        @Override
        public void onUpdateFailed() {
            NToast.shortToast(context, "更新失败，请检查网络环境");
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
            NToast.shortToast(context, "更新失败，请检查网络环境");
            dialog.cancel();
        }
    };

}
