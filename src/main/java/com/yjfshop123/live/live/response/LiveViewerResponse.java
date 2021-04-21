package com.yjfshop123.live.live.response;

import java.util.List;

public class LiveViewerResponse {
    /**
     * list : [{"user_id":202265,"user_nickname":"TTRT","avatar":"zhibo/cj/18/11/26/d4ba222687f0545c4a27d731a16e6019.jpg"}]
     * extra : {"object_domain":"https://dcyun-1257995576.picsh.myqcloud.com/"}
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * user_id : 202265
         * user_nickname : TTRT
         * avatar : zhibo/cj/18/11/26/d4ba222687f0545c4a27d731a16e6019.jpg
         */

        private int user_id;
        private String user_nickname;
        private String avatar;

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
    }
}
