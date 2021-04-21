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
import com.yjfshop123.live.net.response.MySendPostResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.OptionsPopupDialog;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivity;
import com.yjfshop123.live.ui.adapter.MySendPostAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 我的帖子
 */
public class MySendPostFragment extends BaseFragment implements MySendPostAdapter.ClickItemListener{

    private ArrayList<MySendPostResponse.ListBean> mList = new ArrayList<>();

    private int page = 1;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private LinearLayoutManager mLinearLayoutManager;

    private boolean isLoadingMore = false;
    private MySendPostAdapter mySendPostAdapter;

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

        mySendPostAdapter = new MySendPostAdapter(getContext(), mList);
        shimmerRecycler.setAdapter(mySendPostAdapter);
        mySendPostAdapter.setClickItemListener(this);
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        dynamicList();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        dynamicList();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }

        page = 1;
        dynamicList();
    }

    private void deleteDynamic(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", mList.get(selPosition).getDynamic_id())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/myDynamicDelete", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(getActivity(), "删除成功");
                mList.remove(selPosition);
                mySendPostAdapter.notifyDataSetChanged();

                if (mList.size() == 0) {
                    showNotData();
                }
            }
        });
    }

    private void dynamicList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/myDynamicList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    hideLoading();
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    MySendPostResponse response = JsonMananger.jsonToBean(result, MySendPostResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    mySendPostAdapter.notifyDataSetChanged();

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

        String[] items = new String[]{getString(R.string.del_1), getString(R.string.cancel)};
        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                switch (which){
                    case 0:
                        deleteDynamic();
                        break;
                }
            }
        }).show();
    }

    @Override
    public void clickItem(View view, int position) {
        MySendPostResponse.ListBean bean = mList.get(position);

        if (bean.getDynamic_is_video() == 0) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CommunityReplyDetailActivity.class);
            intent.putExtra("dynamic_id", bean.getDynamic_id());
            getActivity().startActivity(intent);
        } else {
            ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(bean.getDynamic_id()), "app/forum/videoDynamic");
        }
        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }
}
