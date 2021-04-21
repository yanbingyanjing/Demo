package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;

import java.math.BigDecimal;

public class SubmitBuyOrderTongjiHolder extends RecyclerView.ViewHolder {


    private TextView yunfei, xiaoji;

    private TextView zongjia;
    private LinearLayout yunfei_ll;

    public SubmitBuyOrderTongjiHolder(View itemView) {
        super(itemView);

        zongjia = itemView.findViewById(R.id.zongjia);
        yunfei_ll = itemView.findViewById(R.id.yunfei_ll);
        yunfei = itemView.findViewById(R.id.yunfei);
        xiaoji = itemView.findViewById(R.id.xiaoji);
    }

    public void bind(Context context, OrderGoodsResponse.GoodsItem data) {
        if (data == null) return;
        if (!TextUtils.isEmpty(data.yun_fee)) {
            yunfei_ll.setVisibility(new BigDecimal(data.yun_fee).compareTo(BigDecimal.ZERO) > 0 ? View.VISIBLE : View.GONE);
            yunfei.setText(data.yun_fee);
        }
        zongjia.setText(data.total_money);
        xiaoji.setText(data.all_money);
    }
}