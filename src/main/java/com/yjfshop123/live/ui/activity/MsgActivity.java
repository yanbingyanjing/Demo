package com.yjfshop123.live.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.umeng.socialize.UMShareAPI;
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
import com.yjfshop123.live.ui.fragment.ConversationFragment;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.simple.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgActivity extends BaseActivityH implements

        BadgeDismissListener

        , ConversationView {

    @BindView(R.id.home_container_fl)
    FrameLayout homeContainerFl;
    private SparseArray<String> mSparseTags = new SparseArray<>();
    private JPTabBar mTabbar;
    private String mi_platformId;
    private int info_complete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        mi_platformId = UserInfoUtil.getMiPlatformId();
        info_complete = UserInfoUtil.getInfoComplete();
        setContentView(R.layout.activity_msg);
        ButterKnife.bind(this);


        systemMessage = new SystemMessage();
        if (!TextUtils.isEmpty(mi_platformId) && info_complete != 0) {
            queryConverMsg();
            new ConversationPresenter(this);

          //  SocketUtil.getInstance().isLL(true).connect();
        }


       // initListener();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        ConversationFragment mFragment4 = new ConversationFragment();
        fragmentTransaction.add(R.id.home_container_fl, mFragment4);

        fragmentTransaction.show(mFragment4);
        fragmentTransaction.commit();

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

        super.onDestroy();
        destroy();
    }

    private void destroy() {
        RealmConverUtils.cancel();
        RealmMessageUtils.cancel();
        if (query != null) {
            query.removeAllChangeListeners();
        }
       // SocketUtil.getInstance().close();
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


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}
