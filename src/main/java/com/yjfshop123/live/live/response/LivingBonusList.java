package com.yjfshop123.live.live.response;

import java.util.List;

public class LivingBonusList {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * fl_uid : 10020
         * fb_uid : 10000
         * avatar : upload/20200319/197A3E230307A7E44CEEECB80303E3DC.jpg
         * user_nickname : 超级管理员
         * id : 1
         * live_id : 8
         */

        private int fl_uid;
        private int fb_uid;
        private String avatar;
        private String user_nickname;
        private int id;
        private int live_id;

        public int getFl_uid() {
            return fl_uid;
        }

        public void setFl_uid(int fl_uid) {
            this.fl_uid = fl_uid;
        }

        public int getFb_uid() {
            return fb_uid;
        }

        public void setFb_uid(int fb_uid) {
            this.fb_uid = fb_uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLive_id() {
            return live_id;
        }

        public void setLive_id(int live_id) {
            this.live_id = live_id;
        }
    }
}
