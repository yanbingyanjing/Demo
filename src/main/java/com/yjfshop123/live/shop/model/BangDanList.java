package com.yjfshop123.live.shop.model;

import java.util.List;

public class BangDanList {
    public long time;
    public int code;
    public String msg;
    public List<DataItem> data;


    public class DataItem {
        public String id;
        public String goodsId;
        public int ranking;
        public int newRankingGoods;
        public String dtitle;
        public String actualPrice;
        public String commissionRate;
        public String couponPrice;
        public String couponReceiveNum;
        public String couponTotalNum;
        public String monthSales;
        public String twoHoursSales;
        public String dailySales;
        public String hotPush;
        public String mainPic;
        public String title;
        public String desc;
        public String originalPrice;
        public String couponLink;
        public String couponStartTime;
        public String couponEndTime;
        public String commissionType;
        public String createTime;
        public String activityType;


        public String[] picList;
        public String guideName;
        public int shopType;
        public String couponConditions;
        public String avgSales;
        public String entryTime;
        public String sellerId;
        public String quanMLink;
        public String hzQuanOver;
        public int yunfeixian;
        public int estimateAmount;
        public int freeshipRemoteDistrict;
        public String top;
        public String keyWord;
        public String upVal;
        public String hotVal;
        public String fresh;
        public String lowest;

    }
}
