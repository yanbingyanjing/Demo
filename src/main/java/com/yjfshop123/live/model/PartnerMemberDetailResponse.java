package com.yjfshop123.live.model;

public class PartnerMemberDetailResponse {
    /**
     *    "name": "xxx",//名字
     *       "phone": "xxxx", //手机号
     *     "promotion_num":100,//推广数量
     *       "team_total_num":100,//团队总人数
     *       "activity_num": "2.5",//活跃度
     *       "gold_egg": "0.5", //金蛋数量
     *       "silver_egg": "1.5",//银单数量
     *    "bad_egg": "1.5",//臭单数量
     *      "register_date": "2020-08-30 20:20:20",//注册时间
     *    "recent_login": "2020-08-30 20:20:20",//最近登陆时间
     *    "medals":[
     *                   {
     *                     "medal_des": "一级选品",//兑换的选品描述
     *                     "num": "1.5",//兑换的个数数量
     *                    },
     *                   {
     *                     "medal_des": "一级选品",//兑换的选品描述
     *                     "num": "1.5",//兑换的个数数量
     *                    }
     *                   ],
     */
    public String name;
    public String phone;
    public String promotion_num;
    public String team_total_num;
    public String activity_num;
    public String gold_egg;
    public String silver_egg;
    public String bad_egg;
    public String register_date;
    public String recent_login;
    public PartnerMemberDetail[] medals;
    public class PartnerMemberDetail{
        public String medal_des;
        public String num;
    }
}
