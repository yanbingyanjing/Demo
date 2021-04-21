package com.yjfshop123.live.video.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.PushUtil;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.server.widget.OptionsPopupDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.CommunityMessageActivity;
import com.yjfshop123.live.ui.activity.SearchActivity;
import com.yjfshop123.live.ui.activity.SystemMessageActivity;
import com.yjfshop123.live.ui.adapter.ConversationAdapter;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.activity.FansActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;

public class VideoConversationFragment extends BaseFragment implements /*ConversationView*/View.OnClickListener {


    private List<IMConversationDB> mConversations;
    private SystemMessage mSystemMessage;

    @BindView(R.id.conversation_recycler_view)
    RecyclerView shimmerRecycler;

    @BindView(R.id.conversation_swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.conversation_dele)
    ImageView mDele;

    @BindView(R.id.h_mag_search)
    ImageView mSearch;

    private LinearLayoutManager mLinearLayoutManager;
    private ConversationAdapter conversationAdapter;

    private BaseActivityH activityH;

    private View systemHeader;

    private String mi_platformId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BaseActivityH){
            activityH = (BaseActivityH) getActivity();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_video_conversation;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        systemHeader = View.inflate(mContext.getApplicationContext(), R.layout.video_system_header, null);
        conversationAdapter = new ConversationAdapter(systemHeader);
        shimmerRecycler.setAdapter(conversationAdapter);

        initSwipeRefresh();

        mConversations = activityH.getData();
        mSystemMessage = activityH.getSystemMessage();
        EventModel2 eventModel2 = activityH.getEventModel2();
        if (eventModel2 != null) {
            readCount_like = eventModel2.getReadCount_like();
            readCount_reply = eventModel2.getReadCount_reply();
        }
        dataSetChanged();

        loadSystemMessage();
        /*queryConverMsg();
        new ConversationPresenter(this);*/
    }

    @Override
    protected void initEvent() {
        conversationAdapter.setOnItemClickListener(new ConversationAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (postion < 1) {
                    return;
                }
                postion = postion - 1;

                Intent intent;
                IMConversation imConversation = new IMConversation();
                intent = new Intent(mContext, MessageListActivity.class);

                imConversation.setUserName(UserInfoUtil.getName());
                imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                imConversation.setType(mConversations.get(postion).getType());
                imConversation.setUserIMId(mConversations.get(postion).getUserIMId());
                imConversation.setOtherPartyIMId(mConversations.get(postion).getOtherPartyIMId());
                imConversation.setUserId(mConversations.get(postion).getUserId());
                imConversation.setOtherPartyId(mConversations.get(postion).getOtherPartyId());

                imConversation.setOtherPartyName(mConversations.get(postion).getOtherPartyName());
                imConversation.setOtherPartyAvatar(mConversations.get(postion).getOtherPartyAvatar());
                imConversation.setConversationId(mConversations.get(postion).getConversationId());
                intent.putExtra("IMConversation", imConversation);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

                mConversations.get(postion).setUnreadCount(0);
                dataSetChanged();

                RealmConverUtils.clerRedCount_(mConversations.get(postion).getConversationId());
            }
        });

        conversationAdapter.setOnLongClickListener(new ConversationAdapter.MyLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {
                if (postion < 1) {
                    return;
                }
                final int pos = postion - 1;

                String[] items = new String[]{getString(R.string.de_item_delete_friend) + "会话", getString(R.string.cancel)};
                OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                    @Override
                    public void onOptionsItemClicked(int which) {
                        switch (which){
                            case 0:
                                String conversationId = mConversations.get(pos).getConversationId();
                                RealmConverUtils.deleteConverMsg(conversationId);
                                RealmMessageUtils.deleteCoverMessageMsg(conversationId);
                                break;
                        }
                    }
                }).show();
            }
        });

        mDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt),getString(R.string.ac_select_friend_sure),
                        getString(R.string.cancel), getString(R.string.clear_the_message), true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();

                                RealmMessageUtils.deleteAllMessageMsg(mi_platformId);
                                RealmConverUtils.deleteAllConverMsg(mi_platformId);

                                if (mConversations != null && mConversations.size() > 0){
                                    mConversations.clear();
                                }

                                dataSetChanged();
                            }
                        });
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        systemHeader.findViewById(R.id.sys_ll_1).setOnClickListener(this);
        systemHeader.findViewById(R.id.sys_ll_2).setOnClickListener(this);
        systemHeader.findViewById(R.id.sys_ll_3).setOnClickListener(this);
        systemHeader.findViewById(R.id.sys_ll_4).setOnClickListener(this);
    }

    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    finishRefresh();
                }
            });
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    public void updateMessage(IMConversationDB imConversationDB) {
        if (imConversationDB != null){
            if (mConversations.size() > 0){
                int pos = -1;
                for (int i = 0; i < mConversations.size(); i++) {
                    if (imConversationDB.getConversationId().equals(mConversations.get(i).getConversationId())){
                        pos = i;
                        continue;
                    }
                }
                if (pos == -1){
                    mConversations.add(imConversationDB);
                }else{
                    mConversations.get(pos).setAvatar(imConversationDB.getAvatar());
                    mConversations.get(pos).setLastMessage(imConversationDB.getLastMessage());
                    mConversations.get(pos).setTimestamp(imConversationDB.getTimestamp());
                    mConversations.get(pos).setName(imConversationDB.getName());
                    long unreadCount = mConversations.get(pos).getUnreadCount();
                    unreadCount ++;
                    mConversations.get(pos).setUnreadCount(unreadCount);
                }
            }else {
                mConversations.add(imConversationDB);
            }

            //当前消息置顶

            conversationAdapter.setCards(mConversations);
            shimmerRecycler.getActualAdapter().notifyDataSetChanged();
        }
    }*/

    /*private RealmResults<IMConversationDB> query;

    private void queryConverMsg(){
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);
                if (mConversations.size() > 0){
                    mConversations.clear();
                }
                mConversations.addAll(datas);
                conversationAdapter.setCards(mConversations);
                shimmerRecycler.getActualAdapter().notifyDataSetChanged();


                long readCount = 0;
                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                homeActivity.mTabbar.showBadge(3, (int)readCount, true);
            }
        });
    }*/

    @Override
    public void onResume() {
        super.onResume();
        mi_platformId = UserInfoUtil.getMiPlatformId();
        PushUtil.getInstance().reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (query != null){
            query.removeChangeListeners();;
        }*/

        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void onEventMainThread(List<IMConversationDB> mList) {
        try {
            if (mList != null){
                mConversations = mList;

                dataSetChanged();
            }
        }catch (Exception e){}
    }

    @Subscriber(tag = Config.EVENT_START)
    public void onEventMainThread(SystemMessage systemMessage) {
        mSystemMessage = systemMessage;
        loadSystemMessage();
    }

    private long readCount_like = 0;
    private long readCount_reply = 0;

    @Subscriber(tag = Config.EVENT_SHEQU)
    public void onEventMainThread(EventModel2 eventModel2) {
        if (eventModel2.getType().equals("forum_notice")) {
            readCount_like = eventModel2.getReadCount_like();
            readCount_reply = eventModel2.getReadCount_reply();
            loadSystemMessage();
        }
    }

    private void loadSystemMessage(){
        TextView c_it_unread_num2 = systemHeader.findViewById(R.id.sys_unread_num_4);
        TextView c_it_unread_num3 = systemHeader.findViewById(R.id.sys_unread_num_2);
        TextView c_it_unread_num4 = systemHeader.findViewById(R.id.sys_unread_num_3);

        if (!TextUtils.isEmpty(mSystemMessage.getConversationId())){
            long unRead = mSystemMessage.getUnreadCount();
            if (unRead <= 0){
                c_it_unread_num2.setVisibility(View.INVISIBLE);
            }else{
                c_it_unread_num2.setVisibility(View.VISIBLE);
                String unReadStr = String.valueOf(unRead);
                if (unRead < 10){
                    c_it_unread_num2.setBackgroundResource(R.drawable.point1);
                }else{
                    c_it_unread_num2.setBackgroundResource(R.drawable.point2);
                    if (unRead > 99){
                        unReadStr = "99+";
                    }
                }
                c_it_unread_num2.setText(unReadStr);
            }
        }

        long unRead3 = readCount_like;
        if (unRead3 <= 0){
            c_it_unread_num3.setVisibility(View.INVISIBLE);
        }else{
            c_it_unread_num3.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead3);
            if (unRead3 < 10){
                c_it_unread_num3.setBackgroundResource(R.drawable.point1);
            }else{
                c_it_unread_num3.setBackgroundResource(R.drawable.point2);
                if (unRead3 > 99){
                    unReadStr = "99+";
                }
            }
            c_it_unread_num3.setText(unReadStr);
        }

        long unRead4 = readCount_reply;
        if (unRead4 <= 0){
            c_it_unread_num4.setVisibility(View.INVISIBLE);
        }else{
            c_it_unread_num4.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead4);
            if (unRead4 < 10){
                c_it_unread_num4.setBackgroundResource(R.drawable.point1);
            }else{
                c_it_unread_num4.setBackgroundResource(R.drawable.point2);
                if (unRead4 > 99){
                    unReadStr = "99+";
                }
            }
            c_it_unread_num4.setText(unReadStr);
        }
    }

    private void dataSetChanged(){
        conversationAdapter.setCards(mConversations);
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.sys_ll_1:
                startActivity(new Intent(mContext, FansActivity.class).putExtra("TYPE", 1).putExtra("USER_ID", mi_platformId));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            case R.id.sys_ll_2:
                intent = new Intent(mContext, CommunityMessageActivity.class);
                intent.putExtra("readCount_like", readCount_like);
                intent.putExtra("readCount_reply", readCount_reply);
                intent.putExtra("current_item", 0);
                break;
            case R.id.sys_ll_3:
                intent = new Intent(mContext, CommunityMessageActivity.class);
                intent.putExtra("readCount_like", readCount_like);
                intent.putExtra("readCount_reply", readCount_reply);
                intent.putExtra("current_item", 1);
                break;
            case R.id.sys_ll_4:
                intent = new Intent(mContext, SystemMessageActivity.class);
                RealmConverUtils.clerRedCount_("system_notice" + mi_platformId);
                break;
        }
        if (intent != null){
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        }
    }
}
