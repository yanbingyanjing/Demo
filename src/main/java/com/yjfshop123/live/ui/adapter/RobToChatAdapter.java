package com.yjfshop123.live.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.RobToChatResponse;
import com.yjfshop123.live.ui.viewholders.RobToChatVideoHolder;

import java.util.ArrayList;
import java.util.List;

public class RobToChatAdapter extends RecyclerView.Adapter<RobToChatVideoHolder> {

    private final static int VIEW_ = 1;
    private final static int VIEW_BOTTOM = 2;

    private List<RobToChatResponse.ListBean> mList = new ArrayList<>();

    public RobToChatAdapter(){
    }

    @Override
    public RobToChatVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_BOTTOM){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_layout, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.robtochat_item, parent, false);
        }
        return new RobToChatVideoHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RobToChatVideoHolder holder, int position) {
        if (getItemViewType(position) == VIEW_BOTTOM) {
            //
        } else if (getItemViewType(position) == VIEW_) {
            holder.bind(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return VIEW_BOTTOM;
        } else {
            return VIEW_;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void setCards(List<RobToChatResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
        void onItemClickSer(View view, int postion);
        void onItemClickSer2(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
