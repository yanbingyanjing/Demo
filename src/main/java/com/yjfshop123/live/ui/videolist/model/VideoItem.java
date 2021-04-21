package com.yjfshop123.live.ui.videolist.model;


import com.yjfshop123.live.net.response.PopularDynamicResponse;

public class VideoItem extends BaseItem {

    private PopularDynamicResponse.ListBean mListBean;

    public VideoItem(PopularDynamicResponse.ListBean listBean) {
        super(BaseItem.VIEW_TYPE_VIDEO);
        mListBean = listBean;
    }

    public PopularDynamicResponse.ListBean getListBean() {
        return mListBean;
    }
}
