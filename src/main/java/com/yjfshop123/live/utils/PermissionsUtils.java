package com.yjfshop123.live.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.yjfshop123.live.R;

public class PermissionsUtils {

    private static boolean isCancelable = false;

    public static void initPermission(Context context){
        mPermissionsChecker = new PermissionsChecker(context);
        isRequireCheck = true;
    }

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String PACKAGE_URL_SCHEME = "package:";

    private static PermissionsChecker mPermissionsChecker;
    private static boolean isRequireCheck;





    static final String[] permissionsAll = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
//            Manifest.permission.CALL_PHONE,//播打电话权限 暂时删除
            Manifest.permission.READ_PHONE_STATE,
    };

    public static void onResume(IPermissions iPermissions, Activity activity){
        if (mPermissionsChecker.lacksPermissions(permissionsAll)) {
            ActivityCompat.requestPermissions(activity, permissionsAll, PERMISSION_REQUEST_CODE);
        } else {
            iPermissions.allPermissions();
        }
    }

    static final String[] permissionsSplash = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA,
//            Manifest.permission.CALL_PHONE,//播打电话权限 暂时删除
            Manifest.permission.READ_PHONE_STATE,
    };

    public static void onResumeSplash(IPermissions iPermissions, Activity activity){
        if (isRequireCheck) {
            if (mPermissionsChecker.lacksPermissions(permissionsSplash)) {
                ActivityCompat.requestPermissions(activity, permissionsSplash, PERMISSION_REQUEST_CODE);
            } else {
                iPermissions.allPermissions();
            }
        } else {
            isRequireCheck = true;
        }
    }






    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults,
                                                  IPermissions iPermissions, Activity activity, boolean isCancelable_){
        isCancelable = isCancelable_;
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            iPermissions.allPermissions();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog(activity);
        }
    }

    private static boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private static void showMissingPermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isCancelable){
                    activity.finish();
                }
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings(activity);
            }
        });
        builder.setCancelable(isCancelable);
        builder.show();
    }

    private static void startAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + activity.getPackageName()));
        activity.startActivity(intent);
    }

}
