package com.yjfshop123.live.live.response;

public class LianmaiResponse {


    /**
     * msg : OK
     * code : 1
     * data : {"t_live_url":"rtmp://t.yunn.com.cn/live/live_203145?txSecret=81a8e6f948a075022daa9d7033da3385&txTime=5d08c51e&user_id=203145&option_type=2&option_id=99&option_class_id=22"}
     */

    private String msg;
    private int code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * t_live_url : rtmp://t.yunn.com.cn/live/live_203145?txSecret=81a8e6f948a075022daa9d7033da3385&txTime=5d08c51e&user_id=203145&option_type=2&option_id=99&option_class_id=22
         */

        private String t_live_url;

        public String getT_live_url() {
            return t_live_url;
        }

        public void setT_live_url(String t_live_url) {
            this.t_live_url = t_live_url;
        }
    }
}
