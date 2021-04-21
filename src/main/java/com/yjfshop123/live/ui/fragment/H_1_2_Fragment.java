package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.GlideImageLoader;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.VicinityUserResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.adapter.RecommendedAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.TypeUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AccordionTransformer;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class H_1_2_Fragment extends BaseFragment implements OnBannerListener {

    //大图 1;中图 2;小图 3
    private static int type = 2;

    private int page = 1;

    private List<VicinityUserResponse.ListBean> mList = new ArrayList<>();
    private List<VicinityUserResponse.BannerBean> mBanners = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private View bannerView;

    private RecommendedAdapter recommendedAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    private int width;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        type = TypeUtil.getFrament2Type();
        if (bannerHeight == 0) {
            bannerHeight = (getScreenWidth() * 260) / 750;
        }
    }

    private int bannerHeight = 0;
    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_h_1_1_;
    }

    @Override
    protected void initAction() {
        if (type == 1) {
            width = CommonUtils.getScreenWidth(getActivity()) - CommonUtils.dip2px(mContext, 16);
        } else if (type == 2) {
            width = (CommonUtils.getScreenWidth(getActivity()) - CommonUtils.dip2px(mContext, 24)) / 2;
        } else {
            width = 0;
        }

        bannerView = View.inflate(mContext.getApplicationContext(), R.layout.home_header, null);

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, type));
        if (type == 1 || type == 3) {
            mLinearLayoutManager = new LinearLayoutManager(mContext);
            shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        } else if (type == 2) {
            mGridLayoutManager = new GridLayoutManager(mContext, 2);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == 0 ? mGridLayoutManager.getSpanCount() : 1;
                }
            });
            shimmerRecycler.setLayoutManager(mGridLayoutManager);
        }

        recommendedAdapter = new RecommendedAdapter(mContext, width, bannerView, type);
        shimmerRecycler.setAdapter(recommendedAdapter);
//        shimmerRecycler.showShimmerAdapter();
//        shimmerRecycler.hideShimmerAdapter();
    }

    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
    }

    private boolean isLogin(){
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
    }

    @Override
    protected void initEvent() {
        recommendedAdapter.setOnItemClickListener(new RecommendedAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (!isLogin()){
                    return;
                }

                if (postion < 1) {
                    return;
                }
                postion = postion - 1;
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(postion).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (type != -1) {
                    int lastVisibleItem = 0;
                    int totalItemCount = 0;
                    if (type == 1 || type == 3) {
                        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                        totalItemCount = mLinearLayoutManager.getItemCount();
                    } else if (type == 2) {
                        lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
                        totalItemCount = mGridLayoutManager.getItemCount();
                    }

                    //表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true;
                            page++;
                            popularUser();                            }
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        showLoading();
        popularUser();
    }

    private void banner() {
        try {
            if (mBanners.size() > 0) {
                bannerView.setVisibility(View.VISIBLE);
                Banner mBanner = bannerView.findViewById(R.id.home_banner);
                ViewGroup.LayoutParams params = mBanner.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = bannerHeight;
                mBanner.setLayoutParams(params);

                List<String> images = new ArrayList<>();
                for (int i = 0; i < mBanners.size(); i++) {
                    images.add(mBanners.get(i).getImg());
                }

                mBanner.setImages(images)
//                .setBannerTitles(App.titles)//标题
                        .setBannerAnimation(AccordionTransformer.class)//动画
                        .setIndicatorGravity(BannerConfig.CENTER)//指示器位置
                        .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)//指示器样式
                        .setDelayTime(3000)//延迟4S
                        .setImageLoader(new GlideImageLoader())
                        .setOnBannerListener(this)
                        .start();
            } else {
                bannerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            bannerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }

        page = 1;
        popularUser();
    }

    @Override
    public void OnBannerClick(int position) {
        String link = mBanners.get(position).getLink();
        if (!TextUtils.isEmpty(link)) {
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getContext().getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
    }

    private void popularUser(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/index/popularUser", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (page == 1) {
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    VicinityUserResponse response = JsonMananger.jsonToBean(result, VicinityUserResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        type = response.getShow_style();
                        TypeUtil.setFrament2Type(type);
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                        if (mBanners.size() > 0) {
                            mBanners.clear();
                        }
                        mBanners.addAll(response.getBanner());
                        banner();
                    }

                    mList.addAll(response.getList());
                    recommendedAdapter.setCards(mList);

                    recommendedAdapter.notifyDataSetChanged();

                    if (mList.size() == 0) {
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1) {
                        showNotData();
                    }
                }
            }
        });
    }

}
