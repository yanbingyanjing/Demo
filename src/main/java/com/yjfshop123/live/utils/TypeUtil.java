package com.yjfshop123.live.utils;


import com.yjfshop123.live.App;

public class TypeUtil {


    private static final String FRAMENT_1_TYPE = "FRAMENT_1_TYPE";
    private static final String FRAMENT_2_TYPE = "FRAMENT_2_TYPE";
    private static final String FRAMENT_3_TYPE = "FRAMENT_3_TYPE";

    public static void setFrament1Type(int type){
        App.configUtil.storeIntShareData(FRAMENT_1_TYPE, type);
        App.configUtil.commit();
    }

    public static int getFrament1Type(){
        return App.configUtil.getIntShareData(FRAMENT_1_TYPE, 3);
    }

    public static void setFrament2Type(int type){
        App.configUtil.storeIntShareData(FRAMENT_2_TYPE, type);
        App.configUtil.commit();
    }

    public static int getFrament2Type(){
        return App.configUtil.getIntShareData(FRAMENT_2_TYPE, 2);
    }

    public static void setFrament3Type(int type){
        App.configUtil.storeIntShareData(FRAMENT_3_TYPE, type);
        App.configUtil.commit();
    }

    public static int getFrament3Type(){
        return App.configUtil.getIntShareData(FRAMENT_3_TYPE, 1);
    }
}
