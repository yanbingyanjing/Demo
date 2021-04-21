package com.yjfshop123.live.live.response;

import java.util.List;

public class ManagerListResponse {
    /**
     * list : [{"user_id":1,"user_nickname":"admin","avatar":"https://img5.duitang.com/uploads/item/201410/17/20141017235209_MEsRe.thumb.700_0.jpeg","sex":1}]
     * extra : {"max_manager":5,"object_domain":"https://dcyun-1257995576.picsh.myqcloud.com/"}
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
         * max_manager : 5
         * object_domain : https://dcyun-1257995576.picsh.myqcloud.com/
         */

        private int max_manager;
        private String object_domain;

        public int getMax_manager() {
            return max_manager;
        }

        public void setMax_manager(int max_manager) {
            this.max_manager = max_manager;
        }

        public String getObject_domain() {
            return object_domain;
        }

        public void setObject_domain(String object_domain) {
            this.object_domain = object_domain;
        }
    }

    public static class ListBean {
        /**
         * user_id : 1
         * user_nickname : admin
         * avatar : https://img5.duitang.com/uploads/item/201410/17/20141017235209_MEsRe.thumb.700_0.jpeg
         * sex : 1
         */

        private int user_id;
        private String user_nickname;
        private String avatar;
        private int sex;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }
}
