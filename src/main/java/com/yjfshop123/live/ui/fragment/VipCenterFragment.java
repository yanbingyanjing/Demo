package com.yjfshop123.live.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.VipCenterResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.PayWebViewActivity;
import com.yjfshop123.live.ui.adapter.VipCenterAdapter1;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;


public class VipCenterFragment extends BaseFragment implements VipCenterAdapter1.ItemClickListener {

    @BindView(R.id.lvVip)
    ListView lvVip;

    TextView tv_vip_expire_time;
    TextView tvBecomeVip;

    private ArrayList<VipCenterResponse.ListBean> listBean = new ArrayList<>();
    private VipCenterAdapter1 adapter1;

    private static final String UTF_8 = "UTF-8";

    private int type = 0;
    private int scene = 2;
    private String money;
    private String token;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_vip_center;
    }

    @Override
    protected void initAction() {
        getVipTypeList();
    }

    @Override
    protected void initEvent() {
        String userName = UserInfoUtil.getName();
        String avatar = UserInfoUtil.getAvatar();
        token = UserInfoUtil.getToken();

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.view_vip_center_head, null);
        View footerView = LayoutInflater.from(mContext).inflate(R.layout.view_vip_center_foot, null);

        tvBecomeVip = headerView.findViewById(R.id.tvBecomeVip);
        CircleImageView imgHead = headerView.findViewById(R.id.imgHead);
        TextView tvUsername = headerView.findViewById(R.id.tvUsername);
        tv_vip_expire_time = headerView.findViewById(R.id.tv_vip_expire_time);

        tvUsername.setText(userName);
        Glide.with(mContext).load(avatar).into(imgHead);

        lvVip.addHeaderView(headerView);
        lvVip.addFooterView(footerView);
        adapter1 = new VipCenterAdapter1(mContext, listBean);
        lvVip.setAdapter(adapter1);
        adapter1.setItemClickListener(this);
        setEvent();
    }

    private void setEvent() {
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
                    .put("extra_money", "0")
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
                Intent intent = new Intent(getActivity(), PayWebViewActivity.class);
                intent.putExtra("html", html);
                intent.putExtra("option", "MyJin");
                startActivity(intent);
            }
        });
    }

    private void getVipTypeList() {
        OKHttpUtils.getInstance().getRequest("app/user/getVipTypeList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    VipCenterResponse response = JsonMananger.jsonToBean(result, VipCenterResponse.class);
                    listBean.addAll(response.getList());
                    adapter1.notifyDataSetChanged();

                    if (response.getIs_vip().equals("1")) {
                        tvBecomeVip.setText(getString(R.string.vip_4));
                        tv_vip_expire_time.setText("会员于" + response.getVip_expire_time() + "到期");
                        tv_vip_expire_time.setVisibility(View.VISIBLE);
                    } else {
                        tvBecomeVip.setText(getString(R.string.vip_5));
                        tv_vip_expire_time.setVisibility(View.GONE);
                    }

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
    public void click(View view, int position) {
        VipCenterResponse.ListBean bean = listBean.get(position);
        type = position + 1;
        money = String.valueOf(bean.getMoney());
        pay();
    }

    @Subscriber(tag = Config.EVENT_START)
    public void getMessage(String option) {
        if (option.equals("vipRecharge")) {
            listBean.clear();
            UserInfoUtil.setIsVip(1);
            initEvent();
            DialogUitl.showSimpleHintDialog(mContext, getString(R.string.vip_alert_title), getString(R.string.vip_alert_msg),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
