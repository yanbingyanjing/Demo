package com.yjfshop123.live.model;

import java.util.List;

public class OtcOrderListResponse {
    public List<OtcOrderData>  list;
    public class OtcOrderData {
        public String order_sn;
        public String order;
        public int order_type;//0-买入订单 1-卖出订单
        public String time;
        public String eggs;
        public String money;
        public String desc;
        public String pay_type;
    }
}
