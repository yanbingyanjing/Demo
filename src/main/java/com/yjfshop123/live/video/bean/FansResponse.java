package com.yjfshop123.live.video.bean;

import java.util.List;

public class FansResponse {


    /**
     * list : [{"user_id":10038,"age":34,"sex":"男","nickname":"李亚山","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKNSU1d3dXJAIv95arkmUibP2OoPlaJrbuJv0cCkERzljOa5Zvx8XM0ic0o5tU8ys3RMibZVmPx2icKxA/132","signature":"该用户尚未编辑个性签名.","is_vip":0,"create_time":"2020-03-31","is_follow":0}]
     * total_page : 1
     * total : 1
     */

    private int total_page;
    private int total;
    private List<ListBean> list;

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * user_id : 10038
         * age : 34
         * sex : 男
         * nickname : 李亚山
         * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKNSU1d3dXJAIv95arkmUibP2OoPlaJrbuJv0cCkERzljOa5Zvx8XM0ic0o5tU8ys3RMibZVmPx2icKxA/132
         * signature : 该用户尚未编辑个性签名.
         * is_vip : 0
         * create_time : 2020-03-31
         * is_follow : 0
         */

        private int user_id;
        private int age;
        private String sex;
        private String nickname;
        private String avatar;
        private String signature;
        private int is_vip;
        private String create_time;
        private int is_follow;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }
    }
}
