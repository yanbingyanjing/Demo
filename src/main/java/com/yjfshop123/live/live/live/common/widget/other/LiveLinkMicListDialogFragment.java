package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.LivingUserResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LiveLinkMicListDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener {

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private RecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private TextView dialog_live_pk;

    private List<LivingUserResponse.LiveListBean> mList = new ArrayList<>();
    private boolean isLoadingMore = false;
    private int page = 1;

    private ListPKAdapter listPKAdapter;
    private String mLiveUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_pk;
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
        params.height = CommonUtils.dip2px(mContext, 300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString("liveUid");
        }

        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);

        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh);
        shimmerRecycler = mRootView.findViewById(R.id.shimmer_recycler_view);
        dialog_live_pk = mRootView.findViewById(R.id.dialog_live_pk);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        shimmerRecycler.setLayoutManager(linearLayoutManager);
        listPKAdapter = new ListPKAdapter(mContext, this);
        shimmerRecycler.setAdapter(listPKAdapter);

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
        page = 1;
        loadData();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
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
                    .put("page", page)
                    .put("channel_id", "0")
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/getLivingList4Lianmai", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    notData();
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try{
                    LivingUserResponse launchLiveResponse = JsonMananger.jsonToBean(result, LivingUserResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    for (int i = 0; i < launchLiveResponse.getLive_list().size(); i++) {
                        String user_id = String.valueOf(launchLiveResponse.getLive_list().get(i).getUser_id());
                        if (!user_id.equals(mLiveUid)){
                            mList.add(launchLiveResponse.getLive_list().get(i));
                        }
                    }

                    listPKAdapter.setCards(mList);
                    listPKAdapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        notData();
                    }
                }catch (Exception e){
                    if (page == 1){
                        notData();
                    }
                }
            }
        });
    }

    private void notData(){
        finishRefresh();

        dialog_live_pk.setVisibility(View.VISIBLE);
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
        dismiss();

        ((TCLiveBasePublisherActivity) mContext).linkMicAnchorApply(mList.get(position));

    }
}