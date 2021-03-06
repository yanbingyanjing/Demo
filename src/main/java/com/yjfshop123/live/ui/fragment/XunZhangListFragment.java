package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.ui.adapter.XunZhangAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我的选品界面  进行中   选品中  已结束
 */
public class XunZhangListFragment extends BaseFragment {
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;
    @BindView(R.id.nested)
    NestedScrollView nested;
    Unbinder unbinder1;
    private XunZhangAdapter xunZhangAdapter;
    private LinearLayoutManager mLinearLayoutManager;
  public   int page = 1;
    MyXunzhangFragment fragment;
    int index = -1;


    List<MyXunZhangResponse.MyXunZhangData> list = new ArrayList<>();
    private boolean isLoadingMore = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_xunzhang;
    }

    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new XunZhangAdapter(mContext);
        shimmerRecycler.setAdapter(xunZhangAdapter);
        showNotData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (index == 0) {
            if (fragment != null) {
                fragment.getData(false);
            }
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setFragment(MyXunzhangFragment fragment) {
        this.fragment = fragment;
    }

    public void loadData(String result) {

        hideLoading();
        if (TextUtils.isEmpty(result)) {
            nested.setVisibility(View.VISIBLE);
            showNotData();
            return;
        } else nested.setVisibility(View.GONE);
        MyXunZhangResponse data = new Gson().fromJson(result, MyXunZhangResponse.class);
        if (data.list == null) {
            nested.setVisibility(View.VISIBLE);
            showNotData();
            return;
        }
        if (data.list.size() > 0) {
            nested.setVisibility(View.GONE);
            page = 1;
        } else {
            nested.setVisibility(View.VISIBLE);
            showNotData();
        }
        this.list = data.list;
        xunZhangAdapter.setCards(this.list);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void loadMoreData(String result) {
        MyXunZhangResponse data = new Gson().fromJson(result, MyXunZhangResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    public void loadMoreOver() {
        isLoadingMore = false;
    }

    @Override
    protected void initEvent() {
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
                        if (fragment != null) {
                            isLoadingMore = true;
                            page++;
                            fragment.getMoreData(page);
                        }

                    }
                }
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        NLog.d("重试", "获取数据");
        fragment.getData(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
