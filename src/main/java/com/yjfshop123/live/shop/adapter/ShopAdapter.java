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
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.ui.ShopDetailActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ShopList.ShopData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public ShopAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<ShopList.ShopData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        ImageView main_pic, shop_logo;
        LinearLayout item_ll;
        TextView title, shop_name;
        TextView actualPrice, originalPrice,monthSales,couponPrice;

        public Vh(View itemView) {
            super(itemView);

            couponPrice = itemView.findViewById(R.id.couponPrice);
            actualPrice = itemView.findViewById(R.id.actualPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            monthSales = itemView.findViewById(R.id.monthSales);
            originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            shop_logo = itemView.findViewById(R.id.shop_logo);
            shop_name = itemView.findViewById(R.id.shop_name);
            title = itemView.findViewById(R.id.title);
            main_pic = itemView.findViewById(R.id.main_pic);
            item_ll = itemView.findViewById(R.id.item_ll);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_pic.getLayoutParams();
            //获取当前控件的布局对象

            params.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 38)) / 2;
            params.height = params.width;//设置当前控件布局的高度
            main_pic.setLayoutParams(params);

            LinearLayout.LayoutParams item_llparams = (LinearLayout.LayoutParams) item_ll.getLayoutParams();
            //获取当前控件的布局对象

            item_llparams.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 38)) / 2;
            item_llparams.height = WRAP_CONTENT;//设置当前控件布局的高度
            item_ll.setLayoutParams(item_llparams);
        }

        void setData(final ShopList.ShopData bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ShopDetailActivity.class);
                    intent.putExtra("data",new Gson().toJson(bean));
                    context.startActivity(intent);
                }
            });
            if (TextUtils.isEmpty(bean.mainPic))
                Glide.with(context).load(R.mipmap.zhanwei).into(main_pic);
            else {
                if(!bean.mainPic.contains("http")){
                    bean.mainPic="http:"+bean.mainPic;
                }
                RequestOptions userAvatarOptions = new RequestOptions() //.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.mainPic + name)).error(R.mipmap.zhanwei)
                        .encodeQuality(70);
                Glide.with(context).load(bean.mainPic).apply(userAvatarOptions).into(main_pic) ;
            }

            if (TextUtils.isEmpty(bean.shopLogo))
                Glide.with(context).load(R.mipmap.shangdian).into(shop_logo);
            else {
                if(!bean.shopLogo.contains("http")){
                    bean.shopLogo="http:"+bean.shopLogo;
                }
                RequestOptions userAvatarOptions = new RequestOptions() //.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.shopLogo + name)).error(R.mipmap.shangdian)
                        .encodeQuality(70);
                Glide.with(context).load(bean.shopLogo).apply(userAvatarOptions).into(shop_logo);
            }
            couponPrice.setText("券¥"+bean.couponPrice);
            shop_name.setText(bean.shopName);
            actualPrice.setText(bean.actualPrice);
            originalPrice.setText("¥"+bean.originalPrice);
            monthSales.setText("已售"+ NumUtil.formatNum(bean.monthSales,false));
            if (bean.shopType == 1) {
                SpannableString msp = new SpannableString("  " + bean.dtitle);
                Drawable rightDrawable = context.getResources().

                        getDrawable(R.mipmap.tianmao);

                rightDrawable.setBounds(0, 0,

                        SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 13));
                msp.setSpan(new ImageSpan(rightDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(msp);

            } else {
                SpannableString msp = new SpannableString("  " + bean.dtitle);
                Drawable rightDrawable = context.getResources().
                        getDrawable(R.mipmap.taobao);
                rightDrawable.setBounds(0, 0,
                        SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 13));
                msp.setSpan(new ImageSpan(rightDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(msp);
            }

        }

    }
}
