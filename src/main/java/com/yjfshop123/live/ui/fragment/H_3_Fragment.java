package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.RobToChatResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.OChatActivity;
import com.yjfshop123.live.ui.adapter.RobToChatAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class H_3_Fragment extends BaseFragment {

    private OChatActivity oChatActivity;

    private LinearLayoutManager mLinearLayoutManager;

    private List<RobToChatResponse.ListBean> mList = new ArrayList<>();
    private RobToChatAdapter robToChatAdapter;


    private int page = 1;
    private int pageSize = 1;

    private boolean isLoadingMore = false;

    @BindView(R.id.ql_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.ql_swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.ql_hint_ll)
    LinearLayout mQlHint;
    @BindView(R.id.ql_qd_switch)
    ImageView mSwitchIv;
    @BindView(R.id.ql_back)
    ImageView ql_back;

    private int vperiodTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OChatActivity){
            oChatActivity = (OChatActivity) getActivity();
        }
        vperiodTime = Const.vperiod_time;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_h_3_;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        robToChatAdapter = new RobToChatAdapter();
        shimmerRecycler.setAdapter(robToChatAdapter);
        initSwipeRefresh();
    }

    private void finishData(){
//        if (TextUtils.isEmpty(state) || state.equals("OPEN")){
            page = 1;
            getNewHeterosexuUsers();
//        }else{
//            finishRefresh();
//        }
    }

    @Override
    protected void initEvent() {
        ql_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oChatActivity.getFinish();
            }
        });

        robToChatAdapter.setOnItemClickListener(new RobToChatAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (postion >= mList.size()){
                    return;
                }

                String mi_tencentId = UserInfoUtil.getMiTencentId();

                int prom_custom_uid = mList.get(postion).getUser_id();
                int user_id = mList.get(postion).getUser_id();

                if (mi_tencentId.equals(String.valueOf(prom_custom_uid))){
                    //自己不能跟自己聊天
                    NToast.shortToast(mContext,  getString(R.string.not_me_chat));
                    return;
                }

                if (prom_custom_uid == user_id){
                    //真人
                }

                IMConversation imConversation = new IMConversation();
                imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

                imConversation.setUserIMId(mi_tencentId);
                imConversation.setUserId(UserInfoUtil.getMiPlatformId());

                imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
                imConversation.setOtherPartyId(String.valueOf(user_id));

                imConversation.setUserName(UserInfoUtil.getName());
                imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                imConversation.setOtherPartyName(mList.get(postion).getUser_nickname());
                imConversation.setOtherPartyAvatar(mList.get(postion).getShow_photo());

                imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("IMConversation", imConversation);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }

            @Override
            public void onItemClickSer(View view, int postion) {

            }

            @Override
            public void onItemClickSer2(View view, int postion) {

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
                            getNewHeterosexuUsers();
                        }
                    }
                }
            }
        });

        mSwitchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getNewHeterosexuUsers(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/getNewHeterosexuUsers", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(mContext);
            }
            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(mContext);
                finishRefresh();
                if (result == null) {
                    if (page == 1) {
                        mQlHint.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                try {
                    RobToChatResponse response = JsonMananger.jsonToBean(result, RobToChatResponse.class);
                    pageSize = response.getTotal_page();
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                        mList.addAll(response.getList());
                        robToChatAdapter.setCards(mList);
                    } else {
                        mList.addAll(response.getList());
                        robToChatAdapter.setCards(mList);
                    }
                    robToChatAdapter.notifyDataSetChanged();

                    if (page == 1 && mList.size() == 0) {
                        mQlHint.setVisibility(View.VISIBLE);
                    }else {
                        mQlHint.setVisibility(View.GONE);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1) {
                        mQlHint.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
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
                    finishData();
                }
            });
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onResume() {
        super.onResume();
        initState();

        if (TextUtils.isEmpty(state) || state.equals("OPEN")){
            initTimer();
        }else {
            finishData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    //OPEN "" 开启
    //CLOSE 关闭
    private  String state = "";
    private Timer timer;

    private void initState(){
        state = UserInfoUtil.getRobToChat();
        if (TextUtils.isEmpty(state) || state.equals("OPEN")){
            mSwitchIv.setImageResource(R.drawable.chat_btn_open);
        }else{
            mSwitchIv.setImageResource(R.drawable.chat_btn_close);
        }
    }

    private void switch_(){
        if (TextUtils.isEmpty(state) || state.equals("OPEN")){
            mSwitchIv.setImageResource(R.drawable.chat_btn_close);
            state = "CLOSE";
            UserInfoUtil.setRobToChat("CLOSE");

            /*if (mList.size() > 0) {
                mList.clear();
            }
            robToChatAdapter.setCards(mList);
            shimmerRecycler.getActualAdapter().notifyDataSetChanged();*/
        }else {
            mSwitchIv.setImageResource(R.drawable.chat_btn_open);
            state = "OPEN";
            UserInfoUtil.setRobToChat("OPEN");

            initTimer();
        }
    }

    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100){
                finishData();
            }
        }
    };

    private void initTimer(){
        try{
            stopTimer();

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(100);
                }
            }, 10, vperiodTime * 1000);
        }catch (Exception e){}
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

}

