package com.yjfshop123.live.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.RobToChatResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.RobToChatAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class RobToChatVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private CircleImageView robtochat_item_icon;
    private ImageView robtochat_item_vip_iv;
    private TextView robtochat_item_name;
    private TextView robtochat_item_time;

    private RobToChatAdapter.MyItemClickListener mItemClickListener;

    public RobToChatVideoHolder (View itemView, RobToChatAdapter.MyItemClickListener itemClickListener) {
        super(itemView);
        this.mItemClickListener = itemClickListener;

        robtochat_item_icon =  itemView.findViewById(R.id.robtochat_item_icon);
        robtochat_item_vip_iv =  itemView.findViewById(R.id.robtochat_item_vip_iv);
        robtochat_item_name = itemView.findViewById(R.id.robtochat_item_name);
        robtochat_item_time = itemView.findViewById(R.id.robtochat_item_time);
        itemView.setOnClickListener(this);
    }

    public void bind(RobToChatResponse.ListBean listBean) {
        robtochat_item_name.setText(listBean.getUser_nickname());
        robtochat_item_time.setText(itemView.getContext().getString(R.string.ql_y) + listBean.getRegister_time() + "加入");

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                .error(R.drawable.loadding)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  
        Glide.with(itemView.getContext())
                .load(listBean.getShow_photo())// 图片地址  
                .apply(options)// 参数  
                .into(robtochat_item_icon);// 需要显示的ImageView控件

        if (listBean.getIs_vip() == 1) {
            robtochat_item_vip_iv.setVisibility(View.VISIBLE);
        }else {
            robtochat_item_vip_iv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getLayoutPosition());
    }
}
