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
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.R;
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
import com.yjfshop123.live.ui.fragment.H_1v1_Fragment;
import com.yjfshop123.live.ui.fragment.H_Video_Fragment;
import com.yjfshop123.live.ui.fragment.MySelfFragment1V1;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.UpdateUtils;
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

public class ChatHomeActivity extends BaseActivityH implements
        OnTabSelectListener
        , BadgeDismissListener
        , MapLoiIml
        , ConversationView {

    private SparseArray<String> mSparseTags = new SparseArray<>();
    private JPTabBar mTabbar;

    private MapUtil mapUtil = new MapUtil(this, this);
    private String mi_platformId;
    private int info_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mi_platformId = UserInfoUtil.getMiPlatformId();
        info_complete = UserInfoUtil.getInfoComplete();

        setContentView(R.layout.activity_home);
        mTabbar = findViewById(R.id.home_tabbar);

        mTabbar.setTitles(getString(R.string.x_recommended), "小视频", getString(R.string.home_bar), getString(R.string.x_message), getString(R.string.x_me))
                .setNormalIcons(R.drawable.tab_home_home, R.drawable.tab_home_video, R.drawable.tab_home_sq, R.drawable.tab_home_msg, R.drawable.tab_home_me)
                //.setSelectedIcons(R.drawable.tab_chat_hover, R.drawable.tab_contacts_hover, R.drawable.tab_ql_hover, R.drawable.tab_found_hover, R.drawable.tab_me_hover)
                .generate();

        mTabbar.setTabListener(this);
        mTabbar.setUseFilter(true);
        mTabbar.setDismissListener(this);

        mSparseTags.put(POS_0, "F_1");
        mSparseTags.put(POS_1, "F_2");
        mSparseTags.put(POS_2, "F_3");
        mSparseTags.put(POS_3, "F_4");
        mSparseTags.put(POS_4, "F_5");

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

        screening();
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
                    new ConversationPresenter(ChatHomeActivity.this);

                    /*if (Const.style == 3){
                        TCLiveRoomMgr.getDestroyLiveRoom();
                    }*/

//                    request(onlineNotice, true);

                    SocketUtil.getInstance().isLL(true).connect();
                }else if (dada.equals(Config.LOGOUTSUCCESS)){
                    //登出
                    try{
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

                        mTabbar.showBadge(3, 0, true);
                        ShortcutBadger.applyCount(App.getInstance(), 0); //for 1.1.4+
                        mTabbar.showBadge(2, 0, true);

                        SocketUtil.getInstance().isLL(false).disconnect();

                        mTabbar.setSelectTab(0);
                        RouterUtil.logout(ChatHomeActivity.this);
                    }catch (Exception e){}
                }
            }
        });
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

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final int POS_4 = 4;
    private int mIndex;

    @Override
    public boolean onTabSelect(int index) {
        if (index == POS_0 ||
                index == POS_1 ||
                index == POS_2){
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

    }

    @Override
    public void onDismiss(int position) {
        if (!TextUtils.isEmpty(mi_platformId)){
            if (position == 2) {
                RealmConverUtils.clerCommunityRedCount(mi_platformId);
            } else if (position == 3) {
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
    private Fragment mFragment5;

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
        if (null != mFragment5) {
            fragmentTransaction.hide(mFragment5);
        }

        if (position == POS_0) {
            if (null == mFragment1) {
                mFragment1 = new H_1v1_Fragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment1, mSparseTags.get(POS_0));
            }
            fragmentTransaction.show(mFragment1);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_1) {
            if (null == mFragment2) {
                mFragment2 = new H_Video_Fragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment2, mSparseTags.get(POS_1));
            }
            fragmentTransaction.show(mFragment2);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_2) {
            if (null == mFragment3) {
                mFragment3 = new CommunityFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment3, mSparseTags.get(POS_2));
            }
            fragmentTransaction.show(mFragment3);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_3) {
            if (null == mFragment4) {
                mFragment4 = new ConversationFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment4, mSparseTags.get(POS_3));
            }
            fragmentTransaction.show(mFragment4);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_4) {
            if (null == mFragment5) {
                mFragment5 = new MySelfFragment1V1();
                fragmentTransaction.add(R.id.home_container_fl, mFragment5, mSparseTags.get(POS_4));
            }
            fragmentTransaction.show(mFragment5);
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
                EventBus.getDefault().post(eventModel2, Config.EVENT_SHEQU);

                EventBus.getDefault().post(mList,Config.EVENT_START);
                EventBus.getDefault().post(systemMessage,Config.EVENT_START);

                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                mTabbar.showBadge(3, (int) readCount, true);

                ShortcutBadger.applyCount(App.getInstance(), (int) readCount); //for 1.1.4+
                //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3

                //
                mTabbar.showBadge(2, (int) (readCount_like + readCount_reply), true);
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
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TIMConversationUtil.lastMessage();
//            }
//        }, 2000);
//        }
    }

    private boolean touchResult = false;
    private int mStartX1, mStartY1, mLastX1, mLastY1;
    private int mScreenWidth, mScreenHeight;
    private int subWidth_50;
    private void screening(){
        mScreenWidth = CommonUtils.getScreenWidth(this);
        mScreenHeight = CommonUtils.getScreenHeight(this);
        subWidth_50 = CommonUtils.dip2px(mContext, 50);
        ImageView homeScreening = findViewById(R.id.home_screening);
        homeScreening.setImageResource(R.drawable.home_1v1_gotochoice);
        homeScreening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(mContext, WhitePanelActivity.class));
                if (!isLogin()){
                    return;
                }
                startActivity(new Intent(mContext, ScreeningActivity.class));
            }
        });
        homeScreening.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchResult = false;
                        mStartX1 = mLastX1 = (int) event.getRawX();
                        mStartY1 = mLastY1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int left, top, right, bottom;
                        int dx = (int) event.getRawX() - mLastX1;
                        int dy = (int) event.getRawY() - mLastY1;
                        left = v.getLeft() + dx;
                        if (left < 0) {
                            left = 0;
                        }
                        right = left + v.getWidth();
                        if (right > mScreenWidth) {
                            right = mScreenWidth;
                            left = right - v.getWidth();
                        }
                        top = v.getTop() + dy;
                        if (top < 0) {
                            top = 0;
                        }
                        bottom = top + v.getHeight();
                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        mLastX1 = (int) event.getRawX();
                        mLastY1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(subWidth_50, subWidth_50);
                        layoutParams.setMargins(v.getLeft(), v.getTop(), 0, 0);
                        //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                        v.setLayoutParams(layoutParams);
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (Math.abs(endX - mStartX1) > 5 || Math.abs(endY - mStartY1) > 5) {
                            //防止点击的时候稍微有点移动点击事件被拦截了
                            touchResult = true;
                        }
                        break;
                }
                return touchResult;
            }
        });
    }
}
