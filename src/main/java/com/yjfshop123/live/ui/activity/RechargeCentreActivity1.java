package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MyWalletResponse;
import com.yjfshop123.live.net.response.RechargeAmountListResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.RechargeCentreAdapter1;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RechargeCentreActivity1 extends BaseActivity implements RechargeCentreAdapter1.MoneyClickListener, View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.lvRecharge)
    ListView lvRecharge;
    TextView tvCoin;

    private RechargeCentreAdapter1 adapter1;
    private ArrayList<RechargeAmountListResponse.ListBean> lists = new ArrayList<>();

    private int type = 0;
    private int scene = 1;
    private static final String UTF_8 = "UTF-8";
    private String money;
    private String extra_money;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_center);
        ButterKnife.bind(this);
        setHeadLayout();
        setHeadRightTextVisibility(View.VISIBLE);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(getString(R.string.chongzhi_center));

        mHeadRightText.setText(getString(R.string.jiaoyi_jilu));

        token = UserInfoUtil.getToken();
    }

    private void initEvent() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.view_recharge_head, null);
        tvCoin = headerView.findViewById(R.id.tvCoin);
        lvRecharge.addHeaderView(headerView);

        mHeadRightText.setOnClickListener(this);

    }

    private void initData() {
        adapter1 = new RechargeCentreAdapter1(this, lists);
        lvRecharge.setAdapter(adapter1);
        adapter1.setMoneyClickListener(this);
        getMyWallet();

        EventBus.getDefault().register(this);
    }

    private void pay() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("scene", scene)
                    .put("type", type)
                    .put("token", token)
                    .put("android", "android")
                    .put("money", money)
                    .put("extra_money", extra_money)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest2("apph5/pay/paypage", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                String html = result;
                Intent intent = new Intent(RechargeCentreActivity1.this, PayWebViewActivity.class);
                intent.putExtra("html", html);
                intent.putExtra("option", "MyJin");
                startActivity(intent);
            }
        });
    }

    private void getMyWallet(){
        OKHttpUtils.getInstance().getRequest("app/user/myWallet", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    MyWalletResponse response = JsonMananger.jsonToBean(result, MyWalletResponse.class);
                    tvCoin.setText(response.getCoin() + "");
                    getRechargeAmountList();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getRechargeAmountList(){
        OKHttpUtils.getInstance().getRequest("app/user/getRechargeAmountList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    RechargeAmountListResponse response = JsonMananger.jsonToBean(result, RechargeAmountListResponse.class);
                    lists.addAll(response.getList());
                    adapter1.notifyDataSetChanged();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void moneyClick(View view, int position) {
        RechargeAmountListResponse.ListBean bean = lists.get(position);
        money = bean.getMoney();
        extra_money = bean.getExtra_money();
        pay();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_right:
                Intent intent = new Intent(mContext, JiaoYiRecodeActivity.class);
                intent.putExtra("TYPE", "CZ");
                startActivity(intent);
                break;
        }
    }

    @Subscriber(tag = Config.EVENT_START)
    public void getMessage(String option) {
        if (option.equals("payRecharge")) {
            lists.clear();
            initData();
//            final IOSAlertDialog dialog = new IOSAlertDialog(RechargeCentreActivity1.this).builder();
//            dialog.setTitle(getString(R.string.vip_alert_title));
//            dialog.setMsg(getString(R.string.vip_alert_msg));
//            dialog.setPositiveButton(getString(R.string.wszl_zhidao), R.color.color_ffd100, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dissmiss();
//                }
//            });
//            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
