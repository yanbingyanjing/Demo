package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.shop.ziying.adapter.SubmitBuyOrderAdapter;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.utils.SystemUtils;

public class OrderDetailAddressHolder extends RecyclerView.ViewHolder {

    private TextView address, copy;
    private TextView name;
    private TextView status, transport_c_name, transport_num;
    private LinearLayout kuaid;

    public OrderDetailAddressHolder(View itemView, final SubmitBuyOrderAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);

        address = itemView.findViewById(R.id.address);
        name = itemView.findViewById(R.id.name);
        status = itemView.findViewById(R.id.status);
        transport_c_name = itemView.findViewById(R.id.transport_c_name);
        transport_num = itemView.findViewById(R.id.transport_num);
        kuaid = itemView.findViewById(R.id.kuaid);
        copy = itemView.findViewById(R.id.copy);
    }


    public void bind(Context context, final OrderDetail.Logistics data, String statusStr) {

        if (data == null) {
            return;
        }
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(data.transport_num))
                    SystemUtils.setClipboard(itemView.getContext(), data.transport_num);
            }
        });
        status.setText(statusStr);
        address.setVisibility(View.VISIBLE);
        address.setText(data.address);
        name.setText(data.shipping_name + " " + data.shipping_tel);
        if (!TextUtils.isEmpty(data.transport_c_name)) {
            kuaid.setVisibility(View.VISIBLE);
            transport_c_name.setText(data.transport_c_name);
            transport_num.setText(data.transport_num);
        }
    }
}
