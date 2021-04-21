package com.yjfshop123.live.shop.model;

import java.util.List;

public class ShopList {

    public long time;
    public int code;
    public String msg;
    public Data data;


    public class Data {
        public int totalNum;
        public String pageId;
        public List<ShopData> list;
    }

    public class ShopData {
        public String id;
        public String goodsId;
        public String itemLink;
        public String title;
        public String dtitle;
        public String desc;
        public String cid;
        public String[] subcid;
        public String tbcid;
        public String mainPic;
        public String marketingMainPic;
        public String video;
        public String originalPrice;
        public String actualPrice;
        public String discounts;
        public String commissionType;
        public String commissionRate;
        public String couponLink;
        public String couponTotalNum;
        public String couponReceiveNum;
        public String couponEndTime;
        public String couponStartTime;
        public String couponPrice;


        public String couponConditions;
        public String monthSales;
        public String twoHoursSales;
        public String dailySales;
        public String brand;
        public String brandId;
        public String brandName;
        public String createTime;
        public String activityType;
        public String activityStartTime;
        public String activityEndTime;
        public int shopType;
        public String haitao;
        public String sellerId;
        public String shopName;
        public String shopLevel;
        public String descScore;
        public String dsrScore;
        public String dsrPercent;
        public String shipScore;
        public String shipPercent;
        public String serviceScore;
        public String servicePercent;
        public String hotPush;
        public String teamName;
        public String goldSellers;
        public String detailPics;  //字符串是数组字符串格式是 Img[]
        public String imgs;//需要自己切分开
        public String reimgs;
        public String quanMLink;
        public String hzQuanOver;
        public String yunfeixian;
        public String estimateAmount;
        public String shopLogo;
        public String[] specialText;
        public String freeshipRemoteDistrict;

        public String isSubdivision;
        public String subdivisionId;
        public String subdivisionName;
        public String subdivisionRank;
    }

    public class Img {
        public String img;
        public int height;
        public int width;
    }
}
