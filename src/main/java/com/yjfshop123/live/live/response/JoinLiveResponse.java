package com.yjfshop123.live.live.response;


public class JoinLiveResponse {
    /**
     * b_live_url : {"rtmp":"rtmp://pull.test.dcyun.cn/live/live_5234920?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69","http_flv":"http://pull.test.dcyun.cn/live/live_5234920.flv?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69","http_m3u8":"http://pull.test.dcyun.cn/live/live_5234920.m3u8?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69"}
     * viewer_num : 1
     * is_follow : 0
     * time_live : []
     * viewer_user : {"user_level":10,"is_vip":0,"is_watch":0,"is_manager":0,"is_banspeech":0}
     * live_user : {"user_id":5234920,"user_nickname":"那个人","avatar":"upload/20200117/A135C3F42FB9293E21BF4EC1E7A080D9.jpg","sex":1,"total_coin_num":0,"watch_num":0}
     * live_warning : {"color":"red","content":"检查人员会24小时巡查直播间，直播内容包含任何低俗、暴露和涉黄内容一律封号处理～"}
     * extra : {"object_domain":"https://dcyun-1257995576.picsh.myqcloud.com/"}
     */

    private BLiveUrlBean b_live_url;
    private int viewer_num;
    private int is_follow;
    private ViewerUserBean viewer_user;
    private LiveUserBean live_user;
    private LiveWarningBean live_warning;
    private ExtraBean extra;
    private String time_live;
    private String is_patrol;

    public String getIs_patrol() {
        return is_patrol;
    }

    public void setIs_patrol(String is_patrol) {
        this.is_patrol = is_patrol;
    }

    public BLiveUrlBean getB_live_url() {
        return b_live_url;
    }

    public void setB_live_url(BLiveUrlBean b_live_url) {
        this.b_live_url = b_live_url;
    }

    public int getViewer_num() {
        return viewer_num;
    }

    public void setViewer_num(int viewer_num) {
        this.viewer_num = viewer_num;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public ViewerUserBean getViewer_user() {
        return viewer_user;
    }

    public void setViewer_user(ViewerUserBean viewer_user) {
        this.viewer_user = viewer_user;
    }

    public LiveUserBean getLive_user() {
        return live_user;
    }

    public void setLive_user(LiveUserBean live_user) {
        this.live_user = live_user;
    }

    public LiveWarningBean getLive_warning() {
        return live_warning;
    }

    public void setLive_warning(LiveWarningBean live_warning) {
        this.live_warning = live_warning;
    }

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public String getTime_live() {
        return time_live;
    }

    public void setTime_live(String time_live) {
        this.time_live = time_live;
    }

    public static class BLiveUrlBean {
        /**
         * rtmp : rtmp://pull.test.dcyun.cn/live/live_5234920?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69
         * http_flv : http://pull.test.dcyun.cn/live/live_5234920.flv?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69
         * http_m3u8 : http://pull.test.dcyun.cn/live/live_5234920.m3u8?txSecret=15f3d2697ed1199fe071297403734c40&txTime=5e22db69
         */

        private String rtmp;
        private String http_flv;
        private String http_m3u8;

        public String getRtmp() {
            return rtmp;
        }

        public void setRtmp(String rtmp) {
            this.rtmp = rtmp;
        }

        public String getHttp_flv() {
            return http_flv;
        }

        public void setHttp_flv(String http_flv) {
            this.http_flv = http_flv;
        }

        public String getHttp_m3u8() {
            return http_m3u8;
        }

        public void setHttp_m3u8(String http_m3u8) {
            this.http_m3u8 = http_m3u8;
        }
    }

    public static class ViewerUserBean {
        /**
         * user_level : 10
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

    public static class LiveUserBean {
        /**
         * user_id : 5234920
         * user_nickname : 那个人
         * avatar : upload/20200117/A135C3F42FB9293E21BF4EC1E7A080D9.jpg
         * sex : 1
         * total_coin_num : 0
         * watch_num : 0
         */

        private int user_id;
        private String user_nickname;
        private String avatar;
        private int sex;
        private String total_coin_num;
        private int watch_num;

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

        public String getTotal_coin_num() {
            return total_coin_num;
        }

        public void setTotal_coin_num(String total_coin_num) {
            this.total_coin_num = total_coin_num;
        }

        public int getWatch_num() {
            return watch_num;
        }

        public void setWatch_num(int watch_num) {
            this.watch_num = watch_num;
        }
    }

    public static class LiveWarningBean {
        /**
         * color : red
         * content : 检查人员会24小时巡查直播间，直播内容包含任何低俗、暴露和涉黄内容一律封号处理～
         */

        private String color;
        private String content;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class ExtraBean {
        /**
         * object_domain : https://dcyun-1257995576.picsh.myqcloud.com/
         */

        private String object_domain;

        public String getObject_domain() {
            return object_domain;
        }

        public void setObject_domain(String object_domain) {
            this.object_domain = object_domain;
        }
    }
}
