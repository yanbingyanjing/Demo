package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyDialogFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.TCBeautyHelper;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

public class CallSettingActivity1v1 extends BaseActivityH implements IPermissions {


    private TXCloudVideoView txCloudVideoView;
    private TRTCCloud trtcCloud;
    private TCBeautyHelper mBeautyHepler;

    private BeautyDialogFragment.BeautyParams mBeautyParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_callset);

        trtcCloud = TRTCCloud.sharedInstance(this);
        txCloudVideoView = findViewById(R.id.txcloud_videoview);

        PermissionsUtils.initPermission(mContext);
        PermissionsUtils.onResume(this, this);

        TRTCCloudDef.TRTCVideoEncParam smallParam = new TRTCCloudDef.TRTCVideoEncParam();
        smallParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
        smallParam.videoFps = 20;
        smallParam.videoBitrate = 600;
        smallParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.enableEncSmallVideoStream(false, smallParam);

        //设置美颜、美白、红润效果级别。
        //TRTC_BEAUTY_STYLE_SMOOTH //平滑
        //TRTC_BEAUTY_STYLE_NATURE //自然
        //0-9
        mBeautyHepler = new TCBeautyHelper(trtcCloud, mContext);
        mBeautyHepler.setmOnDismissListener(new BeautyDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(BeautyDialogFragment.BeautyParams beautyParams) {
                mBeautyParams = beautyParams;
            }
        });

        //frontCamera（true：前置摄像头 false：后置摄像头
//        trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
//        trtcCloud.startLocalPreview(true, txCloudVideoView);

        findViewById(R.id.btn_beauty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeautyParams != null){
                    UserInfoUtil.setBeauty_white_ruddy(mBeautyParams.mBeautyProgress, mBeautyParams.mWhiteProgress,
                            mBeautyParams.mRuddyProgress, mBeautyParams.mFilterIdx);
                    NToast.shortToast(mContext, "美颜设置已保存");
                }
                finish();
            }
        });

        show_();
    }

    private void show_(){
        if (mBeautyHepler.isAdded()){
            mBeautyHepler.dismiss();
        } else {
            mBeautyHepler.show(getFragmentManager(), "");
        }
    }

    @Override
    public void allPermissions() {
        //frontCamera（true：前置摄像头 false：后置摄像头
        trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        trtcCloud.startLocalPreview(true, txCloudVideoView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //销毁 trtc 实例
        if (trtcCloud != null) {
            trtcCloud.setListener(null);
        }
        trtcCloud = null;
        TRTCCloud.destroySharedInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
