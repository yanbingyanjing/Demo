package com.yjfshop123.live.model;

import java.util.List;

public class GoldPriceResponse {


    public String price;//当前金蛋价格

    public List<UpEntity> list;//上涨或者下降幅度

    public class UpEntity {
        public String price;//金蛋价格
        public String up_down;//上涨或者下降幅度
        public String time;//10.27  日期
    }
}


