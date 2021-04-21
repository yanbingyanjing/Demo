package com.yjfshop123.live.ui.activity.team;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.PartnerDetailResponse;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.widget.InComeKlineView;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.DateFormatUtil;
import com.yjfshop123.live.utils.NumUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的--我的团队--城市合伙人--合伙人数据
 */
public class ParTnerDetailActivity extends BaseActivityForNewUi {
    PartnerDetailResponse detailResponse;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.city_user)
    TextView cityUser;
    @BindView(R.id.total_income)
    TextView totalIncome;
    @BindView(R.id.today_income)
    TextView todayIncome;
    @BindView(R.id.incom_detail)
    LinearLayout incomDetail;
    @BindView(R.id.user_data)
    LinearLayout userData;
    @BindView(R.id.month_gold_income)
    TextView monthGoldIncome;
    @BindView(R.id.start_time)
    TextView startTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.month_silver_income)
    TextView monthSilverIncome;
    @BindView(R.id.start_time_t)
    TextView startTimeT;
    @BindView(R.id.end_time_t)
    TextView endTimeT;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.gold_kline)
    InComeKlineView goldKline;
    @BindView(R.id.silver_kline)
    InComeKlineView silverKline;
    @BindView(R.id.zuori_activity)
    TextView zuoriActivity;
    @BindView(R.id.activity)
    TextView activity;
    @BindView(R.id.qianri_activity)
    TextView qianriActivity;
    @BindView(R.id.zuori_percent)
    TextView zuoriPercent;
    @BindView(R.id.jinri_percent)
    TextView jinriPercent;
    @BindView(R.id.max_huoyue)
    TextView maxHuoyue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.partner_detail_title));
        setContentView(R.layout.activity_partner_detail);
        ButterKnife.bind(this);
        initSwipeRefresh();
        monthGoldIncome.setText(getString(R.string.income_title, "0"));
        monthSilverIncome.setText(getString(R.string.income_title, "0"));
        startTime.setText(DateFormatUtil.getNextDate(-29));
        endTime.setText(DateFormatUtil.getCurTimeYMDString());
        startTimeT.setText(DateFormatUtil.getNextDate(-29));
        endTimeT.setText(DateFormatUtil.getCurTimeYMDString());
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

        OKHttpUtils.getInstance().getRequest("app/partner/detail", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                //initDatas(PartnerDetailResponse.testData);
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    private void initDatas(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) return;
        detailResponse = new Gson().fromJson(result, PartnerDetailResponse.class);
        if (detailResponse == null) return;
        city.setText(detailResponse.city);
        cityUser.setText(detailResponse.user_count);
        totalIncome.setText(getString(R.string.gold_egg) + " " + NumUtil.clearZero(detailResponse.gold_income) + "\n" + getString(R.string.yin_egg) + " " + NumUtil.clearZero(detailResponse.silver_income));
        todayIncome.setText(getString(R.string.gold_egg) + " " + NumUtil.clearZero(detailResponse.today_gold_income) + "\n" + getString(R.string.yin_egg) + " " + NumUtil.clearZero(detailResponse.today_silver_income));
        monthGoldIncome.setText(getString(R.string.income_title, NumUtil.clearZero(detailResponse.month_gold_income)));
        monthSilverIncome.setText(getString(R.string.income_title, NumUtil.clearZero(detailResponse.month_silver_income)));
        goldKline.setData(detailResponse.month_gold_income_data);
        silverKline.setData(detailResponse.month_silver_income_data);

maxHuoyue.setText(getString(R.string.lishi_max)+detailResponse.max_activity);
        activity.setText(NumUtil.clearZero(detailResponse.city_activity));
        zuoriActivity.setText(NumUtil.clearZero(detailResponse.yesterday_city_activity));
        qianriActivity.setText(NumUtil.clearZero(detailResponse.before_yesterday_city_activity));
        jinriPercent.setText(detailResponse.yesterday_up + "%");
        zuoriPercent.setText(detailResponse.before_yesterday_up + "%");
//        if (!TextUtils.isEmpty(detailResponse.yesterday_city_activity)) {
//            if (new BigDecimal(detailResponse.yesterday_city_activity).compareTo(BigDecimal.ZERO) > 0) {
//                BigDecimal per = (new BigDecimal(detailResponse.city_activity).subtract(new BigDecimal(detailResponse.yesterday_city_activity))).multiply(new BigDecimal(100))
//                        .divide(new BigDecimal(detailResponse.yesterday_city_activity), 4, BigDecimal.ROUND_DOWN);
//
//                jinriPercent.setText(per.stripTrailingZeros().toPlainString() + "%");
//            } else {
//                jinriPercent.setText("N/A");
//            }
//        } else {
//            jinriPercent.setText("N/A");
//        }
//        if (!TextUtils.isEmpty(detailResponse.before_yesterday_city_activity)) {
//            if (new BigDecimal(detailResponse.before_yesterday_city_activity).compareTo(BigDecimal.ZERO) > 0) {
//                BigDecimal perQiANri = (new BigDecimal(detailResponse.yesterday_city_activity).subtract(new BigDecimal(detailResponse.before_yesterday_city_activity))).multiply(new BigDecimal(100))
//                        .divide(new BigDecimal(detailResponse.before_yesterday_city_activity), 4, BigDecimal.ROUND_DOWN);
//
//                zuoriPercent.setText(perQiANri.stripTrailingZeros().toPlainString() + "%");
//            } else {
//                zuoriPercent.setText("N/A");
//            }
//        } else {
//            zuoriPercent.setText("N/A");
//        }


    }


    @OnClick({R.id.incom_detail, R.id.user_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.incom_detail:
                startActivity(new Intent(this, RebateActivity.class));
                break;
            case R.id.user_data:
                startActivity(new Intent(this, PartnerMemberActivity.class));
                break;
        }
    }
}
