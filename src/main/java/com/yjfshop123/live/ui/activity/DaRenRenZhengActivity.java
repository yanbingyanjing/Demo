package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.UserInfoResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.RoundFrameLayout;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:成为主播
 **/
public class DaRenRenZhengActivity extends BaseActivity implements View.OnClickListener, CosXmlUtils.OSSResultListener {

    public static final int REQUEST_CODE_SELECT = 1005;

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;

    @BindView(R.id.rootLayout1)
    RelativeLayout rootLayout1;
    @BindView(R.id.rootLayout2)
    RelativeLayout rootLayout2;
    @BindView(R.id.voiceJieshao)
    RelativeLayout voiceJieshao;

    @BindView(R.id.voiceJieshao_hint)
    TextView voiceJieshao_hint;

    @BindView(R.id.darenBtn)
    Button darenBtn;

    @BindView(R.id.card1Layout)
    RoundFrameLayout card1Layout;

    @BindView(R.id.mengban1)
    ImageView mengban1;

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.shenfen_one)
    ImageView shenfenOne;
    @BindView(R.id.shenfen_one_ll)
    RoundFrameLayout shenfenOneLl;
    @BindView(R.id.shenfen_two)
    ImageView shenfenTwo;
    @BindView(R.id.shenfen_two_ll)
    RoundFrameLayout shenfenTwoLl;
    @BindView(R.id.linear0)
    LinearLayout linear0;

    private String path1 = "";
    private String voicePath = "";
    private String shenfen1 = "";
    private String shenfen2 = "";
    private CosXmlUtils uploadOssUtils;

    private String userId;
    private int daren_status;
    private int auth_status;

    private ArrayList<String> paths = new ArrayList<>();
    private UploadDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daren_renzheng);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
        initEvent();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.daren_title);


        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    private void initEvent() {
        submit.setOnClickListener(this);
        card1Layout.setOnClickListener(this);
        darenBtn.setOnClickListener(this);
        voiceJieshao.setOnClickListener(this);
        shenfenOneLl.setOnClickListener(this);
        shenfenTwoLl.setOnClickListener(this);
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        getUserInfo();
    }


    private void getUserInfo() {
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(DaRenRenZhengActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(DaRenRenZhengActivity.this);
                initDatas(result);
            }
        });
    }

    private void initDatas(String result) {
        if (result == null) {
            return;
        }


        UserInfoResponse data = new Gson().fromJson(result, UserInfoResponse.class);


        UserInfoUtil.setUserInfo(
                data.sex,
                data.daren_status,
                data.is_vip,
                data.speech_introduction,
                data.auth_status,
                data.province_name + "," + data.city_name + "," + data.district_name,
                data.tags,
                data.age,
                data.signature);
        UserInfoUtil.setInviteCode(data.invite_code);
        userId = UserInfoUtil.getMiTencentId();
        daren_status = UserInfoUtil.getDarenStatus();
        auth_status = UserInfoUtil.getAuthStatus();
        if (daren_status == 0) {
            rootLayout1.setVisibility(View.GONE);
            rootLayout2.setVisibility(View.VISIBLE);
        } else if (daren_status == 1) {
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.callkit_voip_ok), getString(R.string.other_cancel),
                    getString(R.string.dialog_title), true, true,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {
                            finish();
                        }

                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });

            getDarenAuth();
        } else if (daren_status == 2) {
            voiceJieshao.setClickable(false);
            card1Layout.setClickable(false);
            submit.setVisibility(View.GONE);
            NToast.shortToast(this, "主播认证审核通过");
            getDarenAuth();
        } else if (daren_status == 10) {
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.dialog_title1),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
//                            finish();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                paths.clear();
                if (TextUtils.isEmpty(shenfen1)) {
                    Toast.makeText(this, "请上传身份证正面", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(shenfen2) ) {
                    Toast.makeText(this, "请上传身份证反面", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(path1)) {
                    Toast.makeText(this,"请上传本人生活照", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( TextUtils.isEmpty(voicePath)) {
                    Toast.makeText(this, "请上传语音介绍", Toast.LENGTH_SHORT).show();
                    return;
                }
                paths.add(path1);

                dialog = new UploadDialog(this);
                dialog.show(300);
                dialog.uploadPhoto(paths.size());
                ossUploadList();
                break;
            case R.id.card1Layout:
                tickPhoto();
                break;
            case R.id.card2Layout:
                tickPhoto();
                break;
            case R.id.shenfen_one_ll:
                tickPhotoByType(1);
                break;
            case R.id.shenfen_two_ll:
                tickPhotoByType(2);
                break;
            case R.id.darenBtn:
                if (auth_status != 2) {
                    smDialog();
                } else {
                    rootLayout1.setVisibility(View.VISIBLE);
                    rootLayout2.setVisibility(View.GONE);
                }
                break;
            case R.id.voiceJieshao:
                Intent intent = new Intent(this, VoiceJieShaoActivity.class);
//                intent.putExtra("userId", userId + "");
                startActivityForResult(intent, 14);
                break;
        }
    }
    private void tickPhotoByType(int type) {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#B28D51"))
                // 返回图标ResId
                .backResId(R.drawable.head_back)
                // 标题
                .title(getString(R.string.per_1))
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#B28D51"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 400, 400)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();

// 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, type);

    }
    private void tickPhoto() {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#B28D51"))
                // 返回图标ResId
                .backResId(R.drawable.head_back)
                // 标题
                .title(getString(R.string.per_1))
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#B28D51"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 400, 400)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();

// 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_CODE_SELECT);

    }

    private void smDialog() {
        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.my_renzhen), getString(R.string.other_cancel),
                getString(R.string.my_daren_desc), true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();
                        Intent intent = new Intent(DaRenRenZhengActivity.this, ShiMingRenZhengActivity.class);
                        startActivity(intent);
                    }
                });
    }

    //设置图片圆角角度
//    RequestOptions options = RequestOptions.circleCropTransform().override(200,50);
    //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
//    RequestOptions options = RequestOptions.bitmapTransform(requestOptions).override(200, 100);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case PhotoUtils.INTENT_CROP:
//            case PhotoUtils.INTENT_TAKE:
//            case PhotoUtils.INTENT_SELECT:
//                photoUtils.onActivityResult(this, requestCode, resultCode, data);
//                break;
            case 1:
                if (data != null) {
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        shenfen1 = pathList.get(0);
                        Glide.with(DaRenRenZhengActivity.this).load(shenfen1).into(shenfenOne);
                    }
                }
                break;
            case 2:
                if (data != null) {
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        shenfen2 = pathList.get(0);
                        Glide.with(DaRenRenZhengActivity.this).load(shenfen2).into(shenfenTwo);
                    }
                }
                break;
            case REQUEST_CODE_SELECT:
//                if (data != null) {
//                    ArrayList<Uri> list = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
//                    if (flag == 1) {
//                        path1 = list.get(0).getPath();
//                    }
////                    popupWindow.dismiss();
//                    break;
//                }

//                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                //添加图片返回
                if (data != null) {
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        path1 = pathList.get(0);
                        Glide.with(DaRenRenZhengActivity.this).load(path1).into(mengban1);
                    }
                }
//                if (data != null && requestCode == REQUEST_CODE_SELECT) {
//                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                    if (images != null) {
//                        if (flag == 1) {
//                            path1 = images.get(0).path;
//                            Glide.with(DaRenRenZhengActivity.this).load(path1).into(mengban1);
//                        }
////                    }
//                    }
//                }
// else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
//                    //预览图片返回
//                    if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
//                        images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
////                        if (images != null) {
////                            selImageList.clear();
////                            selImageList.addAll(images);
////                        }
//                    }
//                }
                break;
            case 14:
                if (data != null) {
                    String url = data.getStringExtra("url");
                    if (!TextUtils.isEmpty(url)) {
                        voicePath = url;
                    } else {
                        voicePath = UserInfoUtil.getSpeechIntroduction();
                    }
                } else {
                    voicePath = UserInfoUtil.getSpeechIntroduction();
                }
                if (!TextUtils.isEmpty(voicePath)) {
                    voiceJieshao_hint.setText("(已录制)");
                }
                break;
        }
    }

    //主播认证
    private void verifyDaren() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("speech_introduction", voicePath)
                    .put("life_photo", path1)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/verifyDaren", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                finish();
            }

            @Override
            public void onSuccess(String result) {
                DialogUitl.showSimpleHintDialog(mContext,
                        getString(R.string.prompt),
                        getString(R.string.dialog_title),
                        true, new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                finish();
                            }
                        });
            }
        });
    }

    private void getDarenAuth() {
        OKHttpUtils.getInstance().getRequest("app/user/getDarenAuth", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    //String hand_idcard;
                    String life_photo = data.getString("life_photo");
                    //Glide.with(mContext).load(life_photo).into(mengban1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(paths, "image", 4, userId, dialog);
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        for (int i = 0; i < response.size(); i++) {
            OssImageResponse response1 = response.get(i);
            switch (i) {
                case 0:
                    path1 = response1.getObject();
                    break;
            }
        }
        verifyDaren();
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ArrayList<OssImageResponse> response = (ArrayList<OssImageResponse>) msg.getData().getSerializable("response");
                    for (int i = 0; i < response.size(); i++) {
                        OssImageResponse response1 = response.get(i);
                        switch (i) {
                            case 0:
                                path1 = response1.getObject();
                                Glide.with(DaRenRenZhengActivity.this).load(response1.getFull_url()).into(mengban1);
                                break;
                        }
                    }
                    dialog.dissmis();
                    verifyDaren();
                    break;
            }
        }
    };

}
