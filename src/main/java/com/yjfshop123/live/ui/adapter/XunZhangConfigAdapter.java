package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.model.LevelResponse;
import com.yjfshop123.live.model.XunZhangConfigResponse;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.GlideRoundImage;
import com.yjfshop123.live.xuanpin.ui.XuanPinActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class XunZhangConfigAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private XunZhangConfigResponse.XunZhangConfigData[] mList;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public XunZhangConfigAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_xunzhang_config_one, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList[position]);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.length : 0;
    }

    String gold_egg;

    public void setCards(XunZhangConfigResponse.XunZhangConfigData[] list, String gold_egg) {
        if (list == null) {
            return;
        }
        mList = list;
        this.gold_egg = gold_egg;
        notifyDataSetChanged();
    }

    BuyOnClisk buyOnClick;

    public void setBuyOnClick(BuyOnClisk buyOnClick) {
        this.buyOnClick = buyOnClick;

    }

    public interface BuyOnClisk {
        void onClick(XunZhangConfigResponse.XunZhangConfigData bean);
    }

    public class Vh extends RecyclerView.ViewHolder {

        ImageView bg;
        TextView exchangeNeed;
        TextView income_percent;
        TextView medalActivityNum;
        TextView maxBuyCount;
        TextView lock_days;
        TextView effective_time;
        TextView   unit;
        public Vh(View itemView) {
            super(itemView);

            unit= itemView.findViewById(R.id.unit);
            exchangeNeed = itemView.findViewById(R.id.exchange_need);
            lock_days = itemView.findViewById(R.id.medal_total_release_day);
            income_percent = itemView.findViewById(R.id.income_percent);
            medalActivityNum = itemView.findViewById(R.id.medal_activity_num);
            maxBuyCount = itemView.findViewById(R.id.max_buy_count);
            effective_time = itemView.findViewById(R.id.effective_time);
            bg = itemView.findViewById(R.id.bg);
        }

        RequestOptions userAvatarOptions = new RequestOptions()
                .transform(new GlideRoundImage(8));

        void setData(final XunZhangConfigResponse.XunZhangConfigData bean) {

            if (bean.medal_level == 0) {
                unit.setText("个（体验）");
                bg.setBackgroundResource(R.mipmap.bg_config_zero);
            }else unit.setText("个");
            if (bean.medal_level == 1) {

                bg.setBackgroundResource(R.mipmap.bg_config_one);
            }
            if (bean.medal_level == 2) {

                bg.setBackgroundResource(R.mipmap.bg_config_two);
            }
            if (bean.medal_level == 3) {

                bg.setBackgroundResource(R.mipmap.bg_config_three);
            }
            if (bean.medal_level == 4) {

                bg.setBackgroundResource(R.mipmap.bg_config_four);
            }
            if (bean.medal_level == 5) {

                bg.setBackgroundResource(R.mipmap.bg_config_five);
            }
            if (bean.medal_level == 6) {

                bg.setBackgroundResource(R.mipmap.bg_config_six);
            }
            if (bean.medal_level == 7) {

                bg.setBackgroundResource(R.mipmap.bg_config_seven);
            }
            if (bean.medal_level == 8) {

                bg.setBackgroundResource(R.mipmap.bg_config_eight);
            }
            if (bean.medal_level == 9) {

                bg.setBackgroundResource(R.mipmap.bg_config_nine);
            }
            effective_time.setText(bean.effective_time_des);

            exchangeNeed.setText(bean.medal_exchange);
            lock_days.setText(bean.medal_total_release_day);
            income_percent.setText(bean.medal_income_percent);
            medalActivityNum.setText(bean.medal_activity_num);
            maxBuyCount.setText(bean.max_buy_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent xuanpin = new Intent();
                    xuanpin.setClass(context, XuanPinActivity.class);
                    context.startActivity(xuanpin);
                }
            });
        }
    }

}
