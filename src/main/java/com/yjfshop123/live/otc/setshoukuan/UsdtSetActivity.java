package com.yjfshop123.live.otc.setshoukuan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;

public class UsdtSetActivity extends BaseActivityH implements CosXmlUtils.OSSResultListener {
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
    @BindView(R.id.delete)
    TextView delete;

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    String id,select_id;
    boolean is_edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usdt_set);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
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
                    Glide.with(UsdtSetActivity.this).load(qrcode_url).into(erweima);
                }
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(select_id) && !TextUtils.isEmpty(id) && select_id.equals(id)) {
                        NToast.longToast(UsdtSetActivity.this,"默认收款方式，请勿删除，如需删除，请重新选择默认收款方式后再进行此操作！");
                        return;
                    }
                    DialogUitl.showSimpleHintDialog(UsdtSetActivity.this, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                            "确认删除收款方式？", true, true,
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
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, "删除成功");
                finish();
            }
        });

    }

    /**
     * 点击左按钮
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
                    .put("type", USDT_TYPE)
                    .put("qrcode_url", qrcode_url)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/editCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, "修改成功");
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
            body = new JsonBuilder()
                    .put("name", name.getText().toString())
                    .put("card", card.getText().toString())
                    .put("type", USDT_TYPE)

                    .put("qrcode_url", qrcode_url)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/trade/addCard", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(UsdtSetActivity.this);
                NToast.shortToast(UsdtSetActivity.this, "添加成功");
                finish();
            }
        });

    }

    PaySettingResponse.PayData data;

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
//                        if (TextUtils.isEmpty(qrcode_url)) {
//                            NToast.shortToast(this, getString(R.string.please_up_shoukuanma));
//                            return;
//                        }
                        confiry();
                    }
                } else {
                    if (TextUtils.isEmpty(selectPic)) {
                        confiry();
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
                //添加图片返回
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    selectPic = pathList.get(0);
                    add.setVisibility(View.GONE);
                    tips.setVisibility(View.GONE);
                    erweima.setVisibility(View.VISIBLE);
                    Glide.with(UsdtSetActivity.this).load(selectPic).into(erweima);

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
        ISNav.getInstance().toListActivity(this, config, 1);
    }
}
