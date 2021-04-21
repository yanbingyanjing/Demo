package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.AccountantResponse;
import com.yjfshop123.live.model.MyEggResponse;

import com.yjfshop123.live.otc.MainOtcActivity;
import com.yjfshop123.live.otc.ui.OtcGoldActivity;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.update.InstallExchangeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yjfshop123.live.ui.activity.EggOrderListActivity.EGG_GOLD;
import static com.yjfshop123.live.ui.activity.TurnOutEggActivity.GOLD_TYPE;

public class GoldEggActivity extends BaseActivityForNewUi {
    @BindView(R.id.total_num)
    TextView totalNum;

    @BindView(R.id.medaling_num)
    TextView medalingNum;

    @BindView(R.id.exchange_num)
    TextView exchangeNum;
    @BindView(R.id.addition_num)
    TextView additionNum;


    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    int goldIndex = 0;
    int goldOutIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.my_gold_egg));
        setContentView(R.layout.activity_my_goldegg);
        ButterKnife.bind(this);
        setHeadRightTextVisibility(View.VISIBLE);
        mHeadRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(GoldEggActivity.this, EggOrderListActivity.class);
                intent3.putExtra("eggType", EGG_GOLD);
                startActivity(intent3);
            }
        });
        mHeadRightText.setText(getString(R.string.trade_list));
        initSwipeRefresh();
        initView();
        initListener();
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(true);
        }
        loadData();
    }

    private void initView() {
//
//        String[] incomeTitle = getResources().getStringArray(R.array.shouzhi_title);
//        String[] outTitle = getResources().getStringArray(R.array.shouzhi_out_title);
//        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[0]));
//        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[1]));
//        goldTabbar.addTab(goldTabbar.newTab().setText(incomeTitle[2]));
//        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[0]));
//        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[1]));
//        goldOutTabbar.addTab(goldOutTabbar.newTab().setText(outTitle[2]));
//
//        goldAddition.setText(String.format("%s%s", getString(R.string.addition), "0"));
//        goldTarget.setText(String.format("%s%s", getString(R.string.target), "0"));
//        goldReward.setText(String.format("%s%s", getString(R.string.reward), "0"));
//        goldBoost.setText(String.format("%s%s", getString(R.string.boost), "0"));
//        goldMedaling.setText(String.format("%s%s", getString(R.string.medaling), "0"));
//        goldBonus.setText(String.format("%s%s", getString(R.string.bonus), "0"));
//        goldIncomeCoin.setText(String.format("%s%s", getString(R.string.income_coin), "0"));
//
//        goldExchangeMedals.setText(String.format("%s%s", getString(R.string.exchange_medals), "0"));
//        goldFee.setText(String.format("%s%s", getString(R.string.fee), "0"));
//        goldSendReward.setText(String.format("%s%s", getString(R.string.send_reward), "0"));
//        goldTurnoutCoin.setText(String.format("%s%s", getString(R.string.turnout_coin), "0"));


    }


    private void initListener() {
//        goldTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                goldIndex = tab.getPosition();
//                setGoldData();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        goldOutTabbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                goldOutIndex = tab.getPosition();
//                setGoldData();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


    }

    AccountantResponse.GoldIncome goldIncome;
    AccountantResponse.GoldIOut goldIOut;

    private void setGoldData() {
//        if (myEggResponse == null) return;
//
//        if (goldIndex == 0) {
//            goldIncome = myEggResponse.day_income;
//        }
//        if (goldIndex == 1) {
//            goldIncome = myEggResponse.week_income;
//        }
//        if (goldIndex == 2) {
//            goldIncome = myEggResponse.month_income;
//        }
//
//        if (goldOutIndex == 0) {
//            goldIOut = myEggResponse.day_out;
//        }
//        if (goldOutIndex == 1) {
//            goldIOut = myEggResponse.week_out;
//        }
//        if (goldOutIndex == 2) {
//            goldIOut = myEggResponse.month_out;
//        }
//        if (goldIncome != null) {
//            goldAddition.setText(String.format("%s%s", getString(R.string.addition), NumUtil.dealNum(goldIncome.addition)));
//            goldTarget.setText(String.format("%s%s", getString(R.string.target), NumUtil.dealNum(goldIncome.target)));
//            goldReward.setText(String.format("%s%s", getString(R.string.reward), NumUtil.dealNum(goldIncome.reward)));
//            goldBoost.setText(String.format("%s%s", getString(R.string.boost), NumUtil.dealNum(goldIncome.boost)));
//            goldMedaling.setText(String.format("%s%s", getString(R.string.medaling), NumUtil.dealNum(goldIncome.medaling)));
//            goldBonus.setText(String.format("%s%s", getString(R.string.bonus), NumUtil.dealNum(goldIncome.bonus)));
//            goldIncomeCoin.setText(String.format("%s%s", getString(R.string.income_coin), NumUtil.dealNum(goldIncome.income_coin)));
//        }
//        if (goldIOut != null) {
//            goldExchangeMedals.setText(String.format("%s%s", getString(R.string.exchange_medals), NumUtil.dealNum(goldIOut.exchange_medals)));
//            goldFee.setText(String.format("%s%s", getString(R.string.fee), NumUtil.dealNum(goldIOut.fee)));
//            goldSendReward.setText(String.format("%s%s", getString(R.string.send_reward), NumUtil.dealNum(goldIOut.send_reward)));
//            goldTurnoutCoin.setText(String.format("%s%s", getString(R.string.turnout_coin), NumUtil.dealNum(goldIOut.turnout_coin)));
//        }
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
        OKHttpUtils.getInstance().getRequest("app/egg/myGoldEgg", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                //  NToast.shortToast(mContext, getString(R.string.get_data_fail));
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    MyEggResponse myEggResponse;

    private void initDatas(String result) {
        finishRefresh();

        if (TextUtils.isEmpty(result)) return;
        myEggResponse = new Gson().fromJson(result, MyEggResponse.class);
        if (myEggResponse == null) return;
        totalNum.setText(NumUtil.clearZero(myEggResponse.total));
        medalingNum.setText(NumUtil.clearZero(myEggResponse.medaling));
        exchangeNum.setText(NumUtil.clearZero(myEggResponse.can_exchange));
        additionNum.setText(NumUtil.clearZero(myEggResponse.addition));

        setGoldData();
    }

    InstallExchangeDialog dialog;

    @OnClick({R.id.turnin_egg, R.id.turn_out_egg,R.id.jiacheng_btn, R.id.suocang_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jiacheng_btn:
                startActivity( new Intent(this, AdditionActivity.class));
                break;
            case R.id.suocang_btn:
                startActivity( new Intent(this, MedalingActivity.class));
                break;
//
//            case R.id.show_medaling_detail:
//                startActivity( new Intent(this, MedalingActivity.class));
//                break;
//            case R.id.show_addition_detail:
//                startActivity( new Intent(this, AdditionActivity.class));
//                break;
            case R.id.turnin_egg:
//                ShowImgDialog dialogFragment = new ShowImgDialog();
//                dialogFragment.setOnClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (CheckInstalledUtil.checkAppInstalled(GoldEggActivity.this, Const.exchangePkg)) {
//                            CheckInstalledUtil.openPackage(GoldEggActivity.this, Const.exchangePkg);
//
//                        } else {
//                            onCheckIsInstallExchange();
//                        }
//                    }
//                });
//
//                dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");
                startActivity( new Intent(this, MainOtcActivity.class));
                break;
            case R.id.turn_out_egg:
                Intent intent = new Intent(this, TurnOutEggActivity.class);
                intent.putExtra("type", GOLD_TYPE);
                startActivity(intent);
                break;
        }
    }


}
