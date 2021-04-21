package com.yjfshop123.live.xuanpin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailXuanPinActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class XuanPinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<XuanPInResopnse.ItemData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public XuanPinAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_xuanpin, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<XuanPInResopnse.ItemData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    OnClick oncLick;

    public void setOncLick(OnClick oncLick) {
        this.oncLick = oncLick;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        ImageView main_pic;
        LinearLayout item_ll;
        TextView title;
        TextView actualPrice, monthSales, select;

        public Vh(View itemView) {
            super(itemView);

            select = itemView.findViewById(R.id.select);
            actualPrice = itemView.findViewById(R.id.actualPrice);
            monthSales = itemView.findViewById(R.id.monthSales);
            title = itemView.findViewById(R.id.title);
            main_pic = itemView.findViewById(R.id.main_pic);
            item_ll = itemView.findViewById(R.id.item_ll);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_pic.getLayoutParams();
            //获取当前控件的布局对象

            params.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 51)) / 2;
            params.height = params.width;//设置当前控件布局的高度
            main_pic.setLayoutParams(params);

            LinearLayout.LayoutParams item_llparams = (LinearLayout.LayoutParams) item_ll.getLayoutParams();
            //获取当前控件的布局对象

            item_llparams.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 51)) / 2;
            item_llparams.height = WRAP_CONTENT;//设置当前控件布局的高度
            item_ll.setLayoutParams(item_llparams);
        }

        void setData(final XuanPInResopnse.ItemData bean) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oncLick!=null)oncLick.DetailClick(bean);
                }
            });
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(oncLick!=null)oncLick.Click(bean);
                }
            });
            if (TextUtils.isEmpty(bean.pic))
                Glide.with(context).load(R.mipmap.zhanwei).into(main_pic);
            else {
                RequestOptions userAvatarOptions = new RequestOptions() //.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.pic + name)).error(R.mipmap.zhanwei)
                        .encodeQuality(70);
                Glide.with(context).load(CommonUtils.getUrl(bean.pic)).apply(userAvatarOptions).into(main_pic);
            }
            actualPrice.setText(bean.price);
            monthSales.setText(NumUtil.formatNum(bean.buy_times, false) + "人已购买");
            title.setText(bean.title);
        }

    }

    public interface OnClick {
        void Click(XuanPInResopnse.ItemData bean);
        void DetailClick(XuanPInResopnse.ItemData bean);
    }
}
