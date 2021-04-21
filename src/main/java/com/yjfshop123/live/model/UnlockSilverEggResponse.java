package com.yjfshop123.live.model;

import java.util.List;

public class UnlockSilverEggResponse {
public List<Data> list;
    public class Data {
        public String order_id;//订单编号
        public String locked_count;//锁仓数量
        public String has_unlock_times;//已解锁次数 目前是按照1/3的比例去解锁一共解锁3次
        public String first_lock_price;//首次锁仓时的金蛋价格
        public String lock_time;//锁仓时间
        public String first_unlock_price;//首次解锁价格
        public String second_unlock_price;//第二次解锁价格
        public String third_unlock_price;//第三次解锁价格

        public String first_unlock_count;//首次解锁数量
        public String second_unlock_count;//第二次解锁数量
        public String third_unlock_count;//第三次解锁数量

    }


    public static   String testData = "{\n" +
            "    \n" +
            "    \"list\": [\n" +
            "      {\n" +
            "        \"order_id\": \"02002\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }  , \n" +
            "{\n" +
            "        \"order_id\": \"02004\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }  , \n" +
            "{\n" +
            "        \"order_id\": \"02005\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }  , \n" +
            "{\n" +
            "        \"order_id\": \"02006\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }  , \n" +
            "{\n" +
            "        \"order_id\": \"02007\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }, \n" +
            "{\n" +
            "        \"order_id\": \"020011\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }, \n" +
            "{\n" +
            "        \"order_id\": \"020012\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }, \n" +
            "{\n" +
            "        \"order_id\": \"020022\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        }, \n" +
            "{\n" +
            "        \"order_id\": \"0200122\",\n" +
            "        \"locked_count\": \"111\",\n" +
            "        \"has_unlock_times\": \"2\",\n" +
            "        \"first_lock_price\": \"5.3\",\n" +
            "        \"lock_time\": \"2020-08-10-18:06:06\", \n" +
            "        \"first_unlock_price\": \"5.4\",\n" +
            "        \"second_unlock_price\": \"5.5\", \n" +
            "        \"third_unlock_price\": \"5.6\", \n" +
            "        \"first_unlock_count\": \"300\", \n" +
            "        \"second_unlock_count\": \"300\", \n" +
            "        \"third_unlock_count\": \"301\" \n" +
            "        } \n" +

            "        ]\n" +
            "    \n" +
            "}";

}
