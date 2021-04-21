package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MyRewardResponse;
import com.yjfshop123.live.ui.viewholders.RewardHolder;

import java.util.ArrayList;
import java.util.List;

public class RewarddAdapter extends RecyclerView.Adapter<RewardHolder> {

    private List<MyRewardResponse.ListBean> mList = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private Context context;
    private int type;

    public RewarddAdapter(Context context, int type){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.type = type;
    }

    @Override
    public RewardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_reward_item, parent, false);
        return new RewardHolder(context, itemView);
    }

    @Override
    public void onBindViewHolder(RewardHolder holder, int position) {
        holder.bind(mList.get(position), type);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<MyRewardResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

}
