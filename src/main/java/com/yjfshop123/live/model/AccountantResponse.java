package com.yjfshop123.live.model;

//收支分析返回实体类
public class AccountantResponse {
    public String gold_total_income;
    public String silver_total_income;
    public String bad_total_income;
    public String gold_total_out;
    public String silver_total_out;
    public GoldEgg gold_egg;
    public SilverEgg silver_egg;
   // public BadEgg bad_egg;
    public class GoldEgg{
        public GoldIncome day_income;
        public GoldIncome week_income;
        public GoldIncome month_income;
        public GoldIOut day_out;
        public GoldIOut week_out;
        public GoldIOut month_out;
    }
    public class SilverEgg{
        public SilverIncome day_income;
        public SilverIncome week_income;
        public SilverIncome month_income;
        public SilverIOut day_out;
        public SilverIOut week_out;
        public SilverIOut month_out;
    }
    public class BadEgg{
//        public BadIncome day_income;
//        public BadIncome week_income;
//        public BadIncome month_income;
    }
    public class GoldIncome{
        /*
            "addition": "0.5", //加成
                "target": "1.5",//目标
                "reward": "1.5",//获赏
                "boost": "2.5",//助推
                "unlock": "2.5",//解锁
                             "fee_bonus": "2.5", //手续费分红
                "bonus": "2.5",//分红
                "income_coin": "2.5", //收币
         */
        public String addition;
        public String target;
        public String reward;
        public String boost;
        public String unlock;
        public String bonus;
        public String income_coin;
        public String fee_bonus;
        public String other_income;
    }
    public class GoldIOut{
        /*
            "exchange_medals": "0.5", //兑换选品
                "fee": "1.5",//手续费
                "send_reward": "1.5",//打赏
                 "turnout_coin": "2.5",//提币
         */
        public String exchange_medals;
        public String fee;
        public String send_reward;
        public String turnout_coin;
        public String shop_out;
    }

    public class SilverIncome{
        public String addition;
        public String target;
        public String boost;
        public String income_coin;
    }
    public class SilverIOut{
        public String turnout_coin;
        public String fee;
    }
//    public class BadIncome{
//        public String uncomplete_task;
//        public String uncomplete_target;
//    }
}
