package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.GuanZhuResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.GZAdapter;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GZActivity extends BaseActivity {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.authImg)
    CircleImageView authImg;
    @BindView(R.id.lookTotal)
    TextView lookTotal;
    @BindView(R.id.dataGrid)
    LinearLayout dataGrid;
    @BindView(R.id.noDataLay)
    RelativeLayout noDataLay;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    private boolean isLoadingMore = false;
    private int page = 1;

    private GridLayoutManager gridLayoutManager;

    private GZAdapter adapter;
    private ArrayList<GuanZhuResponse.ListBean> items = new ArrayList<>();
    private int style = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_lookme_grid);
        style = getIntent().getIntExtra("TYPE", 0);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        if (style == 2){
            tv_title_center.setText(R.string.meguanzhu_jilu);
        }else if (style == 1){
            tv_title_center.setText(R.string.guanzhu_jilu);
        }else if (style == 0){
            tv_title_center.setText(R.string.lookme_jilu);
        }
        gridLayoutManager = new GridLayoutManager(this, 3);
        shimmerRecycler.setLayoutManager(gridLayoutManager);

        adapter = new GZAdapter(this, items);
        shimmerRecycler.setAdapter(adapter);

        String avatar = UserInfoUtil.getAvatar();
        Glide.with(this).load(avatar).into(authImg);

        initSwipeRefresh();

        adapter.setOnItemClickListener(new GZAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                ActivityUtils.startUserHome(mContext, String.valueOf(items.get(postion).getUser_id()));
            }
        });

        getData();
    }

    public void finishRefresh() {
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
                    getData();
                }
            });
        }

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        getData();
                    }
                }
            }
        });
    }

    private void getData(){
        String url;
        if (style == 2){
            url = "app/user/getMyConcerned";//关注我的用户列表
        }else if (style == 1){
            url = "app/user/getConcernedUser";//我关注的用户列表
        }else if (style == 0){
            url = "app/user/getLookMe";//查看过我的用户-列表
        }else {
            return;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1){
                    dataGrid.setVisibility(View.GONE);
                    noDataLay.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                isLoadingMore = false;

                try {
                    GuanZhuResponse recordListResponse = JsonMananger.jsonToBean(result, GuanZhuResponse.class);
                    if (style == 2){
                        lookTotal.setText(getString(R.string.meguanzhu_total) + recordListResponse.getTotal() + getString(R.string.meguanzhu_total1));
                    }else if (style == 1){
                        lookTotal.setText(recordListResponse.getTotal() + getString(R.string.guanzhume_total));
                        tv_title_center.setText(R.string.guanzhu_jilu);
                    }else if (style == 0){
                        lookTotal.setText(recordListResponse.getTotal() + getString(R.string.lookme_total));
                        tv_title_center.setText(R.string.lookme_jilu);
                    }

                    if (page == 1){
                        items.clear();
                    }
                    items.addAll(recordListResponse.getList());
                    adapter.notifyDataSetChanged();

                    if (items.size() == 0) {
                        dataGrid.setVisibility(View.GONE);
                        noDataLay.setVisibility(View.VISIBLE);
                    } else {
                        dataGrid.setVisibility(View.VISIBLE);
                        noDataLay.setVisibility(View.GONE);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}