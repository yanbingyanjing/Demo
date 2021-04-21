package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.CommunityAdapter;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MMFragment3 extends BaseFragment {

    private int page = 1;

    private List<PopularDynamicResponse.ListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    @BindView(R.id.mm3_hint)
    TextView mm3_hint;

    private CommunityAdapter communityAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private int width;
    private int width_2;
    private int height_video;
    private boolean isLoadingMore = false;

    private String user_id;

    public void setResponse(String user_id){
        this.user_id = user_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_mm3;
    }

    @Override
    protected void initAction() {
        int screenWidth = CommonUtils.getScreenWidth(getActivity());
        width = (screenWidth - CommonUtils.dip2px(mContext, 32)) / 3;
        width_2 = (screenWidth - CommonUtils.dip2px(mContext, 26)) / 2;
        height_video = screenWidth;

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setFocusableInTouchMode(false);

        communityAdapter = new CommunityAdapter(mContext, width, width_2, height_video);
        shimmerRecycler.setAdapter(communityAdapter);
    }

    @Override
    protected void initEvent() {
        communityAdapter.setOnItemClickListener(new CommunityAdapter.MyItemClickListener() {
            @Override
            public void onContentClick(View view, int position) {
                //类型（1:视频 2:图片）
                if (mList.get(position).getVideo_list().size() > 0){

                    ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(mList.get(position).getDynamic_id()), "app/forum/videoDynamic");
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

                }else {

                    Intent intent = new Intent(getActivity(), CommunityReplyDetailActivity.class);
                    intent.putExtra("dynamic_id", mList.get(position).getDynamic_id());
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

                }
            }

            @Override
            public void onPortraitClick(View view, int position) {

            }

            @Override
            public void onPraiseClick(View view, int position) {
                if (mList.get(position).getIs_like() == 0){
                    //点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicDoLike(mList.get(position).getDynamic_id(), false);

                    mList.get(position).setIs_like(1);
                    mList.get(position).setLike_num(mList.get(position).getLike_num() + 1);
                }else {
                    //取消点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicUndoLike(mList.get(position).getDynamic_id(), false);

                    mList.get(position).setIs_like(0);
                    mList.get(position).setLike_num(mList.get(position).getLike_num() - 1);
                }

                communityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onImgClick(View view, int postion, int index) {

                List<String> images = new ArrayList<>();
                for (int i = 0; i < mList.get(postion).getPicture_list().size(); i++) {
                    images.add(CommonUtils.getUrl(mList.get(postion).getPicture_list().get(i).getObject()));
                }

                try {
                    Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                    intent.putExtra(Config.POSITION, index);
                    intent.putExtra("Picture", JsonMananger.beanToJson(images));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        dynamicListByUid();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        dynamicListByUid();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        page = 1;
        dynamicListByUid();
    }

    private void dynamicListByUid(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .put("user_id", user_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/dynamicListByUid", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1){
                    showHint();;
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try {
                    PopularDynamicResponse response = JsonMananger.jsonToBean(result, PopularDynamicResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    communityAdapter.setCards(mList);
                    communityAdapter.notifyDataSetChanged();

                    hideHint();

                    if (mList.size() == 0){
                        showHint();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1){
                        showHint();
                    }
                }
            }
        });
    }

    private void showHint(){
        mm3_hint.setVisibility(View.VISIBLE);
        shimmerRecycler.setVisibility(View.GONE);
    }

    private void hideHint(){
        mm3_hint.setVisibility(View.GONE);
        shimmerRecycler.setVisibility(View.VISIBLE);
    }
}
