package com.yjfshop123.live.shop.ziying.model;

import java.util.List;

public class ZiyingShopDetail {
    public String code;//success  error
    public Data goods;
    public String msg;

    public SpecData[] spec;
    public String description;
    public String[] images_url;
    public SkumapItem[] skumap;
    public AttributeItem[] attribute;
    public galleryItem[] gallery;
    public comment comment;
    public BonusCard[] bonus_card;
    public class BonusCard{
        public int id;
        public String shop_name;
        public String title;
        public String deduct_num;
        public String start_time;
        public String end_time;
        public String full_num;
    }
    public  class comment{
        public int count;
    }
    public class galleryItem{
        public String image;
    }
    public class AttributeItem{
        public String name;
        public String value;
        public String sort;
    }
    public class Data {
        public String id;
        public int shop_id;
        public String kind;
        public String shop_name;
        public String name;
        public int type;
        public int quantity;//存量
        public String sale_count;
        public int shipping;
        public int free_shipping;
        public int local_goods;
        public String weight;
        public String price;
        public int pay_points;
        public String comment_num;

        public String sale_price;
        public String sale_end_time;
        public String sale_start_time;
        public String sale_buy_limit;
        public String pt_price;
        public int pt_tuan_num;

        public String kj_low_price;
        public String kj_top_price;

        public String minimum;
        public String image;
    }
    public class SpecData{
        public String name;
        public int spec_id;
        public SpecItemData[] value;

        public class SpecItemData{
            public String name;
            public String image;
            public boolean active;
            public int id;
        }
    }
    public class SkumapItem{
        public String sell_price;
        public String market_price;
        public int quantity;
        public String specs_key;
        public String pro_no;
        public int id;
    }
}
