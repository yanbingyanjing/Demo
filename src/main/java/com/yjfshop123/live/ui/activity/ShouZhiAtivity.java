package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.AccountantResponse;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.NumUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_GOLD;

public class ShouZhiAtivity extends BaseActivityForNewUi {
    @BindView(R.id.total_income)
    TextView totalIncome;
    @BindView(R.id.total_out)
    TextView totalOut;
    @BindView(R.id.gold_tabbar)
    TabLayout goldTabbar;
    @BindView(R.id.gold_addition)
    TextView goldAddition;
    @BindView(R.id.gold_target)
    TextView goldTarget;
    @BindView(R.id.gold_reward)
    TextView goldReward;
    @BindView(R.id.gold_boost)
    TextView goldBoost;
    @BindView(R.id.gold_medaling)
    TextView goldMedaling;
    @BindView(R.id.gold_bonus)
    TextView goldBonus;
    @BindView(R.id.gold_income_coin)
    TextView goldIncomeCoin;
    @BindView(R.id.gold_out_tabbar)
    TabLayout goldOutTabbar;
    @BindView(R.id.gold_exchange_medals)
    TextView goldExchangeMedals;
    @BindView(R.id.gold_fee)
    TextView goldFee;
    @BindView(R.id.gold_send_reward)
    TextView goldSendReward;
    @BindView(R.id.gold_turnout_coin)
    TextView goldTurnoutCoin;
    @BindView(R.id.silver_tabbar)
    TabLayout silverTabbar;
    @BindView(R.id.silver_addition)
    TextView silverAddition;
    @BindView(R.id.silver_target)
    TextView silverTarget;
    @BindView(R.id.silver_boost)
    TextView silverBoost;
    @BindView(R.id.silver_income_coin)
    TextView silverIncomeCoin;
    @BindView(R.id.silver_out_tabbar)
    TabLayout silverOutTabbar;
    @BindView(R.id.silver_turnout_coin)
    TextView silverTurnoutCoin;
    @BindView(R.id.silver_fee)
    TextView silverFee;

    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    AccountantResponse accountantResponse;
    int goldIndex = 0;
    int goldOutIndex = 0;
    int silverIndex = 0;
    int silverOutIndex = 0;
    int badIndex = 0;
    @BindView(R.id.gold_fee_bonus)
    TextView goldFeeBonus;
    @BindView(R.id.gold_income_qita)
    TextView goldIncomeQita;
    @BindView(R.id.gold_shangcheng)
    TextView goldShangcheng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.shouzhifenxi));
        setContentView(R.layout.activity_shouzhifenxi);
        ButterKnife.bind(this);
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ShouZhiAtivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType", EGG_GOLD);
                startActivity(intent3);
            }
        });
        mHeadRightText.setText(getString(R.string.trade_list));
        String[] incomeTitle = getResources().getStringArray(R.array.shouzhi_title);
        String[] outTitle = getResources().getStringArray(R.array.shouzhi_out_title);
        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[0]));
        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[1]));
        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[2]));
        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[0]));
        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[1]));
        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[2]));


        silverTabbar.addTab(silverTabbar.newTab().setText(incomeTitle[0]));
        silverTabbar.addTab(silverTabbar.newTab().setText(incomeTitle[1]));
        silverTabbar.addTab(silverTabbar.newTab().setText(incomeTitle[2]));
        silverOutTabbar.addTab(silverOutTabbar.newTab().setText(outTitle[0]));
        silverOutTabbar.addTab(silverOutTabbar.newTab().setText(outTitle[1]));
        silverOutTabbar.addTab(silverOutTabbar.newTab().setText(outTitle[2]));

        initView();

        initListener();

        initSwipeRefresh();
        mSwipeRefresh.setRefreshing(true);
        loadData();
    }

    private void initListener() {
        goldTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                goldIndex = tab.getPosition();
                setGoldData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        goldOutTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                goldOutIndex = tab.getPosition();
                setGoldData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        silverTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                silverIndex = tab.getPosition();
                setSilverData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        silverOutTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                silverOutIndex = tab.getPosition();
                setSilverData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initView() {
        goldAddition.setText(String.format("%s %s", getString(R.string.addition), "0"));
        goldTarget.setText(String.format("%s %s", getString(R.string.target), "0"));
        goldReward.setText(String.format("%s %s", getString(R.string.reward), "0"));
        goldBoost.setText(String.format("%s %s", getString(R.string.boost), "0"));
        goldMedaling.setText(String.format("%s %s", getString(R.string.medaling), "0"));
        goldBonus.setText(String.format("%s %s", "选品收益", "0"));
        goldIncomeCoin.setText(String.format("%s %s", getString(R.string.income_coin), "0"));
        goldFeeBonus.setText(String.format("%s %s", "手续费分红", "0"));
        goldIncomeQita.setText(String.format("%s %s", "其他", "0"));

        goldExchangeMedals.setText(String.format("%s %s", getString(R.string.exchange_medals), "0"));
        goldFee.setText(String.format("%s %s", getString(R.string.fee), "0"));
        goldSendReward.setText(String.format("%s %s", getString(R.string.send_reward), "0"));
        goldTurnoutCoin.setText(String.format("%s %s", getString(R.string.turnout_coin), "0"));
goldShangcheng.setText(String.format("%s %s", "商城", "0"));

        silverAddition.setText(String.format("%s %s", getString(R.string.addition), "0"));
        silverTarget.setText(String.format("%s %s", getString(R.string.target), "0"));
        silverBoost.setText(String.format("%s %s", getString(R.string.boost), "0"));
        silverIncomeCoin.setText(String.format("%s %s", getString(R.string.income_coin), "0"));

        silverFee.setText(String.format("%s %s", getString(R.string.fee), "0"));
        silverTurnoutCoin.setText(String.format("%s %s", getString(R.string.turnout_coin), "0"));

        String stringBuilder = "0" +
                getString(R.string.gold_egg) +
                "  " +
                "0" +
                getString(R.string.yin_egg) +
                "  " +
                "0" +
                getString(R.string.chou_egg);
        totalIncome.setText(stringBuilder);

        String stringBuilderOut = "0" +
                getString(R.string.gold_egg) +
                "  " +
                "0" +
                getString(R.string.chou_egg);
        totalOut.setText(stringBuilderOut);


    }

    private void loadData() {
        OKHttpUtils.getInstance().getRequest("app/egg/accountTant", "", new RequestCallback() {
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

    AccountantResponse.GoldIncome goldIncome;
    AccountantResponse.GoldIOut goldIOut;
    AccountantResponse.SilverIncome silverIncome;
    AccountantResponse.SilverIOut silverIOut;

    private void initDatas(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) return;
        accountantResponse = new Gson().fromJson(result, AccountantResponse.class);
        if (accountantResponse == null) return;
        String stringBuilder = NumUtil.clearZero(accountantResponse.gold_total_income) +
                getString(R.string.gold_egg) +
                " " +
                NumUtil.clearZero(accountantResponse.silver_total_income) +
                getString(R.string.yin_egg) +
                " " +
                NumUtil.clearZero(accountantResponse.bad_total_income) +
                getString(R.string.chou_egg);
        totalIncome.setText(stringBuilder);

        String stringBuilderOut = NumUtil.clearZero(accountantResponse.gold_total_out) +
                getString(R.string.gold_egg) +
                " " +
                NumUtil.clearZero(accountantResponse.silver_total_out) +
                getString(R.string.yin_egg);
        totalOut.setText(stringBuilderOut);

        setGoldData();
        //setSilverData();
        // setBadrData();
    }

    private void setGoldData() {
        if (accountantResponse == null) return;
        if (accountantResponse.gold_egg == null) return;

        if (goldIndex == 0) {
            goldIncome = accountantResponse.gold_egg.day_income;
        }
        if (goldIndex == 1) {
            goldIncome = accountantResponse.gold_egg.week_income;
        }
        if (goldIndex == 2) {
            goldIncome = accountantResponse.gold_egg.month_income;
        }

        if (goldOutIndex == 0) {
            goldIOut = accountantResponse.gold_egg.day_out;
        }
        if (goldOutIndex == 1) {
            goldIOut = accountantResponse.gold_egg.week_out;
        }
        if (goldOutIndex == 2) {
            goldIOut = accountantResponse.gold_egg.month_out;
        }
        if (goldIncome != null) {
            goldAddition.setText(String.format("%s %s", getString(R.string.addition), NumUtil.dealNum(goldIncome.addition)));
            goldTarget.setText(String.format("%s %s", getString(R.string.target), NumUtil.dealNum(goldIncome.target)));
            goldReward.setText(String.format("%s %s", getString(R.string.reward), NumUtil.dealNum(goldIncome.reward)));
            goldBoost.setText(String.format("%s %s", getString(R.string.boost), NumUtil.dealNum(goldIncome.boost)));
            goldMedaling.setText(String.format("%s %s", getString(R.string.medaling), NumUtil.dealNum(goldIncome.unlock)));
            goldBonus.setText(String.format("%s %s", "选品收益", NumUtil.dealNum(goldIncome.bonus)));
            goldIncomeCoin.setText(String.format("%s %s", getString(R.string.income_coin), NumUtil.dealNum(goldIncome.income_coin)));
            goldFeeBonus.setText(String.format("%s %s", "手续费分红", NumUtil.dealNum(goldIncome.fee_bonus)));
            goldIncomeQita.setText(String.format("%s %s", "其他", NumUtil.dealNum(goldIncome.other_income)));

        }
        if (goldIOut != null) {
            goldExchangeMedals.setText(String.format("%s %s", getString(R.string.exchange_medals), NumUtil.dealNum(goldIOut.exchange_medals)));
            goldFee.setText(String.format("%s %s", getString(R.string.fee), NumUtil.dealNum(goldIOut.fee)));
            goldSendReward.setText(String.format("%s %s", getString(R.string.send_reward), NumUtil.dealNum(goldIOut.send_reward)));
            goldTurnoutCoin.setText(String.format("%s %s", getString(R.string.turnout_coin), NumUtil.dealNum(goldIOut.turnout_coin)));
            goldShangcheng.setText(String.format("%s %s","商城", NumUtil.dealNum(goldIOut.shop_out)));
        }
    }

    private void setSilverData() {
        if (accountantResponse == null) return;
        if (accountantResponse.silver_egg == null) return;
        if (silverIndex == 0) {
            silverIncome = accountantResponse.silver_egg.day_income;
        }
        if (silverIndex == 1) {
            silverIncome = accountantResponse.silver_egg.week_income;
        }
        if (silverIndex == 2) {
            silverIncome = accountantResponse.silver_egg.month_income;
        }

        if (silverOutIndex == 0) {
            silverIOut = accountantResponse.silver_egg.day_out;
        }
        if (silverOutIndex == 1) {
            silverIOut = accountantResponse.silver_egg.week_out;
        }
        if (silverOutIndex == 2) {
            silverIOut = accountantResponse.silver_egg.month_out;
        }

        if (silverIncome != null) {
            silverAddition.setText(String.format("%s %s", getString(R.string.addition), NumUtil.dealNum(silverIncome.addition)));
            silverTarget.setText(String.format("%s %s", getString(R.string.target), NumUtil.dealNum(silverIncome.target)));
            silverBoost.setText(String.format("%s %s", getString(R.string.boost), NumUtil.dealNum(silverIncome.boost)));
            silverIncomeCoin.setText(String.format("%s %s", getString(R.string.income_coin), NumUtil.dealNum(silverIncome.income_coin)));
        }
        if (silverIOut != null) {
            silverFee.setText(String.format("%s %s", getString(R.string.fee), NumUtil.dealNum(silverIOut.fee)));
            silverTurnoutCoin.setText(String.format("%s %s", getString(R.string.turnout_coin), NumUtil.dealNum(silverIOut.turnout_coin)));
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
}
