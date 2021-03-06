package com.yjfshop123.live.otc.setshoukuan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.PaySettingResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;

public class AliSetActivity extends BaseActivityH implements CosXmlUtils.OSSResultListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.card)
    EditText card;
    @BindView(R.id.erweima)
    ImageView erweima;
    @BindView(R.id.tips)
    TextView tips;
    @BindView(R.id.add)
    ImageView add;
    String id, select_id;
    boolean is_edit = false;
    @BindView(R.id.delete)
    TextView delete;


    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    PaySettingResponse.PayData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_set);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //?????????????????????????????????
        params.height = SystemUtils.getStatusBarHeight(this);//?????????????????????????????????
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        uploadDialog = new UploadDialog(this);
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        if (getIntent() != null) {
            select_id = getIntent().getStringExtra("select_id");
            id = getIntent().getStringExtra("id");
            is_edit = getIntent().getBooleanExtra("is_edite", false);
            if (is_edit) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("data"))) {
                data = new Gson().fromJson(getIntent().getStringExtra("data"), PaySettingResponse.PayData.class);
                name.setText(data.name);
                card.setText(data.card);
                if (!TextUtils.isEmpty(data.qrcode_url)) {
                    qrcode_url = data.qrcode_url;
                    add.setVisibility(View.GONE);
                    tips.setVisibility(View.GONE);
                    erweima.setVisibility(View.VISIBLE);
                    Glide.with(AliSetActivity.this).load(qrcode_url).into(erweima);
                }
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(select_id) && !TextUtils.isEmpty(id) && select_id.equals(id)) {
                        NToast.longToast(AliSetActivity.this,"????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                        return;
                    }
                    DialogUitl.showSimpleHintDialog(AliSetActivity.this, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                            "???????????????????????????", true, true,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {
                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    delete();
                                }
                            });
                }
            });
        }
    }

    public void delete() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("bank_id", id)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/delCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, "????????????");
                finish();
            }
        });

    }

    /**
     * ???????????????
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    private String qrcode_url;

    public void fix() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("bank_id", id)
                    .put("name", name.getText().toString())
                    .put("card", card.getText().toString())
                    .put("type", ALIPAY_TYPE)
                    .put("qrcode_url", qrcode_url)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/editCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, "????????????");
                finish();
            }
        });

    }

    public void confiry() {
        if (is_edit) {
            fix();
            return;
        }
        String body = "";
        try {
            Log.d("??????", qrcode_url);
            body = new JsonBuilder()
                    .put("name", name.getText().toString())
                    .put("card", card.getText().toString())
                    .put("type", ALIPAY_TYPE)
                    .put("qrcode_url", qrcode_url)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/addCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, errInfo);

            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(AliSetActivity.this);
                NToast.shortToast(AliSetActivity.this, "????????????");
                finish();
            }
        });

    }

    @OnClick({R.id.select_erweima, R.id.confir_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_erweima:
                tickPhoto();
                break;
            case R.id.confir_btn:
                if (TextUtils.isEmpty(name.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.please_input_name));
                    return;
                }
                if (TextUtils.isEmpty(card.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.tixian_hint_ali));
                    return;
                }
                if (data != null) {
                    if (!TextUtils.isEmpty(selectPic)) {
                        uploadDialog.show(300);
                        uploadDialog.uploadPhoto(1);
                        uploadOssUtils.uploadData(selectPic, "image", 6, UserInfoUtil.getMiTencentId(), uploadDialog);
                    } else {
                        if (TextUtils.isEmpty(qrcode_url)) {
                            NToast.shortToast(this, getString(R.string.please_up_shoukuanma));
                            return;
                        }
                        confiry();
                    }
                } else {
                    if (TextUtils.isEmpty(selectPic)) {
                        NToast.shortToast(this, getString(R.string.please_up_shoukuanma));
                        return;
                    }
                    uploadDialog.show(300);
                    uploadDialog.uploadPhoto(1);
                    uploadOssUtils.uploadData(selectPic, "image", 6, UserInfoUtil.getMiTencentId(), uploadDialog);
                }

                break;
        }
    }

    String selectPic;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case PhotoUtils.INTENT_CROP:
//            case PhotoUtils.INTENT_TAKE:
//            case PhotoUtils.INTENT_SELECT:
//                photoUtils.onActivityResult(this, requestCode, resultCode, data);
//                break;

            case 1:
                //??????????????????
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    selectPic = pathList.get(0);
                    add.setVisibility(View.GONE);
                    tips.setVisibility(View.GONE);
                    erweima.setVisibility(View.VISIBLE);
                    Glide.with(AliSetActivity.this).load(selectPic).into(erweima);

                }

                break;
        }
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        qrcode_url = response.get(0).getObject();
        handler.sendEmptyMessage(1);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    confiry();
                    break;
            }
        }
    };

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    private void tickPhoto() {
        // ??????????????????
        ISListConfig config = new ISListConfig.Builder()
                // ????????????, ??????true
                .multiSelect(false)
                // ??????????????????????????????, ??????multiSelect???true???????????????????????????true
                .rememberSelected(false)
                // ???????????????????????????
                .btnBgColor(Color.TRANSPARENT)
                // ??????????????????????????????
                .btnTextColor(Color.WHITE)
                // ????????????????????????
                .statusBarColor(Color.parseColor("#B28D51"))
                // ????????????ResId
                .backResId(R.drawable.head_back)
                // ??????
                .title(getString(R.string.per_1))
                // ??????????????????
                .titleColor(Color.WHITE)
                // TitleBar?????????
                .titleBgColor(Color.parseColor("#B28D51"))
                // ???????????????needCrop???true???????????????
//                .cropSize(1, 1, 400, 400)
                .needCrop(false)
                // ????????????????????????????????????true
                .needCamera(true)
                // ?????????????????????????????????9
                .maxNum(9)
                .build();

// ????????????????????????
        ISNav.getInstance().toListActivity(this, config, 1);
    }
}
