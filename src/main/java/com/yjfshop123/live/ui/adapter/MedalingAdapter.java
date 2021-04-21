package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.MyXunZhangResponse;

import java.util.List;

public class MedalingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyXunZhangResponse.MyXunZhangData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public MedalingAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_medaling, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<MyXunZhangResponse.MyXunZhangData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView medal_level;
        TextView medal_need_egg;
        TextView medal_total_release_day;
        TextView medal_released_day;
        TextView start_time;
        TextView  create_time;
        public Vh(View itemView) {
            super(itemView);
            medal_level = itemView.findViewById(R.id.medal_level);
            medal_need_egg = itemView.findViewById(R.id.medal_need_egg);
            medal_total_release_day = itemView.findViewById(R.id.medal_total_release_day);
            medal_released_day = itemView.findViewById(R.id.medal_released_day);
            start_time = itemView.findViewById(R.id.start_time);
            create_time= itemView.findViewById(R.id.create_time);
        }

        void setData(MyXunZhangResponse.MyXunZhangData bean) {
            medal_level.setText(bean.xuanpin_name);
//            medal_need_egg.setText(bean.medal_exchange + context.getString(R.string.ge));
//            medal_total_release_day.setText(bean.medal_exchange + context.getString(R.string.tian));
//            medal_released_day.setText(context.getString(R.string.has_medaling_days,bean.medal_released_day));
            medal_need_egg.setText(bean.medal_exchange );
            medal_total_release_day.setText(bean.medal_total_release_day );
            medal_released_day.setText(bean.medal_released_day);
            start_time.setText(bean.effective_time);
            create_time.setText(bean.create_date);
        }

    }
}
