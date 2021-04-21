package com.yjfshop123.live.model;

import java.util.List;

public class NewSilverEggResponse {
    public String lockedEgg;//锁仓中的银蛋数量
    public String releasedEgg;//已释放的银蛋数量
    public String gold_price;//当前金蛋价格
    public boolean is_up;//是否上涨
    public String up_percent;//上涨或者下降幅度

    public List<UpEntity> goldEggPrices;//上涨或者下降幅度
    public class UpEntity{
        public String gold_price;//金蛋价格
        public boolean is_up;//是否上涨
        public String up_percent;//上涨或者下降幅度
        public String time;//10.27  日期
    }
    public static String testData="{\n" +
            "         \"lockedEgg\": \"1000\",//锁仓中的银蛋数量\n" +
            "         \"releasedEgg\": \"100\", //已释放的银蛋数量\n" +
            "         \"gold_price\":\"7.56\",//当前金蛋价格\n" +
            "         \"is_up\":true,//是否上涨\n" +
            "         \"up_percent\": \"2.5%\",//上涨或者下降幅度\n" +
            "         \"goldEggPrices\":[\n" +
            "                           {\n" +
            "                             \"gold_price\": \"7.56\",//金蛋价格\n" +
            "                             \"is_up\":true,//是否上涨\n" +
            "                             \"up_percent\": \"2.5%\",//上涨或者下降幅度\n" +
            "                             \"time\": \"10.27\"//日期\n" +
            "                           },\n" +
            "                           {\n" +
            "                             \"gold_price\": \"7.66\",//金蛋价格\n" +
            "                             \"is_up\":false,//是否上涨\n" +
            "                             \"up_percent\": \"2.5%\",//上涨或者下降幅度\n" +
            "                             \"time\": \"10.27\"//日期\n" +
            "                           },\n" +
            "                         {\n" +
            "                            \"gold_price\": \"7.76\",//金蛋价格\n" +
            "                            \"is_up\":true,//是否上涨\n" +
            "                            \"up_percent\": \"2.5%\",//上涨或者下降幅度\n" +
            "                            \"time\": \"10.27\"//日期\n" +
            "                        }\n" +
            "                  ]\n" +
            "    }";

}
