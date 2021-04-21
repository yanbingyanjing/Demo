package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.response.MountListResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MountListResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;
    private int mWidth;

    public MountAdapter(Context context, OnItemClickListener onItemClickListener, int width){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
        mWidth = width;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_live_mount, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<MountListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class Vh extends RecyclerView.ViewHolder {

        LinearLayout mFL;
        ImageView mAvatar;
        TextView mName;
        TextView mChoose;
        TextView mPrice;
        TextView remain_days;
        public Vh(View itemView) {
            super(itemView);
            mFL = (LinearLayout) itemView.findViewById(R.id.mount_item_fl);
            mAvatar = (ImageView) itemView.findViewById(R.id.mount_item_icon);
            mName = (TextView) itemView.findViewById(R.id.mount_item_name);
            mChoose = (TextView) itemView.findViewById(R.id.mount_item_choose);
            mPrice = (TextView) itemView.findViewById(R.id.mount_item_price);
            remain_days= (TextView) itemView.findViewById(R.id.remain_days);
            ViewGroup.LayoutParams params = mFL.getLayoutParams();
            params.width = mWidth;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mFL.setLayoutParams(params);
        }

        void setData(final MountListResponse.ListBean bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getMount_cover()))
                    .into(mAvatar);
            mName.setText(bean.getMount_name());
            mPrice.setText(bean.getMount_coin_zh());
            remain_days.setText("剩余使用天数"+bean.getRemain_days()+"");
            if (bean.getStatus() == 0){
                mChoose.setText("购买");
            }else {
                if (bean.getIs_choosed() == 0){
                    mChoose.setText("未使用");
                }else {
                    mChoose.setText("使用中");
                }
            }

            mChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(bean, getLayoutPosition());
                }
            });
        }
    }

}

