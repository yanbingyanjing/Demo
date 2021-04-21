package com.yjfshop123.live.shop.ziying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderAddressHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderPeisongHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderShopDetailHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderShopTitleHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderTongjiHolder;
import com.yjfshop123.live.shop.ziying.model.DefaultAddress;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.shop.ziying.model.SubmitData;
import com.yjfshop123.live.ui.adapter.TaskNewAdapter;
import com.yjfshop123.live.ui.viewholders.TaskNewItemHolder;
import com.yjfshop123.live.ui.viewholders.TaskTitleHolder;
import com.yjfshop123.live.ui.viewholders.TaskTopHolder;

import java.util.ArrayList;
import java.util.List;

import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.remark_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_item_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.total_type;

public class SubmitBuyOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SubmitData> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;


    public SubmitBuyOrderAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == address_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_address, parent, false);
            SubmitBuyOrderAddressHolder submitBuyOrderAddressHolder = new SubmitBuyOrderAddressHolder(itemView, mItemClickListener);

            return submitBuyOrderAddressHolder;
        }
        if (viewType == shop_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_title, parent, false);
            return new SubmitBuyOrderShopTitleHolder(itemView);
        }
        if (viewType == shop_item_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_shop_detail, parent, false);
            return new SubmitBuyOrderShopDetailHolder(itemView);
        }
        if (viewType == remark_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_peisong, parent, false);
            SubmitBuyOrderPeisongHolder submitBuyOrderPeisongHolder = new SubmitBuyOrderPeisongHolder(itemView, textWatcher,textWatcher2);
            return submitBuyOrderPeisongHolder;
        }
        if (viewType == total_type) {
            View itemView = layoutInflater.inflate(R.layout.item_submit_total_tongji, parent, false);
            return new SubmitBuyOrderTongjiHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mList.get(position).type == address_type && mList.get(position).itemdata != null && mList.get(position).itemdata instanceof DefaultAddress.AddressData) {
            ((SubmitBuyOrderAddressHolder) holder).bind(context, (DefaultAddress.AddressData) mList.get(position).itemdata);
        }
        if (mList.get(position).type == address_type && mList.get(position).itemdata == null) {
            ((SubmitBuyOrderAddressHolder) holder).bind(context, null);
        }
        if (mList.get(position).type == shop_type && mList.get(position).itemdata instanceof String) {
            ((SubmitBuyOrderShopTitleHolder) holder).bind(context, (String) mList.get(position).itemdata);
        }
        if (mList.get(position).type == shop_item_type && mList.get(position).itemdata instanceof OrderGoodsResponse.GoodsItemItem) {
            ((SubmitBuyOrderShopDetailHolder) holder).bind(context, (OrderGoodsResponse.GoodsItemItem) mList.get(position).itemdata);
        }
        if (mList.get(position).type == remark_type && mList.get(position).itemdata instanceof OrderGoodsResponse.GoodsItem) {

            ((SubmitBuyOrderPeisongHolder) holder).bind(context, (OrderGoodsResponse.GoodsItem) mList.get(position).itemdata,kind);
        }
        if (mList.get(position).type == total_type && mList.get(position).itemdata instanceof OrderGoodsResponse.GoodsItem) {
            ((SubmitBuyOrderTongjiHolder) holder).bind(context, (OrderGoodsResponse.GoodsItem) mList.get(position).itemdata);
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

    public void setCards(List<SubmitData> list) {

        mList = list;
        notifyDataSetChanged();
    }

    String kind;

    public void setKind(String kind) {

        this.kind = kind;

    }

    private MyItemClickListener mItemClickListener;

    public void setClick(MyItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    TextWatcher textWatcher;
    TextWatcher textWatcher2;
    public void setRemarkListener(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }
    public void setPhoneListener(TextWatcher textWatcher2) {
        this.textWatcher2 = textWatcher2;
    }

    public interface MyItemClickListener {
        void onItemClickAddress(View view, int position);

    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
