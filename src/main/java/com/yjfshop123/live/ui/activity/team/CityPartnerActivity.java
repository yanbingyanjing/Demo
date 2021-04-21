package com.yjfshop123.live.ui.activity.team;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.CityPartnerConfigResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.widget.DialogToastFragment;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.update.InstallExchangeDialog;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//我的团队--合伙人
public class CityPartnerActivity extends BaseActivityForNewUi {
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.partner_detail)
    TextView partnerDetail;
    String vip_level;
    int level = 0;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.level_limit)
    TextView levelLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_partner);
        ButterKnife.bind(this);
        setCenterTitleText(getString(R.string.partner_title));
        if (getIntent() != null) {
            vip_level = getIntent().getStringExtra("vip_level");
            level = Integer.parseInt(vip_level);
        }
        initSwipeRefresh();
        getConfigData();
    }


    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getConfigData();
                }
            });
        }
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void getConfigData() {
        OKHttpUtils.getInstance().getRequest("app/partner/config", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                // initData(CityPartnerConfigResponse.testData);
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                initData(result);
            }
        });

    }

    String goldAdd = "0";
    String silverAdd = "0";
    String scaling = "";
    String apply_fee = "";
    int level_limit;
    CityPartnerConfigResponse cityPartnerConfigResponse;
    String level_limi_title = "";
    private void initData(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        cityPartnerConfigResponse = new Gson().fromJson(result, CityPartnerConfigResponse.class);
        apply_fee = cityPartnerConfigResponse.partner_apply_fee;
        level_limi_title=cityPartnerConfigResponse.level_limit_title;
        Glide.with(this).load(cityPartnerConfigResponse.level_image).into(logo);
        level_limit = Integer.parseInt(cityPartnerConfigResponse.level_limit);
        levelLimit.setText(cityPartnerConfigResponse.level_limit_title);
        partnerDetail.setText(cityPartnerConfigResponse.partner_apply_rule);


    }

    InstallExchangeDialog dialog;
    DialogToastFragment dialogFragment = new DialogToastFragment();

    @OnClick(R.id.apply)
    public void onViewClicked() {
        if (cityPartnerConfigResponse != null) {
            if (level < level_limit) {
                dialogFragment.setContent(getString(R.string.city_partner_levem_too_low_tips, level_limi_title, level_limi_title+""));
                dialogFragment.show(getSupportFragmentManager(), "DialogToastFragment");
                //if (dialog == null) {
//                dialog = new InstallExchangeDialog(this).builder();
//                dialog.setFocus(false)
////                .setFocus(false)
//                        .setPositiveButtonNormal(getString(R.string.dialog_reload), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialog.cancel();
//                            }
//                        })
//                        .setNegativeButton(null).setTitle(getString(R.string.city_partner_levem_too_low_tips,vip_level));
//            }
//            dialog.show();


                return;

            }
            Intent intent = new Intent(CityPartnerActivity.this, PartnerApplyActivity.class);
            intent.putExtra("apply_fee", apply_fee);
            startActivity(intent);
            finish();
        }else {
            NToast.shortToast(this,"未获取到配置信息，请下拉获取");
        }

    }
}
