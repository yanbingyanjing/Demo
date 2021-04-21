package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.CoinRecordListResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 描述:交易记录适配器
 **/
public class JiaoYiRecodeAdapter extends RecyclerView.Adapter<JiaoYiRecodeAdapter.ItemHolder> {

    private Context context;
    private ArrayList<CoinRecordListResponse.ListBean> lists;
    private String type;

    public JiaoYiRecodeAdapter(Context context, ArrayList<CoinRecordListResponse.ListBean> lists, String type) {
        this.context = context;
        this.lists = lists;
        this.type = type;
    }

    @Override
    public JiaoYiRecodeAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jiaoyi_recode, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(JiaoYiRecodeAdapter.ItemHolder holder, int position) {
        CoinRecordListResponse.ListBean bean = lists.get(position);
        if (type.equals("TX")){
            if (bean.getStatus() == 1){
                holder.nameTxt.setText(bean.getSubject() + "(审批中)");
            }else if (bean.getStatus() == 2){
                holder.nameTxt.setText(bean.getSubject() + "(审批通过打款中)");
            }else if (bean.getStatus() == 3){
                holder.nameTxt.setText(bean.getSubject() + "(已打款完成)");
            }else if (bean.getStatus() == 10){
                holder.nameTxt.setText(bean.getSubject() + "(审批拒绝)");
            }
        }else {
            holder.nameTxt.setText(bean.getSubject());
        }
        holder.timeTxt.setText(bean.getCreate_time());
        holder.moneyTxt.setText(bean.getMoney());
        holder.jinbiTxt.setText(bean.getCoin());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameTxt)
        TextView nameTxt;
        @BindView(R.id.timeTxt)
        TextView timeTxt;
        @BindView(R.id.moneyTxt)
        TextView moneyTxt;
        @BindView(R.id.jinbiTxt)
        TextView jinbiTxt;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
