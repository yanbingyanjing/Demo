package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.WULUResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LiveGuardDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private TextView mGuardNum;
    private View mBottom;
    private TextView mTip;
    private TextView mBtnBuy;
    private GuardAdapter mGuardAdapter;
    private List<WULUResponse.ListBean> mList = new ArrayList<>();

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private RecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout dialog_guard_list_ll;
    private TextView dialog_guard_list_tv;

    private String mLiveUid;//主播ID
    private int mIsAnchor = 1;//1 主播 2 观众 3 回播
    private int guardNum;//守护人数
    private String isGuard;//是否守护

    private boolean isLoadingMore = false;
    private int page = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_guard_list;
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
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = CommonUtils.dip2px(mContext, 280);
        params.height = CommonUtils.dip2px(mContext, 360);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveUid = bundle.getString("liveUid");
        guardNum = bundle.getInt("guardNum", 0);

        //1 主播 2 观众 3 回播
        if (mContext instanceof TCLiveBasePublisherActivity){
            mIsAnchor = 1;
        } else if (mContext instanceof TCLivePlayerActivity){
            mIsAnchor = 2;
        } else {
            mIsAnchor = 3;
        }

        mGuardNum = mRootView.findViewById(R.id.guard_num);
        mBottom = mRootView.findViewById(R.id.bottom);
        if (mIsAnchor == 1 || mIsAnchor == 3) {
            mBottom.setVisibility(View.INVISIBLE);
            mGuardNum.setText(getString(R.string.guard_guard) + "(" + guardNum + ")");
        } else if (mIsAnchor == 2){
            isGuard = bundle.getString("isGuard");

            mTip = mRootView.findViewById(R.id.tip);
            mBtnBuy = mRootView.findViewById(R.id.btn_buy);
            mBtnBuy.setOnClickListener(this);

            mGuardNum.setText(getString(R.string.guard_guard) + "(" + guardNum + ")");
            if (isGuard.equals("1")) {
                mTip.setText(R.string.guard_tip_1);
                mBtnBuy.setText(R.string.guard_buy_3);
            } else {
                mTip.setText(getString(R.string.guard_tip_0));
                mBtnBuy.setText(R.string.guard_buy_2);
            }
        }

        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh);
        shimmerRecycler = mRootView.findViewById(R.id.shimmer_recycler_view);
        dialog_guard_list_ll = mRootView.findViewById(R.id.dialog_guard_list_ll);
        dialog_guard_list_tv = mRootView.findViewById(R.id.dialog_guard_list_tv);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        shimmerRecycler.setLayoutManager(linearLayoutManager);
        mGuardAdapter = new GuardAdapter(mContext);
        shimmerRecycler.setAdapter(mGuardAdapter);

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        dismiss();
        if (v.getId() == R.id.btn_buy){
            if (mContext instanceof  TCLivePlayerActivity){
                ((TCLivePlayerActivity) mContext).openBuyGuardWindow();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    .put("user_id", mLiveUid)//主播用户id
                    .put("page", page)//页码
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/watch/getWatchedUserByLiveUid", body, new RequestCallback() {
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
                    WULUResponse wuluResponse = JsonMananger.jsonToBean(result, WULUResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(wuluResponse.getList());
                    mGuardAdapter.setCards(mList);

                    mGuardAdapter.notifyDataSetChanged();

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

        dialog_guard_list_ll.setVisibility(View.VISIBLE);
        if (mIsAnchor == 1) {
            dialog_guard_list_tv.setText(getString(R.string.guard_no_data_2));
        } else if (mIsAnchor == 2 || mIsAnchor == 3){
            dialog_guard_list_tv.setText(getString(R.string.guard_no_data));
        }
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
}
