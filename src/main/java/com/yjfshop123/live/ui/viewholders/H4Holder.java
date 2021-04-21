package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.IncomeRankingResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.H4Adapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

public class H4Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final static int VIEW_HEADER = 0;
    private final static int VIEW_ITEM = 1;

    private Context context;
    private CircleImageView h4_item_icon;
    private TextView h4_item_number;
    private TextView h4_item_name;
    private TextView h4_item_describe;
    private TextView h4_item_sex;
    private View h4_item_top;
    private View h4_item_bottom;
    private View h4_item_bottom_b;

    private H4Adapter.MyItemClickListener mItemClickListener;

    public H4Holder(Context context, View itemView, int viewType, H4Adapter.MyItemClickListener mItemClickListener) {
        super(itemView);
        this.context = context;

        if (viewType == VIEW_HEADER) {
            //
        } else if (viewType == VIEW_ITEM) {
            h4_item_icon = itemView.findViewById(R.id.h4_item_icon);
            h4_item_number = itemView.findViewById(R.id.h4_item_number);
            h4_item_name = itemView.findViewById(R.id.h4_item_name);
            h4_item_describe = itemView.findViewById(R.id.h4_item_describe);
            h4_item_sex = itemView.findViewById(R.id.h4_item_sex);
            h4_item_top = itemView.findViewById(R.id.h4_item_top);
            h4_item_bottom = itemView.findViewById(R.id.h4_item_bottom);
            h4_item_bottom_b = itemView.findViewById(R.id.h4_item_bottom_b);
        }

        this.mItemClickListener = mItemClickListener;
        itemView.setOnClickListener(this);
    }

    public void bind(IncomeRankingResponse.ListBean listBean, int viewType, int size) {
        if (viewType == VIEW_HEADER) {
            //
        } else if (viewType == VIEW_ITEM) {
            h4_item_describe.setText(listBean.getCoin() + context.getString(R.string.my_jinbi));
            h4_item_name.setText(listBean.getUser_nickname());
            h4_item_number.setText(String.valueOf(getLayoutPosition() + 3));
            if (!TextUtils.isEmpty(listBean.getShow_photo())) {
                Glide.with(context)
                        .load(CommonUtils.getUrl(listBean.getShow_photo()))
                        .into(h4_item_icon);
            } else {
                Glide.with(context)
                        .load(R.drawable.splash_logo)
                        .into(h4_item_icon);
            }

            if (listBean.getSex() == 1) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                h4_item_sex.setCompoundDrawables(drawable, null, null, null);
                h4_item_sex.setText(" " + listBean.getAge());//♂♀
                h4_item_sex.setEnabled(false);
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                h4_item_sex.setCompoundDrawables(drawable, null, null, null);
                h4_item_sex.setText(" " + listBean.getAge());//♂♀
                h4_item_sex.setEnabled(true);
            }

            if (getLayoutPosition() == (size - 3)) {
                h4_item_bottom.setBackgroundResource(R.drawable.bg_graditent_fdf7f1_fee8d5_bottom_9);
                // h4_item_bottom_b.setVisibility(View.VISIBLE);
            } else {
                h4_item_bottom.setBackgroundResource(R.drawable.bg_graditent_fdf7f1_fee8d5);
                // h4_item_bottom_b.setVisibility(View.GONE);
            }

            if (getLayoutPosition() == 1) {
                h4_item_top.setBackgroundResource(R.drawable.bg_graditent_fdf7f1_fee8d5_top_9);
            } else {
                h4_item_top.setBackgroundResource(R.drawable.bg_graditent_fdf7f1_fee8d5);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getLayoutPosition());
    }
}

