package com.yjfshop123.live.video.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.video.adapter.FansAdapter;
import com.yjfshop123.live.video.bean.FansResponse;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FansActivity extends BaseActivity implements OnItemClickListener, EmptyLayout.OnRetryListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    private FansAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String mUserID;
    private int type = 0;//0关注 1粉丝

    private int page = 1;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        mContext = this;
        ButterKnife.bind(this);
        setHeadLayout();

        mUserID = getIntent().getStringExtra("USER_ID");
        type = getIntent().getIntExtra("TYPE", 0);

        initView();
        mountList();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        if (type == 0){
            tv_title_center.setText("关注");
        }else {
            tv_title_center.setText("粉丝");
        }
        initSwipeRefresh();

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new FansAdapter(mContext, this);
        adapter.setType(type);
        shimmerRecycler.setAdapter(adapter);

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (type != -1) {
                    int lastVisibleItem ;
                    int totalItemCount;
                    lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    totalItemCount = mLinearLayoutManager.getItemCount();

                    //表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true;
                            page++;
                            mountList();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(Object bean, int position) {
        String type = (String) bean;
        if (type.equals("FOLLOW")){
            focus(position);
        }else {
            ActivityUtils.startUserHome(mContext, String.valueOf(mList.get(position).getUser_id()));
        }
    }

    private TextView mFollow;

    private void focus(final int position){
        FansAdapter.Vh viewHolder = (FansAdapter.Vh) shimmerRecycler.findViewHolderForAdapterPosition(position);
        mFollow = viewHolder.mFollow;
        int isFollow = mList.get(position).getIs_follow();
        final int user_id = mList.get(position).getUser_id();
        if(type==0){
            //如果是关注列表
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", user_id)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                    guanzhuTongzhi.isGuanzhu = 0;
                    guanzhuTongzhi.user_id = user_id;
                    EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                    mList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });

            return;
        }

        if (isFollow > 0) {
            //取消关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", user_id)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                    guanzhuTongzhi.isGuanzhu = 0;
                    guanzhuTongzhi.user_id =user_id;
                    EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                    mList.get(position).setIs_follow(0);
                    mFollow.setText("关注");
                    mFollow.setBackgroundResource(R.drawable.icon_focus_1);
                    mFollow.setTextColor(getResources().getColor(R.color.white));
                }
            });
        } else {
            //关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", user_id)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    mList.get(position).setIs_follow(1);
                    mFollow.setText("取消关注");
                    GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                    guanzhuTongzhi.isGuanzhu = 0;
                    guanzhuTongzhi.user_id = user_id;
                    EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                    mFollow.setBackgroundResource(R.drawable.icon_focus_2);
                    mFollow.setTextColor(getResources().getColor(R.color.color_999999));
                    NToast.shortToast(mContext, "已关注成功");
                }
            });
        }
    }

    private void mountList(){
        String url;
        if (type == 0){
            url = "app/user/getMyConcerned";
        }else {
            url = "app/user/getConcernedUser";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", mUserID)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (page == 1){
                    showNotData();
                }else {
                    page--;
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    FansResponse response = JsonMananger.jsonToBean(result, FansResponse.class);

                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    adapter.setCards(mList);
                    adapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1){
                        showNotData();
                    }
                }
            }
        });
    }
    private List<FansResponse.ListBean> mList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNetError() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    @Override
    public void onRetry() {
        page = 1;
        mountList();
    }

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
                    mountList();
                }
            });
        }
    }
}
