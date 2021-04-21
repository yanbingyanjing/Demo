package com.yjfshop123.live.live.live.common.widget.ready;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.ChannelListResponse;

public class LiveClassAdapter extends RecyclerView.Adapter<LiveClassAdapter.Vh> {

    private ChannelListResponse mChannelListResponse;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<ChannelListResponse.ChannelListBean> mOnItemClickListener;
    private int classId = -1;

    public LiveClassAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mChannelListResponse.getChannel_list().get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<ChannelListResponse.ChannelListBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addData(ChannelListResponse channelListResponse, int classId){
        mChannelListResponse = channelListResponse;
        this.classId = classId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mChannelListResponse.getChannel_list().get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mChannelListResponse == null){
            return 0;
        }
        return mChannelListResponse.getChannel_list().size();
    }

    class Vh extends RecyclerView.ViewHolder {

//        private ImageView mIcon;
        private TextView mName;

        public Vh(View itemView) {
            super(itemView);
//            mIcon = itemView.findViewById(R.id.item_live_class_icon);
            mName = itemView.findViewById(R.id.item_live_class_name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ChannelListResponse.ChannelListBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());

            if (classId == bean.getId()) {
                mName.setTextColor(0xffffdd00);
            }else {
                mName.setTextColor(0xffffffff);
            }
        }
    }
}
