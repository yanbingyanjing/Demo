package com.yjfshop123.live.shop.model;

import java.util.List;

public class PintuanResponse {
    public List<PintuanData> list;
    public static class PintuanData {
        public String goods_image;
        public String pintuan_id;
        public String goods_id;
        public String desc;
        public String goods_name;
        public String nickname;
        public int invite_people;
        public String goods_price;
        public String pintuan_users;
        public String status_desc;//描述状态
        public int status;//1 能参与 0不可以参与
        public int quantity;//库存
        public int hasBuy;//已经买了多少
    }
}
