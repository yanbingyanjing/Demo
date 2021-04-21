package com.yjfshop123.live.shop.ziying.model;

public class OrderDetail {
    public String code;//success  error

    public String msg;
    public OrderDetailData data;

    public class OrderDetailData {
        public String zt;
        public OrderInfo orderInfo;
        public HistoryItem[] history;
        public Logistics logistics;
        public GoodsItem[] goods;
    }

    public class OrderInfo {
        public String status;
        public String statusStr;
        public String type;
        public String total;
        public String yun_fee;
        public String local_yun_fee;
        public String pay_fee;
        public String bonus_card_deduct;

    }

    public class Logistics {
        public String transport_c_name;
        public String transport_num;
        public String shipping_name;
        public String shipping_tel;
        public String address;
    }

    public class HistoryItem {
        public String desc;
        public String title;

    }

    public class GoodsItem {
        public String id;
        public String image;
        public String name;
        public String price;
        public String kind;
        public String recharge_mobile;
        public String quantity;
        public String spec;
        public String status;
    }

}
