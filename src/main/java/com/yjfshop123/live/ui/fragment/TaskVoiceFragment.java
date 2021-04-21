package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.utils.ClickUtil;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.TaskLobbyActivity;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TaskVoiceFragment extends BaseFragment{

    //聊天类型（1:文字,2:语音,3:视频）
    private static int type = 2;

    private int pageSize = 1;
    private int page = 1;

    private List<ChatTaskListResponse.ListBean> mList = new ArrayList<>();

    @BindView(R.id.task_shimmer_recycler_view)
    RecyclerView shimmerRecycler;
//    @BindView(R.id.task_swipe_refresh)
//    VerticalSwipeRefreshLayout mSwipeRefresh;

    private TaskAdapter taskAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private TaskLobbyActivity taskLobbyActivity;

    private boolean isLoadingMore = false;

    private String mi_tencentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TaskLobbyActivity){
            taskLobbyActivity = (TaskLobbyActivity) getActivity();
        }

        mi_tencentId = UserInfoUtil.getMiTencentId();
        EventBus.getDefault().register(this);

    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        taskAdapter = new TaskAdapter(mContext, mi_tencentId);
        shimmerRecycler.setAdapter(taskAdapter);
//        shimmerRecycler.showShimmerAdapter();
    }

    @Override
    protected void initEvent() {
        taskAdapter.setOnItemClickListener(new TaskAdapter.MyItemClickListener() {
            @Override
            public void onItemClickP(View view, int position) {
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(position).getProm_custom_uid()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }

            @Override
            public void onItemClickC(View view, int position) {
                String prom_custom_uid = String.valueOf(mList.get(position).getProm_custom_uid());
                if (mi_tencentId.equals(prom_custom_uid)){
                    /*PromptPopupDialog.newInstance(mContext,
                            getString(R.string.prompt),
                            getString(R.string.task_hint),
                            getString(R.string.i_know))
                            .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                                @Override
                                public void onPositiveButtonClicked() {
                                    //
                                }
                            }).show();*/

                    task_id = mList.get(position).getTask_id();
                    cancelChatTask();
                    return;
                }
            }

            @Override
            public void onItemClickQ(View view, int position) {
                if (!canClick()){
                    return;
                }

                taskLobbyActivity.request_(mList.get(position), 1);
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (pageSize > page) {
                    int lastVisibleItem;
                    int totalItemCount;
                    lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    totalItemCount = mLinearLayoutManager.getItemCount();

                    //表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                        if(!isLoadingMore){
                            isLoadingMore = true;
                            page ++ ;
                            chatTaskList();
                        }
                    }
                }
            }
        });
    }

    private boolean canClick() {
        return ClickUtil.canClick();
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        chatTaskList();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }
        page = 1;
        chatTaskList();
    }

    private void cancelChatTask(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("task_id", task_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/cancelChatTask", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                page = 1;
                chatTaskList();
            }
        });
    }

    private int task_id;

    private void chatTaskList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/chatTaskList", body, new RequestCallback() {
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
                    ChatTaskListResponse response = JsonMananger.jsonToBean(result, ChatTaskListResponse.class);
                    pageSize = response.getTotal_page();
                    isLoadingMore = false;
                    finishRefresh();
                    if (page == 1 && mList.size() > 0){
                        mList.clear();
                    }

                    mList.addAll(response.getList());
                    taskAdapter.setCards(mList);
                    taskAdapter.notifyDataSetChanged();

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

    @Subscriber(tag = Config.EVENT_START)
    public void onEventMainThread(String type) {
        try {
            //type 2:语音 3:视频
            if (type.equals("2")){
                page = 1;
                chatTaskList();
            }
        }catch (Exception e){}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}