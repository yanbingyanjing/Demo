package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.GiftListResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

public class GiftAdapter extends PagerAdapter {

    private GiftListResponse giftListResponse;
    private static int mPos = 0;

    public GiftAdapter(GiftListResponse giftListResponse, int pos) {
        this.giftListResponse = giftListResponse;
        mPos = pos;
    }

    public void setPos(int pos){
        mPos = pos;
    }

    private View newView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.gift_adapter_item, null);
        ViewHolder holder = new ViewHolder();

        holder.gift_adapter_item_ll_1 = view.findViewById(R.id.gift_adapter_item_ll_1);
        holder.gift_adapter_item_ll_2 = view.findViewById(R.id.gift_adapter_item_ll_2);
        holder.gift_adapter_item_ll_3 = view.findViewById(R.id.gift_adapter_item_ll_3);
        holder.gift_adapter_item_ll_4 = view.findViewById(R.id.gift_adapter_item_ll_4);
        holder.gift_adapter_item_ll_5 = view.findViewById(R.id.gift_adapter_item_ll_5);
        holder.gift_adapter_item_ll_6 = view.findViewById(R.id.gift_adapter_item_ll_6);
        holder.gift_adapter_item_ll_7 = view.findViewById(R.id.gift_adapter_item_ll_7);
        holder.gift_adapter_item_ll_8 = view.findViewById(R.id.gift_adapter_item_ll_8);

        view.setTag(holder);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        int count = giftListResponse.getList().size() / 8;
        int remainder = giftListResponse.getList().size() % 8;
        if (remainder == 0 && count != 0){
            return count;
        }else {
            return count + 1;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = this.newView(container.getContext());
        updatePhotoView(position, view, container.getContext());
        view.setId(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ViewHolder holder = (ViewHolder)container.findViewById(position).getTag();
        container.removeView((View)object);
    }

    private void updatePhotoView(int position, final View view, Context context) {
        ViewHolder holder = (ViewHolder)view.getTag();
        int size = giftListResponse.getList().size();
        if (size > position * 8 + 8){
            size = position * 8 + 8;
        }
        for (int i = position * 8; i < size; i++) {
            final LinearLayout linearLayout = getView(i % 8, holder);
            View view_ = LayoutInflater.from(context).inflate(R.layout.gift_item, null);
            TextView gift_item_name = view_.findViewById(R.id.gift_item_name);
            TextView gift_item_cost = view_.findViewById(R.id.gift_item_cost);
            ImageView gift_item_icon = view_.findViewById(R.id.gift_item_icon);
            ImageView gift_item_choose = view_.findViewById(R.id.gift_item_choose);
            if (mPos == i){
                gift_item_choose.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundResource(R.drawable._gift_bg);
            }else {
                gift_item_choose.setVisibility(View.GONE);
                linearLayout.setBackgroundResource(android.R.color.transparent);
            }
            gift_item_name.setText(giftListResponse.getList().get(i).getName());
            gift_item_cost.setText(giftListResponse.getList().get(i).getCoin_zh());
            String imgUrl = giftListResponse.getList().get(i).getIcon_img();
            Glide.with(context)
                    .load(CommonUtils.getUrl(imgUrl))
                    .into(gift_item_icon);
            linearLayout.addView(view_);

            final int pos = i;
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(linearLayout, pos);
                }
            });
        }
    }

    private LinearLayout getView(int position, ViewHolder holder){
        switch (position){
            case 0:
                return holder.gift_adapter_item_ll_1;
            case 1:
                return holder.gift_adapter_item_ll_2;
            case 2:
                return holder.gift_adapter_item_ll_3;
            case 3:
                return holder.gift_adapter_item_ll_4;
            case 4:
                return holder.gift_adapter_item_ll_5;
            case 5:
                return holder.gift_adapter_item_ll_6;
            case 6:
                return holder.gift_adapter_item_ll_7;
            case 7:
                return holder.gift_adapter_item_ll_8;
        }
        return null;
    }

    public class ViewHolder {
        LinearLayout gift_adapter_item_ll_1
                ,gift_adapter_item_ll_2
                ,gift_adapter_item_ll_3
                ,gift_adapter_item_ll_4
                ,gift_adapter_item_ll_5
                ,gift_adapter_item_ll_6
                ,gift_adapter_item_ll_7
                ,gift_adapter_item_ll_8;

        public ViewHolder() {
        }
    }

    public MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }
}
