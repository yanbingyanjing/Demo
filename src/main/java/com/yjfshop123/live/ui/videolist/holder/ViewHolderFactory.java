package com.yjfshop123.live.ui.videolist.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.videolist.model.BaseItem;

public class ViewHolderFactory {

    public static BaseViewHolder<? extends BaseItem> buildViewHolder(ViewGroup parent, int viewType, int screenWidth) {
        switch (viewType) {
            case BaseItem.VIEW_TYPE_VIDEO:
                return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_video, parent, false), screenWidth);

            case BaseItem.VIEW_TYPE_PICTURE:
                return new PicViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_pic, parent, false), screenWidth);

            default:
            case BaseItem.VIEW_TYPE_BOTTOM:
                return new BottomViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_bottom, parent, false));
        }
    }

    //关注新界面
    public static BaseViewHolder<? extends BaseItem> buildViewHolderGuanzhu(ViewGroup parent, int viewType, int screenWidth) {
        switch (viewType) {
            case BaseItem.VIEW_TYPE_VIDEO:
                return new VideoViewHolderGuanzhu(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_video_guanzhu, parent, false), screenWidth);

            case BaseItem.VIEW_TYPE_PICTURE:
                return new PicViewHolderGuanzhu(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_pic_guanzhu, parent, false), screenWidth);

            default:
            case BaseItem.VIEW_TYPE_BOTTOM:
                return new BottomViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_bottom, parent, false));
        }
    }

    //春花春草
    public static BaseViewHolder<? extends BaseItem> buildViewHolderNew(ViewGroup parent, int viewType, Context context) {
        switch (viewType) {
            case BaseItem.VIEW_TYPE_VIDEO:
                return new VideoViewHolderNew(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_pic_new, parent, false), context);

            case BaseItem.VIEW_TYPE_PICTURE:
                return new PicViewHolderNew(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_pic_new, parent, false), context);

            default:
            case BaseItem.VIEW_TYPE_BOTTOM:
                return new BottomViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_bottom, parent, false));
        }
    }

}
