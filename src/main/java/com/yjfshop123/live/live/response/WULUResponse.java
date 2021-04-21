package com.yjfshop123.live.live.response;

import java.util.List;

public class WULUResponse {
    /**
     * list : [{"user_id":203139,"user_nickname":"13000000020","sex":2,"is_vip":0,"user_level":"LV6","cost_coin":1200}]
     * extra : {"object_domain":"http://zhibo005.oss-cn-beijing.aliyuncs.com/"}
     */

    private ExtraBean extra;
    private List<ListBean> list;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ExtraBean {
        /**
         * object_domain : http://zhibo005.oss-cn-beijing.aliyuncs.com/
         */

        private String object_domain;

        public String getObject_domain() {
            return object_domain;
        }

        public void setObject_domain(String object_domain) {
            this.object_domain = object_domain;
        }
    }

    public static class ListBean {
        /**
         * user_id : 203139
         * user_nickname : 13000000020
         * sex : 2
         * is_vip : 0
         * user_level : LV6
         * cost_coin : 1200
         */

        private int user_id;
        private String user_nickname;
        private int sex;
        private int is_vip;
        private String user_level;
        private String avatar;
        private int cost_coin;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public int getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(int is_vip) {
            this.is_vip = is_vip;
        }

        public String getUser_level() {
            return user_level;
        }

        public void setUser_level(String user_level) {
            this.user_level = user_level;
        }

        public int getCost_coin() {
            return cost_coin;
        }

        public void setCost_coin(int cost_coin) {
            this.cost_coin = cost_coin;
        }
    }
}
