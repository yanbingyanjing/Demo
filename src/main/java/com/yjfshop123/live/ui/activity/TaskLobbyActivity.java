package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.net.response.ChatTaskResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.BottomMenuDialogQL;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.yjfshop123.live.ui.fragment.TaskVideoFragment;
import com.yjfshop123.live.ui.fragment.TaskVoiceFragment;
import com.yjfshop123.live.ui.widget.MorePopWindow;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.TaskAlertDialog;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;

public class TaskLobbyActivity extends BaseActivity implements ChatViewIF, IPermissions {

    private Context mContext;

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTabLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;

    private int type = 3;//聊天类型（1:文字 2:语音 3:视频）

    private ImageView qlRwAdd;

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        public void onPageSelected(int position) {

            /*if (position == 0){
                slide(true);
            }else {
                slide(false);
            }*/
        }

        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_task_lobby);
        setHeadVisibility(View.GONE);

        PermissionsUtils.initPermission(mContext);
        init();
    }

    private void init(){
        findViewById(R.id.atl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = findViewById(R.id.atl_vp);
        mSlidingTabLayout = findViewById(R.id.atl_nts_sty);

        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.task_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

//        findViewById(R.id.atl_vp_view).setBackground(new ColorDrawable(ThemeColorUtils.getThemeColor()));
//        findViewById(R.id.atl_vp_fl).setBackground(new ColorDrawable(ThemeColorUtils.getThemeColor()));

        qlRwAdd = findViewById(R.id.ql_rw_add_iv);
        qlRwAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorePopWindow morePopWindow = new MorePopWindow(TaskLobbyActivity.this, new MorePopWindow.VoiceClickListener() {
                    @Override
                    public void onVoiceClick() {
                        type = 2;
                        showPhotoDialog(type);
                    }
                }, new MorePopWindow.VideoClickListener() {
                    @Override
                    public void onVideoClick() {
                        type = 3;
                        showPhotoDialog(type);
                    }
                });
                morePopWindow.showPopupWindow(qlRwAdd);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        slide(true);

        getCostList();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);
            switch (position) {
                case POS_0:
                    if (mContent == null) {
                        mContent = new TaskVideoFragment();
                        mContentFragments.put(POS_0, mContent);
                    }
                    TaskVideoFragment fragment1 = (TaskVideoFragment)mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new TaskVoiceFragment();
                        mContentFragments.put(POS_1, mContent);
                    }
                    TaskVoiceFragment fragment2 = (TaskVoiceFragment) mContentFragments.get(POS_1);
                    return fragment2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
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

    private void publishChatTask(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("per_cost", per_cost)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/publishChatTask", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(mContext);
                if (errCode == 201) {//金蛋不足
                    PromptPopupDialog.newInstance(mContext,
                            "",
                            errInfo,
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
                } else{
                    DialogUitl.showSimpleHintDialog(mContext,
                            getString(R.string.prompt),
                            getString(R.string.ac_select_friend_sure),
                            getString(R.string.cancel),
                            errInfo,
                            true,
                            true,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {
                                }
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(mContext);
                if (type == 3){
                    mViewPager.setCurrentItem(0);
                }else {
                    mViewPager.setCurrentItem(1);
                }
                EventBus.getDefault().post(String.valueOf(type),Config.EVENT_START);//type 2:语音 3:视频
            }
        });
    }

    private void getCostList(){
        OKHttpUtils.getInstance().getRequest("app/user/getCostList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONArray list = data.getJSONArray("list");
                    priceList.clear();
                    for (int i = 0; i < list.length(); i++) {
                        priceList.add(list.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getChatTask(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("task_id", task_id_v_)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/getChatTask", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(mContext);
                shoeHintDialog(errInfo, errCode);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(mContext);
                try {
                    ChatTaskResponse response = JsonMananger.jsonToBean(result, ChatTaskResponse.class);
                    response_(response);
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private int type_tt = 1;
    private ChatTaskListResponse.ListBean mListBean;

    @Override
    public void allPermissions() {
        request_0_0(mListBean, type_tt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    public void request_(ChatTaskListResponse.ListBean listBean, int type){
        type_tt = type;
        mListBean = listBean;
        PermissionsUtils.onResume(TaskLobbyActivity.this, TaskLobbyActivity.this);
    }

    //type 1 语音 2视频
    private void request_0_0(ChatTaskListResponse.ListBean listBean, int type){
        if (listBean == null){
            return;
        }

        String mi_tencentId = UserInfoUtil.getMiTencentId();
        int prom_custom_uid = listBean.getProm_custom_uid();
        int user_id = listBean.getUser_id();

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))){
            //自己不能跟自己聊天
            return;
        }

        if (prom_custom_uid == user_id){
            //真人
        }

        chatPresenter = new ChatPresenter(this, String.valueOf(prom_custom_uid), TIMConversationType.C2C);

        imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(UserInfoUtil.getMiPlatformId());

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(UserInfoUtil.getName());
        imConversation.setUserAvatar(UserInfoUtil.getAvatar());

        imConversation.setOtherPartyName(listBean.getUser_nickname());
        imConversation.setOtherPartyAvatar(listBean.getAvatar());

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        if (type == 0){
            return;
        }

        task_id_v_ = listBean.getTask_id();
        if (type == 1){
            type_v_ = 2;
            getChatTask();
            return;
        }

        if (type == 2){
            type_v_ = 3;
            getChatTask();
            return;
        }
    }

    private ChatPresenter chatPresenter;
    private IMConversation imConversation;
    private int task_id_v_;
    private int type_v_;

    public void response_(ChatTaskResponse response){
        //-1 无限聊天 >0剩余可聊时间
        //0 没钱了
        RoomActivity.order_no = null;
        RoomActivity.rest_time = 0;
        int rest_time = response.getRest_time();
        if (rest_time == 0){
            shoeHintDialog("金蛋不足", 201);
        }else {
            RoomActivity.order_no = response.getOrder_no();
            RoomActivity.rest_time = rest_time;
            MediaMessage imageMessage;
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        1,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                        2,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }
        }

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

    /////////////////////////////////////////////////////////
    private BottomMenuDialogQL dialog;
    private int per_cost;

    private void showPhotoDialog(int type) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (priceList.size() == 0){
            return;
        }
        dialog = new BottomMenuDialogQL(mContext, type, String.valueOf(priceList.get(0)));
        dialog.setConfirmListener(new BottomMenuDialogQL.ConfirmListener() {
            @Override
            public void onClick(String cost) {
                per_cost = Integer.parseInt(cost);
                dialog.dismiss();
                LoadDialog.show(mContext);

                publishChatTask();
            }
        });
        dialog.setCostListener(new BottomMenuDialogQL.CostListener() {
            @Override
            public void onClick(TextView costTv) {
                popupDialog(costTv);
            }
        });
//        dialog.setCancelable(false);
        dialog.show();
    }

    private void popupDialog(final TextView costTv){
        final TaskAlertDialog priceAlertDialog = new TaskAlertDialog(this);
        priceAlertDialog.setData(priceList);
        priceAlertDialog.setTiTle(getString(R.string.task_1));
        priceAlertDialog.builder();
        priceAlertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        priceAlertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = priceAlertDialog.getPriceStr();
                costTv.setText(str);
            }
        });
        priceAlertDialog.show();
    }

    private ArrayList<Integer> priceList = new ArrayList<>();

}
