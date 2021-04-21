package com.yjfshop123.live.ctc.order;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.OtcOrderResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.taskcenter.UploadAdapter;
import com.yjfshop123.live.taskcenter.UploadSucaiAcitivity;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.widget.ShowCommonDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.DateFormatUtil;
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
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;
import static com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity.REQUEST_CODE_SELECT;

public class CrtcOrderPayActivity extends BaseActivityH implements CosXmlUtils.OSSResultListener {
    OtcOrderResponse otcOrderResponse;
    String result;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.time_left)
    TextView timeLeft;
    @BindView(R.id.jine)
    TextView jine;
    @BindView(R.id.shoukuanren)
    TextView shoukuanren;
    @BindView(R.id.name_copy)
    ImageView nameCopy;
    @BindView(R.id.bank_name)
    TextView bankName;
    @BindView(R.id.bank_name_copy)
    ImageView bankNameCopy;
    @BindView(R.id.bank_create_name)
    TextView bankCreateName;
    @BindView(R.id.bank_create_name_copy)
    ImageView bankCreateNameCopy;
    @BindView(R.id.bank_account)
    TextView bankAccount;
    @BindView(R.id.bank_account_copy)
    ImageView bankAccountCopy;
    @BindView(R.id.bank_ll)
    LinearLayout bankLl;
    @BindView(R.id.shoukuanren_other)
    TextView shoukuanrenOther;
    @BindView(R.id.name_other_copy)
    ImageView nameOtherCopy;
    @BindView(R.id.shoukuan_account_other)
    TextView shoukuanAccount;
    @BindView(R.id.shoukuan_account_other_copy)
    ImageView shoukuanAccountCopy;
    @BindView(R.id.erweima)
    ImageView erweima;
    @BindView(R.id.other_ll)
    LinearLayout otherLl;
    @BindView(R.id.pay_btn)
    TextView payBtn;
    String payTypeSelect = BANK_TYPE;
    @BindView(R.id.list)
    RecyclerView uploadList;
    @BindView(R.id.select_fukuan)
    LinearLayout selectFukuan;
    private LinearLayoutManager mLinearLayoutManager;
    UploadAdapter adapter;
    public ArrayList<String> dataList = new ArrayList<>();
    public ArrayList<String> dataListUpload = new ArrayList<>();
    private CosXmlUtils uploadOssUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_order_pay);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        selectFukuan.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            result = getIntent().getStringExtra("result");
            if (!TextUtils.isEmpty(result)) {
                otcOrderResponse = new Gson().fromJson(result, OtcOrderResponse.class);
            }
        }

        if (otcOrderResponse != null) {
            jine.setText(otcOrderResponse.money);

            shoukuanren.setText(otcOrderResponse.real_name);
            shoukuanrenOther.setText(otcOrderResponse.real_name);
            if (!TextUtils.isEmpty(otcOrderResponse.pay_type)) {
                if (otcOrderResponse.pay_type.equals("card")) {
                    tvTitleCenter.setText(getString(R.string.yinhangka));
                    payTypeSelect = BANK_TYPE;
                    bankLl.setVisibility(View.VISIBLE);
                    otherLl.setVisibility(View.GONE);
                    bankName.setText(otcOrderResponse.bank);
                    bankCreateName.setText(otcOrderResponse.bank_branch);
                    bankAccount.setText(otcOrderResponse.pay_address);
                }
                if (otcOrderResponse.pay_type.equals("alipay") || otcOrderResponse.pay_type.equals("wechat") || otcOrderResponse.pay_type.equals(USDT_TYPE)) {
                    bankLl.setVisibility(View.GONE);
                    otherLl.setVisibility(View.VISIBLE);
                    shoukuanAccount.setText(otcOrderResponse.pay_address);
                    if (otcOrderResponse.pay_type.equals("alipay")) {
                        tvTitleCenter.setText(getString(R.string.tixian_ali));
                        payTypeSelect = ALIPAY_TYPE;
                    }
                    if (otcOrderResponse.pay_type.equals("wechat")) {
                        tvTitleCenter.setText(getString(R.string.setting_wx));
                        payTypeSelect = WECHAT_TYPE;
                    }
                    if (otcOrderResponse.pay_type.equals(USDT_TYPE)) {
                        tvTitleCenter.setText("USDT");
                        payTypeSelect = USDT_TYPE;
                    }
                }
            }
            if (!TextUtils.isEmpty(otcOrderResponse.expire_time)) {
                if (timer != null) timer.cancel();
                long timeLe = DateFormatUtil.getDateLong(otcOrderResponse.expire_time) - System.currentTimeMillis();
                if (timeLe <= 0) {
                    //超时
                    timeLeft.setText(R.string.has_chaoshi);
                } else {
                    timer = new CountDownTimer(timeLe, 1000) {
                        /**
                         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                         * @param millisUntilFinished
                         */
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timeLeft.setText(millisUntilFinished / 60000 + "分" + (millisUntilFinished / 1000) % 60 + "秒");
                        }

                        /**
                         * 倒计时完成时被调用
                         */
                        @Override
                        public void onFinish() {
                            timeLeft.setText(R.string.has_chaoshi);
                        }
                    };
                    timer.start();
                }
            }

        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        uploadList.setLayoutManager(mLinearLayoutManager);
        adapter = new UploadAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tickPhoto();
            }
        });
        uploadList.setAdapter(adapter);
        adapter.setCards(dataList);
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    CountDownTimer timer;

    @OnClick({R.id.name_copy, R.id.bank_name_copy, R.id.bank_create_name_copy, R.id.bank_account_copy, R.id.name_other_copy, R.id.shoukuan_account_other_copy, R.id.erweima, R.id.pay_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.name_copy:
                SystemUtils.setClipboard(this, shoukuanren.getText().toString());
                break;
            case R.id.bank_name_copy:
                SystemUtils.setClipboard(this, bankName.getText().toString());
                break;
            case R.id.bank_create_name_copy:
                SystemUtils.setClipboard(this, bankCreateName.getText().toString());
                break;
            case R.id.bank_account_copy:
                SystemUtils.setClipboard(this, bankAccount.getText().toString());
                break;
            case R.id.name_other_copy:
                SystemUtils.setClipboard(this, shoukuanrenOther.getText().toString());
                break;
            case R.id.shoukuan_account_other_copy:
                SystemUtils.setClipboard(this, shoukuanAccount.getText().toString());
                break;
            case R.id.erweima:
                try {
                    List<String> images = new ArrayList<>();
                    images.add(otcOrderResponse.code_url);
                    Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                    intent.putExtra(Config.POSITION, 0);
                    intent.putExtra("Picture", JsonMananger.beanToJson(images));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pay_btn:
                if (dataListUpload != null && dataListUpload.size() > 0) {
                    dialogFragment.setTitle(getString(R.string.confir_pay_tips)
                    );
                    dialogFragment.setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirPay();
                        }
                    });
                    dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");
                } else {
                    if (dataList == null || dataList.size() <= 0) {
                        NToast.shortToast(CrtcOrderPayActivity.this, "请上传至少一张付款凭证");
                        return;
                    }
                    dialogFragment.setTitle(getString(R.string.confir_pay_tips)
                    );
                    dialogFragment.setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog = new UploadDialog(CrtcOrderPayActivity.this);
                            dialog.show(300);
                            dialog.uploadPhoto(dataList.size());
                            ossUploadList();
                        }
                    });
                    dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");

                }

                break;
        }
    }

    ShowCommonDialog dialogFragment = new ShowCommonDialog();

    private class Reques {
        public String buy_order;

        public ArrayList<String> pics;

    }

    public void confirPay() {
        if (otcOrderResponse == null) return;
        String body = "";
        if (TextUtils.isEmpty(otcOrderResponse.order)) return;
        try {
            Reques reques = new Reques();
            reques.buy_order = otcOrderResponse.order;
            reques.pics = dataListUpload;
            body = JsonMananger.beanToJson(reques);

        } catch (HttpException e) {
        }
        LoadDialog.show(CrtcOrderPayActivity.this);
        OKHttpUtils.getInstance().getRequest("app/trade/buyRelease", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(CrtcOrderPayActivity.this);
                NToast.shortToast(CrtcOrderPayActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(CrtcOrderPayActivity.this);
                DialogUitl.showSimpleHintDialog(CrtcOrderPayActivity.this, "", getString(R.string.confirm),
                        "",
                        getString(R.string.pay_complete_dengdai_fangxing),
                        true,
                        false,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                                setResult(1);
                                finish();
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                setResult(1);
                                finish();
                            }
                        });


            }
        });
    }


    private void tickPhoto() {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(true)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (data != null) {
                    ArrayList<String> pathList = data.getStringArrayListExtra("result");
                    if (dataList != null && pathList != null) dataList.addAll(pathList);
                    //  dataList = pathList;
                    adapter.setCards(dataList);
                }
                break;
        }
    }

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(dataList, "image", 5, UserInfoUtil.getMiPlatformId(), dialog);
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

    UploadDialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ArrayList<OssImageResponse> response = (ArrayList<OssImageResponse>) msg.getData().getSerializable("response");
                    for (int i = 0; i < response.size(); i++) {
                        OssImageResponse response1 = response.get(i);
                        dataListUpload.add(response1.getObject());
                    }
                    dialog.dissmis();
                    confirPay();
                    break;
            }
        }
    };

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }
}
