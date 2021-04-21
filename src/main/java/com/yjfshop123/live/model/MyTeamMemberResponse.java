package com.yjfshop123.live.model;

import java.util.List;

public class MyTeamMemberResponse {
    public List<MyTeamMemberData> list;

    public class MyTeamMemberData {
        /**
         * "mobile":“xxxxxxx”//直推下级用户的手机号
         * "user_name": "xxx",//用户名字
         * "userid": "xxx",//用户id
         * "user_vip_level": "lv1",//等级
         * "user_team_activity_num": 11,//下级用户团队活跃度
         * "user_promotion_num":11,//下级用户推广人数
         * "max_medaling":100,//最高选品等级
         * "user_activity_num":100,//个人活跃度
         * "is_follow":"1"//当前登录的用户是否已关注推荐人  1是已关注   0是未关注
         */
        public String mobile;
        public String user_name;
        public String user_id;
        public String user_vip_level;
        public String user_team_activity_num;
        public String user_activity_num;
        public String user_promotion_num;
        public String max_medaling;
        public String avatar;
        public String level_title;
        public int is_follow;
        public int is_hero;//1英雄  0不是英雄
        public String hero_pic;
    }

    public static String testData = "{\n" +
            "      \"list\": [\n" +
            "        {\n" +
            "        \"mobile\": \"1854544124\",//选品名称\n" +
            "        \"user_name\": \"用户1\",//每日收益\n" +
            "        \"user_vip_level\":10,//兑换所需金蛋数量\n" +
            "        \"user_team_activity_num\":44,//总释放天数\n" +
            "        \"user_promotion_num\":3//已释放天数\n" +
            "        },\n" +
            "        {\n" +
            "        \"mobile\": \"1854544124\",//选品名称\n" +
            "        \"user_name\": \"用户2\",//每日收益\n" +
            "        \"user_vip_level\":10,//兑换所需金蛋数量\n" +
            "        \"user_team_activity_num\":44,//总释放天数\n" +
            "        \"user_promotion_num\":3//已释放天数\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "        \"mobile\": \"1854544124\",//选品名称\n" +
            "        \"user_name\": \"用户3\",//每日收益\n" +
            "        \"user_vip_level\":10,//兑换所需金蛋数量\n" +
            "        \"user_team_activity_num\":44,//总释放天数\n" +
            "        \"user_promotion_num\":3//已释放天数\n" +
            "        }\n" +
            "      ]\n" +
            "}";


}
