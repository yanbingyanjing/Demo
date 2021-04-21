package com.yjfshop123.live.live.live.common.widget.other;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.PayoutRankingResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContributeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PayoutRankingResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public ContributeAdapter(Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_live_contribute, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<PayoutRankingResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class Vh extends RecyclerView.ViewHolder {

        private ImageView number_icon;
        private ImageView mAvatar;
        private TextView number;
        private TextView mName;
        private ImageView mSex;
        private TextView btn_gold;

        public Vh(View itemView) {
            super(itemView);
            number_icon = (ImageView) itemView.findViewById(R.id.number_icon);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            number = (TextView) itemView.findViewById(R.id.number);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            btn_gold = (TextView) itemView.findViewById(R.id.btn_gold);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(null, getLayoutPosition());
                }
            });
        }

        void setData(PayoutRankingResponse.ListBean bean, int position) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getShow_photo()))
                    .into(mAvatar);

            mName.setText(bean.getUser_nickname());
            if (bean.getSex() == 1){
                mSex.setImageResource(R.drawable.ic_voice_sex_man);
            }else {
                mSex.setImageResource(R.drawable.ic_voice_sex_women);
            }

            btn_gold.setText(bean.getCoin() + context.getString(R.string.my_jinbi));

            position = position + 1;
            if (position == 1){
                number_icon.setVisibility(View.VISIBLE);
                number.setVisibility(View.GONE);
                number_icon.setImageResource(R.drawable.contribute_1);
            }else if (position == 2){
                number_icon.setVisibility(View.VISIBLE);
                number.setVisibility(View.GONE);
                number_icon.setImageResource(R.drawable.contribute_2);
            }else if (position == 3){
                number_icon.setVisibility(View.VISIBLE);
                number.setVisibility(View.GONE);
                number_icon.setImageResource(R.drawable.contribute_3);
            }else {
                number_icon.setVisibility(View.GONE);
                number.setVisibility(View.VISIBLE);
                number.setText(String.valueOf(position));
            }
        }
    }

}



