package com.yjfshop123.live.model;

public class PartnerDetailResponse {
    public String city;
    public String user_count;
    public String gold_income;
    public String silver_income;
    public String today_gold_income;
    public String today_silver_income;
    public String month_gold_income;
    public String month_silver_income;
    public MonthPartnerData[] month_gold_income_data;
    public MonthPartnerData[] month_silver_income_data;
    public String city_activity;//城市活跃度
    public String yesterday_city_activity;// 昨日城市活跃度
    public String before_yesterday_city_activity;//前天城市活跃度
    public String yesterday_up;
    public String before_yesterday_up;
    public String max_activity;
    public class MonthPartnerData {

        public String create_date;
        public String income;
    }

    /*
           "      {\n" +
                   "        \"time\": \"2020-02-01\",\n" +
                   "        \"income\": \"22\"\n" +
                   "        }  , \n" +
                   "{\n" +
                   "        \"time\": \"2020-02-02\",\n" +
                   "        \"income\": \"24\"\n" +
                   "        }  , \n" +
                   "{\n" +
                   "        \"time\": \"2020-02-03\",\n" +
                   "        \"income\": \"29\"\n" +
                   "        }  , \n" +
                   "{\n" +
                   "        \"time\": \"2020-02-04\",\n" +
                   "        \"income\": \"23\"\n" +
                   "        }  , \n" +
                   "{\n" +
                   "        \"time\": \"2020-02-05\",\n" +
                   "        \"income\": \"30\"\n" +
                   "        }, \n" +*/
    public static String testData = "{\"city\":\"东城区\",\"user_count\":0,\"gold_income\":\"0.1250\",\"silver_income\":\"0.0250\",\"today_gold_income\":\"0.0250\",\"today_silver_income\":\"0.0050\",\"month_gold_income\":0.125,\"month_silver_income\":0.025,\"month_gold_income_data\":[{\"create_date\":20201104,\"income\":\"0.1250\"}],\"month_silver_income_data\":[{\"create_date\":20201104,\"income\":\"0.0250\"}]}";
    /**
     *  "city":"广东省深圳市",   //城市
     *         "user_count": 111,   //城市用户数量
     *         "gold_income": "22",   //金蛋总收益
     *         "silver_income": "33",   //银蛋总收益
     *         "today_gold_income": "22",   //今日金蛋收益
     *         "today_silver_income": "33", //今日银蛋收益
     *         "month_gold_income": "22",   //最近30日金蛋收益
     *         "month_silver_income": "33",   //最近30日银蛋收益
     *         "month_gold_income_data":[
     *                {
     *                   "time":"2020-02-01",//日期
     *                  "income":"22"//当日金蛋收益
     *                 }，
     *                 {
     *                   "time":"2020-02-02",//日期
     *                    "income":"33"//当日金蛋收益
     *                  }
     *                  ],
     *            "month_silver_income_data":[
     *                 {
     *                   "time":"2020-02-01",//日期
     *                   "income":"22"//当日银蛋收益
     *                  }，
     *                  {
     *                   "time":"2020-02-02",//日期
     *                   "income":"33"//当日银蛋收益
     *                  }
     *                 ]
     *     }
     */
}
