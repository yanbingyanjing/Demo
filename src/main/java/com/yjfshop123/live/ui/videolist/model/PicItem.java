package com.yjfshop123.live.ui.videolist.model;

import com.yjfshop123.live.net.response.PopularDynamicResponse;

public class PicItem extends BaseItem {

    private PopularDynamicResponse.ListBean mListBean;

    public PicItem(PopularDynamicResponse.ListBean listBean) {
        super(BaseItem.VIEW_TYPE_PICTURE);
        mListBean = listBean;
    }

    public PopularDynamicResponse.ListBean getListBean() {
        return mListBean;
    }
}
