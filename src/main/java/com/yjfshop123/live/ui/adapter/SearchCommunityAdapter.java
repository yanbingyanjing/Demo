package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SearchDynamicResponse;
import com.yjfshop123.live.ui.viewholders.SearchCommunityViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SearchCommunityAdapter extends RecyclerView.Adapter<SearchCommunityViewHolder> {

    private List<SearchDynamicResponse.ListBean> mList = new ArrayList<>();
    private Context context;
    private String content = "";

    public SearchCommunityAdapter(Context context){
        this.context = context;
    }

    @Override
    public SearchCommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_community_item, parent, false);
        return new SearchCommunityViewHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(SearchCommunityViewHolder holder, int position) {
        holder.bind(context, mList.get(position), content);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<SearchDynamicResponse.ListBean> list, String content) {
        if (list == null) {
            return;
        }
        mList = list;
        this.content = content;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
        void onPraiseItemClick(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}