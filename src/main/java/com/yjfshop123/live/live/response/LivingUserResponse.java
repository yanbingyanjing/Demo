package com.yjfshop123.live.live.response;

import java.util.List;

public class LivingUserResponse {
    private List<LiveListBean> live_list;

    public List<LiveListBean> getLive_list() {
        return live_list;
    }

    public void setLive_list(List<LiveListBean> live_list) {
        this.live_list = live_list;
    }

    public static class LiveListBean {
        /**
         * live_id :
         * user_id :
         * user_nickname : 姜瑞佳
         * avatar : zhibo/2019/1/28/1234567892019012829279.jpg
         * sex :
         */

        private String live_id;
        private String user_id;
        private String user_nickname;
        private String avatar;
        private String sex;

        public String getLive_id() {
            return live_id;
        }

        public void setLive_id(String live_id) {
            this.live_id = live_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}
