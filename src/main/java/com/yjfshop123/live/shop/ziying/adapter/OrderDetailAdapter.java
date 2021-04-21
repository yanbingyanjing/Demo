package com.yjfshop123.live.shop.ziying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailAddressHolder;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailGoodHolder;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailHistoryHolder;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderAddressHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderPeisongHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderShopDetailHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderShopTitleHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderTongjiHolder;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.shop.ziying.model.SubmitData;

import java.util.ArrayList;
import java.util.List;

import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.history_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.remark_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_item_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.total_type;

public class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SubmitData> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;


    public OrderDetailAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == address_type) {
            View itemView = layoutInflater.inflate(R.layout.item_ziying_order_detail_address, parent, false);
            OrderDetailAddressHolder submitBuyOrderAddressHolder = new OrderDetailAddressHolder(itemView,null) ;
            return submitBuyOrderAddressHolder;
        }
        if (viewType == shop_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_title, parent, false);
            return new SubmitBuyOrderShopTitleHolder(itemView);
        }
        if (viewType == shop_item_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_shop_detail, parent, false);
            return new OrderDetailGoodHolder(itemView);
        }

        if (viewType == total_type) {
            View itemView = layoutInflater.inflate(R.layout.item_order_detail, parent, false);
            return new OrderDetailHolder(itemView);
        }
        if (viewType == history_type) {
            View itemView = layoutInflater.inflate(R.layout.item_ziying_order_detail_history, parent, false);
            return new OrderDetailHistoryHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.get(position).type == history_type && mList.get(position).itemdata instanceof OrderDetail.HistoryItem) {
            ((OrderDetailHistoryHolder) holder).bind(context, (OrderDetail.HistoryItem) mList.get(position).itemdata);
        }
        if (mList.get(position).type == address_type && mList.get(position).itemdata !=null&& mList.get(position).itemdata instanceof OrderDetail.Logistics) {
            ((OrderDetailAddressHolder) holder).bind(context, (OrderDetail.Logistics) mList.get(position).itemdata,statusStr);
        }
        if (mList.get(position).type == address_type && mList.get(position).itemdata ==null) {
            ((OrderDetailAddressHolder) holder).bind(context, null,statusStr);
        }
        if (mList.get(position).type == shop_type && mList.get(position).itemdata instanceof String) {
            ((SubmitBuyOrderShopTitleHolder) holder).bind(context, (String) mList.get(position).itemdata);
        }
        if (mList.get(position).type == shop_item_type && mList.get(position).itemdata instanceof OrderDetail.GoodsItem) {
            ((OrderDetailGoodHolder) holder).bind(context, (OrderDetail.GoodsItem) mList.get(position).itemdata);
        }

        if (mList.get(position).type == total_type && mList.get(position).itemdata instanceof OrderDetail.OrderInfo) {
            ((OrderDetailHolder) holder).bind(context, (OrderDetail.OrderInfo) mList.get(position).itemdata);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    String statusStr;
    public void setCards(List<SubmitData> list,String statusStr) {
this.statusStr=statusStr;
        mList = list;
        notifyDataSetChanged();
    }

}
