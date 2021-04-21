package com.yjfshop123.live.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangqi on 2018/8/2.
 */
public class MyUtils {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static int dp2px(float dpValue) {
        if (mContext == null)
            throw new NullPointerException("mContext为null，请在init方法中进行赋值");
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static float sp2px(float spValue) {
        if (mContext == null)
            throw new NullPointerException("mContext为null，请在init方法中进行赋值");
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 性别转换  man -->男  woman-->女
     *
     * @param sex
     */
    public static String setSex(String sex) {
        String s = "不明";
        if (TextUtils.isEmpty(sex))
            return s;
        if ("man".equals(sex))
            return "男";
        if ("woman".equals(sex))
            return "女";
        return s;
    }

    /**
     * 设置圆角图片
     *
     * @param url
     * @param iv
     */
    public static void setCircleImageFormat(String url, final ImageView iv) {
        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.img_loading)
//                .error(R.drawable.img_error)
                //设置图片加载的优先级
                .priority(Priority.HIGH)
                //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
                .skipMemoryCache(false)
                //缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //圆形
                .circleCrop();
        Glide.with(mContext).load(url)
                //动画效果
//                .transition(withCrossFade())
//                .thumbnail(0.2f)
                .apply(options)
                .into(iv);
    }

    /**
     * 设置模糊背景图片
     *
     * @param iv
     * @param url
     */
    public static void setVagueImageFormat(String url, final ImageView iv) {
        Glide.with(mContext).asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        iv.setImageBitmap(getVagueBitmap(resource, 10));
                    }
                });

    }

    /**
     * 将btimap图片设置为模糊状态
     *
     * @param bitmap
     * @param radius 设置一个模糊的半径,其值为 0－25
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Bitmap getVagueBitmap(Bitmap bitmap, int radius) {
        RenderScript renderScript = RenderScript.create(mContext);

        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(input);
        blur.setRadius(radius);
        blur.forEach(output);

        output.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    /**
     * @param activity
     * @param bgAlpha  屏幕透明度0.0-1.0
     *                 1表示完全不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 格式化小数位
     *
     * @param d
     * @param point
     * @return
     */
    public static String formatDouble(Double d, int point) {
        if (point < 0) {
            throw new IllegalStateException("小数点位数不合法:" + point);
        } else {
            String format = "0";
            if (point > 0) {
                format = format + ".";
            }

            for (int i = 0; i < point; ++i) {
                format = format + "0";
            }

            DecimalFormat df = new DecimalFormat(format);
            return df.format(d);
        }
    }

    public static int getTextWidth(String text, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }
    public static int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static String formatTime(long time, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(long2Date(time));
    }

    public static Date long2Date(long time) {
        Date date = new Date();
        date.setTime(time);
        return date;
    }

    public static int getWeekOfDate(Date dt) {
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }


}
