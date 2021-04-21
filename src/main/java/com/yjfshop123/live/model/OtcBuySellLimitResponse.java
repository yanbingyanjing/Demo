package com.yjfshop123.live.model;

public class OtcBuySellLimitResponse {

        public  LimitData alipay;
        public  LimitData card;
        public  LimitData wechat;
    public  LimitData usdt;
    public class LimitData {
        public String type;//alipay     card      wechat
        public String buyMinNumber;//按照人民币限制
        public String buyMaxNumber;
        public String sellMinNumber;
        public String sellMaxNumber;
    }
    public static String BANK_TYPE="card";
    public static String WECHAT_TYPE="wechat";
    public static String ALIPAY_TYPE="alipay";
    public static String USDT_TYPE="usdt";
}
