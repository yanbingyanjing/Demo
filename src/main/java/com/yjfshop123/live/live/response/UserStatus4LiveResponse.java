package com.yjfshop123.live.live.response;

public class UserStatus4LiveResponse {

    /**
     * code : 1
     * msg : OK
     * data : {"user_level":3,"is_vip":0,"is_watch":0,"is_manager":0,"is_banspeech":0}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_level : 3
         * is_vip : 0
         * is_watch : 0
         * is_manager : 0
         * is_banspeech : 0
         */

        private int user_level;
        private int is_vip;
        private int is_watch;
        private int is_manager;
        private int is_banspeech;
        private int is_bulletscreen;
        private String mount_url;

        public String getMount_url() {
            return mount_url;
        }

        public void setMount_url(String mount_url) {
            this.mount_url = mount_url;
        }

        public int getIs_bulletscreen() {
            return is_bulletscreen;
        }

        public void setIs_bulletscreen(int is_bulletscreen) {
            this.is_bulletscreen = is_bulletscreen;
        }

        public int getUser_level() {
            return user_level;
        }

        public void setUser_level(int user_level) {
            this.user_level = user_level;
        }

        public int getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(int is_vip) {
            this.is_vip = is_vip;
        }

        public int getIs_watch() {
            return is_watch;
        }

        public void setIs_watch(int is_watch) {
            this.is_watch = is_watch;
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
