package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.ui.adapter.TaskAdapter;

public class OrderDetailGoodHolder extends RecyclerView.ViewHolder {


    private TextView shop_name;
    private ImageView shop_logo;
    private TextView count;
    private TextView price, recharge_mobile;
    private TaskAdapter.MyItemClickListener mItemClickListener;
    private LinearLayout recharge_mobile_ll;

    public OrderDetailGoodHolder(View itemView) {
        super(itemView);
        recharge_mobile = itemView.findViewById(R.id.recharge_mobile);
        recharge_mobile_ll = itemView.findViewById(R.id.recharge_mobile_ll);
        shop_name = itemView.findViewById(R.id.shop_name);
        shop_logo = itemView.findViewById(R.id.shop_logo);
        count = itemView.findViewById(R.id.shop_count);
        price = itemView.findViewById(R.id.price);
    }

    public void bind(Context context, OrderDetail.GoodsItem data) {
        if (data == null) return;
        shop_name.setText(data.name);
        count.setText(data.spec + "\n" + "x" + data.quantity);
        price.setText(data.price);
        Glide.with(context).load(data.image).into(shop_logo);
        if (!TextUtils.isEmpty(data.kind)&&data.kind.equals("huafei")) {
            recharge_mobile_ll.setVisibility(View.VISIBLE);
            recharge_mobile.setText(data.recharge_mobile);
        }
    }
}
