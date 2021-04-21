package com.yjfshop123.live.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.RebroadcastListResponse;
import com.yjfshop123.live.ui.viewholders.ItemBackCallHolder;

import java.util.ArrayList;
import java.util.List;

public class BackCallAdapter extends RecyclerView.Adapter<ItemBackCallHolder> {

    private List<RebroadcastListResponse.ListBean> mList = new ArrayList<>();
    private int width;

    public BackCallAdapter(int width){
        this.width = width;
    }

    @Override
    public ItemBackCallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_backcall_item, parent, false);
        ItemBackCallHolder itemHolder = new ItemBackCallHolder(itemView, mItemClickListener);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemBackCallHolder holder, int position) {
        holder.bind(mList.get(position), width);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<RebroadcastListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemCancel(View view, int position);
        void onItemDelete(View view, int position);
        void onItemBackCall(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
