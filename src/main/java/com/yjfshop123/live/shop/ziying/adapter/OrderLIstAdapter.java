package com.yjfshop123.live.shop.ziying.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.model.AddCartResponse;
import com.yjfshop123.live.shop.ziying.model.OrderListResponse;
import com.yjfshop123.live.shop.ziying.model.ZiyingShopList;
import com.yjfshop123.live.shop.ziying.ui.NewShopDetailActivity;
import com.yjfshop123.live.shop.ziying.ui.OrderDetailActivity;
import com.yjfshop123.live.shop.ziying.ui.SubmitBuyOrderActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.viewholders.SucaiHolder;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class OrderLIstAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<OrderListResponse.OrderItem> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public OrderLIstAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_ziying_orderlist, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<OrderListResponse.OrderItem> list) {
        mList = list;
        notifyDataSetChanged();
    }


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
    String name = dateFormat.format(new Date());

    public class Vh extends RecyclerView.ViewHolder {
        LinearLayoutManager mLinearLayoutManager;
        ImageAdapter uploadAdapter;
        RecyclerView list;
        TextView time, to_pay,wuliu;
        TextView status, order_no, amount, total_value, cancel_order, confir_shouhuo;
        View click_btn;

        public Vh(View itemView) {
            super(itemView);
            wuliu= itemView.findViewById(R.id.chakan_wuliu);
            click_btn = itemView.findViewById(R.id.click_btn);
            confir_shouhuo = itemView.findViewById(R.id.confir_shouhuo);
            cancel_order = itemView.findViewById(R.id.cancel_order);
            to_pay = itemView.findViewById(R.id.to_Pay);
            list = itemView.findViewById(R.id.list);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            order_no = itemView.findViewById(R.id.order_no);
            amount = itemView.findViewById(R.id.amount);
            total_value = itemView.findViewById(R.id.total_value);
            mLinearLayoutManager = new LinearLayoutManager(context);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            list.setLayoutManager(mLinearLayoutManager);
        }

        void setData(final OrderListResponse.OrderItem bean) {
            click_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean != null) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("id", bean.id);
                        ((Activity) context).startActivity(intent);
                    }
                }
            });
            cancel_order.setVisibility(View.GONE);
            to_pay.setVisibility(View.GONE);
            wuliu.setVisibility(View.GONE);
            confir_shouhuo.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(bean.status)) {
                if (bean.status.contains("3")) {
                    //待付款
                    cancel_order.setVisibility(View.VISIBLE);
                    to_pay.setVisibility(View.VISIBLE);
                }
                if (bean.status.contains("1")) {
                    //待发货
                }
                if (bean.status.contains("4")) {
                    //待收货
                    confir_shouhuo.setVisibility(View.VISIBLE);
                }
                if (!bean.status.contains("1")&&!bean.status.contains("3")) {
                    //非待付款 和待发货 状态都显示物流按钮
                    wuliu.setVisibility(View.VISIBLE);
                }

            }
            to_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderDeal != null) orderDeal.toPay(bean, getLayoutPosition());
                }
            });
            cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderDeal != null) orderDeal.cancelOrder(bean, getLayoutPosition());
                }
            });
            confir_shouhuo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderDeal != null) orderDeal.shouhuo(bean, getLayoutPosition());
                }
            });
            wuliu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderDeal != null) orderDeal.wuliu(bean, getLayoutPosition());
                }
            });
            uploadAdapter = new ImageAdapter(context);
            list.setAdapter(uploadAdapter);
            uploadAdapter.setCards(bean.list);
            time.setText(bean.dateAdd);
            status.setText(bean.statusStr);
            order_no.setText(bean.orderNumber);
            amount.setText(context.getString(R.string.gongji_duoshao, bean.total_quantity));
            total_value.setText(context.getString(R.string.heji_titile) + NumUtil.clearZero(bean.amountReal));
        }

    }

    OrderDeal orderDeal;

    public void setOrderDeal(OrderDeal orderDeal) {
        this.orderDeal = orderDeal;
    }

    public interface OrderDeal {
        void toPay(OrderListResponse.OrderItem bean, int index);

        void cancelOrder(OrderListResponse.OrderItem bean, int index);

        void shouhuo(OrderListResponse.OrderItem bean, int index);
        void wuliu(OrderListResponse.OrderItem bean, int index);
    }


    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private OrderListResponse.ImageItem[] mList;
        private LayoutInflater layoutInflater;
        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Vh(layoutInflater.inflate(R.layout.item_order_ziying_image, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Vh) holder).setData(mList[position]);

        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.length : 0;
        }

        public void setCards(OrderListResponse.ImageItem[] list) {
            if (list == null) {
                return;
            }
            mList = list;
            notifyDataSetChanged();
        }

        public class Vh extends RecyclerView.ViewHolder {

            SelectableRoundedImageView imageView;

            public Vh(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.images);

            }

            void setData(final OrderListResponse.ImageItem bean) {
                com.bumptech.xchat.Glide.with(context).load(bean.image).into(imageView);
            }

        }
    }
}
