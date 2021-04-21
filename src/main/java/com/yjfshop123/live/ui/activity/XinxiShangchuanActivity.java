package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.umeng.commonsdk.debug.I;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.taskcenter.UploadAdapter;
import com.yjfshop123.live.ui.adapter.XunlianyingAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSDateTotalDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.DateFormatUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity.REQUEST_CODE_SELECT;

public class XinxiShangchuanActivity extends BaseActivityH implements CosXmlUtils.OSSResultListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.list)
    RecyclerView uploadList;


    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.weixin)
    EditText weixin;
    @BindView(R.id.dida_date)
    TextView didaDate;
    @BindView(R.id.fancheng_date)
    TextView fanchengDate;
    private LinearLayoutManager mLinearLayoutManager;
    XunlianyingAdapter adapter;
    private CosXmlUtils uploadOssUtils;
    public ArrayList<Image> dataList = new ArrayList<>();
    public ArrayList<String> dataListUpload = new ArrayList<>();

    public class Image {
        public String path;
        public boolean isHttp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xinxishangchuan);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        uploadList.setLayoutManager(mLinearLayoutManager);
        adapter = new XunlianyingAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tickPhoto();
            }
        });

        uploadList.setAdapter(adapter);
        adapter.setCards(dataList);

        loadData(false);
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

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();

    }


    Response data;


    private void loadData(boolean isFromRefresh) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .build();
        } catch (Exception e) {
        }

        OKHttpUtils.getInstance().getRequest("app/activity/getTraffic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                data = new Gson().fromJson(result, Response.class);
                if (data.info == null) return;
                name.setText(data.info.name);
                phone.setText(data.info.phone);
                weixin.setText(data.info.weixin);
                didaDate.setText(data.info.arrive_time);
                fanchengDate.setText(data.info.back_time);
                if (data.info.imgs != null) {
                    for (int i = 0; i < data.info.imgs.size(); i++) {
                        Image image=new Image();
                        image.path=data.info.imgs.get(i);
                        image.isHttp=true;
                        dataList.add(image);
                    }
                }
                if (adapter != null) adapter.notifyDataSetChanged();


            }
        });
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
                    upload();
                    break;
            }
        }
    };

    @OnClick(R.id.confir)
    public void onViewClicked() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            NToast.shortToast(this, "请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            NToast.shortToast(this, "请输入联系电话");
            return;
        }
        if (TextUtils.isEmpty(weixin.getText().toString())) {
            NToast.shortToast(this, "请输入微信号");
            return;
        }
        if (TextUtils.isEmpty(didaDate.getText().toString())) {
            NToast.shortToast(this, "请输入抵达日期");
            return;
        }
        if (TextUtils.isEmpty(fanchengDate.getText().toString())) {
            NToast.shortToast(this, "请输入返程日期");
            return;
        }
//        if (dataListUpload != null && dataListUpload.size() > 0) {
//            upload();
//        } else {
        if (dataList == null || dataList.size() <= 0) {
            NToast.shortToast(this, "请上传至少一张交通航班截图");
            return;
        }
        dialog = new UploadDialog(this);
        dialog.show(300);
        dialog.uploadPhoto(dataList.size());
        ossUploadList();
        //  }
    }

    IOSDateTotalDialog startdialog;//抵达日期
    IOSDateTotalDialog enddialog;//返程日期

    @OnClick({R.id.dida_date, R.id.fancheng_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dida_date:
                if (startdialog == null) {
                    startdialog = new IOSDateTotalDialog(Objects.requireNonNull(this)).builder();
                    startdialog.setTitle(getString(R.string.select_date));
                    startdialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    startdialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            didaDate.setText(startdialog.getDateStr());
                        }
                    });
                }
                startdialog.show();
                break;
            case R.id.fancheng_date:
                if (enddialog == null) {
                    enddialog = new IOSDateTotalDialog(Objects.requireNonNull(this)).builder();
                    enddialog.setTitle(getString(R.string.select_date));
                    enddialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    enddialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            fanchengDate.setText(enddialog.getDateStr());

                        }
                    });
                }
                enddialog.show();
                break;
        }
    }

    private class Reques {
        public String name;
        public String phone;
        public String weixin;
        public String arrive_time;
        public String back_time;
        public ArrayList<String> imgs;

    }

    private class Response {
        public Xinxi info;
    }

    private class Xinxi {
        public String name;
        public String phone;
        public String weixin;
        public String arrive_time;
        public String back_time;
        public ArrayList<String> imgs;

    }

    private void upload() {
        LoadDialog.show(this);
        String body = "";
        Reques reques = new Reques();
        reques.name = name.getText().toString();
        reques.phone = phone.getText().toString();
        reques.weixin = weixin.getText().toString();
        reques.arrive_time = didaDate.getText().toString();
        reques.back_time = fanchengDate.getText().toString();
        reques.imgs = dataListUpload;
        try {
            body = JsonMananger.beanToJson(reques);
        } catch (HttpException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/activity/submitTraffic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(XinxiShangchuanActivity.this, errInfo);
                LoadDialog.dismiss(XinxiShangchuanActivity.this);

            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(XinxiShangchuanActivity.this);
                NToast.shortToast(XinxiShangchuanActivity.this, "提交成功！");                //initData(testData);
                finish();
            }
        });
    }

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        ArrayList<String> dataListUp = new ArrayList<>();
        dataListUpload.clear();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isHttp) {
                dataListUpload.add(dataList.get(i).path);
            } else
                dataListUp.add(dataList.get(i).path);
        }
        uploadOssUtils.ossUploadList(dataListUp, "image", 5, UserInfoUtil.getMiTencentId(), dialog);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (data != null) {
                    ArrayList<String> pathList = data.getStringArrayListExtra("result");
                    if (pathList == null) return;
                    for(int i=0;i<pathList.size();i++){
                        Image image=new Image();
                        image.path=pathList.get(i);
                        image.isHttp=false;
                        dataList.add(image);
                    }
                    //  dataList = pathList;
                    adapter.setCards(dataList);
                }
                break;
        }
    }
}
