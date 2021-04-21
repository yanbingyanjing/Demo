package com.yjfshop123.live.video;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.Constants;
import com.yjfshop123.live.live.live.common.widget.gift.view.DrawableTextView;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.ui.widget.switchbutton.SwitchButton;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.bean.CircleListBean;
import com.yjfshop123.live.video.fragment.VideoClassDialogFragment;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yjfshop123.live.xuanpin.view.SelectXuanPinDialog;
import com.yjfshop123.live.xuanpin.view.ShopSelectDialog;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;


public class VideoPublishActivity extends AbsActivity
        implements ITXLivePlayListener,
        View.OnClickListener,
        CosXmlUtils.OSSResultListener {


    public static void forward(Context context, String videoPath, int saveType) {
        Intent intent = new Intent(context, VideoPublishActivity.class);
        intent.putExtra(Constants.VIDEO_PATH, videoPath);
        intent.putExtra(Constants.VIDEO_SAVE_TYPE, saveType);
        context.startActivity(intent);
    }

    private static final String TAG = "VideoPublishActivity";
    private TextView mNum;
    DrawableTextView shop;
    FrameLayout shopLl;
    private TextView mLocation;
    private TextView classification;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mPlayer;
    private String mVideoPath;
    private boolean mPlayStarted;//播放是否开始了
    private boolean mPaused;//生命周期暂停
    private EditText mInput;
    private String mVideoTitle;//视频标题
    private String mVideoClassID = "";//分类id
    //    private Dialog mLoading;
    private int mSaveType;
    private SwitchButton vpSwitch;
    private String mCity;

    private CommonCallback<CircleListBean.ListBean> mClassCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_publish;
    }

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        mSaveType = intent.getIntExtra(Constants.VIDEO_SAVE_TYPE, Constants.VIDEO_SAVE_SAVE_AND_PUB);
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        findViewById(R.id.btn_pub).setOnClickListener(this);
        findViewById(R.id.vp_classification).setOnClickListener(this);
        mNum = (TextView) findViewById(R.id.num);
        mInput = (EditText) findViewById(R.id.input);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNum != null) {
                    mNum.setText(s.length() + "/50");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        shopLl = findViewById(R.id.shop_select);
        shop = findViewById(R.id.shop);
        vpSwitch = findViewById(R.id.vp_switch);
        mLocation = findViewById(R.id.location);
        classification = findViewById(R.id.classification);
        mCity = UserInfoUtil.getAddress();
        mLocation.setText(mCity);
        mTXCloudVideoView = findViewById(R.id.video_view);
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setConfig(new TXLivePlayConfig());
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer.setPlayListener(this);
        int result = mPlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
        if (result == 0) {
            mPlayStarted = true;
        }

        vpSwitch.setChecked(true);//默认开启位置
        vpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    mLocation.setTextColor(getResources().getColor(R.color.color_title_txt));
                } else {
                    mLocation.setTextColor(getResources().getColor(R.color.color_content_txt));
                }
            }
        });

        mClassCallback = new CommonCallback<CircleListBean.ListBean>() {
            @Override
            public void callback(CircleListBean.ListBean bean) {
                mVideoClassID = bean.getId();
                classification.setText(bean.getName());
            }
        };

        uploadDialog = new UploadDialog(this);
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        loadData();
        shopLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    fragment.setOnclick(new ShopSelectDialog.Onclick() {
                        @Override
                        public void OnClick(XuanPInResopnse.ItemData itemData) {
                            fragment.dismiss();
                            selectSop = itemData;
                            shop.setText(itemData.title);
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "ShopSelectDialog");
                }
            }
        });
    }

    XuanPInResopnse.ItemData selectSop;
    ShopSelectDialog fragment = new ShopSelectDialog();

    @Override
    public void onPlayEvent(int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
                onReplay();
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 获取到视频宽高回调
     */
    public void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                params.height = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                params.gravity = Gravity.CENTER;
                mTXCloudVideoView.requestLayout();
            }
        }
    }

    /**
     * 循环播放
     */
    private void onReplay() {
        if (mPlayStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayStarted && mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mPlayStarted && mPlayer != null) {
            mPlayer.resume();
        }
        mPaused = false;
    }

    public void release() {
        mPlayStarted = false;
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
    }

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/medal/goods", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                XuanPInResopnse datas = new Gson().fromJson(result, XuanPInResopnse.class);

                if (datas != null && datas.list != null && datas.list.size() > 0) {
                    shopLl.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogUitl.showSimpleHintDialog(mContext,
                getString(R.string.prompt),
                getString(R.string.ac_select_friend_sure),
                getString(R.string.cancel),
                "是否放弃发布此条视频",
                true,
                true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

//                        if (mSaveType == Constants.VIDEO_SAVE_PUB) {
//                            if (!TextUtils.isEmpty(mVideoPath)) {
//                                File file = new File(mVideoPath);
//                                if (file.exists()) {
//                                    file.delete();
//                                }
//                            }
//                        }
                        release();
                        VideoPublishActivity.super.onBackPressed();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        NLog.e(TAG, "-------->onDestroy");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_pub) {
            publishVideo();
        } else if (i == R.id.vp_classification) {
            chooseClass();
        }
    }

    /**
     * 选择分类
     */
    private void chooseClass() {
        Bundle bundle = new Bundle();
        bundle.putString(TCConstants.CLASS_ID, mVideoClassID);
        VideoClassDialogFragment fragment = new VideoClassDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mClassCallback);
        fragment.show(getSupportFragmentManager(), "VideoClassDialogFragment");
    }

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;

    /**
     * 发布视频
     */
    private void publishVideo() {
        String title = mInput.getText().toString().trim();
        //视频描述
        if (TextUtils.isEmpty(title)) {
            NToast.shortToast(mContext, "视频标题不能为空~");
            return;
        }
        if (TextUtils.isEmpty(mVideoClassID)) {
            NToast.shortToast(mContext, "请选择视频分类~");
            return;
        }
        mVideoTitle = title;
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }

        /*mLoading = DialogUitl.loadingDialog(mContext, "发布中");
        mLoading.show();
        Bitmap bitmap = null;
        //生成视频封面图
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mVideoPath);
            bitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        if (bitmap == null) {
            if (mLoading != null) {
                mLoading.dismiss();
            }
            NToast.shortToast(mContext, "生成视频封面图失败");
            return;
        }
        String coverImagePath = mVideoPath.replace(".mp4", ".jpg");
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            imageFile = null;
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (imageFile == null) {
            if (mLoading != null) {
                mLoading.dismiss();
            }
            NToast.shortToast(mContext, "生成视频封面图失败");
            return;
        }*/


        uploadDialog.show(300);
        uploadDialog.uploadVideo();
        uploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (uploadDialog.getProgress() >= 100)
                    LoadDialog.show(VideoPublishActivity.this);
            }
        });
        ossUploadList();
    }

    private void ossUploadList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> videos = new ArrayList<>();
                videos.add(mVideoPath);
                // 51 短视频
                uploadOssUtils.ossUploadList(videos, "video", 51, UserInfoUtil.getMiTencentId(), uploadDialog);
            }
        }).start();
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {

    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

        if (response != null) {
            OssVideoResponse response1 = response.get(0);
//            response1.getCover_img();
//            response1.getFull_url();
//            response1.getObject();
            saveUploadVideoInfo(response1.getObject());
        } else LoadDialog.dismiss(this);
    }

    /**
     * 把视频上传后的信息保存在服务器
     */
    private void saveUploadVideoInfo(String url) {
        if (!vpSwitch.isChecked()) {
            //关闭位置分享
            mCity = "";
        }
        String body = "";
        String xuanpin_id = "";
        if (selectSop != null) xuanpin_id = selectSop.id;
        LoadDialog.show(this);
        try {
            body = new JsonBuilder()
                    .put("type", "1")//1:普通视频 2:带货视频
                    .put("content", mVideoTitle)//内容,当类型为2时，必传
                    .put("video", url)//视频uri
                    .put("province_name", "")//省份名称
                    .put("city_name", mCity)//城市名称
                    .put("circle_id", mVideoClassID)//圈子ID
                    .put("goods_id", "0")//商品ID,当类型为2时，必传
                    .put("xuanpin_id", xuanpin_id)//商品ID,当类型为2时，必传

                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortvideo/addDynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(VideoPublishActivity.this);
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(VideoPublishActivity.this);
                NToast.shortToast(mContext, "发布成功~");
                finish();
            }
        });
    }

}

