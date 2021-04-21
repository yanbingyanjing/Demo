package com.yjfshop123.live.live.live.common.widget.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.ui.adapter.ConversationAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import android.support.v7.widget.RecyclerView;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatListViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ActionListener mActionListener;
    private RecyclerView shimmerRecycler;
    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLinearLayoutManager;
    private ConversationAdapter conversationAdapter;

    private String mLiveUid;//主播的uid

    public ChatListViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    @Override
    protected void processArguments(Object... args) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_list;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_back).setOnClickListener(this);
        shimmerRecycler = (RecyclerView) findViewById(R.id.conversation_recycler_view);
        mSwipeRefresh = (VerticalSwipeRefreshLayout) findViewById(R.id.conversation_swipe_refresh);

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        conversationAdapter = new ConversationAdapter(null);
        shimmerRecycler.setAdapter(conversationAdapter);

        initSwipeRefresh();
        initEvent();
        loadData();
    }

    private void initEvent(){
        conversationAdapter.setOnItemClickListener(new ConversationAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                mActionListener.onItemClick(mList.get(postion));
            }
        });

        conversationAdapter.setOnLongClickListener(new ConversationAdapter.MyLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {

            }
        });
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
                    loadData();
                    finishRefresh();
                }
            });
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        mActionListener = null;
        if (query != null) {
            query.removeAllChangeListeners();
        }
    }

    private RealmResults<IMConversationDB> query;
    private List<IMConversationDB> mList = new ArrayList<>();

    public void loadData() {
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).equalTo("userIMId", mLiveUid).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 2) {
                        datas.remove(i);
                    }
                }

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 5) {
                        datas.remove(i);
                    }
                }
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 6) {
                        datas.remove(i);
                    }
                }

                if (mList.size() > 0) {
                    mList.clear();
                }
                mList.addAll(datas);
                dataSetChanged();
            }
        });
    }

    private void dataSetChanged(){
        conversationAdapter.setCards(mList);
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            if (mActionListener != null) {
                mActionListener.onCloseClick();
            }
        }
    }

    public interface ActionListener {
        void onCloseClick();

        void onItemClick(IMConversationDB mIMConversationDB);
    }

}