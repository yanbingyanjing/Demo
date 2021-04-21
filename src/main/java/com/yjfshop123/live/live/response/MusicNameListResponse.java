package com.yjfshop123.live.live.response;

import java.util.List;

public class MusicNameListResponse {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 1
         * music_name : 红色高跟鞋
         * singer : 蔡健雅
         */

        private String id;
        private String music_name;
        private String singer;
        private String is_pay;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMusic_name() {
            return music_name;
        }

        public void setMusic_name(String music_name) {
            this.music_name = music_name;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getIs_pay() {
            return is_pay;
        }

        public void setIs_pay(String is_pay) {
            this.is_pay = is_pay;
        }
    }
}
