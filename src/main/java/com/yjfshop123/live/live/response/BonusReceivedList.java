package com.yjfshop123.live.live.response;

import java.util.List;

public class BonusReceivedList {
    /**
     * list : [{"money":5,"user_id":10005,"avatar":"https://dcyun-1257995576.picsh.myqcloud.com/upload/20200429/E5689C688EBFAE5C36BD30EC289F81A7.jpg?imageMogr2/thumbnail/600x","user_nickname":"艳贵人","create_time":"2020-05-18 15:31:23"}]
     * num : 1/2
     */

    private String num;
    private List<ListBean> list;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * money : 5
         * user_id : 10005
         * avatar : https://dcyun-1257995576.picsh.myqcloud.com/upload/20200429/E5689C688EBFAE5C36BD30EC289F81A7.jpg?imageMogr2/thumbnail/600x
         * user_nickname : 艳贵人
         * create_time : 2020-05-18 15:31:23
         */

        private int money;
        private int user_id;
        private String avatar;
        private String user_nickname;
        private String create_time;

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}

