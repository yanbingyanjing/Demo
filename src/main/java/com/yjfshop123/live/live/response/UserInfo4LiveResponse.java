package com.yjfshop123.live.live.response;

public class UserInfo4LiveResponse {
    /**
     * user_info : {"user_id":125905,"user_nickname":"大包子","sex":2,"age":20,"used_coin":0,"avatar":"https://dcyun-1257995576.picsh.myqcloud.com/upload/20190719/ea5ea6724449102e6d3b0d4ed661a6b2.jpg","signature":"寻找小宝贝儿","is_vip":0,"be_follow_num":0,"follow_num":0,"is_manager":0,"is_banspeech":0}
     * is_follow : 0
     * is_manager : 0
     */

    private UserInfoBean user_info;
    private int is_follow;
    private int is_manager;

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getIs_manager() {
        return is_manager;
    }

    public void setIs_manager(int is_manager) {
        this.is_manager = is_manager;
    }

    public static class UserInfoBean {
        /**
         * user_id : 125905
         * user_nickname : 大包子
         * sex : 2
         * age : 20
         * used_coin : 0
         * avatar : https://dcyun-1257995576.picsh.myqcloud.com/upload/20190719/ea5ea6724449102e6d3b0d4ed661a6b2.jpg
         * signature : 寻找小宝贝儿
         * is_vip : 0
         * be_follow_num : 0
         * follow_num : 0
         * is_manager : 0
         * is_banspeech : 0
         */

        private int user_id;
        private String user_nickname;
        private int sex;
        private int age;
        private int used_coin;
        private String avatar;
        private String signature;
        private int is_vip;
        private int be_follow_num;
        private int follow_num;
        private int is_manager;
        private int is_banspeech;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getUsed_coin() {
            return used_coin;
        }

        public void setUsed_coin(int used_coin) {
            this.used_coin = used_coin;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public int getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(int is_vip) {
            this.is_vip = is_vip;
        }

        public int getBe_follow_num() {
            return be_follow_num;
        }

        public void setBe_follow_num(int be_follow_num) {
            this.be_follow_num = be_follow_num;
        }

        public int getFollow_num() {
            return follow_num;
        }

        public void setFollow_num(int follow_num) {
            this.follow_num = follow_num;
        }

        public int getIs_manager() {
            return is_manager;
        }

        public void setIs_manager(int is_manager) {
            this.is_manager = is_manager;
        }

        public int getIs_banspeech() {
            return is_banspeech;
        }

        public void setIs_banspeech(int is_banspeech) {
            this.is_banspeech = is_banspeech;
        }
    }
}
