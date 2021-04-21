package com.yjfshop123.live.ui.videolist.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.model.BaseItem;

import butterknife.ButterKnife;


public abstract class BaseViewHolder<T extends BaseItem> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void onBind(int position, T iItem, CommunityClickListener clickListener);
}
