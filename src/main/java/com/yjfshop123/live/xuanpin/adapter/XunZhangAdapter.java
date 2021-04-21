package com.yjfshop123.live.xuanpin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.NewSilverEggResponse;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.ui.activity.yinegg.NewSilverEggAdapter;

import java.util.List;

public class XunZhangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;

    public XunZhangAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Vh(layoutInflater.inflate(R.layout.item_xunzhang_xuanpin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    List<XuanPInResopnse.MedalIten> data;

    public void setHeadData(List<XuanPInResopnse.MedalIten> data) {
        if (data == null) {
            return;
        }
        this.data = data;
        notifyDataSetChanged();
    }

    int index = 0;

    public class Vh extends RecyclerView.ViewHolder {
        TextView egg;


        public Vh(View itemView) {
            super(itemView);
            egg = itemView.findViewById(R.id.egg);

        }

        void setData(XuanPInResopnse.MedalIten bean) {
            if (bean == null) return;

            egg.setText(bean.medal_exchange+ "金蛋" +(bean.medal_level==0?"(体验)":""));
            if (getLayoutPosition() == index) {
                egg.setSelected(true);
                if (onClick != null) onClick.Click(getLayoutPosition());
            } else egg.setSelected(false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLayoutPosition() != index) {
                        index = getLayoutPosition();
                        notifyDataSetChanged();
                    }
                }
            });
        }

    }

    public interface OnClick {
        void Click(int position);
    }

}