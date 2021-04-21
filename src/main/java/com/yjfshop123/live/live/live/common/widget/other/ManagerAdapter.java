package com.yjfshop123.live.live.live.common.widget.other;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.ManagerListResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;

import java.util.ArrayList;
import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ManagerListResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public ManagerAdapter(Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_live_manager, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<ManagerListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView btn_del;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            btn_del = (ImageView) itemView.findViewById(R.id.btn_del);

            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(null, getLayoutPosition());
                }
            });
        }

        void setData(ManagerListResponse.ListBean bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getAvatar()))
                    .into(mAvatar);

            mName.setText(bean.getUser_nickname());
            if (bean.getSex() == 1){
                mSex.setImageResource(R.drawable.ic_voice_sex_man);
            }else {
                mSex.setImageResource(R.drawable.ic_voice_sex_women);
            }
        }
    }

}


