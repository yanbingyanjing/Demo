package com.yjfshop123.live.live.live.common.widget.ready;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;

import java.util.List;


public class LiveRoomTypeAdapter extends RecyclerView.Adapter<LiveRoomTypeAdapter.Vh> {

    private List<LiveRoomTypeBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveRoomTypeBean> mOnItemClickListener;

    public LiveRoomTypeAdapter(Context context, int checkedId) {
        mList = LiveRoomTypeBean.getLiveTypeList();

        for (LiveRoomTypeBean bean : mList) {
            if (bean.getId() == checkedId) {
                bean.setChecked(true);
                break;
            }
        }

        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveRoomTypeBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView)itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveRoomTypeBean bean, int position) {
            itemView.setTag(position);
            mName.setText(itemView.getContext().getString(bean.getName()));
            if (bean.isChecked()) {
                mName.setTextColor(0xffffdd00);
                mIcon.setImageResource(bean.getCheckedIcon());
            } else {
                mName.setTextColor(0xffffffff);
                mIcon.setImageResource(bean.getUnCheckedIcon());
            }
        }
    }
}
