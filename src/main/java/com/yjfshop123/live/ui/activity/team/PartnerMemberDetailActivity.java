package com.yjfshop123.live.ui.activity.team;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.PartnerMemberDetailResponse;
import com.yjfshop123.live.model.PartnerMemberResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的--我的团队--城市合伙人--合伙人会员明细---会员详情
 */
public class PartnerMemberDetailActivity extends BaseActivityForNewUi {
    PartnerMemberDetailResponse data;
    String phone;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.other_promotion)
    TextView otherPromotion;
    @BindView(R.id.phone_number)
    TextView phoneNumber;
    @BindView(R.id.team_total_count)
    TextView teamTotalCount;
    @BindView(R.id.medaling)
    TextView medaling;
    @BindView(R.id.active_num)
    TextView activeNum;
    @BindView(R.id.gold_egg_num)
    TextView goldEggNum;
    @BindView(R.id.silver_egg_num)
    TextView silverEggNum;
    @BindView(R.id.bad_egg_num)
    TextView badEggNum;
    @BindView(R.id.register_date)
    TextView registerDate;
    @BindView(R.id.recent_login)
    TextView recentLogin;
    @BindView(R.id.refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.partner_member_title));
        setContentView(R.layout.activity_partner_member_detail);
        ButterKnife.bind(this);
        if (getIntent() != null)
            phone = getIntent().getStringExtra("phone");

        initSwipeRefresh();
        getData();
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
        }
    }
    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }
    /**
     * 获取数据
     */
    public void getData() {
        if (TextUtils.isEmpty(phone)) return;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("mobile", phone)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/partner/memberDetail", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (errCode < 0) {
                    NToast.shortToast(PartnerMemberDetailActivity.this, getString(R.string.get_data_fail));
                } else
                    NToast.shortToast(PartnerMemberDetailActivity.this, errInfo);

                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                //模拟数据
                loadData(result);
            }
        });

    }

    public void loadData(String result) {

        if (TextUtils.isEmpty(result)) return;

        data = new Gson().fromJson(result, PartnerMemberDetailResponse.class);
        name.setText(data.name);
        phoneNumber.setText(data.phone);
        otherPromotion.setText(data.promotion_num);
        teamTotalCount.setText(data.team_total_num);
        activeNum.setText(data.activity_num);
        goldEggNum.setText(data.gold_egg);
        silverEggNum.setText(data.silver_egg);
        badEggNum.setText(data.bad_egg);
        registerDate.setText(data.register_date);
        recentLogin.setText(data.recent_login);
        if (data.medals != null && data.medals.length > 0) {
            String medalStr = "";
            for (int i = 0; i < data.medals.length; i++) {
                medalStr = data.medals[i].medal_des + data.medals[i].num + getString(R.string.ge);
            }
            medaling.setText(medalStr);
        }

    }

}
