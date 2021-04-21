package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.list.LiveListAdapter;
import com.yjfshop123.live.live.live.play.TCVodPlayerActivity;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration2;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MMFragment4 extends BaseFragment implements OnItemClickListener {

    private int page = 1;

    private List<LivingListResponse.LiveListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    @BindView(R.id.mm3_hint)
    TextView mm3_hint;

    private LiveListAdapter liveListAdapter;
    private GridLayoutManager mGridLayoutManager;

    private int width;
    private boolean isLoadingMore = false;
    private String user_id;
    private long mLastClickPubTS = 0;

    public void setResponse(String user_id){
        this.user_id = user_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_mm3;
    }

    @Override
    protected void initAction() {
        width = (CommonUtils.getScreenWidth(getActivity()) - CommonUtils.dip2px(mContext, 24)) / 2;
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration2(mContext));
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);

        liveListAdapter = new LiveListAdapter(mContext, width, this, true);
        shimmerRecycler.setAdapter(liveListAdapter);
    }

    @Override
    public void onItemClick(Object ob, int position) {
        if (System.currentTimeMillis() - mLastClickPubTS > 1000) {
            mLastClickPubTS = System.currentTimeMillis();

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
                        getVideoListByUid();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        getVideoListByUid();
    }

    private void getVideoListByUid(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .put("user_id", user_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/rebroadcast/getVideoListByUid", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1){
                    showHint();
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                if (result != null) {
                    try {
                        LivingListResponse response = JsonMananger.jsonToBean(result, LivingListResponse.class);
                        isLoadingMore = false;
                        if (page == 1) {
                            if (mList.size() > 0) {
                                mList.clear();
                            }
                        }

                        mList.addAll(response.getLive_list());
                        liveListAdapter.setCards(mList);
                        liveListAdapter.notifyDataSetChanged();

                        hideHint();

                        if (mList.size() == 0){
                            showHint();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (page == 1){
                            showNotData();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        page = 1;
        getVideoListByUid();
    }

    private void showHint(){
        mm3_hint.setVisibility(View.VISIBLE);
        shimmerRecycler.setVisibility(View.GONE);
    }

    private void hideHint(){
        mm3_hint.setVisibility(View.GONE);
        shimmerRecycler.setVisibility(View.VISIBLE);
    }
}
