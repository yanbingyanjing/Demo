package com.yjfshop123.live.model;

import com.google.gson.annotations.SerializedName;

public class TaskNewResponse {
    public static String view_video="view_video";//看视频
    public static String wechat_share="wechat_share";//微信朋友发圈

    public static String quanzi="quanzi";//大公鸡圈子发一篇帖子
    public static String short_video="short_video";//大公鸡圈子发短视频
    public static String follow_daren="follow_daren";//需要执行跳转主播个人中心
    public static String invite_new_user="invite_new_user";//跳转到邀请页面
    public static String send_gift="send_gift";//跳转到直播页面

    public static String short_video_like="short_video_like";//大公鸡短视频点赞评论关注
    public static String kuaishou_like="kuaishou_like";//快手点赞
    public static String chunzhuang_like="quanzi_like";//春庄赞评论关注


    public String task_center_icon;
    public TaskData[] tasks;
    public class TaskData{
        public String title;
        public TaskItem[] list;
    }
    public class TaskItem{
        /**
         *    "task_icon": "https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20201022/app_icon/8b185a7823f3929a3309c194012ceb81.jpg?imageMogr2/thumbnail/600x",//任务图片
         *                                  "name": "观看视频",
         *                                   "des": "任务：分享朋友圈",//任务的描述
         *                                   "time": 660,//每日任务 看视频类别，time字段表示的时间，其他任务则是需要完成的次数
         *                                   "deal_time": 2//完成次数
         *                                   "task_type":"video",//任务类别
         *                                   "is_complete": false//每日任务是否已完成
         *                                   "end_time":"2021-02-01",//任务结束时间，
         */
        public int id;
        public String task_icon;
        public String name;
        @SerializedName("desc")
        public String des;
        public int template_id;
        public String time;
        public int need_times;
        @SerializedName("complete_times")
        public String deal_time;
        public String tag;
        public String daren_id;//需要关注的主播id

        public boolean is_complete;
        public String end_time;
        public String task_status;
        public String task_example;
        public int need_check;
    }

    public static class TaskCenterData{
        public int type;//1:top   2  title   3  任务   4没有任务
        public Object data;
    }
}
