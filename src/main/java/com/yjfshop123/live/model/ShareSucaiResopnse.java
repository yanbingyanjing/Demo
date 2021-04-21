package com.yjfshop123.live.model;

import java.util.List;

public class ShareSucaiResopnse {
    public Sucai[] list;
    public static class Sucai{
        public String title;
        public String share_url;
        public String invite_code;
        public boolean isSelect=false;
        public boolean isShowShare=true;
        public String content;
        public String create_time;
        public String[] pic_list;
    }
}
