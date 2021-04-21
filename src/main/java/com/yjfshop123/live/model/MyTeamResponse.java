package com.yjfshop123.live.model;

public class MyTeamResponse {
    /**
     *      "promotion_num":100,//推广数量
     *      "team_total_num":100,//团队总人数
     *      "team_activity_num":100,//团队活跃度
     *      "team_gold_income":100,//团队金蛋收益
     *      "team_silver_income":100,//团队银蛋收益
     *      "team_bad_income":100,//团队臭蛋收益
     *           "team_max_medaling":100,//团队最高选品等级
     *            "partner_status": 2,           //合伙人状态 0:未认证 1:认证中，2:认证通过，10:认证失败）
     *           "invite_user_name":"邀请人的姓名"
     * "invite_user_id":"邀请人的id"
     * "is_follow":"1"//当前登录的用户是否已关注推荐人  1是已关注   0是未关注
     */
    public  String promotion_num;
    public  String team_total_num;
    public  String team_activity_num;
    public  String team_gold_income;
    public  String team_silver_income;
    public  String team_bad_income;
    public  String team_max_medaling;
    public int  partner_status;
    public  String invite_user_name;
    public  String invite_user_id;
    public  String up_down_pic;
    public int  is_follow;
    public String hero_activity;//英雄活跃
    public String league_activity;//联盟活跃度
}
