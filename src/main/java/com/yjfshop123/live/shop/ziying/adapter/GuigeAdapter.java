package com.yjfshop123.live.shop.ziying.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopDetail;
import com.yjfshop123.live.xuanpin.adapter.XunZhangAdapter;

import java.util.List;

public class GuigeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;

    public GuigeAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Vh(layoutInflater.inflate(R.layout.item_guige_tx, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(data[position]);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.length : 0;
    }


    ZiyingShopDetail.SpecData[] data;

    public void setHeadData(ZiyingShopDetail.SpecData[] data) {
        if (data == null) {
            return;
        }
        this.data = data;
        notifyDataSetChanged();
    }

    int index = 0;

    public class Vh extends RecyclerView.ViewHolder {
        TextView guige;


        public Vh(View itemView) {
            super(itemView);
            guige = itemView.findViewById(R.id.guige);

        }

        void setData(ZiyingShopDetail.SpecData bean) {
            if (bean == null) return;
            guige.setText(bean.name);

        }

    }

}
