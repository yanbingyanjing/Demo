package com.yjfshop123.live.shop.model;

import java.util.List;

public class HomeFenlei {

    public long time;
    public int code;
    public String msg;
    public List<DataSecond> data;


    public static class DataSecond {
        public String cpic;
        public int cid;
        public String cname;
        public Subcategorie[] subcategories;
    }

    public class Subcategorie {
        public String scpic;
        public int subcid;
        public String subcname;

    }
}
