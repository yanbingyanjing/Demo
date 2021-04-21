package com.yjfshop123.live.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.other.MainStartDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.RotaryTableFragment;
import com.yjfshop123.live.live.live.push.camera.TCLivePublisherActivity;
import com.yjfshop123.live.message.ConversationPresenter;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.message.interf.ConversationView;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.model.PriceResponse;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.model.UserInfoResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.otc.CtcOtcOrderManagerActivity;
import com.yjfshop123.live.otc.MainOtcActivity;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.ui.ShopFragment;
import com.yjfshop123.live.socket.SocketMsgListener;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.ui.fragment.CommunityFragment;
import com.yjfshop123.live.ui.fragment.CommunityFragmentNew;
import com.yjfshop123.live.ui.fragment.H_1_Fragment;
import com.yjfshop123.live.ui.fragment.ConversationFragment;
import com.yjfshop123.live.ui.fragment.MyFragmentNewThree;
import com.yjfshop123.live.ui.fragment.MySelfFragmentNew;
import com.yjfshop123.live.ui.fragment.ShangchengFragment;
import com.yjfshop123.live.ui.widget.ChoujiangFragment;
import com.yjfshop123.live.ui.widget.DialogHomeActivitiesFragment;
import com.yjfshop123.live.ui.widget.DialogHomeAddFragment;
import com.yjfshop123.live.ui.widget.SexSelectDialogFragment;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.ShareUtill;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.UpdateUtils;
import com.yjfshop123.live.video.VideoRecordActivity;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.yjfshop123.live.App.is_check_update;

public class HomeActivity extends BaseActivityH implements
        OnTabSelectListener
        , BadgeDismissListener
        , MapLoiIml
        , ConversationView, View.OnClickListener, SocketMsgListener {

    private SparseArray<String> mSparseTags = new SparseArray<>();
    private JPTabBar mTabbar;

    private MapUtil mapUtil = new MapUtil(this, this);
    private String mi_platformId;
    private int info_complete;
    private LinearLayout live;
    private LinearLayout record;
    private TextView recordTx;
    private TextView liveTx;
    private View zhezhao;
    RelativeLayout homeTarget;
    RelativeLayout homeScreening;
    private TextView msg_red;
    private ImageView img_live;
    private ImageView img_video;
    private TextView target_msg;
    ImageView target_logo;
    ImageView weichuli_order,t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mi_platformId = UserInfoUtil.getMiPlatformId();
        info_complete = UserInfoUtil.getInfoComplete();

        StatusBarUtil.StatusBarDarkMode(this);
        setContentView(R.layout.activity_home);
        target_logo = findViewById(R.id.target_logo);
        t= findViewById(R.id.t);
        Glide.with(this).asGif().load(R.drawable.eggbag).into(t);
        target_msg = findViewById(R.id.target_msg);
        mTabbar = findViewById(R.id.home_tabbar);
        live = findViewById(R.id.btn_live);
        msg_red = findViewById(R.id.msg_count);
        record = findViewById(R.id.btn_video);
        img_live = findViewById(R.id.img_live);
        img_video = findViewById(R.id.img_video);
        liveTx = findViewById(R.id.tx_live);
        recordTx = findViewById(R.id.tx_video);
        homeScreening = findViewById(R.id.home_screening);
        homeTarget = findViewById(R.id.home_target);
        zhezhao = findViewById(R.id.zhezhao);
        weichuli_order = findViewById(R.id.weichuli_order);
        zhezhao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isAniming) {
                    if (!flag) {
                        closeAnim();  //收回菜单动画
                    }
                }
                return true;
            }
        });
        //  mTabbar.setTitles(getString(R.string.live_zhibo), getString(R.string.quanzi), getString(R.string.shangcheng), getString(R.string.x_message), getString(R.string.x_me))
        mTabbar.setTitles(getString(R.string.x_recommended), getString(R.string.shangcheng), "", getString(R.string.quanzi), getString(R.string.x_me))
                .setSelectedIcons(R.mipmap.home_selected, R.mipmap.shop_selected, R.mipmap.home_add, R.mipmap.quanzi_selected, R.mipmap.my_selected)
                .setNormalIcons(R.mipmap.home_unselect, R.mipmap.shop_unselect, R.mipmap.home_add, R.mipmap.quanzi_unselect, R.mipmap.my_unselect)
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
        if (!TextUtils.isEmpty(mi_platformId) && info_complete != 0) {
            queryConverMsg();
            new ConversationPresenter(this);

            SocketUtil.getInstance().isLL(true).connect();
            SocketUtil.getInstance().setMesgListener(this);
        }

        screening();
        initListener();
        checkUpdate();
        //检测是否安装交易所
        //onCheckIsInstallExchange();
//        RotaryTableFragment fragmentRotary = new RotaryTableFragment();
//        fragmentRotary.setHomeActivity(this);
//        fragmentRotary.show(getSupportFragmentManager(), "RotaryTableFragment");
        ChoujiangFragment fragmentRotary = new ChoujiangFragment();
        fragmentRotary.setHomeActivity(this);
        fragmentRotary.show(getSupportFragmentManager(), "ChoujiangFragment");
        live.setOnClickListener(this);
        record.setOnClickListener(this);
        initAnim();
        mScreenWidth = SystemUtils.getScreenWidth(this);
        mScreenHeight = SystemUtils.getScreenWidth(this);
        subWidth_50 = SystemUtils.dip2px(this, 50);
        loadUserConfig();
        weichuli_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CtcOtcOrderManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    int dingdan_count;

    @Subscriber(tag = Config.EVENT_OTC)
    public void onOtcTipsMessage(String content) {
        getUserStatus();
    }

    private void getUserStatus() {
        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                UserStatusResponse userStatusResponse = new Gson().fromJson(result, UserStatusResponse.class);
                dingdan_count = userStatusResponse.c2c_order_running_nums + userStatusResponse.otc_order_running_nums;
                if (dingdan_count > 0) {
                    if (mIndex != 0) {
                        weichuli_order.setVisibility(View.VISIBLE);
                    } else {
                        weichuli_order.setVisibility(View.INVISIBLE);
                    }

                } else {
                    weichuli_order.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    public void checkIsShowShare() {
        //        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

    }

    private void initListener() {
        BroadcastManager.getInstance(mContext).addAction(Config.LOGIN, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String dada = intent.getStringExtra("String");
                if (dada.equals(Config.LOGINSUCCESS)) {
                    //登入

                    mi_platformId = UserInfoUtil.getMiPlatformId();
                    info_complete = UserInfoUtil.getInfoComplete();
                    systemMessage = new SystemMessage();
                    queryConverMsg();
                    new ConversationPresenter(HomeActivity.this);

                    /*if (Const.style == 3){
                        TCLiveRoomMgr.getDestroyLiveRoom();
                    }*/

//                    request(onlineNotice, true);

                    SocketUtil.getInstance().isLL(true).connect();
                } else if (dada.equals(Config.LOGOUTSUCCESS)) {
                    NLog.e("收到退出消息", "1");
                    //登出
                    try {
                        if (info_complete == 0) {
                            return;
                        }
                        NLog.e("收到退出消息", "2");
                        mi_platformId = null;
                        info_complete = 0;

                        UserInfoUtil.setMiTencentId("");
                        UserInfoUtil.setMiPlatformId("");
                        UserInfoUtil.setToken_InfoComplete("", 0);
                        UserInfoUtil.setFromUid("");
                        UserInfoUtil.setIsRead(false);

                        mTabbar.showBadge(3, 0, true);
                        ShortcutBadger.applyCount(App.getInstance(), 0); //for 1.1.4+
                        mTabbar.showBadge(1, 0, true);

                        SocketUtil.getInstance().isLL(false).disconnect();

                        //mTabbar.setSelectTab(0);
                        RouterUtil.logout(HomeActivity.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    DialogHomeAddFragment dialogFragment = new DialogHomeAddFragment();

    @Override
    public boolean onTabSelect(int index) {
        if (index == POS_2) {
            dialogFragment.setListener(mMainStartChooseCallback);
            dialogFragment.show(getSupportFragmentManager(), "DialogHomeAddFragment");
            return false;
        }
//        if (index == POS_1) {
//            NToast.shortToast(this, getString(R.string.now_not_open));
//            return false;
//        }
        if (index == POS_0) {
            //homeTarget.setVisibility(View.INVISIBLE);
            homeScreening.setVisibility(View.INVISIBLE);
            weichuli_order.setVisibility(View.INVISIBLE);
            if (mFragment1 != null) {
                ((H_1_Fragment) mFragment1).setVisible(true);
            }
        }
//        else if (index == POS_1 || index == POS_3) {
//            StatusBarUtil.StatusBarLightMode(this);
//            if (mFragment1 != null) {
//                ((H_1_Fragment) mFragment1).setVisible(false);
//            }
//        }
        else {
            // if (data != null) homeTarget.setVisibility(View.VISIBLE);
            homeScreening.setVisibility(View.INVISIBLE);
            if (dingdan_count > 0) {
                weichuli_order.setVisibility(View.VISIBLE);
            } else weichuli_order.setVisibility(View.INVISIBLE);

            StatusBarUtil.StatusBarLightMode(this);
            if (mFragment1 != null) {
                ((H_1_Fragment) mFragment1).setVisible(false);
            }
        }

        if (index == POS_4) {
            homeTarget.setVisibility(View.VISIBLE);
            if (mFragment5 != null) ((MyFragmentNewThree) mFragment5).setUserVisibleHint(true);

        } else {
            if (data != null)
                homeTarget.setVisibility(View.VISIBLE);
            if (mFragment5 != null) ((MyFragmentNewThree) mFragment5).setUserVisibleHint(false);
        }
        if (index == POS_0 ||
                index == POS_3 ||
                index == POS_1) {
            openFragment(index);
            return true;
        } else {
            if (isLogin()) {
                openFragment(index);
                return true;
            } else {
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
        if (!TextUtils.isEmpty(mi_platformId)) {
            if (position == 1) {
                RealmConverUtils.clerCommunityRedCount(mi_platformId);
            } else if (position == 3) {
                //取消 未读消息
                RealmConverUtils.clerRedCount(mi_platformId);
            }
        }
    }

    private boolean isLogin() {
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // super.onSaveInstanceState(outState, outPersistentState);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

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
                mFragment1 = new H_1_Fragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment1, mSparseTags.get(POS_0));
            }

            Bundle bundle = new Bundle();
            bundle.putString("TYPE", "1");
            mFragment1.setArguments(bundle);

            fragmentTransaction.show(mFragment1);
            fragmentTransaction.commit();
            return;
        }
        if (position == POS_1) {
            if (null == mFragment4) {
                //   mFragment4 = new ShangchengFragment();
                mFragment4 = new ShopFragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment4, mSparseTags.get(POS_1));
            }

            fragmentTransaction.show(mFragment4);
            fragmentTransaction.commit();
            return;
        }

        if (position == POS_2) {
            if (null == mFragment2) {
                mFragment2 = new H_1_Fragment();
                fragmentTransaction.add(R.id.home_container_fl, mFragment2, mSparseTags.get(POS_2));
            }

            Bundle bundle = new Bundle();
            bundle.putString("TYPE", "2");
            mFragment2.setArguments(bundle);

            fragmentTransaction.show(mFragment2);
            fragmentTransaction.commit();
            return;
        }


        if (position == POS_3) {
            if (null == mFragment3) {
                //     mFragment3 = new CommunityFragment();
                mFragment3 = new CommunityFragmentNew();
                fragmentTransaction.add(R.id.home_container_fl, mFragment3, mSparseTags.get(POS_3));
            }
            fragmentTransaction.show(mFragment3);
            fragmentTransaction.commit();
            return;
//            if (null == mFragment4) {
//                mFragment4 = new ConversationFragment();
//                fragmentTransaction.add(R.id.home_container_fl, mFragment4, mSparseTags.get(POS_3));
//            }
//            fragmentTransaction.show(mFragment4);
//            fragmentTransaction.commit();
//            return;
        }

        if (position == POS_4) {
            if (null == mFragment5) {
                mFragment5 = new MyFragmentNewThree();
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

    private void destroy() {
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

    private void checkUpdate() {
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

                EventBus.getDefault().post(mList, Config.EVENT_START);
                EventBus.getDefault().post(systemMessage, Config.EVENT_START);

                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                //圈子消息数量
                if ((int) readCount == 0) {
                    msg_red.setVisibility(View.INVISIBLE);
                    msg_red.setText("");
                } else {
                    msg_red.setVisibility(View.VISIBLE);
                    msg_red.setText((int) readCount > 99 ? "99+" : readCount + "");
                }
                // mTabbar.showBadge(3, (int) readCount, true);

                ShortcutBadger.applyCount(App.getInstance(), (int) readCount); //for 1.1.4+
                //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3

                //圈子最新消息数量
                mTabbar.showBadge(3, (int) (readCount_like + readCount_reply), true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if(mFragment5!=null)mFragment5.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 10001 && resultCode == 10001) {
            mTabbar.setSelectTab(0);
            if (mFragment1 != null && mFragment1 instanceof H_1_Fragment) {
                if (((H_1_Fragment) mFragment1).mViewPager != null)
                    ((H_1_Fragment) mFragment1).mViewPager.setCurrentItem(1);
            }
        }
        if (requestCode == 10001 && resultCode == 10002) {
            mTabbar.setSelectTab(0);
            if (mFragment1 != null && mFragment1 instanceof H_1_Fragment) {
                if (((H_1_Fragment) mFragment1).mViewPager != null)
                    ((H_1_Fragment) mFragment1).mViewPager.setCurrentItem(0);
            }
        }
        if (requestCode == 10001 && resultCode == 10003) {
            mTabbar.setSelectTab(3);
        }
        if (requestCode == 10002 && resultCode == 10002) {
            mTabbar.setSelectTab(0);
            if (mFragment1 != null && mFragment1 instanceof H_1_Fragment) {
                if (((H_1_Fragment) mFragment1).mViewPager != null)
                    ((H_1_Fragment) mFragment1).mViewPager.setCurrentItem(2);
            }
        }
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
        //检测是否哦有需要打开的视频
        NLog.d("homeActivity","onResume");
        ShareUtill.checkIsShowShare(this);
        loadData();
        getUserStatus();
//        DialogUitl.showSimpleHintDialog(HomeActivity.this, getString(R.string.prompt), "去购买", getString(R.string.other_cancel),
//                "暂未拥有选品，请移步选品中心进行购买", true, true,
//                new DialogUitl.SimpleCallback2() {
//                    @Override
//                    public void onCancelClick() {
//                        System.exit(0);
//                    }
//
//                    @Override
//                    public void onConfirmClick(Dialog dialog, String content) {
//                        dialog.dismiss();
//                        //LoadDialog.show(HomeActivity.this);
//
//                        showYindao();
//                    }
//                });

    }

    private void showYindao() {
        NewbieGuide.with(HomeActivity.this)
                .setLabel("guide1").setOnGuideChangedListener(new OnGuideChangedListener() {
            @Override
            public void onShowed(Controller controller) {

            }

            @Override
            public void onRemoved(Controller controller) {
                if (mTabbar != null) mTabbar.setSelectTab(4);
            }
        }).alwaysShow(true)//总是显示，调试时可以打开
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(mTabbar.getBottonItem(4))
                        //.setBackgroundColor(getResources().getColor(R.color.color_4D000000))
                        .addHighLight(new RectF(SystemUtils.getScreenWidth(HomeActivity.this) * 4 / 5, SystemUtils.getScreenHeight(this, false) - SystemUtils.dip2px(this, 52), SystemUtils.getScreenWidth(this), SystemUtils.getScreenHeight(this, false)))
                        .setLayoutRes(R.layout.item_home_yindao, R.id.click_btn).setEverywhereCancelable(false))
                .show();
    }

    long doration = 200;

    private void initAnim() {

        animatorMenuBtn = ObjectAnimator.ofFloat(
                homeScreening, "rotation", 0f, 45f);
        animatorMenuBtn.setStartDelay(200);
        animatorMenuBtn.setDuration(doration);

        animatorMenuBtnEnd = ObjectAnimator.ofFloat(
                homeScreening, "rotation", 45f, 0f);

        animatorMenuBtnEnd.setDuration(doration);
        animatorMenuBtnEnd.setStartDelay(200);
        animatorZhezhaoStart = ObjectAnimator.ofFloat(
                zhezhao, "alpha", 0f, 1f);
        animatorZhezhaoStart.setStartDelay(200);
        animatorZhezhaoStart.setDuration(doration);

        animatorLiveStart = ObjectAnimator.ofFloat(
                liveTx, "alpha", 0f, 1f);
        animatorLiveStart.setStartDelay(200);
        animatorLiveStart.setDuration(doration);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                live, "translationX", lastDx, -SystemUtils.dip2px(this, 102) + lastDx);

        ObjectAnimator animatorRotationLive = ObjectAnimator.ofFloat(
                live, "rotation", 0f, 45f, 0f);

        //定义属性动画集合的对象
        animLiveSetStart = new AnimatorSet();
        animLiveSetStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                liveTx.setVisibility(View.VISIBLE);
                zhezhao.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //通过with方法，让两个动画同时进行
        animLiveSetStart.play(animator1).with(animatorRotationLive);
        //设置延迟时间,让菜单内容相继弹出
        animLiveSetStart.setStartDelay(200);
        //然后，设置flag为true，当再次点击的时候，收回菜单
        animLiveSetStart.setDuration(doration);

        animatorVideoStart = ObjectAnimator.ofFloat(
                recordTx, "alpha", 0f, 1f);
        animatorVideoStart.setStartDelay(200);
        animatorVideoStart.setDuration(doration);
        //给出一个沿Y轴移动的动画
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                record, "translationY", lastDy, -SystemUtils.dip2px(this, 124) + lastDy);
//给出一个沿X轴移动的动画
        ObjectAnimator animatorRotationVideo = ObjectAnimator.ofFloat(
                record, "rotation", 0f, 45f, 0f);

        //定义属性动画集合的对象
        animVideoSetStart = new AnimatorSet();
        animVideoSetStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                recordTx.setVisibility(View.VISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //通过with方法，让两个动画同时进行
        animVideoSetStart.play(animator2).with(animatorRotationVideo);
        //设置延迟时间,让菜单内容相继弹出
        animVideoSetStart.setStartDelay(200);
        animVideoSetStart.setDuration(doration);

        animatorZhezhaoEnd = ObjectAnimator.ofFloat(
                zhezhao, "alpha", 1f, 0f);
        animatorZhezhaoEnd.setStartDelay(200);
        animatorZhezhaoEnd.setDuration(doration);


        animatorLiveEnd = ObjectAnimator.ofFloat(
                liveTx, "alpha", 1f, 0f);
        animatorLiveEnd.setStartDelay(200);
        animatorLiveEnd.setDuration(doration);
//给出一个沿X轴移动的动画
        ObjectAnimator animator11 = ObjectAnimator.ofFloat(
                live, "translationX", -SystemUtils.dip2px(this, 102) + lastDx, lastDx);
        ObjectAnimator animatorRotationLiveEnd = ObjectAnimator.ofFloat(
                live, "rotation", 0f, 45f, 0f);

        //定义属性动画集合的对象
        animLiveSetEnd = new AnimatorSet();
        animLiveSetEnd.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                liveTx.setVisibility(View.INVISIBLE);
                zhezhao.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //通过with方法，让两个动画同时进行
        animLiveSetEnd.play(animator11).with(animatorRotationLiveEnd);
        //设置延迟时间,让菜单内容相继弹出
        animLiveSetEnd.setStartDelay(200);
        animLiveSetEnd.setDuration(doration);
        //然后，设置flag为true，当再次点击的时候，收回菜单


        animatorVideoEnd = ObjectAnimator.ofFloat(
                recordTx, "alpha", 1f, 0f);
        animatorVideoEnd.setStartDelay(200);
        animatorVideoEnd.setDuration(doration);
        //给出一个沿Y轴移动的动画
        ObjectAnimator animator21 = ObjectAnimator.ofFloat(
                record, "translationY", -SystemUtils.dip2px(this, 124) + lastDy, lastDy);
//给出一个沿X轴移动的动画
        ObjectAnimator animatorRotationVideoEnd = ObjectAnimator.ofFloat(
                record, "rotation", 0f, 45f, 0f);

        //定义属性动画集合的对象
        animVideoSetEnd = new AnimatorSet();
        animVideoSetEnd.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recordTx.setVisibility(View.INVISIBLE);
                isAniming = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //通过with方法，让两个动画同时进行
        animVideoSetEnd.play(animator21).with(animatorRotationVideoEnd);
        //设置延迟时间,让菜单内容相继弹出
        animVideoSetEnd.setStartDelay(200);
        animVideoSetEnd.setDuration(doration);
    }


    //是否可以打开菜单
    boolean flag = true;
    AnimatorSet animLiveSetEnd;
    AnimatorSet animVideoSetEnd;
    ObjectAnimator animatorLiveEnd;
    ObjectAnimator animatorVideoEnd;
    ObjectAnimator animatorZhezhaoEnd;


    AnimatorSet animLiveSetStart;
    AnimatorSet animVideoSetStart;
    ObjectAnimator animatorLiveStart;
    ObjectAnimator animatorVideoStart;
    ObjectAnimator animatorZhezhaoStart;

    ObjectAnimator animatorMenuBtn;
    ObjectAnimator animatorMenuBtnEnd;
    boolean isAniming = false;

    private void closeAnim() {

        animatorZhezhaoEnd.start();

        animatorMenuBtnEnd.start();
        animatorLiveEnd.start();
        animLiveSetEnd.start();
        //然后，设置flag为true，当再次点击的时候，收回菜单


        animatorVideoEnd.start();
        //给出一个沿Y轴移动的动画
        animVideoSetEnd.start();
        flag = true;


    }

    //两种方法的内容大体相同，只是动画属性的参数相反
    private void startAnim() {

        animatorZhezhaoStart.start();
        animatorMenuBtn.start();
        animatorLiveStart.start();

        animLiveSetStart.start();
        //然后，设置flag为true，当再次点击的时候，收回菜单
        animatorVideoStart.start();
        //给出一个沿Y轴移动的动画
        animVideoSetStart.start();

        flag = false;


    }


    /*private boolean touchResult = false;
    private int mStartX1, mStartY1, mLastX1, mLastY1;
    private int mScreenWidth, mScreenHeight;
    private int subWidth_50;*/
    private void screening() {
        /*mScreenWidth = CommonUtils.getScreenWidth(this);
        mScreenHeight = CommonUtils.getScreenHeight(this);
        subWidth_50 = CommonUtils.dip2px(mContext, 50);*/

        homeScreening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MsgActivity.class);
                startActivity(intent);
//                if (!isAniming) {
//                    if (flag) {
//                        startAnim();  //弹出菜单动画
//                    } else {
//                        closeAnim();  //收回菜单动画
//                    }
//                }


            }
        });
        homeScreening.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchResult = false;

                        mStartX1 = (int) event.getRawX();
                        mStartY1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getRawX() - mStartX1;
                        dy = (int) event.getRawY() - mStartY1;
                        if (flag) {
                            if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
//                                record.setTranslationX(dx + lastDx);
//                                live.setTranslationX(dx + lastDx);
                                v.setTranslationX(dx + lastDx);
//                                record.setTranslationY(dy + lastDy);
//                                live.setTranslationY(dy + lastDy);
                                v.setTranslationY(dy + lastDy);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (Math.abs(endX - mStartX1) > 5 || Math.abs(endY - mStartY1) > 5) {
                            //防止点击的时候稍微有点移动点击事件被拦截了
                            lastDx = lastDx + dx;
                            lastDy = lastDy + dy;
                            touchResult = true;
                        } else {
                            initAnim();
                        }
                        if (lastDx != 0) {
                            lastDx = 0;
                            v.setTranslationX(lastDx);
                        }
                        break;
                }
                return touchResult;
            }
        });

        homeTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialog.show(HomeActivity.this);
                loadUserConfig();
            }
        });
        homeTarget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchResultTarget = false;
                        mStartX1Target = (int) event.getRawX();
                        mStartY1Target = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dxTarget = (int) event.getRawX() - mStartX1Target;
                        dyTarget = (int) event.getRawY() - mStartY1Target;

                        if (Math.abs(dxTarget) > 5 || Math.abs(dyTarget) > 5) {
//                                record.setTranslationX(dx + lastDx);
//                                live.setTranslationX(dx + lastDx);
                            v.setTranslationX(dxTarget + lastDxTarget);
//                                record.setTranslationY(dy + lastDy);
//                                live.setTranslationY(dy + lastDy);
                            v.setTranslationY(dyTarget + lastDyTarget);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (Math.abs(endX - mStartX1Target) > 5 || Math.abs(endY - mStartY1Target) > 5) {
                            //防止点击的时候稍微有点移动点击事件被拦截了
                            lastDxTarget = lastDxTarget + dxTarget;
                            lastDyTarget = lastDyTarget + dyTarget;
                            touchResultTarget = true;
                        } else {
                            initAnim();
                        }
                        if (lastDxTarget != 0) {
                            lastDxTarget = 0;
                            v.setTranslationX(lastDxTarget);
                        }
                        break;
                }
                return touchResultTarget;
            }
        });
    }

    int dx, dy, lastDx, lastDy;
    boolean touchResult = true;
    int mStartX1, mCurrentX1, mCurrentY1, mStartY1;
    int mScreenWidth;
    int mScreenHeight;
    int subWidth_50;
    boolean touchResultTarget = true;
    int mStartX1Target, mStartY1Target;
    int dxTarget, dyTarget, lastDxTarget, lastDyTarget;

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            if (!isLogin()) {
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
            if (!isLogin()) {
                return;
            }
            startActivity(new Intent(mContext, VideoRecordActivity.class));
            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_live:
                mMainStartChooseCallback.onLiveClick();
                break;
            case R.id.btn_video:
                mMainStartChooseCallback.onVideoClick();
                break;
        }
    }


    public void loadData() {
        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
            }

            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) return;
                initTargetRewardDatas(result);
            }
        });
    }

    public void checkPrize() {
        if (data == null) {
            LoadDialog.show(HomeActivity.this);
            OKHttpUtils.getInstance().getRequest("app/prize/getPrizeInfo", "", new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            LoadDialog.dismiss(HomeActivity.this);
                            NToast.shortToast(HomeActivity.this, errInfo);
                        }

                        @Override
                        public void onSuccess(String result) {
                            LoadDialog.dismiss(HomeActivity.this);
                            try {
                                UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                                if (!data.can_selectt_prize) {
                                    NToast.shortToast(HomeActivity.this, data.hint2);
                                } else {
                                    ChoujiangFragment fragmentRotary = new ChoujiangFragment();
                                    fragmentRotary.setHomeActivity(HomeActivity.this);
                                    fragmentRotary.show(getSupportFragmentManager(), "ChoujiangFragment");
                                }

                            } catch (Exception e) {

                            }

                        }
                    });
                }

                @Override
                public void onSuccess(String result) {
                    LoadDialog.dismiss(HomeActivity.this);
                    if (TextUtils.isEmpty(result)) return;
                    initTargetRewardDatas(result);
                    startActivity(new Intent(HomeActivity.this, TargetRewardActivity.class));
                }
            });

        } else {
            startActivity(new Intent(HomeActivity.this, TargetRewardActivity.class));
        }
    }

    public void loadUserConfig() {

        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(HomeActivity.this);
                NToast.shortToast(HomeActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(HomeActivity.this);
                try {
                    if (TextUtils.isEmpty(result)) return;
                    UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                    if (data.home_activitys != null && data.home_activitys.size() > 0) {
                        DialogHomeActivitiesFragment dialogHomeActivitiesFragment = new DialogHomeActivitiesFragment();
                        dialogHomeActivitiesFragment.setCards(data.home_activitys);
                        dialogHomeActivitiesFragment.setHome(HomeActivity.this);
                        dialogHomeActivitiesFragment.show(getSupportFragmentManager(), "DialogHomeActivitiesFragment");
                    } else {
                        NToast.shortToast(HomeActivity.this, "暂无活动");
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    float count;
    float targetProgress;
    boolean isrecive = true;
    String tartgetReward;
    public TargetRewardResponse data;

    private void initTargetRewardDatas(String result) {
//
//        if (mIndex != 0 && homeTarget.getVisibility() == View.GONE) {
//            homeTarget.setVisibility(View.VISIBLE);
//        }
        if (homeTarget.getVisibility() != View.VISIBLE) {
            if (mIndex != POS_4)
                homeTarget.setVisibility(View.VISIBLE);
        }
        if (mIndex == POS_4 && mFragment5 != null && mFragment5 instanceof MyFragmentNewThree) {
            ((MyFragmentNewThree) mFragment5).getUserInfoData();
            homeTarget.setVisibility(View.VISIBLE);
        }
        data = new Gson().fromJson(result, TargetRewardResponse.class);

        tartgetReward = data.target_reward;
        count = data.target_reward_count;
        targetProgress = data.target_reward_progress;
        isrecive = data.target_reward_is_get;
        //Glide.with(this).load(CommonUtils.getUrl(data.target_reward_icon)).into(target_logo);
        // target_msg.setText(data.target_reward_value);
//        if (targetProgress >= 100) {
//            target_msg.setText(R.string.target_has_complete);
//        } else
//            target_msg.setText(getString(R.string.renwu_progress) + (data.target_reward_progress + "%"));
//        npb.setProgress(targetProgress);
//        rewardName.setText(data.target_reward);
//        Glide.with(this).load(data.target_reward_icon).into(rewardIcon);
//        teamActivityNum.setText(NumUtil.clearZero(data.team_activity_num));
//        needActivity.setText(getString(R.string.target_bottom_pao, NumUtil.clearZero(data.target_reward_need_activity_num)));
//        chaju.setText(getString(R.string.chaju, NumUtil.subtractNum(data.target_reward_need_activity_num, data.team_activity_num + "").contains("-")?"0":NumUtil.subtractNum(data.target_reward_need_activity_num, data.team_activity_num + "")));
//        if (TextUtils.isEmpty(tartgetReward)) {
//            targetRewardLl.setVisibility(View.VISIBLE);
//        }
//        reciveBtn.setEnabled(targetProgress >= 100 && !isrecive);

        // rewardDes.setText(String.format(Locale.CHINA, "%s%d%s", targetProgress == 100 ? getString(R.string.target_complete) : getString(R.string.target_ing), count, tartgetReward));


    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onMessage(String content) {
        if (TextUtils.isEmpty(content)) {
        }
//        try {
//            JSONObject jso = null;
//            jso = new JSONObject(content);
//            String code = jso.getString("code");
//            if (code.equals("target_msg")) {
//                JSONObject data = jso.getJSONObject("data");
//
//                targetProgress = data.getInt("target_reward_progress");
//
//                if (targetProgress >= 100) {
//                    target_msg.setText(R.string.target_has_complete);
//                } else
//                    target_msg.setText(getString(R.string.renwu_progress) + (targetProgress + "%"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }
}
