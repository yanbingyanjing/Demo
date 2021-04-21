package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.AlbumVideoResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.MyAlbumActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.PhotoAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class PhotosFragment extends BaseFragment implements View.OnClickListener, PhotoAdapter.OnCheckClickListener, CosXmlUtils.OSSResultListener, AdapterView.OnItemClickListener {

    @BindView(R.id.photoGrid)
    GridView photoGrid;
    @BindView(R.id.deleteLayout)
    LinearLayout deleteLayout;

    @BindView(R.id.quanxuanLayout)
    RelativeLayout quanxuanLayout;
    @BindView(R.id.quanxuanCheck)
    CheckBox quanxuanCheck;
    @BindView(R.id.setCount)
    TextView setCount;
    @BindView(R.id.deleteCount)
    TextView deleteCount;
    @BindView(R.id.noDataLay)
    RelativeLayout noDataLay;
    @BindView(R.id.photoLayout)
    FrameLayout photoLayout;
    @BindView(R.id.upLoadLay)
    LinearLayout upLoadLay;

    public PhotoAdapter andVideoAdapter;
    public ArrayList<AlbumVideoResponse.AlbumBean> lists = new ArrayList<>();
    private ArrayList<AlbumVideoResponse.AlbumBean> checked = new ArrayList<>();
    private ArrayList<String> photoString = new ArrayList<>();
    private String mUserId;

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    private static PhotosFragment instace;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (lists.size() == 0) {
                        photoLayout.setVisibility(View.GONE);
                        noDataLay.setVisibility(View.VISIBLE);
                    } else {
                        photoLayout.setVisibility(View.VISIBLE);
                        noDataLay.setVisibility(View.GONE);
                    }

                    andVideoAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static PhotosFragment getInstance() {
        instace = new PhotosFragment();
        return instace;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_photos;
    }

    ArrayList<Uri> iterms = new ArrayList<>();

    @Override
    protected void initAction() {
        andVideoAdapter = new PhotoAdapter(getActivity(), lists, "photo");
        photoGrid.setAdapter(andVideoAdapter);
        mUserId = UserInfoUtil.getMiTencentId();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initEvent() {
        deleteCount.setOnClickListener(this);
        quanxuanLayout.setOnClickListener(this);
        quanxuanCheck.setOnClickListener(this);
        andVideoAdapter.setCheckClickListener(this);
        photoGrid.setOnItemClickListener(this);
        upLoadLay.setOnClickListener(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setCount() {
        setCount.setText("已选" + checked.size() + "个，共" + iterms.size() + "个");
    }

    private void setDatas() {
        if (lists.size() == 0) {
            photoLayout.setVisibility(View.GONE);
            noDataLay.setVisibility(View.VISIBLE);
        } else {
            photoLayout.setVisibility(View.VISIBLE);
            noDataLay.setVisibility(View.GONE);
        }
        handler.sendEmptyMessage(1);
    }

    public void showOption() {
        checked.clear();
        upLoadLay.setVisibility(View.GONE);
        deleteLayout.setVisibility(View.VISIBLE);
        andVideoAdapter.setOption("option");
        andVideoAdapter.notifyDataSetChanged();
        setCount.setText("已选" + 0 + "个，共" + lists.size() + "个");

    }

    public void hidOption() {
        checked.clear();
        upLoadLay.setVisibility(View.VISIBLE);
        deleteLayout.setVisibility(View.GONE);
        andVideoAdapter.setOption("noOption");
        andVideoAdapter.setChecked("checkNone");
        quanxuanCheck.setChecked(false);
        andVideoAdapter.notifyDataSetChanged();
    }

    private boolean checkAll = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteCount:
                deleteObj();
                setDatas();
                checkAll = true;
                andVideoAdapter.setChecked("noCheckAll");
                andVideoAdapter.notifyDataSetChanged();
                break;
            case R.id.quanxuanLayout:
            case R.id.quanxuanCheck:
                if (checkAll) {
                    andVideoAdapter.setChecked("checkAll");
                    andVideoAdapter.notifyDataSetChanged();
                    setCount.setText("已选" + lists.size() + "个，共" + lists.size() + "个");
                    checkAll = false;
                    quanxuanCheck.setChecked(true);
                    checked.addAll(lists);
                } else {
                    andVideoAdapter.setChecked("noCheckAll");
                    andVideoAdapter.notifyDataSetChanged();
                    setCount.setText("已选" + 0 + "个，共" + lists.size() + "个");
                    checked.clear();
                    quanxuanCheck.setChecked(false);
                    checkAll = true;
                }
                break;
            case R.id.upLoadLay:
                if (deleteLayout.getVisibility() == View.GONE) {
                    if (lists.size() >= 9) {
                        NToast.shortToast(getActivity(), R.string.album_photo_tip);
                        return;
                    }
                    popupDialog();
                }
                break;
        }
    }

    private void popupDialog() {
        tickPhoto();
        return;
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
                .maxNum(9 - lists.size())
                .build();

// 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, 1005);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoString.clear();
        if (requestCode == 1005) {
            if (data != null) {
                List<String> pathList = data.getStringArrayListExtra("result");
                for (String path : pathList) {
                    photoString.add(path);
                }
                if (photoString.size() == 0) {
                    modifyUserAlbum();
                } else {
                    uploadDialog = new UploadDialog(getActivity());
                    uploadOssUtils = new CosXmlUtils(getActivity());
                    uploadOssUtils.setOssResultListener(this);
                    uploadDialog.show(300);
                    uploadDialog.uploadPhoto(photoString.size());
                    ossUploadList();
                }
            }
        }
    }

    @Override
    public void checkItem(int position) {
        if (!checked.contains(lists.get(position))) {
            checked.add(lists.get(position));
        } else {
            checked.remove(lists.get(position));
        }
        if (checked.size() != lists.size()) {
            quanxuanCheck.setChecked(false);
        } else {
            quanxuanCheck.setChecked(true);
        }
        setCount.setText("已选" + checked.size() + "个，共" + lists.size() + "个");
    }

    private void deleteObj() {
        for (int i = 0; i < checked.size(); i++) {
            lists.remove(checked.get(i));
//            MyAlbumActivity.localPhotoList.remove(checked.get(i));
        }
        cosdelObjects();
        setCount.setText("已选" + checked.size() + "个，共" + lists.size() + "个");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int posotion, long l) {
        ArrayList<String> imgs = new ArrayList<>();
        try {
            for (int i = 0; i < lists.size(); i++) {
                imgs.add(lists.get(i).getFull_url());
            }
            Intent intent = new Intent(mContext, XPicturePagerActivity.class);
            intent.putExtra(Config.POSITION, posotion);
            intent.putExtra("Picture", JsonMananger.beanToJson(imgs));
            startActivity(intent);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    private void modifyUserAlbum(){
        String str1 = "";
        for (int i = 0; i < lists.size(); i++) {
            String line = lists.get(i).getObject();
            if (lists.size() == 1) {
                str1 += line;
            } else if (i == lists.size() - 1) {
                str1 += line;
            } else {
                str1 += line + ",";
            }
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("album", str1)
//                    .put("video", str1)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/modifyUserAlbum", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    private void cosdelObjects(){
        if (sb == null) {
            sb = new StringBuilder();
            for (int i = 0; i < checked.size(); i++) {
                sb.append(checked.get(i).getObject());
                if (i != 0 || i != checked.size() - 1) {
                    sb.append(",");
                }
            }
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("objects", sb.toString())
                    .put("class_id", 1)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/material/delObjects", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(getActivity(), "删除成功");
                modifyUserAlbum();
            }
        });
    }

    private StringBuilder sb;

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(photoString, "image", 1, mUserId, uploadDialog);
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void refreshData(String loadedData) {
        if (loadedData.equals("loadedData")) {
            lists.addAll(MyAlbumActivity.localPhotoList);
            setDatas();
        } else if (loadedData.equals("bianji")) {
            showOption();
        } else if (loadedData.equals("complite")) {
            hidOption();
        }
    }


    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        if (response != null) {
            ArrayList<AlbumVideoResponse.AlbumBean> photosRes = new ArrayList<>();
            for (int i = 0; i < response.size(); i++) {
                AlbumVideoResponse.AlbumBean photo = new AlbumVideoResponse.AlbumBean();
                photo.setFull_url(response.get(i).getFull_url());
                photo.setObject(response.get(i).getObject());
                photosRes.add(photo);
                lists.add(photo);
            }
            photoString.clear();
            handler.sendEmptyMessage(1);
            modifyUserAlbum();
        }
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {

    }

    @Subscriber(tag = Config.EVENT_START)
    public void ssss(ArrayList<String> lists) {
        sb = new StringBuilder();
        for (int i = 0; i < lists.size(); i++) {
            sb.append(lists.get(i));
            if (i != 0 || i != lists.size() - 1) {
                sb.append(",");
            }
        }
        cosdelObjects();
        NToast.shortToast(getActivity(), "网络异常，请重新上传");
    }

}
