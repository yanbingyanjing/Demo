package com.yjfshop123.live.live.live.common.utils;


public class TCConstants {

    //主播退出广播字段
    public static final String EXIT_APP         = "EXIT_APP";

    public static final String ROOM_TITLE       = "room_title";
    public static final String COVER_PIC        = "cover_pic";

    /**
     * IM 互动消息类型
     */
    public static final int IMCMD_PAILN_TEXT    = 1;   // 文本消息
    public static final int IMCMD_ENTER_LIVE    = 2;   // 用户加入直播
    public static final int IMCMD_EXIT_LIVE     = 3;   // 用户退出直播
    public static final int IMCMD_PRAISE        = 4;   // 点赞消息
    public static final int IMCMD_DANMU         = 5;   // 弹幕消息
    public static final int IMCMD_GIFT        = 6;   // 礼物消息
    public static final int IMCMD_GUARDIAN        = 7;   // 守护
    public static final int IMCMD_PK             = 8; // PK消息
    public static final int IMCMD_SYSTEM    = 10;   // 系统 通知 本地

    //观众
    public static final int ERROR_1 = 1001;//观众拉流失败
    public static final int ERROR_2 = 1002;//观众被T出直播间
    public static final int ERROR_3 = 1003;//观众收到主播关闭直播间
    public static final int ERROR_4 = 1004;//观众 进入直播间失败
    //主播
    public static final int ERROR_5 = 1005;//获取权限失败
    public static final int ERROR_6 = 1006;//创建直播间失败
    public static final int ERROR_7 = 1007;//关闭直播间
    public static final int ERROR_8 = 1008;//主播 网络异常直播间已经关闭

    //网络类型
    public static final int NETTYPE_NONE = 0;
    public static final int NETTYPE_WIFI = 1;
    public static final int NETTYPE_4G   = 2;
    public static final int NETTYPE_3G   = 3;
    public static final int NETTYPE_2G   = 4;

    public static final int MALE    = 0;
    public static final int FEMALE  = 1;

    //直播房间类型
    public static final String CHECKED_ID = "checkedId";
    //频道类型
    public static final String CLASS_ID = "classId";
    public static final int LIVE_TYPE_NORMAL = 0;//普通房间
    public static final int LIVE_TYPE_PWD = 1;//密码房间
    public static final int LIVE_TYPE_PAY = 2;//收费房间

    public static final int LIVE_FUNC_BEAUTY = 2001;//美颜
    public static final int LIVE_FUNC_CAMERA = 2002;//切换摄像头
    public static final int LIVE_FUNC_FLASH = 2003;//闪光灯
    public static final int LIVE_FUNC_MUSIC = 2004;//伴奏
    public static final int LIVE_FUNC_SHARE = 2005;//分享
    public static final int LIVE_FUNC_GAME = 2006;//游戏
    public static final int LIVE_FUNC_RED_PACK = 2007;//红包
    public static final int LIVE_FUNC_LINK_MIC = 2008;//主播连麦
    public static final int LIVE_FUNC_LINK_PK = 2009;//主播pK
    public static final int LIVE_FUNC_LINK_RECOR = 2010;//录制直播
}

