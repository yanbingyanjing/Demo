package com.yjfshop123.live.shop.ziying.model;

public class DefaultAddress {
    public String code;//success  error

    public String msg;
    public AddressData data;
    /**
     * {
     *   "code": "success",
     *   "data": {
     *     "address_id": 59,
     *     "uid": 429,
     *     "name": "12233",
     *     "telephone": "18520000000",
     *     "address": "2222",
     *     "city_id": 87,
     *     "country_id": 1327,
     *     "province_id": 4,
     *     "region": "山西,长治市,平顺县",
     *     "is_default": 1
     *   }
     * }
     */
    public class AddressData{
        public String address_id;
        public String uid;
        public String name;
        public String telephone;
        public String address;
        public String city_id;
        public String country_id;
        public String province_id;
        public String region;
        public int is_default;
    }
}
