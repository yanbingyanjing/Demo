package com.yjfshop123.live.model;

import java.util.List;

public class AdditionResponse {
    public List<AdditionData> list;

    public class AdditionData {
        /**
         * "id":2//数据唯一id
         * "addition_origin": "直推购买",//来源
         * "addition_user": "aaa",//用户

         * "addition_user_id": "aaa",//用户id
         * "start_time": "2020-09-09",//开始时间

         * "end_time": "2020-09-09",//结束时间
         * "addition_amount": "10",//金额
         * "addition_released":10,//已进行天数
         * "addition_day_income":44,//日收益
         *  addition_left;剩余待释放加成
         *     *  addition_days;总释放天数
         */
        public int id;
        public String addition_origin;
        public String addition_user;
        public String addition_user_id;
        public String addition_left;
        public String   addition_days;
        public String start_time;
        public String addition_amount;
        public String addition_released;
        public String addition_day_income;
        public String burn;//烧伤值
        public String  end_time;
    }
}
