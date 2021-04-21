package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.GameListResponse;
import com.yjfshop123.live.model.MyBadEggResponse;
import com.yjfshop123.live.ui.adapter.GameListAdapter;
import com.yjfshop123.live.ui.widget.DialogGameCoinExchangeFragment;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.ThirdGameUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_BAD;
import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_GOLD;

public class BadEggActivity extends BaseActivityH {

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.total_num)
    TextView totalNum;


    @BindView(R.id.text_right)
    TextView mHeadRightText;
    @BindView(R.id.game_list)
    RecyclerView gameList;
    @BindView(R.id.jinbi_num)
    TextView jinbiNum;
    @BindView(R.id.quan_num)
    TextView quanNum;
    @BindView(R.id.jinbi_tips)
    TextView jinbiTips;
    @BindView(R.id.tips)
    TextView tips;
    private LinearLayoutManager mLinearLayoutManager;
    private GameListAdapter gameListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bad_egg);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);

        ButterKnife.bind(this);
        StatusBarUtil.StatusBarDarkMode(this);
        mHeadRightText.setText(getString(R.string.trade_list));
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(BadEggActivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType", EGG_BAD);
                startActivity(intent3);
            }
        });

        initSwipeRefresh();
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(true);
        }
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        gameList.setLayoutManager(mLinearLayoutManager);
        gameListAdapter = new GameListAdapter(mContext);
        gameList.setAdapter(gameListAdapter);
        loadData();
        loadGameData();
        //同步账号
        ThirdGameUtil.getInstance().tongbuAccount(this, UserInfoUtil.getMiTencentId(), UserInfoUtil.getPhoneNumber());
        ThirdGameUtil.getInstance().fabi();
//        gameCenter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThirdGameUtil.getInstance().openGame(BadEggActivity.this, "111");
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        loadGameData();
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                    loadGameData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void loadData() {
        OKHttpUtils.getInstance().getRequest("app/silver_egg/mySmellyEgg", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    private void loadGameData() {
        OKHttpUtils.getInstance().getRequest("app/game/config", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                initGameDatas(result);
            }
        });
    }

    MyBadEggResponse myEggResponse;

    private void initDatas(String result) {
        finishRefresh();

        if (TextUtils.isEmpty(result)) return;
        myEggResponse = new Gson().fromJson(result, MyBadEggResponse.class);
        if (myEggResponse == null) return;
        totalNum.setText(String.format("%s%s", NumUtil.clearZero(myEggResponse.total), ""));
//        uncompleteTargetNum.setText(String.format("%s%s", NumUtil.clearZero(myEggResponse.uncomplete_target), ""));
//        uncompleteTaskNum.setText(String.format("%s%s", NumUtil.clearZero(myEggResponse.uncomplete_task), ""));

    }

    GameListResponse gameListResponse;

    private void initGameDatas(String result) {


        if (TextUtils.isEmpty(result)) return;
        gameListResponse = new Gson().fromJson(result, GameListResponse.class);
        gameListAdapter.setCards(gameListResponse.gameList);
        jinbiNum.setText(gameListResponse.coin);
        jinbiTips.setText(gameListResponse.coupon_desc);
        tips.setText(gameListResponse.coin_desc);
//        uncompleteTargetNum.setText(String.format("%s%s", NumUtil.clearZero(myEggResponse.uncomplete_target), ""));
//        uncompleteTaskNum.setText(String.format("%s%s", NumUtil.clearZero(myEggResponse.uncomplete_task), ""));

    }

    @OnClick({R.id.duihuan_record, R.id.duihuanjindan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.duihuan_record:
                Intent intent3 = new Intent(BadEggActivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType", EGG_GOLD);
                intent3.putExtra("index", 3);
                startActivity(intent3);
                break;
            case R.id.duihuanjindan:
                DialogGameCoinExchangeFragment dialogHomeActivitiesFragment = new DialogGameCoinExchangeFragment();
                dialogHomeActivitiesFragment.setBadEggActivity(this);
                dialogHomeActivitiesFragment.show(getSupportFragmentManager(), "DialogGameCoinExchangeFragment");
                break;
        }
    }
}
