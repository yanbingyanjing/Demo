package com.yjfshop123.live.model;

import java.util.List;

public class XuanPInResopnse {
    public String gold_egg;
    public String egg_price;
    public List<ItemData> list;

    public class ItemData {
        public String id;
        public String title;
        public String goods_id;
        public String goods_url;
        public String name;
        public String buy_times;
        public String price;
        public String egg_price;
        public String price_unit;
        public String pic;
        public String medal_total_release_day;
        public List<MedalIten> medals;
    }

    public class MedalIten {
        public String medal_id;
        public String medal_exchange;
        public String medal_income;
        public int medal_level;
    }
}
