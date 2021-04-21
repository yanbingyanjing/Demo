package com.yjfshop123.live.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.ui.viewholders.SystemVideoHolder;

import java.util.ArrayList;
import java.util.List;

public class SystemAdapter extends RecyclerView.Adapter<SystemVideoHolder> {

    private List<MessageDB> mList = new ArrayList<>();

    public SystemAdapter(){
    }

    @Override
    public SystemVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_message_item, parent, false);
        SystemVideoHolder searchVideoHolder = new SystemVideoHolder(itemView);
        return searchVideoHolder;
    }

    @Override
    public void onBindViewHolder(SystemVideoHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<MessageDB> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

}

