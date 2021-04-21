package com.yjfshop123.live.shop.ziying.adapter;

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
import com.yjfshop123.live.shop.adapter.ShopAdapter;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.ui.ShopDetailActivity;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NewShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ZiyingShopList.Data> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public NewShopAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_new_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<ZiyingShopList.Data> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        ImageView main_pic;
        LinearLayout item_ll;
        TextView title;
        TextView actualPrice,monthSales;

        public Vh(View itemView) {
            super(itemView);

            actualPrice = itemView.findViewById(R.id.actualPrice);
            monthSales = itemView.findViewById(R.id.monthSales);
            title = itemView.findViewById(R.id.title);
            main_pic = itemView.findViewById(R.id.main_pic);
            item_ll = itemView.findViewById(R.id.item_ll);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_pic.getLayoutParams();
            //获取当前控件的布局对象

            params.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 40)) / 2;
            params.height = params.width;//设置当前控件布局的高度
            main_pic.setLayoutParams(params);

            LinearLayout.LayoutParams item_llparams = (LinearLayout.LayoutParams) item_ll.getLayoutParams();
            //获取当前控件的布局对象

            item_llparams.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 40)) / 2;
            item_llparams.height = WRAP_CONTENT;//设置当前控件布局的高度
            item_ll.setLayoutParams(item_llparams);
        }

        void setData(final ZiyingShopList.Data bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, NewShopDetailActivity.class);
                    intent.putExtra("goods_id",bean.goods_id+"");
                    context.startActivity(intent);
                }
            });
            if (TextUtils.isEmpty(bean.image))
                Glide.with(context).load(R.mipmap.zhanwei).into(main_pic);
            else {
                if(!bean.image.contains("http")){
                    bean.image="http:"+bean.image;
                }
                RequestOptions userAvatarOptions = new RequestOptions() //.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.image + name)).error(R.mipmap.zhanwei)
                        .encodeQuality(70);
                Glide.with(context).load(bean.image).apply(userAvatarOptions).into(main_pic) ;
            }
            actualPrice.setText( NumUtil.clearZero(bean.price));
            monthSales.setText("已售"+ NumUtil.formatNum(bean.sale_count,false));
                title.setText(bean.name);


        }

    }
}
