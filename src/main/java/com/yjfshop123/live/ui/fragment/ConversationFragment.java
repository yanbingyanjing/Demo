package com.yjfshop123.live.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcBuyOrderDetailActivity;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.PushUtil;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.message.db.RealmMessageUtils;
import com.yjfshop123.live.message.db.SystemMessage;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.KefuResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.server.widget.OptionsPopupDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.SearchActivity;
import com.yjfshop123.live.ui.activity.SystemMessageActivity;
import com.yjfshop123.live.ui.adapter.ConversationAdapter;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.net.utils.json.JsonMananger.jsonToBean;

public class ConversationFragment extends BaseFragment /*implements ConversationView*/ {


    Unbinder unbinder;
    private List<IMConversationDB> mConversations;
    private SystemMessage mSystemMessage;

    @BindView(R.id.conversation_recycler_view)
    RecyclerView shimmerRecycler;

    @BindView(R.id.conversation_swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.conversation_search)
    ImageView mSearch;

    @BindView(R.id.conversation_dele)
    ImageView mDele;

    @BindView(R.id.top)
    View top;

    private LinearLayoutManager mLinearLayoutManager;
    private ConversationAdapter conversationAdapter;

    private BaseActivityH activityH;

    private View systemHeader;

    private String mi_platformId;
   LinearLayout zhushou;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BaseActivityH) {
            activityH = (BaseActivityH) getActivity();
        }
        EventBus.getDefault().register(this);
    }

    RelativeLayout system_view_one;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initAction() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  top.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        top.setLayoutParams(params);
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        systemHeader = View.inflate(mContext.getApplicationContext(), R.layout.system_header, null);
        conversationAdapter = new ConversationAdapter(systemHeader);
        shimmerRecycler.setAdapter(conversationAdapter);
//        shimmerRecycler.showShimmerAdapter();
//        shimmerRecycler.hideShimmerAdapter();

        initSwipeRefresh();

        mConversations = activityH.getData();
        mSystemMessage = activityH.getSystemMessage();
        dataSetChanged();

        loadSystemMessage();
        /*queryConverMsg();
        new ConversationPresenter(this);*/
    }

    @Override
    protected void initEvent() {
        conversationAdapter.setOnItemClickListener(new ConversationAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (postion < 1) {
                    return;
                }
                postion = postion - 1;

                Intent intent;
                IMConversation imConversation = new IMConversation();
                intent = new Intent(mContext, MessageListActivity.class);

                imConversation.setUserName(UserInfoUtil.getName());
                imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                imConversation.setType(mConversations.get(postion).getType());
                imConversation.setUserIMId(mConversations.get(postion).getUserIMId());
                imConversation.setOtherPartyIMId(mConversations.get(postion).getOtherPartyIMId());
                imConversation.setUserId(mConversations.get(postion).getUserId());
                imConversation.setOtherPartyId(mConversations.get(postion).getOtherPartyId());

                imConversation.setOtherPartyName(mConversations.get(postion).getOtherPartyName());
                imConversation.setOtherPartyAvatar(mConversations.get(postion).getOtherPartyAvatar());
                imConversation.setConversationId(mConversations.get(postion).getConversationId());
                intent.putExtra("IMConversation", imConversation);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

                mConversations.get(postion).setUnreadCount(0);
                dataSetChanged();

                RealmConverUtils.clerRedCount_(mConversations.get(postion).getConversationId());
            }
        });

        conversationAdapter.setOnLongClickListener(new ConversationAdapter.MyLongClickListener() {
            @Override
            public void onLongClick(View view, final int postion) {
                if (postion < 1) {
                    return;
                }
                final int pos = postion - 1;

                String[] items = new String[]{getString(R.string.de_item_delete_friend) + "会话", getString(R.string.cancel)};
                OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                    @Override
                    public void onOptionsItemClicked(int which) {
                        switch (which) {
                            case 0:
                                String conversationId = mConversations.get(pos).getConversationId();
                                RealmConverUtils.deleteConverMsg(conversationId);
                                RealmMessageUtils.deleteCoverMessageMsg(conversationId);
                                break;
                        }
                    }
                }).show();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        mDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure),
                        getString(R.string.cancel), getString(R.string.clear_the_message), true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();

                                RealmMessageUtils.deleteAllMessageMsg(mi_platformId);
                                RealmConverUtils.deleteAllConverMsg(mi_platformId);

                                if (mConversations != null && mConversations.size() > 0) {
                                    mConversations.clear();
                                }

                                dataSetChanged();
                            }
                        });
            }
        });

        system_view_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SystemMessageActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                RealmConverUtils.clerRedCount_("system_notice" + mi_platformId);
            }
        });
        zhushou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = "";

                OKHttpUtils.getInstance().getRequest("app/user/getKefu", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(getContext());
                        NToast.shortToast(mContext,errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(getContext());
                        try {
                            KefuResponse mResponse = jsonToBean(result, KefuResponse.class);
                            IMConversation imConversation = new IMConversation();
                            imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

                            imConversation.setUserIMId(UserInfoUtil.getMiTencentId());
                            imConversation.setUserId(UserInfoUtil.getMiPlatformId());

                            imConversation.setOtherPartyIMId(String.valueOf(mResponse.user_id));
                            imConversation.setOtherPartyId(String.valueOf(mResponse.user_id));

                            imConversation.setUserName(UserInfoUtil.getName());
                            imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                            imConversation.setOtherPartyName(mResponse.user_nickname);
                            imConversation.setOtherPartyAvatar(CommonUtils.getUrl(mResponse.avatar));

                            imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());


                            Intent intent1 = new Intent(mContext, MessageListActivity.class);
                            intent1.putExtra("IMConversation", imConversation);
                            startActivity(intent1);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                    finishRefresh();
                }
            });
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    public void updateMessage(IMConversationDB imConversationDB) {
        if (imConversationDB != null){
            if (mConversations.size() > 0){
                int pos = -1;
                for (int i = 0; i < mConversations.size(); i++) {
                    if (imConversationDB.getConversationId().equals(mConversations.get(i).getConversationId())){
                        pos = i;
                        continue;
                    }
                }
                if (pos == -1){
                    mConversations.add(imConversationDB);
                }else{
                    mConversations.get(pos).setAvatar(imConversationDB.getAvatar());
                    mConversations.get(pos).setLastMessage(imConversationDB.getLastMessage());
                    mConversations.get(pos).setTimestamp(imConversationDB.getTimestamp());
                    mConversations.get(pos).setName(imConversationDB.getName());
                    long unreadCount = mConversations.get(pos).getUnreadCount();
                    unreadCount ++;
                    mConversations.get(pos).setUnreadCount(unreadCount);
                }
            }else {
                mConversations.add(imConversationDB);
            }

            //当前消息置顶

            conversationAdapter.setCards(mConversations);
            shimmerRecycler.getActualAdapter().notifyDataSetChanged();
        }
    }*/

    /*private RealmResults<IMConversationDB> query;

    private void queryConverMsg(){
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);
                if (mConversations.size() > 0){
                    mConversations.clear();
                }
                mConversations.addAll(datas);
                conversationAdapter.setCards(mConversations);
                shimmerRecycler.getActualAdapter().notifyDataSetChanged();


                long readCount = 0;
                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                homeActivity.mTabbar.showBadge(3, (int)readCount, true);
            }
        });
    }*/

    @Override
    public void onResume() {
        super.onResume();
        mi_platformId = UserInfoUtil.getMiPlatformId();
        PushUtil.getInstance().reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (query != null){
            query.removeChangeListeners();;
        }*/

        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void onEventMainThread(List<IMConversationDB> mList) {
        try {
            if (mList != null) {
                mConversations = mList;

                dataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    @Subscriber(tag = Config.EVENT_START)
    public void onEventMainThread(SystemMessage systemMessage) {
        mSystemMessage = systemMessage;
        loadSystemMessage();
    }

    private void loadSystemMessage() {
        zhushou=systemHeader.findViewById(R.id.zhushou);
        system_view_one = systemHeader.findViewById(R.id.system_view_one);
        TextView c_it_name = systemHeader.findViewById(R.id.c_it_name);
        TextView c_it_last_message = systemHeader.findViewById(R.id.c_it_last_message);
        TextView c_it_unread_num = systemHeader.findViewById(R.id.c_it_unread_num);

        if (TextUtils.isEmpty(mSystemMessage.getConversationId())) {
            c_it_last_message.setVisibility(View.GONE);
            c_it_name.setText(getString(R.string.de_actionbar_sub_system));
        } else {
            String lastMessage = mSystemMessage.getLastMessage();
            if (TextUtils.isEmpty(lastMessage)) {
                c_it_last_message.setVisibility(View.GONE);
            } else {
                c_it_last_message.setVisibility(View.VISIBLE);
                c_it_last_message.setText(lastMessage);
            }
            c_it_name.setText(mSystemMessage.getOtherPartyName());

            long unRead = mSystemMessage.getUnreadCount();
            if (unRead <= 0) {
                c_it_unread_num.setVisibility(View.INVISIBLE);
            } else {
                c_it_unread_num.setVisibility(View.VISIBLE);
                String unReadStr = String.valueOf(unRead);
                if (unRead < 10) {
                    c_it_unread_num.setBackgroundResource(R.drawable.point1);
                } else {
                    c_it_unread_num.setBackgroundResource(R.drawable.point1);
                    if (unRead > 99) {
                        unReadStr = "99+";
                    }
                }
                c_it_unread_num.setText(unReadStr);
            }
        }
    }

    private void dataSetChanged() {
        conversationAdapter.setCards(mConversations);
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_left)
    public void onViewClicked() {
        getActivity().finish();
    }
}

