package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class TaskHolder extends RecyclerView.ViewHolder {

    private CircleImageView mIcon;
    private ImageView task_adapter_item_vip_iv;
    private TextView mNicknameTv;
    private TextView mCostTv;
    private TextView task_adapter_item_mi_tv;
    private TextView task_adapter_item_user_tv;

    private TaskAdapter.MyItemClickListener mItemClickListener;

    public TaskHolder(View itemView, TaskAdapter.MyItemClickListener itemClickListener) {
        super(itemView);

        mIcon =  itemView.findViewById(R.id.task_adapter_item_icon);
        task_adapter_item_vip_iv =  itemView.findViewById(R.id.task_adapter_item_vip_iv);
        mNicknameTv = itemView.findViewById(R.id.task_adapter_item_nickname_tv);
        mCostTv = itemView.findViewById(R.id.task_adapter_item_cost_tv);

        task_adapter_item_mi_tv = itemView.findViewById(R.id.task_adapter_item_mi_tv);
        task_adapter_item_user_tv = itemView.findViewById(R.id.task_adapter_item_user_tv);

        this.mItemClickListener = itemClickListener;

        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClickP(v, getLayoutPosition());
            }
        });
        task_adapter_item_mi_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClickC(v, getLayoutPosition());
            }
        });
        task_adapter_item_user_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClickQ(v, getLayoutPosition());
            }
        });
    }

    public void bind(Context context, ChatTaskListResponse.ListBean listBean, String mi_tencentId) {
        mNicknameTv.setText(listBean.getUser_nickname());
        mCostTv.setText(listBean.getPer_cost() + context.getString(R.string.ql_cost));

        if (mi_tencentId.equals(String.valueOf(listBean.getUser_id()))){
            task_adapter_item_mi_tv.setVisibility(View.VISIBLE);
            task_adapter_item_user_tv.setVisibility(View.GONE);
        }else {
            task_adapter_item_mi_tv.setVisibility(View.GONE);
            task_adapter_item_user_tv.setVisibility(View.VISIBLE);
        }

        String avatar = listBean.getAvatar();

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                .error(R.drawable.imageloader)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

        Glide.with(itemView.getContext())
                .load(avatar)// 图片地址  
                .apply(options)// 参数  
                .into(mIcon);// 需要显示的ImageView控件

        if (listBean.getIs_vip() == 1) {
            task_adapter_item_vip_iv.setVisibility(View.VISIBLE);
        }else {
            task_adapter_item_vip_iv.setVisibility(View.GONE);
        }
    }
}
