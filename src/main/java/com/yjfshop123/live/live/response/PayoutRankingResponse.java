package com.yjfshop123.live.live.response;

import java.util.List;

public class PayoutRankingResponse {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * num : 1
         * user_id : 17
         * user_nickname : 客服2
         * sex : 2
         * city_name : 呼和浩特市
         * show_photo : http://pic1.win4000.com/wallpaper/1/5390434de95dd.jpg
         * is_vip : 0
         * coin : 188
         * is_follow : 1
         */

        private int num;
        private int user_id;
        private String user_nickname;
        private int sex;
        private String city_name;
        private String show_photo;
        private int is_vip;
        private String coin;
        private int is_follow;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

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

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getShow_photo() {
            return show_photo;
        }

        public void setShow_photo(String show_photo) {
            this.show_photo = show_photo;
        }

        public int getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(int is_vip) {
            this.is_vip = is_vip;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }
    }
}
