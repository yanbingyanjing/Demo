package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.ui.adapter.TaskAdapter;

public class OrderDetailHistoryHolder  extends RecyclerView.ViewHolder {


    private TextView history;
    private TextView history_time;


    public OrderDetailHistoryHolder(View itemView) {
        super(itemView);
        history = itemView.findViewById(R.id.history);
        history_time = itemView.findViewById(R.id.history_time);
    }

    public void bind(Context context, OrderDetail.HistoryItem data) {
        if (data == null) return;
        history.setText(data.title);
        history_time.setText(data.desc);
    }
}
