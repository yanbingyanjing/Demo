package com.yjfshop123.live.live.live.list;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.play.TCVodPlayerActivity;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FragmentLiveList extends BaseFragment implements OnItemClickListener {

    private int page = 1;
    private int channel_id = 0;
    private boolean isHB = false;

    private List<LivingListResponse.LiveListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private LiveListAdapter liveListAdapter;
    private GridLayoutManager mGridLayoutManager;

    private int width;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            channel_id = bundle.getInt("channel_id", 0);
            isHB = bundle.getBoolean("ISHB");
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_live;
    }

    @Override
    protected void initAction() {
        width = (CommonUtils.getScreenWidth(getActivity()) - CommonUtils.dip2px(mContext, 24)) / 2;

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);

        liveListAdapter = new LiveListAdapter(mContext, width, this, isHB);
        shimmerRecycler.setAdapter(liveListAdapter);
    }

    @Override
    public void onItemClick(Object ob, final int position) {
        if (!isLogin()) {
            return;
        }
        if (System.currentTimeMillis() - mLastClickPubTS > 1000) {
            mLastClickPubTS = System.currentTimeMillis();

            //  if (isHB) {
            if (mList.get(position).getVod_type() == 2) {

                LivingListResponse.LiveListBean bean = mList.get(position);
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
                startLivePlay(mList.get(position));
            }
        }
    }

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mGridLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadLiveList();
                    }
                }
            }
        });
    }

    public void refresh() {
        page = 1;
        loadLiveList();
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        refresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            refresh();
        }
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
            refresh();
        }
        refresh();

    }

    private void loadLiveList() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            if (page == 1) {
                showNetError();
            }
            return;
        }

        String url;
        if (isHB) {
            url = "app/rebroadcast/getRebroadcastList";
        } else {
            url = "app/live/getLivingList";
        }
        String offset_id = "";
        if (page > 1 && mList.size() > 0) {
            offset_id = String.valueOf(mList.get(0).getLive_id());
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .put("channel_id", channel_id)//频道ID
                    .put("scene_id", 1)//场景id，可不填（1:直播首页调用 2:连麦中调用）
                    .put("offset_id", offset_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                hideLoading();
                if (page == 1) {
                    showNotData();
                }
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                hideLoading();
                if (result != null) {
                    try {
                        LivingListResponse launchLiveResponse = JsonMananger.jsonToBean(result, LivingListResponse.class);
                        isLoadingMore = false;
                        if (page == 1) {
                            if (mList.size() > 0) {
                                mList.clear();
                            }
                        }

                        mList.addAll(launchLiveResponse.getLive_list());
                        liveListAdapter.setCards(mList);

                        liveListAdapter.notifyDataSetChanged();

                        if (mList.size() == 0) {
                            showNotData();
                        }
                    } catch (Exception e) {
                        if (page == 1) {
                            showNotData();
                        }
                    }
                }
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

    private long mLastClickPubTS = 0;
    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
        refresh();
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
}