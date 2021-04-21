package com.bumptech.xchat;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.bumptech.xchat.load.engine.DiskCacheStrategy;
import com.bumptech.xchat.load.resource.drawable.GlideDrawable;
import com.bumptech.xchat.request.animation.GlideAnimation;
import com.bumptech.xchat.request.target.SimpleTarget;

import java.io.File;


public class ImgLoader {
    private static RequestManager sManager;
//    private static BlurTransformation sBlurTransformation;

    static {
        sManager = Glide.with(App.getInstance());
//        sBlurTransformation = new BlurTransformation(CommonAppContext.sInstance, 10);
    }

    public static void display(String url, ImageView imageView) {
        sManager.load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void displayWithError(String url, ImageView imageView, int errorRes) {
        sManager.load(url).error(errorRes).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void displayAvatar(String url, ImageView imageView) {
        displayWithError(url, imageView, R.drawable.my_image_unknown_head);
    }

    public static void display(File file, ImageView imageView) {
        sManager.load(file).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void display(int res, ImageView imageView) {
        sManager.load(res).into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(String videoPath, ImageView imageView) {
        sManager.load(Uri.fromFile(new File(videoPath))).into(imageView);
    }

    public static void displayDrawable(String url, final DrawableCallback callback) {
        sManager.load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }
        });
    }

    public static void displayDrawable(File file, final DrawableCallback callback) {
        sManager.load(file).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }
        });
    }


    public static void display(String url, ImageView imageView, int placeholderRes) {
        sManager.load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(placeholderRes).into(imageView);
    }

    /**
     * 显示模糊的毛玻璃图片
     */
//    public static void displayBlur(String url, ImageView imageView) {
//        sManager.load(url)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .bitmapTransform(sBlurTransformation)
//                .into(imageView);
//    }


    public interface DrawableCallback {
        void onLoadSuccess(Drawable drawable);

        void onLoadFailed();
    }


}

