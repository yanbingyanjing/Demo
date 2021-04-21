package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.model.LevelResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.adapter.LevelAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 升级的界面
 */
public class LevelActivity extends BaseActivityH implements OnItemClickListener {
    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.head)
    CircleImageView head;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.update_des)
    TextView updateDes;
    private LinearLayoutManager mLinearLayoutManager;
    private LevelAdapter adapter;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        statusBarView.setLayoutParams(params);
        initSwipeRefresh();
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        adapter = new LevelAdapter(mContext, this);
        shimmerRecycler.setAdapter(adapter);
        loadData();
    }
    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    @Override
    public void onItemClick(Object bean, int position) {
        if (bean == null) return;
        if (bean instanceof LevelResponse.LevelData) {
            LoadDialog.show(this);
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("id", ((LevelResponse.LevelData) bean).id)
                        .build();
            } catch (JSONException e) {
            }

            OKHttpUtils.getInstance().getRequest("app/user/updatelv", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    LoadDialog.dismiss(LevelActivity.this);
                    NToast.shortToast(mContext, errInfo);
                }

                @Override
                public void onSuccess(String result) {
                    LoadDialog.dismiss(LevelActivity.this);
                    NToast.shortToast(mContext, getString(R.string.lv_update_success));
                }
            });
        }

    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
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

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/user/levelList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNoNetData();
                }
                //  initDatas(LevelResponse.testData);

            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }


    LevelResponse levelResponse;
    LevelResponse.LevelData levelData;
    private void initDatas(String result) {
        hideLoading();

        finishRefresh();
        if (mEmptyLayout != null) {
            mEmptyLayout.hide();
        }
        if (TextUtils.isEmpty(result)) return;
        levelResponse = new Gson().fromJson(result, LevelResponse.class);
        if (levelResponse == null) return;
        if (levelResponse.list != null && levelResponse.list.length == 0) {
            showNotData();
        }
         if(levelResponse.user!=null){
             name.setText(levelResponse.user.name);
             updateDes.setText(levelResponse.user.update_desc);
             des.setText(getString(R.string.jiarujitian,levelResponse.user.join_days+" "));
             if (!TextUtils.isEmpty(levelResponse.user.avatar)) {
                 Glide.with(mContext)
                         .load(CommonUtils.getUrl(levelResponse.user.avatar))
                         .into(head);
             }
             level.setText(levelResponse.user.level_name+levelResponse.user.level_title);
//             if (levelResponse.user.level==0) {
//                 level.setText("LV"+levelResponse.user.level+"流量用户");
//
//             }
//             if (levelResponse.user.level==1) {
//                 level.setText("LV"+levelResponse.user.level+"助理选品师");
//
//             }
//             if (levelResponse.user.level==2) {
//                 level.setText("LV"+levelResponse.user.level+"初级选品师");
//
//             }
//             if (levelResponse.user.level==3) {
//                 level.setText("LV"+levelResponse.user.level+"中级选品师");
//
//             }
//             if (levelResponse.user.level==4) {
//                 level.setText("LV"+levelResponse.user.level+"高级选品师");
//
//             }
//             if (levelResponse.user.level==5) {
//                 level.setText("LV"+levelResponse.user.level+"资深选品师");
//
//             }
//             if (levelResponse.user.level==6) {
//                 level.setText("LV"+levelResponse.user.level+"核心选品师");
//
//             }
//             if (levelResponse.user.level==7) {
//                 level.setText("LV"+levelResponse.user.level+"高级选品总监");
//
//             }
//             if (levelResponse.user.level==8) {
//                 level.setText("LV"+levelResponse.user.level+"资深选品总监");
//
//             }
//             if (levelResponse.user.level==9) {
//                 level.setText("LV"+levelResponse.user.level+"首席选品官");
//             }

         }
        adapter.setCards(levelResponse.list);
    }
}
