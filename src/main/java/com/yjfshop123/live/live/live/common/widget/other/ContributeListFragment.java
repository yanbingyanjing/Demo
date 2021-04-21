package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.PayoutRankingResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ContributeListFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener {

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private RecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private TextView dialog_contribute_list_hint;

    private List<PayoutRankingResponse.ListBean> mList = new ArrayList<>();
    private ContributeAdapter contributeAdapter;
    private boolean isLoadingMore = false;
    private int page = 1;
    private int type = 1;//1:周榜 2:月榜 3:总榜 4:日榜
    private String live_id;

    private View dialog_contribute_weeks_v;
    private View dialog_contribute_month_v;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_contribute_list;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = CommonUtils.dip2px(mContext, 360);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        live_id = bundle.getString("live_id");

        mRootView.findViewById(R.id.dialog_contribute_weeks).setOnClickListener(this);
        mRootView.findViewById(R.id.dialog_contribute_month).setOnClickListener(this);
        dialog_contribute_weeks_v = mRootView.findViewById(R.id.dialog_contribute_weeks_v);
        dialog_contribute_month_v = mRootView.findViewById(R.id.dialog_contribute_month_v);

        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh);
        shimmerRecycler = mRootView.findViewById(R.id.shimmer_recycler_view);
        dialog_contribute_list_hint = mRootView.findViewById(R.id.dialog_contribute_list_hint);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        shimmerRecycler.setLayoutManager(linearLayoutManager);
        contributeAdapter = new ContributeAdapter(mContext, this);
        shimmerRecycler.setAdapter(contributeAdapter);

        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = linearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadData();
                    }
                }
            }
        });

        initSwipeRefresh();
        type = 1;
        page = 1;
        loadData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.dialog_contribute_weeks:
                if (type == 1){
                    return;
                }
                dialog_contribute_weeks_v.setVisibility(View.VISIBLE);
                dialog_contribute_month_v.setVisibility(View.INVISIBLE);
                //周
                type = 1;
                page = 1;
                loadData();
                break;
            case R.id.dialog_contribute_month:
                if (type == 2){
                    return;
                }
                dialog_contribute_weeks_v.setVisibility(View.INVISIBLE);
                dialog_contribute_month_v.setVisibility(View.VISIBLE);
                //月
                type = 2;
                page = 1;
                loadData();
                break;
        }
    }

    private void loadData() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            notData();
            return;
        }

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", live_id)
                    .put("type", type)//1:周榜 2:月榜 3:总榜 4:日榜
                    .put("page", page)//页码，默认第一页
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/payoutRanking4Living", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    notData();
                }else {
                    dialog_contribute_list_hint.setVisibility(View.GONE);
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try{
                    PayoutRankingResponse managerListResponse = JsonMananger.jsonToBean(result, PayoutRankingResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(managerListResponse.getList());

                    contributeAdapter.setCards(mList);
                    contributeAdapter.notifyDataSetChanged();
                    if (mList.size() == 0){
                        notData();
                    }else {
                        dialog_contribute_list_hint.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    if (page == 1){
                        notData();
                    }else {
                        dialog_contribute_list_hint.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void notData(){
        finishRefresh();
        dialog_contribute_list_hint.setVisibility(View.VISIBLE);
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
                    loadData();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(Object bean, int position) {
        //去主页
        ActivityUtils.startUserHome(mContext, String.valueOf(mList.get(position).getUser_id()));
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }
}
