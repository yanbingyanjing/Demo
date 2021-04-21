package com.yjfshop123.live.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SearchUserResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.SearchAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

public class SearchVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SelectableRoundedImageView search_item_icon;
    private TextView search_item_name;
    private TextView search_item_city;

    private SearchAdapter.MyItemClickListener mItemClickListener;

    public SearchVideoHolder (View itemView, SearchAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);

        search_item_icon =  itemView.findViewById(R.id.search_item_icon);
        search_item_name = itemView.findViewById(R.id.search_item_name);
        search_item_city = itemView.findViewById(R.id.search_item_city);

        this.mItemClickListener = mItemClickListener;
        itemView.setOnClickListener(this);
    }

    public void bind(SearchUserResponse.ListBean listBean) {
        search_item_name.setText(listBean.getUser_nickname());
        search_item_city.setText(listBean.getCity_name());

        Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(listBean.getShow_photo()))
                .into(search_item_icon);
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getPosition());
    }
}
