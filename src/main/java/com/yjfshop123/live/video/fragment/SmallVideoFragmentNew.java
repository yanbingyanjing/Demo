package com.yjfshop123.live.video.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.gson.Gson;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.model.TaskResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CommunityReplyItemDetailResponse;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.shop.model.VideoList;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.ui.IVideoView;
import com.yjfshop123.live.ui.activity.CommunityReplyListActivity;
import com.yjfshop123.live.ui.activity.ReplyDialogActivity;
import com.yjfshop123.live.ui.adapter.CommunityReplyDetailAdapter2;
import com.yjfshop123.live.ui.adapter.VideoListAdapter;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.CountDownView;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.TTAdManagerHolder;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.VideoShare;
import com.yjfshop123.live.video.utils.TelephonyUtil;
import com.yuyh.library.imgsel.utils.FileUtils;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

//提前预加载视频
public class SmallVideoFragmentNew extends BaseFragment implements
        VerticalSwipeRefreshLayout.OnRefreshListener, EmptyLayout.OnRetryListener,
        IVideoView, ITXVodPlayListener, TelephonyUtil.OnTelephoneListener {

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.player_iv_pause)
    ImageView mPlayerIvPause;
    @BindView(R.id.community_video_back_)
    ImageView communityVideoBack;
    @BindView(R.id.community_video_more_)
    ImageView communityVideoMore;
    Unbinder unbinder;
    @BindView(R.id.status)
    View status;
    @BindView(R.id.daojishi)
    CountDownView daojishi;

    private ObjectAnimator mPlayBtnAnimator;

    private Context mContext;
    private List<VideoDynamicResponse.ListBean> datas;

    private int currentPosition = 0;
    private int mPosition = 0;
    private LinearLayoutManager linearLayoutManager;
    private VideoListAdapter videoListAdapter;

    private int mPage = 1;

    private String mDynamicId;
    private int mType = 1;
    private String mUrl = "";

    private String mi_tencentId;

    private boolean isLoadingMore_ = false;
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private TXVodPlayer mTXVodPlayer;
    private TXVodPlayer mTXVodPlayerPre, mTXVodPlayerPre2;//预加载
    private String mTXVodPlayerPre_index="moren" , mTXVodPlayerPre2_index="moren";
    private TXCloudVideoView mTXCloudVideoView;
    private ImageView mIvCover;

    private String mUserID;
    private long start_time;
    private long end_time;
    private int adJiange = 10;//间隔多少视频插入一条广告

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_small_video_new;
    }

    float expressViewWidth, expressViewHeight;
    int videoIndex = 0;
    private TTAdNative mTTAdNative;
    AdSlot adSlot;

    @Override
    protected void initAction() {
        EventBus.getDefault().register(this);
        mTTAdNative = TTAdManagerHolder.get().createAdNative(getActivity());
        expressViewWidth = SystemUtils.getScreenWidth(getContext());
        expressViewHeight = SystemUtils.getScreenHeight(getContext(), false) - SystemUtils.getStatusBarHeight(getContext()) - SystemUtils.dip2px(getContext(), 52);
        Log.d("广告加载宽高", "第一步" + expressViewWidth + " " + expressViewHeight);
        expressViewWidth = SystemUtils.px2dip(getContext(), expressViewWidth);
        expressViewHeight = (int) (SystemUtils.px2dip(getContext(), expressViewHeight) * 1.1);
        Log.d("广告加载宽高", "第一步" + expressViewWidth + " " + expressViewHeight);
        adSlot = new AdSlot.Builder()
                .setCodeId(Const.mCodeIdVideo)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .setAdCount(1) //请求广告数量为1到3条
                .build();
        //TTAdManagerHolder.get().requestPermissionIfNecessary(getContext());


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) status.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(mContext);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        status.setLayoutParams(params);
        mi_tencentId = UserInfoUtil.getMiTencentId();
        mSwipeRefresh.setColorSchemeResources(R.color.color_style);
        mSwipeRefresh.setOnRefreshListener(this);
        communityVideoBack.setVisibility(View.GONE);
        communityVideoMore.setVisibility(View.GONE);
        mUrl = "app/shortVideo/getPopularVideoList";
        mType = 2;
//        RelativeLayout.LayoutParams paramsSS = (RelativeLayout.LayoutParams) daojishi.getLayoutParams();
//        //获取当前控件的布局对象
//        paramsSS.topMargin = SystemUtils.getStatusBarHeight(getContext())+SystemUtils.dip2px(getContext(),62);//设置当前控件布局的高度
//
//        daojishi.setLayoutParams(paramsSS);
        datas = new ArrayList<>();


        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        int screenWidth = CommonUtils.getScreenWidth(mContext);
        videoListAdapter = new VideoListAdapter(mContext, datas, this, screenWidth);
        mRecyclerView.setAdapter(videoListAdapter);
        mEmptyLayout.setBackgroundResource(R.drawable.shape_video_bottom_mask);
        new GravityPagerSnapHelper(Gravity.BOTTOM, true, new GravitySnapHelper.SnapListener() {
            @Override
            public void onSnap(int position) {

                if (currentPosition == position) {
                    return;
                }
                //检查是否有商品加入
//                if (isNeedInserShop&&videoList != null && videoList.size() > 0) {
//                    isNeedInserShop=false;
//                    if (datas != null && (datas.size() - currentPosition) > 5) {
//                        boolean isNeedUpdate = false;
//                        for (int i = currentPosition + 5; i < datas.size(); i = i + 5) {
//                            if (videoIndex < videoList.size())
//                                if (TextUtils.isEmpty(datas.get(i).goodsId)) {
//                                    isNeedUpdate = true;
//                                    datas.add(i, videoList.get(videoIndex));
//                                    videoIndex++;
//                                }
//                        }
//                        if (isNeedUpdate) videoListAdapter.notifyDataSetChanged();
//                    }
//                }

//                if (mTXVodPlayer != null) {
//
//                   // NLog.d("播放视频的时长", "视频id:" + datas.get(currentPosition).getDynamic_id() + "\n视频播放时长：" + (isRestarPlay?(mTXVodPlayer.getDuration()+""):(mTXVodPlayer.getCurrentPlaybackTime() + "")));
//                }
                currentPosition = position;
                if (isFollowUpdate) {
                    isFollowUpdate = false;
                    Log.d("播放-刷新试图了", "1");
                    videoListAdapter.notifyDataSetChanged();
                }
                isRestarPlay = false;
                Log.d("播放来源", "2");
                startPlay(currentPosition);

                if (currentPosition >= datas.size() - 4) {
                    if (!isLoadingMore_) {
                        isLoadingMore_ = true;
                        mPage++;
                        videoList();
                    }
                }

            }
        }).attachToRecyclerView(mRecyclerView);
        showLoading();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTXVodPlayer.seek(seekBar.getProgress());
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });

        initPlayBtnAnimator();
//        Log.d("广告加载失败", "跑了2次");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadExpressDrawNativeAd(10);
//            }
//        }, 500);
        onRefresh();
        // getVideoData();
    }

    List<TTNativeExpressAd> adList = new ArrayList<>();
    int adSetIndex = 0;


    private void uploadLookRecord(final String id) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", id)
                    .build();
        } catch (JSONException e) {
        }
        NLog.e("上报数据", id);
        OKHttpUtils.getInstance().getRequest("app/shortvideo/report", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                NLog.e("上报数据失败", " 视频ID：" + id + errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NLog.e("上报数据成功", id);
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    boolean isFirst = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        NLog.d("SmallVideoFragmentNew", "setUserVisibleHint" + isVisibleToUser);

        if (isVisibleToUser) {
            if (isExit) {
                isExit = false;
                if (!isFirst) {
                    //从暂停状态恢复
                    Log.d("播放来源", "1");
                    //startPlay(currentPosition);
                    resume();
                    loadData();
                } else {
                    isFirst = false;
                    // resume();
                }
            }
        } else {
            isExit = true;
            pause();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }


    private void initvideoList() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", mPage)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortVideo/getPopularVideoList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (datas != null && datas.size() > 0) {
                } else
                    showNotData();
            }

            @Override
            public void onSuccess(String result) {

                if (result == null) {
                    hideLoading();
                    return;
                }
                try {
                    VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);
                    isLoadingMore = false;

                    if (response.getList().size() == 0) {
                        hideLoading();
                        showNotData();
                        if (datas != null && datas.size() > 0) {
                            datas.clear();
                            if (videoListAdapter != null) {
                                Log.d("播放-刷新试图了", "2");
                                videoListAdapter.notifyDataSetChanged();
                            }
                        }

                    } else {
                        mDynamicId = response.getList().get(0).getDynamic_id() + "";
                        videoList();
                    }

                } catch (HttpException e) {
                    hideLoading();
                    e.printStackTrace();
                    if (page == 1) {
                        showNotData();
                    }
                }
            }
        });
    }

    private void startPlay(final int position) {
        if (datas == null || datas.size() == 0) return;
        if (isExit || !getUserVisibleHint()) {
            return;
        }
//        if (isNeedInserShop && adList != null && adList.size() > 0) {
//            isNeedInserShop = false;
//            if (datas != null && (datas.size() - currentPosition) > adJiange) {
//                boolean isNeedUpdate = false;
//                for (int i = currentPosition + adJiange; i < datas.size(); i = i + adJiange) {
//                    Log.d("广告数据处理",adSetIndex+"  "+adList.size()+" "+i);
//                    if (adSetIndex < adList.size()) {
//                        isNeedUpdate = true;
//                        VideoDynamicResponse.ListBean listBean = new VideoDynamicResponse.ListBean();
//                        listBean.ad = adList.get(adSetIndex);
//                        datas.add(i, listBean);
//                        adSetIndex++;
//                    }
//
//                }
//                if (isNeedUpdate) videoListAdapter.notifyDataSetChanged();
//            }
//        }

        if (mTXVodPlayer != null) {
            mTXVodPlayer.seek(0);
            mTXVodPlayer.pause();
            mTXVodPlayer.stopPlay(true);
            mTXVodPlayer=null;
            if (mTXCloudVideoView != null) {
                mTXCloudVideoView.onDestroy();
                mTXCloudVideoView = null;
            }
        }
        //暂停广告
        pauseAd();
        hidePlayBtn();
        Log.d("播放_数据", datas.get(position).is_ad + "    123");
        if (datas.get(position).is_ad == 1) {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != mRecyclerView.findViewHolderForAdapterPosition(position)) {
                        if (mRecyclerView.findViewHolderForAdapterPosition(position) instanceof VideoListAdapter.AdViewHolder) {
                            startAd(position);
                        }
                    }
                }
            }, 100);
            return;
        }

        if (mIvCover != null) {
            mIvCover.setVisibility(View.VISIBLE);
        }
        uploadLookRecord(datas.get(position).getDynamic_id() + "");
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlay2(position);
            }
        }, 100);
    }

    VideoView videoView;

    private void pauseAd() {
        if (videoView != null && videoView.isPlaying())
            videoView.pause();
    }

    //播放广告
    private void startAd(final int position) {
        if (null != mRecyclerView.findViewHolderForAdapterPosition(position)) {
            if (mRecyclerView.findViewHolderForAdapterPosition(position) instanceof VideoListAdapter.AdViewHolder) {
                VideoListAdapter.AdViewHolder viewHolder = (VideoListAdapter.AdViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                final View view = viewHolder.mVideo.getChildAt(0);
                if (view == null || !(view instanceof VideoView)) {
                    return;
                }
                videoView = (VideoView) view;
                final MediaPlayer[] mediaPlayer = new MediaPlayer[1];

                videoView.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            mediaPlayer[0] = mp;
                            mp.setLooping(true);
                            return false;
                        }
                    });
                }
                viewHolder.mVPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoView.isPlaying()) {
                            showPlayBtn();
                            pause();
                            if (mPlayBtnAnimator != null) {
                                mPlayBtnAnimator.start();
                            }
                        } else {
                            videoView.start();
                        }

                    }
                });
            }

        }
    }

    private void startPlay2(int position) {
        if (null != mRecyclerView.findViewHolderForAdapterPosition(position) && mRecyclerView.findViewHolderForAdapterPosition(position) instanceof VideoListAdapter.ViewHolder) {
            Log.d("播放_正常", position + "    123");
            VideoListAdapter.ViewHolder viewHolder = (VideoListAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            mTXCloudVideoView = viewHolder.mVideo;
            community_video_follow_current = viewHolder.community_video_follow;
            mIvCover = viewHolder.mCover;
            VideoDynamicResponse.ListBean currentUrl = datas.get(position );
//            mPlayerIvPause = viewHolder.mPause;
            if (mTXVodPlayerPre == null) {
                mTXVodPlayerPre = new TXVodPlayer(mContext);
                mTXVodPlayerPre.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

                TXVodPlayConfig config = new TXVodPlayConfig();
                config.setCacheFolderPath(getInnerSDCardPath() + "/chatCache");
                config.setMaxCacheItems(10);
//                if (SystemUtils.isH265HWDecoderSupport()) {
//                    mTXVodPlayer.enableHardwareDecode(true);
//                }
                mTXVodPlayerPre.setConfig(config);
                mTXVodPlayerPre.setAutoPlay(false);
            }

            if (mTXVodPlayerPre2 == null) {
                mTXVodPlayerPre2 = new TXVodPlayer(mContext);
                mTXVodPlayerPre2.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

                TXVodPlayConfig config = new TXVodPlayConfig();
                config.setCacheFolderPath(getInnerSDCardPath() + "/chatCache");
                config.setMaxCacheItems(10);
//                if (SystemUtils.isH265HWDecoderSupport()) {
//                    mTXVodPlayer.enableHardwareDecode(true);
//                }
                mTXVodPlayerPre2.setConfig(config);
                mTXVodPlayerPre2.setAutoPlay(false);
            }

            if (mTXVodPlayerPre_index.equals(currentUrl.getVideo_list().get(0).getObject())) {
                //如果第一个预加载的是当前视频直接使用
                //则第二个加载下一个视频
                NLog.d("视频加载", "0");
                mTXVodPlayer = mTXVodPlayerPre;
                mTXVodPlayer.setVodListener(this);
                mTXVodPlayer.setPlayerView(mTXCloudVideoView);
                mTXVodPlayer.resume();

                if (position + 1 < datas.size() && datas.get(position + 1).is_ad != 1) {
                    VideoDynamicResponse.ListBean l = datas.get(position + 1);
                    mTXVodPlayerPre2_index = l.getVideo_list().get(0).getObject();
                    //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
                    int width = l.getVideo_list().get(0).getExtra_info().getWidth();
                    int height = l.getVideo_list().get(0).getExtra_info().getHeight();
                    if (width > height) {
                        mTXVodPlayerPre2.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                    } else {
                        mTXVodPlayerPre2.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                    }
                    String videoUrl = CommonUtils.getUrl(l.getVideo_list().get(0).getObject());
                    mTXVodPlayerPre2.startPlay(videoUrl);
                }
            } else {
                if (mTXVodPlayerPre2_index .equals(currentUrl.getVideo_list().get(0).getObject())) {
                    //如果第二个预加载的是当前视频
                    NLog.d("视频加载", "1");
                    mTXVodPlayer = mTXVodPlayerPre2;
                    mTXVodPlayer.setVodListener(this);
                    mTXVodPlayer.setPlayerView(mTXCloudVideoView);
                    mTXVodPlayer.resume();
                    //则第一个加载下一个视频
                    if (position + 1 < datas.size() && datas.get(position + 1).is_ad != 1) {

                        VideoDynamicResponse.ListBean l = datas.get(position + 1);
                        mTXVodPlayerPre_index = l.getVideo_list().get(0).getObject();
                        //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
                        int width = l.getVideo_list().get(0).getExtra_info().getWidth();
                        int height = l.getVideo_list().get(0).getExtra_info().getHeight();
                        if (width > height) {
                            mTXVodPlayerPre.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        } else {
                            mTXVodPlayerPre.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        }
                        String videoUrl = CommonUtils.getUrl(l.getVideo_list().get(0).getObject());
                        mTXVodPlayerPre.startPlay(videoUrl);
                    }
                } else {
                    //两个全部都没有加载当前视频
                    //第一个加载当前视频 第二个加载下一个视频
                    if (position < datas.size() && datas.get(position).is_ad != 1) {

                        VideoDynamicResponse.ListBean l = datas.get(position);
                        mTXVodPlayerPre_index = l.getVideo_list().get(0).getObject();
                        //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
                        int width = l.getVideo_list().get(0).getExtra_info().getWidth();
                        int height = l.getVideo_list().get(0).getExtra_info().getHeight();
                        if (width > height) {
                            mTXVodPlayerPre.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        } else {
                            mTXVodPlayerPre.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        }
                        String videoUrl = CommonUtils.getUrl(l.getVideo_list().get(0).getObject());
                        mTXVodPlayer = mTXVodPlayerPre;
                        NLog.d("视频加载", "2");
                        NLog.d("视频加载mTXCloudVideoView", mTXCloudVideoView==null?"空":"正常");
                        mTXVodPlayer.setVodListener(this);
                        mTXVodPlayer.setPlayerView(mTXCloudVideoView);
                        mTXVodPlayer.startPlay(videoUrl);

                    }
                    if (position + 1 < datas.size() && datas.get(position + 1).is_ad != 1) {

                        VideoDynamicResponse.ListBean l = datas.get(position + 1);
                        mTXVodPlayerPre2_index = l.getVideo_list().get(0).getObject();
                        //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
                        int width = l.getVideo_list().get(0).getExtra_info().getWidth();
                        int height = l.getVideo_list().get(0).getExtra_info().getHeight();
                        if (width > height) {
                            mTXVodPlayerPre2.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        } else {
                            mTXVodPlayerPre2.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        }
                        String videoUrl = CommonUtils.getUrl(l.getVideo_list().get(0).getObject());
                        mTXVodPlayerPre2.startPlay(videoUrl);
                    }

                }
            }
//            if (mTXVodPlayerPre_index == position) {
//                //如果第一个预加载的是当前视频直接使用
//                mTXVodPlayer = mTXVodPlayerPre;
//            }
//            if (mTXVodPlayerPre2_index == position) {
//                //如果第二个预加载的是当前视频直接使用
//                mTXVodPlayer = mTXVodPlayerPre2;
//            }
            //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
//            int width = datas.get(position).getVideo_list().get(0).getExtra_info().getWidth();
//            int height = datas.get(position).getVideo_list().get(0).getExtra_info().getHeight();
//            if (width > height) {
//                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
//            } else {
//                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
//            }
            //  mTXVodPlayer.setVodListener(this);

            //  String videoUrl = CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getObject());
            //  mTXVodPlayer.setPlayerView(mTXCloudVideoView);
            //     mTXVodPlayer.startPlay(videoUrl);

            viewHolder.mVPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTXVodPlayer == null) {
                        return;
                    }
                    if (mTXVodPlayer.isPlaying()) {
                        pause();

                        showPlayBtn();
                        if (mPlayBtnAnimator != null) {
                            mPlayBtnAnimator.start();
                        }
                    } else {
                        resume();
                    }
                }
            });
        }
    }

    private void initPlayBtnAnimator() {
        mPlayBtnAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayerIvPause,
                PropertyValuesHolder.ofFloat("scaleX", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mPlayBtnAnimator.setDuration(150);
        mPlayBtnAnimator.setInterpolator(new AccelerateInterpolator());
    }

    private void showPlayBtn() {
        if (mPlayerIvPause != null && mPlayerIvPause.getVisibility() != View.VISIBLE) {
            mPlayerIvPause.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayBtn() {
        if (mPlayerIvPause != null && mPlayerIvPause.getVisibility() == View.VISIBLE) {
            mPlayerIvPause.setVisibility(View.GONE);
        }
    }

    private String getInnerSDCardPath() {
        //放到缓存目录
        return FileUtils.createRootPath(getContext());
        //  return Environment.getExternalStorageDirectory().getPath();
    }

    boolean isRestarPlay = false;

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            isRestarPlay = true;
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timer == null && getUserVisibleHint()) {
            isExit = false;
            resume();
            loadData();
        }
        NLog.d("SmallVideoFragmentNew", "onResume");

    }

    @Subscriber(tag = Config.EVENT_GUANZHU)
    public void onGuanzhu(GuanzhuTongzhi content) {
        NLog.d("是否有关注消息收到", "收到了");
        if (datas != null && datas.size() > 0) {
            if (content != null) {
                for (int i = 0; i < datas.size(); i++) {
                    if (content.user_id == datas.get(i).getUser_id()) {
                        datas.get(i).setIs_follow(content.isGuanzhu);
                        if (content.isGuanzhu == 1)
                            community_video_follow_current.setImageResource(R.drawable.ic_add_follow_2);
                        else {
                            community_video_follow_current.setImageResource(R.drawable.ic_add_follow);
                        }
                        isFollowUpdate = true;
                    }
                }

            }
        }
    }

    //获取任务中心的数据
    private void loadData() {

        OKHttpUtils.getInstance().getRequest("app/task/taskCenter", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
//                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
//                    showNoNetData();
//                }
            }

            @Override
            public void onSuccess(String result) {

                initData(result);
            }
        });
    }

    int time;

    private void initData(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) {
            return;
        }

        TaskResponse data = new Gson().fromJson(result, TaskResponse.class);
        if (timer == null && data != null && !data.is_complete && !TextUtils.isEmpty(data.time)) {
            daojishi.setVisibility(View.VISIBLE);
            start_time = System.currentTimeMillis() / 1000;

            time = Integer.parseInt(data.time);
            RelativeLayout.LayoutParams paramsSS = (RelativeLayout.LayoutParams) daojishi.getLayoutParams();
            //获取当前控件的布局对象
            paramsSS.topMargin = SystemUtils.getStatusBarHeight(getContext()) + SystemUtils.dip2px(getContext(), 62);//设置当前控件布局的高度
            if (time >= 100) {
                paramsSS.width = SystemUtils.dip2px(getContext(), 30);
                paramsSS.height = SystemUtils.dip2px(getContext(), 30);
            }
            daojishi.setLayoutParams(paramsSS);
//            daojishi.setAddCountDownListener(new CountDownView.OnCountDownFinishListener() {
//                @Override
//                public void countDownFinished() {
//                    if (start_time > 0)
//                        updateTaskLookDate();
//                    daojishi.setVisibility(View.GONE);
//                }
//            });
//            daojishi.startCountDown();
            timer = new CountDownTimer(time * 1000, 1000) {
                /**
                 * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                 * @param millisUntilFinished
                 */
                @Override
                public void onTick(long millisUntilFinished) {
                    if (daojishi.getVisibility() != View.VISIBLE) {
                        daojishi.setVisibility(View.VISIBLE);
                    }
                    daojishi.setCountdownTime((int) millisUntilFinished / 1000, 360 * (time - millisUntilFinished / 1000) / time);
                    NLog.d("倒计时", millisUntilFinished / 1000 + "s" + " " + 360 * (millisUntilFinished) / time + " " + daojishi.getVisibility());
                }

                /**
                 * 倒计时完成时被调用
                 */
                @Override
                public void onFinish() {
                    daojishi.setVisibility(View.GONE);
                    if (start_time > 0)
                        updateTaskLookDate();
                }
            };
            timer.start();
        } else daojishi.setVisibility(View.INVISIBLE);
    }

    private CountDownTimer timer;

    @Override
    public void onPause() {
        super.onPause();
        NLog.d("SmallVideoFragmentNew", "onPause");
        isExit = true;
        pause();
        if (timer != null) {

            timer.cancel();
            timer = null;
        }
    }

    private void updateTaskLookDate() {
        String body = "";
        try {

            body = new JsonBuilder()
                    .put("start_time", start_time)
                    .put("end_time", System.currentTimeMillis() / 1000)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/task/viewVideoTask", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NLog.d("上传任务观看时长失败", errInfo);
            }

            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) return;
                NToast.shortToast(getContext(), "视频任务完成");
                NLog.e("上传任务观看时长成功");
//                TaskCompleteResponse taskCompleteResponse = new Gson().fromJson(result, TaskCompleteResponse.class);
//                ShowTargetDialog dialogFragment = new ShowTargetDialog();
//                dialogFragment.setTitle("视频任务已完成");
//                dialogFragment.setTopTitle("视频任务奖励");
//                dialogFragment.show(getChildFragmentManager(), "ShowTargetDialog");
            }
        });

    }

    private void pause() {
        pauseAd();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.pause();
        }
    }

    private void resume() {

        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }
        if (videoView != null) videoView.stopPlayback();
        stopPlay(true);
        mTXVodPlayer = null;
        mTXVodPlayerPre2 = null;
        mTXVodPlayerPre = null;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        TelephonyUtil.getInstance().uninitPhoneListener();
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {

        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
            //FIXBUG:不能修改为横屏，合唱会变为横向的
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放
            mIvCover.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            if (mTXVodPlayer != null) mTXVodPlayer.resume();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            hidePlayBtn();
            if (isExit) onPause();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }

            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            int ableDuration = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
                mSeekBar.setSecondaryProgress(ableDuration);
                mSeekBar.setMax(duration);
            }
        } else if (event < 0) {

        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    @Override
    public void onRinging() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onOffhook() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(true);
        }
    }

    @Override
    public void onIdle() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setMute(false);
        }
    }

    private boolean isExit = true;

    boolean isNeedInserShop = false;

    private void videoList() {
        String body = "";
        try {

            body = new JsonBuilder()
                    .put("dynamic_id", mDynamicId)
                    .put("page", mPage)
                    .build();

        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(mUrl, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (mPage == 1) showNotData();
                if (mPage > 1) mPage--;
                isLoadingMore_ = false;
            }

            @Override
            public void onSuccess(String result) {

                if (result == null) {
                    hideLoading();
                    isLoadingMore_ = false;
                    return;
                }
                adAd(result);
            }
        });
    }

    //处理视频数据
    private void dealVideoData(String result, VideoDynamicResponse.ListBean ad) {
        hideLoading();
        isLoadingMore_ = false;
        try {
            isNeedInserShop = true;
            VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);
            if (mPage == 1) {
                datas.clear();
            }
            int startUpdateIndex = datas.size();

            datas.addAll(response.getList());
            if (ad != null)
                datas.add(ad);

            if (mPage == 1) {
                videoListAdapter.notifyDataSetChanged();
            } else {
                videoListAdapter.notifyItemRangeChanged(startUpdateIndex, datas.size() - startUpdateIndex);
            }
            if (!isExit) {
                if (mPage == 1) {
                    //adSetIndex=0;
                    Log.d("播放来源", "3");
                    startPlay(0);
                } else {

                    if (mTXVodPlayer != null && mTXVodPlayer.isPlaying()) {
                        return;
                    }

                    if (videoView != null && videoView.isPlaying()) {
                        return;
                    }
                    //修复加载下页第一个视频无法播放问题
                    //startPlay(currentPosition);
                }
            }
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    //添加广告
    private void adAd(final String result) {

        mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d("广告加载失败", message);
                dealVideoData(result, null);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.d("广告加载成功", "第一步" + (ads == null));
                if (ads == null || ads.isEmpty()) {
                    dealVideoData(result, null);
                    return;
                }

                for (final TTNativeExpressAd ad : ads) {
                    //点击监听器必须在getAdView之前调
                    ad.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                        @Override
                        public void onVideoLoad() {

                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {

                        }

                        @Override
                        public void onVideoAdStartPlay() {
                            if (isExit) onPause();
                        }

                        @Override
                        public void onVideoAdPaused() {
                            Log.d("drawss", "onVideoAdPaused!");
                        }

                        @Override
                        public void onVideoAdContinuePlay() {

                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {

                        }

                        @Override
                        public void onVideoAdComplete() {
                            Log.d("drawss", "onVideoAdComplete!");
                            if (videoView != null) {
                                videoView.resume();
                            }
                        }

                        @Override
                        public void onClickRetry() {

                            Log.d("drawss", "onClickRetry!");
                        }
                    });
                    ad.setCanInterruptVideoPlay(true);
                    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {

                        }

                        @Override
                        public void onAdShow(View view, int type) {

                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {
                            dealVideoData(result, null);
                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            Log.d("广告加载成功", "第二步" + new Gson().toJson(ad.getMediaExtraInfo()) + "\n" + new Gson().toJson(ad.getVideoModel()));


                            VideoDynamicResponse.ListBean listBean = new VideoDynamicResponse.ListBean();
                            listBean.is_ad = 1;
                            listBean.ad = ad;
                            dealVideoData(result, listBean);
                        }
                    });
                    ad.render();
                }

            }
        });
    }


    private void addReplay() {
        String url = "";
        if (mType == 1) {
            url = "app/forum/addReply4Dynamic";
        } else if (mType == 2) {
            url = "app/shortvideo/addReply4Dynamic";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", datas.get(mPosition).getDynamic_id())
                    .put("parent_reply_id", 0)
                    .put("reviewed_user_id", 0)
                    .put("content", replyContent)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "评论成功");

                int reply_num = datas.get(mPosition).getReply_num() + 1;
                datas.get(mPosition).setReply_num(reply_num);
                community_video_reply_num.setText(reply_num + "");
                if (reply_dialog_title != null) {
                    reply_dialog_title.setText("回复(" + reply_num + ")");
                }

                replyContent = "";

                page = 1;
                replayList();
            }
        });
    }

    private void deleteDynamic() {
        String url = "";
        if (mType == 1) {
            url = "app/forum/myDynamicDelete";
        } else if (mType == 2) {
            url = "app/shortvideo/myDynamicDelete";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", datas.get(currentPosition).getDynamic_id())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "删除成功");
            }
        });
    }

    @Override
    public void onRetry() {
        mPage = 1;
//        videoList();
//
        showLoading();
        initvideoList();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        mPage = 1;
        initvideoList();
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(true);
        mPage = 1;
        initvideoList();
    }

    private void dialogMore(boolean isVV) {

        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_c_video, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        contentView.findViewById(R.id.dialog_c_video_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //举报
//                NToast.shortToast(mContext, "上报成功~");

                if (datas != null && datas.size() > currentPosition) {
                    int prom_custom_uid = datas.get(currentPosition).getProm_custom_uid();
                    String link = Const.getDomain() + "/apph5/inform/index"
                            + "?user_id=" + mi_tencentId
                            + "&be_user_id=" + prom_custom_uid;
                    Intent intent = new Intent("io.xchat.intent.action.webview");
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra("url", link);
                    startActivity(intent);
                }

                bottomDialog.dismiss();
            }
        });

        LinearLayout delVideo = contentView.findViewById(R.id.dialog_select_del_video);
        delVideo.setVisibility(View.GONE);
        if (isVV && datas != null && datas.size() > currentPosition) {
            String user_id = String.valueOf(datas.get(currentPosition).getUser_id());
            if (user_id.equals(mi_tencentId)) {
                delVideo.setVisibility(View.VISIBLE);
                delVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDynamic();
                        bottomDialog.dismiss();
                    }
                });
            }
        }

        contentView.findViewById(R.id.dialog_c_video_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                blockUser();
            }
        });

        contentView.findViewById(R.id.dialog_c_video_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
    }

    private void blockUser() {
        DialogUitl.showSimpleHintDialog(mContext,
                getString(R.string.prompt),
                getString(R.string.other_ok),
                getString(R.string.other_cancel),
                "拉黑该用户？",
                true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        String body = "";
                        try {
                            body = new JsonBuilder()
                                    .put("user_id", String.valueOf(datas.get(currentPosition).getUser_id()))
                                    .build();
                        } catch (JSONException e) {
                        }
                        OKHttpUtils.getInstance().getRequest("app/user/blockUser", body, new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                NToast.shortToast(mContext, errInfo);
                            }

                            @Override
                            public void onSuccess(String result) {
                                NToast.shortToast(mContext, "拉黑成功");
                            }
                        });

                    }
                });
    }

    @Override
    public void avatar(int position) {
        mPosition = position;
        if (datas.size() == 0) {
            return;
        }

        ActivityUtils.startUserHome(mContext, String.valueOf(datas.get(position).getUser_id()));
        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    public void like(ImageView community_video_like_num_iv, TextView community_video_like_num, int position) {
        mPosition = position;
        if (datas.size() == 0) {
            return;
        }

        if (datas.get(position).getIs_like() == 0) {
            //点赞 异步走接口
            if (mType == 2) {
                CommunityDoLike.getInstance().shortVideoDoLike(datas.get(position).getDynamic_id(), false);
            } else {
                CommunityDoLike.getInstance().dynamicDoLike(datas.get(position).getDynamic_id(), false);
            }

            datas.get(position).setIs_like(1);
            community_video_like_num_iv.setImageResource(R.drawable.ic_short_video_yidianzan_p);
            datas.get(position).setLike_num(datas.get(position).getLike_num() + 1);
        } else {
            //取消点赞 异步走接口
            if (mType == 2) {
                CommunityDoLike.getInstance().shortVideoUndoLike(datas.get(position).getDynamic_id(), false);
            } else {
                CommunityDoLike.getInstance().dynamicUndoLike(datas.get(position).getDynamic_id(), false);
            }

            datas.get(position).setIs_like(0);
            community_video_like_num_iv.setImageResource(R.drawable.ic_short_video_dianzan_p);
            datas.get(position).setLike_num(datas.get(position).getLike_num() - 1);
        }

        community_video_like_num.setText(datas.get(position).getLike_num() + "");

//        videoListAdapter.notifyDataSetChanged();
    }

    private TextView community_video_reply_num;

    @Override
    public void reply(TextView tv, int position) {
        mPosition = position;
        if (datas.size() == 0) {
            return;
        }
        community_video_reply_num = tv;

        replyDialog();
    }

    @Override
    public void gift(int position) {
        mPosition = position;
        if (datas.size() == 0) {
            return;
        }

        startGift(position);
    }

    private ImageView community_video_follow;
    boolean isFollowUpdate = false;
    private ImageView community_video_follow_current;

    @Override
    public void follow(ImageView iv, final int position) {
        mPosition = position;
        if (datas.size() == 0) {
            return;
        }
        community_video_follow = iv;

        if (datas.get(position).getIs_follow() == 0) {
            //去关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", String.valueOf(datas.get(mPosition).getUser_id()))
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }

                @Override
                public void onSuccess(String result) {
                    datas.get(mPosition).setIs_follow(1);
                    community_video_follow.setImageResource(R.drawable.ic_add_follow_2);
                    NToast.shortToast(mContext, "已关注");
                    for (int i = 0; i < datas.size(); i++) {
                        if (i != position) {
                            if (datas.get(i).getUser_id() == datas.get(position).getUser_id()) {
                                datas.get(i).setIs_follow(1);
                                isFollowUpdate = true;
                            }
                        }
                    }


                }
            });
        } else {
            //取消关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", String.valueOf(datas.get(mPosition).getUser_id()))
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }

                @Override
                public void onSuccess(String result) {
                    datas.get(mPosition).setIs_follow(0);
                    community_video_follow.setImageResource(R.drawable.ic_add_follow);
                    for (int i = 0; i < datas.size(); i++) {
                        if (i != position) {
                            if (datas.get(i).getUser_id() == datas.get(position).getUser_id()) {
                                datas.get(i).setIs_follow(0);
                                isFollowUpdate = true;
                            }
                        }
                    }

                }
            });
        }
    }


    public class UrlData {
        public String url;
    }

    private int sharePostion;

    @Override
    public void share(int position) {
        NLog.d("点击分享了", "3");
        sharePostion = position;
        try {
            //getUrl(CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getCover_img()), datas.get(position).getUser_nickname(), datas.get(position).getContent());
            // showShareDialog();
            VideoShare.getInstance().showShareDialog(getActivity(), datas.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    AlertDialog mDialog;
//
//    protected void showShareDialog() {
//        View view = getLayoutInflater().inflate(R.layout.share_dialog2, null);
//        mDialog = new AlertDialog.Builder(getContext(), R.style.BottomDialog2).create();
//        mDialog.show();
//
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setContentView(view);
//            window.setWindowAnimations(R.style.BottomDialog_Animation);
//            WindowManager.LayoutParams mParams = window.getAttributes();
//            mParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
//            mParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setGravity(Gravity.BOTTOM);
//            window.setAttributes(mParams);
//        }
//
//        TextView btn_wx = view.findViewById(R.id.btn_share_wx);
//        TextView btn_circle = view.findViewById(R.id.btn_share_circle);
//        TextView btn_qq = view.findViewById(R.id.btn_share_qq);
//        TextView btn_qzone = view.findViewById(R.id.btn_share_qzone);
//        TextView btn_wb = view.findViewById(R.id.btn_share_wb);
//        TextView btn_cancel = view.findViewById(R.id.btn_share_cancle);
//
//        btn_wx.setOnClickListener(mShareBtnClickListen);
//        btn_circle.setOnClickListener(mShareBtnClickListen);
//        btn_qq.setOnClickListener(mShareBtnClickListen);
//        btn_qzone.setOnClickListener(mShareBtnClickListen);
//        btn_wb.setOnClickListener(mShareBtnClickListen);
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDialog.dismiss();
//            }
//        });
//    }
//
//    private View.OnClickListener mShareBtnClickListen = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.btn_share_wx:
//                    mShare_meidia = SHARE_MEDIA.WEIXIN;
//                    break;
//                case R.id.btn_share_circle:
//                    mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
//                    break;
//                case R.id.btn_share_qq:
//                    mShare_meidia = SHARE_MEDIA.QQ;
//                    break;
//                case R.id.btn_share_qzone:
//                    mShare_meidia = SHARE_MEDIA.QZONE;
//                    break;
//                case R.id.btn_share_wb:
//                    mShare_meidia = SHARE_MEDIA.SINA;
//                    break;
//                default:
//                    break;
//            }
//
//            share();
//        }
//    };
//
//    private void share() {
//        String body = "";
//        LoadDialog.show(getContext());
//        final ShareData shareData=new ShareData();
//        shareData.type="video";
//        shareData.dynamic_id=datas.get(sharePostion).getDynamic_id()+"";
//        shareData.avatar=datas.get(sharePostion).getVideo_list().get(0).getCover_img();
//        shareData.content=datas.get(sharePostion).getContent();
//        try {
//            body = new JsonBuilder()
//                    .put("type", "video")
//                    .put("dynamic_id", shareData.dynamic_id)
//                    .put("content", shareData.content)
//                    .build();
//        } catch (JSONException e) {
//        }
////        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
////        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
//        OKHttpUtils.getInstance().getRequest("app/share/index", body, new com.yjfshop123.live.Interface.RequestCallback() {
//            @Override
//            public void onError(int errCode, String errInfo) {
//                NToast.shortToast(mContext, errInfo);
//                LoadDialog.dismiss(getContext());
//            }
//
//            @Override
//            public void onSuccess(String result) {
//                LoadDialog.dismiss(getContext());
//                if (result == null) {
//                    return;
//                }
//                try {
//                    UrlData urlData = new Gson().fromJson(result, UrlData.class);
//                    startShare(UserInfoUtil.getName(), urlData.url, datas.get(sharePostion).getVideo_list().get(0).getCover_img(), datas.get(sharePostion).getContent());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;
//
//    private void startShare(String title, String link, String icon_link, String desc) {
//        ShareAction shareAction = new ShareAction(getActivity());
//        String shareUrl = link;
//        UMWeb web = new UMWeb(shareUrl);
//        web.setThumb(new UMImage(getContext(), icon_link));
//        web.setTitle(title);
//        web.setDescription(desc);
////        shareAction.withText(desc);
//        shareAction.withMedia(web);
//        shareAction.setCallback(umShareListener);
//        shareAction.setPlatform(mShare_meidia).share();
//    }
//
//    private UMShareListener umShareListener = new UMShareListener() {
//
//        @Override
//        public void onStart(SHARE_MEDIA platform) {
//            // LoadDialog.show(getContext());
//            //   NToast.shortToast(mContext, platform + " onStart");
//            if (mDialog != null) mDialog.dismiss();
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            //NToast.shortToast(mContext, platform + " 分享成功啦");
//            if (mDialog != null) mDialog.dismiss();
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
////            NToast.shortToast(mContext, "分享失败");
//            if (mDialog != null) mDialog.dismiss();
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
////            NToast.shortToast(mContext, "分享取消了");
//            if (mDialog != null) mDialog.dismiss();
//        }
//    };

    @Override
    public void opShop(int position) {
//        Intent intent=new Intent(getActivity(), ShopByGoodActivity.class);
//        intent.putExtra("goodId",datas.get(position).goodsId);
//        startActivity(intent);
        Intent intent = new Intent(getActivity(), NewShopDetailActivity.class);
        intent.putExtra("goods_id", datas.get(position).goods_url);
        startActivity(intent);
    }

    private void startGift(int position) {
//        int prom_custom_uid = datas.get(position).getProm_custom_uid();
        int user_id = datas.get(position).getUser_id();
        int prom_custom_uid = user_id;

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))) {
            NToast.shortToast(mContext, "无法给自己送礼物~");
            return;
        }

        IMConversation imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(UserInfoUtil.getMiPlatformId());

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(UserInfoUtil.getName());
        imConversation.setUserAvatar(UserInfoUtil.getAvatar());

        imConversation.setOtherPartyName(datas.get(position).getUser_nickname());
        imConversation.setOtherPartyAvatar(CommonUtils.getUrl(datas.get(position).getAvatar()));

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        Intent intent;
        intent = new Intent(mContext, MessageListActivity.class);

        intent.putExtra("IMConversation", imConversation);
        intent.putExtra("GIFT", "GIFT");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == 222) {
            replyContent = data.getStringExtra("replyContent");
            int type = data.getIntExtra("type", 0);
            if (type == 1) {
                if (TextUtils.isEmpty(replyContent)) {
                    reply_dialog_tv.setText("留下你的精彩评论吧");
                    reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_content_txt));
                } else {
                    reply_dialog_tv.setText(replyContent);
                    reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_title_txt));
                }
            } else if (type == 2) {
                if (TextUtils.isEmpty(replyContent) || TextUtils.isEmpty(replyContent.trim())) {
                    NToast.shortToast(mContext, "评论不能为空~");
                    return;
                }
                addReplay();
                reply_dialog_tv.setText("留下你的精彩评论吧");
                reply_dialog_tv.setTextColor(getResources().getColor(R.color.color_content_txt));
            }
        }
    }

    private int page = 1;
    private VerticalSwipeRefreshLayout replyRefresh;
    private RecyclerView recyclerView;
    private CommunityReplyDetailAdapter2 communityReplyDetailAdapter2;
    private boolean isLoadingMore = false;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView reply_dialog_tv;
    private String replyContent = "";
    private TextView reply_dialog_title;
    private RelativeLayout noDataLayout;

    private void replyDialog() {
        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.reply_dialog, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        reply_dialog_title = contentView.findViewById(R.id.reply_dialog_title);
        reply_dialog_title.setText("回复(" + datas.get(mPosition).getReply_num() + ")");

        replyRefresh = contentView.findViewById(R.id.reply_dialog_refresh);
        recyclerView = contentView.findViewById(R.id.reply_dialog_recycler_view);
        reply_dialog_tv = contentView.findViewById(R.id.reply_dialog_tv);
        noDataLayout = contentView.findViewById(R.id.noDataLayout);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        communityReplyDetailAdapter2 = new CommunityReplyDetailAdapter2(mContext, mType, false);
        recyclerView.setAdapter(communityReplyDetailAdapter2);
        replyRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                replayList();
            }
        });

        page = 1;
        replayList();

        contentView.findViewById(R.id.reply_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.reply_dialog_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        replayList();
                    }
                }
            }
        });

        contentView.findViewById(R.id.reply_dialog_fl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReplyDialogActivity.class);
                intent.putExtra("replyContent", replyContent);
                startActivityForResult(intent, 111);
            }
        });

        communityReplyDetailAdapter2.setOptionClickListener(new CommunityReplyDetailAdapter2.OptionClickListener() {
            @Override
            public void optionClick() {
                dialogMore(false);
            }

            @Override
            public void headAndNameClick(View view, int option) {
                ActivityUtils.startUserHome(mContext, String.valueOf(lists.get(option).getUser_id()));
                ((Activity) mContext).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }

            @Override
            public void itemClick(View view, int option, int index) {
                Intent intent = new Intent(mContext, CommunityReplyListActivity.class);

                if (index == -1) {

                } else {
                    intent.putExtra("detail_index", index);
                    intent.putExtra("preview_index", option);
                }

                intent.putExtra("replyId", lists.get(option).getReply_id());
                intent.putExtra("TYPE", mType);
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });
    }

    private void replayList() {
        String url = "";
        if (mType == 1) {
            url = "app/forum/replayList4Dynamic";
        } else if (mType == 2) {
            url = "app/shortvideo/replayList4Dynamic";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", datas.get(mPosition).getDynamic_id())
                    .put("type", 1)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                replyFinishRefresh();
                isLoadingMore = false;
                if (page == 1) {
                    noDataLayout.setVisibility(View.VISIBLE);
                    replyRefresh.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSuccess(String result) {
                replyFinishRefresh();
                isLoadingMore = false;
                try {
                    CommunityReplyItemDetailResponse response = JsonMananger.jsonToBean(result, CommunityReplyItemDetailResponse.class);
                    if (communityReplyDetailAdapter2 != null && recyclerView != null) {

                        if (page == 1) {
                            if (lists.size() > 0) {
                                lists.clear();
                            }
                        }

                        lists.addAll(response.getList());

                        if (lists.size() > 0) {
                            noDataLayout.setVisibility(View.GONE);
                            replyRefresh.setVisibility(View.VISIBLE);
                        } else {
                            noDataLayout.setVisibility(View.VISIBLE);
                            replyRefresh.setVisibility(View.GONE);
                        }

                        communityReplyDetailAdapter2.setCards(lists);
                        communityReplyDetailAdapter2.notifyDataSetChanged();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private List<CommunityReplyItemDetailResponse.ListBeanX> lists = new ArrayList<>();

    private void replyFinishRefresh() {
        if (replyRefresh != null) {
            replyRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    public void getVideoData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.0.0");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("cid", "-1");
        paraMap.put("pageId", "1");
        paraMap.put("pageSize", 100 + "");

        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取视频商品的数据", response);
                initVideoShopData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取视频商品的数据", error);

            }
        }, HttpUtil.video_shop_url, paraMap);
    }


    List<VideoDynamicResponse.ListBean> videoList;

    private void initVideoShopData(String result) {
        if (TextUtils.isEmpty(result)) return;
        VideoList data = new Gson().fromJson(result, VideoList.class);
        if (data == null) return;
        if (data.data == null) return;
        videoList = data.data.lists;
        if (videoList != null && videoList.size() > 0) {
            for (int i = 0; i < videoList.size(); i++) {
                List<VideoDynamicResponse.ListBean.VideoListBean> videoListBeans = new ArrayList<>();
                VideoDynamicResponse.ListBean.VideoListBean d = new VideoDynamicResponse.ListBean.VideoListBean();
                d.setCover_img(videoList.get(i).videoCoverImg);
                d.setObject(videoList.get(i).videoUrl);
                VideoDynamicResponse.ListBean.VideoListBean.ExtraInfoBean ddd = new VideoDynamicResponse.ListBean.VideoListBean.ExtraInfoBean();
                ddd.setHeight(900);
                ddd.setWidth(500);
                d.setExtra_info(ddd);
                videoListBeans.add(d);
                videoList.get(i).setVideo_list(videoListBeans);
                ;
            }
        }
    }

}