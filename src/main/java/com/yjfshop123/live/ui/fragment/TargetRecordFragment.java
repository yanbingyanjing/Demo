package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.model.TargetRecordResponse;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.ui.adapter.TargetRecordAdapter;
import com.yjfshop123.live.ui.adapter.XunZhangAdapter;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TargetRecordFragment extends BaseFragment {
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;
    private TargetRecordAdapter xunZhangAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);

    }

    protected int setContentViewById() {
        return R.layout.fragment_target_record;
    }


    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new TargetRecordAdapter(mContext);
        shimmerRecycler.setAdapter(xunZhangAdapter);
        getData(false);
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
                        isLoadingMore = true;
                        page++;
                        getMoreData();
                    }
                }
            }
        });
    }

    public int page = 1;
    public int type = 1;//1已领取  2未领取

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        page = 1;
        if (isFromRefresh) {
        } else {
            showLoading();
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeRecord", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }

                //模拟数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                //模拟数据
                loadData(result);

            }
        });

    }

    /**
     * 获取数据
     */
    public void getMoreData() {


        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeRecord", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                //模拟数据
                loadMoreData(result);

            }
        });

    }

    private boolean isLoadingMore = false;
    public List<TargetRewardResponse> list;

    private void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) {
            showNotData();
            return;
        }
        TargetRecordResponse data = new Gson().fromJson(result, TargetRecordResponse.class);
        if (data.list == null) {

            showNotData();
            return;
        }
        if (data.list.size() > 0) {
            page = 1;
        } else {
            showNotData();
        }
        this.list = data.list;
        xunZhangAdapter.setCards(this.list);

    }

    private void loadMoreData(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        TargetRecordResponse data = new Gson().fromJson(result, TargetRecordResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        if (data.list.size() > 0) {
            this.list.addAll(data.list);
        }
        xunZhangAdapter.notifyDataSetChanged();
    }


    @Override
    protected void updateViews(boolean isRefresh) {
        getData(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
