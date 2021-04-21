package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.GuanZhuResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GZAdapter extends RecyclerView.Adapter<GZAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GuanZhuResponse.ListBean> lists;

    public GZAdapter(Context context, ArrayList<GuanZhuResponse.ListBean> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public GZAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GZAdapter.ViewHolder holder, final int i) {
        GuanZhuResponse.ListBean bean = lists.get(i);
        Glide.with(context).load(bean.getAvatar()).into(holder.imageView);
        holder.name.setText(bean.getNickname());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(view, i);
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.authImg)
        CircleImageView imageView;
        @BindView(R.id.authName)
        TextView name;
        @BindView(R.id.rootLayout)
        LinearLayout rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}