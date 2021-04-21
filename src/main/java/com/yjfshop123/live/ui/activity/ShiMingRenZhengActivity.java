package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.webank.facelight.contants.WbFaceVerifyResult;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.authid.FaceVerfyProcess;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.CheckIdcardResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.RoundFrameLayout;
import com.yjfshop123.live.ui.widget.SelectCardTypeDialogFragment;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.MapUtil;
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
import butterknife.OnClick;

/**
 * 日期:2018/10/24
 * 描述:实名认证
 **/
public class ShiMingRenZhengActivity extends BaseActivityForNewUi implements View.OnClickListener, CosXmlUtils.OSSResultListener, MapLoiIml {

    public static final int REQUEST_CODE_SELECT = 1005;

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.renzhengPrewLayout)
    LinearLayout renzhengPrewLayout;
//    @BindView(R.id.renzhengSuccessLayout)
//    LinearLayout renzhengSuccessLayout;
//    @BindView(R.id.renzhengFaildLayout)
//    LinearLayout renzhengFaildLayout;

    @BindView(R.id.card1Layout)
    RoundFrameLayout card1Layout;
    @BindView(R.id.card2Layout)
    RoundFrameLayout card2Layout;

    @BindView(R.id.mengban1)
    ImageView mengban1;
    @BindView(R.id.mengban2)
    ImageView mengban2;

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.realName)
    EditText realName;
    @BindView(R.id.cardNumber)
    EditText cardNumber;
    @BindView(R.id.cityTxt)
    TextView cityTxt;
    @BindView(R.id.one)
    View one;
    @BindView(R.id.cityLayout)
    LinearLayout cityLayout;

    @BindView(R.id.shenfen_ll)
    LinearLayout shenfenLl;
    @BindView(R.id.huzhaoNumber)
    EditText huzhaoNumber;
    @BindView(R.id.huzhao_ll)
    LinearLayout huzhaoLl;
    @BindView(R.id.rightImg5)
    ImageView rightImg5;
    @BindView(R.id.shenfen_tupian_ll)
    LinearLayout shenfenTupianLl;
    @BindView(R.id.huzhao1)
    ImageView huzhao1;
    @BindView(R.id.huzhaoLayout)
    RoundFrameLayout huzhaoLayout;
    @BindView(R.id.huzhao_tupian_ll)
    LinearLayout huzhaoTupianLl;
    @BindView(R.id.shenfenzheng_tips)
    LinearLayout shenfenzhengTips;
    @BindView(R.id.huzhao_tips)
    LinearLayout huzhaoTips;
    @BindView(R.id.cardType)
    TextView cardType;
    @BindView(R.id.type_select)
    LinearLayout typeSelect;
    @BindView(R.id.laxia)
    ImageView laxia;

    private boolean isCard = false;

//    @BindView(R.id.renZheng)
//    Button renZheng;

    private Uri selectUri;

    private String path1 = "", path2 = "", huzhaoPath = "";
    private String localPath1 = "", localPath2 = "", localHuzhaoPath = "";
    private int flag = -1;

    private CosXmlUtils uploadOssUtils;
    private String userId;
    private FaceVerfyProcess faceVerfyProcess;
    private String country;
    private int idcard_type = 1;//1身份证 2护照
    private boolean isCanSelectCardType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        is_need_check_verfy = false;
        setContentView(R.layout.activity_shiming_renzheng);
        ButterKnife.bind(this);
        country = getIntent().getStringExtra("country");
        UserInfoUtil.setToken_InfoComplete(UserInfoUtil.getToken(), 0);

        setHeadLayout();
        initView();
        initEvent();

    }

    private void initView() {
        if (!TextUtils.isEmpty(country) && !country.equals("中国")) {
            cityLayout.setVisibility(View.GONE);
            isCanSelectCardType = true;
            laxia.setVisibility(View.VISIBLE);
        }
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.shiming_title);
    }

    private void initEvent() {
        typeSelect.setOnClickListener(this);
        submit.setOnClickListener(this);
        card1Layout.setOnClickListener(this);
        card2Layout.setOnClickListener(this);
//        renZheng.setOnClickListener(this);
        huzhaoLayout.setOnClickListener(this);
        userId = UserInfoUtil.getMiTencentId();
        int status = UserInfoUtil.getAuthStatus();

        if (status == 0) {//未提交

        } else if (status == 10) { //审核失败
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.dialog_title1),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        } else if (status == 2) {//通过
            submit.setVisibility(View.GONE);
            realName.setEnabled(false);
            cardNumber.setEnabled(false);
            card1Layout.setClickable(false);
            card2Layout.setClickable(false);
            getIdcard();
            NToast.shortToast(this, "实名认证审核通过");
        } else if (status == 1) {//审核中
            getIdcard();
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.dialog_title),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            finish();
                        }
                    });
        }

        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 18) {
                    isCard = true;
                } else {
                    isCard = false;
                }
            }
        });
        faceVerfyProcess = new FaceVerfyProcess(this);
        faceVerfyProcess.setListener(new FaceVerfyProcess.FaceListener() {

            @Override
            public void oncomplete() {

                LoadDialog.dismiss(ShiMingRenZhengActivity.this);
                NToast.shortToast(ShiMingRenZhengActivity.this, "");
            }

            @Override
            public void onError(String errInfo) {
                NLog.d("人脸核身onError", errInfo);
                NToast.shortToast(ShiMingRenZhengActivity.this, errInfo);
                LoadDialog.dismiss(ShiMingRenZhengActivity.this);
            }

            @Override
            public void onFail(WbFaceVerifyResult result) {
                LoadDialog.dismiss(ShiMingRenZhengActivity.this);
            }

            @Override
            public void onAuthSuccess(WbFaceVerifyResult result) {
                LoadDialog.dismiss(ShiMingRenZhengActivity.this);
                dialog = new UploadDialog(ShiMingRenZhengActivity.this);
                dialog.show(300);
                dialog.uploadPhoto(paths.size());
                ossUploadList();
            }
        });

        startMap();

    }

    private MapUtil mapUtil = new MapUtil(this, this);

    private void startMap() {
        mapUtil.LoPoi();
    }

    public String latitude = "";
    public String longitude = "";
    public String address = "";

    @Override
    public void onMapSuccess(double latitude, double longitude, String address, String currentWeizhi) {
        this.latitude = latitude + "";
        this.longitude = longitude + "";
        this.address = currentWeizhi;
        if (TextUtils.isEmpty(currentWeizhi) || currentWeizhi.equals(",,")) {
            cityTxt.setText("");
        } else {
            cityTxt.setText(currentWeizhi);
        }
    }

    @Override
    public void onMapFail(String msg) {

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
//                .cropSize(1, 1, 400, 400)
                .needCrop(false)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9)
                .build();

// 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_CODE_SELECT);

    }

    ArrayList<String> paths = new ArrayList<>();
    UploadDialog dialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (idcard_type == 1) {
                    //身份证上传
                    onShenfenUpload();
                } else {
                    //护照上传
                    onHuzhaoUpload();
                }
                break;
            case R.id.card1Layout:
                flag = 1;
                tickPhoto();
                break;
            case R.id.card2Layout:
                flag = 2;
                tickPhoto();
                break;
            case R.id.huzhaoLayout:
                flag = 3;
                tickPhoto();
                break;
            case R.id.type_select:
                if (isCanSelectCardType) SelectType();
                break;
        }
    }

    private void onShenfenUpload() {
        if (TextUtils.isEmpty(realName.getText().toString().trim())) {
            NToast.shortToast(this, "请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(cardNumber.getText().toString().trim())) {
            NToast.shortToast(this, "请输入身份证号");
            return;
        }
//        if (!isCard) {
//            NToast.shortToast(this, "请输入正确的身份证号");
//            return;
//        }
//        if (TextUtils.isEmpty(cityTxt.getText().toString())) {
//            if (!TextUtils.isEmpty(country) && !country.equals("中国")) {
//
//            } else {
//                NToast.shortToast(this, getString(R.string.qingxuanze_address));
//                return;
//            }
//        }

//        paths.clear();
//        if (path1.equals("") || path2.equals("")) {
//            Toast.makeText(this, R.string.daren_message, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        paths.add(path1);
//        paths.add(path2);

        //getAuthBaseData();
//        dialog = new UploadDialog(this);
//        dialog.show(300);
//        dialog.uploadPhoto(paths.size());
//        ossUploadList();
        DialogUitl.showSimpleDialog(this,
                "请确保身份信息准确，一旦设置无法修改!",
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        verifyIdcard();
                    }
                });


    }

    private void onHuzhaoUpload() {
        if (TextUtils.isEmpty(realName.getText().toString().trim())) {
            NToast.shortToast(this, "请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(huzhaoNumber.getText().toString().trim())) {
            NToast.shortToast(this, "请输入护照号码");
            return;
        }

//        if (TextUtils.isEmpty(cityTxt.getText().toString())) {
//            if (!TextUtils.isEmpty(country) && !country.equals("中国")) {
//
//            } else {
//                NToast.shortToast(this, getString(R.string.qingxuanze_address));
//                return;
//            }
//        }
//        paths.clear();
//        if (huzhaoPath.equals("")) {
//            Toast.makeText(this, R.string.daren_message, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        paths.add(huzhaoPath);
//
//        //getAuthBaseData();
//        dialog = new UploadDialog(this);
//        dialog.show(300);
//        dialog.uploadPhoto(paths.size());
//        ossUploadList();
        DialogUitl.showSimpleDialog(this,
                "请确保身份信息准确，一旦设置无法修改!",
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        verifyIdcard();
                    }
                });

    }

    private void SelectType() {
        SelectCardTypeDialogFragment dialogFragment = new SelectCardTypeDialogFragment();
        dialogFragment.setMainStartChooseCallback(new SelectCardTypeDialogFragment.ChooseCallback() {
            @Override
            public void onselect(String text, int type) {
                cardType.setText(text);
                if (type == 1) {
                    idcard_type = 1;
                    shenfenLl.setVisibility(View.VISIBLE);
                    shenfenTupianLl.setVisibility(View.GONE);
                    shenfenzhengTips.setVisibility(View.GONE);
                    huzhaoLl.setVisibility(View.GONE);
                    huzhaoTupianLl.setVisibility(View.GONE);
                    huzhaoTips.setVisibility(View.GONE);
                } else {
                    idcard_type = 2;
                    shenfenLl.setVisibility(View.GONE);
                    shenfenTupianLl.setVisibility(View.GONE);
                    shenfenzhengTips.setVisibility(View.GONE);
                    huzhaoLl.setVisibility(View.VISIBLE);
                    huzhaoTupianLl.setVisibility(View.GONE);
                    huzhaoTips.setVisibility(View.GONE);
                }
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "SelectCardTypeDialogFragment");
    }

    private void getAuthBaseData() {
        faceVerfyProcess.setId(cardNumber.getText().toString());
        faceVerfyProcess.setName(realName.getText().toString());
        LoadDialog.show(this);
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("idcard_no", cardNumber.getText().toString())
                    .put("real_name", realName.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/checkIdcard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                LoadDialog.dismiss(ShiMingRenZhengActivity.this);
            }

            @Override
            public void onSuccess(String result) {
                NLog.d("实名认证", result);
                CheckIdcardResponse checkIdcardResponse = new Gson().fromJson(result, CheckIdcardResponse.class);
                faceVerfyProcess.setNonce(checkIdcardResponse.getNonce_str());
                faceVerfyProcess.setOrderNo(checkIdcardResponse.getOrder_no());
                faceVerfyProcess.setSign(checkIdcardResponse.getSign());
                faceVerfyProcess.startProcess();
            }
        });
    }

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
                    ArrayList<Uri> list = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
                    if (flag == 1) {
                        path1 = list.get(0).getPath();
//                        Glide.with(ShiMingRenZhengActivity.this).load(path1).into(mengban1);
                    } else if (flag == 2) {
                        path2 = list.get(0).getPath();
//                        Glide.with(ShiMingRenZhengActivity.this).load(path2).into(mengban2);
                    }
//                    popupWindow.dismiss();
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

                //添加图片返回
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    if (flag == 1) {
                        path1 = pathList.get(0);
                        Glide.with(ShiMingRenZhengActivity.this).load(path1).into(mengban1);
                    } else if (flag == 2) {
                        path2 = pathList.get(0);
                        Glide.with(ShiMingRenZhengActivity.this).load(path2).into(mengban2);
                    } else if (flag == 3) {
                        huzhaoPath = pathList.get(0);
                        Glide.with(ShiMingRenZhengActivity.this).load(huzhaoPath).into(huzhao1);
                    }
                }
//                if (data != null && requestCode == REQUEST_CODE_SELECT) {
//                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                    if (images != null) {
//                        if (flag == 1) {
//                            path1 = images.get(0).path;
//                            Glide.with(ShiMingRenZhengActivity.this).load(path1).into(mengban1);
//                        } else if (flag == 2) {
//                            path2 = images.get(0).path;
//                            Glide.with(ShiMingRenZhengActivity.this).load(path2).into(mengban2);
//                        }
//                    }
//                }
                break;
        }
    }


    private void verifyIdcard() {

        JsonBuilder body = new JsonBuilder();
        try {
//            body.put("idcard_no", cardNumber.getText().toString())
//                    .put("idcard_type", idcard_type)//1身份证 2护照
//                    .put("real_name", realName.getText().toString());
            if (idcard_type == 1) {
                // body.put("idcard_front", path1)
                //     .put("idcard_back", path2)
                // .put("idcard_type", path2)//1身份证 2护照
                //
                //  .put("video_url", path2)
                body.put("idcard_no", cardNumber.getText().toString())
                        .put("idcard_type", idcard_type)//1身份证 2护照
                        .put("real_name", realName.getText().toString())
                ;
            } else {
//                body.put("idcard_front", huzhaoPath)
//                        // .put("idcard_type", path2)//1身份证 2护照
//                        .put("idcard_type", idcard_type)//1身份证 2护照
//                        .put("video_url", huzhaoPath)
                body.put("idcard_no", huzhaoNumber.getText().toString())
                        .put("real_name", realName.getText().toString())
                        .put("idcard_type", idcard_type)//1身份证 2护照
                ;
            }
            if (!TextUtils.isEmpty(longitude))
                body.put("longitude",String.valueOf(longitude));
            if (!TextUtils.isEmpty(latitude))
                body.put("latitude",String.valueOf(latitude));

            if (!TextUtils.isEmpty(country) && !country.equals("中国")) {
                body.put("province_name", country);
                body.put("city_name", country);
                body.put("district_name", country);
            } else {
                if (!TextUtils.isEmpty(address)) {
                    String[] location = address.split(",");
                    if (location.length == 0) {
                        NLog.d("城市獲取", "城市未获取到");
                    } else if (location.length == 1) {
                        body.put("province_name",location[0]);
                        body.put("city_name", location[0]);
                        body.put("district_name",location[0]);
                    } else if (location.length == 2) {
                        body.put("province_name",location[0]);
                        body.put("city_name", location[1]);
                        body.put("district_name",location[1]);
                    } else if (location.length == 3) {
                        body.put("province_name",location[0]);
                        body.put("city_name", location[1]);
                        body.put("district_name",location[2]);
                    }
                }
            }
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/user/verifyIdcard", body.build(), new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                //finish();
            }

            @Override
            public void onSuccess(String result) {
                UserInfoUtil.setAuthStatus(2);
                UserInfoUtil.setToken_InfoComplete(UserInfoUtil.getToken(), 1);
                Intent intent = new Intent(ShiMingRenZhengActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
//                DialogUitl.showSimpleHintDialog(mContext,
//                        getString(R.string.prompt),
//                        getString(R.string.dialog_title),
//                        true, new DialogUitl.SimpleCallback() {
//                            @Override
//                            public void onConfirmClick(Dialog dialog, String content) {
//                                dialog.dismiss();
//                                finish();
//                            }
//                        });
            }
        });
    }

    private void getIdcard() {
        OKHttpUtils.getInstance().getRequest("app/user/getIdcard", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    String idcard_front = data.getString("idcard_front");
                    String idcard_back = data.getString("idcard_back");
                    String idcard_no = data.getString("idcard_no");
                    String real_name = data.getString("real_name");
                    realName.setText(real_name);
                    cardNumber.setText(idcard_no);
                    Glide.with(ShiMingRenZhengActivity.this).load(CommonUtils.getUrl(idcard_front)).into(mengban1);
                    Glide.with(ShiMingRenZhengActivity.this).load(CommonUtils.getUrl(idcard_back)).into(mengban2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(paths, "image", 5, userId, dialog);
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("response", response);
        Message message = new Message();
        message.what = 1;
        message.setData(bundle);
        handler.sendMessage(message);
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
                                if (idcard_type == 1) {
                                    //身份证上传结果
                                    path1 = response1.getObject();
                                    localPath1 = response1.getFull_url();
                                    Glide.with(ShiMingRenZhengActivity.this).load(localPath1).into(mengban1);
                                } else {
                                    //护照上传结果
                                    huzhaoPath = response1.getObject();
                                    localHuzhaoPath = response1.getFull_url();
                                    Glide.with(ShiMingRenZhengActivity.this).load(localHuzhaoPath).into(huzhao1);
                                }
                                break;
                            case 1:
                                path2 = response1.getObject();
                                localPath2 = response1.getFull_url();
                                Glide.with(ShiMingRenZhengActivity.this).load(localPath2).into(mengban2);
                                break;
                        }

                    }
                    dialog.dissmis();
                    verifyIdcard();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapUtil != null) mapUtil.destroy();
        LoadDialog.dismiss(this);
        int status = UserInfoUtil.getAuthStatus();
        NLog.d("实名界面销毁", status + "");
        if (status == 0) {//未提交
            UserInfoUtil.setToken_InfoComplete("", 0);
            // finish();
        } else if (status == 10) { //审核失败
            UserInfoUtil.setToken_InfoComplete("", 0);
            //  finish();
        } else if (status == 2) {//通过
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
        } else if (status == 1) {//审核中
            UserInfoUtil.setToken_InfoComplete("", 0);
        }
    }


    @OnClick(R.id.cityLayout)
    public void onViewClicked() {

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
                String city = alertDialog.getCityStr();
                cityTxt.setText(city);
            }
        });
        alertDialog.show();
    }
}
