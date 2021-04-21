package com.yjfshop123.live.shop.ziying.model;

public class AddressCityModel {
    public String id;
    public String name;
    public CityList[] cityList;

    public class CityList {
        public String id;
        public String name;
        public String pid;
        public DistrictList[] districtList;
    }

    public class DistrictList {
        public String id;
        public String name;
        public String pid;

    }
}
