package com.yjfshop123.live.live.response;

import java.util.List;

public class GameListResponse {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * game_id : 1
         * category_id : 1
         * cn_name : 三国杀
         * icon : 野外icon
         */

        private int game_id;
        private int category_id;
        private String cn_name;
        private String icon;

        public int getGame_id() {
            return game_id;
        }

        public void setGame_id(int game_id) {
            this.game_id = game_id;
        }

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getCn_name() {
            return cn_name;
        }

        public void setCn_name(String cn_name) {
            this.cn_name = cn_name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
