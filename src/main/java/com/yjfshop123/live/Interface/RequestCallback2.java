package com.yjfshop123.live.Interface;

public interface RequestCallback2 {

    void onError(int errCode, String errInfo);

    void onSuccess(String result);

    void onProgress(long totalBytes, long remainingBytes, boolean done);
}
