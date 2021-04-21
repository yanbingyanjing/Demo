package com.yjfshop123.live.video.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SMDLResponse;

import java.util.ArrayList;
import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<ItemMyVideoHolder> {

    private List<SMDLResponse.ListBean> mList = new ArrayList<>();
    private int width;

    public MyVideoAdapter(int width){
        this.width = width;
    }
    private String user_id;
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public ItemMyVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_item2, parent, false);
        ItemMyVideoHolder itemHolder = new ItemMyVideoHolder(itemView, mItemClickListener);
        itemHolder.setUserId(user_id);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemMyVideoHolder holder, int position) {
        holder.bind(mList.get(position), width);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<SMDLResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemDelete(View view, int position);
        void onItemOpen(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
