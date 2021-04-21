package com.yjfshop123.live.live.live.common.widget.ready;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.MLVBLiveRoom;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.StringUtil;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;
import com.yjfshop123.live.live.live.common.widget.gift.view.DrawableTextView;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.ChannelListResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.ui.activity.DaRenRenZhengActivity;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;

public class LiveReadyViewHolder extends AbsViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView mAvatar;
    private TextView mCoverText;
    private EditText mEditTitle;
    private TextView mLocation;
    private TextView mLiveClass;
    private TextView mLiveTypeTextView;//房间类型TextView
    private int mLiveClassID = -1;//直播频道id
    private int mLiveType = TCConstants.LIVE_TYPE_NORMAL;//房间类型
    private String mLiveTypeVal = "";//房间密码;
    private int mCoin = 0;// 门票收费金额
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;
    private CommonCallback<ChannelListResponse.ChannelListBean> mLiveClassCallback;

    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.MORE;
    private CheckBox mCbShareWX;
    private CheckBox mCbShareCircle;
    private CheckBox mCbShareQQ;
    private CheckBox mCbShareQzone;

    private String location;

    protected MLVBLiveRoom mLiveRoom;
    private boolean pureAudio;

    public LiveReadyViewHolder(Context context, ViewGroup parentView, MLVBLiveRoom liveRoom) {
        super(context, parentView);
        mLiveRoom = liveRoom;

        OKHttpUtils.getInstance().getRequest("app/live/getChannelList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mLiveClass.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    ChannelListResponse channelListResponse = JsonMananger.jsonToBean(result, ChannelListResponse.class);
                    if (channelListResponse.getChannel_list().size() > 0) {
                        mLiveClass.setVisibility(View.VISIBLE);
                    } else {
                        mLiveClass.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    mLiveClass.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_ready;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mCoverText = (TextView) findViewById(R.id.cover_text);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLocation = (TextView) findViewById(R.id.location);
        location = UserInfoUtil.getAddress();
        mLocation.setText(location);
        mLiveClass = (TextView) findViewById(R.id.live_class);
        mLiveTypeTextView = (TextView) findViewById(R.id.btn_room_type);

        mCbShareWX = (CheckBox) findViewById(R.id.cb_share_wx);
        mCbShareCircle = (CheckBox) findViewById(R.id.cb_share_circle);
        mCbShareQQ = (CheckBox) findViewById(R.id.cb_share_qq);
        mCbShareQzone = (CheckBox) findViewById(R.id.cb_share_qzone);

        mCbShareWX.setOnCheckedChangeListener(this);
        mCbShareCircle.setOnCheckedChangeListener(this);
        mCbShareQQ.setOnCheckedChangeListener(this);
        mCbShareQzone.setOnCheckedChangeListener(this);

        mLiveClass.setOnClickListener(this);
        findViewById(R.id.avatar_group).setOnClickListener(this);
        ImageView btn_camera = (ImageView) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        DrawableTextView btn_beauty = (DrawableTextView) findViewById(R.id.btn_beauty);
        if (mContext instanceof TCLiveBasePublisherActivity) {
            pureAudio = ((TCLiveBasePublisherActivity) mContext).pureAudio;
        }
        if (pureAudio) {
            btn_beauty.setVisibility(View.GONE);
            btn_camera.setVisibility(View.GONE);
        } else {
            btn_beauty.setVisibility(View.VISIBLE);
            btn_camera.setVisibility(View.VISIBLE);
            btn_beauty.setOnClickListener(this);
        }
        findViewById(R.id.btn_start_live).setOnClickListener(this);
        mLiveTypeTextView.setOnClickListener(this);

        mLiveClassCallback = new CommonCallback<ChannelListResponse.ChannelListBean>() {
            @Override
            public void callback(ChannelListResponse.ChannelListBean bean) {
                mLiveClassID = bean.getId();
                mLiveClass.setText(bean.getName());
            }
        };

        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case TCConstants.LIVE_TYPE_NORMAL:
                        onLiveTypeNormal(bean);
                        break;
                    case TCConstants.LIVE_TYPE_PWD:
                        onLiveTypePwd(bean);
                        break;
                    case TCConstants.LIVE_TYPE_PAY:
                        onLiveTypePay(bean);
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.avatar_group) {
            ((TCLiveBasePublisherActivity) mContext).initOss();
        } else if (i == R.id.btn_camera) {
            toggleCamera();
        } else if (i == R.id.btn_close) {
            close();
        } else if (i == R.id.live_class) {
            chooseLiveClass();
        } else if (i == R.id.btn_beauty) {
            beauty();
        } else if (i == R.id.btn_room_type) {
            chooseLiveType();
        } else if (i == R.id.btn_start_live) {
            long curTime = System.currentTimeMillis();
            if (curTime < mLastLinkMicTime + LINKMIC_INTERVAL) {
                NToast.shortToast(mContext, "太频繁啦，休息一下！");
            } else {
                mLastLinkMicTime = curTime;

                if (TextUtils.isEmpty(mPath)) {
                    startLive(null);
                } else {
                    ((TCLiveBasePublisherActivity) mContext).uploadOss();
                }
            }
        }
    }

    private long mLastLinkMicTime = 0;
    private static final long LINKMIC_INTERVAL = 2 * 1000;

    /**
     * 切换镜头
     */
    private void toggleCamera() {
        if (mLiveRoom != null) {
            mLiveRoom.switchCamera();
        }
    }

    /**
     * 关闭
     */
    private void close() {
        ((TCLiveBasePublisherActivity) mContext).exit();
    }

    /**
     * 选择直播频道
     */
    private void chooseLiveClass() {
        Bundle bundle = new Bundle();
        bundle.putInt(TCConstants.CLASS_ID, mLiveClassID);
        LiveClassDialogFragment fragment = new LiveClassDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveClassCallback);
        fragment.show(((TCLiveBasePublisherActivity) mContext).getSupportFragmentManager(), "LiveClassDialogFragment");
    }

    /**
     * 设置美颜
     */
    private void beauty() {
        ((TCLiveBasePublisherActivity) mContext).beauty();
    }

    /**
     * 选择直播类型
     */
    private void chooseLiveType() {
        Bundle bundle = new Bundle();
        bundle.putInt(TCConstants.CHECKED_ID, mLiveType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveTypeCallback);
        fragment.show(((TCLiveBasePublisherActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveType = bean.getId();
        mLiveTypeTextView.setText(mContext.getString(bean.getName()));
        mLiveTypeVal = "";
        mCoin = 0;
    }

    /**
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, mContext.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8,
                false, new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        if (TextUtils.isEmpty(content)) {
                            NToast.shortToast(mContext, R.string.live_set_pwd_empty);
                        } else if (content.length() < 4 || content.length() > 8) {
                            NToast.shortToast(mContext, R.string.live_set_pwd_empty_1);
                        } else {
                            mLiveType = bean.getId();
                            mLiveTypeTextView.setText(mContext.getString(bean.getName()));

                            mLiveTypeVal = content;
//                    if (StringUtil.isInt(content)) {
//                        mLiveTypeVal = Integer.parseInt(content);
//                    }
                            dialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, mContext.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8,
                false, new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        if (TextUtils.isEmpty(content)) {
                            NToast.shortToast(mContext, R.string.live_set_fee_empty);
                        } else {
                            mLiveType = bean.getId();
                            mLiveTypeTextView.setText(mContext.getString(bean.getName()));
                            if (StringUtil.isInt(content)) {
                                mCoin = Integer.parseInt(content);
                            }
                            dialog.dismiss();
                        }
                    }
                });
    }


    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击开始直播按钮
     */
    public void startLive(String cover_img) {
        final String title = mEditTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            NToast.shortToast(mContext, "请输入直播标题");
            return;
        }

        /*if (mLiveClassID == -1){
            NToast.shortToast(mContext, "请选择频道");
            return;
        }*/

        String city_name = location;
        int channel_id = mLiveClassID;
        int type = 1;
        if (mLiveType == TCConstants.LIVE_TYPE_NORMAL) {
            type = 1;
        } else if (mLiveType == TCConstants.LIVE_TYPE_PWD) {
            type = 2;
        } else if (mLiveType == TCConstants.LIVE_TYPE_PAY) {
            type = 3;
        }
        String in_password = mLiveTypeVal;
        String in_coin = String.valueOf(mCoin);

        if (cover_img == null) {
            cover_img = UserInfoUtil.getAvatar();
        }

        int live_mode;
        if (pureAudio) {
            live_mode = 2;
        } else {
            live_mode = 1;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("title", title)//标题
                    .put("channel_id", channel_id)//频道ID
                    .put("type", type)//房间类型(1:普通房间 2:密码房间 3:门票房间)
                    .put("in_password", in_password)//密码
                    .put("cover_img", cover_img)//封面图
                    .put("city_name", city_name)//城市名称
                    .put("in_coin", in_coin)//门票金蛋数
                    .put("live_mode", live_mode)//直播类型(1:视频直播 2:语音直播)
                    .build();
        } catch (JSONException e) {
        }
        NLog.e("直播数据", body + "");

        OKHttpUtils.getInstance().getRequest("app/live/launchLive", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (!TextUtils.isEmpty(errInfo) && errInfo.equals("您还不是主播，请先认证主播")) {
                    DialogUitl.showSimpleDialog(mContext,
                            "您还不是主播，是否去认证？",
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {

                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, DaRenRenZhengActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });

                    return;
                }
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                ((TCLiveBasePublisherActivity) mContext).createRoom(result, mShare_meidia);
            }
        });
    }

    public void release() {
        mLiveTypeCallback = null;
        mLiveClassCallback = null;
    }

    private CompoundButton mCbLastChecked = null;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == false) {
            mShare_meidia = SHARE_MEDIA.MORE;
            mCbLastChecked = null;
            return;
        }
        if (mCbLastChecked != null) {
            mCbLastChecked.setChecked(false);
        }
        mCbLastChecked = buttonView;
        switch (buttonView.getId()) {
            case R.id.cb_share_wx:
                mShare_meidia = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.cb_share_circle:
                mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case R.id.cb_share_qq:
                mShare_meidia = SHARE_MEDIA.QQ;
                break;
            case R.id.cb_share_qzone:
                mShare_meidia = SHARE_MEDIA.QZONE;
                break;
            default:
                break;
        }
    }

    private String mPath = null;

    public void loadPath(String path) {
        mPath = path;
        if (mPath != null) {
            Glide.with(mContext).load(mPath).into(mAvatar);
            mCoverText.setText(mContext.getString(R.string.live_cover_2));
            mCoverText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_live_cover));
        }
    }
}
