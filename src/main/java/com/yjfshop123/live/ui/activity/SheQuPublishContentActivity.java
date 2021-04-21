package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.video_shooting.activity.VideoAlbumActivity;
import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.request.AddDynamicRequest;
import com.yjfshop123.live.net.response.AlbumVideoResponse;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.SheQuPublishContentAdapter;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jiguang.imui.chatinput.emoji.Constants;
import cn.jiguang.imui.chatinput.emoji.EmojiBean;
import cn.jiguang.imui.chatinput.emoji.EmojiView;
import cn.jiguang.imui.chatinput.emoji.EmoticonsKeyboardUtils;
import cn.jiguang.imui.chatinput.emoji.data.EmoticonEntity;
import cn.jiguang.imui.chatinput.emoji.listener.EmoticonClickListener;
import cn.jiguang.imui.chatinput.utils.SimpleCommonUtils;

public class SheQuPublishContentActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        SheQuPublishContentAdapter.OnImageClickListener, CosXmlUtils.OSSResultListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.title)
    EditText titleEt;
    @BindView(R.id.cityTxt)
    TextView cityTxt;
    @BindView(R.id.content)
    EditText contentEt;
    @BindView(R.id.biaoqing)
    ImageView biaoqing;
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.video)
    ImageView video;
    @BindView(R.id.gridLay)
    RelativeLayout gridLay;
    @BindView(R.id.pictureGrid)
    GridView pictureGrid;
    @BindView(R.id.aurora_rl_emoji_container)
    EmojiView mEmojiRl;
    @BindView(R.id.nimingLayout)
    RelativeLayout nimingLayout;
    @BindView(R.id.nimingTxt)
    TextView nimingTxt;
    @BindView(R.id.nimingImage)
    ImageView nimingImage;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;

    int type = -1;
    private int addDynamicType = -1;
    private boolean isNiMing = true;
    private boolean titleFocus = false;
    private boolean contentFocus = false;
    private boolean cityCheck = true;
    private String flag = "";
    private int resourceType = -1;

    private CosXmlUtils uploadOssUtils;
    private UploadDialog uploadDialog;

    private String cityStr;
    private String proviceStr;

    private ArrayList<String> photoString = new ArrayList<>();

    private SheQuPublishContentAdapter adapter;

    private String mUserid;

    private AddDynamicRequest dynamicRequest = new AddDynamicRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isStatusBar = true;
        isShow = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shequ_publish_content);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tv_title_center.setText(getString(R.string.sq_1));
        mHeadRightText.setText(getString(R.string.sq_2));
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setOnClickListener(this);

        biaoqing.setOnClickListener(this);
        picture.setOnClickListener(this);
        video.setOnClickListener(this);
        contentEt.setOnClickListener(this);
        nimingLayout.setOnClickListener(this);
        cityTxt.setOnClickListener(this);
        pictureGrid.setOnItemClickListener(this);

        mUserid = UserInfoUtil.getMiTencentId();
        adapter = new SheQuPublishContentAdapter(this, photoString);
        pictureGrid.setAdapter(adapter);

        adapter.setOnImageClickListener(this);

        type = getIntent().getIntExtra("type", -1);

        try {
            if (!TextUtils.isEmpty(App.currentWeizhi)) {
                String[] str = App.currentWeizhi.split(",");
                proviceStr = str[0];
                cityStr = str[1];
                cityTxt.setText(str[1]);
            } else {
                if (!TextUtils.isEmpty(UserInfoUtil.getCity())) {
                    String[] city = UserInfoUtil.getCity().split(",");
                    if (city.length == 0) {
                        cityTxt.setVisibility(View.GONE);
                    } else if (city.length == 1) {
                        proviceStr = city[0];
                        cityStr = city[0];
                        cityTxt.setText(city[0]);
                    } else if (city.length == 2) {
                        proviceStr = city[0];
                        cityStr = city[1];
                        cityTxt.setText(city[1]);
                    } else if (city.length == 3) {
                        proviceStr = city[0];
                        cityStr = city[1];
                        cityTxt.setText(city[1]);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            cityTxt.setVisibility(View.GONE);
        }

        if (type == 1) {//　视频
            titleEt.setHint(getString(R.string.sq_3));
            contentEt.setVisibility(View.GONE);
            flag = "video";
            addDynamicType = 1;
            startActivityForResult(new Intent(this, VideoAlbumActivity.class), 20002);
        } else if (type == 2) {//图片
            titleEt.setHint(getString(R.string.sq_3));
            contentEt.setVisibility(View.GONE);
            addDynamicType = 1;
            flag = "photo";
            popupDialog();
        } else if (type == 3) {//心情
            titleEt.setHint(getString(R.string.sq_3));
            addDynamicType = 1;
            contentEt.setVisibility(View.GONE);
        } else if (type == 4) {//帖子
            titleEt.setHint(getString(R.string.sq_4));
            InputFilter[] filters = {new InputFilter.LengthFilter(30)};
            titleEt.setFilters(filters);
            addDynamicType = 2;
        }
        mEmojiRl.setAdapter(SimpleCommonUtils.getCommonAdapter(mContext, emoticonClickListener));

//        startMap();

        EventBus.getDefault().register(this);


        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

        titleEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    titleFocus = true;
                    dismissEmojiLayout();
                    if (type == 4) {
                        biaoqing.setVisibility(View.GONE);
                    }
                } else {//失去焦点
                    titleFocus = false;
                }
            }
        });

        contentEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    contentFocus = true;
                    dismissEmojiLayout();
                    if (type == 4) {
                        biaoqing.setVisibility(View.VISIBLE);
                    }
                } else {//失去焦点
                    contentFocus = false;
                }
            }
        });

        gridLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dismissEmojiLayout();
                hideKeyBord();
                return false;
            }
        });

        titleEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        type == 1 ||
                                type == 2 ||
                                type == 3) {
                    dismissEmojiLayout();
                }
            }
        });
    }


    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (titleFocus) {
                if (isDelBtn) {
                    SimpleCommonUtils.delClick(titleEt);
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                        // if(o instanceof EmoticonEntity){
                        // OnSendImage(((EmoticonEntity)o).getIconUri());
                        // }
                    } else {
                        String content_ = null;
                        if (o instanceof EmojiBean) {
                            content_ = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content_ = ((EmoticonEntity) o).getContent();
                        }

                        if (TextUtils.isEmpty(content_)) {
                            return;
                        }

                        /*int index = title.getSelectionStart();
                        Editable editable = title.getText();
                        editable.insert(index, content_);*/

                        Editable editable = titleEt.getText();
                        int start = titleEt.getSelectionStart();
                        int end = titleEt.getSelectionEnd();
                        start = (start < 0 ? 0 : start);
                        end = (start < 0 ? 0 : end);
                        editable.replace(start, end, content_);
                        int editEnd = titleEt.getSelectionEnd();
                        PandaEmoTranslator.getInstance().replaceEmoticons(editable, 0, editable.toString().length());
                        titleEt.setSelection(editEnd);
                    }
                }
            }

            if (contentFocus) {
                if (isDelBtn) {
                    SimpleCommonUtils.delClick(contentEt);
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                        // if(o instanceof EmoticonEntity){
                        // OnSendImage(((EmoticonEntity)o).getIconUri());
                        // }
                    } else {
                        String content_ = null;
                        if (o instanceof EmojiBean) {
                            content_ = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content_ = ((EmoticonEntity) o).getContent();
                        }

                        if (TextUtils.isEmpty(content_)) {
                            return;
                        }

                        /*int index = content.getSelectionStart();
                        Editable editable = content.getText();
                        editable.insert(index, content_);*/

                        Editable editable = contentEt.getText();
                        int start = contentEt.getSelectionStart();
                        int end = contentEt.getSelectionEnd();
                        start = (start < 0 ? 0 : start);
                        end = (start < 0 ? 0 : end);
                        editable.replace(start, end, content_);
                        int editEnd = contentEt.getSelectionEnd();
                        PandaEmoTranslator.getInstance().replaceEmoticons(editable, 0, editable.toString().length());
                        contentEt.setSelection(editEnd);
                    }
                }
            }
        }
    };

//    private MapUtil mapUtil = new MapUtil(this, this);

//    private void startMap() {
//        mapUtil.LoPoi();
//    }

    private void addDynamic() {
        if (addDynamicType == 2) {
            if (TextUtils.isEmpty(titleEt.getText().toString().trim())) {
                NToast.shortToast(this, "信息未完善");
                return;
            } else {
                dynamicRequest.setTitle(titleEt.getText().toString());
            }
            if (TextUtils.isEmpty(contentEt.getText().toString().trim())) {
                NToast.shortToast(this, "信息未完善");
                return;
            } else {
                if (contentEt.getText().toString().length() <= 20) {
                    NToast.shortToast(this, "正文不能少于20字");
                    return;
                }
                dynamicRequest.setContent(contentEt.getText().toString());
            }
        } else if (addDynamicType == 1) {
            if (type == 3) {
                if (TextUtils.isEmpty(titleEt.getText().toString().trim())) {
                    NToast.shortToast(this, "信息未完善");
                    return;
                }
            } else {
                if (photoString.size() < 1) {
                    NToast.shortToast(this, "信息未完善");
                    return;
                } else if (photoString.contains("") && photoString.size() == 1) {
                    NToast.shortToast(this, "信息未完善");
                    return;
                }
            }
            dynamicRequest.setTitle(titleEt.getText().toString());
        }
        dynamicRequest.setType(addDynamicType);
        if (cityCheck) {
            dynamicRequest.setCity_name(cityStr);
            dynamicRequest.setProvince_name(proviceStr);

        } else {
            dynamicRequest.setCity_name("");
            dynamicRequest.setProvince_name("");
        }

        photoString.remove("");

//        Intent intent = new Intent(this, CommunityGroupActivity.class);
//        intent.putExtra("request", dynamicRequest);
//        intent.putExtra("resourceType", resourceType);
//        intent.putExtra("resourceList", photoString);
//        startActivity(intent);


        save();

    }

    private void save() {
        dynamicRequest.setCircle_id(1);
        if (photoString.size() != 0) {
            uploadDialog = new UploadDialog(this);
            uploadOssUtils = new CosXmlUtils(this);
            uploadOssUtils.setOssResultListener(this);
            uploadDialog.show(300);
            if (resourceType == 2) {
                uploadPhoto();
            } else {
                uploadVideo();
            }
        } else {
            getAddDynamic();
        }
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        super.onHeadLeftButtonClick(v);
        hideKeyBord();
    }

    private void hideKeyBord() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_right:
                hideKeyBord();
                addDynamic();
                break;
            case R.id.biaoqing:
                initBiaoQing();
                break;
            case R.id.picture:
                if (flag.equals("video")) {
                    photoString.clear();
                }
                if (photoString.contains("")) {
                    photoString.remove("");
                }
                flag = "photo";
                popupDialog();
                break;
            case R.id.video:
                flag = "video";
                startActivityForResult(new Intent(this, VideoAlbumActivity.class), 20002);
                break;
            case R.id.nimingLayout:
                if (isNiMing) {
                    nimingImage.setImageResource(R.drawable.forum_create_anonymity_check_s);
                    nimingTxt.setTextColor(getResources().getColor(R.color.color_ffd100));
                    isNiMing = false;
                } else {
                    nimingImage.setImageResource(R.drawable.forum_create_anonymity_check_n);
                    nimingTxt.setTextColor(getResources().getColor(R.color.color_909090));
                    isNiMing = true;
                }
                break;
            case R.id.content:
                dismissEmojiLayout();
                break;
            case R.id.cityTxt:
                if (cityCheck) {
                    cityCheck = false;
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.forum_create_post_location_n_ic);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    cityTxt.setCompoundDrawables(drawable, null, null, null);
                    cityTxt.setTextColor(getResources().getColor(R.color.color_909090));
                } else {
                    cityCheck = true;
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.forum_create_post_location_c_ic);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    cityTxt.setCompoundDrawables(drawable, null, null, null);
                    cityTxt.setTextColor(getResources().getColor(R.color.color_ffd100));
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
                .needCrop(false)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(9 - photoString.size())
                .build();

        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(SheQuPublishContentActivity.this, config, 1005);

    }

    private void initBiaoQing() {
        hideKeyBord();
        if (mEmojiRl.getVisibility() == View.VISIBLE) {
            dismissEmojiLayout();
        } else if (false/*isKeyboardVisible()*/) {//判断 键盘是否打开 TODO
            EmoticonsKeyboardUtils.closeSoftKeyboard(contentEt);
            showEmojiLayout();
//            biaoqing.setImageResource(getResources().getDrawable(R.drawable.));
        } else {
//            showMenuLayout();
            showEmojiLayout();
        }
    }

    public void showEmojiLayout() {
        mEmojiRl.setVisibility(View.VISIBLE);
    }

    public void dismissEmojiLayout() {
        mEmojiRl.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            if (type == 1 || type == 2) {
                if (photoString.size() == 0) {
                    finish();
                }
            }
            return;
        }
        if (requestCode == 1005) {
            List<String> pathList = data.getStringArrayListExtra("result");
            for (String path : pathList) {
                photoString.add(path);
            }
            if (photoString.size() < 9) {
                photoString.add("");
            }
            resourceType = 2;
            flag = "image";
            adapter.notifyDataSetChanged();

//            adapter.setData(photoString);
//            adapter.notifyDataSetChanged();
//            if (photoString.size() == 0) {
//                request();
//            } else {
//                uploadDialog = new UploadDialog(getActivity());
//                uploadOssUtils = new UploadOssUtils(getActivity());
//                uploadOssUtils.initData();
//                uploadOssUtils.setOssResultListener(this);
//                uploadDialog.show(300);
//                uploadDialog.uploadPhoto(photoString.size());
//                request(3);
//            }
        } else if (requestCode == 20002) {
            resourceType = 1;
            String path = data.getStringExtra("videoPath");
            photoString.clear();
            photoString.add(path);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (photoString.get(i).equals("")) {
            photoString.remove("");
            popupDialog();
        } else {
            if (photoString.get(i).endsWith(".mp4")) {
                String path = photoString.get(i);
                String imgPath = firstImage(path);
                VideoPreviewActivity.startActivity(SheQuPublishContentActivity.this, path, imgPath);
            } else {
                ArrayList<String> imgs = new ArrayList<>();
                Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                intent.putExtra(Config.POSITION, i);
                try {
                    imgs.addAll(photoString);
                    imgs.remove("");
                    intent.putExtra("Picture", JsonMananger.beanToJson(imgs));
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        }
    }

    @Override
    public void onImageClick(View view, int position) {
        photoString.remove(position);
        adapter.notifyDataSetChanged();
    }

    private String firstImage(String filePath) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        String snapshotPath = FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        return snapshotPath;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEmojiRl.getVisibility() == View.VISIBLE) {
                dismissEmojiLayout();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void getMessage(String finish) {
        if (finish.equals("finish")) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void uploadPhoto() {
        flag = "image";

        uploadDialog.uploadPhoto(photoString.size());
        ossUploadList();
    }

    private void uploadVideo() {
        flag = "video";
        uploadDialog.uploadVideo();
        ossUploadList();
    }

    public ArrayList<AlbumVideoResponse.AlbumBean> photos = new ArrayList<>();
    public ArrayList<AlbumVideoResponse.VideoBean> videos = new ArrayList<>();
    private String photoStr = "";
    private String videoStr = "";

    @Override
    public void ossResult(ArrayList<OssImageResponse> response) {
        if (response != null) {
            ArrayList<AlbumVideoResponse.AlbumBean> photosRes = new ArrayList<>();
            for (int i = 0; i < response.size(); i++) {
                AlbumVideoResponse.AlbumBean photo = new AlbumVideoResponse.AlbumBean();
                photo.setFull_url(response.get(i).getFull_url());
                photo.setObject(response.get(i).getObject());
                photosRes.add(photo);
                photos.add(photo);
            }
            for (int i = 0; i < photos.size(); i++) {
                String line = photos.get(i).getObject();
                if (photos.size() == 1) {
                    photoStr += line;
                } else if (i == photos.size() - 1) {
                    photoStr += line;
                } else {
                    photoStr += line + ",";
                }
            }
        }
        dynamicRequest.setPicture(photoStr);
        getAddDynamic();
    }

    @Override
    public void ossVideoResult(ArrayList<OssVideoResponse> response) {
        if (response != null) {
            OssVideoResponse response1 = response.get(0);
            AlbumVideoResponse.VideoBean obj = new AlbumVideoResponse.VideoBean();
            obj.setCover_img(response1.getCover_img());
            obj.setFull_url(response1.getFull_url());
            obj.setObject(response1.getObject());
            videos.add(obj);
        }
        for (int i = 0; i < videos.size(); i++) {
            String line = videos.get(i).getObject();
            if (videos.size() == 1) {
                videoStr += line;
            } else if (i == videos.size() - 1) {
                videoStr += line;
            } else {
                videoStr += line + ",";
            }
        }
        dynamicRequest.setVideo(videoStr);
        getAddDynamic();
    }

    private void getAddDynamic() {
        String body = null;
        try {
            body = JsonMananger.beanToJson(dynamicRequest);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        OKHttpUtils.getInstance().getRequest("app/forum/addDynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "发布成功");
                EventBus.getDefault().post("finish", Config.EVENT_START);
                finish();
            }
        });
    }

    private void ossUploadList() {
        //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面）
        int class_id = 11;
        if (flag.equals("video")) {
            class_id = 12;
        }
        uploadOssUtils.ossUploadList(photoString, flag, class_id, mUserid, uploadDialog);
    }

}
