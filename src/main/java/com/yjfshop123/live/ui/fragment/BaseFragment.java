package com.yjfshop123.live.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.IBaseView;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment implements
        IBaseView, EmptyLayout.OnRetryListener{

    public View view;

    protected Context mContext;

    @Nullable
    @BindView(R.id.empty_layout)
    public EmptyLayout mEmptyLayout;

    @Nullable
    @BindView(R.id.swipe_refresh)
    protected SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view= inflater.inflate(setContentViewById(), container, false);
        ButterKnife.bind(this, view);

        initAction();

        initSwipeRefresh();

        initEvent();
        initData();


        return view;
    }

    public void addLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){}

    /**
     * 初始化布局文件
     *
     * @return
     */
    protected abstract int setContentViewById();

    /**
     * 初始化各个控件
     */
    protected abstract void initAction();

    /**
     * 初始化各种事件
     */
    protected abstract void initEvent();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    //###
    @Override
    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    @Override
    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null){
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    @Override
    public void showNetError() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    @Override
    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    @Override
    public void showNotGZ() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setEmptyMessage("还没有关注 快去关注吧");
            mEmptyLayout.setRetryListener(this);
        }
        finishRefresh();
    }

    @Override
    public void onRetry() {
        updateViews(false);
    }

    @Override
    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateViews(true);
                }
            });
        }
    }

    protected abstract void updateViews(boolean isRefresh);

}
