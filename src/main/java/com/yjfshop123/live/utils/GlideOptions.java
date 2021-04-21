package com.yjfshop123.live.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.yjfshop123.live.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;

/**
 *
 * 日期:2018/12/14
 * 描述:
 **/
public class GlideOptions {

    public static void glideUrl(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loadding)// 正在加载中的图片  
                .error(R.drawable.loadding)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  
        Glide.with(context).load(url).into(view);
    }

    /**
     * 我的页面占位图
     *
     * @return
     */
    public static RequestOptions myselfGlideptions() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.my_image_unknown_head)// 正在加载中的图片  
                .error(R.drawable.my_image_unknown_head)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  
        return options;
    }

    /**
     * 完善个人资料占位图
     *
     * @return
     */
    public static RequestOptions wangShanGlideptions() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.autor_img)// 正在加载中的图片  
                .error(R.drawable.autor_img)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  
        return options;
    }

    /**
     * 加载中占位图
     *
     * @return
     */
    public static RequestOptions glideptions() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loadding)// 正在加载中的图片  
                .error(R.drawable.loadding)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  
        return options;
    }

}
