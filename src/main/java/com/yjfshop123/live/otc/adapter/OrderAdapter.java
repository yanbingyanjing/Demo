package com.yjfshop123.live.otc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.OtcOrderListResponse;
import com.yjfshop123.live.model.PaySettingResponse;

import java.util.List;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<OtcOrderListResponse.OtcOrderData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public OrderAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_otc_order, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<OtcOrderListResponse.OtcOrderData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    OnItemClickListener onItemClickListener;


    public void setListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onclick(OtcOrderListResponse.OtcOrderData data);
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView time;
        TextView order_type;
        TextView eggs;
        TextView money;
        TextView desc;
        TextView unit;

        public Vh(View itemView) {
            super(itemView);
            unit = itemView.findViewById(R.id.unit);
            time = itemView.findViewById(R.id.time);
            order_type = itemView.findViewById(R.id.order_type);
            eggs = itemView.findViewById(R.id.eggs);
            money = itemView.findViewById(R.id.money);
            desc = itemView.findViewById(R.id.desc);
        }

        void setData(final OtcOrderListResponse.OtcOrderData bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) onItemClickListener.onclick(bean);
                }
            });
            if (bean == null) return;
            time.setText(bean.time);
            if (bean.order_type == 0) {
                order_type.setText("购买");
            } else order_type.setText("出售");

            if (!TextUtils.isEmpty(bean.pay_type)) {
                if(bean.pay_type.equals(USDT_TYPE)){
                    unit.setText("交易金额(USDT)");
                }else {
                    unit.setText("交易金额(RMB)");
                }
            }
            eggs.setText(bean.eggs);
            money.setText(bean.money);
            desc.setText(bean.desc);
        }

    }
}
