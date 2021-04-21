package com.yjfshop123.live.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.ui.viewholders.ConversationVideoHolder;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationVideoHolder> {

    private List<IMConversationDB> mList = new ArrayList<>();

    private View systemHeader;

    public ConversationAdapter(View systemHeader){
        this.systemHeader = systemHeader;
    }

    @Override
    public ConversationVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 100){
            itemView = systemHeader;
        }else{
            if (ActivityUtils.IS_VIDEO()){
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_conversation_item, parent, false);
            }else {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_conversation_item, parent, false);
            }
        }
        return new ConversationVideoHolder(itemView, mItemClickListener, mLongClickListener, viewType);
    }

    @Override
    public void onBindViewHolder(ConversationVideoHolder holder, int position) {
        if (systemHeader == null){
            holder.bind(mList.get(position), getItemViewType(position), 0);
        }else {
            if (getItemViewType(position) == 100){
            }else{
                holder.bind(mList.get(position - 1), getItemViewType(position), 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (systemHeader == null){
            return mList.size();
        }else {
            return mList.size() + 1;
        }
    }

    public void setCards(List<IMConversationDB> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (systemHeader == null){
            return 101;
        }else {
            if (position == 0) {
                return 100;
            } else {
                return 101;
            }
        }
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }



    private MyLongClickListener mLongClickListener;

    public interface MyLongClickListener {
        void onLongClick(View view, int postion);
    }

    public void setOnLongClickListener(MyLongClickListener listener){
        this.mLongClickListener = listener;
    }

}
