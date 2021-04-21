package com.yjfshop123.live.shop.ziying.model;

import java.util.List;

public class WuliuResponse {
    public String code;//success  error

    public String msg;
    public List<WuliuData> list;
    public class  WuliuData{
        public String title;
        public String desc;
    }
}
