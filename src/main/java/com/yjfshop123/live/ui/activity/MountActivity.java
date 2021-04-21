package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MountListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.MountAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MountActivity extends BaseActivityForNewUi implements OnItemClickListener, EmptyLayout.OnRetryListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    private MountAdapter adapter;
    private GridLayoutManager mGridLayoutManager;
    private int width;
    private int mMountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();

        mountList();
    }

    private void initView() {
        width = (screenWidth_ - CommonUtils.dip2px(mContext, 24)) / 2;

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText("坐骑");
        initSwipeRefresh();

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        adapter = new MountAdapter(mContext, this, width);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);
        shimmerRecycler.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Object bean, int position) {
        MountListResponse.ListBean liveMountBean = (MountListResponse.ListBean) bean;
        mMountId = liveMountBean.getId();
        if (liveMountBean.getStatus() == 0){
            //去购买
            DialogUitl.showSimpleHintDialog(mContext,
                    getString(R.string.prompt),
                    getString(R.string.other_ok),
                    getString(R.string.other_cancel),
                    "购买坐骑" + liveMountBean.getMount_name() + "？",
                    true, true,
                    new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {

                        }
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            buyMount();
                        }
                    });
        }else {
            if (liveMountBean.getIs_choosed() == 0){
                //去使用
                DialogUitl.showSimpleHintDialog(mContext,
                        getString(R.string.prompt),
                        getString(R.string.other_ok),
                        getString(R.string.other_cancel),
                        "使用坐骑" + liveMountBean.getMount_name() + "？",
                        true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                useMount();
                            }
                        });
            }else {
                //取消使用
                DialogUitl.showSimpleHintDialog(mContext,
                        getString(R.string.prompt),
                        getString(R.string.other_ok),
                        getString(R.string.other_cancel),
                        "取消已使用坐骑" + liveMountBean.getMount_name() + "？",
                        true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                cancelMount();
                            }
                        });
            }
        }
    }

    private void cancelMount(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mount_id", String.valueOf(mMountId))
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/mount/cancelMount", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                mountList();
            }
        });
    }

    private void useMount(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mount_id", String.valueOf(mMountId))
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/mount/useMount", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                mountList();
            }
        });
    }

    private void buyMount(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mount_id", String.valueOf(mMountId))
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/mount/buyMount", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                mountList();
            }
        });
    }

    private void mountList(){
        OKHttpUtils.getInstance().getRequest("app/mount/mountList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                showNotData();
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    MountListResponse response = JsonMananger.jsonToBean(result, MountListResponse.class);
                    if (mList.size() > 0){
                        mList.clear();
                    }
                    mList.addAll(response.getList());
                    adapter.setCards(mList);
                    adapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    showNotData();
                }
            }
        });
    }
    private List<MountListResponse.ListBean> mList = new ArrayList<>();

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
                    mountList();
                }
            });
        }
    }
}
