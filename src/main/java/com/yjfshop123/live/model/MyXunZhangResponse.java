package com.yjfshop123.live.model;

import java.util.List;

public class MyXunZhangResponse {
    public List<MyXunZhangData> list;

    public class MyXunZhangData {
        /**
         * "id":1//数据唯一id
         *       * "medal_level": 1,//选品等级
         *         "medal_des": "体验选品",//选品名称
         *         "medal_icon": "pc/xxx",//选品图标地址
         *         "medal_daily_income": 11,//每日收益
         *         "medal_income_percent": "25%",//收益率
         *         "medal_exchange":10,//兑换所需金蛋数量
         *         "medal_total_release_day":44,//总释放天数
         *         "medal_released_day":3,//已释放天数
         *         "effective_time":"2020-08-30 20:20:20",//生效时间
         */
        public int id;
        public int medal_level;
        public String medal_des;
        public String medal_icon;
        public String limit_count;
        public String medal_daily_income;
        public String medal_income_percent;
        public String medal_exchange;
        public String medal_total_release_day;
        public String medal_released_day;
        public String medal_released_status;
        public String effective_time;
        public String create_date;//购买日期
        public String xuanpin_name;
        public String xuanpin_id;
        public String xuanpin_count;
        public String xuanpin_pic;
        public int is_buy;

    }

}