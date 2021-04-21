package com.yjfshop123.live.live.response;

import java.util.List;

public class LivingListResponse {
    /**
     * live_list : [{"vod_type":1,"live_id":6183,"user_id":100353,"user_nickname":"赵云","avatar":"https://zb-1300742153.cos.ap-shanghai.myqcloud.com/upload/20200101/F7AEDA56962012A937C3358B5B06E335.jpg?imageMogr2/thumbnail/600x","sex":2,"title":"私人定制","cover_img":"upload/20200116/4AD660CA0D94CA205F99AEF3FE38B7B3.jpg","live_mode":1,"type":1,"in_coin":0,"start_time":1579142640,"city_name":"临沂市","viewer":2,"total_coin_num":13,"watch_num":0,"item_title":"综合什锦果蔬脆片混合装蔬菜干零食蔬果干秋葵香菇儿童250g/500g","item_url":"https://t00img.yangkeduo.com/goods/images/2019-09-10/19ef3fdc-5fa8-4fb2-a9d9-b9b2a0cfda81.jpg","zk_final_price":"12.12","real_price":"12.12"},{"vod_type":1,"live_id":6179,"user_id":101867,"user_nickname":"琪琪","avatar":"upload/20200106/A606E9F33EB15760518A78FDA46B9316.jpg","sex":2,"title":"鼠年行大运","cover_img":"upload/20200116/12AAD58C25A78F6CD5E0D94614C6127D.jpg","live_mode":1,"type":1,"in_coin":0,"start_time":1579142272,"city_name":"成都市","viewer":2,"total_coin_num":0,"watch_num":0,"item_title":"【拍2条59元】打底裤女加绒加厚保暖秋冬季外穿连裤袜秋裤瘦腿袜","item_url":"http://gw.alicdn.com/bao/uploaded/i1/3544055809/O1CN01Nd3jud1smZJXZS25x_!!0-item_pic.jpg","zk_final_price":"59","real_price":"259"},{"vod_type":1,"live_id":6169,"user_id":127932,"user_nickname":"格格吉祥","avatar":"upload/20200110/23E6175B74383E7589461B070E0F89C6.jpg","sex":2,"title":"早上好","cover_img":"upload/20200116/35A91D751B93DBB5D7EA02A871AC8E6E.jpg","live_mode":1,"type":1,"in_coin":0,"start_time":1579143413,"city_name":"沈阳市","viewer":2,"total_coin_num":7,"watch_num":0,"item_title":"洁玉2/4条装纯棉毛巾洗脸不掉毛吸水大人家用 全棉加厚洗脸巾","item_url":"https://t00img.yangkeduo.com/goods/images/2019-12-08/4a2033fd-ab21-4796-b23e-be43a73704fd.jpg","zk_final_price":"6.9","real_price":"16.9"}]
     * extra : {"object_domain":"https://zb-1300742153.cos.ap-shanghai.myqcloud.com/"}
     */

    private ExtraBean extra;
    private List<LiveListBean> live_list;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public List<LiveListBean> getLive_list() {
        return live_list;
    }

    public void setLive_list(List<LiveListBean> live_list) {
        this.live_list = live_list;
    }

    public static class ExtraBean {
        /**
         * object_domain : https://zb-1300742153.cos.ap-shanghai.myqcloud.com/
         */

        private String object_domain;

        public String getObject_domain() {
            return object_domain;
        }

        public void setObject_domain(String object_domain) {
            this.object_domain = object_domain;
        }
    }

    public static class LiveListBean {
        /**
         * vod_type : 1
         * live_id : 6183
         * user_id : 100353
         * user_nickname : 赵云
         * avatar : https://zb-1300742153.cos.ap-shanghai.myqcloud.com/upload/20200101/F7AEDA56962012A937C3358B5B06E335.jpg?imageMogr2/thumbnail/600x
         * sex : 2
         * title : 私人定制
         * cover_img : upload/20200116/4AD660CA0D94CA205F99AEF3FE38B7B3.jpg
         * live_mode : 1
         * type : 1
         * in_coin : 0
         * start_time : 1579142640
         * city_name : 临沂市
         * viewer : 2
         * total_coin_num : 13
         * watch_num : 0
         * item_title : 综合什锦果蔬脆片混合装蔬菜干零食蔬果干秋葵香菇儿童250g/500g
         * item_url : https://t00img.yangkeduo.com/goods/images/2019-09-10/19ef3fdc-5fa8-4fb2-a9d9-b9b2a0cfda81.jpg
         * zk_final_price : 12.12
         * real_price : 12.12
         */

        private int vod_type;
        private int live_id;
        private int user_id;
        private String user_nickname;
        private String video_url;
        private String avatar;
        private int sex;
        private String title;
        private String cover_img;
        private int live_mode;
        private int type;
        private int in_coin;
        private int start_time;
        private String city_name;
        private int viewer;
        private String total_coin_num;
        private int watch_num;
        private String item_title;
        private String item_url;
        private String zk_final_price;
        private String real_price;

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public int getVod_type() {
            return vod_type;
        }

        public void setVod_type(int vod_type) {
            this.vod_type = vod_type;
        }

        public int getLive_id() {
            return live_id;
        }

        public void setLive_id(int live_id) {
            this.live_id = live_id;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover_img() {
            return cover_img;
        }

        public void setCover_img(String cover_img) {
            this.cover_img = cover_img;
        }

        public int getLive_mode() {
            return live_mode;
        }

        public void setLive_mode(int live_mode) {
            this.live_mode = live_mode;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getIn_coin() {
            return in_coin;
        }

        public void setIn_coin(int in_coin) {
            this.in_coin = in_coin;
        }

        public int getStart_time() {
            return start_time;
        }

        public void setStart_time(int start_time) {
            this.start_time = start_time;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public int getViewer() {
            return viewer;
        }

        public void setViewer(int viewer) {
            this.viewer = viewer;
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

        public String getItem_title() {
            return item_title;
        }

        public void setItem_title(String item_title) {
            this.item_title = item_title;
        }

        public String getItem_url() {
            return item_url;
        }

        public void setItem_url(String item_url) {
            this.item_url = item_url;
        }

        public String getZk_final_price() {
            return zk_final_price;
        }

        public void setZk_final_price(String zk_final_price) {
            this.zk_final_price = zk_final_price;
        }

        public String getReal_price() {
            return real_price;
        }

        public void setReal_price(String real_price) {
            this.real_price = real_price;
        }
    }
}
