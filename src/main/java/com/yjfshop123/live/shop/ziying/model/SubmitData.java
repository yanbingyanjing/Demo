package com.yjfshop123.live.shop.ziying.model;

public class SubmitData {
    public int type;//1地址  2//店铺名字   3商品   4备注 配送方式   5总计 小计      6历史记录   7话费
    public  static int address_type = 1;
    public static int shop_type = 2;
    public static int shop_item_type = 3;
    public static int remark_type = 4;
    public static int total_type = 5;
    public static int history_type = 6;
    public static int huafei_type = 7;
    public Object itemdata;
}
