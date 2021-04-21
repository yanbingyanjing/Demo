package com.yjfshop123.live.ui.activity.income;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.InComeResponseResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.IncomeAdapter;
import com.yjfshop123.live.ui.fragment.BaseFragment;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IncomeFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.income)
    TextView income;
    @BindView(R.id.total_income)
    TextView totalIncome;
    private IncomeAdapter xunZhangAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    int type = 1;
    int page = 1;

    public void setType(int type) {
        this.type = type;
    }

    boolean isLoadingMore = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_income;
    }

    String dateStr;
    String endDateStr;

    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new IncomeAdapter(mContext);
        shimmerRecycler.setAdapter(xunZhangAdapter);
        showLoading();
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

    public void setDate(String dateStr, String endDateStr) {
        this.dateStr = dateStr;
        this.endDateStr = endDateStr;
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        page = 1;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("start_date", dateStr)

                    .put("end_date", endDateStr)
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }
        if (!isFromRefresh) {
            showLoading();
        }

        OKHttpUtils.getInstance().getRequest("app/user/getIncomeList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMore = false;
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                }
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                isLoadingMore = false;
                //模拟数据
                // loadData(testData);
                loadData(result);
            }
        });

    }

    private void loadData(String result) {
        hideLoading();
        InComeResponseResponse responseResponse = null;
        try {
            responseResponse = JsonMananger.jsonToBean(result, InComeResponseResponse.class);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        if (responseResponse == null) return;
        if (page == 1) {
            income.setText(responseResponse.getTotal() + "");
        }
        list = responseResponse.getList();
        xunZhangAdapter.setCards(list);
    }

    private void loadMoreData(String result) {
        hideLoading();
        InComeResponseResponse responseResponse = null;
        try {
            responseResponse = JsonMananger.jsonToBean(result, InComeResponseResponse.class);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        if (responseResponse == null || responseResponse.getList() == null || responseResponse.getList().size() == 0) {
            page--;
            return;
        }

        list.addAll(responseResponse.getList());
        xunZhangAdapter.notifyDataSetChanged();
    }

    List<InComeResponseResponse.ListBean> list;

    /**
     * 获取数据
     */
    public void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("start_date", dateStr)
                    .put("end_date", endDateStr)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/user/getIncomeList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //模拟数据
                page--;
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                loadMoreData(result);
                isLoadingMore = false;
                //模拟数据
                //loadMoreData(testData);

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
