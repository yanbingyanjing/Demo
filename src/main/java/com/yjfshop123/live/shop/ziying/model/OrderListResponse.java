package com.yjfshop123.live.shop.ziying.model;

import java.util.List;

public class OrderListResponse {
    public String code;//success  error

    public String msg;

    public List<OrderItem> orderList;
    public class OrderItem{
        public String id;

        public String order_common_no_id;
        public String buy_type;
        public String goods_type;
        public String dateAdd;
        /**
         * 全部 all
         * 待付款 3
         * 待发货 1
         * 待收货 4
         * 待评价 6
         * 退换货 after_sales
         */
        public String status;
        public String statusStr;
        public String orderNumber;
        public String remark;
        public String amountReal;
        public String total_quantity;
        public String local_transport_type;
        public ImageItem[] list;
    }
    public class ImageItem{
        public String image;
    }
}
