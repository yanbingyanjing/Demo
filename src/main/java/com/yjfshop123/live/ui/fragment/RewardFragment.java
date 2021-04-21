package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MyRewardResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.RewardActivity;
import com.yjfshop123.live.ui.adapter.RewarddAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RewardFragment extends BaseFragment {

//     * 0 邀请奖励排行榜
//     * 1 邀请人数排行榜
//     * 2 我的徒弟奖励数据
//     * 3 我的徒孙奖励数据
    private int type = 0;
    private int page = 1;

    private List<MyRewardResponse.ListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private RewarddAdapter rewarddAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private RewardActivity rewardActivity;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof RewardActivity) {
            rewardActivity = (RewardActivity) getActivity();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("TYPE", 0);
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_community_1;
    }

    @Override
    protected void initAction() {

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        rewarddAdapter = new RewarddAdapter(mContext, type);
        shimmerRecycler.setAdapter(rewarddAdapter);
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        getMyReward();
                    }
                }
            }
        });
    }

    public void refresh() {
        page = 1;
        getMyReward();
    }

    @Override
    protected void initData() {
        super.initData();
        refresh();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            refresh();
            showLoading();
        }
    }

    private void getMyReward(){
        String url;
        if (type == 0){
            url = "app/promotion/getInviteBonusRanking";//邀请奖励排行榜
        }else if (type == 1){
            url = "app/promotion/getInviteUserRanking";//邀请人数排行榜
        }else if (type == 2){
            url = "app/promotion/getMyLevel1Invite";//我的徒弟奖励数据
        }else {
            url = "app/promotion/getMyLevel2Invite";//我的徒孙奖励数据
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    MyRewardResponse response = JsonMananger.jsonToBean(result, MyRewardResponse.class);
                    isLoadingMore = false;
                    if (page == 1 && mList.size() > 0) {
                        mList.clear();
                    }

                    mList.addAll(response.getList());
                    rewarddAdapter.setCards(mList);

                    rewarddAdapter.notifyDataSetChanged();

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

}
