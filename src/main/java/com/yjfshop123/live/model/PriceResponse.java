package com.yjfshop123.live.model;

import java.util.List;

public class PriceResponse {
    public List<AdditionData> list;

    public class AdditionData {
        /**
         "id": 1,
         "prize_name": "金蛋",//奖品名称
         "prize_num": "100.0000",//奖品对应价值
         "prize_icon": "",//奖品图片
         "prize_count": 1//奖品个数
         */
        public int id;
        public String prize_name;
        public String prize_num;
        public String prize_icon;
        public String prize_count;

    }
}
