package com.yjfshop123.live.video.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.adapter.VideoAdapter;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 短视频
 */
public class SmallVideoFragment extends BaseFragment implements OnItemClickListener {

    @BindView(R.id.shimmer_recycler_view_video)
    RecyclerView shimmerRecycler;

    @BindView(R.id.f_video_t)
    View f_video_t;
    @BindView(R.id.f_video_t2)
    FrameLayout f_video_t2;

    private VideoAdapter videoAdapter;
    private GridLayoutManager mGridLayoutManager;

    private int width;

    private int page = 1;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        width = (CommonUtils.getScreenWidth(getActivity()) - CommonUtils.dip2px(mContext, 24)) / 2;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initAction() {
        f_video_t2.setVisibility(View.GONE);
        f_video_t.setVisibility(View.GONE);

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        videoAdapter = new VideoAdapter(width, this);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);

        shimmerRecycler.setAdapter(videoAdapter);
//        shimmerRecycler.showShimmerAdapter();
//        shimmerRecycler.hideShimmerAdapter();
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (pageSize > page) {
                int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mGridLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if(!isLoadingMore){
                        isLoadingMore = true;
                        page ++ ;
                        videoList();
                    }
                }
//                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        videoList();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }

        page = 1;
        videoList();
    }

    private void videoList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortVideo/getPopularVideoList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                if (result == null){
                    return;
                }
                try {
                    VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);
                    isLoadingMore = false;
                    if (page == 1){
                        if (mList.size() > 0){
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    videoAdapter.setCards(mList);

                    videoAdapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1){
                        showNotData();
                    }
                }
            }
        });
    }

    private List<VideoDynamicResponse.ListBean> mList = new ArrayList<>();

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
    public void onItemClick(Object bean, int position) {
        if (!isLogin()){
            return;
        }

        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getPopularVideoList");
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }
}
