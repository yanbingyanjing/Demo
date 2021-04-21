package com.yjfshop123.live.ui.activity.yinegg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.model.NewSilverEggResponse;
import com.yjfshop123.live.model.UnlockSilverEggResponse;
import com.yjfshop123.live.utils.NumUtil;

import java.util.List;

public class NewSilverEggAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UnlockSilverEggResponse.Data> mList;
    private LayoutInflater layoutInflater;
    private Context context;
    private NewSilverEggResponse silverEggResponse;

    public NewSilverEggAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new Head(layoutInflater.inflate(R.layout.item_new_silver_head, parent, false));

        }
        return new Vh(layoutInflater.inflate(R.layout.item_new_silver_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= 1) {
            ((Vh) holder).setData(mList.get(position-1));
        } else ((Head) holder).setData(silverEggResponse);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() + 1 : 1;
    }

    public void setHeadData(NewSilverEggResponse silverEggResponse) {
        if (silverEggResponse == null) {
            return;
        }
        this.silverEggResponse = silverEggResponse;
        notifyDataSetChanged();
    }
    View.OnClickListener onClickListener;
    public void setExchangeOnClick(View.OnClickListener onClickListener) {

        this.onClickListener = onClickListener;
    }
    public void setListData(List<UnlockSilverEggResponse.Data> list) {
        if (list == null) {
            return;
        }
        this.mList = list;
        notifyDataSetChanged();

    }

    public class PriceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new Vh(layoutInflater.inflate(R.layout.item_new_silver_price, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((Vh) holder).setData(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        List<NewSilverEggResponse.UpEntity> data;

        public void setHeadData(List<NewSilverEggResponse.UpEntity> data) {
            if (data == null) {
                return;
            }
            this.data = data;
            notifyDataSetChanged();
        }

        public class Vh extends RecyclerView.ViewHolder {
            TextView price;
            TextView up_down;
            TextView date;


            public Vh(View itemView) {
                super(itemView);
                price = itemView.findViewById(R.id.price);
                up_down = itemView.findViewById(R.id.up_down);
                date = itemView.findViewById(R.id.date);

            }

            void setData(NewSilverEggResponse.UpEntity bean) {
                if (bean == null) return;
                price.setText(bean.gold_price);
              //  up_down.setText(bean.up_percent);
                date.setText(bean.time);

//                if (bean.is_up) {
//                    Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.up_rise);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    up_down.setCompoundDrawables(drawable, null, null, null);
//                } else {
//                    Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.down_rise);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    up_down.setCompoundDrawables(drawable, null, null, null);
//                }
            }

        }


    }

    private LinearLayoutManager mLinearLayoutManager;
    private PriceAdapter priceAdapter;

    public class Head extends RecyclerView.ViewHolder {
        TextView current_price;
        TextView current_up_down;
        TextView lock_count;
        TextView release_count;
        TextView exchange;
        RecyclerView list;
       LinearLayout gold_price_detail;
        public Head(View itemView) {
            super(itemView);
            gold_price_detail = itemView.findViewById(R.id.gold_price_detail);
            list = itemView.findViewById(R.id.list);
            current_price = itemView.findViewById(R.id.current_price);
            current_up_down = itemView.findViewById(R.id.current_up_down);
            lock_count = itemView.findViewById(R.id.lock_count);
            release_count = itemView.findViewById(R.id.release_count);
            exchange = itemView.findViewById(R.id.exchange);
            mLinearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            list.setLayoutManager(mLinearLayoutManager);
            priceAdapter = new PriceAdapter();
            list.setAdapter(priceAdapter);
        }

        void setData(NewSilverEggResponse bean) {
            if (bean == null) return;
            gold_price_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,GoldPriceActivity.class);
                    ((Activity)context).startActivity(intent);
                }
            });
            //priceAdapter.setHeadData(bean.goldEggPrices);
            current_price.setText(bean.gold_price);
            lock_count.setText(bean.lockedEgg);
            release_count.setText(bean.releasedEgg);
            exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickListener!=null)onClickListener.onClick(v);
                }
            });
//            current_up_down.setText(bean.up_percent);
//            if (bean.is_up) {
//                Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.up_rise);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                current_up_down.setCompoundDrawables(drawable, null, null, null);
//            } else {
//                Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.down_rise);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                current_up_down.setCompoundDrawables(drawable, null, null, null);
//            }
        }

    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView order_id;
        ImageView one, two, three;
        TextView one_lock_status, two_lock_status, three_lock_status;
        TextView one_price, two_price, three_price;
        TextView locked_count, locked_status, first_locked_price, locked_time;
LinearLayout list_ll;
        public Vh(View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            one = itemView.findViewById(R.id.one);
            two = itemView.findViewById(R.id.two);
            three = itemView.findViewById(R.id.three);
            one_lock_status = itemView.findViewById(R.id.one_lock_status);
            two_lock_status = itemView.findViewById(R.id.two_lock_status);
            three_lock_status = itemView.findViewById(R.id.three_lock_status);

            one_price = itemView.findViewById(R.id.one_price);
            two_price = itemView.findViewById(R.id.two_price);
            three_price = itemView.findViewById(R.id.three_price);

            locked_count = itemView.findViewById(R.id.locked_count);
            locked_status = itemView.findViewById(R.id.locked_status);
            first_locked_price = itemView.findViewById(R.id.first_locked_price);
            locked_time = itemView.findViewById(R.id.locked_time);


            list_ll=itemView.findViewById(R.id.list_ll);
        }

        void setData(final UnlockSilverEggResponse.Data bean) {
            if (bean == null) return;
            order_id.setText(bean.order_id);
            list_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,NewSilverListActivity.class);
                    intent.putExtra("time",bean.lock_time);
                    context.startActivity(intent);
                }
            });
            //设置解锁状态
            if (!TextUtils.isEmpty(bean.has_unlock_times)) {
                int time = Integer.parseInt(bean.has_unlock_times);
                if (time >= 1) {
                    one_lock_status.setText(TextUtils.isEmpty(bean.first_unlock_count) ? "" : context.getString(R.string.yijiesuo, NumUtil.clearZero(bean.first_unlock_count)));
                    Glide.with(context).load(R.mipmap.unloaced_bg).into(one);
                    one_price.setBackgroundResource(R.drawable.bg_graditent_fae2ae_b28d51_top_12);
                }
                if (time >= 2) {
                    Glide.with(context).load(R.mipmap.unloaced_bg).into(two);
                    two_price.setBackgroundResource(R.drawable.bg_graditent_fae2ae_b28d51_top_12);
                    two_lock_status.setText(TextUtils.isEmpty(bean.second_unlock_count) ? "" : context.getString(R.string.yijiesuo, NumUtil.clearZero(bean.second_unlock_count)));
                }
                if (time >= 3) {
                    Glide.with(context).load(R.mipmap.unloaced_bg).into(three);
                    three_price.setBackgroundResource(R.drawable.bg_graditent_fae2ae_b28d51_top_12);
                    three_lock_status.setText(TextUtils.isEmpty(bean.third_unlock_count) ? "" : context.getString(R.string.yijiesuo, NumUtil.clearZero(bean.third_unlock_count)));

                }

                if (time < 1) {
                    Glide.with(context).load(R.mipmap.locked_bg).into(one);
                    one_price.setBackgroundResource(R.drawable.bg_graditent_c2d4ff_59450f0_top_12);
                    one_lock_status.setText(TextUtils.isEmpty(bean.first_unlock_count) ? "" : context.getString(R.string.weijiesuo, NumUtil.clearZero(bean.first_unlock_count)));
                }
                if (time < 2) {
                    Glide.with(context).load(R.mipmap.locked_bg).into(two);
                    two_price.setBackgroundResource(R.drawable.bg_graditent_c2d4ff_59450f0_top_12);
                    two_lock_status.setText(TextUtils.isEmpty(bean.second_unlock_count) ? "" : context.getString(R.string.weijiesuo, NumUtil.clearZero(bean.second_unlock_count)));

                }
                if (time < 3) {
                    Glide.with(context).load(R.mipmap.locked_bg).into(three);
                    three_price.setBackgroundResource(R.drawable.bg_graditent_c2d4ff_59450f0_top_12);
                    three_lock_status.setText(TextUtils.isEmpty(bean.third_unlock_count) ? "" : context.getString(R.string.weijiesuo, NumUtil.clearZero(bean.third_unlock_count)));

                }
            }
            //设置解锁价格
            one_price.setText(TextUtils.isEmpty(bean.first_unlock_price) ? "" : context.getString(R.string.jiesuo_price, NumUtil.clearZero(bean.first_unlock_price)));
            three_price.setText(TextUtils.isEmpty(bean.third_unlock_price) ? "" : context.getString(R.string.jiesuo_price, NumUtil.clearZero(bean.third_unlock_price)));
            two_price.setText(TextUtils.isEmpty(bean.second_unlock_price) ? "" : context.getString(R.string.jiesuo_price, NumUtil.clearZero(bean.second_unlock_price)));

            locked_count.setText(TextUtils.isEmpty(bean.locked_count) ? "" : bean.locked_count);
            locked_status.setText(TextUtils.isEmpty(bean.has_unlock_times) ? "" : context.getString(R.string.yijiesuo, bean.has_unlock_times + "/3"));
            first_locked_price.setText(TextUtils.isEmpty(bean.first_lock_price) ? "" : bean.first_lock_price);
            locked_time.setText(TextUtils.isEmpty(bean.lock_time) ? "" : bean.lock_time);
        }

    }
}


























































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































