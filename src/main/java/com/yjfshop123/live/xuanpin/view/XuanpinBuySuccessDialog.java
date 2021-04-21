package com.yjfshop123.live.xuanpin.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.xuanpin.adapter.XunZhangAdapter;

import org.json.JSONException;

import java.math.BigDecimal;

public class XuanpinBuySuccessDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private TextView count, egg_count, name;
    private String countStr, egg_countStr, nameStr;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_xuanpin_buy_success;
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
        params.width = SystemUtils.getScreenWidth(mContext)*3/4;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    TextView confir;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        count = mRootView.findViewById(R.id.count);
        egg_count = mRootView.findViewById(R.id.egg_count);
        name = mRootView.findViewById(R.id.xuanpin);

        confir = mRootView.findViewById(R.id.btn_confirm);
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        initData();
    }


    private void initData() {
        count.setText(countStr);
        egg_count.setText(egg_countStr);
        name.setText("选品："+nameStr);
    }

    public void setContent(String countStr, String egg_countStr, String nameStr) {
        this.countStr = countStr;
        this.egg_countStr = egg_countStr;
        this.nameStr = nameStr;
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
        if (i == R.id.btn_confirm) {
            return;

        }
    }


}
