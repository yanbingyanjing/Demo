package com.yjfshop123.live.model;

import java.util.List;

public class OtcOrderResponse {
    /**
     *  "sell_user": "管腾飞",
     *     "price": "6.7563",
     *     "eggs": 10,
     *     "money": "67.56",
     *     "code_url": "http://192.168.2.184:8086/oss/2020-05-25/73e10837-eb52-4e0b-b2a3-c11587e23645.jpg",
     *     "pay_type": "支付宝",
     *     "pay_address": "18546771321",
     *     "create_time": "2020-11-20 15:59:22",
     *     "order": "otc2020112015592241022849333562",
     *     "bank": "",
     *     "bank_branch": ""
     */
    public String fee;
    public String admin_user_id;
    public String user_name;
    public String real_name;
    public String price;
    public String eggs;
    public String money;
    public String code_url;
    public String pay_type;
    public String pay_address;
    public String create_time;
    public String order;
    public int status;//0-已取消 1-未付款 2-已付款 3-已完成 4-申诉中 5-待审核 6-审核不通过
    public String desc;
    public String order_sn;
    public String bank;
    public String bank_branch;
    public String expire_time;
    public String buyer_phone;
    public String seller_phone;
    public List<String> pics;
}
