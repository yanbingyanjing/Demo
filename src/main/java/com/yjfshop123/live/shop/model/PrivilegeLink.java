package com.yjfshop123.live.shop.model;

public class PrivilegeLink {

    public long time;
    public int code;
    public String msg;
    public ZhuanLianData data;

    public class ZhuanLianData {
        public String couponClickUrl;
        public String couponEndTime;
        public String couponInfo;
        public String couponStartTime;
        public String itemId;
        public String couponTotalCount;
        public String couponRemainCount;
        public String itemUrl;
        public String tpwd;
        public String longTpwd;
        public String maxCommissionRate;
        public String shortUrl;
        public String minCommissionRate;
        public String  kuaiZhanUrl;
    }
}
