package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.viewholders.SucaiHolder;
import com.yjfshop123.live.ui.viewholders.TaskNewItemHolder;
import com.yjfshop123.live.ui.viewholders.TaskTitleHolder;
import com.yjfshop123.live.ui.viewholders.TaskTopHolder;
import com.yjfshop123.live.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class SucaiAdapter extends RecyclerView.Adapter<SucaiHolder> {

    private List<ShareSucaiResopnse.Sucai> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;


    public SucaiAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    CommunityClickListener mItemClickListener;

    public void setmItemClickListener(CommunityClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public SucaiHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = layoutInflater.inflate(R.layout.item_sucai, parent, false);
        return new SucaiHolder(itemView, CommonUtils.getScreenWidth(context));

    }

    @Override
    public void onBindViewHolder(SucaiHolder holder, int position) {
        holder.onBind(context,position, mList.get(position), mItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<ShareSucaiResopnse.Sucai> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }



    public interface MyItemClickListener {
        void onItemClickP(View view, int position);

        void onItemClickC(View view, int position);

        void onItemClickQ(View view, int position);
    }

}
