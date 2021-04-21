package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yuyh.library.imgsel.bean.Image;

public class SubmitBuyOrderShopDetailHolder extends RecyclerView.ViewHolder {


    private TextView shop_name;
    private ImageView shop_logo;
    private TextView count;
    private TextView price;
    private TaskAdapter.MyItemClickListener mItemClickListener;

    public SubmitBuyOrderShopDetailHolder(View itemView) {
        super(itemView);

        shop_name = itemView.findViewById(R.id.shop_name);
        shop_logo = itemView.findViewById(R.id.shop_logo);
        count = itemView.findViewById(R.id.shop_count);
        price = itemView.findViewById(R.id.price);
    }

    public void bind(Context context, OrderGoodsResponse.GoodsItemItem data) {
        if (data == null) return;
        shop_name.setText(data.name);
        count.setText(data.spec_name+"\n"+"x"+data.quantity);
        price.setText(data.price);
        Glide.with(context).load(data.image).into(shop_logo);

    }
}
