package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MessageListResponse;
import com.yjfshop123.live.ui.viewholders.CMessageViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CMessageAdapter extends RecyclerView.Adapter<CMessageViewHolder> {

    private List<MessageListResponse.ListBean> mList = new ArrayList<>();

    //顶:1 回复:2
    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_2 = 2;

    private LayoutInflater layoutInflater;
    private int type;

    private Context context;

    public CMessageAdapter(Context context, int type){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.type = type;
    }

    @Override
    public CMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == VIEW_ITEM_1){
            itemView = layoutInflater.inflate(R.layout.cmessage_item1, parent, false);
        }else if (viewType == VIEW_ITEM_2) {
            itemView = layoutInflater.inflate(R.layout.cmessage_item2, parent, false);
        }
        return new CMessageViewHolder(itemView, viewType, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(CMessageViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_ITEM_1) {
            holder.bind(context, mList.get(position), getItemViewType(position));
        } else if (getItemViewType(position) == VIEW_ITEM_2){
            holder.bind(context, mList.get(position), getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (type == 1){
            return VIEW_ITEM_1;
        }else {
            return VIEW_ITEM_2;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<MessageListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
        void onPortraitClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}

