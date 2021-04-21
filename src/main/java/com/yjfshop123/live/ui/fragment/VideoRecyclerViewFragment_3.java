package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.holder.BaseViewHolder;
import com.yjfshop123.live.ui.videolist.holder.ViewHolderFactory;
import com.yjfshop123.live.ui.videolist.model.BaseItem;
import com.yjfshop123.live.ui.videolist.model.BottomItem;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.videolist.model.VideoItem;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;
import com.waynell.videolist.widget.TextureVideoView;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VideoRecyclerViewFragment_3 extends BaseFragment {

    private int page = 1;

    private List<BaseItem> mListItems = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private MyAdapter myAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SingleListViewItemActiveCalculator mCalculator;

    private int screenWidth;
    private boolean isLoadingMore = false;
    private int mScrollState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    private void refreshData() {
        page = 1;
        popularDynamic();
    }

    @Subscriber(tag = Config.EVENT_START)
    public void refresh(String type) {
        if (type.equals("100003")) {
            refreshData();
        } else if (type.equals("100003_3")) {
            videoStopped_();
        } else if (type.equals("100003_3_3")) {
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
        return R.layout.fragment_community_1;
    }

    @Override
    protected void initAction() {
        screenWidth = CommonUtils.getScreenWidth(getActivity());

        shimmerRecycler.setHasFixedSize(true);
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
//        mLinearLayoutManager = new FullyLinearLayoutManager(mContext);//处理冲突
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new MyAdapter();

        mCalculator = new SingleListViewItemActiveCalculator(myAdapter,
                new RecyclerViewItemPositionGetter(mLinearLayoutManager, shimmerRecycler));
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setAdapter(myAdapter);
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && myAdapter.getItemCount() > 0) {
                    mCalculator.onScrollStateIdle();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mCalculator.onScrolled(mScrollState);

                //更多
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        popularDynamic();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        popularDynamic();
        showLoading();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh) {
            page = 1;
            popularDynamic();
            showLoading();
        }
    }

    private void popularDynamic(){
        String body = "";
        String url = null;
        int type = 3;
        if (type == 1){
            url = "app/forum/popularDynamic";
        }else if (type == 2){
            url = "app/forum/vicinityDynamic";
        }else if (type == 3){
            url = "app/forum/followDynamic";
        }
        try {
            if (type == 2){
                body = new JsonBuilder()
                        .put("page", page)
                        .put("city_name", UserInfoUtil.getAddress())
                        .build();
            }else {
                body = new JsonBuilder()
                        .put("page", page)
                        .build();
            }
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                EventBus.getDefault().post("finishRefresh",Config.EVENT_START);
                hideLoading();
                if (page == 1){
                    showNotData();
                }
            }
            @Override
            public void onSuccess(String result) {
                EventBus.getDefault().post("finishRefresh",Config.EVENT_START);
                hideLoading();
                try {
                    PopularDynamicResponse response = JsonMananger.jsonToBean(result, PopularDynamicResponse.class);
                    isLoadingMore = false;
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
                        if (!(mListItems.get(mListItems.size() - 1) instanceof BottomItem)) {
                            mListItems.add(new BottomItem());
                        }
                    }

                    myAdapter.notifyDataSetChanged();

                    if (mListItems.size() == 0) {
                        showNotData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<BaseViewHolder<? extends BaseItem>>
            implements ItemsProvider, CommunityClickListener {

        @Override
        public BaseViewHolder<? extends BaseItem> onCreateViewHolder(ViewGroup parent, int viewType) {
            return ViewHolderFactory.buildViewHolder(parent, viewType, screenWidth);
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
            if (!isLogin()){
                return;
            }

            //类型（1:视频 2:图片）
            if (mListItems.get(position) instanceof VideoItem) {
                VideoItem videoItem = (VideoItem) mListItems.get(position);
                ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(videoItem.getListBean().getDynamic_id()), "app/forum/videoDynamic");
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

            } else if (mListItems.get(position) instanceof PicItem) {
                PicItem picItem = (PicItem) mListItems.get(position);
                Intent intent = new Intent(getActivity(), CommunityReplyDetailActivity.class);
                intent.putExtra("dynamic_id", picItem.getListBean().getDynamic_id());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

            } else {

            }
        }

        @Override
        public void onPortraitClick(View view, int position) {
            if (!isLogin()){
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
            ActivityUtils.startUserHome(mContext,String.valueOf(user_id));
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        }

        @Override
        public void onPraiseClick(View view, int position) {
            if (!isLogin()){
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

            myAdapter.notifyDataSetChanged();
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
    public void onPause() {
        super.onPause();
        videoStopped_();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
