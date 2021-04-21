package com.yjfshop123.live.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.VicinityUserResponse;
import com.yjfshop123.live.ui.viewholders.ItemHolder;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<ItemHolder> {

    private List<VicinityUserResponse.ListBean> mList = new ArrayList<>();

    private int width;
    private View header;

    /**
     * 顶部banner
     */
    private final static int VIEW_HEADER = 0;
    /**
     * 大图
     */
    private final static int VIEW_ITEM_1 = 1;
    /**
     * 中图
     */
    private final static int VIEW_ITEM_2 = 2;
    /**
     * 小图
     */
    private final static int VIEW_ITEM_3 = 3;

    private LayoutInflater layoutInflater;
    private int type;

    private Context context;

    public RecommendedAdapter(Context context, int width, View header, int type){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.width = width;
        this.header = header;
        this.type = type;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == VIEW_HEADER) {
            itemView = header;
        }else if (viewType == VIEW_ITEM_1){
            itemView = layoutInflater.inflate(R.layout.layout_recommended_item, parent, false);
        }else if (viewType == VIEW_ITEM_2) {
            itemView = layoutInflater.inflate(R.layout.layout_recommended_item2, parent, false);
        }else if (viewType == VIEW_ITEM_3) {
            itemView = layoutInflater.inflate(R.layout.layout_recommended_item3, parent, false);
        }
        return new ItemHolder(context,itemView, viewType, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (getItemViewType(position) == VIEW_HEADER) {
            //banner
        } else if (getItemViewType(position) == VIEW_ITEM_1) {
            holder.bind(context, mList.get(position - 1), width, getItemViewType(position));
        } else if (getItemViewType(position) == VIEW_ITEM_2){
            holder.bind(context, mList.get(position - 1), width, getItemViewType(position));
        }else if (getItemViewType(position) == VIEW_ITEM_3){
            holder.bind(context, mList.get(position - 1), width, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_HEADER;
        } else {
            if (type == 1){
                return VIEW_ITEM_1;
            }else if (type == 2){
                return VIEW_ITEM_2;
            }else if (type == 3){
                return VIEW_ITEM_3;
            }
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void setCards(List<VicinityUserResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
