package com.yjfshop123.live.game.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.GameListResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GameListResponse.ListBean> mList = new ArrayList<>();
    private int width;
    private OnItemClickListener mOnClickListener;

    public GameAdapter(int width, OnItemClickListener onItemClickListener){
        this.width = width;
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_game_item, parent, false);
        ItemVideoHolder itemHolder = new ItemVideoHolder(itemView);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemVideoHolder)holder).bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<GameListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class ItemVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SelectableRoundedImageView mIcon;
        private TextView mName;

        public ItemVideoHolder (View itemView) {
            super(itemView);

            mIcon =  itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);

            ViewGroup.LayoutParams params = mIcon.getLayoutParams();
            params.width = width;
            params.height = width;
            mIcon.setLayoutParams(params);

            itemView.setOnClickListener(this);
        }

        void bind(GameListResponse.ListBean bean) {
            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(bean.getIcon()))
                    .into(mIcon);

            mName.setText(bean.getCn_name());
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(null, getLayoutPosition());
        }
    }

}
