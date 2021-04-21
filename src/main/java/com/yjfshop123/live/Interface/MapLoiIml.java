package com.yjfshop123.live.Interface;

public interface MapLoiIml {
    void onMapSuccess(double latitude, double longitude, String address,String currentWeizhi);

    void onMapFail(String msg);
}
