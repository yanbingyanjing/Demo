package com.yjfshop123.live.taskcenter;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.SucaiAdapter;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.widget.SucaiSaveTipsDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.SaveNetPhotoUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity.REQUEST_CODE_SELECT;

public class UploadSucaiAcitivity extends BaseActivityH implements CosXmlUtils.OSSResultListener, CommunityClickListener {
    @BindView(R.id.list)
    RecyclerView uploadList;
    @BindView(R.id.confir)
    TextView confir;
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
    @BindView(R.id.sucailist)
    RecyclerView sucailist;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.task_des)
    TextView taskDes;
    private LinearLayoutManager mLinearLayoutManager;
    UploadAdapter adapter;
    String userId;
    UploadDialog dialog;
    public ArrayList<String> dataList = new ArrayList<>();
    public ArrayList<String> dataListUpload = new ArrayList<>();
    int id = -1;
    private CosXmlUtils uploadOssUtils;
    int template_id = -1;
    String task_example;
    List<String> pics = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManagerSucai;
    String des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_sucai);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //?????????????????????????????????
        params.height = SystemUtils.getStatusBarHeight(this);//?????????????????????????????????
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        template_id = getIntent().getIntExtra("template_id", -1);
        id = getIntent().getIntExtra("id", -1);
        task_example = getIntent().getStringExtra("task_example");
        des = getIntent().getStringExtra("des");
        taskDes.setText("???????????????"+des);
        if (!TextUtils.isEmpty(task_example)) {
            pics.add(task_example);
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// ?????? recyclerview ???????????????????????????
        uploadList.setLayoutManager(mLinearLayoutManager);
        adapter = new UploadAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tickPhoto();
            }
        });
        uploadList.setAdapter(adapter);
        adapter.setCards(dataList);
        userId = UserInfoUtil.getMiTencentId();
        uploadOssUtils = new CosXmlUtils(this);
        uploadOssUtils.setOssResultListener(this);
        // ????????????????????????
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UploadSucaiAcitivity.this, DangeSucaiActivity.class);
//                intent.putExtra("id", id);
//                intent.putExtra("template_id", template_id);
//
//                startActivity(intent);
            }
        });
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(task_example)) {
                        return;
                    }
                    Intent intent = new Intent(UploadSucaiAcitivity.this, XPicturePagerActivity.class);
                    intent.putExtra(Config.POSITION, 0);

                    intent.putExtra("Picture", JsonMananger.beanToJson(pics));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
        initSwipeRefresh();
        mLinearLayoutManagerSucai = new LinearLayoutManager(this);
        sucailist.setLayoutManager(mLinearLayoutManagerSucai);
        adapterSucai = new SucaiAdapter(this);
        adapterSucai.setmItemClickListener(this);
        sucailist.setAdapter(adapterSucai);
        loadData();
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    /**
     * ???????????????
     */
    public void onHeadLeftButtonClick(View v) {
        finish();

    }

    private void tickPhoto() {
        // ??????????????????
        ISListConfig config = new ISListConfig.Builder()
                // ????????????, ??????true
                .multiSelect(true)
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
                .cropSize(1, 1, 400, 400)
                .needCrop(true)
                // ????????????????????????????????????true
                .needCamera(true)
                // ?????????????????????????????????9
                .maxNum(9)
                .build();

        // ????????????????????????
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
        //???1:???????????? 2:???????????? 3:?????????????????? 4:???????????? 5:???????????? 6:???????????? 11:???????????? 12:???????????? 21:??????????????????
        uploadOssUtils.ossUploadList(dataList, "image", 5, userId, dialog);
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
                        dataListUpload.add(response1.getObject());
                    }
                    dialog.dissmis();
                    upload();
                    break;
            }
        }
    };

    @Override
    public void onContentClick(View view, int position) {
        //????????????
        if (mList == null || mList.get(position) == null) return;
        SystemUtils.setClipboard(this, mList.get(position).title + "\n" + mList.get(position).content + "\n????????????"+mList.get(position).invite_code+"\n???????????????" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""));
    }

    @Override
    public void onPortraitClick(View view, int position) {
        if (mList == null || mList.get(position) == null) return;
        //????????????
       SystemUtils.setClipboardNoToast(this, mList.get(position).title + "\n" + mList.get(position).content +"\n????????????"+mList.get(position).invite_code+ "\n???????????????" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""));
        //SystemUtils.setClipboardNoToast(this, mList.get(position).title + "\n" + mList.get(position).content);

        SaveNetPhotoUtils.savePhotoListForShare(this, mList.get(position).pic_list, mList.get(position).share_url ,
                new SaveNetPhotoUtils.OnsaveOk() {
                    @Override
                    public void onSave() {
                        //??????????????????
                        SucaiSaveTipsDialog dialogFragment = new SucaiSaveTipsDialog();
                        dialogFragment.setClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!CheckInstalledUtil.isWeChatAppInstalled(UploadSucaiAcitivity.this)) {
                                    NToast.shortToast(UploadSucaiAcitivity.this, "???????????????????????????????????????");
                                    return;
                                }
                                Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setComponent(lan.getComponent());
                                startActivity(intent);
                            }
                        });
                        dialogFragment.show(getSupportFragmentManager(), "SucaiSaveTipsDialog");
                    }
                });


    }

    @Override
    public void onPraiseClick(View view, int position) {

    }

    @Override
    public void onImgClick(View view, int position, int index) {
        List<String> images = new ArrayList<>();
        if (mList != null && position < mList.size() && mList.get(position) != null && mList.get(position).pic_list != null) {
            images = Arrays.asList(mList.get(position).pic_list);
            try {
                Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                intent.putExtra(Config.POSITION, index);
                intent.putExtra("erweima", mList.get(position).share_url);
                intent.putExtra("Picture", JsonMananger.beanToJson(images));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onVideo(TextureVideoView videoView, ImageView videoCover, ImageView videoBtn) {

    }

    private class Reques {
        public String id;
        public String tag;
        public ArrayList<String> imgs;

    }

    private void upload() {
        LoadDialog.show(this);
        String body = "";
        Reques reques = new Reques();
        reques.id = id + "";
        reques.imgs = dataListUpload;
        try {
            body = JsonMananger.beanToJson(reques);
        } catch (HttpException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/task/uploadTask", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(UploadSucaiAcitivity.this, errInfo);
                LoadDialog.dismiss(UploadSucaiAcitivity.this);

            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(UploadSucaiAcitivity.this);
                NToast.shortToast(UploadSucaiAcitivity.this, "???????????????");                //initData(testData);
                finish();
            }
        });
    }

    @OnClick(R.id.confir)
    public void onViewClicked() {
        if (dataListUpload != null && dataListUpload.size() > 0) {
            upload();
        } else {
            if (dataList == null || dataList.size() <= 0) {
                NToast.shortToast(this, "???????????????????????????");
                return;
            }
            dialog = new UploadDialog(this);
            dialog.show(300);
            dialog.uploadPhoto(dataList.size());
            ossUploadList();
        }
    }

    int page = 1;
    boolean isLoadingMore = false;

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("template_id", template_id + "")
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/task/shareTemplate", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //????????????
                isLoadingMore = false;
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                finishRefresh();
                initData(result);
                //initData(testData);
            }
        });
    }

    ShareSucaiResopnse data;
    private List<ShareSucaiResopnse.Sucai> mList = new ArrayList<>();
    ShareSucaiResopnse.Sucai selectSucai;
    private SucaiAdapter adapterSucai;

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        if (page == 1)
            selectSucai = null;
        data = new Gson().fromJson(result, ShareSucaiResopnse.class);
        if (page > 1 && (data.list == null || data.list.length == 0)) {
            page--;
            return;
        }
        if (page == 1) {
            mList.clear();
        }
        mList.addAll(Arrays.asList(data.list));
        if (adapterSucai != null) adapterSucai.setCards(mList);
    }


}
