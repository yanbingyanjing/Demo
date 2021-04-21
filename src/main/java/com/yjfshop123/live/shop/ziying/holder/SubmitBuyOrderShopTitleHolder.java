package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.ui.adapter.TaskAdapter;

public class SubmitBuyOrderShopTitleHolder  extends RecyclerView.ViewHolder {


    private TextView shop_name;

    private TaskAdapter.MyItemClickListener mItemClickListener;

    public SubmitBuyOrderShopTitleHolder(View itemView) {
        super(itemView);

        shop_name = itemView.findViewById(R.id.shop_name);

    }

    public void bind(Context context, String data) {
        if (data == null) return;
        shop_name.setText(data);
    }
}
