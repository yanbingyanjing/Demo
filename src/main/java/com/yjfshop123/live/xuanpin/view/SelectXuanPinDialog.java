package com.yjfshop123.live.xuanpin.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcBuyOrderDetailActivity;
import com.yjfshop123.live.ctc.view.BuyEggDialog;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.CtcListResopnse;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderPayResponse;
import com.yjfshop123.live.shop.ziying.model.SubmitData;
import com.yjfshop123.live.shop.ziying.ui.AddressListActivity;
import com.yjfshop123.live.shop.ziying.ui.SubmitBuyOrderActivity;
import com.yjfshop123.live.ui.activity.yinegg.NewSilverEggAdapter;
import com.yjfshop123.live.ui.fragment.MyFragmentNewTwo;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.xuanpin.adapter.XunZhangAdapter;
import com.yjfshop123.live.xuanpin.ui.XuanPinActivity;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.TreeMap;

import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;

public class SelectXuanPinDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_xuanpin_select;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    TextView name;
    TextView price, egg_price;
    TextView release_days;
    TextView xuanpin_count;
    TextView income, address_name, address;
    RecyclerView list;
    TextView confir;
    ImageView close, jihuijia;
    LinearLayout jihuijia_ll, address_manage;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        close = mRootView.findViewById(R.id.close);
        price = mRootView.findViewById(R.id.price);
        name = mRootView.findViewById(R.id.name);
        jihuijia_ll = mRootView.findViewById(R.id.jihuijia_ll);
        egg_price = mRootView.findViewById(R.id.egg_price);
        jihuijia = mRootView.findViewById(R.id.jihuijia);
        release_days = mRootView.findViewById(R.id.release_days);
        xuanpin_count = mRootView.findViewById(R.id.xuanpin_count);
        income = mRootView.findViewById(R.id.income);
        list = mRootView.findViewById(R.id.list);
        confir = mRootView.findViewById(R.id.confir);
        address_manage = mRootView.findViewById(R.id.address_manage);
        address_name = mRootView.findViewById(R.id.address_name);
        address = mRootView.findViewById(R.id.address);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        list.setLayoutManager(mLinearLayoutManager);
        priceAdapter = new XunZhangAdapter(mContext);
        priceAdapter.setOnClick(new XunZhangAdapter.OnClick() {
            @Override
            public void Click(int position) {
                if (data != null && data.medals != null && position < data.medals.size()) {
                    medalIten = data.medals.get(position);
                    income.setText(NumUtil.clearZero(medalIten.medal_income) + "个金蛋");
                    medalId = medalIten.medal_id;
                    if (!TextUtils.isEmpty(medalIten.medal_exchange) && !TextUtils.isEmpty(data.price)) {
                        String count = new BigDecimal(medalIten.medal_exchange).multiply(new BigDecimal(eggPrice)).divide(new BigDecimal(data.price), 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                        xuanpin_count.setText(count);
                        if (new BigDecimal(xuanpin_count.getText().toString()).compareTo(new BigDecimal("1")) < 0) {
                            jihuijia.setSelected(false);
                            Glide.with(mContext).load(R.mipmap.huangse_kuangzi).into(jihuijia);
                        }
                    }

                }
            }
        });
        list.setAdapter(priceAdapter);
        address_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressListActivity.class);
                startActivity(intent);
            }
        });
        jihuijia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medalIten.medal_level==0){
                    //体验金蛋不可以寄回
                    NToast.shortToast(mContext,"体验选品不可寄回");
                    return;
                }
                if (jihuijia.isSelected()) {
                    jihuijia.setSelected(false);
                    Glide.with(mContext).load(R.mipmap.huangse_kuangzi).into(jihuijia);

                } else {
                    if (new BigDecimal(xuanpin_count.getText().toString()).compareTo(new BigDecimal("1")) < 0) {
                        NToast.shortToast(mContext, "寄送选品数量至少1个");
                    } else
                        DialogUitl.showSimpleDialog(getContext(),
                                "到期后您的选品：" + xuanpin_count.getText().toString() + data.name + "将寄送到自营商城中的默认收货地址",
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {
                                    }

                                    @Override
                                    public void onConfirmClick(Dialog dialog1, String content) {
                                        if (currentAddress == null) {
                                            DialogUitl.showSimpleDialog(getContext(),
                                                    "尚未设置默认寄送地址或地址不可用，是否打开地址管理",
                                                    new DialogUitl.SimpleCallback2() {
                                                        @Override
                                                        public void onCancelClick() {
                                                        }

                                                        @Override
                                                        public void onConfirmClick(Dialog dialog1, String content) {
                                                            Intent intent = new Intent(getActivity(), AddressListActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                        }
                                        jihuijia.setSelected(true);
                                        Glide.with(mContext).load(R.mipmap.huangse_gouzi).into(jihuijia);
                                    }
                                });


                }
            }
        });
        initData();
    }

    public void getAddressData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        HttpUtil.getInstance().getAsynHttpNoSign(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {

                initAddressData(response);
            }

            @Override
            public void onFailure(int what, String error) {

            }
        }, HttpUtil.ziying_shop_buy_now_thrd_url, paraMap);
    }

    DefaultAddress.AddressData currentAddress;

    private void initAddressData(String result) {
        if (TextUtils.isEmpty(result)) return;

        DefaultAddress defaultAddress = new Gson().fromJson(result, DefaultAddress.class);

        if (defaultAddress != null && !TextUtils.isEmpty(defaultAddress.code) && defaultAddress.code.equals("error")) {
            address.setVisibility(View.GONE);
            currentAddress=null;
            address_name.setText("新增收货地址");
            return;
        }
        currentAddress = defaultAddress.data;
        address.setVisibility(View.VISIBLE);
        address.setText(defaultAddress.data.region);
        address_name.setText(defaultAddress.data.name + " " + defaultAddress.data.telephone);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAddressData();
    }

    XuanPInResopnse.MedalIten medalIten;
    String medalId = "";
    XunZhangAdapter priceAdapter;

    private void initData() {
        if (data == null) return;
        egg_price.setText(NumUtil.clearZero(eggPrice) + "元/个");
        price.setText(NumUtil.clearZero(data.price) + "元/" + data.price_unit);
        name.setText(data.name);
        release_days.setText(data.medal_total_release_day + "天");
        priceAdapter.setHeadData(data.medals);
    }

    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }

    String eggPrice = "0";

    public void setData(XuanPInResopnse.ItemData data, String eggPrice) {
        this.data = data;
        if (TextUtils.isEmpty(eggPrice)) eggPrice = "0";
        this.eggPrice = eggPrice;
    }

    XuanPInResopnse.ItemData data;

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.confir) {
            buyMedal();
            return;

        }
    }

    /**
     * 获取数据
     */
    public void buyMedal() {

        LoadDialog.show(getContext());
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("medal_id", medalId)
                    .put("xuanpin_id", data.id)
                    .put("count", "1")
                    .put("is_buy",jihuijia.isSelected()?"1":"0" )
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/medal/buyMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                LoadDialog.dismiss(getContext());
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
                if (data == null) return;
                XuanpinBuySuccessDialog fragment = new XuanpinBuySuccessDialog();
                fragment.setContent(xuanpin_count.getText().toString(), medalIten == null ? "0" : medalIten.medal_exchange, data.name);

                fragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "XuanpinBuySuccessDialog");
                dismiss();
                //  NToast.shortToast(mContext, "购买成功");

            }
        });

    }

    public class ResultData {
        public String order;
    }

}
