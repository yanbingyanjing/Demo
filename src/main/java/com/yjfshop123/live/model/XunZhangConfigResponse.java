package com.yjfshop123.live.model;

public class XunZhangConfigResponse {
    public XunZhangConfigData[] list;
    public String gold_egg;
    public class XunZhangConfigData {
        /**
         "id":1//数据唯一id
         "medal_level":0,//选品等级 0 体验选品 1一级选品
         "medal_des": "体验选品",//选品名称
         "medal_icon": "pc/xxx",//选品图标地址
         "medal_daily_income": 11,//每日收益
         "medal_income_percent": "25%",//收益率
         "medal_exchange":10,//兑换所需金蛋数量
         "medal_total_release_day":44,//总释放天数
         "medal_activity_num":3,//活跃度
         "effective_hours":23,//多少小时以后生效
         "max_buy_count":8,//最多购买数量
         "effective_time_des":"",//生效时间描述
         "buy_tips_des":"",//购买弹窗描述
         */
        public int id;
        public String medal_des;
        public int medal_level;
        public String medal_icon;
        public String medal_daily_income;
        public String medal_income_percent;
        public String medal_exchange;
        public String medal_total_release_day;
        public String medal_activity_num;
        public String effective_hours;
        public String max_buy_count;
        public String effective_time_des;
        public String buy_tips_des;
    }
}
