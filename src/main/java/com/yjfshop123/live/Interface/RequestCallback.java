package com.yjfshop123.live.Interface;

public interface RequestCallback {

    void onError(int errCode, String errInfo);

    void onSuccess(String result);
}
