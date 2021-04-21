package com.yjfshop123.live.shop.ziying.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.xchat.load.engine.DiskCacheStrategy;
import com.bumptech.xchat.load.resource.drawable.GlideDrawable;
import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.shop.adapter.ShopDetailAdapter;
import com.yjfshop123.live.shop.model.PrivilegeLink;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.CommonUtil;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailAddressHolder;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailGoodHolder;
import com.yjfshop123.live.shop.ziying.holder.OrderDetailHistoryHolder;
import com.yjfshop123.live.shop.ziying.holder.SubmitBuyOrderShopTitleHolder;
import com.yjfshop123.live.shop.ziying.model.OrderDetail;
import com.yjfshop123.live.shop.ziying.model.ShopDetailData;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopDetail;
import com.yjfshop123.live.shop.ziying.view.BuyView;
import com.yjfshop123.live.ui.activity.XWebviewActivity;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.xuanpin.adapter.XunZhangAdapter;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static com.yjfshop123.live.shop.ziying.model.ShopDetailData.detailPic_type;
import static com.yjfshop123.live.shop.ziying.model.ShopDetailData.guige_type;
import static com.yjfshop123.live.shop.ziying.model.ShopDetailData.head_type;
import static com.yjfshop123.live.shop.ziying.model.ShopDetailData.title_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.address_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.history_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_item_type;
import static com.yjfshop123.live.shop.ziying.model.SubmitData.shop_type;

public class NewShopDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] detailPic;
    private List<ShopList.ShopData> mList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ZiyingShopDetail shopData;

    public NewShopDetailAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == head_type) {
            return new Vh(layoutInflater.inflate(R.layout.item_ziying_shop_detail_head, parent, false));
        } else if (viewType == detailPic_type) {
            return new DetailVh(layoutInflater.inflate(R.layout.item_image, parent, false));
        }
        else if (viewType == title_type) {
            return new TitleVh(layoutInflater.inflate(R.layout.item_ziying_shop_detail_title, parent, false));
        }
        else if (viewType == guige_type) {
            return new CanshuVh(layoutInflater.inflate(R.layout.item_ziying_shop_canshu, parent, false));
        }
        return new Vh(layoutInflater.inflate(R.layout.item_shop_detail_head, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataList.get(position).type == head_type && dataList.get(position).itemdata != null && dataList.get(position).itemdata instanceof ZiyingShopDetail) {
            ((Vh) holder).setData((ZiyingShopDetail) dataList.get(position).itemdata);
        }
        if (dataList.get(position).type == title_type && dataList.get(position).itemdata != null && dataList.get(position).itemdata instanceof String) {
            ((TitleVh) holder).setData((String) dataList.get(position).itemdata);
        }
        if (dataList.get(position).type == detailPic_type && dataList.get(position).itemdata != null && dataList.get(position).itemdata instanceof String) {
            ((DetailVh) holder).setData((String) dataList.get(position).itemdata);
        }
        if (dataList.get(position).type == guige_type && dataList.get(position).itemdata != null && dataList.get(position).itemdata instanceof ZiyingShopDetail.AttributeItem) {
            ((CanshuVh) holder).setData((ZiyingShopDetail.AttributeItem) dataList.get(position).itemdata);
        }

    }

    boolean isScrolling = false;
    ZLoadingDialog dialog;

    public void setZLoadingDialog(ZLoadingDialog dialog) {
        this.dialog = dialog;
    }


    @Override
    public int getItemViewType(int position) {

        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    List<ShopDetailData> dataList;

    public void setData(List<ShopDetailData> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    View.OnClickListener onClickListener;

    public void onClict(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        TextView title;
        TextView actualPrice, monthSales;
        RecyclerView list;
        LinearLayout guige;
        private LinearLayoutManager mLinearLayoutManager;
        GuigeAdapter priceAdapter;
        RelativeLayout guige_ll;
        View guige_zhezhao;

        public Vh(View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.list);
            guige_zhezhao = itemView.findViewById(R.id.guige_zhezhao);
            actualPrice = itemView.findViewById(R.id.actualPrice);
            guige = itemView.findViewById(R.id.guige);
            monthSales = itemView.findViewById(R.id.monthSales);
            guige_ll = itemView.findViewById(R.id.guige_ll);
            title = itemView.findViewById(R.id.title);

            mLinearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            list.setLayoutManager(mLinearLayoutManager);
            priceAdapter = new GuigeAdapter(context);
            list.setAdapter(priceAdapter);

        }

        void setData(final ZiyingShopDetail bean) {

            if (bean == null) return;
            if (bean.goods == null) return;
            guige_zhezhao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) onClickListener.onClick(v);
                }
            });
            if (bean.spec == null || bean.spec.length == 0) {
                guige_ll.setVisibility(View.GONE);
            } else {
                priceAdapter.setHeadData(bean.spec);
                guige_ll.setVisibility(View.VISIBLE);
            }

            actualPrice.setText(NumUtil.clearZero(bean.goods.price));

            monthSales.setText("已售" + NumUtil.formatNum(bean.goods.sale_count, false));
            title.setText(bean.goods.name);
        }

    }

    public class DetailVh extends RecyclerView.ViewHolder {
        ImageView detail_pic;


        public DetailVh(View itemView) {
            super(itemView);
            detail_pic = itemView.findViewById(R.id.detail_pic);

        }

        void setData(String bean) {

//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) detail_pic.getLayoutParams();
//            //获取当前控件的布局对象
//            params.width = SystemUtils.getScreenWidth(context);
//            params.height = params.width * bean.height / bean.width;//设置当前控件布局的高度
//            detail_pic.setLayoutParams(params);


            RequestOptions userAvatarOptions = new RequestOptions()//.signature(new ObjectKey(System.currentTimeMillis()))
                    .signature(new ObjectKey(bean + name)).error(R.mipmap.zhanwei).fitCenter()
                    .encodeQuality(20);

            Glide.with(context).load(bean).apply(userAvatarOptions).into(detail_pic);
//            Glide.with(context)
//                    .load(bean)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            if (imageView == null) {
//                                return false;
//                            }
//                            if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
//                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                            }
//                            ViewGroup.LayoutParams params = imageView.getLayoutParams();
//                            int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
//                            //float scale = (float) vw / (float) resource.getIntrinsicWidth();
//                            int vh = (int) ((float)vw/(float) 1.78);
//                            params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
//                            imageView.setLayoutParams(params);
//                            return false;
//                        }
//                    })
//                    .placeholder(errorImageId)
//                    .error(R.mipmap.zhanwei)
//                    .into(detail_pic);

        }

    }

    public class TitleVh extends RecyclerView.ViewHolder {
        TextView title;


        public TitleVh(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);

        }

        void setData(String bean) {
            title.setText(bean);

        }

    }
    public class CanshuVh extends RecyclerView.ViewHolder {
        TextView name,value;


        public CanshuVh(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);

        }

        void setData(ZiyingShopDetail.AttributeItem bean) {
            name.setText(bean.name);
            value.setText(bean.value);
        }

    }



}

