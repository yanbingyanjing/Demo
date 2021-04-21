package com.yjfshop123.live.xuanpin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.shop.ui.ShopDetailActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.xuanpin.view.ShopSelectDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelectShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<XuanPInResopnse.ItemData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public SelectShopAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_select_shop_l, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }
    ShopSelectDialog.Onclick onclick;
    public void setOnclick( ShopSelectDialog.Onclick onclick){
        this.onclick=onclick;
    }
    public void setCards(List<XuanPInResopnse.ItemData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        ImageView  shop_logo;
        TextView title;
        TextView actualPrice;

        public Vh(View itemView) {
            super(itemView);

            actualPrice = itemView.findViewById(R.id.actualPrice);

            shop_logo = itemView.findViewById(R.id.main_pic);

            title = itemView.findViewById(R.id.title);


        }

        void setData(final XuanPInResopnse.ItemData bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if(onclick!=null)onclick.OnClick(bean);
                }
            });

            if (TextUtils.isEmpty(bean.pic))
                Glide.with(context).load(R.mipmap.shangdian).into(shop_logo);
            else {

                RequestOptions userAvatarOptions = new RequestOptions() //.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.pic + name)).error(R.mipmap.shangdian)
                        .encodeQuality(70);
                Glide.with(context).load(CommonUtils.getUrl(bean.pic)).apply(userAvatarOptions).into(shop_logo);
            }

            actualPrice.setText(NumUtil.clearZero(bean.egg_price));
            title.setText(bean.title);


        }

    }
}
