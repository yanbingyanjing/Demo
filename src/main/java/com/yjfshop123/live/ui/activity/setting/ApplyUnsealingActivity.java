package com.yjfshop123.live.ui.activity.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;
import com.yjfshop123.live.ui.adapter.ApplyUnsealingAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity.REQUEST_CODE_SELECT;

/**
 * 申请解封
 */
public class ApplyUnsealingActivity extends BaseActivityForNewUi implements CosXmlUtils.OSSResultListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.text_right)
    TextView textRight;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    @BindView(R.id.layout_container)
    FrameLayout layoutContainer;
    @BindView(R.id.real_name)
    EditText realName;
    @BindView(R.id.contact_phone)
    EditText contactPhone;
    @BindView(R.id.signatureTxt)
    EditText signatureTxt;
    @BindView(R.id.currentTxt)
    TextView currentTxt;
    @BindView(R.id.upload_list)
    RecyclerView uploadList;
    @BindView(R.id.confir)
    Button confir;
    private static final int MAX_NUM = 300;
    @BindView(R.id.scroll)
    ScrollView scroll;
    private LinearLayoutManager mLinearLayoutManager;
    private ApplyUnsealingAdapter adapter;
    public ArrayList<String> dataList = new ArrayList<>();
    //上传以后的路径
    public ArrayList<String> dataListUpload = new ArrayList<>();
    CosXmlUtils uploadOssUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.unsealing_title));
        setContentView(R.layout.activity_apply_unsealing);
        ButterKnife.bind(this);
        signatureTxt.addTextChangedListener(watcher);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        uploadList.setLayoutManager(mLinearLayoutManager);
        adapter = new ApplyUnsealingAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tickPhoto();
            }
        });
        uploadList.setAdapter(adapter);
        adapter.setCards(dataList);
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    hideKeyBord();
                }
            });
        }
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
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

    Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<ApplyUnsealingActivity> mActivity;

        public MyHandler(ApplyUnsealingActivity activity) {
            mActivity = new WeakReference<ApplyUnsealingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    ArrayList<OssImageResponse> response = (ArrayList<OssImageResponse>) msg.getData().getSerializable("response");
                    for (int i = 0; i < response.size(); i++) {
                        OssImageResponse response1 = response.get(i);
                        mActivity.get().dataListUpload.add(response1.getObject());
                    }
                    mActivity.get().dialog.dissmis();
                    mActivity.get().apply();

                    break;
            }
        }
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    private void tickPhoto() {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(true)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(true)
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
                    dataList = pathList;
                    adapter.setCards(dataList);
                }
                break;
        }
    }


    TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
            Log.i("onTextChanged", s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
            Log.i("beforeTextChanged", s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() > MAX_NUM) {
                s.delete(MAX_NUM, s.length());
            }
            int num = MAX_NUM - s.length();
            currentTxt.setText(s.length() + "/" + MAX_NUM + "字");
        }
    };

    @OnClick(R.id.confir)
    public void onViewClicked() {
        if (TextUtils.isEmpty(realName.getText().toString())) {
            NToast.shortToast(this, getString(R.string.hint_input_real_name));
            return;
        }
        if (TextUtils.isEmpty(contactPhone.getText().toString())) {
            NToast.shortToast(this, getString(R.string.hint_input_contact_phone));
            return;
        }
        if (TextUtils.isEmpty(signatureTxt.getText().toString())) {
            NToast.shortToast(this, getString(R.string.hint_input_unsealing_seaon));
            return;
        }
        if (dataList.size() <= 0) {
            NToast.shortToast(this, getString(R.string.hint_input_jietu));
            return;
        }
        dialog = new UploadDialog(this);
        dialog.show(300);
        dialog.uploadPhoto(dataList.size());
        ossUploadList();
    }

    public UploadDialog dialog;

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(dataList, "image", 1, UserInfoUtil.getMiTencentId(), dialog);
    }
    private void apply() {

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("name", realName.getText().toString())
                    .put("mobile", contactPhone.getText().toString())
                    .put("reason", signatureTxt.getText().toString())
                    .put("photos", new Gson().toJson(dataListUpload))

                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/user/unsealing", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(ApplyUnsealingActivity.this);
                NToast.shortToast(ApplyUnsealingActivity.this,errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(ApplyUnsealingActivity.this);
                NToast.shortToast(ApplyUnsealingActivity.this,getString(R.string.unsealing_has_apply));
                finish();
            }
        });
    }
}
