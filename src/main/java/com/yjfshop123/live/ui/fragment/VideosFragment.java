package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.video_shooting.activity.VideoAlbumActivity;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.response.AlbumVideoResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.MyAlbumActivity;
import com.yjfshop123.live.ui.activity.VideoPreviewActivity;
import com.yjfshop123.live.ui.adapter.SelfVideoAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;


public class VideosFragment extends BaseFragment implements
        View.OnClickListener,
        SelfVideoAdapter.OnCheckClickListener,
        CosXmlUtils.OSSResultListener,
        AdapterView.OnItemClickListener{

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

    public SelfVideoAdapter andVideoAdapter;
    public ArrayList<AlbumVideoResponse.VideoBean> lists = new ArrayList<>();
    private ArrayList<AlbumVideoResponse.VideoBean> checked = new ArrayList<>();
    private ArrayList<String> videoString = new ArrayList<>();

    private String flag = "";
    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;
    private String mUserId;

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
        mUserId = UserInfoUtil.getMiTencentId();

//        videoString = (ArrayList<LoginResponse.DataBean.UserInfoBean.Videos>) getArguments().getSerializable("videoString");
        lists.clear();
//        if (videoString != null) {
//            for (int i = 0; i < videoString.size(); i++) {
//                lists.add(Uri.parse(videoString.get(i).getCover_img()));
//            }
//        }

        andVideoAdapter = new SelfVideoAdapter(getActivity(), lists, "video");
        photoGrid.setAdapter(andVideoAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initEvent() {
        deleteCount.setOnClickListener(this);
        quanxuanLayout.setOnClickListener(this);
        quanxuanCheck.setOnClickListener(this);
        andVideoAdapter.setCheckClickListener(this);
        upLoadLay.setOnClickListener(this);
        photoGrid.setOnItemClickListener(this);

        uploadDialog = new UploadDialog(getActivity());
        uploadOssUtils = new CosXmlUtils(getActivity());
        uploadOssUtils.setOssResultListener(this);

        setCount.setText("已选" + checked.size() + "个，共" + lists.size() + "个");
    }

    private void setDatas() {
        if (lists.size() == 0) {
            photoLayout.setVisibility(View.GONE);
            noDataLay.setVisibility(View.VISIBLE);
        } else {
            photoLayout.setVisibility(View.VISIBLE);
            noDataLay.setVisibility(View.GONE);
        }
        iterms.clear();
        for (int i = 0; i < lists.size(); i++) {
            iterms.add(Uri.parse(lists.get(i).getCover_img()));
        }
        handler.sendEmptyMessage(1);
    }

    public void setCount() {
        setCount.setText("已选" + checked.size() + "个，共" + iterms.size() + "个");
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
                cosdelObjects();
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
                if (lists.size() >= 9) {
                    NToast.shortToast(getActivity(), R.string.album_video_tip);
                    return;
                }
                chooseVideo();
                break;
        }
    }

    @Override
    public void checkItem(int position) {
//        Uri ims = lists.get(position);
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
        }
//        checked.clear();
        setCount.setText("已选" + checked.size() + "个，共" + lists.size() + "个");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlbumVideoResponse.VideoBean bean = lists.get(i);
        VideoPreviewActivity.startActivity(getActivity(), bean.getFull_url(), bean.getCover_img());
    }

    private void modifyUserVideo(){
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
//                    .put("album", album)
                    .put("video", str1)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/modifyUserVideo", body, new RequestCallback() {
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
                    .put("class_id", 2)
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
                modifyUserVideo();
            }
        });
    }

    private StringBuilder sb;

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 20002) {
            String path = data.getStringExtra("videoPath");
            if (!TextUtils.isEmpty(path)) {
                uploadDialog.show(300);
                uploadDialog.uploadVideo();
                ossUploadList(path);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void refreshData(String loadedData) {
        if (loadedData.equals("loadedData")) {
            lists.addAll(MyAlbumActivity.localVideoList);
            setDatas();
        } else if (loadedData.equals("bianji")) {
            showOption();
        } else if (loadedData.equals("complite")) {
            hidOption();
        }
    }

    @Subscriber(tag = Config.EVENT_START)
    public void ssss(ArrayList<String> lists){
        sb = new StringBuilder();
        for (int i = 0; i < lists.size(); i++) {
            sb.append(lists.get(i));
            if (i != 0 || i != lists.size() - 1) {
                sb.append(",");
            }
        }
//        handler.sendEmptyMessage(2);
//        request();
        NToast.shortToast(getActivity(), "网络异常，请重新上传");
    }

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {

    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {
        if (response != null) {
            OssVideoResponse response1 = response.get(0);
            AlbumVideoResponse.VideoBean obj = new AlbumVideoResponse.VideoBean();
            obj.setCover_img(response1.getCover_img());
            obj.setFull_url(response1.getFull_url());
            obj.setObject(response1.getObject());
            lists.add(obj);
//            userInfoBean.getVideos().addAll(videos);
//            VideosFragment1.freshData(userInfoBean.getVideos());
//            SPUtils.saveObj2SP(this, loginResponse, "");
//            VideosFragment1.freshData(localVideoList);
            videoString.clear();
            handler.sendEmptyMessage(1);
            modifyUserVideo();
        }
    }

    private void ossUploadList(String path) {
        flag = "video";
        ArrayList<String> items = new ArrayList<>();
        items.add(path);

        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        uploadOssUtils.ossUploadList(items, flag, 2, mUserId, uploadDialog);

    }

    private void chooseVideo() {
        startActivityForResult(new Intent(getActivity(), VideoAlbumActivity.class), 20002);
    }
}
