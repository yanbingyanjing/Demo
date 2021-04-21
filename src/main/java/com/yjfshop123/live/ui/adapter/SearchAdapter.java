package com.yjfshop123.live.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SearchUserResponse;
import com.yjfshop123.live.ui.viewholders.SearchVideoHolder;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchVideoHolder> {

    private List<SearchUserResponse.ListBean> mList = new ArrayList<>();

    public SearchAdapter(){
    }

    @Override
    public SearchVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_search_item, parent, false);
        SearchVideoHolder searchVideoHolder = new SearchVideoHolder(itemView, mItemClickListener);
        return searchVideoHolder;
    }

    @Override
    public void onBindViewHolder(SearchVideoHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<SearchUserResponse.ListBean> list) {
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
