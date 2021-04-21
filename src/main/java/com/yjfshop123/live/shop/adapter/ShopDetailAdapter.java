package com.yjfshop123.live.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.model.PrivilegeLink;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.CommonUtil;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.zyao89.view.zloading.ZLoadingDialog;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class ShopDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ShopList.Img> detailPic;
    private List<ShopList.ShopData> mList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ShopList.ShopData shopData;

    public ShopDetailAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == head_type) {
            return new Vh(layoutInflater.inflate(R.layout.item_shop_detail_head, parent, false));
        } else if (viewType == detailPic_type) {
            return new DetailVh(layoutInflater.inflate(R.layout.item_image, parent, false));
        }
        return new Vh(layoutInflater.inflate(R.layout.item_shop_detail_head, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((Vh) holder).setData(shopData);
        } else if (position > 0 && position < detailPic.size() + 1) {
            ((DetailVh) holder).setData(detailPic.get(position - 1));
        }
    }

    boolean isScrolling = false;
    ZLoadingDialog dialog;

    public void setZLoadingDialog(ZLoadingDialog dialog) {
        this.dialog = dialog;
    }

    int head_type = 1;//商品介绍类型
    int detailPic_type = 2;//图文详情
    int tuijian_type = 3;//推荐喜欢

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return head_type;
        } else if (position > 0 && position < detailPic.size() + 1) {
            return detailPic_type;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return detailPic != null ? detailPic.size() + 1 : 1;
    }

    public void setDetailPic(List<ShopList.Img> detailPic) {
        if (detailPic == null) {
            return;
        }
        this.detailPic = detailPic;
        notifyDataSetChanged();
    }

    public void setShopDate(ShopList.ShopData date) {
        if (date == null) {
            return;
        }
        shopData = date;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        TextView title, youhui_time, desc;
        TextView actualPrice, originalPrice, monthSales, couponPrice;
        TextView qianggou;

        public Vh(View itemView) {
            super(itemView);
            youhui_time = itemView.findViewById(R.id.youhui_time);
            qianggou = itemView.findViewById(R.id.qianggou);
            couponPrice = itemView.findViewById(R.id.couponPrice);
            actualPrice = itemView.findViewById(R.id.actualPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            monthSales = itemView.findViewById(R.id.monthSales);
            originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
        }

        void setData(final ShopList.ShopData bean) {
            qianggou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (CommonUtil.isPkgInstalled(context, "com.taobao.taobao")) {
//
//                        CommonUtil.gotoGoodsDetail((Activity) context, bean.itemLink);
//                        CommonUtil.gotoCoupon((Activity) context, bean.couponLink);
//                    } else {
//                        //没安装淘宝客户端
//                        CommonUtil.openBrowser(context, "https://" + "market.m.taobao.com/apps/aliyx/coupon/detail.html?sellerId=2200723171488&activityId=8a1a9c28cc9b40e58aa8a9d8b5562dab");
//                    }
                    getZhuanLianData();
                }
            });
            if (bean == null) return;
            if (!TextUtils.isEmpty(bean.couponStartTime) && !TextUtils.isEmpty(bean.couponEndTime)) {
                if (bean.couponStartTime.length() > 10) {
                    bean.couponStartTime = bean.couponStartTime.substring(0, 10);
                }
                if (bean.couponEndTime.length() > 10) {
                    bean.couponEndTime = bean.couponEndTime.substring(0, 10);
                }
                bean.couponEndTime.replace("-", ".");
                bean.couponStartTime.replace("-", ".");
                youhui_time.setText(bean.couponStartTime.substring(0, 10) + "-" + bean.couponEndTime.substring(0, 10));
            }
            desc.setText("产品优势:" + bean.desc);
            couponPrice.setText(bean.couponPrice);
            actualPrice.setText(bean.actualPrice);
            originalPrice.setText("¥" + bean.originalPrice);
            monthSales.setText("已售" + NumUtil.formatNum(bean.monthSales, false));
            if (bean.shopType == 1) {
                SpannableString msp = new SpannableString("  " + bean.title);
                Drawable rightDrawable = context.getResources().

                        getDrawable(R.mipmap.tianmao);

                rightDrawable.setBounds(0, 0,

                        SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 13));
                msp.setSpan(new ImageSpan(rightDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(msp);

            } else {
                SpannableString msp = new SpannableString("  " + bean.title);
                Drawable rightDrawable = context.getResources().
                        getDrawable(R.mipmap.taobao);
                rightDrawable.setBounds(0, 0,
                        SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 13));
                msp.setSpan(new ImageSpan(rightDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(msp);
            }

        }

    }

    public class DetailVh extends RecyclerView.ViewHolder {
        ImageView detail_pic;


        public DetailVh(View itemView) {
            super(itemView);
            detail_pic = itemView.findViewById(R.id.detail_pic);

        }

        void setData(ShopList.Img bean) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) detail_pic.getLayoutParams();
            //获取当前控件的布局对象
            params.width = SystemUtils.getScreenWidth(context);
            params.height = params.width * bean.height / bean.width;//设置当前控件布局的高度
            detail_pic.setLayoutParams(params);


            if (!TextUtils.isEmpty(bean.img)) {
                if (!bean.img.contains("http")) {
                    bean.img = "http:" + bean.img;
                }

                RequestOptions userAvatarOptions = new RequestOptions().override(params.width * 2 / 3, params.height * 2 / 3)//.signature(new ObjectKey(System.currentTimeMillis()))
                        .signature(new ObjectKey(bean.img + name)).error(R.mipmap.zhanwei).centerCrop()
                        .encodeQuality(90);

                Glide.with(context).load(bean.img).apply(userAvatarOptions).into(detail_pic);
            }

        }

    }


    public void getZhuanLianData() {
        // if (!form)
        // dialog.show();
        dialog.show();
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.3.1");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("goodsId", shopData.goodsId);


        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                dialog.dismiss();
                // if (!form) dialog.dismiss();
                initZhuanLianData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                dialog.dismiss();
                // NToast.shortToast(context,error);
                if (CommonUtil.isPkgInstalled(context, "com.taobao.taobao")) {

                    CommonUtil.gotoGoodsDetail((Activity) context, shopData.itemLink);
                    CommonUtil.gotoCoupon((Activity) context, shopData.couponLink);
                } else {
                    //没安装淘宝客户端
                    CommonUtil.openBrowser(context, "https://" + "market.m.taobao.com/apps/aliyx/coupon/detail.html?sellerId=2200723171488&activityId=8a1a9c28cc9b40e58aa8a9d8b5562dab");
                }
            }
        }, HttpUtil.zhuanlian_shop_url, paraMap);
    }

    private void initZhuanLianData(String result) {
        if (TextUtils.isEmpty(result)) return;
        PrivilegeLink homeFenlei = new Gson().fromJson(result, PrivilegeLink.class);
        if (homeFenlei == null) return;
        if (homeFenlei.code != 0) {
            if (CommonUtil.isPkgInstalled(context, "com.taobao.taobao")) {

                CommonUtil.gotoGoodsDetail((Activity) context, shopData.itemLink);
                CommonUtil.gotoCoupon((Activity) context, shopData.couponLink);
            } else {
                //没安装淘宝客户端
                CommonUtil.openBrowser(context, "https://" + "market.m.taobao.com/apps/aliyx/coupon/detail.html?sellerId=2200723171488&activityId=8a1a9c28cc9b40e58aa8a9d8b5562dab");
            }
            //NToast.shortToast(context, homeFenlei.msg);
            return;
        }
        if (CommonUtil.isPkgInstalled(context, "com.taobao.taobao")) {
            CommonUtil.gotoCoupon((Activity) context, homeFenlei.data.couponClickUrl);
        } else {
            //没安装淘宝客户端
            CommonUtil.openBrowser(context, "https://" + "market.m.taobao.com/apps/aliyx/coupon/detail.html?sellerId=2200723171488&activityId=8a1a9c28cc9b40e58aa8a9d8b5562dab");
        }

    }
}

