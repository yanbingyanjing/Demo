package com.yjfshop123.live.shop.model;

import com.yjfshop123.live.net.response.VideoDynamicResponse;

import java.util.List;

public class VideoList {

    public long time;
    public int code;
    public String msg;
    public Data data;

    public class Data {
        public List<VideoDynamicResponse.ListBean> lists;
        public int currentPage;
        public int pageSize;
        public int totalCount;
    }

    public class VideoData {
        public String goodsId;
        public String title;
        public String mainpic;
        public String mothSales;
        public String dailySales;
        public String couponId;
        public String couponPrice;
        public String originPrice;
        public String actualPrice;
        public String couponTotalNum;
        public String couponRemainNum;
        public String couponReceiveNum;
        public String couponLink;
        public String couponEndTime;
        public String couponStartTime;
        public String commissionRate;
        public String videoUrl;
        public String videoCoverImg;
        public String videoLikeCount;
        public String videoCommentCount;
        public String videoShareCount;
    }
}
