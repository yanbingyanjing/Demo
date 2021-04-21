package com.yjfshop123.live.model;

public class CityPartnerConfigResponse {
    /**
     *  "gold_scaling": "0.5%",   //金蛋加成
     *       "silver_scaling": "0.5%"//银蛋加成
     *          *       "apply_fee": "111"//申请所需费用
     */
    public String partner_apply_rule;
    public String level_limit;
    public String level_limit_title;
    public String level_image;
    public String partner_apply_fee;

    public static String testData = "{ \"gold_scaling\": \"0.4%\",\"silver_scaling\": \"0.4%\",\"apply_fee\":\"300000\"}";

}
