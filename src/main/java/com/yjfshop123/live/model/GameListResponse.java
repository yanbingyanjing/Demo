package com.yjfshop123.live.model;

import java.util.List;

public class GameListResponse {
    public int exchange_coupon;
    public int today_exchange_times;
    public String[] coinList;
    public String coin;
    public String coin_desc;
    public String coupon_desc;
    public String desc;
    public List<GameData> gameList;
    public class GameData {
        public String id;
        public String name;
        public String pic;
    }
}
