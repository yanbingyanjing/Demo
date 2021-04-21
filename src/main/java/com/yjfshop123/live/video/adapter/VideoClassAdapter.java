package com.yjfshop123.live.video.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.video.bean.CircleListBean;

public class VideoClassAdapter extends RecyclerView.Adapter<VideoClassAdapter.Vh> {

    private CircleListBean circleListBean;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<CircleListBean.ListBean> mOnItemClickListener;
    private String classId = "";
    private Context context;

    public VideoClassAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(circleListBean.getList().get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<CircleListBean.ListBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addData(CircleListBean circleListBean, String classId){
        this.circleListBean = circleListBean;
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
        vh.setData(circleListBean.getList().get(position), position);
    }

    @Override
    public int getItemCount() {
        if (circleListBean == null){
            return 0;
        }
        return circleListBean.getList().size();
    }

    class Vh extends RecyclerView.ViewHolder {

        private TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_live_class_name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CircleListBean.ListBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());

            if (classId.equals(bean.getId())){
                mName.setTextColor(context.getResources().getColor(R.color.color_style));
            }else {
                mName.setTextColor(context.getResources().getColor(R.color.color_title_txt));
            }
        }
    }
}

