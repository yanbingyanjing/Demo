package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.InComeResponseResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InComDetailAdapter extends RecyclerView.Adapter<InComDetailAdapter.MyHolder> {

    private Context context;
    private List<InComeResponseResponse.ListBean> lists;

    public InComDetailAdapter(Context context, List<InComeResponseResponse.ListBean> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_incom_detail, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        InComeResponseResponse.ListBean bean = lists.get(position);
        holder.tvText.setText(bean.getSubject());
        holder.tvTime.setText(bean.getCreate_time());
        holder.tvIncome.setText(bean.getCoin_zh());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvIncome)
        TextView tvIncome;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
