package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.ui.activity.EggOrderDetailActivity;
import com.yjfshop123.live.ui.activity.EggOrderListActivity;
import com.yjfshop123.live.utils.NumUtil;

import java.util.List;

public class EggListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  List<EggListDataResponse.EggListData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public EggListAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_egg_order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }
    int accountant=-1;
    public void setAccount(int accountant) {
        this.accountant = accountant;
    }
    @Override
    public int getItemCount() {
        return mList!=null?mList.size():0;
    }

    public void setCards( List<EggListDataResponse.EggListData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;

        public LinearLayout itemll;
        TextView type;
        TextView change;
        TextView date;
        TextView  name;
        public Vh(View itemView) {
            super(itemView);
            itemll = itemView.findViewById(R.id.item);
            icon = itemView.findViewById(R.id.icon);
            type = itemView.findViewById(R.id.order_type);
            change = itemView.findViewById(R.id.change_des);
            date = itemView.findViewById(R.id.date);
            name= itemView.findViewById(R.id.name);
          //  itemll.setOnClickListener(this);
            if(accountant == EggOrderListActivity.accountant_out){
                change.setTextColor(context.getResources().getColor(R.color.color_01AA7B));
            }
        }

        void setData(EggListDataResponse.EggListData bean) {
            Glide.with(context)
                    .load(R.drawable.splash_logo)
                    .into(icon);
            type.setText(context.getString(R.string.leixing_title)+bean.order_type);
            change.setText(bean.change_des);
            date.setText(bean.date);
            name.setText(bean.name+"(ID:"+bean.user_id+")");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.item:
                    Intent intent=new Intent(context, EggOrderDetailActivity.class);
                    intent.putExtra("data",new Gson().toJson(mList.get(getLayoutPosition())));
                    context.startActivity(intent);
                    break;

            }
        }
    }

}
