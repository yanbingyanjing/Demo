package com.yjfshop123.live.message.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.video_shooting.activity.XVideoCameraActivity;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Interface.ILoginView;
import com.yjfshop123.live.LoginHelper;
import com.yjfshop123.live.R;
import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.utils.ClickUtil;
import com.yjfshop123.live.message.ChatPresenter;
import com.yjfshop123.live.message.ConversationFactory;
import com.yjfshop123.live.message.MessageFactory;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.models.DefaultUser;
import com.yjfshop123.live.message.ui.models.GiftMessage;
import com.yjfshop123.live.message.ui.models.ImageMessage;
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.message.ui.models.MyMessage;
import com.yjfshop123.live.message.ui.models.TextMessage;
import com.yjfshop123.live.message.ui.models.VideoMessage;
import com.yjfshop123.live.message.ui.models.VoiceMessage;
import com.yjfshop123.live.message.ui.views.ChatView;
import com.yjfshop123.live.model.VIPEvent;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.GiftListResponse;
import com.yjfshop123.live.net.response.LaunchChatResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.utils.imageloader.CircleBitmapDisplayer;
import com.yjfshop123.live.server.utils.imageloader.DisplayImageOptions;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.OptionsPopupDialog;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.ui.activity.MyWalletActivity1;
import com.yjfshop123.live.ui.activity.RoomActivity;
import com.yjfshop123.live.ui.activity.VideoPreviewActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.GiftAdapter;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.TimeUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.CustomMenuEventListener;
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.menu.view.MenuFeature;
import cn.jiguang.imui.chatinput.menu.view.MenuItem;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.chatinput.model.VideoItem;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MessageListActivity extends BaseActivity implements View.OnTouchListener,
        EasyPermissions.PermissionCallbacks, SensorEventListener, ChatViewIF, IPermissions, ILoginView {

    private final static String TAG = "MessageListActivity";
    private final int RC_RECORD_VOICE = 0x0001;
    private final int RC_CAMERA = 0x0002;
    private final int RC_PHOTO = 0x0003;

    private ChatView mChatView;
    private MsgListAdapter<MyMessage> mAdapter;

    private List<MyMessage> mData = new ArrayList<>();

    private InputMethodManager mImm;
    private Window mWindow;
    private HeadsetDetectReceiver mReceiver;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private ArrayList<String> mPathList = new ArrayList<>();
    private ArrayList<String> mMsgIdList = new ArrayList<>();

    private String miAvatar;
    private String miName;

    private ChatPresenter chatPresenter;
    private String gift = "";
    private boolean ISGIFT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isStatusBar = true;
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);
        setHeadVisibility(View.GONE);

        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();
        registerProximitySensorListener();

        miAvatar = UserInfoUtil.getAvatar();
        miName = UserInfoUtil.getName();
//        int is_vip = UserInfoUtil.getIsVip();
//        if (is_vip == 0) {
//            isMeVip = false;
//        } else {
//            isMeVip = true;
//        }

        Intent data = getIntent();
        imConversation = (IMConversation) data.getSerializableExtra("IMConversation");
        RealmConverUtils.conversationId = imConversation.getConversationId();
        try {
            gift = data.getStringExtra("GIFT");
        } catch (Exception e) {
            gift = "";
        }

        mChatView = findViewById(R.id.chat_view);
        mChatView.initModule();
        mChatView.setTitle(imConversation.getOtherPartyName());
        ImageView chat_vip_iv = findViewById(R.id.chat_vip_iv);

        initMsgAdapter();
        loadMessages();

        mReceiver = new HeadsetDetectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
        mChatView.setOnTouchListener(this);

        // 0 单聊  1 群聊  2 系统消息
        if (imConversation.getType() == 0) {
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.C2C);
        } else if (imConversation.getType() == 1) {
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.Group);
        } else if (imConversation.getType() == 2) {
            chatPresenter = new ChatPresenter(this, imConversation.getOtherPartyIMId(), TIMConversationType.System);
        }

        mChatView.getChatInputView().setTemplateChangedListener(new ChatInputView.TemplateListener() {
            @Override
            public void onTemplateChanged(boolean isTemplate, CharSequence s) {

            }
        });

        mChatView.setMenuClickListener(new OnMenuClickListener() {
            @Override
            public boolean onSendTextMessage(CharSequence input) {
                if (input.length() == 0) {
                    return false;
                }

                MyMessage message = new MyMessage(input.toString(), IMessage.MessageType.SEND_TEXT.ordinal(), null);
                message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));
//                long currentTime = System.currentTimeMillis() / 1000;
//                message.setTimeString(TimeUtil.getTimeStr(currentTime));
                message.setMessageStatus(IMessage.MessageStatus.SEND_GOING);
                mAdapter.addToStart(message, true);

                TextMessage textMessage = new TextMessage(input.toString(), imConversation);
                chatPresenter.sendMessage(textMessage.getMessage(), MessageListActivity.this, true);
                return true;
            }

            @Override
            public void onSendFiles(List<FileItem> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }

                MyMessage message;
                for (FileItem item : list) {
                    if (item.getType() == FileItem.Type.Image) {
                        message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal(), null);
                        mPathList.add(item.getFilePath());
                        mMsgIdList.add(message.getMsgId());

//                        long currentTime = System.currentTimeMillis() / 1000;
//                        message.setTimeString(TimeUtil.getTimeStr(currentTime));

                        message.setMediaFilePath(item.getFilePath());
                        message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(item.getFilePath(), options);
                        message.setImageW(options.outWidth);
                        message.setImageH(options.outHeight);

                        final MyMessage fMsg = message;
                        MessageListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addToStart(fMsg, true);
                            }
                        });

                        ImageMessage imageMessage = new ImageMessage(item.getFilePath(), true, imConversation);
                        chatPresenter.sendMessage(imageMessage.getMessage(), MessageListActivity.this, true);

                    } else if (item.getType() == FileItem.Type.Video) {
                        sendVideo(item.getFilePath(), ((VideoItem) item).getDuration());
                    } else {
                        throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video");
                    }
                }
            }

            @Override
            public boolean switchToMicrophoneMode() {
                scrollToBottom();
                /*String[] perms = new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            "This app needs access microphone to record voice.",
                            RC_RECORD_VOICE, perms);
                }*/
                type_PP = 110;
                PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                return true;
            }

            @Override
            public boolean switchToGalleryMode() {
                scrollToBottom();
                String[] perms = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            "This app needs access photos.",
                            RC_PHOTO, perms);
                }
                // If you call updateData, select photo view will try to update data(Last update over 30 seconds.)
                mChatView.getChatInputView().getSelectPhotoView().updateData();
                return true;
            }

            @Override
            public boolean switchToCameraMode() {
                scrollToBottom();
                String[] perms = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                };

                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            "This app needs access camera.",
                            RC_CAMERA, perms);
                    return false;
                } else {
                    File rootDir = getFilesDir();
                    String fileDir = rootDir.getAbsolutePath() + "/photo";
                    mChatView.setCameraCaptureFile(fileDir, new SimpleDateFormat("yyyy-MM-dd-hhmmss",
                            Locale.getDefault()).format(new Date()));
                }
                return true;
            }

            @Override
            public boolean switchToEmojiMode() {
                scrollToBottom();
                return true;
            }
        });

        mChatView.setRecordVoiceListener(new RecordVoiceListener() {
            @Override
            public void onStartRecord() {
                // set voice file path, after recording, audio file will save here
                String path = Environment.getExternalStorageDirectory().getPath() + "/voice";
                File destDir = new File(path);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                mChatView.setRecordVoiceFile(destDir.getPath(), DateFormat.format("yyyy-MM-dd-hhmmss", Calendar.getInstance(Locale.CHINA)) + "");
            }

            @Override
            public void onStartRecord2() {
                String[] perms = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                };
                if (!EasyPermissions.hasPermissions(MessageListActivity.this, perms)) {
                    EasyPermissions.requestPermissions(MessageListActivity.this,
                            "This app needs access camera.",
                            RC_CAMERA, perms);
                }
            }

            @Override
            public void onFinishRecord(File voiceFile, int duration) {
                //仿QQ录音回调
                MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_VOICE.ordinal(), null);
                message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));
                message.setMediaFilePath(voiceFile.getPath());
                message.setDuration(duration);
//                long currentTime = System.currentTimeMillis() / 1000;
//                message.setTimeString(TimeUtil.getTimeStr(currentTime));
                mAdapter.addToStart(message, true);

                VoiceMessage textMessage = new VoiceMessage(duration, voiceFile.getPath(), imConversation);
                chatPresenter.sendMessage(textMessage.getMessage(), MessageListActivity.this, true);
            }

            @Override
            public void onFinishRecord(String voicePath, int duration) {
                //仿微信录音回调
                MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_VOICE.ordinal(), null);
                message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));
                message.setMediaFilePath(voicePath);
                message.setDuration(duration);
//                long currentTime = System.currentTimeMillis() / 1000;
//                message.setTimeString(TimeUtil.getTimeStr(currentTime));
                mAdapter.addToStart(message, true);

                VoiceMessage textMessage = new VoiceMessage(duration, voicePath, imConversation);
                chatPresenter.sendMessage(textMessage.getMessage(), MessageListActivity.this, true);
            }

            @Override
            public void onCancelRecord() {

            }

            /**
             * In preview record voice layout, fires when click cancel button
             * Add since chatinput v0.7.3
             */
            @Override
            public void onPreviewCancel() {

            }

            /**
             * In preview record voice layout, fires when click send button
             * Add since chatinput v0.7.3
             */
            @Override
            public void onPreviewSend() {

            }
        });

        mChatView.setOnCameraCallbackListener(new OnCameraCallbackListener() {
            @Override
            public void onTakePictureCompleted(String photoPath) {
                MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal(), null);
                mPathList.add(photoPath);
                mMsgIdList.add(message.getMsgId());

//                long currentTime = System.currentTimeMillis() / 1000;
//                message.setTimeString(TimeUtil.getTimeStr(currentTime));

                message.setMediaFilePath(photoPath);
                message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, options);
                message.setImageW(options.outWidth);
                message.setImageH(options.outHeight);

                final MyMessage fMsg = message;
                MessageListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addToStart(fMsg, true);
                    }
                });

                ImageMessage imageMessage = new ImageMessage(photoPath, true, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), MessageListActivity.this, true);
            }

            @Override
            public void onStartVideoRecord() {

            }

            @Override
            public void onFinishVideoRecord(String videoPath) {

                NToast.shortToast(mContext, videoPath);

            }

            @Override
            public void onCancelVideoRecord() {

            }
        });

        mChatView.getChatInputView().getInputView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollToBottom();
                return false;
            }
        });

        mChatView.getSelectAlbumBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageListActivity.this, "OnClick select album button",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mChatView.getChatInputView().setCustomMenuClickListener(new CustomMenuEventListener() {
            @Override
            public boolean onMenuItemClick(String tag, MenuItem menuItem) {
                //Menu feature will not be show shown if return false；
                if (tag.equals("TAG_VIDEO_VOICE_CALL")) {

                    final Dialog bottomDialog = new Dialog(MessageListActivity.this, R.style.BottomDialog);
                    View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_content_normal, null);
                    bottomDialog.setContentView(contentView);
                    ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                    layoutParams.width = getResources().getDisplayMetrics().widthPixels;
                    contentView.setLayoutParams(layoutParams);
                    bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
                    bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
                    bottomDialog.show();

                    contentView.findViewById(R.id.dialog_voice_ll).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomDialog.dismiss();
                            type_PP = 111;
                            PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                        }
                    });

                    contentView.findViewById(R.id.dialog_video_ll).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomDialog.dismiss();
                            type_PP = 112;
                            PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                        }
                    });
                } else if (tag.equals("Y_TAG_CAMERA")) {
                    type_PP = 113;
                    PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                } else if (tag.equals("Y_TAG_GIFT")) {
                    dialogGift();
                }
                return true;
            }

            @Override
            public void onMenuFeatureVisibilityChanged(int visibility, String tag, MenuFeature menuFeature) {
                if (visibility == View.VISIBLE) {
                    // Menu feature is visible.
                } else {
                    // Menu feature is gone.
                }
            }
        });

        mChatView.getChatBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyboard();
                finish();
            }
        });

        mChatView.getChatPeople().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startUserHome(mContext, imConversation.getOtherPartyId());
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        EventBus.getDefault().register(this);

        recharge();
        recharge2();

        PermissionsUtils.initPermission(mContext);
    }

    //110 录音
    //111 语音通话
    //112 视频通话
    //113 相机
    private int type_PP = 110;

    @Override
    public void allPermissions() {
        switch (type_PP) {
            case 110:

                break;
            case 111:
                type_v_ = 2;
                launchChat();
                break;
            case 112:
                type_v_ = 3;
                launchChat();
                break;
            case 113:
                startActivityForResult(new Intent(mContext, XVideoCameraActivity.class), 10000);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    private void dialogGift() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_gift, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        ViewPager viewPager = contentView.findViewById(R.id.dialog_gift_viewpager);
        indicatorLL = contentView.findViewById(R.id.gift_indicator_ll);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                indicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (giftListResponse == null) {
            return;
        }

        TextView dialog_gift_coin = contentView.findViewById(R.id.dialog_gift_coin);
        dialog_gift_coin.setText(getString(R.string.jb_hint_13) + NumUtil.clearZero(giftListResponse.getRest_coin()));

        contentView.findViewById(R.id.dialog_gift_recharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                Intent intent = new Intent(mContext, MyWalletActivity1.class);
                intent.putExtra("currentItem", 0);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        contentView.findViewById(R.id.dialog_gift_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift();
                bottomDialog.dismiss();
            }
        });

        giftAdapter = new GiftAdapter(giftListResponse, mGiftPost);
        viewPager.setAdapter(giftAdapter);
        viewPager.setCurrentItem(0);
        initIndicator(giftListResponse.getList().size(), 0);

        giftAdapter.setOnItemClickListener(new GiftAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                mGiftPost = postion;
                giftAdapter.setPos(mGiftPost);
                giftAdapter.notifyDataSetChanged();
            }
        });
    }

    private LinearLayout indicatorLL;
    private GiftAdapter giftAdapter;
    private GiftListResponse giftListResponse;
    private int mGiftPost = 0;
    private int mCount = 0;

    private void initIndicator(int size, int pos) {
        mCount = size / 8;
        int remainder = size % 8;
        if (remainder != 0 || mCount == 0) {
            mCount = mCount + 1;
        }
        for (int i = 0; i < mCount; i++) {
            View indicator = LayoutInflater.from(mContext).inflate(R.layout.gift_indicator, null);
            indicatorLL.addView(indicator, i);
            ImageView indicator_ = (ImageView) indicator;
            if (pos == i) {
                indicator_.setImageResource(R.drawable.gift_page_indicator_focused);
            } else {
                indicator_.setImageResource(R.drawable.gift_page_indicator);
            }
        }
    }

    private void indicator(int pos) {
        for (int i = 0; i < mCount; i++) {
            ImageView indicator = (ImageView) indicatorLL.getChildAt(i);
            if (pos == i) {
                indicator.setImageResource(R.drawable.gift_page_indicator_focused);
            } else {
                indicator.setImageResource(R.drawable.gift_page_indicator);
            }
        }
    }

    private void hintKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void registerProximitySensorListener() {
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //听筒 耳机 黑屏 模式
        //TODO
        //TODO
        //TODO
        //TODO
        /*AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        try {
            if (audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn()) {
                return;
            }
            if (mAdapter.getMediaPlayer().isPlaying()) {
                float distance = event.values[0];
                if (distance >= mSensor.getMaximumRange()) {
                    mAdapter.setAudioPlayByEarPhone(0);
                    setScreenOn();
                } else {
                    mAdapter.setAudioPlayByEarPhone(2);
                    ViewHolderController.getInstance().replayVoice();
                    setScreenOff();
                }
            } else {
                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void setScreenOn() {
        if (mWakeLock != null) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void setScreenOff() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        mWakeLock.acquire();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class HeadsetDetectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    mAdapter.setAudioPlayByEarPhone(state);
                }
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "").build().show();
        }
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    private void initMsgAdapter() {

        final float density = getResources().getDisplayMetrics().density;
        final float MIN_WIDTH = 60 * density;
        final float MAX_WIDTH = 200 * density;
        final float MIN_HEIGHT = 60 * density;
        final float MAX_HEIGHT = 200 * density;

        ImageLoader imageLoader = new ImageLoader() {

            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                if (TextUtils.isEmpty(string)) {
                    avatarImageView.setImageResource(R.drawable.splash_logo);
                } else {
                    if (!isDestroy((Activity) mContext)) {
                        if (!TextUtils.isEmpty(string)) {
                            Glide.with(mContext)
                                    .load(string)
                                    .into(avatarImageView);
                        } else {
                            Glide.with(mContext)
                                    .load(R.drawable.splash_logo)
                                    .into(avatarImageView);
                        }
                    }
                }
            }

            @Override
            public void loadAvatarImage(ImageView avatarImageView) {
                Glide.with(mContext)
                        .load(R.drawable.splash_logo)
                        .into(avatarImageView);
            }

            @Override
            public void loadImage(final ImageView imageView, String string, int imageW, int imageH) {
                if (imageW != 0) {
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();

                    int width;
                    if (imageW < imageH) {
                        width = getScreenWidth() / 3;
                    } else {
                        width = getScreenWidth() / 2;
                    }

                    params.width = width;
                    params.height = (width * imageH) / imageW;
                    imageView.setLayoutParams(params);

                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                            .error(R.drawable.imageloader)// 加载失败的图片  
                            .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

                    Glide.with(getApplicationContext())
                            .load(string)// 图片地址  
                            .apply(options)// 参数  
                            .into(imageView);// 需要显示的ImageView控件
                } else {
                    Glide.with(getApplicationContext())
                            .load(string)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                    int imageWidth = bitmap.getWidth();
                                    int imageHeight = bitmap.getHeight();

                                    int width_;
                                    if (imageWidth < imageHeight) {
                                        width_ = getScreenWidth() / 3;
                                    } else {
                                        width_ = getScreenWidth() / 2;
                                    }
                                    imageHeight = (width_ * imageHeight) / imageWidth;
                                    imageWidth = width_;

                                    Log.d(TAG, "Image width " + imageWidth + " height: " + imageHeight);

                                    // 裁剪 bitmap
                                    float width, height;
                                    if (imageWidth > imageHeight) {
                                        if (imageWidth > MAX_WIDTH) {
                                            float temp = MAX_WIDTH / imageWidth * imageHeight;
                                            height = temp > MIN_HEIGHT ? temp : MIN_HEIGHT;
                                            width = MAX_WIDTH;
                                        } else if (imageWidth < MIN_WIDTH) {
                                            float temp = MIN_WIDTH / imageWidth * imageHeight;
                                            height = temp < MAX_HEIGHT ? temp : MAX_HEIGHT;
                                            width = MIN_WIDTH;
                                        } else {
                                            float ratio = imageWidth / imageHeight;
                                            if (ratio > 3) {
                                                ratio = 3;
                                            }
                                            height = imageHeight * ratio;
                                            width = imageWidth;
                                        }
                                    } else {
                                        if (imageHeight > MAX_HEIGHT) {
                                            float temp = MAX_HEIGHT / imageHeight * imageWidth;
                                            width = temp > MIN_WIDTH ? temp : MIN_WIDTH;
                                            height = MAX_HEIGHT;
                                        } else if (imageHeight < MIN_HEIGHT) {
                                            float temp = MIN_HEIGHT / imageHeight * imageWidth;
                                            width = temp < MAX_WIDTH ? temp : MAX_WIDTH;
                                            height = MIN_HEIGHT;
                                        } else {
                                            float ratio = imageHeight / imageWidth;
                                            if (ratio > 3) {
                                                ratio = 3;
                                            }
                                            width = imageWidth * ratio;
                                            height = imageHeight;
                                        }
                                    }
                                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                    params.width = (int) width;
                                    params.height = (int) height;
                                    imageView.setLayoutParams(params);
                                    Matrix matrix = new Matrix();
                                    float scaleWidth = width / imageWidth;
                                    float scaleHeight = height / imageHeight;
                                    matrix.postScale(scaleWidth, scaleHeight);
                                    try {
                                        imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true));
                                    } catch (Exception e) {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                }
                            });
                }
            }

            @Override
            public void loadVideo(ImageView imageCover, String uri, String videoThumbnailPath, int videoThumbnailW, int videoThumbnailH) {
                if (videoThumbnailW != 0) {
                    ViewGroup.LayoutParams params = imageCover.getLayoutParams();

                    int width;
                    if (videoThumbnailW < videoThumbnailH) {
                        width = getScreenWidth() / 3;
                    } else {
                        width = getScreenWidth() / 2;
                    }

                    params.width = width;
                    params.height = (width * videoThumbnailH) / videoThumbnailW;
                    imageCover.setLayoutParams(params);

                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                            .error(R.drawable.imageloader)// 加载失败的图片  
                            .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

                    Glide.with(getApplicationContext())
                            .load(videoThumbnailPath)// 图片地址  
                            .apply(options)// 参数  
                            .into(imageCover);// 需要显示的ImageView控件
                } else {
                    long interval = 5000 * 1000;
                    Glide.with(MessageListActivity.this)
                            .load(uri)
                            .apply(new RequestOptions().frame(interval).override(200, 400))
                            .into(imageCover);
                }
            }

            @Override
            public void loadGift(ImageView imageGift, String giftIconImg, String giftEffectImg) {

                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                        .error(R.drawable.imageloader)// 加载失败的图片  
                        .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

                Glide.with(getApplicationContext())
                        .load(giftIconImg)
                        .apply(options)
                        .into(imageGift);

                /*if (options == null){
                    options = createDisplayImageOptions(imgUrl == null ? R.drawable.aurora_picture_not_found : 0);
                }
                File file = com.yjfshop123.live.server.utils.imageloader.ImageLoader.getInstance().getDiskCache().get(imgUrl);
                if (file == null && !TextUtils.isEmpty(imgUrl)) {
                    com.yjfshop123.live.server.utils.imageloader.ImageLoader.getInstance().displayImage(imgUrl, imageGift, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String var1, View var2) {
                        }

                        @Override
                        public void onLoadingFailed(String var1, View var2, FailReason var3) {
                        }

                        @Override
                        public void onLoadingComplete(String var1, View var2, Bitmap bitmap) {
                            try {
                                File file_ = com.yjfshop123.live.server.utils.imageloader.ImageLoader.getInstance().getDiskCache().get(imgUrl);
                                GifDrawable gifDrawable = new GifDrawable(file_);
                                imageGift.setImageDrawable(gifDrawable);
                            }catch (Exception e){}
                        }

                        @Override
                        public void onLoadingCancelled(String var1, View var2) {
                        }
                    }, null);
                }else {
                    try {
                        GifDrawable gifDrawable = new GifDrawable(file);
                        imageGift.setImageDrawable(gifDrawable);
                    }catch (Exception e){}
                }*/
            }
        };

        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);

        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(MyMessage message) {
                // do something
                if (message.getType() == IMessage.MessageType.RECEIVE_VIDEO.ordinal()
                        || message.getType() == IMessage.MessageType.SEND_VIDEO.ordinal()) {

                    if (!TextUtils.isEmpty(message.getMediaFilePath())) {
                        VideoPreviewActivity.startActivity(MessageListActivity.this,
                                message.getMediaFilePath(),
                                message.getVideoThumbnailPath());
                    }

                } else if (message.getType() == IMessage.MessageType.RECEIVE_IMAGE.ordinal()
                        || message.getType() == IMessage.MessageType.SEND_IMAGE.ordinal()) {

                    int position = mMsgIdList.indexOf(message.getMsgId());
                    if (mPathList.size() > 0) {
                        try {
                            Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                            intent.putExtra(Config.POSITION, position);
                            intent.putExtra("Picture", JsonMananger.beanToJson(mPathList));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (message.getType() == IMessage.MessageType.RECEIVE_MEDIA.ordinal()
                        || message.getType() == IMessage.MessageType.SEND_MEDIA.ordinal()) {

                    int which = message.getMediaType();
                    switch (which) {
                        case 1:
                            if (!canClick()) {
                                return;
                            }

                            type_PP = 111;
                            PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                            break;
                        case 2:
                            if (!canClick()) {
                                return;
                            }

                            type_PP = 112;
                            PermissionsUtils.onResume(MessageListActivity.this, MessageListActivity.this);
                            break;
                    }

                } else {
//                    Toast.makeText(getApplicationContext(), "点击消息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(View view, final MyMessage message) {
//                Toast.makeText(getApplicationContext(), "长按消息", Toast.LENGTH_SHORT).show();
                String[] items = new String[]{getString(R.string.de_dialog_item_message_copy),getString(R.string.de_dialog_item_message_delete), getString(R.string.cancel)};
                OptionsPopupDialog.newInstance(MessageListActivity.this, items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                    @Override
                    public void onOptionsItemClicked(int which) {
                        switch (which) {
                            case 0:
                                SystemUtils.setClipboard(MessageListActivity.this,message.getText());
                                break;
                            case 1:
                                mAdapter.deleteById(message.getMsgId());
                                RealmMessageUtils.deleteMessageMsg(message.getMsgId());
                                break;
                        }
                    }
                }).show();

//                conversation.deleteMessage(new Integer(message.getMsgId()));
//                //移除视图
//                mAdapter.deleteById(message.getMsgId());
                // do something

//                mAdapter.deleteById(String id): 根据 message id 来删除消息
//                mAdapter.deleteByIds(String[] ids): 根据 message id 批量删除
//                mAdapter.delete(IMessage message): 根据消息对象删除
//                mAdapter.delete(List messages): 批量删除
//                mAdapter.clear(): 清空消息

//                adapter.update(IMessage message)
//                adapter.update(String oldId, IMessage newMessage)
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {
                //头像点击事件
                if (message.getType() == IMessage.MessageType.RECEIVE_MEDIA.ordinal()
                        || message.getType() == IMessage.MessageType.RECEIVE_TEXT.ordinal()
                        || message.getType() == IMessage.MessageType.RECEIVE_IMAGE.ordinal()
                        || message.getType() == IMessage.MessageType.RECEIVE_VOICE.ordinal()
                        || message.getType() == IMessage.MessageType.RECEIVE_VIDEO.ordinal()
                        || message.getType() == IMessage.MessageType.RECEIVE_GIFT.ordinal()) {
                    ActivityUtils.startUserHome(mContext, imConversation.getOtherPartyId());
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } else {
                    ActivityUtils.startUserHome(mContext, imConversation.getUserIMId());
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });

        mAdapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener<MyMessage>() {
            @Override
            public void onStatusViewClick(MyMessage message) {
                // message status view click, resend or download here
            }
        });

        VerticalSwipeRefreshLayout mSwipeRefresh = mChatView.getSwipeRefresh();
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadNextPage();
                }
            });
        }

        // Deprecated, should use onRefreshBegin to load next page
        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
//                loadNextPage();
            }
        });

        mChatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private DisplayImageOptions options;

    private DisplayImageOptions createDisplayImageOptions(int defaultResId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        if (defaultResId != 0) {
            Drawable defaultDrawable = this.getResources().getDrawable(defaultResId);
            builder.showImageOnLoading(defaultDrawable);
            builder.showImageForEmptyUri(defaultDrawable);
            builder.showImageOnFail(defaultDrawable);
        }

        builder.displayer(new CircleBitmapDisplayer());
        DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    private boolean canClick() {
        return ClickUtil.canClick();
    }

    private void loadNextPage() {
        //拉取下一页消息
        page++;
        loadMessages();
    }

    private int page = 1;
    private int pageSize = 10;

    private RealmResults<MessageDB> query;
    private IMConversation imConversation;

    /**
     * 加载本地消息
     */
    private void loadMessages() {
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(MessageDB.class).equalTo("conversationId", imConversation.getConversationId()).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<MessageDB>>() {
            @Override
            public void onChange(RealmResults<MessageDB> element) {
                element = element.sort("timestamp");//时间戳 增序
                //("id", Sort.DESCENDING);降序

                if (mData.size() > 0) {
                    mData.clear();
                }

                if (mPathList.size() > 0) {
                    mPathList.clear();
                }

                if (mMsgIdList.size() > 0) {
                    mMsgIdList.clear();
                }

                if (mAdapter.getItemCount() > 0) {
                    mAdapter.clear();
                }

                MyMessage message = null;
                List<MessageDB> list = mRealm.copyFromRealm(element);

                int startIndex = 0;
                int size = list.size();
                int queryCount = page * pageSize;
                if (size > queryCount) {
                    startIndex = size - queryCount;
                }

                for (int i = startIndex; i < size; i++) {
                    String msgId = list.get(i).getMessageId();
                    //处理各种类型消息
                    switch (list.get(i).getType()) {// 0 文字  1 图片 2 语音  4  礼物  3 视频
                        case 0:
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(list.get(i).getText(), IMessage.MessageType.RECEIVE_TEXT.ordinal(), msgId);
                            } else {
                                message = new MyMessage(list.get(i).getText(), IMessage.MessageType.SEND_TEXT.ordinal(), msgId);
                            }
                            break;
                        case 1:
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(null, IMessage.MessageType.RECEIVE_IMAGE.ordinal(), msgId);
                            } else {
                                message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal(), msgId);
                            }

                            String imageThumbnailURL = list.get(i).getImageThumbnailURL();
                            String imageOriginalURL = list.get(i).getImageOriginalURL();

                            mPathList.add(imageOriginalURL);
                            mMsgIdList.add(message.getMsgId());

                            if (TextUtils.isEmpty(imageThumbnailURL)) {
                                message.setMediaFilePath(imageOriginalURL);
                            } else {
                                message.setMediaFilePath(imageThumbnailURL);
                            }
                            message.setImageW(list.get(i).getImageW());
                            message.setImageH(list.get(i).getImageH());
                            break;
                        case 2:
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(null, IMessage.MessageType.RECEIVE_VOICE.ordinal(), msgId);
                            } else {
                                message = new MyMessage(null, IMessage.MessageType.SEND_VOICE.ordinal(), msgId);
                            }
                            message.setMediaFilePath(list.get(i).getVoicePath());
                            message.setDuration(list.get(i).getVoiceDuration());
                            break;
                        case 3:
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(null, IMessage.MessageType.RECEIVE_VIDEO.ordinal(), msgId);
                            } else {
                                message = new MyMessage(null, IMessage.MessageType.SEND_VIDEO.ordinal(), msgId);
                            }
                            message.setMediaFilePath(list.get(i).getVideoPath());
                            message.setDuration(list.get(i).getVideoDuration());
                            message.setVideoThumbnailW(list.get(i).getVideoThumbnailW());
                            message.setVideoThumbnailH(list.get(i).getVideoThumbnailH());
                            message.setVideoThumbnailPath(list.get(i).getVideoThumbnailPath());
                            break;
                        case 10://视频 语音通话 记录处理
                            String txt = list.get(i).getText();
                            if (TextUtils.isEmpty(txt)) {
                                txt = "已取消";
                            } else {
                                txt = "聊天时长 " + txt;
                            }
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(txt, IMessage.MessageType.RECEIVE_MEDIA.ordinal(), msgId);
                            } else {
                                message = new MyMessage(txt, IMessage.MessageType.SEND_MEDIA.ordinal(), msgId);
                            }
                            message.setMediaType(list.get(i).getMediaType());
                            break;
                        case 4:
                            if (list.get(i).getIsSender() == 0) {
                                message = new MyMessage(null, IMessage.MessageType.RECEIVE_GIFT.ordinal(), msgId);
                            } else {
                                message = new MyMessage(null, IMessage.MessageType.SEND_GIFT.ordinal(), msgId);
                            }
                            message.setGiftName(list.get(i).getGiftName());
                            message.setGiftEffectImg(list.get(i).getGiftEffectImg());
                            message.setGiftCoinZh(list.get(i).getGiftCoinZh());
                            message.setGiftIconImg(list.get(i).getGiftIconImg());
                            break;
                    }

                    if (list.get(i).getIsSender() == 0) {
                        message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), imConversation.getOtherPartyName(), imConversation.getOtherPartyAvatar()));
                    } else {
                        message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));
                        if (list.get(i).getIsSenderResult() == 1) {
                            message.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
                        } else {
                            message.setMessageStatus(IMessage.MessageStatus.SEND_FAILED);
                        }
                    }

                    //两条消息之间 超过3分钟 显示时间
                    if (i == 0) {
                        message.setTimeString(TimeUtil.getTimeStr(list.get(i).getTimestamp()));
                    } else {
                        long timestamp_ = list.get(i - 1).getTimestamp();
                        long timestamp = list.get(i).getTimestamp();
                        if ((timestamp - timestamp_) > 180) {
                            message.setTimeString(TimeUtil.getTimeStr(list.get(i).getTimestamp()));
                        }
                    }

                    mData.add(message);

//                    重大事件提示 时间提示
//                    MyMessage eventMsg = new MyMessage("结束通话", IMessage.MessageType.EVENT.ordinal());
//                    mAdapter.addToStart(eventMsg, true);
                }

                mAdapter.addToEndChronologically(mData);
                mChatView.finishRefresh();
                if (page == 1) {
                    scrollToBottom();
                }
            }
        });
    }

    @Subscriber(tag = Config.EVENT_START)
    public void onMessage(VIPEvent event) {
        if (event.getType() == 1) {
            recharge();
        } else {
            recharge2();
        }
    }

    private void recharge() {
        String vipMessage = UserInfoUtil.getVIPMessage();
        if (!TextUtils.isEmpty(vipMessage)) {
            String[] vipMessages = UserInfoUtil.getVIPMessage().split("@@");
            if (vipMessages[1].equals("false")) {
                UserInfoUtil.setVIPMessage(vipMessages[0] + "@@" + "true");

                PromptPopupDialog.newInstance(this,
                        "",
                        "充VIP无限畅聊",
                        "成为VIP")
                        .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                Intent intent = new Intent(mContext, MyWalletActivity1.class);
                                intent.putExtra("currentItem", 1);
                                startActivity(intent);
                            }
                        }).setLayoutRes(R.layout.vip_popup_dialog).show();
            }
        }
    }

    private void recharge2() {
        String goldMessage = UserInfoUtil.getGoldMessage();
        if (!TextUtils.isEmpty(goldMessage)) {
            String[] goldMessages = UserInfoUtil.getGoldMessage().split("@@");
            if (goldMessages[1].equals("false")) {
                UserInfoUtil.setGoldMessage(goldMessages[0] + "@@" + "true");

                PromptPopupDialog.newInstance(this,
                        "",
                        "金蛋充值",
                        getString(R.string.recharge))
                        .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                Intent intent = new Intent(mContext, MyWalletActivity1.class);
                                intent.putExtra("currentItem", 0);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                            }
                        }).setLayoutRes(R.layout.recharge_popup_dialog).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mSensorManager.unregisterListener(this);
        if (query != null) {
            query.removeAllChangeListeners();
        }
        RealmConverUtils.conversationId = null;

        EventBus.getDefault().unregister(this);
    }

    private void scrollToBottom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatView.getMessageListView().smoothScrollToPosition(0);
            }
        }, 200);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                try {
                    View v = getCurrentFocus();
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        view.clearFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
        }
        return false;
    }

    @Override
    public void updateMessage(MessageDB messageDB) {

    }

    @Override
    public void onLoginSDKSuccess() {
        NToast.shortToast(mContext, "发送失败，请重新发送");
    }

    @Override
    public void onLoginSDKFailed(int i, String s) {
        NToast.shortToast(mContext, "登录失败，请退出APP重新登录");
    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (code == 6013 || code == 6014) {
            LoginHelper loginHelper = new LoginHelper(this);
            loginHelper.loginSDK(UserInfoUtil.getMiTencentId(), UserInfoUtil.getSignature());
        } else {
            if (desc != null) {
                NToast.shortToast(mContext, desc);
            }
        }

        if (messageDB != null && messageDB.getType() == 10) {
            //视频语音通话发送
            return;
        }

        if (imConversationDB != null) {
            imConversationDB.setLastMessage(/*"[失败]"*/"❗" + imConversationDB.getLastMessage());
            RealmConverUtils.addConverMsg(imConversationDB);
        }

        if (messageDB != null) {
            messageDB.setIsSender(1);
            messageDB.setIsSenderResult(0);
            RealmMessageUtils.addMessageMsg(messageDB, null);
        }
    }

    @Override
    public void onSuccess(TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10) {
            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, true);
            return;
        }

        if (imConversationDB != null) {
            RealmConverUtils.addConverMsg(imConversationDB);
        }

        if (messageDB != null) {
            messageDB.setIsSender(1);
            messageDB.setIsSenderResult(1);
            RealmMessageUtils.addMessageMsg(messageDB, null);
        }
    }

    public void sendGift() {
        int receive_uid = Integer.parseInt(imConversation.getOtherPartyIMId());
        int robot_id = Integer.parseInt(imConversation.getOtherPartyId());
        if (mGiftPost >= giftListResponse.getList().size()) return;
        String gift_uni_code = giftListResponse.getList().get(mGiftPost).getGift_uni_code();
        String live_id = "0";
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("receive_uid", receive_uid)
                    .put("robot_id", robot_id)
                    .put("gift_uni_code", gift_uni_code)
                    .put("gift_num", 1)//礼物数量，不传默认为1
                    .put("live_id", live_id)//房间ID，默认为0
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/gift/sendGift", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                shoeHintDialog(errInfo, errCode);
            }

            @Override
            public void onSuccess(String result) {
                GiftMessage giftMessage = new GiftMessage(giftListResponse.getList().get(mGiftPost).getName(),
                        giftListResponse.getList().get(mGiftPost).getIcon_img(),
                        giftListResponse.getList().get(mGiftPost).getEffect_img(),
                        giftListResponse.getList().get(mGiftPost).getCoin_zh(),
                        imConversation);
                chatPresenter.sendMessage(giftMessage.getMessage(), null, false);

                getGiftList();
            }
        });
    }

    private void getGiftList() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", "1")//场景类型 1:社区聊天场景 2:直播场景
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/gift/getGiftList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    giftListResponse = JsonMananger.jsonToBean(result, GiftListResponse.class);
                    if (gift != null && gift.equals("GIFT") && ISGIFT) {
                        ISGIFT = false;
                        dialogGift();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGiftList();
        getChatState();
    }

    private void getChatState() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", imConversation.getUserIMId())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/getChatState", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    isMsg = data.getInt("is_msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void launchChat() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("accept_uid", imConversation.getOtherPartyIMId())
                    .put("type", type_v_)
                    .put("robot_id", imConversation.getOtherPartyId())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/launchChat", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                shoeHintDialog(errInfo, errCode);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    LaunchChatResponse response = JsonMananger.jsonToBean(result, LaunchChatResponse.class);
                    response_(response);
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //    public static boolean isMeVip = false;
    public static int isMsg = 1;

    private int type_v_;

    public void response_(LaunchChatResponse response) {
        //-1 无限聊天 >0剩余可聊时间
        //0 没钱了
        RoomActivity.order_no = null;
        RoomActivity.rest_time = 0;
        int rest_time = response.getRest_time();
//        if (rest_time == 0) {
//            shoeHintDialog("金蛋不足", 201);
//        } else {
            RoomActivity.order_no = response.getOrder_no();
            RoomActivity.rest_time = rest_time;
            MediaMessage imageMessage;
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2) {
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        1, 0, response.getSpeech_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            } else if (type_v_ == 3) {
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        2, 0, response.getVideo_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }
       // }

            /*RoomActivity.order_no = response.getData().getOrder_no();
            MediaMessage imageMessage;
            int home_id = response.getData().getHome_id();
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        1,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        2,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }*/
    }

    private void shoeHintDialog(String msg, int code) {
        if (code == 201) {
            PromptPopupDialog.newInstance(this,
                    "",
                    msg,
                    getString(R.string.recharge))
                    .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            Intent intent = new Intent(mContext, MyWalletActivity1.class);
                            intent.putExtra("currentItem", 0);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    }).setLayoutRes(R.layout.recharge_popup_dialog).show();
        } else {
            DialogUitl.showSimpleHintDialog(this, getString(R.string.prompt), msg,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    //requestCode 10000
    //resultCode 10001 错误
    //resultCode 10002 图片
    //resultCode 10003 视频
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 10000) {
            return;
        }

        if (resultCode == 10001) {
            //错误
            return;
        }

        MyMessage message;

        if (resultCode == 10002) {
            //图片
            String imgPath = data.getStringExtra("imgPath");

            message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE.ordinal(), null);
            mPathList.add(imgPath);
            mMsgIdList.add(message.getMsgId());

//            long currentTime = System.currentTimeMillis() / 1000;
//            message.setTimeString(TimeUtil.getTimeStr(currentTime));

            message.setMediaFilePath(imgPath);
            message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, options);
            message.setImageW(options.outWidth);
            message.setImageH(options.outHeight);

            final MyMessage fMsg = message;
            MessageListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addToStart(fMsg, true);
                }
            });

            ImageMessage imageMessage = new ImageMessage(imgPath, true, imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), MessageListActivity.this, true);
            return;
        }

        if (resultCode == 10003) {
            //视频
            String videoPath = data.getStringExtra("videoPath");
            long time = data.getLongExtra("videoTime", 0);
            long duration = time / 1000;

            sendVideo(videoPath, duration);
            return;
        }
    }

    private synchronized void sendVideo(String videoPath, long time) {
        MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_VIDEO.ordinal(), null);
        long duration = time;
        message.setDuration(duration);

//        long currentTime = System.currentTimeMillis() / 1000;
//        message.setTimeString(TimeUtil.getTimeStr(currentTime));

        String filePath = videoPath;
        message.setMediaFilePath(filePath);
        message.setUserInfo(new DefaultUser(imConversation.getOtherPartyId(), miName, miAvatar));

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        String snapshotPath = FileUtil.createFile(thumb, /*new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())*/System.currentTimeMillis() + "" + time);
        long vHeight = thumb.getHeight();
        long vWidth = thumb.getWidth();

        message.setVideoThumbnailW((int) vWidth);
        message.setVideoThumbnailH((int) vHeight);
        message.setVideoThumbnailPath(snapshotPath);

        final MyMessage fMsg = message;
        MessageListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addToStart(fMsg, true);
            }
        });

        VideoMessage videoMessage = new VideoMessage(duration, filePath, snapshotPath, vHeight, vWidth, imConversation);
        chatPresenter.sendMessage(videoMessage.getMessage(), MessageListActivity.this, true);
    }
}
