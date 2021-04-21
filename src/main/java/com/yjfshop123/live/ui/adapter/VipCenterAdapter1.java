package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.VipCenterResponse;

import java.util.ArrayList;


public class VipCenterAdapter1 extends BaseAdapter {

    private Context context;
    private ArrayList<VipCenterResponse.ListBean> lists;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public VipCenterAdapter1(Context context, ArrayList<VipCenterResponse.ListBean> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_vip_center, viewGroup, false);
        VipCenterResponse.ListBean bean = lists.get(i);

        TextView imgIcon = view.findViewById(R.id.imgIcon);
        TextView tvExpire = view.findViewById(R.id.tvExpire);
        TextView tvPay = view.findViewById(R.id.tvPay);
        TextView tvUnitPrice = view.findViewById(R.id.tvUnitPrice);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        ImageView imgRecommend = view.findViewById(R.id.imgRecommend);

        if (bean.getRecommend() == 1) {
            imgRecommend.setVisibility(View.VISIBLE);
        } else {
            imgRecommend.setVisibility(View.GONE);
        }

        if (bean.getSubject().equals("月卡"/*context.getString(R.string.centee_foot_7)*/)) {
            imgIcon.setBackground(context.getResources().getDrawable(R.drawable.orange_oval));
            imgIcon.setText("30");
            tvExpire.setText("1" + context.getString(R.string.centee_foot_4));
            tvPrice.setText("￥" + bean.getMoney());
        } else if (bean.getSubject().equals("季卡"/*context.getString(R.string.centee_foot_8)*/)) {
            imgIcon.setBackground(context.getResources().getDrawable(R.drawable.oval_0284e9));
            imgIcon.setText("90");
            tvExpire.setText("3" + context.getString(R.string.centee_foot_4));
            tvPrice.setText("￥" + bean.getMoney());
        } else if (bean.getSubject().equals("年卡"/*context.getString(R.string.centee_foot_9)*/)) {
            imgIcon.setBackground(context.getResources().getDrawable(R.drawable.ffee82ee_oval));
            imgIcon.setText(context.getString(R.string.year));
            tvExpire.setText("1" + context.getString(R.string.centee_foot_6));
            tvPrice.setText("￥" + bean.getMoney());
        }
        tvUnitPrice.setText(bean.getDay_cost() + "/" + context.getString(R.string.centee_foot_5));
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.click(view,i);
            }
        });
        return view;
    }


    public interface ItemClickListener {
        void click(View view, int position);
    }

}
