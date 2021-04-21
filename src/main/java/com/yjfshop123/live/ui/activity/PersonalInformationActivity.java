package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.request.UserInfoRequest;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.person.PersonNameFixActivity;
import com.yjfshop123.live.ui.widget.SexSelectDialogFragment;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSAgeAlertDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;
import com.yjfshop123.live.utils.HandlerUtils;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 日期:2018/10/20
 * 描述: 个人资料
 **/
public class PersonalInformationActivity extends BaseActivityForNewUi implements View.OnClickListener, CosXmlUtils.OSSResultListener {

    public static final int REQUEST_CODE_SELECT = 1005;
    @BindView(R.id.headerBack)
    ImageView headerBack;
    @BindView(R.id.headerText)
    TextView headerText;
    @BindView(R.id.nextTxt)
    TextView nextTxt;
    @BindView(R.id.status_bar_view)
    View status_bar_view;
    @BindView(R.id.btn_left)
    ImageView btn_left;
    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.text_right)
    TextView text_right;
    @BindView(R.id.layout_head)
    RelativeLayout layout_head;
    @BindView(R.id.headerImgLayout)
    CircleImageView autorImg;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.nickNameLayout)
    LinearLayout nickNameLayout;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.sexTxt)
    TextView sexTxt;
    @BindView(R.id.rightImg3)
    ImageView rightImg3;
    @BindView(R.id.cityTxt)
    TextView cityTxt;
    @BindView(R.id.rightImg5)
    ImageView rightImg5;
    @BindView(R.id.cityLayout)
    LinearLayout cityLayout;
    @BindView(R.id.ageTxt)
    TextView ageTxt;
    @BindView(R.id.rightImg4)
    ImageView rightImg4;
    @BindView(R.id.ageLayout)
    LinearLayout ageLayout;
    @BindView(R.id.txt2)
    TextView txt2;
    @BindView(R.id.tagsTxt)
    TextView tagsTxt;
    @BindView(R.id.rightImg8)
    ImageView rightImg8;
    @BindView(R.id.myTagLayout)
    LinearLayout myTagLayout;
    @BindView(R.id.speechIntroduction)
    TextView speechIntroduction;
    @BindView(R.id.rightImg6)
    ImageView rightImg6;
    @BindView(R.id.voiceLayout)
    LinearLayout voiceLayout;
    @BindView(R.id.txt1)
    TextView txt1;
    @BindView(R.id.signatureTxt)
    TextView signatureTxt;
    @BindView(R.id.rightImg7)
    ImageView rightImg7;
    @BindView(R.id.signatureLayout)
    LinearLayout signatureLayout;
    @BindView(R.id.saveBtn)
    Button saveBtn;


    private String localUri;//本地头像

    private String voicePath;
    private String localVoice;

    private String oldData = "";
    private String newData = "";
    private UserInfoRequest userInfoRequest;
    private CosXmlUtils uploadOssUtils;
    private HandlerUtils handlerUtils;

    private String tags;
    private String userSignature;
    private String mAvatar;
    private String mName;
    private int mSex;
    private int mAge;
    private String mCity;
    private String mUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_nformation);
        ButterKnife.bind(PersonalInformationActivity.this);
        StatusBarUtil.StatusBarDarkMode(this);
        mContext = this;
        userInfoRequest = new UserInfoRequest();
        initEvent();
    }

    private void initEvent() {
        tv_title_center.setVisibility(View.VISIBLE);
        setCenterTitleText(getString(R.string.persional_ziliao));
        text_right.setVisibility(View.VISIBLE);
        text_right.setText(R.string.bind_save);
        text_right.setOnClickListener(this);

        autorImg.setOnClickListener(this);
       // cityLayout.setOnClickListener(this);
        ageLayout.setOnClickListener(this);
        myTagLayout.setOnClickListener(this);
        signatureLayout.setOnClickListener(this);
        nickNameLayout.setOnClickListener(this);
        voiceLayout.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        sexTxt.setOnClickListener(this);
        if (ActivityUtils.IS_VIDEO()) {
            myTagLayout.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.GONE);
        }

        initData();

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    private void initData() {
        mAvatar = UserInfoUtil.getAvatar();
        mName = UserInfoUtil.getName();
        mSex = UserInfoUtil.getSex();
        mAge = UserInfoUtil.getAge();
        mCity = UserInfoUtil.getCity();
        tags = UserInfoUtil.getTags();
        userSignature = UserInfoUtil.getUserSignature();
        localVoice = UserInfoUtil.getSpeechIntroduction();
        mUserid = UserInfoUtil.getMiTencentId();
        if (!TextUtils.isEmpty(mAvatar))
            Glide.with(this).load(mAvatar).into(autorImg);
        userName.setText(mName);
        sexTxt.setText(mSex == 1 ? "男" : "女");

        if (mAge == 0) {
            ageTxt.setText(R.string.other_choose);
        } else {
            ageTxt.setText(String.valueOf(mAge));
        }

        if (TextUtils.isEmpty(mCity)) {
            cityTxt.setText(R.string.other_choose);
//            cityTxt.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            cityTxt.setText(mCity);
//            cityTxt.setTextColor(getResources().getColor(R.color.color_333333));
        }

        if (TextUtils.isEmpty(tags)) {
            tagsTxt.setText(R.string.other_choose);
//            tagsTxt.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            tagsTxt.setText(tags);
//            tagsTxt.setTextColor(getResources().getColor(R.color.color_333333));
        }

        if (TextUtils.isEmpty(userSignature)) {
            signatureTxt.setText(R.string.persional_desc);
//            signatureTxt.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            signatureTxt.setText(userSignature);
//            signatureTxt.setTextColor(getResources().getColor(R.color.color_333333));
        }

        if (TextUtils.isEmpty(localVoice)) {
            speechIntroduction.setText(R.string.persional_jieshao_desc);
//            speechIntroduction.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            speechIntroduction.setText(R.string.persional_jieshao);
//            speechIntroduction.setTextColor(getResources().getColor(R.color.color_333333));
        }

        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        handlerUtils = new HandlerUtils(this);

        userInfoRequest.setSex(mSex);
        userInfoRequest.setAvatar(mAvatar);
        userInfoRequest.setUser_nickname(mName);
        userInfoRequest.setAge(mAge);
        userInfoRequest.setSignature(userSignature);
        userInfoRequest.setSpeech_introduction(voicePath);
        String[] items = mCity.split(",");
        if (items.length > 0) {
            if (items.length == 2) {
                userInfoRequest.setProvince_name(items[0]);
                userInfoRequest.setCity_name(items[1]);
                userInfoRequest.setDistrict_name(items[1]);
            } else {
                userInfoRequest.setProvince_name(items[0]);
                userInfoRequest.setCity_name(items[1]);
                userInfoRequest.setDistrict_name(items[2]);
            }
        }
        userInfoRequest.setTags(tags);
        try {
            oldData = JsonMananger.beanToJson(userInfoRequest);
        } catch (HttpException e) {
            e.printStackTrace();
        }
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
        ISNav.getInstance().toListActivity(this, config, 1005);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.sexTxt:
                SexSelectDialogFragment dialogFragment = new SexSelectDialogFragment();
                dialogFragment.setMainStartChooseCallback(new MainStartChooseCallback() {
                    @Override
                    public void onLiveClick() {
                        //男
                        userInfoRequest.setSex(1);
                        sexTxt.setText("男");
                    }

                    @Override
                    public void onVideoClick() {
                        //女
                        userInfoRequest.setSex(2);
                        sexTxt.setText("女");
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "SexSelectDialogFragment");
                break;
            case R.id.btn_left:
                exit();
                break;
            case R.id.headerImgLayout:
                tickPhoto();
                break;
            case R.id.ageLayout:
                final IOSAgeAlertDialog ageAlertDialog = new IOSAgeAlertDialog(this);
                ageAlertDialog.builder(ageTxt.getText().toString()).setTitle(getString(R.string.wszl_age));
                ageAlertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                ageAlertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAge = Integer.parseInt(ageAlertDialog.getAgeStr());
                        ageTxt.setText(ageAlertDialog.getAgeStr());
//                        ageTxt.setTextColor(getResources().getColor(R.color.color_333333));
                        userInfoRequest.setAge(mAge);
                    }
                });
                ageAlertDialog.show();
                break;
            case R.id.cityLayout:
                final IOSCityAlertDialog alertDialog = new IOSCityAlertDialog(this);
                alertDialog.builder().setTitle(getString(R.string.wszl_city));
                alertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                alertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCity = alertDialog.getCityStr();
                        cityTxt.setText(mCity);
//                        cityTxt.setTextColor(getResources().getColor(R.color.color_333333));
                        String[] items = mCity.split(",");
                        if (items.length == 2) {
                            userInfoRequest.setProvince_name(items[0]);
                            userInfoRequest.setCity_name(items[1]);
                            userInfoRequest.setDistrict_name(items[1]);
                        } else {
                            userInfoRequest.setProvince_name(items[0]);
                            userInfoRequest.setCity_name(items[1]);
                            userInfoRequest.setDistrict_name(items[2]);
                        }
                    }
                });
                alertDialog.show();
                break;
            case R.id.myTagLayout:
                intent.setClass(this, MyTagActivity1.class);
                intent.putExtra("content", tags);
                startActivityForResult(intent, 12);
                break;
            case R.id.signatureLayout:
                intent.setClass(this, MySignatureActivity.class);
                intent.putExtra("content", userSignature);
                startActivityForResult(intent, 1);
                break;
            case R.id.nickNameLayout:
//                DialogUitl.showSimpleInputDialog(mContext, getString(R.string.per_2), DialogUitl.INPUT_TYPE_TEXT, 8,
//                        true, new DialogUitl.SimpleCallback() {
//                            @Override
//                            public void onConfirmClick(Dialog dialog, String content) {
//                                if (TextUtils.isEmpty(content)) {
//                                    NToast.shortToast(mContext, R.string.wszl_input_nick);
//                                    return;
//                                }
//
//                                content = content.trim();
//                                if (TextUtils.isEmpty(content)) {
//                                    NToast.shortToast(mContext, "昵称不能为空格");
//                                    return;
//                                }
//
//                                mName = content;
//                                userName.setText(mName);
////                                userName.setTextColor(getResources().getColor(R.color.color_333333));
//                                userInfoRequest.setUser_nickname(mName);
//                                dialog.dismiss();
//                            }
//                        });
                Intent intent1 = new Intent(this, PersonNameFixActivity.class);
                intent1.putExtra("name", mName);
                startActivityForResult(intent1, 1001);
                break;
            case R.id.voiceLayout:
                intent.setClass(this, VoiceJieShaoActivity.class);
                startActivityForResult(intent, 14);
                break;
            case R.id.text_right:

//                if (TextUtils.isEmpty(mAvatar) && TextUtils.isEmpty(localUri)) {
//                    NToast.shortToast(this, "请完善信息");
//                    return;
//                }
//                if (TextUtils.isEmpty(userName.getText().toString())) {
//                    NToast.shortToast(this, "请完善信息");
//                    return;
//                }

                if (localUri != null) {
//                    ArrayList<String> paths = new ArrayList<>();
//                    paths.add(selectUri);
//                    if (!localUri.contains(BuildConfig.APPLICATION_ID) || !userInfoRequest.getAvatar().contains(BuildConfig.APPLICATION_ID)) {
//                        ();
//                    } else {
                    //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
                    uploadOssUtils.uploadData(localUri, "image", 6, mUserid, null);
//                    }
                } else {
                    saveUserInfo();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1://个性签名返回
                if (data != null) {
                    userSignature = data.getStringExtra("result");
                    if (TextUtils.isEmpty(userSignature)) {
                        signatureTxt.setText(R.string.persional_desc);
//                        signatureTxt.setTextColor(getResources().getColor(R.color.color_999999));
                    } else {
                        signatureTxt.setText(userSignature);
//                        signatureTxt.setTextColor(getResources().getColor(R.color.color_333333));
                    }
                    userInfoRequest.setSignature(userSignature);
                }
                break;
            case 12:
                if (data != null) {
                    String tags_ = data.getStringExtra("result");
                    tags = "";
                    if (!TextUtils.isEmpty(tags_)) {
                        tags = tags_;
                    }

                    if (TextUtils.isEmpty(tags)) {
                        tagsTxt.setText(R.string.other_choose);
//                        tagsTxt.setTextColor(getResources().getColor(R.color.color_999999));
                    } else {
                        tagsTxt.setText(tags);
//                        tagsTxt.setTextColor(getResources().getColor(R.color.color_333333));
                    }

                    userInfoRequest.setTags(tags);
                }
                break;
            case 14:
                if (data != null) {
                    String url = data.getStringExtra("url");
                    if (!TextUtils.isEmpty(url)) {
                        voicePath = url;
                        speechIntroduction.setText(R.string.persional_jieshao);
//                        speechIntroduction.setTextColor(getResources().getColor(R.color.color_333333));
                    }
                    userInfoRequest.setSpeech_introduction(voicePath);
                }
                break;
            case REQUEST_CODE_SELECT:
                if (data != null) {
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        localUri = pathList.get(0);
                        userInfoRequest.setAvatar("");
                        Glide.with(PersonalInformationActivity.this).load(localUri).into(autorImg);
                    }
                }
                break;
            case 1001:
                if (data != null) {
                    String name = data.getStringExtra("name");
                    if (!TextUtils.isEmpty(name)) {
                        mName = name;
                        userInfoRequest.setUser_nickname(mName);
                        userName.setText(mName);
                    }

                }
                break;
        }
    }

    private void saveUserInfo() {
        handlerUtils.show();
        try {
            String json = JsonMananger.beanToJson(userInfoRequest);
            OKHttpUtils.getInstance().getRequest("app/user/modifyUserInfo", json, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, errInfo);
                }

                @Override
                public void onSuccess(String result) {
                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, R.string.other_save_success);
                    UserInfoUtil.setAvatar(mAvatar);
                    UserInfoUtil.setName(mName);
                    finish();
                }
            });
        } catch (HttpException e) {
            e.printStackTrace();
            NToast.shortToast(mContext, "保存失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        mAvatar = response.get(0).getFull_url();
        userInfoRequest.setAvatar(mAvatar);
//        userInfoRequest.setAvatar(response.get(0).getObject());
        saveUserInfo();
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onHeadLeftButtonClick(View v) {
        exit();
    }

    private void exit() {
        try {
            newData = JsonMananger.beanToJson(userInfoRequest);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        if (!oldData.equals(newData)) {
//            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
//                    "当前资料未保存，是否放弃编辑", true, true,
//                    new DialogUitl.SimpleCallback2() {
//                        @Override
//                        public void onCancelClick() {
//                        }
//
//                        @Override
//                        public void onConfirmClick(Dialog dialog, String content) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
            if (localUri != null) {
//                    ArrayList<String> paths = new ArrayList<>();
//                    paths.add(selectUri);
//                    if (!localUri.contains(BuildConfig.APPLICATION_ID) || !userInfoRequest.getAvatar().contains(BuildConfig.APPLICATION_ID)) {
//                        ();
//                    } else {
                //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
                uploadOssUtils.uploadData(localUri, "image", 6, mUserid, null);
//                    }
            } else {
                saveUserInfo();
            }
        } else {
            finish();
        }
    }

}
