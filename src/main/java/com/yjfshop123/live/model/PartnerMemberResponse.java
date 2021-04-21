package com.yjfshop123.live.model;

import java.util.List;

public class PartnerMemberResponse {
    public int member_count;
    public int total_activity_num;
    public List<PartnerMember> list;
    public class PartnerMember {
        public String name;
        public String user_id;
        public String user_name;

        public String user_level;
        public String user_activity_num;

        public int is_follow;
        public String phone;
        public String user_team_activity_num;
        public String max_medaling;
    }
    public static String testData = "{\n" +
            "      \"member_count\":\"100\","+
            "      \"total_activity_num\":\"1000\","+
            "      \"list\": [\n" +
            "        {\n" +
            "        \"phone\": \"1854544124\",//选品名称\n" +
            "        \"name\": \"用户1\",//每日收益\n" +
            "        \"max_medaling\":\"一级\",//兑换所需金蛋数量\n" +
            "        \"register_date\":\"2020-08-10\"//总释放天数\n" +

            "        },\n" +
            "        {\n" +
            "        \"phone\": \"1854544124\",//选品名称\n" +
            "        \"name\": \"用户2\",//每日收益\n" +
            "        \"max_medaling\":\"二级\",//兑换所需金蛋数量\n" +
            "        \"register_date\":\"2020-08-10\"//总释放天数\n" +

            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"phone\": \"1854544124\",//选品名称\n" +
            "        \"name\": \"用户3\",//每日收益\n" +
            "        \"max_medaling\":\"三级\",//兑换所需金蛋数量\n" +
            "        \"register_date\":\"2020-08-10\"//总释放天数\n" +
            "        }\n" +
            "      ]\n" +
            "}";

    /**
     *     "member_count":100,//用户总数
     *     "total_activity_num":1000,//总活跃度
     *       "list": [
     *         {
     *         "name": "xxx",//用户名字
     *         "phone": "xxxxxxxx",//用户手机号
     *         "max_medaling":"三级"/最高选品等级
     *         "register_date": "2020-08-10 18:06:06", // 注册时间
     *         }
     *       ]
     */
}
