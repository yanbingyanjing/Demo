package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.shop.ziying.adapter.SubmitBuyOrderAdapter;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yjfshop123.live.utils.CommonUtils;

public class SubmitBuyOrderAddressHolder extends RecyclerView.ViewHolder {

    private TextView address;
    private TextView name;


    public SubmitBuyOrderAddressHolder(View itemView, final SubmitBuyOrderAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);

        address = itemView.findViewById(R.id.address);
        name = itemView.findViewById(R.id.name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NLog.d("地址点击", mItemClickListener != null ? "1" : "2");
                if (mItemClickListener != null)
                    mItemClickListener.onItemClickAddress(v, getLayoutPosition());
            }
        });
    }


    public void bind(Context context, DefaultAddress.AddressData data) {

        if (data == null) {
            address.setVisibility(View.GONE);
            name.setText(context.getString(R.string.xinzeng_address));
            return;
        }
        address.setVisibility(View.VISIBLE);
        address.setText(data.region + " " + data.address);
        name.setText(data.name + " " + data.telephone);
    }
}
