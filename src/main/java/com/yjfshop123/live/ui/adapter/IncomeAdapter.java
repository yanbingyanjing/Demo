package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.net.response.InComeResponseResponse;

import java.util.List;

public class IncomeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<InComeResponseResponse.ListBean> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public IncomeAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_incom, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ((Vh) holder).setData(mList.get(position));

    }

    @Override
    public int getItemCount() {

        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<InComeResponseResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {

        TextView index;
        TextView name;
        ImageView head;
        TextView level;
        TextView des;
        TextView guanzhu;
        public Vh(View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            name = itemView.findViewById(R.id.name);
            head = itemView.findViewById(R.id.head);
            level = itemView.findViewById(R.id.level);
            des = itemView.findViewById(R.id.des);
            guanzhu = itemView.findViewById(R.id.guanzhu);
        }

        void setData(InComeResponseResponse.ListBean bean) {
            Glide.with(context).load(R.drawable.splash_logo).into(head);
            des.setText(bean.getSubject()+bean.getCoin_zh());
            index.setText(getLayoutPosition()+1+"");
        }

    }
}
