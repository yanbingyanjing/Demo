package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.ui.viewholders.CommunityHolder;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityHolder> {

    //类型（1:视频 2:图片）
    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_2 = 2;
    private final static int VIEW_ITEM_BOTTOM = 3;

    private int width;
    private LayoutInflater layoutInflater;
    private Context context;

    private List<PopularDynamicResponse.ListBean> mList = new ArrayList<>();

    private int width_2;
    private int height_video;

    public CommunityAdapter(Context context, int width, int width_2, int height_video){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.width = width;
        this.width_2 = width_2;
        this.height_video = height_video;
    }

    @Override
    public CommunityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == VIEW_ITEM_1){
            itemView = layoutInflater.inflate(R.layout.community_item_2, parent, false);
        }else if (viewType == VIEW_ITEM_2) {
            itemView = layoutInflater.inflate(R.layout.community_item_1, parent, false);
        }else if (viewType == VIEW_ITEM_BOTTOM) {
            itemView = layoutInflater.inflate(R.layout.community_item_bottom, parent, false);
        }
        return new CommunityHolder(itemView, viewType, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(CommunityHolder holder, int position) {
        if (getItemViewType(position) == VIEW_ITEM_BOTTOM){
            return;
        }
        holder.bind(context, mList.get(position), width, getItemViewType(position), width_2, height_video);
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == position){
            return VIEW_ITEM_BOTTOM;
        }else if (mList.get(position).getVideo_list().size() > 0){
            return VIEW_ITEM_1;
        }else {
            return VIEW_ITEM_2;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void setCards(List<PopularDynamicResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onContentClick(View view, int position);
        void onPortraitClick(View view, int position);
        void onPraiseClick(View view, int position);
        void onImgClick(View view, int position, int index);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}

