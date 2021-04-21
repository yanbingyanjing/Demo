package com.yjfshop123.live.video.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.UpdateUtils;
import com.yjfshop123.live.video.VideoRecordActivity;
import com.yjfshop123.live.video.fragment.HomeSVFragment;
import com.yjfshop123.live.video.fragment.LocalVideoFragment;
import com.yjfshop123.live.video.fragment.MeVideoFragment;
import com.yjfshop123.live.video.fragment.NavigationEndFragment;
import com.yjfshop123.live.video.fragment.VideoConversationFragment;

import org.simple.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

public class SmallVideoActivity extends BaseActivityH implements MapLoiIml, View.OnClickListener {

    private MapUtil mapUtil = new MapUtil(this, this);
    private String mi_platformId;
    private int info_complete;

    private TextView mSmallVideoHomeTv;
    private TextView mSmallVideoCityTv;
    private TextView mSmallVideoMessageTv;
    private TextView mSmallVideoMeTv;
    private TextView mSmallVideoUnreadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mi_platformId = UserInfoUtil.getMiPlatformId();
        info_complete = UserInfoUtil.getInfoComplete();
        setContentView(R.layout.activity_small_video);

        initView();

        fragmentManager = getSupportFragmentManager();
        openFragment(0);

        systemMessage = new SystemMessage();
        if (!TextUtils.isEmpty(mi_platformId) && info_complete != 0){
            queryConverMsg();
            SocketUtil.getInstance().isLL(true).connect();
        }

        startMap();
        initListener();
        checkUpdate();
    }

    private void initView(){
        mSmallVideoHomeTv = findViewById(R.id.small_video_home_tv);
        mSmallVideoHomeTv.setOnClickListener(this);
        mSmallVideoCityTv = findViewById(R.id.small_video_city_tv);
        mSmallVideoCityTv.setOnClickListener(this);
        mSmallVideoMessageTv = findViewById(R.id.small_video_message_tv);
        mSmallVideoMessageTv.setOnClickListener(this);
        mSmallVideoMeTv = findViewById(R.id.small_video_me_tv);
        mSmallVideoMeTv.setOnClickListener(this);
        findViewById(R.id.small_video_add_iv).setOnClickListener(this);
        mSmallVideoUnreadNum = findViewById(R.id.small_video_unread_num);

        drawer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.small_video_home_tv:
                openFragment(0);
                break;
            case R.id.small_video_city_tv:
                openFragment(1);
                break;
            case R.id.small_video_message_tv:
                if (isLogin()){
                    openFragment(2);
                }
                break;
            case R.id.small_video_me_tv:
                if (isLogin()){
                    openFragment(3);
                }
                break;
            case R.id.small_video_add_iv:
                if (!isLogin()){
                    return;
                }
                startActivity(new Intent(mContext, VideoRecordActivity.class));
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
        }
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
                    SocketUtil.getInstance().isLL(true).connect();
                }else if (dada.equals(Config.LOGOUTSUCCESS)){
                    logout();
                }
            }
        });
    }

    public void logout(){
        //登出
        try{
            if (info_complete == 0){
                return;
            }

            if (mFragment1 != null){
                mFragment1.cleanLogin();
            }

            if (mFragment2 != null){
                mFragment2.cleanLogin();
            }

            if (fragmentManager != null && mFragment4 != null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(mFragment4);
                fragmentTransaction.commit();
                mFragment4 = null;
            }

            mi_platformId = null;
            info_complete = 0;

            UserInfoUtil.setMiTencentId("");
            UserInfoUtil.setMiPlatformId("");
            UserInfoUtil.setToken_InfoComplete("", 0);
            UserInfoUtil.setFromUid("");
            UserInfoUtil.setIsRead(false);

            unRead(0);
            ShortcutBadger.applyCount(App.getInstance(), 0); //for 1.1.4+

            SocketUtil.getInstance().isLL(false).disconnect();

            openFragment(0);
            RouterUtil.logout(SmallVideoActivity.this);
        }catch (Exception e){}
    }

    private void unRead(long unRead){
        if (unRead <= 0){
            mSmallVideoUnreadNum.setVisibility(View.INVISIBLE);
        }else{
            mSmallVideoUnreadNum.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                mSmallVideoUnreadNum.setBackgroundResource(R.drawable.point1);
            }else{
                mSmallVideoUnreadNum.setBackgroundResource(R.drawable.point2);
                if (unRead > 99){
                    unReadStr = "99+";
                }
            }
            mSmallVideoUnreadNum.setText(unReadStr);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    private HomeSVFragment mFragment1;
    private LocalVideoFragment mFragment2;
    private Fragment mFragment3, mFragment4;

    public void openFragment(int position) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
        mSmallVideoHomeTv.setTextColor(getResources().getColor(R.color.color_999999));
        mSmallVideoCityTv.setTextColor(getResources().getColor(R.color.color_999999));
        mSmallVideoMessageTv.setTextColor(getResources().getColor(R.color.color_999999));
        mSmallVideoMeTv.setTextColor(getResources().getColor(R.color.color_999999));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (position == 0) {
            mSmallVideoHomeTv.setTextColor(getResources().getColor(R.color.white));
            if (null == mFragment1) {
                mFragment1 = new HomeSVFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment1, "F_0");
            }
            fragmentTransaction.show(mFragment1);
            fragmentTransaction.commit();
            return;
        }

        if (position == 1) {
            mSmallVideoCityTv.setTextColor(getResources().getColor(R.color.white));
            if (null == mFragment2) {
                mFragment2 = new LocalVideoFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment2, "F_1");
            }
            fragmentTransaction.show(mFragment2);
            fragmentTransaction.commit();
            return;
        }

        if (position == 2) {
            mSmallVideoMessageTv.setTextColor(getResources().getColor(R.color.white));
            if (null == mFragment3) {
                mFragment3 = new VideoConversationFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment3, "F_2");
            }
            fragmentTransaction.show(mFragment3);
            fragmentTransaction.commit();
            return;
        }

        if (position == 3) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mSmallVideoMeTv.setTextColor(getResources().getColor(R.color.white));
            if (null == mFragment4) {
                mFragment4 = new MeVideoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("USER_ID", "");
                mFragment4.setArguments(bundle);
                fragmentTransaction.add(R.id.home_container_fl, mFragment4, "F_3");
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

    private RealmResults<IMConversationDB> query;

    private void queryConverMsg() {
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).equalTo("userIMId", mi_platformId).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);

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

                eventModel2 = new EventModel2("forum_notice", readCount_like, readCount_reply);
                EventBus.getDefault().post(eventModel2, Config.EVENT_SHEQU);
                //
                EventBus.getDefault().post(mList, Config.EVENT_START);
                EventBus.getDefault().post(systemMessage, Config.EVENT_START);

                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                unRead(readCount);

                ShortcutBadger.applyCount(App.getInstance(), (int) readCount); //for 1.1.4+
                //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private NavigationView navigationEnd;
    private DrawerLayout drawerLayout;

    private void drawer(){
        navigationEnd = findViewById(R.id.navigation_end);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationEndFragment endFragment = new NavigationEndFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_end, endFragment).commit();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    public void closeDrawer(){
        if(drawerLayout.isDrawerOpen(navigationEnd)){
            drawerLayout.closeDrawer(navigationEnd);
        }
    }

    public void onDrawer(){
        if(drawerLayout.isDrawerOpen(navigationEnd)){
            drawerLayout.closeDrawer(navigationEnd);
        }else{
            drawerLayout.openDrawer(navigationEnd);
        }
    }
}
