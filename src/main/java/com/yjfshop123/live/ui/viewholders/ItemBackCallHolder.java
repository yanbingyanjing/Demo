package com.yjfshop123.live.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.RebroadcastListResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.BackCallAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;

public class ItemBackCallHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SelectableRoundedImageView mIcon;
    private TextView mData;
    private TextView mTitle;
    private FrameLayout mFl;

    private BackCallAdapter.MyItemClickListener mItemClickListener;

    public ItemBackCallHolder (View itemView, BackCallAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);

        mIcon =  itemView.findViewById(R.id.backcall_item_icon);
        mData =  itemView.findViewById(R.id.backcall_item_data);
        mTitle = itemView.findViewById(R.id.backcall_item_title);
        mFl = itemView.findViewById(R.id.backcall_item_fl);

        this.mItemClickListener = mItemClickListener;
        itemView.findViewById(R.id.backcall_item_cancel).setOnClickListener(this);
        itemView.findViewById(R.id.backcall_item_del).setOnClickListener(this);
        itemView.findViewById(R.id.backcall_item_btn).setOnClickListener(this);
        mFl.setOnClickListener(this);
    }

    public void bind(RebroadcastListResponse.ListBean bean, int width) {

        mData.setText(bean.getCreate_time());
        mTitle.setText(bean.getTitle());

        ViewGroup.LayoutParams params = mIcon.getLayoutParams();
        params.width = width;
        params.height = (int)(width*1.5);
        mIcon.setLayoutParams(params);

        Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(bean.getCover_img()))
                .into(mIcon);

        if (bean.getIs_open() == 1){
            mFl.setVisibility(View.VISIBLE);
        }else {
            mFl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backcall_item_btn:
                mItemClickListener.onItemBackCall(v, getLayoutPosition());
                break;
            case R.id.backcall_item_del:
                mItemClickListener.onItemDelete(v, getLayoutPosition());
                break;
            case R.id.backcall_item_cancel:
                mItemClickListener.onItemCancel(v, getLayoutPosition());
                break;
        }
    }
}
