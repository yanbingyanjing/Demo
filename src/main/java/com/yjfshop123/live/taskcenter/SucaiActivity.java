package com.yjfshop123.live.taskcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.SucaiAdapter;
import com.yjfshop123.live.ui.adapter.TaskNewAdapter;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SaveNetPhotoUtils;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SucaiActivity extends BaseActivityForNewUi implements CommunityClickListener {


    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private SucaiAdapter adapter;
    int page = 1;
    boolean isLoadingMore = false;
    int id = -1;
    int template_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTooBarBack(R.drawable.bg_gradient_faf7ed_f4ecd7);
        setBlackColorTooBar();
        setCenterTitleText(getString(R.string.xuanchuan_sucai));
        setContentView(R.layout.acticity_sucai);
        setHeadRightTextVisibility(View.VISIBLE);
        id = getIntent().getIntExtra("id", -1);
        template_id = getIntent().getIntExtra("template_id", -1);
        mHeadRightText.setText("????????????");
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeadRightButtonClick(v);
            }
        });
        ButterKnife.bind(this);
        // initSwipeRefresh();
        initSwipeRefresh();
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new SucaiAdapter(mContext);
        adapter.setmItemClickListener(this);
        shimmerRecycler.setAdapter(adapter);
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager.getItemCount();

                //????????????4???item?????????????????????????????????
                // dy>0 ??????????????????
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadData();
                    }
                }
            }
        });
        loadData();
    }

    @Override
    public void onHeadRightButtonClick(View v) {
        super.onHeadRightButtonClick(v);
        if (selectSucai == null) {
            NToast.shortToast(this, "???????????????");
            return;
        }

        Intent intent = new Intent(this, UploadSucaiAcitivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void showLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, false);
        }
    }

    public void hideLoading() {
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.enableRefresh(mSwipeRefresh, true);
            SwipeRefreshHelper.controlRefresh(mSwipeRefresh, false);
        }
    }

    public void showNotData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_DATA);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();

                    loadData();
                }
            });
        }
        finishRefresh();
    }

    public void showNoNetData() {
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
            mEmptyLayout.setRetryListener(new EmptyLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    showLoading();
                    loadData();
                }
            });
        }
        finishRefresh();
    }

    public static String testData = "\n" +
            "{\n" +
            "\"list\":[\n" +
            "{\n" +
            "\"content\":\"??????1\",\n" +
            "\"pic_list\":[\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\",\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\",\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\"\n" +
            "],\n" +
            "\"title\":\"??????1\"\n" +
            "}\n" +
            "]\n" +
            "}";

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/task/shareTemplateList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page > 1)
                    page--;
                //????????????
                isLoadingMore = false;
                hideLoading();
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                }

            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                isLoadingMore = false;
                initData(result);
                //initData(testData);
            }
        });
    }

    ShareSucaiResopnse data;
    private List<ShareSucaiResopnse.Sucai> mList = new ArrayList<>();
    ShareSucaiResopnse.Sucai selectSucai;

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        if (page == 1)
            selectSucai = null;
        data = new Gson().fromJson(result, ShareSucaiResopnse.class);
        if (page > 1 && (data.list == null || data.list.length == 0)) {
            page--;
            return;
        }
        if (page == 1) {
            mList.clear();
        }
        mList.addAll(Arrays.asList(data.list));
        if (adapter != null) adapter.setCards(mList);
    }

    @Override
    public void onContentClick(View view, int position) {
        //????????????
        if (mList == null || mList.get(position) == null) return;
        SystemUtils.setClipboard(this, mList.get(position).title + "\n" + mList.get(position).content + "\n???????????????" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""));
    }

    @Override
    public void onPortraitClick(View view, int position) {
        if (mList == null || mList.get(position) == null) return;
        //????????????
        SaveNetPhotoUtils.savePhotoList(this, mList.get(position).pic_list);
    }

    @Override
    public void onPraiseClick(View view, int position) {
        //?????????????????????
        if (mList == null || mList.get(position) == null) return;
        selectSucai = mList.get(position);
        for (int i = 0; i < mList.size(); i++) {
            if (position == i) {

                mList.get(i).isSelect = true;
            } else {
                mList.get(i).isSelect = false;
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onImgClick(View view, int position, int index) {
        List<String> images = new ArrayList<>();
        if (mList != null && position < mList.size() && mList.get(position) != null && mList.get(position).pic_list != null) {
            images = Arrays.asList(mList.get(position).pic_list);
            try {
                Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                intent.putExtra(Config.POSITION, index);
                intent.putExtra("Picture", JsonMananger.beanToJson(images));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onVideo(TextureVideoView videoView, ImageView videoCover, ImageView videoBtn) {

    }

//    @OnClick(R.id.task_ll)
//    public void onViewClicked() {
//        if(data!=null&&!data.is_complete){
//            setResult(10001);
//            finish();
//        }
//    }
}
