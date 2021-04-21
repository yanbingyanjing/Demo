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
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.model.OtcBuySellLimitResponse;
import com.yjfshop123.live.model.PaySettingResponse;
import com.yjfshop123.live.ui.adapter.AdditionAdapter;
import com.yjfshop123.live.utils.NumUtil;

import java.math.BigDecimal;
import java.util.List;

import static com.yjfshop123.live.model.OtcBuySellLimitResponse.ALIPAY_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.BANK_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.USDT_TYPE;
import static com.yjfshop123.live.model.OtcBuySellLimitResponse.WECHAT_TYPE;

public class PayTypeSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<PaySettingResponse.PayData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public PayTypeSelectAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_pay_type, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<PaySettingResponse.PayData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    boolean bianji_type = false;//编辑状态
    OnItemClickListener onItemClickListener;
    String select_id;
    OtcBuySellLimitResponse otcBuySellLimitResponse;

    public void setBianjiType(boolean bianji_type) {
        this.bianji_type = bianji_type;
    }

    public void setSelectId(String select_id) {
        this.select_id = select_id;
    }

    public void setLimit(OtcBuySellLimitResponse otcBuySellLimitResponse) {
        this.otcBuySellLimitResponse = otcBuySellLimitResponse;
    }

    public void setListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onclick(PaySettingResponse.PayData data,boolean is_edite);
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView name;
        TextView card;
        TextView type_name;
        ImageView logo;
        ImageView selected_logo,edit_logo;
        ImageView selected_logo_edit;
        TextView zanbuzhichi;

        public Vh(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            type_name = itemView.findViewById(R.id.type_name);
            logo = itemView.findViewById(R.id.logo);
            selected_logo = itemView.findViewById(R.id.selected_logo);
            edit_logo=itemView.findViewById(R.id.edit_logo);
            selected_logo_edit = itemView.findViewById(R.id.selected_logo_edit);
            zanbuzhichi = itemView.findViewById(R.id.zanbuzhichi);
        }

        void setData(final PaySettingResponse.PayData bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onclick(bean,false);
                    }
                }
            });
            edit_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onclick(bean,true);
                    }
                }
            });
            if (bean != null) {

                name.setText(bean.name);
                card.setText(bean.card);
//                if (!bianji_type) {
//                    selected_logo_edit.setVisibility(View.GONE);
//                    if (!TextUtils.isEmpty(select_id) && select_id.equals(bean.id)) {
//                        selected_logo.setVisibility(View.VISIBLE);
//                    } else selected_logo.setVisibility(View.GONE);
//                } else {
//                    selected_logo_edit.setVisibility(View.VISIBLE);
//                    selected_logo.setVisibility(View.GONE);
//                }

                if (bean.type.equals(BANK_TYPE)) {
                    if (otcBuySellLimitResponse != null) {
                        if (otcBuySellLimitResponse.card==null)zanbuzhichi.setVisibility(View.VISIBLE);
                    }
                    type_name.setText(itemView.getContext().getString(R.string.yinhangka));
                    Glide.with(itemView.getContext()).load(R.mipmap.hongse_bank).into(logo);
                    return;
                }
                if (bean.type.equals(WECHAT_TYPE)) {
                    if (otcBuySellLimitResponse != null) {
                        if (otcBuySellLimitResponse.wechat==null)zanbuzhichi.setVisibility(View.VISIBLE);
                    }
                    type_name.setText(itemView.getContext().getString(R.string.setting_wx));
                    Glide.with(itemView.getContext()).load(R.mipmap.weixin).into(logo);

                    return;
                }
                if (bean.type.equals(ALIPAY_TYPE)) {
                    if (otcBuySellLimitResponse != null) {
                        if (otcBuySellLimitResponse.alipay==null)zanbuzhichi.setVisibility(View.VISIBLE);
                    }
                    type_name.setText(itemView.getContext().getString(R.string.tixian_ali));
                    Glide.with(itemView.getContext()).load(R.mipmap.zhifubao).into(logo);
                    return;
                }
                if (bean.type.equals(USDT_TYPE)) {
                    if (otcBuySellLimitResponse != null) {
                        if (otcBuySellLimitResponse.usdt==null)zanbuzhichi.setVisibility(View.VISIBLE);
                    }
                    type_name.setText("USDT(ERC20)");
                    Glide.with(itemView.getContext()).load(R.mipmap.usdt).into(logo);
                    return;
                }
                name.setText(bean.name);
            }
        }

    }
}
