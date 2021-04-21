package com.yjfshop123.live.shop.ziying.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopDetail;
//购买弹窗中规格的适配器
public class NewShopBuyGuigeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;

    public NewShopBuyGuigeAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new Vh(layoutInflater.inflate(R.layout.item_ziying_shop_buy, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(data[position]);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.length : 0;
    }


    ZiyingShopDetail.SpecData[] data;

    public void setHeadData(ZiyingShopDetail.SpecData[] data) {
        if (data == null) {
            return;
        }
        this.data = data;
        notifyDataSetChanged();
    }

    int index = 0;

    public class Vh extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView list;
        LinearLayoutManager mLinearLayoutManager;
        GuigeAdapter priceAdapter;

        public Vh(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            list = itemView.findViewById(R.id.list);
            mLinearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            list.setLayoutManager(mLinearLayoutManager);
            priceAdapter = new GuigeAdapter(context);
            list.setAdapter(priceAdapter);
        }

        void setData(ZiyingShopDetail.SpecData bean) {
            if (bean == null) return;
            name.setText(bean.name);
            priceAdapter.setHeadData(bean.value,getLayoutPosition());
        }

    }

    OnSelectGuiGe onSelectGuiGe;

    public void setSelect(OnSelectGuiGe onSelectGuiGe) {
        this.onSelectGuiGe = onSelectGuiGe;
    }

    public interface OnSelectGuiGe {
        void onClick(int specIndex, int valueIndex);
    }


    public class GuigeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater layoutInflater;
        private Context context;
        private int specIndex = -1;

        public GuigeAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new GuigeAdapter.Vh(layoutInflater.inflate(R.layout.item_guige_tx, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((Vh) holder).setData(data[position]);
        }

        @Override
        public int getItemCount() {
            return data != null ? data.length : 0;
        }


        ZiyingShopDetail.SpecData.SpecItemData[] data;

        public void setHeadData(ZiyingShopDetail.SpecData.SpecItemData[] data,int specIndex ) {
            if (data == null) {
                return;
            }
            this.specIndex = specIndex;
            this.data = data;
            notifyDataSetChanged();
        }

        int index = 0;

        public class Vh extends RecyclerView.ViewHolder {
            TextView guige;


            public Vh(View itemView) {
                super(itemView);
                guige = itemView.findViewById(R.id.guige);

            }

            void setData(final ZiyingShopDetail.SpecData.SpecItemData bean) {
                if (bean == null) return;
                guige.setText(bean.name);
                guige.setSelected(bean.active);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.active) {
                            return;
                        }
                        if (onSelectGuiGe != null) {
                            onSelectGuiGe.onClick(specIndex, getLayoutPosition());
                        }
                    }
                });

            }

        }

    }

}
