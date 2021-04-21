package com.yjfshop123.live.shop.ziying.model;

public class OrderGoodsResponse {

    public String code;//success  error

    public String msg;
    public GoodsItem[] goods_list;
    public String shipping;
    public String local_goods;
    public String goods_type;
    public String order_total_price;
    public String local_transport;
    public String local_shop_id;
    public class GoodsItem {
        public String yun_fee;
        public String bonus_card_title;
        public String bonus_card_id;
        public String bonus_card_deduct;
        public String shop_name;
        public String shop_id;
        public String goods_type;
        public String total_money;
        public String all_money;
        public String all_pay_points;
        public Goods goods;



    }
    public class Goods {
        public GoodsItemItem[] goods_list;
        public String quantity;
        public String weight;
        public String total_money;
        public String total_points;


    }

    public class GoodsItemItem {
        public String goods_id;
        public String name;
        public String weight_class_id;
        public String minimum;
        public String shipping;
        public String subtract;
        public String free_shipping;
        public String return_points;
        public String image;
        public String price;
        public String pro_no;
        public String weight;
        public String goods_type;
        public String cart_id;
        public String goods_spec_id;
        public String buy_type;
        public String quantity;
        public String shop_id;
        public String shop_name;
        public String pin_tuan_id;
        public String kj_id;
        public String local_goods;
        public String original_image;
        public String spec_name;

    }
}
