package com.yjfshop123.live.live.response;

import java.util.List;

public class ChannelListResponse {
    private List<ChannelListBean> channel_list;

    public List<ChannelListBean> getChannel_list() {
        return channel_list;
    }

    public void setChannel_list(List<ChannelListBean> channel_list) {
        this.channel_list = channel_list;
    }

    public static class ChannelListBean {
        /**
         * id : 1
         * name : 野外
         * description : 野外运动
         * icon : 野外icon
         */

        private int id;
        private String name;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
