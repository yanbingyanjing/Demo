package com.yjfshop123.live.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.umeng.commonsdk.debug.E;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.play.TCVodPlayerActivity;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivity;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivityNew;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.QuanziLiveAdapter;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.holder.BaseViewHolder;
import com.yjfshop123.live.ui.videolist.holder.ViewHolderFactory;
import com.yjfshop123.live.ui.videolist.model.BaseItem;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.videolist.model.VideoItem;
import com.yjfshop123.live.ui.widget.shimmer.PaddIngNew;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoRecyclerViewFragment_1_new extends BaseFragment {

    Unbinder unbinder;
    private int page = 1;

    private List<BaseItem> mListItems = new ArrayList<>();
    @BindView(R.id.live_list)
    RecyclerView live_list;
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
//    @BindView(R.id.refrash)
//    SmartRefreshLayout refreshLayout;
    private MyAdapter myAdapter;
    // private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mLinearLayoutManager;
    private SingleListViewItemActiveCalculator mCalculator;
    LinearLayoutManager mLinearLayoutManager_live;
    private int screenWidth;
    private boolean isLoadingMore = false;
    private int mScrollState;
    private boolean isLoadingMoreLive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    private void refreshData() {
        page = 1;
      //  refreshLayout.setNoMoreData(false);
        popularDynamic();
        livePage = 1;
        loadLiveList();
    }

    @Subscriber(tag = Config.EVENT_START)
    public void refresh(String type) {
        if (type.equals("100001")) {
            refreshData();
        } else if (type.equals("100001_1")) {
            videoStopped_();
        } else if (type.equals("100001_1_1")) {
            shimmerRecycler.scrollToPosition(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_community_1_new;
    }

    private long mLastClickPubTS = 0;

    @Override
    protected void initAction() {
        screenWidth = CommonUtils.getScreenWidth(getActivity());

        // shimmerRecycler.setHasFixedSize(true);
        // shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
        shimmerRecycler.addItemDecoration(new PaddIngNew(mContext));
//        mLinearLayoutManager = new FullyLinearLayoutManager(mContext);//处理冲突
        // mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLinearLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        myAdapter = new MyAdapter();

//        mCalculator = new SingleListViewItemActiveCalculator(myAdapter,
//                new RecyclerViewItemPositionGetter(mLinearLayoutManager, shimmerRecycler));
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setAdapter(myAdapter);

        shimmerRecycler.setItemAnimator(null);


        quanziLiveAdapter = new QuanziLiveAdapter(getContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(Object bean1, int position) {
                if (System.currentTimeMillis() - mLastClickPubTS > 1000) {
                    mLastClickPubTS = System.currentTimeMillis();

                    //  if (isHB) {
                    if (mListLive.get(position).getVod_type() == 2) {

                        LivingListResponse.LiveListBean bean = mListLive.get(position);
                        Intent intent = new Intent(mContext, TCVodPlayerActivity.class);
                        intent.putExtra(TCConstants.ROOM_TITLE, bean.getTitle());
                        intent.putExtra(TCConstants.COVER_PIC, CommonUtils.getUrl(bean.getCover_img()));
                        intent.putExtra("VIDEO_URL", bean.getVideo_url());
                        intent.putExtra("USER_NICKNAME", bean.getUser_nickname());
                        intent.putExtra("USER_ID", String.valueOf(bean.getUser_id()));
                        intent.putExtra("AVATAR", bean.getAvatar());
                        intent.putExtra("TOTAL_COIN_NUM", String.valueOf(bean.getTotal_coin_num()));
                        intent.putExtra("WATCH_NUM", String.valueOf(bean.getWatch_num()));
                        intent.putExtra("LIVE_ID", String.valueOf(bean.getLive_id()));
                        startActivity(intent);

                    } else {
                        startLivePlay(mListLive.get(position));
                    }
                }
            }
        });
        mLinearLayoutManager_live = new LinearLayoutManager(getContext());
        mLinearLayoutManager_live.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        live_list.setLayoutManager(mLinearLayoutManager_live);
        live_list.setAdapter(quanziLiveAdapter);
        live_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager_live.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager_live.getItemCount();
                NLog.d("滑动结果", lastVisibleItem + "   " + (totalItemCount - 4) + "  " + dx + " " + isLoadingMoreLive);
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dx > 0) {
                    if (!isLoadingMoreLive) {
                        isLoadingMoreLive = true;
                        livePage++;
                        loadLiveList();
                    }
                }
            }
        });

      //  refreshLayout.setEnableAutoLoadMore(true);
//        refreshLayout.setNoMoreData(false);
//        refreshLayout.setEnableRefresh(true);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                updateViews(true);
//            }
//        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                if (!isLoadingMore) {
//                    isLoadingMore = true;
//                    page++;
//                    popularDynamic();
//                }
//            }
//        });

//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                NLog.d("滑动距离",verticalOffset+"  "+isLoadingMore);
//
//                if (mSwipeRefresh == null) return;
//                if (isLoadingMore) {
//                    mSwipeRefresh.setEnabled(false);
//                } else {
//                   // if (live_list.getVisibility() == View.VISIBLE) {
//                        if (verticalOffset >= 0) {
//                            mSwipeRefresh.setEnabled(true);
//                        } else {
//                            mSwipeRefresh.setEnabled(false);
//                        }
////                    } else {
////                        mSwipeRefreshNew.setEnabled(false);
////                    }
//                }
//
//            }
//        });
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int[] mf = null;
                int[] mfFirst = null;
                mfFirst = mLinearLayoutManager.findFirstVisibleItemPositions(mfFirst);
                mf = mLinearLayoutManager.findLastVisibleItemPositions(mf);
                if (mf != null&&mf.length>0) {
                    if (mf[mf.length-1] >= mListItems.size() - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true;
                            page++;
                            popularDynamic();
                        }
                    }

                }
                /*if (mfFirst != null&&mfFirst.length>0) {
                    if (mfFirst[0] ==0) {
                       mSwipeRefresh.setEnabled(true);
                    }else {
                        mSwipeRefresh.setEnabled(false);
                    }

                }*/
            }

        });
    }

    private void startLivePlay(final LivingListResponse.LiveListBean item) {
        if (item.getLive_id() == 0) {
            ActivityUtils.startUserHome(mContext, String.valueOf(item.getUser_id()));
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            return;
        }

        if (item.getType() == 2) {
            DialogUitl.showSimpleInputDialog(mContext, "请输入房间密码", DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            if (TextUtils.isEmpty(content)) {
                                NToast.shortToast(mContext, "请输入房间密码");
                            } else {
                                startLivePlay_(item, content);
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (item.getType() == 3) {
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), "进入该直播间将收取" + item.getIn_coin() + getString(R.string.my_jinbi),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            startLivePlay_(item, null);
                        }
                    });
        } else {
            startLivePlay_(item, null);
        }
    }

    private void startLivePlay_(final LivingListResponse.LiveListBean item, String in_password) {
        String url;
        if (item.getVod_type() == 2) {
            url = "app/live/joinLive4Lubo";
        } else {
            url = "app/live/joinLive";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", item.getLive_id())
                    .put("in_password", in_password)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), errInfo,
                        true, new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                            }
                        });
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Intent intent = new Intent(getActivity(), TCLivePlayerActivity.class);

                    if (item.getLive_mode() == 2) {
                        intent.putExtra("pureAudio", true);
                    } else {
                        intent.putExtra("pureAudio", false);
                    }
                    intent.putExtra(TCConstants.ROOM_TITLE, item.getTitle());
                    intent.putExtra(TCConstants.COVER_PIC, CommonUtils.getUrl(item.getCover_img()));
                    intent.putExtra("LivePlay", result);
                    intent.putExtra("vod_type", item.getVod_type());
                    intent.putExtra("live_id", String.valueOf(item.getLive_id()));


                    intent.putExtra("zhubo_user_id", item.getUser_id() + "");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });
    }



    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        livePage = 1;
        NLog.d("qiehuan", "1");
        popularDynamic();
        showLoading();
        loadLiveList();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh) {
            page = 1;
            //refreshLayout.setNoMoreData(false);
            popularDynamic();
            showLoading();
        }else {
            page = 1;
            livePage = 1;
            NLog.d("qiehuan", "1");
            popularDynamic();
            //showLoading();
            loadLiveList();
        }
    }

    private void popularDynamic() {
        String body = "";
        String url = null;
        int type = 1;
        if (type == 1) {
            url = "app/forum/popularDynamic";
        } else if (type == 2) {
            url = "app/forum/vicinityDynamic";
        } else if (type == 3) {
            url = "app/forum/followDynamic";
        }
        try {
            if (type == 2) {
                body = new JsonBuilder()
                        .put("page", page)
                        .put("city_name", UserInfoUtil.getAddress())
                        .build();
            } else {
                body = new JsonBuilder()
                        .put("page", page)
                        .build();
            }
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                EventBus.getDefault().post("finishRefresh", Config.EVENT_START);
                hideLoading();
                //refreshLayout.finishRefresh();
               // refreshLayout.finishLoadMore(0,false,false);//传入false表示加载失败
                if (page == 1) {
                    showNotData();
                }
            }

            @Override
            public void onSuccess(String result) {
                EventBus.getDefault().post("finishRefresh", Config.EVENT_START);
                hideLoading();
               // refreshLayout.finishRefresh();
               // refreshLayout.finishLoadMore(0);//传入false表示加载失败

                try {
                    PopularDynamicResponse response = JsonMananger.jsonToBean(result, PopularDynamicResponse.class);
                    isLoadingMore = false;
                    int startInsert = mListItems.size();
                    if (page == 1) {
                        if (mListItems.size() > 0) {
                            mListItems.clear();
                        }
                    }

                    for (int i = 0; i < response.getList().size(); i++) {
                        if (response.getList().get(i).getVideo_list().size() > 0) {
                            mListItems.add(new VideoItem(response.getList().get(i)));
                        } else {
                            mListItems.add(new PicItem(response.getList().get(i)));
                        }
                    }

                    //当前无数据且原始数据超过一条
                    if (response.getList().size() == 0 && mListItems.size() > 1) {
                        //且最后一条不是底线显示底线
//                        if (!(mListItems.get(mListItems.size() - 1) instanceof BottomItem)){
//                            mListItems.add(new BottomItem());
//                        }
                       // refreshLayout.setNoMoreData(true);
                    }
                    if (page == 1) {
                        myAdapter.notifyDataSetChanged();
                    } else {
                        myAdapter.notifyItemRangeInserted(startInsert, response.getList().size());
                    }

                    if (mListItems.size() == 0) {
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        NLog.d("qiehuan", "0");
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class MyAdapter extends RecyclerView.Adapter<BaseViewHolder<? extends BaseItem>>
            implements ItemsProvider, CommunityClickListener {

        @Override
        public BaseViewHolder<? extends BaseItem> onCreateViewHolder(ViewGroup parent, int viewType) {
            return ViewHolderFactory.buildViewHolderNew(parent, viewType, getContext());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            BaseItem baseItem = mListItems.get(position);
            holder.onBind(position, baseItem, this);
        }

        @Override
        public int getItemCount() {
            return mListItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mListItems.get(position).getViewType();
        }

        @Override
        public ListItem getListItem(int position) {
            RecyclerView.ViewHolder holder = shimmerRecycler.findViewHolderForAdapterPosition(position);
            if (holder instanceof ListItem) {
                return (ListItem) holder;
            }
            return null;
        }

        @Override
        public int listItemSize() {
            return getItemCount();
        }

        @Override
        public void onContentClick(View view, int position) {
            if (!isLogin()) {
                return;
            }

            //类型（1:视频 2:图片）
            if (mListItems.get(position) instanceof VideoItem) {
                VideoItem videoItem = (VideoItem) mListItems.get(position);
                ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(videoItem.getListBean().getDynamic_id()), "app/forum/videoDynamic");
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

            } else if (mListItems.get(position) instanceof PicItem) {
                PicItem picItem = (PicItem) mListItems.get(position);
                Intent intent = new Intent(getActivity(), CommunityReplyDetailActivityNew.class);
                intent.putExtra("dynamic_id", picItem.getListBean().getDynamic_id());
                intent.putExtra("time", picItem.getListBean().getPublish_time());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

            } else {

            }
        }

        @Override
        public void onPortraitClick(View view, int position) {
            if (!isLogin()) {
                return;
            }

            int user_id = 0;
            if (mListItems.get(position) instanceof VideoItem) {
                VideoItem videoItem = (VideoItem) mListItems.get(position);
                user_id = videoItem.getListBean().getUser_id();
            } else if (mListItems.get(position) instanceof PicItem) {
                PicItem picItem = (PicItem) mListItems.get(position);
                user_id = picItem.getListBean().getUser_id();
            }
            ActivityUtils.startUserHome(mContext, String.valueOf(user_id));
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        }

        @Override
        public void onPraiseClick(View view, int position) {
            if (!isLogin()) {
                return;
            }

            if (mListItems.get(position) instanceof VideoItem) {
                VideoItem videoItem = (VideoItem) mListItems.get(position);
                if (videoItem.getListBean().getIs_like() == 0) {
                    //点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicDoLike(videoItem.getListBean().getDynamic_id(), false);

                    videoItem.getListBean().setIs_like(1);
                    videoItem.getListBean().setLike_num(videoItem.getListBean().getLike_num() + 1);
                    mListItems.set(position, videoItem);
                } else {
                    //取消点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicUndoLike(videoItem.getListBean().getDynamic_id(), false);

                    videoItem.getListBean().setIs_like(0);
                    videoItem.getListBean().setLike_num(videoItem.getListBean().getLike_num() - 1);
                    mListItems.set(position, videoItem);
                }
            } else if (mListItems.get(position) instanceof PicItem) {
                PicItem picItem = (PicItem) mListItems.get(position);
                if (picItem.getListBean().getIs_like() == 0) {
                    //点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicDoLike(picItem.getListBean().getDynamic_id(), false);

                    picItem.getListBean().setIs_like(1);
                    picItem.getListBean().setLike_num(picItem.getListBean().getLike_num() + 1);
                    mListItems.set(position, picItem);
                } else {
                    //取消点赞 异步走接口
                    CommunityDoLike.getInstance().dynamicUndoLike(picItem.getListBean().getDynamic_id(), false);

                    picItem.getListBean().setIs_like(0);
                    picItem.getListBean().setLike_num(picItem.getListBean().getLike_num() - 1);
                    mListItems.set(position, picItem);
                }
            }

            myAdapter.notifyItemChanged(position);
        }

        @Override
        public void onImgClick(View view, int position, int index) {
            List<String> images = new ArrayList<>();
            if (mListItems.get(position) instanceof PicItem) {
                PicItem picItem = (PicItem) mListItems.get(position);
                for (int i = 0; i < picItem.getListBean().getPicture_list().size(); i++) {
                    images.add(CommonUtils.getUrl(picItem.getListBean().getPicture_list().get(i).getObject()));
                }

                try {
                    Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                    intent.putExtra(Config.POSITION, index);
                    intent.putExtra("is_need_save", true);
                    intent.putExtra("Picture", JsonMananger.beanToJson(images));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onVideo(TextureVideoView videoView, ImageView videoCover, ImageView videoBtn) {
            mVideoView = videoView;
            mVideoCover = videoCover;
            mVideoBtn = videoBtn;
        }

    }

    private TextureVideoView mVideoView;
    private ImageView mVideoCover;
    private ImageView mVideoBtn;

    private void videoStopped_() {
        if (mVideoView != null) {
            mVideoView.stop();
            mVideoView.setAlpha(0);
        }

        if (mVideoCover != null) {
            mVideoCover.setAlpha(1.f);
        }

        if (mVideoBtn != null) {
            mVideoBtn.setVisibility(View.VISIBLE);
        }
    }

    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
    }

    private boolean isLogin() {
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
    public void onPause() {
        super.onPause();
        videoStopped_();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //直播间获取
    private List<LivingListResponse.LiveListBean> mListLive = new ArrayList<>();
    private int livePage = 1;
    QuanziLiveAdapter quanziLiveAdapter;

    private void loadLiveList() {

        String url = "app/live/getLivingList";


        String offset_id = "";
        if (livePage > 1 && mListLive.size() > 0) {
            offset_id = String.valueOf(mListLive.get(0).getLive_id());
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", livePage)
                    .put("channel_id", 0)//频道ID
                    .put("scene_id", 1)//场景id，可不填（1:直播首页调用 2:连麦中调用）
                    .put("offset_id", offset_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMoreLive = false;
                if (livePage > 1) livePage--;
                if (mListLive != null && mListLive.size() == 0) {
                    live_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(String result) {

                if (result != null) {
                    try {
                        isLoadingMoreLive = false;
                        LivingListResponse launchLiveResponse = JsonMananger.jsonToBean(result, LivingListResponse.class);
                        if (livePage == 1) {
                            if (mListLive.size() > 0) {
                                mListLive.clear();
                            }
                        }
                        if (livePage > 1) {
                            if (launchLiveResponse.getLive_list().size() == 0) {
                                livePage--;
                            }
                        }
                        if (launchLiveResponse.getLive_list() != null)
                            for (int i = 0; i < launchLiveResponse.getLive_list().size(); i++) {
                                if (launchLiveResponse.getLive_list().get(i).getVod_type() != 2) {
                                    mListLive.add(launchLiveResponse.getLive_list().get(i));
                                }
                            }
                        //mListLive.addAll(launchLiveResponse.getLive_list());
                        quanziLiveAdapter.setCards(mListLive);
                        quanziLiveAdapter.notifyDataSetChanged();

                        if (mListLive.size() == 0) {
                            live_list.setVisibility(View.GONE);
                        } else {
                            live_list.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        if (livePage == 1 && mListLive.size() == 0) {
                            live_list.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }
}
