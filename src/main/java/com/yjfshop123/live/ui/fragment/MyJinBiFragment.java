package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.MyWalletResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.activity.ExpenditureDetailActivity;
import com.yjfshop123.live.ui.activity.InComeDetailActivity;
import com.yjfshop123.live.ui.activity.RechargeCentreActivity1;
import com.yjfshop123.live.ui.activity.TiXianActivity1;
import com.yjfshop123.live.ui.activity.WithdrawDetailActivity;

import butterknife.BindView;


public class MyJinBiFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tvTotalAibi)
    TextView tvTotalAibi;
    @BindView(R.id.tvRecharge)
    TextView tvRecharge;
    @BindView(R.id.tvWithdraw)
    TextView tvWithdraw;
    @BindView(R.id.tvRatio)
    TextView tvRatio;

    @BindView(R.id.splitView)
    View splitView;
    @BindView(R.id.viewComeInDetail)
    RelativeLayout viewComeInDetail;
    @BindView(R.id.viewComeOutDetail)
    RelativeLayout viewComeOutDetail;
    @BindView(R.id.viewWithdrawDetail)
    RelativeLayout viewWithdrawDetail;

    @BindView(R.id.comeInTotal)
    TextView comeInTotal;
    @BindView(R.id.comeOutTotal)
    TextView comeOutTotal;
    @BindView(R.id.withdrawTotal)
    TextView withdrawTotal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_jinbi;
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initEvent() {
        tvRecharge.setOnClickListener(this);
        tvWithdraw.setOnClickListener(this);
        viewComeInDetail.setOnClickListener(this);
        viewComeOutDetail.setOnClickListener(this);
        viewWithdrawDetail.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyWallet();
    }

    @Override
    protected void initData() {
        super.initData();
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
                    tvTotalAibi.setText(response.getCoin() + "");
                    tvRatio.setText(response.getCoin_note() + "");
                    comeInTotal.setText(getString(R.string.month_1) + response.getIncome_total_coin() + getString(R.string.my_jinbi));
                    comeOutTotal.setText(getString(R.string.month_2) + response.getPayout_total_coin() + getString(R.string.my_jinbi));
                    withdrawTotal.setText(getString(R.string.month_3) + response.getWithdraw_total_coin() + getString(R.string.my_jinbi));
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tvRecharge:
                intent.setClass(getActivity(), RechargeCentreActivity1.class);
                startActivity(intent);
                break;
            case R.id.tvWithdraw:
                intent.setClass(getActivity(), TiXianActivity1.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.viewComeInDetail:
                intent.setClass(getActivity(), InComeDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.viewComeOutDetail:
                intent.setClass(getActivity(), ExpenditureDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.viewWithdrawDetail:
                intent.setClass(getActivity(), WithdrawDetailActivity.class);
                startActivity(intent);
                break;
        }
    }
}
