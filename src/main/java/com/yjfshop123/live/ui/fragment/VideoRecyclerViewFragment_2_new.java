package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.taskcenter.UploadSucaiAcitivity;
import com.yjfshop123.live.ui.activity.CommunityReplyDetailActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.holder.BaseViewHolder;
import com.yjfshop123.live.ui.videolist.holder.ShangxueyuanHolder;
import com.yjfshop123.live.ui.videolist.holder.ViewHolderFactory;
import com.yjfshop123.live.ui.videolist.model.BaseItem;
import com.yjfshop123.live.ui.videolist.model.BottomItem;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.videolist.model.VideoItem;
import com.yjfshop123.live.ui.widget.SucaiSaveTipsDialog;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SaveNetPhotoUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class VideoRecyclerViewFragment_2_new extends BaseFragment {

    private int page = 1;

    private List<ShareSucaiResopnse.Sucai> mListItems = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private MyAdapter myAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    private int screenWidth;
    private boolean isLoadingMore = false;
    private int mScrollState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_video_recylerview_new;
    }

    @Override
    protected void initAction() {
        screenWidth = CommonUtils.getScreenWidth(getActivity());

        shimmerRecycler.setHasFixedSize(true);
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));
//        mLinearLayoutManager = new FullyLinearLayoutManager(mContext);//处理冲突
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new MyAdapter();
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setAdapter(myAdapter);
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
        } else {
            page = 1;
            popularDynamic();
        }
    }

    private void popularDynamic() {
        String body = "";
        String url = null;

        url = "app/task/shareTemplateList";

        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                EventBus.getDefault().post("finishRefresh", Config.EVENT_START);
                hideLoading();
                if (page == 1) {
                    showNotData();
                }else {page--;}
            }

            @Override
            public void onSuccess(String result) {
                EventBus.getDefault().post("finishRefresh", Config.EVENT_START);
                hideLoading();
                try {
                    ShareSucaiResopnse response = new Gson().fromJson(result, ShareSucaiResopnse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mListItems.size() > 0) {
                            mListItems.clear();
                        }
                    }else {
                        if (response.list==null||response.list.length == 0) {
                           page--;
                        }
                    }

                    mListItems.addAll(Arrays.asList(response.list));

                    //当前无数据且原始数据超过一条
//                    if (response.getList().size() == 0 && mListItems.size() > 1) {
//                        //且最后一条不是底线显示底线
//                        if (!(mListItems.get(mListItems.size() - 1) instanceof BottomItem)) {
//                            mListItems.add(new BottomItem());
//                        }
//                    }

                    myAdapter.notifyDataSetChanged();

                    if (mListItems.size() == 0) {
                        showNotData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements CommunityClickListener {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ShangxueyuanHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_shangxueyuan, parent, false), screenWidth);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ShareSucaiResopnse.Sucai baseItem = mListItems.get(position);
            ((ShangxueyuanHolder) holder).onBind(getContext(),position, baseItem, this);
        }

        @Override
        public int getItemCount() {
            return mListItems.size();
        }


        @Override
        public void onContentClick(View view, int position) {

        }

        @Override
        public void onPortraitClick(View view, int position) {
            if (mListItems == null || mListItems.get(position) == null) return;
            //保存图片
            SystemUtils.setClipboardNoToast(getContext(), mListItems.get(position).title + "\n" + mListItems.get(position).content +"\n邀请码："+mListItems.get(position).invite_code+ "\n推广链接：" + (mListItems.get(position).isShowShare ? mListItems.get(position).share_url : ""));
            //SystemUtils.setClipboardNoToast(this, mList.get(position).title + "\n" + mList.get(position).content);

            SaveNetPhotoUtils.savePhotoListForShare(getContext(), mListItems.get(position).pic_list, mListItems.get(position).share_url ,
                    new SaveNetPhotoUtils.OnsaveOk() {
                        @Override
                        public void onSave() {
                            //保存图片弹窗
                            SucaiSaveTipsDialog dialogFragment = new SucaiSaveTipsDialog();
                            dialogFragment.setClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!CheckInstalledUtil.isWeChatAppInstalled(getContext())) {
                                        NToast.shortToast(getContext(), "微信未安装，请先安装微信！");
                                        return;
                                    }
                                    Intent lan = getContext().getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setComponent(lan.getComponent());
                                    startActivity(intent);
                                }
                            });
                            dialogFragment.show(getFragmentManager(), "SucaiSaveTipsDialog");
                        }
                    });


        }

        @Override
        public void onPraiseClick(View view, int position) {

        }

        @Override
        public void onImgClick(View view, int position, int index) {

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
}
