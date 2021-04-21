package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.utils.NumUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;

public class AdditionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<AdditionResponse.AdditionData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public AdditionAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_addition, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<AdditionResponse.AdditionData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView left_addition;
        TextView shao_shang;
        TextView origin;
        TextView name;
        TextView deNotificationStart;
        TextView jine;
        TextView hasJinxingDaying;
        TextView dayIncome;
        TextView shao_shang_title;
        TextView    de_notification_end;
        public Vh(View itemView) {
            super(itemView);
            de_notification_end = itemView.findViewById(R.id.de_notification_end);
            shao_shang = itemView.findViewById(R.id.shao_shang);
            origin = itemView.findViewById(R.id.origin);
            left_addition = itemView.findViewById(R.id.left_addition);
            jine = itemView.findViewById(R.id.jine);
            name = itemView.findViewById(R.id.name);
            shao_shang_title = itemView.findViewById(R.id.shao_shang_title);
            deNotificationStart = itemView.findViewById(R.id.de_notification_start);
            hasJinxingDaying = itemView.findViewById(R.id.has_jinxing_daying);
            dayIncome = itemView.findViewById(R.id.day_income);
        }

        void setData(AdditionResponse.AdditionData bean) {
            shao_shang_title.setVisibility(View.GONE);
            shao_shang.setVisibility(View.GONE);
            shao_shang.setText("0");
            de_notification_end.setText(bean.end_time);
            origin.setText(bean.addition_origin);
            left_addition.setText(NumUtil.clearZero(bean.addition_left));
            jine.setText(NumUtil.clearZero(bean.addition_amount));
            deNotificationStart.setText(bean.start_time);
            hasJinxingDaying.setText(bean.addition_released + "/" + bean.addition_days);
            dayIncome.setText(NumUtil.clearZero(bean.addition_day_income));
            name.setText(bean.addition_user+"(ID:"+bean.addition_user_id+")");
            if (!TextUtils.isEmpty(bean.burn) && new BigDecimal(bean.burn).compareTo(BigDecimal.ZERO) > 0) {
                shao_shang_title.setVisibility(View.VISIBLE);
                shao_shang.setVisibility(View.VISIBLE);
                shao_shang.setText(NumUtil.clearZero(bean.burn));
            }

        }

    }
}
