package com.yjfshop123.live.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.MapLoiIml;
import com.yjfshop123.live.net.utils.NLog;

public class MapUtil implements AMapLocationListener {

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mLocationClient = null;

    private Context context;
    private MapLoiIml mapLoiIml;

    public MapUtil(Context context, MapLoiIml mapLoiIml) {
        this.context = context;
        this.mapLoiIml = mapLoiIml;
    }

    public void LoPoi() {
        //声明mLocationOption对象
        mLocationOption = new AMapLocationClientOption();
        //声明AMapLocationClient类对象
        mLocationClient = new AMapLocationClient(context);
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);

        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为30s
        mLocationOption.setInterval(1000 * 60);//三分钟定位一次
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    public void destroy() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                /*//定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(amapLocation.getTime());
//                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
//                amapLocation.getAOIName();//获取当前定位点的AOI信息*/

                String address = amapLocation.getAddress();
                String currentWeizhi = amapLocation.getProvince() + "," + amapLocation.getCity() + "," + amapLocation.getDistrict();
                if (TextUtils.isEmpty(address)) {
                    address = amapLocation.getCity()
                            + amapLocation.getDistrict()
                            + amapLocation.getStreet()
                            + amapLocation.getStreetNum();
                }

                double latitude = amapLocation.getLatitude();
                double longitude = amapLocation.getLongitude();
                UserInfoUtil.setAddress(String.valueOf(latitude), String.valueOf(longitude), amapLocation.getCity());
            NLog.d("地址",String.valueOf(latitude)+" "+String.valueOf(longitude)+currentWeizhi);
                App.currentWeizhi = currentWeizhi;
                mapLoiIml.onMapSuccess(latitude, longitude, address, currentWeizhi);
//                String locationInfo = "latitude:" + latitude + "longitude:" + longitude + "address:" + address;
//                NToast.longToast(mContext, locationInfo);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                NToast.longToast(mContext, "location Error, ErrCode:"
//                        + amapLocation.getErrorCode() + ", errInfo:"
//                        + amapLocation.getErrorInfo());
                NLog.d("地址获取错误",amapLocation.getErrorInfo());
                mapLoiIml.onMapFail(amapLocation.getErrorInfo());
            }
        }
    }
}
