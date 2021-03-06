package com.yjfshop123.live.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyLvJingFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.BeautyTieZhiFragment;
import com.yjfshop123.live.live.live.common.widget.beautysetting.IBeauty;
import com.yjfshop123.live.live.live.common.widget.beautysetting.TCBeautyHelperNew;
import com.yjfshop123.live.live.live.common.widget.beautysetting.VideoMaterialMetaData;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.faceunity.FURenderer;
import com.faceunity.entity.Filter;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;


public class CallSettingActivity extends BaseActivityH implements IPermissions,
        FURenderer.OnTrackingStatusChangedListener, IBeauty {

    private FURenderer mFURenderer;
    private boolean mOnFirstCreate = true;

    private TXLivePushConfig mLivePushConfig;
    private TXLivePusher mLivePusher;

    private TXCloudVideoView txCloudVideoView;
    private TCBeautyHelperNew mBeautyHepler;

    private BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi;
    private String proMeiFu;
    private String proMeiXing;
    private BeautyLvJingFragment.BeautyParams beautyParamsLvJing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_callset);

        mFURenderer = new FURenderer
                .Builder(mContext)
                .createEGLContext(false)
                .maxFaces(2)
                .setNeedFaceBeauty(true)
                .inputPropOrientation(0)
                .setOnTrackingStatusChangedListener(this)
                .build();

        txCloudVideoView = findViewById(R.id.txcloud_videoview);

        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePusher.setConfig(mLivePushConfig);
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
            /**
             * ???OpenGL??????????????????????????????????????????????????????????????????
             * @param i  ??????ID
             * @param i1      ???????????????
             * @param i2     ???????????????
             * @return ?????????SDK?????????
             * ?????????SDK??????????????????????????????GLES20.GL_TEXTURE_2D??????????????????SDK???????????????????????????GLES20.GL_TEXTURE_2D
             */
            @Override
            public int onTextureCustomProcess(int i, int i1, int i2) {
                if (mOnFirstCreate) {
                    NLog.d("TAGTAG", "onTextureCustomProcess: create");
                    mFURenderer.onSurfaceCreated();
                    mOnFirstCreate = false;
                }
                int texId = mFURenderer.onDrawFrame(i, i1, i2);
                return texId;
            }

            /**
             * ???????????????????????????
             * @param floats   ????????????????????????????????????????????????P???X,Y????????????[0.f, 1.f]
             */
            @Override
            public void onDetectFacePoints(float[] floats) {

            }

            /**
             * ???OpenGL????????????????????????????????????????????????OpenGL??????
             */
            @Override
            public void onTextureDestoryed() {
                NLog.d("TAGTAG", "onTextureDestoryed: t:" + Thread.currentThread().getId());
                mFURenderer.onSurfaceDestroyed();
                mOnFirstCreate = true;
            }
        });

        mBeautyHepler = new TCBeautyHelperNew(mFURenderer);

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
                UserInfoUtil.setBeauty(beautyParamsTieZhi.mVideoMaterialMetaData.path,
                        proMeiFu,
                        proMeiXing,
                        beautyParamsLvJing.mFilterProgress,
                        beautyParamsLvJing.mFilter);

                NToast.shortToast(mContext, "?????????????????????");
                finish();
            }
        });

        beautyParamsTieZhi = new BeautyTieZhiFragment.BeautyParams();
        String tiezhi_path = UserInfoUtil.getTiezhiPath();
        if (TextUtils.isEmpty(tiezhi_path)){
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video_none", "video_none", "", "");
        }else {
            beautyParamsTieZhi.mVideoMaterialMetaData = new VideoMaterialMetaData("video", tiezhi_path, "", "");
        }
        String miefu = UserInfoUtil.getMeifu();
        if (TextUtils.isEmpty(miefu)){
            proMeiFu = "0@@20@@20@@20@@20@@20";
        }else {
            proMeiFu = miefu;
        }
        String miexing = UserInfoUtil.getMeiXing();
        if (TextUtils.isEmpty(miexing)){
            proMeiXing = "0@@30@@30@@30@@30@@30@@30@@30@@30@@30";
        }else {
            proMeiXing = miexing;
        }
        int filterProgress = UserInfoUtil.getFilterProgress();
        String filterStr = UserInfoUtil.getFilterStr();
        beautyParamsLvJing = new BeautyLvJingFragment.BeautyParams();
        beautyParamsLvJing.mFilterProgress = filterProgress;
        if (TextUtils.isEmpty(filterStr)){
            beautyParamsLvJing.mFilter = Filter.Key.ORIGIN;
        }else {
            beautyParamsLvJing.mFilter = filterStr;
        }
        mBeautyHepler.init(beautyParamsTieZhi, proMeiFu, proMeiXing, beautyParamsLvJing);


        show_();

        PermissionsUtils.initPermission(mContext);
        PermissionsUtils.onResume(this, this);
    }

    public void setFilter(Bitmap image) {
        if (mLivePusher != null){
            mLivePusher.setFilter(image);
        }
    }

    private void show_(){
        if (mBeautyHepler.isAddedMain()){
            mBeautyHepler.dismissMain();
        } else {
            mBeautyHepler.showMain(getFragmentManager(), "BeautyMainFragment");
        }
    }

    @Override
    public void allPermissions() {
        if (mLivePusher != null){
            mLivePusher.startCameraPreview(txCloudVideoView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLivePusher != null) {
            mLivePusher.setPushListener(null);
            mLivePusher.stopCameraPreview(true);
            mLivePusher.stopPusher();
            mLivePusher = null;
        }
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

    @Override
    public void onTrackingStatusChanged(int status) {
        //status <= 0  ??????????????????
    }

    @Override
    public void closeMain() {

    }

    @Override
    public void closeTieZhi(BeautyTieZhiFragment.BeautyParams params) {
        beautyParamsTieZhi = params;
    }

    @Override
    public void closeMeiYan(String proMeiFu, String proMeiXing) {
        this.proMeiFu = proMeiFu;
        this.proMeiXing = proMeiXing;
    }

    @Override
    public void closeLvJing(BeautyLvJingFragment.BeautyParams params) {
        beautyParamsLvJing = params;
    }
}
