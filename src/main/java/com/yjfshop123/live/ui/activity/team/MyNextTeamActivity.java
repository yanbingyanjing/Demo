package com.yjfshop123.live.ui.activity.team;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.model.MyTeamMemberResponse;
import com.yjfshop123.live.model.MyTeamResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.adapter.MyTeamAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyNextTeamActivity extends BaseActivityForNewUi {

    @BindView(R.id.path)
    TextView path;
    @BindView(R.id.my_promotion)
    TextView myPromotion;
    @BindView(R.id.team_people_count)
    TextView teamPeopleCount;
    @BindView(R.id.team_activity_num)
    TextView teamActivityNum;

    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    String mobile;
    String pathString;
    String user_name;
    @BindView(R.id.team_max_medaling)
    TextView teamMaxMedaling;
    @BindView(R.id.tuijian)
    TextView tuijian;
    @BindView(R.id.mm_details_controls_add)
    TextView mm_details_controls_add;
    @BindView(R.id.hero_activity)
    TextView heroActivity;
    @BindView(R.id.league_activity)
    TextView leagueActivity;
    @BindView(R.id.up)
    ImageView up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_next_team);
        ButterKnife.bind(this);
        setCenterTitleText("他的分店");

        mHeadRightText.setText(getString(R.string.city_partner));
        if (getIntent() != null) {
            mobile = getIntent().getStringExtra("mobile");
            pathString = getIntent().getStringExtra("path");
            user_name = getIntent().getStringExtra("user_name");
        }
        if (TextUtils.isEmpty(mobile)) {
            finish();
            return;
        }
        setCenterTitleText(user_name);
        pathString = pathString + ">" + user_name;
        path.setText(pathString);

        initView();
        initSwipeRefresh();
        loadData();
        loadMemberData(false);
    }

    private LinearLayoutManager mLinearLayoutManager;
    boolean isLoadingMore = false;
    MyTeamAdapter xunZhangAdapter;

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new MyTeamAdapter(mContext);
        shimmerRecycler.setAdapter(xunZhangAdapter);
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
                        loadMemberMoreData();
                    }
                }
            }
        });

    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                    loadMemberData(true);
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }


    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    loadMemberData(false);
                }
            });
        }
        finishRefresh();
    }

    public void showNoNetData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    loadMemberData(false);
                }
            });
        }
        finishRefresh();
    }

    private void loadData() {
        page = 1;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mobile", mobile)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/team/myTeam", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    int page = 1;

    private void loadMemberData(boolean ifRefresh) {
        if (!ifRefresh)
            showLoading();
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mobile", mobile)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/team/teamMembers", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }
                // TODO 测试数据
//                 hideLoading();
//                  initTeamMemberDatas(MyTeamMemberResponse.testData);
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                initTeamMemberDatas(result);
            }
        });
    }

    private void loadMemberMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mobile", mobile)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/team/teamMembers", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                initTeamMemberMoreDatas(result);
            }
        });
    }

    List<MyTeamMemberResponse.MyTeamMemberData> list;

    private void initTeamMemberDatas(String result) {

        if (TextUtils.isEmpty(result)) return;
        MyTeamMemberResponse myTeamMemberResponse = new Gson().fromJson(result, MyTeamMemberResponse.class);
        if (myTeamMemberResponse == null) return;
        list = myTeamMemberResponse.list;
        if (list != null && list.size() == 0) {
            showNotData();
        }
        xunZhangAdapter.setCards(list, pathString);
    }

    private void initTeamMemberMoreDatas(String result) {

        if (TextUtils.isEmpty(result)) return;
        MyTeamMemberResponse myTeamMemberResponse = new Gson().fromJson(result, MyTeamMemberResponse.class);
        if (myTeamMemberResponse.list == null || myTeamMemberResponse.list.size() == 0) {
            page--;
            return;
        }
        list.addAll(myTeamMemberResponse.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    MyTeamResponse myEggResponse;

    private void initDatas(String result) {
        finishRefresh();

        if (TextUtils.isEmpty(result)) return;
        myEggResponse = new Gson().fromJson(result, MyTeamResponse.class);
        if (myEggResponse == null) return;
        teamMaxMedaling.setText(myEggResponse.team_max_medaling);
        myPromotion.setText("(" + myEggResponse.promotion_num + ")");
        teamPeopleCount.setText("分店总人数：" + myEggResponse.team_total_num);
        teamActivityNum.setText(myEggResponse.team_activity_num);
        tuijian.setText(myEggResponse.invite_user_name);
        heroActivity.setText(NumUtil.clearZero(myEggResponse.hero_activity));
        leagueActivity.setText(NumUtil.clearZero(myEggResponse.league_activity));
        if (new BigDecimal(TextUtils.isEmpty(myEggResponse.hero_activity)?"0":myEggResponse.hero_activity).compareTo(new BigDecimal(TextUtils.isEmpty(myEggResponse.league_activity)?"0":myEggResponse.league_activity)) > 0) {
            Glide.with(this).load(R.mipmap.shangsheng).into(up);
            up.setVisibility(View.VISIBLE);
        }else  up.setVisibility(View.GONE);
        if (myEggResponse.is_follow > 0) {
            mm_details_controls_add.setText(getString(R.string.follow));
        } else {
            mm_details_controls_add.setText(getString(R.string.mm_dedails_1));
        }

    }

    @OnClick(R.id.mm_details_controls_add)
    public void onViewClicked() {
        if (myEggResponse == null) return;
        if (myEggResponse.is_follow > 0) {
            //取消关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", myEggResponse.invite_user_id)
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
                    guanzhuTongzhi.user_id = Integer.valueOf(myEggResponse.invite_user_id);
                    EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                    myEggResponse.is_follow = 0;
                    mm_details_controls_add.setText(getString(R.string.mm_dedails_1));
                }
            });
        } else {
            //关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", myEggResponse.invite_user_id)
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
                    myEggResponse.is_follow = 1;
                    GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                    guanzhuTongzhi.isGuanzhu = 1;
                    guanzhuTongzhi.user_id = Integer.valueOf(myEggResponse.invite_user_id);
                    EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                    mm_details_controls_add.setText(getString(R.string.follow));
                    NToast.shortToast(mContext, "已关注成功");
                }
            });
        }

    }
}
