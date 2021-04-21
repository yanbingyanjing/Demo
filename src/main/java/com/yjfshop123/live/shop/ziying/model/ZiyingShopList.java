package com.yjfshop123.live.shop.ziying.model;

import com.yjfshop123.live.shop.model.ShopList;

import java.util.List;

public class ZiyingShopList {
    public String code;//success  error
    public List<Data> list;
    public String msg;

    public class Data {
//       "goods_id":32,
//               "type":1,
//               "price":"100.00",
//               "image":"https:\/\/v5.91ycc.com\/public\/uploads\/cache\/images\/shop7\/06\/01-200x200.jpg",
//               "name":"建盏茶杯油滴杯茶具品茗杯主人杯",
//               "sale_count":37,
//               "quantity":1548,
//               "pay_points":0,
//               "sale_price":"0.00",
//               "pt_price":"0.00",
//               "kj_low_price":"0.00",
//               "kj_top_price":"0.00",
//               "pt_tuan_num":2,
//               "sale_end_time":null,
//               "sale_start_time":null,
//               "local_goods":1

        public String   kind;//类型 huafei表示是话费  shiwu表示正常商品
        public int goods_id;
        public int type;
        public String price;
        public String image;
        public String name;
        public String sale_count;
        public int quantity;//存量
        public int pay_points;
        public String sale_price;
        public String pt_price;
        public String kj_low_price;
        public String kj_top_price;
        public int pt_tuan_num;
        public String sale_end_time;
        public String sale_start_time;
        public int local_goods;


    }


}
