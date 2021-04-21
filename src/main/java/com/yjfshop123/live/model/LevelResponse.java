package com.yjfshop123.live.model;

public class LevelResponse {
    public LevelData[] list;
public UserData user;
    public class LevelData {
        public int id;
        public int vip_level;
        public String vip_level_name;
        public String vip_level_des;
        public String vip_level_icon;
        public String vip_level_fee;
        public String vip_level_bonus;
        public String vip_level_lower;
        public String   vip_level_title;
        public String vip_level_update_need;
        public boolean vip_level_status;
    }
    public class UserData {
        public String name;
        public String avatar;
        public String  level_title;
        public int level;
        public String level_name;
        public int join_days;
        public String update_desc;

    }

    public static String testData = "{\n" +
            "      \"list\": [\n" +
            "        {\n" +
            "        \"id\": \"1\",//选品名称\n" +
            "        \"vip_level\": \"LV1\",//每日收益\n" +
            "        \"vip_level_des\":\"直推1个\",//兑换所需金蛋数量\n" +
            "        \"vip_level_icon\":\"123\",//总释放天数\n" +
            "    \"vip_level_bonus\":\"28%\",//总释放天数\n" +
            "    \"vip_level_lower\":\"10%\",//总释放天数\n" +
            "    \"vip_level_update_need\":\"11\",//总释放天数\n" +

            "    \"vip_level_status\":true,//总释放天数\n" +
            "        \"vip_level_fee\":\"18%\"//已释放天数\n" +
            "        },\n" +
            "        {\n" +
            "        \"id\": \"2\",//选品名称\n" +
            "        \"vip_level\": \"LV2\",//每日收益\n" +
            "        \"vip_level_des\":\"直推2个\",//兑换所需金蛋数量\n" +
            "        \"vip_level_icon\":\"123\",//总释放天数\n" +
            "    \"vip_level_bonus\":\"28%\",//总释放天数\n" +
            "    \"vip_level_update_need\":\"11\",//总释放天数\n" +

            "    \"vip_level_lower\":\"10%\",//总释放天数\n" +
            "    \"vip_level_status\":true,//总释放天数\n" +
            "        \"vip_level_fee\":\"18%\"//已释放天数\n" +
            "        },\n" +

            "        {\n" +
            "        \"id\": \"3\",//选品名称\n" +
            "        \"vip_level\": \"LV3\",//每日收益\n" +
            "        \"vip_level_des\":\"直推3个\",//兑换所需金蛋数量\n" +
            "        \"vip_level_icon\":\"123\",//总释放天数\n" +
            "    \"vip_level_bonus\":\"28%\",//总释放天数\n" +
            "    \"vip_level_lower\":\"10%\",//总释放天数\n" +

            "    \"vip_level_update_need\":\"11\",//总释放天数\n" +
            "    \"vip_level_status\":false,//总释放天数\n" +
            "        \"vip_level_fee\":\"18%\"//已释放天数\n" +
            "        }\n" +
            "      ]\n" +
            "}";

}
