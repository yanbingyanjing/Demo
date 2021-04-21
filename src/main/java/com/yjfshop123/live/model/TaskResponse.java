package com.yjfshop123.live.model;

public class TaskResponse {
    /**
     *     "times":"50"，//每日任务 观看视频需要多少秒
     *     "task_img":"http://xxxx",//每日任务图片
     *    "task_detail_img":"xxxxx"//任务中心介绍图
     *       "is_complete":false//每日任务是否已完成
     */
    public String time;
    public String task_icon;
    public String task_center_icon;
    public boolean is_complete;
    public String name;

}
