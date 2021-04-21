package com.yjfshop123.live.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CommunityReplyItemDetailResponse;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.IVideoView;
import com.yjfshop123.live.ui.adapter.CommunityReplyDetailAdapter2;
import com.yjfshop123.live.ui.adapter.VideoListAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.VideoShare;
import com.yjfshop123.live.video.fragment.SmallVideoFragmentNew;
import com.yjfshop123.live.video.utils.TelephonyUtil;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GSYVideoActivity extends BaseActivity implements
        VerticalSwipeRefreshLayout.OnRefreshListener, EmptyLayout.OnRetryListener,
        IVideoView, ITXVodPlayListener, TelephonyUtil.OnTelephoneListener {

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;

    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.player_iv_pause)
    ImageView mPlayerIvPause;

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
    private TXCloudVideoView mTXCloudVideoView;
    private ImageView mIvCover;

    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_gsyvideo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setHeadVisibility(View.GONE);

        mi_tencentId = UserInfoUtil.getMiTencentId();

        ButterKnife.bind(this);

        mSwipeRefresh.setColorSchemeResources(R.color.color_style);
        mSwipeRefresh.setOnRefreshListener(this);


        mUrl = getIntent().getStringExtra("URL");
        mType = getIntent().getIntExtra("TYPE", 1);
        if (mType == 3) {
            mType = 2;
            mUserID = getIntent().getStringExtra("USER_ID");
            datas = (ArrayList<VideoDynamicResponse.ListBean>) getIntent().getSerializableExtra("DATAS");
            mPage = getIntent().getIntExtra("PAGE", 1);
            currentPosition = getIntent().getIntExtra("POSITION", 0);
        } else {
            datas = new ArrayList<>();
            mDynamicId = getIntent().getStringExtra("DYNAMIC_ID");
        }

        TelephonyUtil.getInstance().setOnTelephoneListener(this);
        TelephonyUtil.getInstance().initPhoneListener();

        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        int screenWidth = CommonUtils.getScreenWidth(this);
        videoListAdapter = new VideoListAdapter(this, datas, this, screenWidth);
        mRecyclerView.setAdapter(videoListAdapter);

        new GravityPagerSnapHelper(Gravity.BOTTOM, true, new GravitySnapHelper.SnapListener() {
            @Override
            public void onSnap(int position) {
                if (currentPosition == position) {
                    return;
                }
                currentPosition = position;
                startPlay(position);

                if (currentPosition == datas.size() - 1) {
                    if (!isLoadingMore_) {
                        isLoadingMore_ = true;
                        mPage++;
                        videoList();
                    }
                }

            }
        }).attachToRecyclerView(mRecyclerView);

        findViewById(R.id.community_video_back_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExit();
            }
        });

        findViewById(R.id.community_video_more_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMore(true);
            }
        });

        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }

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

        if (!TextUtils.isEmpty(mUserID)) {
            if (mEmptyLayout != null) {
                mEmptyLayout.hide();
            }
            mRecyclerView.scrollToPosition(currentPosition);
//            mRecyclerView.smoothScrollToPosition(currentPosition);//模拟滑动到指定位置
            startPlay(currentPosition);
        } else {
            onRefresh();
        }
    }

    private void startPlay(final int position) {
        if (isExit) {
            return;
        }

        if (mTXVodPlayer != null) {
            mTXVodPlayer.seek(0);
            mTXVodPlayer.pause();
            mTXVodPlayer.stopPlay(true);
        }
        if (mIvCover != null) {
            mIvCover.setVisibility(View.VISIBLE);
        }
        hidePlayBtn();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlay2(position);
            }
        }, 100);
    }

    private void startPlay2(int position) {
        if (null != mRecyclerView.findViewHolderForAdapterPosition(position)) {
            VideoListAdapter.ViewHolder viewHolder = (VideoListAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            mTXCloudVideoView = viewHolder.mVideo;
            mIvCover = viewHolder.mCover;
//            mPlayerIvPause = viewHolder.mPause;

            if (mTXVodPlayer == null) {
                mTXVodPlayer = new TXVodPlayer(mContext);
                mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

                TXVodPlayConfig config = new TXVodPlayConfig();
                config.setCacheFolderPath(getInnerSDCardPath() + "/chatCache");
                config.setMaxCacheItems(10);
//                if (SystemUtils.isH265HWDecoderSupport()) {
//                    mTXVodPlayer.enableHardwareDecode(true);
//                }
                mTXVodPlayer.setConfig(config);
                mTXVodPlayer.setAutoPlay(false);
            }
            NLog.d("视频播放", "1");
            //FIXBUG:FULL_SCREEN 合唱显示不全,ADJUST_RESOLUTION 黑边
            int width = datas.get(position).getVideo_list().get(0).getExtra_info().getWidth();
            int height = datas.get(position).getVideo_list().get(0).getExtra_info().getHeight();
            if (width > height) {
                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            } else {
                mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            }
            mTXVodPlayer.setVodListener(this);
            NLog.d("视频播放", "21" + "\n" + ((mTXCloudVideoView instanceof TXCloudVideoView) ? "true" : "false"));
            String videoUrl = CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getObject());
            mTXVodPlayer.setPlayerView(mTXCloudVideoView);
            mTXVodPlayer.startPlay(videoUrl);
            NLog.d("视频播放", "2" + "\n" + videoUrl);
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
        return Environment.getExternalStorageDirectory().getPath();
    }

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isExit = false;
        resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isExit = true;
        pause();
    }

    private void pause() {
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
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }

        stopPlay(true);
        mTXVodPlayer = null;

        TelephonyUtil.getInstance().uninitPhoneListener();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        NLog.d("视频播放", "视频播放类型" + event);
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
            NLog.d("视频播放", "TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME");
            mIvCover.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            mTXVodPlayer.resume();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            NLog.d("视频播放", "TXLiveConstants.PLAY_EVT_PLAY_BEGIN");
            hidePlayBtn();
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

    private boolean isExit = false;

    private void getExit() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void videoList() {
        String body = "";
        try {
            if (!TextUtils.isEmpty(mUserID)) {
                body = new JsonBuilder()
                        .put("user_id", mUserID)
                        .put("page", mPage)
                        .build();
            } else {
                body = new JsonBuilder()
                        .put("dynamic_id", mDynamicId)
                        .put("page", mPage)
                        .build();
            }
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(mUrl, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
                if (errCode == 2201) {
                    DialogUitl.showSimpleHintDialog(GSYVideoActivity.this, getString(R.string.prompt), getString(R.string.ac_select_friend_sure),
                            getString(R.string.cancel), errInfo,
                            true, false,
                            new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {
                                    getExit();
                                }

                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    dialog.dismiss();
                                    getExit();
                                }
                            });
                }
            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;

                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
                if (result == null) {
                    return;
                }
                try {
                    VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);
                    if (mEmptyLayout != null) {
                        mEmptyLayout.hide();
                    }
                    if (mPage == 1) {
                        datas.clear();
                    }
                    datas.addAll(response.getList());
                    videoListAdapter.notifyDataSetChanged();

                    if (mPage == 1) {
                        startPlay(0);
                    } else {
                        //修复加载下页第一个视频无法播放问题
                        startPlay(currentPosition);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
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
                getExit();
            }
        });
    }

    @Override
    public void onRetry() {
        mPage = 1;
        videoList();

        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(true);
        mPage = 1;
        videoList();
    }

    private void dialogMore(boolean isVV) {

        final Dialog bottomDialog = new Dialog(GSYVideoActivity.this, R.style.BottomDialog);
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
                    intent.setPackage(getPackageName());
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
        DialogUitl.showSimpleHintDialog(this,
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
        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
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

    @Override
    public void follow(ImageView iv, int position) {
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
                }
            });
        }
    }

    private int sharePostion;

    @Override
    public void share(int position) {
        NLog.d("点击分享了", "3");
        sharePostion = position;
        try {
            //getUrl(CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getCover_img()), datas.get(position).getUser_nickname(), datas.get(position).getContent());
            VideoShare.getInstance().showShareDialog(this,datas.get(position));

           // showShareDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    public void opShop(int position) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        final Dialog bottomDialog = new Dialog(GSYVideoActivity.this, R.style.BottomDialog);
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
        initReplySwipeRefresh();

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
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
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
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
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

    private void initReplySwipeRefresh() {
        if (replyRefresh != null) {
            SwipeRefreshHelper.init(replyRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    replayList();
                }
            });
        }
    }
}
