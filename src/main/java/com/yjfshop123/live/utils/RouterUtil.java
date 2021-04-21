package com.yjfshop123.live.utils;

import android.app.Activity;
import android.content.Intent;

import com.yjfshop123.live.ui.activity.LoginActivity;

public class RouterUtil {
    public static void logout(Activity activity){
        Intent intent=new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
       activity.finish();
    }
}
