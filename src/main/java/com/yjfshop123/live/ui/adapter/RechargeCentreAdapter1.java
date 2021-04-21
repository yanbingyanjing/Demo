package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.RechargeAmountListResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RechargeCentreAdapter1 extends BaseAdapter {

    private Context context;
    private ArrayList<RechargeAmountListResponse.ListBean> lists;

    private MoneyClickListener moneyClickListener;

    public void setMoneyClickListener(MoneyClickListener moneyClickListener) {
        this.moneyClickListener = moneyClickListener;
    }

    public RechargeCentreAdapter1(Context context, ArrayList<RechargeAmountListResponse.ListBean> lists) {
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
        view = LayoutInflater.from(context).inflate(R.layout.adapter_recharge_center, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.bgImage.setVisibility(View.GONE);
        if (i == 0) {
            holder.detailLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_recharge_center_top_ffffff_20_button));
            holder.bgImage.setVisibility(View.VISIBLE);
        } else if (i == lists.size() - 1) {
            holder.detailLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_recharge_center_buttom_ffffff_20_button));
        } else {
            holder.detailLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_ffffff_button));
        }
        RechargeAmountListResponse.ListBean bean = lists.get(i);
        if (TextUtils.isEmpty(bean.getExtra_coin_zh())){
            holder.title.setText(bean.getCoin_zh());
        }else {
            holder.title.setText(bean.getCoin_zh() + "(加送" + bean.getExtra_coin_zh() + ")");
        }
        holder.money.setText("￥" + bean.getMoney());
        holder.money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moneyClickListener.moneyClick(view, i);
            }
        });
        return view;
    }

    class ViewHolder {
        @BindView(R.id.detailLayout)
        RelativeLayout detailLayout;
        @BindView(R.id.bgImage)
        ImageView bgImage;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.money)
        TextView money;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface MoneyClickListener {
        void moneyClick(View view, int position);
    }

}
