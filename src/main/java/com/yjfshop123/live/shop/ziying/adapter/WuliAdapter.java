package com.yjfshop123.live.shop.ziying.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.WuliuResponse;
import com.yjfshop123.live.shop.ziying.ui.AddressAddActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class WuliAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<WuliuResponse.WuliuData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public WuliAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_wuliu, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<WuliuResponse.WuliuData> list) {

        mList = list;
        notifyDataSetChanged();
    }



    public class Vh extends RecyclerView.ViewHolder {

        TextView tv_info, tv_date;
        ImageView iv_new,iv_old;
        View  v_short_line,v_long_line;
        public Vh(View itemView) {
            super(itemView);
            tv_info = itemView.findViewById(R.id.tv_info);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_new = itemView.findViewById(R.id.iv_new);
            iv_old = itemView.findViewById(R.id.iv_old);
            v_short_line = itemView.findViewById(R.id.v_short_line);
            v_long_line = itemView.findViewById(R.id.v_long_line);

        }

        void setData(final WuliuResponse.WuliuData data) {
            if (data == null) return;
            tv_info.setText(data.desc);
            tv_date.setText(data.title);
            if(getLayoutPosition()==0){
                tv_info.setTextColor( context.getResources().getColor(R.color.green_color));
                tv_date.setTextColor( context.getResources().getColor(R.color.green_color));
                iv_new.setVisibility(View.VISIBLE);
                iv_old.setVisibility(View.GONE);
                v_short_line.setVisibility(View.INVISIBLE);
            }else {
                tv_info.setTextColor( context.getResources().getColor(R.color.gray1));
                tv_date.setTextColor( context.getResources().getColor(R.color.gray1));
                iv_new.setVisibility(View.GONE);
                iv_old.setVisibility(View.VISIBLE);
                v_short_line.setVisibility(View.VISIBLE);
            }
            if(getLayoutPosition()==mList.size()-1){
                v_long_line.setVisibility(View.GONE);
            }else {
                v_long_line.setVisibility(View.VISIBLE);
            }
        }

    }



}

