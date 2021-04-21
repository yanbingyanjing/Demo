package com.yjfshop123.live.live.commondef;

public class MLVBCommonDef {
    public interface LiveRoomErrorCode {
        int ERROR_PUSH = -3; //推流错误
        int ERROR_LICENSE_INVALID = -5; //license 校验失败
        int ERROR_PLAY = -6; //播放错误
        int ERROR_IM_FORCE_OFFLINE = -7; // IM 被强制下线（例如：多端登录）
    }
}
