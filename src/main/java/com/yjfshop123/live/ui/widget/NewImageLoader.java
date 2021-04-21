package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.widget.ImageView;

import com.yjfshop123.live.server.widget.RoundImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.youth.banner.loader.ImageLoaderInterface;

public abstract class NewImageLoader implements ImageLoaderInterface<SelectableRoundedImageView> {

    @Override
    public SelectableRoundedImageView createImageView(Context context) {
        SelectableRoundedImageView imageView = new SelectableRoundedImageView(context);
        imageView.setCornerRadiiDP(CommonUtils.dip2px(context,2),CommonUtils.dip2px(context,2),CommonUtils.dip2px(context,2),CommonUtils.dip2px(context,2));
        return imageView;
    }


}
