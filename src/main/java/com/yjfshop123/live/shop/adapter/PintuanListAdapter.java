package com.yjfshop123.live.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.model.PintuanResponse;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.ui.ShopDetailActivity;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PintuanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<PintuanResponse.PintuanData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public PintuanListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_pintuan_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<PintuanResponse.PintuanData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        ImageView pic;

        TextView pintuan_name, pintuan_shop_name,pintuan_users;
        TextView pintuan_invite_count, pintuan_shop_price,pintuan_buy;

        public Vh(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            pintuan_name = itemView.findViewById(R.id.pintuan_name);
            pintuan_shop_name = itemView.findViewById(R.id.pintuan_shop_name);
            pintuan_invite_count = itemView.findViewById(R.id.pintuan_invite_count);
            pintuan_users= itemView.findViewById(R.id.pintuan_users);
            pintuan_shop_price = itemView.findViewById(R.id.pintuan_shop_price);
            pintuan_buy = itemView.findViewById(R.id.pintuan_buy);

        }

        void setData(final PintuanResponse.PintuanData bean) {
            pintuan_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bean.status==0){
                        NToast.shortToast(context,"活动"+bean.status_desc);
                        return;
                    }

                    Intent intent=new Intent(context, NewShopDetailActivity.class);
                    intent.putExtra("goods_id",bean.goods_id);
                    intent.putExtra("pintuan_id",bean.pintuan_id);
                    context.startActivity(intent);
                }
            });
            pintuan_buy.setText(bean.status_desc);
            if (TextUtils.isEmpty(bean.goods_image))
                Glide.with(context).load(R.mipmap.zhanwei).into(pic);
            else {
                Glide.with(context).load(bean.goods_image).into(pic) ;
            }
            pintuan_name.setText(bean.desc);
            pintuan_shop_name.setText("商品："+bean.goods_name);
            pintuan_shop_price.setText(NumUtil.clearZero(bean.goods_price));
            pintuan_invite_count.setText("团长"+bean.nickname+","+context.getString(R.string.yi_invite_count,bean.invite_people+""));
            pintuan_users.setText("团员："+bean.pintuan_users);

        }

    }
}
