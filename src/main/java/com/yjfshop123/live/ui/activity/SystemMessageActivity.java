package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.ui.adapter.SystemAdapter;

import android.support.v7.widget.RecyclerView;

import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class SystemMessageActivity extends BaseActivity {

    private RecyclerView shimmerRecycler;
    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private SystemAdapter systemAdapter;

    private String mi_platformId;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_system_message);
        setHeadVisibility(View.GONE);

        mi_platformId = UserInfoUtil.getMiPlatformId();
        RealmConverUtils.conversationId = "system_notice" + mi_platformId;

        findViewById(R.id.system_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.system_dele_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUitl.showSimpleHintDialog(SystemMessageActivity.this, getString(R.string.prompt), getString(R.string.ac_select_friend_sure),
                        getString(R.string.cancel), getString(R.string.del_3), true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();

                                RealmMessageUtils.deleteAllSystemMsg(mi_platformId);
                                RealmConverUtils.deleteSystemConverMsg(mi_platformId);

                                if (mDatas != null && mDatas.size() > 0) {
                                    mDatas.clear();
                                }
                                systemAdapter.setCards(mDatas);
                                systemAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });

        mSwipeRefresh = findViewById(R.id.system_swipe_refresh);
        shimmerRecycler = findViewById(R.id.system_shimmer_recycler_view);

//        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        systemAdapter = new SystemAdapter();
        shimmerRecycler.setAdapter(systemAdapter);
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadMessages();
                    }
                }
            }
        });
        initSwipeRefresh();

        loadMessages();
    }

    boolean isLoadingMore = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealmConverUtils.conversationId = null;
    }

    private List<MessageDB> mDatas = new ArrayList<>();

    private int page = 1;
    private int pageSize = 50;

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadMessages();
                }
            });
        }
    }

    private RealmResults<MessageDB> query;

    private void loadMessages() {
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(MessageDB.class).equalTo("conversationId", "system_notice" + mi_platformId).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<MessageDB>>() {
            @Override
            public void onChange(RealmResults<MessageDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                //("id", Sort.DESCENDING);降序

                finishRefresh();

                if (mDatas.size() > 0) {
                    mDatas.clear();
                }

                List<MessageDB> list = mRealm.copyFromRealm(element);

                int startIndex = 0;
                int endIndex = 0;
                int size = list.size();
                int queryCount = page * pageSize;
                if (size <= queryCount) {
                    endIndex = size;
                } else endIndex = queryCount;

                for (int i = startIndex; i < endIndex; i++) {
                    if (i == 0) {
                        list.get(i).setIsShowTime(1);
                    } else {
                        long timestamp_ = list.get(i - 1).getTimestamp();
                        long timestamp = list.get(i).getTimestamp();
                        if ((timestamp - timestamp_) > 180) {
                            list.get(i).setIsShowTime(1);
                        } else {
                            list.get(i).setIsShowTime(0);
                        }
                    }
                    mDatas.add(list.get(i));
                }

                systemAdapter.setCards(mDatas);
                systemAdapter.notifyDataSetChanged();

//                if (page == 1) {
//                    shimmerRecycler.scrollToPosition(mDatas.size() - 1);
//                }
                isLoadingMore=false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
