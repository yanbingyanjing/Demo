package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.model.GoldPriceResponse;
import com.yjfshop123.live.utils.NumUtil;

import java.math.BigDecimal;
import java.util.List;

public class GoldPriceAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<GoldPriceResponse.UpEntity> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public GoldPriceAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_gold_price, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<GoldPriceResponse.UpEntity> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView price;
        TextView time;
        TextView up_down;

        public Vh(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            price = itemView.findViewById(R.id.price);
            up_down = itemView.findViewById(R.id.up_down);

        }

        void setData(GoldPriceResponse.UpEntity bean) {
            time.setText(bean.time);
            price.setText(bean.price);
            up_down.setText(bean.up_down);

        }

    }

}
