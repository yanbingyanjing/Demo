package com.yjfshop123.live.shop.ziying.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.adapter.NewShopBuyGuigeAdapter;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopDetail;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.shop.ziying.ui.SubmitBuyOrderActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.xuanpin.adapter.XunZhangAdapter;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.TreeMap;

public class BuyView extends AbsDialogFragment implements View.OnClickListener, NewShopBuyGuigeAdapter.OnSelectGuiGe {

    private View.OnClickListener mCallback;
    private String title;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ziying_buy;
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


    TextView price;
    int count = 1;
    int maxcount = 0;
    RecyclerView list;
    TextView confir, jian, add, kucun;
    ImageView close, shop_icon;
    EditText countTx;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        close = mRootView.findViewById(R.id.close);
        kucun = mRootView.findViewById(R.id.kucun);
        price = mRootView.findViewById(R.id.price);
        shop_icon = mRootView.findViewById(R.id.shop_icon);
        list = mRootView.findViewById(R.id.list);
        confir = mRootView.findViewById(R.id.confir);
        jian = mRootView.findViewById(R.id.jian);
        add = mRootView.findViewById(R.id.add);
        countTx = mRootView.findViewById(R.id.count);
        countTx.addTextChangedListener(new TextWatcher() {
            String befor;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                befor = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(befor) && !TextUtils.isEmpty(s.toString()) && befor.equals(s.toString()))
                    return;
                if (TextUtils.isEmpty(s.toString())) {
                    count = 0;
                    countTx.setText(count + "");
                    countTx.setSelection((count + "").length());
                } else {
                    count = Integer.valueOf(s.toString());
                }
                if (count > maxcount) {
                    count = maxcount;
                    countTx.setText(count + "");
                    countTx.setSelection((count + "").length());
                }

                if (count < 0) {
                    count = 0;
                    countTx.setText(count + "");
                    countTx.setSelection((count + "").length());
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        jian.setOnClickListener(this);
        add.setOnClickListener(this);
        mRootView.findViewById(R.id.confir).setOnClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(mLinearLayoutManager);
        priceAdapter = new NewShopBuyGuigeAdapter(mContext);
        priceAdapter.setSelect(this);
        list.setAdapter(priceAdapter);
        initData();
    }

    ZiyingShopDetail.SkumapItem skumapItem;//选中的规格
    String selectKey = "";
    String medalId = "";
    NewShopBuyGuigeAdapter priceAdapter;

    private void initData() {
        if (data == null) return;
        maxcount = data.goods.quantity;
        price.setText(NumUtil.clearZero(data.goods.price) + getString(R.string.gold_egg));
        Glide.with(mContext).load(data.goods.image).into(shop_icon);
        countTx.setText(count + "");
        setKucun();
        priceAdapter.setHeadData(data.spec);
    }

    public void setOnClick(View.OnClickListener callback) {
        mCallback = callback;
    }


    public void setData(ZiyingShopDetail data) {
        this.data = data;
    }

    public void setPintuan_id(String pintuan_id) {
        this.pintuan_id = pintuan_id;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    private String kind;
    private String pintuan_id;
    ZiyingShopDetail data;

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        switch (i) {
            case R.id.confir:

                buyMedal();

                break;
            case R.id.jian:
//                if (!TextUtils.isEmpty(kind) && kind.equals("huafei")) {
//                    NToast.shortToast(mContext,"该商品只能购买一件");
//                    return;
//                }
                count--;
                if (count < 0) count = 0;
                countTx.setText(count + "");
                break;
            case R.id.add:
//                if (!TextUtils.isEmpty(kind) && kind.equals("huafei")) {
//                    NToast.shortToast(mContext,"该商品只能购买一件");
//                    return;
//                }
                count++;
                if (count > maxcount) count = maxcount;
                countTx.setText(count + "");
                break;
        }

    }

    /**
     * 获取数据
     */
    public void buyMedal() {
        if (data != null && data.spec != null && data.spec.length > 0 && skumapItem == null) {
            NToast.shortToast(getContext(), getString(R.string.qing_select_guige));
            return;
        }
        if (maxcount == 0) {
            NToast.shortToast(getContext(), getString(R.string.kucun_buzu));
            return;
        }
        if (count == 0) {
            NToast.shortToast(getContext(), getString(R.string.xuanze_buy_count));
            return;
        }
        LoadDialog.show(getContext());
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("goods_id", data.goods.id);
        paraMap.put("buy_type", "2");
        paraMap.put("quantity", count + "");
        paraMap.put("sid", skumapItem == null ? "" : (skumapItem.id + ""));
        HttpUtil.getInstance().postAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                LoadDialog.dismiss(mContext);
                NLog.d("立即购买第一步", response);
                // if (!form) dialog.dismiss();
                initAddCart(response);
            }

            @Override
            public void onFailure(int what, String error) {
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, error);
                //  if (!form) dialog.dismiss();
            }
        }, HttpUtil.ziying_shop_buy_now_first_url, paraMap);

    }

    AddCartResponse addCartResponse;

    public void initAddCart(String response) {
        if (TextUtils.isEmpty(response)) return;
        addCartResponse = new Gson().fromJson(response, AddCartResponse.class);

        if (addCartResponse == null) return;
        if (TextUtils.isEmpty(addCartResponse.code) || !addCartResponse.code.equals("success")) {
            NToast.shortToast(mContext, addCartResponse.msg);
            return;
        }
        Intent intent = new Intent(mContext, SubmitBuyOrderActivity.class);
        intent.putExtra("cart_id", addCartResponse.cart_id);
        if (!TextUtils.isEmpty(pintuan_id))
            intent.putExtra("pintuan_id", pintuan_id);
        if (!TextUtils.isEmpty(kind))
            intent.putExtra("kind", kind);


        startActivity(intent);
        dismiss();
    }

    @Override
    public void onClick(int specIndex, int valueIndex) {

        if (data == null || data.spec == null || data.spec.length == 0) return;
        selectKey = "";
        for (int i = 0; i < data.spec.length; i++) {
            for (int j = 0; j < data.spec[i].value.length; j++) {
                if (specIndex == i) {
                    if (j == valueIndex) {
                        data.spec[i].value[j].active = true;
                    } else {
                        data.spec[i].value[j].active = false;
                    }
                }
                if (data.spec[i].value[j].active) {
                    selectKey = selectKey + ";" + data.spec[i].spec_id + ":" + data.spec[i].value[j].id;
                }
            }

        }
        if (priceAdapter != null) priceAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(selectKey)) {
            selectKey = selectKey + ";";
        } else return;
        if (data.skumap == null) {
            NToast.shortToast(mContext, getString(R.string.kucun_buzu));
            return;
        }
        for (int i = 0; i < data.skumap.length; i++) {
            if (data.skumap[i].specs_key.equals(selectKey)) {
                skumapItem = data.skumap[i];
                maxcount = data.skumap[i].quantity;
                kucun.setText(getString(R.string.kucun, data.skumap[i].quantity + ""));
                break;
            }
        }
    }

    private void setKucun() {
        kucun.setText(getString(R.string.kucun, data.goods.quantity + ""));
        if (data == null || data.spec == null || data.spec.length == 0) return;
        selectKey = "";
        for (int i = 0; i < data.spec.length; i++) {
            for (int j = 0; j < data.spec[i].value.length; j++) {
                if (data.spec[i].value[j].active) {
                    selectKey = selectKey + ";" + data.spec[i].spec_id + ":" + data.spec[i].value[j].id;
                }
            }

        }
        if (!TextUtils.isEmpty(selectKey)) {
            selectKey = selectKey + ";";
        } else {
            return;
        }
        if (data.skumap == null) {
            NToast.shortToast(mContext, getString(R.string.kucun_buzu));
            return;
        }
        for (int i = 0; i < data.skumap.length; i++) {
            if (data.skumap[i].specs_key.equals(selectKey)) {
                skumapItem = data.skumap[i];
                maxcount = data.skumap[i].quantity;
                kucun.setText(getString(R.string.kucun, data.skumap[i].quantity + ""));
                break;
            }
        }
    }

    public class ResultData {
        public String order;
    }

}
