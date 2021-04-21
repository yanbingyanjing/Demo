package com.yjfshop123.live.live;

import android.os.Bundle;


public interface IMLVBLiveRoomListener {

    /**
     * 创建房间的结果回调接口
     */
    public interface CreateRoomCallback {
        void onError(int errCode, String errInfo);
        void onSuccess(String RoomID);
    }

    /**
     * 创建房间的结果回调接口
     */
    public interface EnterRoomCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

    /**
     * 离开房间的结果回调接口
     */
    public interface ExitRoomCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

    /**
     * 进入连麦的结果回调接口
     */
    public interface JoinAnchorCallback {
        //错误回调
        void onError(int errCode, String errInfo);
        //成功回调
        void onSuccess();
    }

    /**
     * 错误回调
     *
     * SDK 不可恢复的错误，一定要监听，并分情况给用户适当的界面提示
     *
     * @param errCode 	错误码
     * @param errMsg 	错误信息
     * @param extraInfo 额外信息，如错误发生的用户，一般不需要关注，默认是本地错误
     */
    public void onError(int errCode, String errMsg, Bundle extraInfo);

    /**
     * 播放器回调接口
     */
    public interface PlayCallback {
        /**
         * 开始回调
         */
        void onBegin();
        /**
         * 错误回调
         *
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void onError(int errCode, String errInfo);

        /**
         * 其他事件回调
         *
         * @param event 事件 ID
         * @param param 事件附加信息
         */
        void onEvent(int event, Bundle param);
    }

    //####################################################################################################################

    public interface RequestCallback {

        void onError(int errCode, String errInfo);

        void onSuccess(String result);
    }
    //####################################################################################################################

}