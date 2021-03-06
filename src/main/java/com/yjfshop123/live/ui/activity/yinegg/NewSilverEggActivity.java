package com.yjfshop123.live.ui.activity.yinegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.NewSilverEggResponse;
import com.yjfshop123.live.model.UnlockSilverEggResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.EggOrderListActivity;
import com.yjfshop123.live.ui.activity.SilverEggActivity;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_SILVER;

public class NewSilverEggActivity extends BaseActivityH {

    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.text_right)
    TextView textRight;
    private LinearLayoutManager mLinearLayoutManager;
    private NewSilverEggAdapter searchAdapter;

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_new_silver_egg);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //?????????????????????????????????
        params.height = SystemUtils.getStatusBarHeight(this);//?????????????????????????????????
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);

        ButterKnife.bind(this);
        StatusBarUtil.StatusBarLightMode(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(mLinearLayoutManager);
        searchAdapter = new NewSilverEggAdapter(mContext);
        searchAdapter.setExchangeOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeEgg();
            }
        });
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(NewSilverEggActivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType",EGG_SILVER);
                startActivity(intent3);
            }
        });
        list.setAdapter(searchAdapter);

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager.getItemCount();

                //????????????4???item?????????????????????????????????
                // dy>0 ??????????????????
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        getMoreData();
                    }
                }
            }
        });
        initSwipeRefresh();
        loadData();
        loadOrderData();

    }

    private void exchangeEgg() {
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/silver_egg/exchangeGoldEgg", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                LoadDialog.dismiss(NewSilverEggActivity.this);
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(NewSilverEggActivity.this);
                NToast.shortToast(mContext, "????????????");
                loadData();
            }
        });
    }

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/silver_egg/finance", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                //  NToast.shortToast(mContext, getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    NewSilverEggResponse newSilverEggResponse;

    private void initDatas(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) return;
        newSilverEggResponse = new Gson().fromJson(result, NewSilverEggResponse.class);
        if (newSilverEggResponse == null) return;

        searchAdapter.setHeadData(newSilverEggResponse);

    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                    loadOrderData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void loadOrderData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/silver_egg/unlockDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, getString(R.string.get_data_fail));
                // loadData(UnlockSilverEggResponse.testData);
            }

            @Override
            public void onSuccess(String result) {
                loadData(result);
            }
        });
    }

    private void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/silver_egg/unlockDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                loadMoreData(result);
            }
        });
    }

    List<UnlockSilverEggResponse.Data> orderData;

    public void loadData(String result) {

        if (TextUtils.isEmpty(result)) return;
        UnlockSilverEggResponse data = new Gson().fromJson(result, UnlockSilverEggResponse.class);
        if (data.list == null) return;
        if (data.list.size() > 0) {
            page = 1;
        }
        this.orderData = data.list;
        searchAdapter.setListData(this.orderData);

    }

    public void loadMoreData(String result) {
        UnlockSilverEggResponse data = new Gson().fromJson(result, UnlockSilverEggResponse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }

        this.orderData.addAll(data.list);
        searchAdapter.notifyDataSetChanged();
    }

    int page = 1;
    public boolean isLoadingMore = false;

    @OnClick(R.id.btn_left)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
        }
    }
}
