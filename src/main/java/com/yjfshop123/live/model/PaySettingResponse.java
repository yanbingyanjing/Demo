package com.yjfshop123.live.model;

import java.util.List;

public class PaySettingResponse {
    public List<PayData> list;
    public class PayData {
        public String id;
        public String sub_bank;
        public String  card;
        public String type;
        public String name;
        public String bank;
        public String qrcode_url;
    }

}
