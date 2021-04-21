package com.yjfshop123.live.live.response;

import java.util.List;

public class WatchTypeListsResponse {
    /**
     * watch_expire_time : 未开通
     * rest_coin : 0
     * list : [{"day_time":7,"coin":300,"subject":"7天体验"},{"day_time":30,"coin":1000,"subject":"1个月"},{"day_time":365,"coin":12000,"subject":"尊贵守护全年"}]
     */

    private String watch_expire_time;
    private int rest_coin;
    private List<ListBean> list;

    public String getWatch_expire_time() {
        return watch_expire_time;
    }

    public void setWatch_expire_time(String watch_expire_time) {
        this.watch_expire_time = watch_expire_time;
    }

    public int getRest_coin() {
        return rest_coin;
    }

    public void setRest_coin(int rest_coin) {
        this.rest_coin = rest_coin;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * day_time : 7
         * coin : 300
         * subject : 7天体验
         */

        private int day_time;
        private int coin;
        private String subject;

        public int getDay_time() {
            return day_time;
        }

        public void setDay_time(int day_time) {
            this.day_time = day_time;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}
