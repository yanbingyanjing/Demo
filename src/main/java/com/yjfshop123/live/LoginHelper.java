package com.yjfshop123.live;

import com.yjfshop123.live.Interface.ILoginView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;

public class LoginHelper {

    private ILoginView loginView;
    private boolean isFirst;
    private int number = 0;
    //private static String code = "[70001,70002,70003,70005,70009,70013,70014,70016,70020,70052,70107,70114,70050,70051]";

    public LoginHelper(ILoginView view){
        loginView = view;
        isFirst = true;
    }

    public void loginSDK(final String userId, final String userSig){
        number ++;
        TIMManager.getInstance().login(userId, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (isFirst){
                    loginView.onLoginSDKFailed(i, s);
                }

                isFirst = false;
                if (number < 4){
                    //失败连续登录三次
                    loginSDK(userId, userSig);
                }
            }

            @Override
            public void onSuccess() {
                if (isFirst){
                    loginView.onLoginSDKSuccess();
                }

                isFirst = false;
            }
        });
    }

}
