package com.yjfshop123.live.shop.ziying.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.view.IOSZiyingCityDialog;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.utils.RouterUtil;
import com.yjfshop123.live.utils.StatusBarUtil;

import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressAddActivity extends BaseActivityForNewUi {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.detail_address)
    EditText detailAddress;
    @BindView(R.id.submit)
    TextView submit;
    IOSZiyingCityDialog alertDialog;
    String provinceId;
    String cityId;
    String districtId;
    boolean is_has_default = true;
    DefaultAddress.AddressData data;
    @BindView(R.id.delete)
    TextView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ziying_address);
        ButterKnife.bind(this);
        setBlackColorTooBar();
        setCenterTitleText(getString(R.string.add_address_titile));
        setTooBarBack(R.color.white);
        StatusBarUtil.StatusBarLightMode(this);
        is_has_default = getIntent().getBooleanExtra("is_has_default", true);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("data"))) {
            data = new Gson().fromJson(getIntent().getStringExtra("data"), DefaultAddress.AddressData.class);
            name.setText(data.name);
            phone.setText(data.telephone);
            delete.setVisibility(View.VISIBLE);
            detailAddress.setText(data.address);
        }
        alertDialog = new IOSZiyingCityDialog(this);
        alertDialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        alertDialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = alertDialog.getCityStr();
                address.setText(city);
                provinceId = alertDialog.getProvinceId();
                cityId = alertDialog.getCityId();
                districtId = alertDialog.getEareId();
            }
        });

    }


    @OnClick({R.id.address, R.id.submit, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
                DialogUitl.showSimpleDialog(mContext,
                        getString(R.string.confir_delete_shouhuo_address),
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                deleteData();
                            }
                        });
                break;
            case R.id.address:
                if (alertDialog != null) alertDialog.show();
                break;
            case R.id.submit:
                if (TextUtils.isEmpty(name.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.please_input_shoujianren_name));
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.hint_input_contact_phone));
                    return;
                }
                if (TextUtils.isEmpty(detailAddress.getText().toString())) {
                    NToast.shortToast(this, getString(R.string.please_detail_address));
                    return;
                }
                if (TextUtils.isEmpty(provinceId)) {
                    NToast.shortToast(this,  getString(R.string.please_suozai_diqu));
                    return;
                }
                if (data != null) {
                    fixAddress();
                } else
                    addAddress();
                break;
        }
    }

    /**
     * 获取数据
     */
    public void addAddress() {

        LoadDialog.show(this);
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("name", name.getText().toString());
        paraMap.put("tel", phone.getText().toString());
        paraMap.put("address", detailAddress.getText().toString());
        paraMap.put("provinceId", provinceId);
        paraMap.put("cityId", cityId);
        paraMap.put("districtId", districtId);
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                LoadDialog.dismiss(mContext);
                NLog.d("添加地址", response);
                // if (!form) dialog.dismiss();
                initAddCart(response);
            }

            @Override
            public void onFailure(int what, String error) {
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, error);
                //  if (!form) dialog.dismiss();
            }
        }, HttpUtil.ziying_shop_add_address_url, paraMap);

    }

    public void fixAddress() {

        LoadDialog.show(this);
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("name", name.getText().toString());
        paraMap.put("tel", phone.getText().toString());
        paraMap.put("address", detailAddress.getText().toString());
        paraMap.put("provinceId", provinceId);
        paraMap.put("cityId", cityId);
        paraMap.put("districtId", districtId);
        paraMap.put("id", data.address_id);
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                LoadDialog.dismiss(mContext);
                // if (!form) dialog.dismiss();
                AddCartResponse addCartResponse = new Gson().fromJson(response, AddCartResponse.class);

                if (!TextUtils.isEmpty(addCartResponse.code) && addCartResponse.code.equals("success")) {
                    NToast.shortToast(mContext, getString(R.string.fix_address_success));
                    finish();
                } else NToast.shortToast(mContext, getString(R.string.fix_fail));

            }

            @Override
            public void onFailure(int what, String error) {
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, error);
                //  if (!form) dialog.dismiss();
            }
        }, HttpUtil.ziying_shop_fix_address_url, paraMap);

    }

    public void initAddCart(String response) {
        if (TextUtils.isEmpty(response)) return;
        AddCartResponse addCartResponse = new Gson().fromJson(response, AddCartResponse.class);

        if (addCartResponse == null) return;
        if (TextUtils.isEmpty(addCartResponse.code) || !addCartResponse.code.equals("success")) {
            NToast.shortToast(mContext, addCartResponse.msg);
            return;
        }
        NToast.shortToast(mContext, getString(R.string.tianjia_success));
        finish();
    }


    public void deleteData() {
        if (data == null) return;
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("id", data.address_id);
        LoadDialog.show(this);
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                LoadDialog.dismiss(mContext);
                AddCartResponse addCartResponse = new Gson().fromJson(response, AddCartResponse.class);
                if (!TextUtils.isEmpty(addCartResponse.code) && addCartResponse.code.equals("success")) {
                    NToast.shortToast(mContext, getString(R.string.delete_address_success));
                    finish();
                } else NToast.shortToast(mContext, getString(R.string.delete_fail));


            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取的数据", error);
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, error);
            }
        }, HttpUtil.ziying_shop_delete_address_url, paraMap);
    }


}
