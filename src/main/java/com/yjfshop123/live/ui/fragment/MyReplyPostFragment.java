package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MyReplyPostResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.OptionsPopupDialog;
import com.yjfshop123.live.ui.activity.CommunityReplyListActivity;
import com.yjfshop123.live.ui.adapter.MyReplyPostAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 我的回复
 */
public class MyReplyPostFragment extends BaseFragment implements MyReplyPostAdapter.ClickItemListener{

    private MyReplyPostAdapter myReplyPostAdapter;
    private ArrayList<MyReplyPostResponse.ListBean> mList = new ArrayList<>();

    private int page = 1;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private LinearLayoutManager mLinearLayoutManager;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_send_post;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        myReplyPostAdapter = new MyReplyPostAdapter(getContext(), mList);
        shimmerRecycler.setAdapter(myReplyPostAdapter);
        myReplyPostAdapter.setClickItemListener(this);
    }

    @Override
    protected void initEvent() {
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
                        replyList();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        replyList();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }

        page = 1;
        replyList();
    }

    private void replyDelete(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("reply_id", mList.get(selPosition).getReply_id())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/myReplyDelete", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                mList.remove(selPosition);
                myReplyPostAdapter.notifyDataSetChanged();

                if (mList.size() == 0) {
                    showNotData();
                }
            }
        });
    }

    private void replyList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/myReplyList", body, new RequestCallback() {
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
                    MyReplyPostResponse response = JsonMananger.jsonToBean(result, MyReplyPostResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    myReplyPostAdapter.notifyDataSetChanged();

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

    private int selPosition;

    @Override
    public void deleteItem(View view, int position) {
        selPosition = position;

        String[] items = new String[]{getString(R.string.del_2), getString(R.string.cancel)};
        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                switch (which){
                    case 0:
                        replyDelete();
                        break;
                }
            }
        }).show();
    }

    @Override
    public void clickItem(View view, int position) {
        MyReplyPostResponse.ListBean bean = mList.get(position);
        if(bean.getDynamic_is_video()==0) {
            Intent intent=new Intent();
            intent.setClass(getActivity(), CommunityReplyListActivity.class);
            intent.putExtra("replyId", bean.getReply_id());
            intent.putExtra("preview_index", bean.getParent_reply_id());
            intent.putExtra("detail_index", bean.getReply_floor());
            getActivity().startActivity(intent);
        }else{
            ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(bean.getDynamic_id()), "app/forum/videoDynamic");
        }
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    public void clickImage(View view, int position) {
        ActivityUtils.startUserHome(mContext,UserInfoUtil.getMiTencentId());
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }
}
