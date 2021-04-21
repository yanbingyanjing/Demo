package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MessageListResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.CommunityMessageActivity;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivity;
import com.yjfshop123.live.ui.activity.CommunityReplyListActivity;
import com.yjfshop123.live.ui.adapter.CMessageAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommunityMessageFragment1 extends BaseFragment {

    //顶:1 回复:2
    private static int type = 1;

    private int page = 1;
    private int pageSize = 1;

    private List<MessageListResponse.ListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private CMessageAdapter cMessageAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private CommunityMessageActivity communityMessageActivity;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof CommunityMessageActivity) {
            communityMessageActivity = (CommunityMessageActivity) getActivity();
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_community_message;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        cMessageAdapter = new CMessageAdapter(mContext, type);
        shimmerRecycler.setAdapter(cMessageAdapter);
    }

    @Override
    protected void initEvent() {
        cMessageAdapter.setOnItemClickListener(new CMessageAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 类型 11:社区动态的点赞 12:社区回复的点赞 31:社区动态的回复 32:社区回复的回复
                int type_ = mList.get(position).getType();
                if (type_ == 11 || type_ == 31){
                    if (!TextUtils.isEmpty(mList.get(position).getVideo_cover())){
                        //视频动态
                        if (ActivityUtils.IS_VIDEO()){
                            ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getObject_id()), "app/shortVideo/getPopularVideoList");
                        }else {
                            ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(mList.get(position).getObject_id()), "app/forum/videoDynamic");
                        }
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }else {
                        //普通动态
                        Intent intent = new Intent(getActivity(), CommunityReplyDetailActivity.class);
                        intent.putExtra("dynamic_id", mList.get(position).getObject_id());
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }
                }else if (type_ == 12 || type_ == 32){
                    Intent intent = new Intent(mContext, CommunityReplyListActivity.class);
                    intent.putExtra("replyId", mList.get(position).getObject_id());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }

            @Override
            public void onPortraitClick(View view, int position) {
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(position).getBy_user_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (pageSize > page) {

                    int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = mLinearLayoutManager.getItemCount();

                    //表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true;
                            page++;
                            likeMessageList();
                        }
                    }
                }
            }
        });
    }

    public void loadData(){
        page = 1;
        likeMessageList();
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        showLoading();
        likeMessageList();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }else {
            communityMessageActivity.clearMessage_L();
        }

        page = 1;
        likeMessageList();
    }

    private void likeMessageList(){
        String body = "";
        String url;
        if (ActivityUtils.IS_VIDEO()){
            url = "app/shortVideo/likeMessageList";
        }else {
            url = "app/forum/likeMessageList";
        }
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    MessageListResponse response = JsonMananger.jsonToBean(result, MessageListResponse.class);
                    pageSize = response.getTotal();
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    cMessageAdapter.setCards(mList);

                    cMessageAdapter.notifyDataSetChanged();

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
}
