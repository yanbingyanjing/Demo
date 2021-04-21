package com.yjfshop123.live.ctc.adapter;

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
import com.yjfshop123.live.model.CtcListResopnse;
import com.yjfshop123.live.utils.NumUtil;

import java.util.List;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class WeiTuoMangerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<CtcListResopnse.CtcListData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public WeiTuoMangerAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_ctc_weituo, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<CtcListResopnse.CtcListData> list) {
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
        void onclick(CtcListResopnse.CtcListData data);

        void onCancelWeituoclick(CtcListResopnse.CtcListData data, int position);
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView name;
        TextView deal;
        TextView amount;
        TextView price;
        TextView limit;
        ImageView head;
        ImageView ali;
        ImageView wechat;
        ImageView USDT;
        ImageView bank;
        TextView buy_sell_btn;
        TextView cancel_btn;
        TextView  total;
        TextView unit,usdtTip;
        public Vh(View itemView) {
            super(itemView);
            usdtTip=itemView.findViewById(R.id.usdtTip);
            unit=  itemView.findViewById(R.id.unit);
            USDT = itemView.findViewById(R.id.usdt);
            name = itemView.findViewById(R.id.name);
            deal = itemView.findViewById(R.id.has_deal);
            amount = itemView.findViewById(R.id.amount);
            price = itemView.findViewById(R.id.price);
            limit = itemView.findViewById(R.id.limit);
            head = itemView.findViewById(R.id.head);
            buy_sell_btn = itemView.findViewById(R.id.buy_sell_btn);
            ali = itemView.findViewById(R.id.ali);
            wechat = itemView.findViewById(R.id.wechat);
            bank = itemView.findViewById(R.id.bank);
            total= itemView.findViewById(R.id.total);
            cancel_btn = itemView.findViewById(R.id.cancel_btn);
        }

        void setData(final CtcListResopnse.CtcListData bean) {
            ali.setVisibility(View.GONE);
            wechat.setVisibility(View.GONE);
            bank.setVisibility(View.GONE);
            USDT.setVisibility(View.GONE);
            buy_sell_btn.setVisibility(View.GONE);
            cancel_btn.setVisibility(View.INVISIBLE);
            usdtTip.setVisibility(View.GONE);
            unit.setText("¥");
            buy_sell_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) onItemClickListener.onclick(bean);
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onCancelWeituoclick(bean, getLayoutPosition());
                }
            });
            if (bean == null) return;
            if(bean.status==0||bean.status==1)
                cancel_btn.setVisibility(View.VISIBLE);
            if(bean.amount.equals("0")){
                cancel_btn.setVisibility(View.INVISIBLE);
            }
            total.setText(NumUtil.clearZero(bean.total_eggs));
            name.setText(bean.name);
            deal.setText(bean.desc);
            amount.setText(NumUtil.clearZero(bean.amount));
            price.setText(NumUtil.clearZero(bean.price));
            limit.setText(NumUtil.clearZero(bean.minNum) + "-" + NumUtil.clearZero(bean.maxNum) + "(个)");
            if (TextUtils.isEmpty(bean.avatar)) {
                Glide.with(itemView.getContext()).load(R.drawable.splash_logo).into(head);
            } else {
                Glide.with(itemView.getContext()).load(bean.avatar).into(head);
            }
            if (!TextUtils.isEmpty(bean.card_type)) {
                if (bean.card_type.equals(BANK_TYPE)) {
                    bank.setVisibility(View.VISIBLE);
                    return;
                }
                if (bean.card_type.equals(WECHAT_TYPE)) {
                    wechat.setVisibility(View.VISIBLE);
                    return;
                }
                if (bean.card_type.equals(USDT_TYPE)) {
                    USDT.setVisibility(View.VISIBLE);
                    unit.setText("USDT");
                    usdtTip.setVisibility(View.VISIBLE);
                    return;
                }
                if (bean.card_type.equals(ALIPAY_TYPE)) {
                    ali.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}

