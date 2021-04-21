package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.ChatPresenter;
import com.yjfshop123.live.message.ConversationFactory;
import com.yjfshop123.live.message.MessageFactory;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.FindRandomUserResponse;
import com.yjfshop123.live.net.response.LaunchChatResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScreeningActivity extends BaseActivity implements View.OnClickListener, ChatViewIF {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.screening_video)
    ImageView screening_video;
    @BindView(R.id.screening_chat)
    ImageView screening_chat;
    @BindView(R.id.screening_voice)
    ImageView screening_voice;

    @BindView(R.id.screening_refresh)
    ImageView screening_refresh;

    @BindView(R.id.screening_sex)
    ImageView screening_sex;
    @BindView(R.id.screening_name)
    TextView screening_name;
    @BindView(R.id.screening_id)
    TextView screening_id;
    @BindView(R.id.screening_info)
    TextView screening_info;

    @BindView(R.id.screening_icon)
    ImageView screening_icon;

    private String mi_tencentId;
    private String platformId;
    private String name;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_screening);
        ButterKnife.bind(this);

        setHeadLayout();
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(getString(R.string.screening_title));

        screening_video.setOnClickListener(this);
        screening_chat.setOnClickListener(this);
        screening_voice.setOnClickListener(this);
        screening_refresh.setOnClickListener(this);

        mi_tencentId = UserInfoUtil.getMiTencentId();
        platformId = UserInfoUtil.getMiPlatformId();
        name = UserInfoUtil.getName();
        avatar = UserInfoUtil.getAvatar();
        refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screening_video:
                startChat(2);
                break;
            case R.id.screening_chat:
                startChat(0);
                break;
            case R.id.screening_voice:
                startChat(1);
                break;
            case R.id.screening_refresh:
                refreshData();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshData() {
        findRandomUser();
    }

    private void findRandomUser(){
        OKHttpUtils.getInstance().getRequest("app/chat/findRandomUser", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    mResponse = JsonMananger.jsonToBean(result, FindRandomUserResponse.class);
                    loadData();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void launchChat(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("accept_uid", user_id_v_)
                    .put("type", type_v_)
                    .put("robot_id", robot_id_)
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

    private void loadData(){
        if (mResponse == null){
            return;
        }
        screening_name.setText(mResponse.getInfo().getUser_nickname());
        screening_id.setText("ID:" + mResponse.getInfo().getProm_custom_uid());
        screening_info.setText(getString(R.string.screening_age)+ mResponse.getInfo().getAge()
                + getString(R.string.screening_jx) + mResponse.getInfo().getHometown());
        if (mResponse.getInfo().getSex() == 1){
            screening_sex.setImageResource(R.drawable.ic_voice_sex_man);
        }else {
            screening_sex.setImageResource(R.drawable.ic_voice_sex_women);
        }
        Glide.with(mContext.getApplicationContext())
                .load(CommonUtils.getUrl(mResponse.getInfo().getAvatar()))
                .transition(new DrawableTransitionOptions().crossFade(1500))
//                .transition(new DrawableTransitionOptions().withCrossFade())
                .into(screening_icon);
    }

    /**
     * type 0聊天 1 语音  2 视频
     */
    private void startChat(int type) {

        if (mResponse == null){
            return;
        }

        chatPresenter = new ChatPresenter(this, String.valueOf(mResponse.getInfo().getProm_custom_uid()), TIMConversationType.C2C);

        int prom_custom_uid = mResponse.getInfo().getProm_custom_uid();
        int user_id = mResponse.getInfo().getUser_id();

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))){
            //自己不能跟自己聊天
            NToast.shortToast(mContext,  getString(R.string.not_me_chat));
            return;
        }

        if (prom_custom_uid == user_id){
            //真人
        }

        imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(platformId);

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(name);
        imConversation.setUserAvatar(avatar);

        imConversation.setOtherPartyName(mResponse.getInfo().getUser_nickname());
        imConversation.setOtherPartyAvatar(CommonUtils.getUrl(mResponse.getInfo().getAvatar()));

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        if (type == 0){
            Intent intent;
            intent = new Intent(mContext, MessageListActivity.class);
            intent.putExtra("IMConversation", imConversation);
            startActivity(intent);
            return;
        }

        user_id_v_ = prom_custom_uid;
        robot_id_ = user_id;
        if (type == 1){
            type_v_ = 2;
            launchChat();
            return;
        }

        if (type == 2){
            type_v_ = 3;
            launchChat();
            return;
        }
    }

    private FindRandomUserResponse mResponse;
    private ChatPresenter chatPresenter;
    private IMConversation imConversation;
    private int user_id_v_;
    private int robot_id_;
    private int type_v_;

    public void response_(LaunchChatResponse response){
        //-1 无限聊天 >0剩余可聊时间
        //0 没钱了
        RoomActivity.order_no = null;
        RoomActivity.rest_time = 0;
        int rest_time = response.getRest_time();
//        if (rest_time == 0){
//            shoeHintDialog("金蛋不足", 201);
//        }else {
            RoomActivity.order_no = response.getOrder_no();
            RoomActivity.rest_time = rest_time;
            MediaMessage imageMessage;
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        1,0, response.getSpeech_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        2,0, response.getVideo_cost(), imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }
        //}

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

    private void shoeHintDialog(String msg, int code){
        if (code == 201){
            PromptPopupDialog.newInstance(this,
                    "",
                    msg,
                    getString(R.string.recharge))
                    .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            Intent intent = new Intent(mContext, MyWalletActivity1.class);
                            intent.putExtra("currentItem",0);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    }).setLayoutRes(R.layout.recharge_popup_dialog).show();
        }else{
            DialogUitl.showSimpleHintDialog(this, getString(R.string.prompt), msg,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void updateMessage(MessageDB messageDB) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
//        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10){
            NToast.shortToast(mContext, "发送失败 请检测您的网络~");
            return;
        }
    }

    @Override
    public void onSuccess(TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10){
            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, true);
            return;
        }
    }
}


