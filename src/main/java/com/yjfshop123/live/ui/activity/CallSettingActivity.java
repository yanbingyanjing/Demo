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
        // 设置自定义视频处理回调，在主播预览及编码前回调出来，用户可以用来做自定义美颜或者增加视频特效
        mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
            /**
             * 在OpenGL线程中回调，在这里可以进行采集图像的二次处理
             * @param i  纹理ID
             * @param i1      纹理的宽度
             * @param i2     纹理的高度
             * @return 返回给SDK的纹理
             * 说明：SDK回调出来的纹理类型是GLES20.GL_TEXTURE_2D，接口返回给SDK的纹理类型也必须是GLES20.GL_TEXTURE_2D
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
             * 增值版回调人脸坐标
             * @param floats   归一化人脸坐标，每两个值表示某点P的X,Y值。值域[0.f, 1.f]
             */
            @Override
            public void onDetectFacePoints(float[] floats) {

            }

            /**
             * 在OpenGL线程中回调，可以在这里释放创建的OpenGL资源
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

                NToast.shortToast(mContext, "美颜设置已保存");
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
        //status <= 0  未检测到人脸
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
