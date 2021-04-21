package com.yjfshop123.live.model;

public class TargetRewardResponse {
    /**
     *  "target_reward": "iphone",   //目标奖
     *       "target_reward_icon": "pc/xxx",   //目标奖图片
     *       "target_reward_count": 1,   //目标奖数量


              "target_reward_value": 50金蛋   //奖品价值500个金蛋/银蛋/金蛋加成  iphone
     *       "target_reward_progress": 45,   //目标奖进度
     *       "target_reward_is_get": true,   //目标奖是否已领取
     * target_reward_des：//奖品描述
     *       "is_real_reward": true,   //是否是实物奖励
     *              "target_reward_need_activity_num": 45,   //需要完成多少活跃度才能获得目标奖
     *                   *    "team_activity_num": 1,    //团队活跃度
     */
    public String target_reward;
    public String target_reward_icon;
    public float target_reward_count;
    public String target_reward_des;
    public float target_reward_progress;
    public boolean target_reward_is_get;
    public boolean is_real_reward;
    public int id;
    public String target_reward_need_activity_num;
    public String   date;
    public String team_activity_num;
    public String target_reward_value;
}
