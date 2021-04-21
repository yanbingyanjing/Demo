package com.yjfshop123.live.model;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {
    public int user_id;
    public String token;
    public String mobile;
    public String user_nickname;
    public int sex;
    public int age;
    public String invite_code;//邀请码
    public String qq;
    public String weixin;
    public String avatar;
    public String signature;
    public String speech_introduction;
    //public String[] album;
    public String[] video;
    public String tags;
    public int is_vip;
    public String vip_expire_time;
    public int be_look_num;
    public int be_follow_num;
    public int follow_num;
    public String province_name;
    public String city_name;
    public String district_name;
    public int open_video;
    public int video_cost;
    public int open_speech;
    public int speech_cost;
    public String last_look_me;
    public String last_follow_me;
    public String last_follow;
    public int auth_status;
    public int daren_status;
    public int open_position;
    public int guild_status;
    @SerializedName("level")
    public String vip_level;
    public String level_title;
    public String level_name;
    public int person_activity_num;
    public int team_activity_num;
    public String gold_egg;
    public String silver_egg;
    public String bad_egg;
    public int partner_status; // "partner_status": 2,            //合伙人状态 0:未认证 1:认证中，2:认证通过，10:认证失败）
    public String like_num;//获赞
    public String forum_num;//动态
}
