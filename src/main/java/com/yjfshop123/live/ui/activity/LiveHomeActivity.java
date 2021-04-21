package com.yjfshop123.live.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.other.MainStartDialogFragment;
import com.yjfshop123.live.live.live.push.camera.TCLivePublisherActivity;
import com.yjfshop123.live.message.ConversationPresenter;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.message.interf.ConversationView;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.ui.fragment.CommunityFragment;
import com.yjfshop123.live.ui.fragment.ConversationFragment;
import com.yjfshop123.live.ui.fragment.H_1_Fragment;
import com.yjfshop123.live.ui.fragment.MySelfFragmentNew;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.UpdateUtils;
import com.yjfshop123.live.video.VideoRecordActivity;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.umeng.socialize.UMShareAPI;

import org.simple.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

public class LiveHomeActivity extends BaseActivityH implements OnTabSelectListener, BadgeDismissListener, MapLoiIml, ConversationView {

    private SparseArray<String> mSparseTags = new SparseArray<>();
    private JPTabBar mTabbar;

    private MapUtil mapUtil = new MapUtil(this, this);
    private String mi_platformId;
    private int info_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mi_platformId = UserInfoUtil.getMiPlatformId();
        info_complete = UserInfoUtil.getInfoComplete();

        setContentView(R.layout.activity_home_live);
        mTabbar = findViewById(R.id.home_tabbar);

        mTabbar.setTitles(getString(R.string.x_recommended), getString(R.string.home_bar4), getString(R.string.x_message), getString(R.string.x_me))
                .setNormalIcons(R.drawable.tab_home_home, R.drawable.tab_home_sq, R.drawable.tab_home_msg, R.drawable.tab_home_me)
//                .setSelectedIcons(R.drawable.tab_chat_hover, R.drawable.tab_contacts_hover, R.drawable.tab_found_hover, R.drawable.tab_me_hover)
                .generate();

        mTabbar.setTabListener(this);
        mTabbar.setUseFilter(true);
        mTabbar.setDismissListener(this);

        mSparseTags.put(POS_0, "F_1");
        mSparseTags.put(POS_1, "F_2");
        mSparseTags.put(POS_2, "F_3");
        mSparseTags.put(POS_3, "F_4");

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        openFragment(POS_0);

        startMap();

        systemMessage = new SystemMessage();
        if (!TextUtils.isEmpty(mi_platformId) && info_complete != 0){
            queryConverMsg();
            new ConversationPresenter(this);

            SocketUtil.getInstance().isLL(true).connect();
        }

        initListener();
        checkUpdate();
    }

    private void initListener() {
        BroadcastManager.getInstance(mContext).addAction(Config.LOGIN, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String dada = intent.getStringExtra("String");
                if (dada.equals(Config.LOGINSUCCESS)){
                    //登入

                    mi_platformId = UserInfoUtil.getMiPlatformId();
                    info_complete = UserInfoUtil.getInfoComplete();
                    systemMessage = new SystemMessage();
                    queryConverMsg();
                    new ConversationPresenter(LiveHomeActivity.this);
//                    request(onlineNotice, true);

                    SocketUtil.getInstance().isLL(true).connect();
                }else if (dada.equals(Config.LOGOUTSUCCESS)){
                    //登出

                    if (info_complete == 0){
                        return;
                    }

                    mi_platformId = null;
                    info_complete = 0;

                    UserInfoUtil.setMiTencentId("");
                    UserInfoUtil.setMiPlatformId("");
                    UserInfoUtil.setToken_InfoComplete("", 0);
                    UserInfoUtil.setFromUid("");
                    UserInfoUtil.setIsRead(false);

                    mTabbar.setSelectTab(0);

                    mTabbar.showBadge(2, 0, true);
                    ShortcutBadger.applyCount(App.getInstance(), 0); //for 1.1.4+

                    SocketUtil.getInstance().isLL(false).disconnect();
                    RouterUtil.logout(LiveHomeActivity.this);
                }
            }
        });
    }

//    //singleTask
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        try {
//            String data = intent.getStringExtra("DATA");
//            if (data.equals("queryConverMsg_")) {
//                mi_platformId = UserInfoUtil.getMiPlatformId();
//                info_complete = UserInfoUtil.getInfoComplete();
//                queryConverMsg();
//                new ConversationPresenter(this);
//
//                TCLiveRoomMgr.getDestroyLiveRoom();
//            }else if (data.equals("logOutSuccess")){
//                mTabbar.setSelectTab(0);
//                destroy();
//                App.iSDKID();
//
//                mi_platformId = null;
//                info_complete = 0;
//                mTabbar.showBadge(2, 0, true);
//                ShortcutBadger.applyCount(App.getInstance(), 0); //for 1.1.4+
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private int mIndex;

    @Override
    public boolean onTabSelect(int index) {
        if (index == POS_0 || index == POS_1){
            openFragment(index);
            return true;
        }else {
            if (isLogin()){
                openFragment(index);
                return true;
            }else {
                mTabbar.setSelectTab(mIndex);
                return false;
            }
        }
    }

    @Override
    public void onClickMiddle(View middleBtn) {
        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");
    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            if (!isLogin()){
                return;
            }

            //视频直播
            startActivity(new Intent(mContext, TCLivePublisherActivity.class).putExtra("pureAudio", false));
            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

            /*DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "视频直播",
                    "录屏直播", "选择直播方式", true, true,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {

                            //录屏直播
                            startActivity(new Intent(mContext, TCScreenRecordActivity.class).putExtra("pureAudio", false));
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();

                            //视频直播
                            startActivity(new Intent(mContext, TCLivePublisherActivity.class).putExtra("pureAudio", false));
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    });*/

            /*//视频直播
            startActivity(new Intent(mContext, TCLivePublisherActivity.class).putExtra("pureAudio", false));
            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);*/

            /*DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "视频直播",
                    "语音直播", "选择直播方式", true, true,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {

                            //语音直播
                            startActivity(new Intent(mContext, TCLivePublisherActivity.class).putExtra("pureAudio", true));
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();

                            //视频直播
                            startActivity(new Intent(mContext, TCLivePublisherActivity.class).putExtra("pureAudio", false));
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    });*/
        }

        @Override
        public void onVideoClick() {
            if (!isLogin()){
                return;
            }
            startActivity(new Intent(mContext, VideoRecordActivity.class));
            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        }
    };

    @Override
    public void onDismiss(int position) {
        if (!TextUtils.isEmpty(mi_platformId)){
            if (position == 2) {
                //取消 未读消息
                RealmConverUtils.clerRedCount(mi_platformId);
            }
        }
    }

    private boolean isLogin(){
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(mContext, LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
    }

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment mFragment1;
    private Fragment mFragment2;
    private Fragment mFragment3;
    private Fragment mFragment4;

    public void openFragment(int position) {
        mIndex = position;

        fragmentTransaction = fragmentManager.beginTransaction();
        if (null != mFragment1) {
            fragmentTransaction.hide(mFragment1);
        }
        if (null != mFragment2) {
            fragmentTransaction.hide(mFragment2);
        }
        if (null != mFragment3) {
            fragmentTransaction.hide(mFragment3);
        }
        if (null != mFragment4) {
            fragmentTransaction.hide(mFragment4);
        }

        if (position == POS_0) {
            if (null == mFragment1) {
                mFragment1 = new H_1_Fragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment1, mSparseTags.get(POS_0));
            }

            Bundle bundle = new Bundle();
            bundle.putString("TYPE", "1");
            mFragment1.setArguments(bundle);

//            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.show(mFragment1);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_1) {
            if (null == mFragment2) {
                mFragment2 = new CommunityFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment2, mSparseTags.get(POS_1));
            }
            fragmentTransaction.show(mFragment2);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_2) {
            if (null == mFragment3) {
                mFragment3 = new ConversationFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment3, mSparseTags.get(POS_2));
            }
            fragmentTransaction.show(mFragment3);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_3) {
            if (null == mFragment4) {
                mFragment4 = new MySelfFragmentNew();
                fragmentTransaction.add(R.id.home_container_fl, mFragment4, mSparseTags.get(POS_3));
            }
            fragmentTransaction.show(mFragment4);
            fragmentTransaction.commit();
            return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus() && event.getAction() == MotionEvent.ACTION_UP) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        mapUtil.destroy();
        super.onDestroy();
        destroy();
    }

    private void destroy(){
        RealmConverUtils.cancel();
        RealmMessageUtils.cancel();
        if (query != null) {
            query.removeAllChangeListeners();
        }

        SocketUtil.getInstance().close();
    }

    private void startMap() {
        mapUtil.LoPoi();
    }


    @Override
    public void onMapSuccess(double latitude, double longitude, String address, String currentWeizhi) {
    }

    @Override
    public void onMapFail(String msg) {
    }

    private void checkUpdate(){
        UpdateUtils updateUtils = new UpdateUtils(this);
        updateUtils.updateDiy();
    }

    @Override
    public void updateMessage(IMConversationDB imConversationDB) {

    }

    private RealmResults<IMConversationDB> query;

    private void queryConverMsg() {
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).equalTo("userIMId", mi_platformId).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);

                //
                long readCount_like = 0;
                long readCount_reply = 0;

                long readCount = 0;
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 2) {
                        readCount = datas.get(i).getUnreadCount();

                        systemMessage.setConversationId(datas.get(i).getConversationId());
                        systemMessage.setType(datas.get(i).getType());
                        systemMessage.setUnreadCount(datas.get(i).getUnreadCount());
                        systemMessage.setTimestamp(datas.get(i).getTimestamp());
                        systemMessage.setLastMessage(datas.get(i).getLastMessage());
                        systemMessage.setOtherPartyName(datas.get(i).getOtherPartyName());
                        datas.remove(i);
                    }
                }

                //
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 5) {
                        readCount_like = datas.get(i).getUnreadCount();
                        datas.remove(i);
                    }
                }
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 6) {
                        readCount_reply = datas.get(i).getUnreadCount();
                        datas.remove(i);
                    }
                }

                if (mList.size() > 0) {
                    mList.clear();
                }
                mList.addAll(datas);

                //
                eventModel2 = new EventModel2("forum_notice", readCount_like, readCount_reply);
                EventBus.getDefault().post(eventModel2,Config.EVENT_SHEQU);

                EventBus.getDefault().post(mList, Config.EVENT_START);
                EventBus.getDefault().post(systemMessage, Config.EVENT_START);

                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                mTabbar.showBadge(2, (int) readCount, true);

                ShortcutBadger.applyCount(App.getInstance(), (int) readCount); //for 1.1.4+
                //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3

                mTabbar.showBadge(1, (int) (readCount_like + readCount_reply), true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        request(onlineNotice, true);
    }
}