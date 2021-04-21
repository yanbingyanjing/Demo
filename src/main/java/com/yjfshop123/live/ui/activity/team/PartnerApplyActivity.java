package com.yjfshop123.live.ui.activity.team;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.CountryListReponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.oss.CosXmlUtils;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;
import com.yjfshop123.live.ui.widget.ApplySuccessDialogFragment;
import com.yjfshop123.live.ui.widget.DialogToastFragment;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSApplyCityAlertDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSCityAlertDialog;
import com.yjfshop123.live.utils.MapUtil;
import com.yjfshop123.live.utils.update.InstallExchangeDialog;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//我的团队--合伙人--合伙人申请
public class PartnerApplyActivity extends BaseActivityForNewUi implements MapLoiIml {

    @BindView(R.id.city_select)
    TextView citySelect;
    @BindView(R.id.fee)
    TextView fee;
    @BindView(R.id.submit)
    Button submit;
    String apply_fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_apply);
        ButterKnife.bind(this);
        setCenterTitleText(getString(R.string.partner_apply_title));
        if (getIntent() != null) {
            apply_fee = getIntent().getStringExtra("apply_fee");
        }
        fee.setText(apply_fee);
        //startMap();
    }

    @OnClick({R.id.city_select_btn, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.city_select_btn:
                getCountryData();
                break;
            case R.id.submit:
                if (TextUtils.isEmpty(citySelect.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.please_select_city));
                    return;
                }
                String[] location = citySelect.getText().toString().split(",");
                if (location.length == 0) {
                    NToast.shortToast(this, "城市错误，请重新选择");
                } else if (location.length == 1) {
                    apply(location[0]);

                } else  {
                    apply(location[1]);
                }
                break;
        }
    }
    CountryListReponse countryListReponse;

    String[] countryList;

    private void getCountryData() {
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("app/login/getCountry", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
              //  NToast.shortToast(mContext, errInfo);
                LoadDialog.dismiss(PartnerApplyActivity.this);
                final IOSApplyCityAlertDialog alertDialog = new IOSApplyCityAlertDialog(PartnerApplyActivity.this,null);
                alertDialog.builder().setTitle(getString(R.string.wszl_city));
                alertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                alertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String city = alertDialog.getCityStr();
                        citySelect.setText(city);
                    }
                });
                alertDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(PartnerApplyActivity.this);
                if (TextUtils.isEmpty(result)) {
                    NToast.shortToast(mContext, "获取国家列表失败，请重试");
                    return;
                }
                countryListReponse = new Gson().fromJson(result, CountryListReponse.class);
                if (countryListReponse.list == null || countryListReponse.list.length == 0) {
                    NToast.shortToast(mContext, "获取国家列表失败，请重试");
                    return;
                }
                countryList = new String[countryListReponse.list.length];
                for (int i = 0; i < countryListReponse.list.length; i++) {
                    countryList[i] = countryListReponse.list[i].cn;
                }

                final IOSApplyCityAlertDialog alertDialog = new IOSApplyCityAlertDialog(PartnerApplyActivity.this,countryList);
                alertDialog.builder().setTitle(getString(R.string.wszl_city));
                alertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                alertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String city = alertDialog.getCityStr();
                        citySelect.setText(city);
                    }
                });
                alertDialog.show();
            }
        });

    }

    InstallExchangeDialog dialog;
    ApplySuccessDialogFragment dialogFragment = new ApplySuccessDialogFragment();

    private void apply(String city) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("city", city)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(this);
        OKHttpUtils.getInstance().getRequest("/app/partner/apply", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(PartnerApplyActivity.this);
                if (errCode < 0) {
                    NToast.shortToast(PartnerApplyActivity.this, getString(R.string.apply_partner_fail));
                } else
                    NToast.shortToast(PartnerApplyActivity.this, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(PartnerApplyActivity.this);
                //NToast.shortToast(PartnerApplyActivity.this, getString(R.string.gongxi_lingqu_success) + data.target_reward_value_unit + data.target_reward_value);
                dialogFragment.setContent(getString(R.string.dialog_reload));
                dialogFragment.show(getSupportFragmentManager(), "DialogToastFragment");

//                if (dialog == null) {
//                    dialog = new InstallExchangeDialog(PartnerApplyActivity.this).builder();
//                    dialog.setFocus(false)
////                .setFocus(false)
//                            .setPositiveButtonNormal(getString(R.string.dialog_reload), new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dialog.cancel();
//                                    Intent intent = new Intent();
//                                    intent.setClass(PartnerApplyActivity.this, HomeActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    ;
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton(null).setTitle(getString(R.string.partner_apply_success));
//                }
//                dialog.show();
            }
        });
    }

    private MapUtil mapUtil = new MapUtil(this, this);

    private void startMap() {
        mapUtil.LoPoi();
    }

    public static double latitude = 39.915;
    public static double longitude = 116.404;
    public static String address = "北京天安门";

    @Override
    public void onMapSuccess(double latitude, double longitude, String address, String currentWeizhi) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        if (TextUtils.isEmpty(currentWeizhi) || currentWeizhi.equals(",,")) {
            citySelect.setText("");
        } else {
            citySelect.setText(currentWeizhi);
        }
    }

    @Override
    public void onMapFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapUtil.destroy();
    }
}
