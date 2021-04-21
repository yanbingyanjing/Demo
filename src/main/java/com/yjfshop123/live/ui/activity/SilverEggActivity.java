package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;

import com.yjfshop123.live.model.MySilverEggResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_SILVER;
import static com.yjfshop123.live.ui.activity.TurnOutEggActivity.GOLD_TYPE;
import static com.yjfshop123.live.ui.activity.TurnOutEggActivity.SILVER_TYPE;

public class SilverEggActivity extends BaseActivityForNewUi {
    @BindView(R.id.total_num)
    TextView totalNum;
    @BindView(R.id.can_use_num)
    TextView canUseNum;
    @BindView(R.id.total_income_num)
    TextView totalIncomeNum;
    @BindView(R.id.toady_income_num)
    TextView toadyIncomeNum;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.my_silver_egg));
        setContentView(R.layout.activity_silver_egg);
        ButterKnife.bind(this);
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(SilverEggActivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType",EGG_SILVER);
                startActivity(intent3);
            }
        });
        mHeadRightText.setText(getString(R.string.trade_list));
        initSwipeRefresh();
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(true);
        }
        loadData();

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

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/silver_egg/mySilverEgg", "", new RequestCallback() {
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

    MySilverEggResponse myEggResponse;

    private void initDatas(String result) {
        finishRefresh();

        if (TextUtils.isEmpty(result)) return;
        myEggResponse = new Gson().fromJson(result, MySilverEggResponse.class);
        if (myEggResponse == null) return;
        totalNum.setText(String.format("%s%s",  NumUtil.clearZero(myEggResponse.total), ""));
        canUseNum.setText(String.format("%s%s",  NumUtil.clearZero(myEggResponse.can_use), ""));
        totalIncomeNum.setText(String.format("%s%s",  NumUtil.clearZero(myEggResponse.total_income),""));
        toadyIncomeNum.setText(String.format("%s%s",  NumUtil.clearZero(myEggResponse.today_income), ""));
    }

    @OnClick(R.id.turnout_egg)
    public void onViewClicked() {
        Intent intent=new Intent(this,TurnOutEggActivity.class);
        intent.putExtra("type",SILVER_TYPE);
        startActivity(intent);
    }
}
