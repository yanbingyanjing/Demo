package com.yjfshop123.live.taskcenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.adapter.SucaiAdapter;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.widget.SexSelectDialogFragment;
import com.yjfshop123.live.ui.widget.SucaiSaveTipsDialog;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.SaveNetPhotoUtils;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DangeSucaiActivity extends BaseActivityForNewUi implements CommunityClickListener {


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
    int id=-1;
    int template_id=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTooBarBack(R.drawable.bg_gradient_faf7ed_f4ecd7);
        setBlackColorTooBar();
        setCenterTitleText(getString(R.string.xuanchuan_sucai));
        setContentView(R.layout.acticity_sucai);

        id = getIntent().getIntExtra("id", -1);
        template_id = getIntent().getIntExtra("template_id", -1);
        ButterKnife.bind(this);
        // initSwipeRefresh();
        initSwipeRefresh();
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new SucaiAdapter(mContext);
        adapter.setmItemClickListener(this);
        shimmerRecycler.setAdapter(adapter);
//        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem;
//                int totalItemCount;
//                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
//                totalItemCount = mLinearLayoutManager.getItemCount();
//
//                //表示剩下4个item自动加载，各位自由选择
//                // dy>0 表示向下滑动
//                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
//                    if (!isLoadingMore) {
//                        isLoadingMore = true;
//                        page++;
//                        loadData();
//                    }
//                }
//            }
//        });
        loadData();
    }

    @Override
    public void onHeadRightButtonClick(View v) {
        super.onHeadRightButtonClick(v);
        if (selectSucai == null) {
            NToast.shortToast(this, "请选择素材");
            return;
        }

        Intent intent = new Intent(this, UploadSucaiAcitivity.class);
        intent.putExtra("id",id);
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
            "\"content\":\"内容1\",\n" +
            "\"pic_list\":[\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\",\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\",\n" +
            "\"https://img.alicdn.com/bao/uploaded/O1CN01bnCnba1keldCYlhb0_!!2-item_pic.png\"\n" +
            "],\n" +
            "\"title\":\"数据1\"\n" +
            "}\n" +
            "]\n" +
            "}";

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("template_id", template_id+"")
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/task/shareTemplate", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page > 1)
                    page--;
                //模拟数据
                isLoadingMore = false;

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();;
                }else    hideLoading();

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
        if (page == 1&& data.list.length == 0) {
            showNotData();
        }
        mList.addAll(Arrays.asList(data.list));
        if (adapter != null) adapter.setCards(mList);
    }

    @Override
    public void onContentClick(View view, int position) {
        //复制文案
        if (mList == null || mList.get(position) == null) return;
        SystemUtils.setClipboard(this, mList.get(position).title + "\n" + mList.get(position).content + "\n推广链接：" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""));
    }

    @Override
    public void onPortraitClick(View view, int position) {
        if (mList == null || mList.get(position) == null) return;
        SystemUtils.setClipboardNoToast(this, mList.get(position).title + "\n" + mList.get(position).content + "\n推广链接：" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""));

        SaveNetPhotoUtils.savePhotoListForShare(this, mList.get(position).pic_list, mList.get(position).title + "\n" + mList.get(position).content + "\n推广链接：" + (mList.get(position).isShowShare ? mList.get(position).share_url : ""),
                new SaveNetPhotoUtils.OnsaveOk() {
                    @Override
                    public void onSave() {
                        //保存图片弹窗
                        SucaiSaveTipsDialog dialogFragment = new SucaiSaveTipsDialog();
                        dialogFragment.setClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!CheckInstalledUtil.isWeChatAppInstalled(DangeSucaiActivity.this)){
                                    NToast.shortToast(DangeSucaiActivity.this,"微信未安装，请先安装微信！");
                                    return;
                                }
                                Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setComponent(lan.getComponent());
                                startActivity(intent);
                            }
                        });
                        dialogFragment.show(getSupportFragmentManager(), "SucaiSaveTipsDialog");
                    }
                });


    }

    @Override
    public void onPraiseClick(View view, int position) {
        //选择该营销素材
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
