package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yjfshop123.live.live.response.ManagerListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import android.support.v7.widget.RecyclerView;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ManagerListFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener {

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private RecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private TextView dialog_manager_list_hint, dialog_manager_list_count;

    private List<ManagerListResponse.ListBean> mList = new ArrayList<>();

    private ManagerAdapter managerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_manager_list;
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

        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);

        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh);
        shimmerRecycler = mRootView.findViewById(R.id.shimmer_recycler_view);
        dialog_manager_list_hint = mRootView.findViewById(R.id.dialog_manager_list_hint);
        dialog_manager_list_count = mRootView.findViewById(R.id.dialog_manager_list_count);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        shimmerRecycler.setLayoutManager(linearLayoutManager);
        managerAdapter = new ManagerAdapter(mContext, this);
        shimmerRecycler.setAdapter(managerAdapter);

        initSwipeRefresh();
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

        //直播间管理员列表
        OKHttpUtils.getInstance().getRequest("app/live/getManagerList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                notData();
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try{
                    ManagerListResponse managerListResponse = JsonMananger.jsonToBean(result, ManagerListResponse.class);
                    if (mList.size() > 0) {
                        mList.clear();
                    }

                    mList.addAll(managerListResponse.getList());

                    managerAdapter.setCards(mList);
                    managerAdapter.notifyDataSetChanged();
                    dialog_manager_list_count.setText("当前管理员(" + mList.size() + "/" + managerListResponse.getExtra().getMax_manager() + ")");
                    if (mList.size() == 0){
                        notData();
                    }
                }catch (Exception e){
                    notData();
                }
            }
        });
    }

    private void notData(){
        finishRefresh();
        dialog_manager_list_hint.setVisibility(View.VISIBLE);
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
        //直播间删除管理员
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("manager_uid", String.valueOf(mList.get(position).getUser_id()))//管理员uid
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/delManager", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "取消管理成功");
                loadData();
            }
        });
    }
}