package com.yjfshop123.live.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.umeng.commonsdk.debug.I;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;

import java.lang.reflect.Field;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.media.MediaCodecList.REGULAR_CODECS;

/**
 * Description:工具类
 *
 * @author 杜乾-Dusan,Created on 2018/2/9 - 16:11.
 * E-mail:duqian2010@gmail.com
 */
public class SystemUtils {

    private static int screenHeight = 0;
    private static int screenWidth = 0;
    private static int statusBarHeight = 0;

    private SystemUtils() {
    }
    public static  void hideKeyBord(Activity context) {
        View view =context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setClipboard(Context context, String s) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("text", s);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        NToast.shortToast(context, "已复制到剪贴板");
    }


    public static void setClipboardForKouling(Context context, String s) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        Intent intent=new Intent();
        intent.putExtra("type","from_dgj");
        ClipData.Item item=new ClipData.Item(s,intent,null);
        ClipData mClipData = new ClipData("text",new String[] {
                ClipDescription.MIMETYPE_TEXT_PLAIN },item);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        NToast.shortToast(context, "已复制到剪贴板");
    }
    public static void setClipboardNoToast(Context context, String s) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("text", s);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        //NToast.shortToast(context, "已复制到剪贴板");
    }
    /**
     * 设置textView结尾...后面显示的文字和颜色
     *
     * @param context    上下文
     * @param textView   textview
     * @param originText 原文本
     * @param endText    结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand   当前是否是展开状态
     */
    public static void toggleEllipsize(final Context context,
                                       final TextView textView,
                                       final int count,
                                       final String originText,
                                       final String endText,
                                       final int endColorID,
                                       final boolean isExpand) {
        if (TextUtils.isEmpty(originText)) {
            return;
        }

        if (isExpand) {
            textView.setText(originText);
        } else {
            if (originText.length()>count) {
                CharSequence temp = originText.substring(0,count)+"..." + endText;
                SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
                ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor
                                (endColorID)),
                        temp.length() - endText.length(), temp.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(ssb.toString()));
            } else {
                textView.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(originText));
            }
        }

    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context mContext) {
        if (screenWidth > 0) {
            return screenWidth;
        }
        if (mContext == null) {
            return 0;
        }
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度,是否包含导航栏高度
     */
    public static int getScreenHeight(Context mContext, boolean isIncludeNav) {
        if (mContext == null) {
            return 0;
        }
        NLog.d("虚拟高度有",getNavigationBarHeight(mContext));
        int screenHeight = getScreenHeight(mContext);
        if (isIncludeNav) {
            return screenHeight;
        } else {

            return screenHeight - getNavigationBarHeight(mContext);
        }
    }

    /**
     * 获取屏幕高(包括底部虚拟按键)
     *
     * @param mContext
     * @return
     */
    public static int getScreenHeight(Context mContext) {
        if (screenHeight > 0) {
            return screenHeight;
        }

        if (mContext == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getWindowManager(mContext).getDefaultDisplay();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(displayMetrics);
            } else {
                display.getMetrics(displayMetrics);
            }
            screenHeight = displayMetrics.heightPixels;
        } catch (Exception e) {
            screenHeight = display.getHeight();
        }
        return screenHeight;
    }


    /**
     * 获取WindowManager。
     */
    public static WindowManager getWindowManager(Context mContext) {
        if (mContext == null) {
            return null;
        }
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 获取NavigationBar的高度
     */
    public static int getNavigationBarHeight(Context mContext) {
        if (!hasNavigationBar(mContext)) {
            return 0;
        }
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 是否存在NavigationBar
     */
    public static boolean hasNavigationBar(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager(mContext).getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.x != size.x || realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(mContext).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    /**
     * dp转成px
     *
     * @param mContext
     * @param dipValue
     * @return
     */
    public static int dip2px(Context mContext, float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float sp2px(Context mContext, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, mContext.getResources().getDisplayMetrics());
    }

    /**
     * px转成dp
     *
     * @param mContext
     * @param pxValue
     * @return
     */
    public static int px2dip(Context mContext, float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getStatusBarHeightByReflect(Context mContext) {
        if (statusBarHeight > 0) {
            return statusBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int sbHeightId = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mContext.getResources().getDimensionPixelSize(sbHeightId);
        } catch (Exception e1) {
            e1.printStackTrace();
            statusBarHeight = 0;
        }
        return statusBarHeight;
    }


    public static int getStatusBarHeight(Context mContext) {
        int statusBarHeight = getStatusBarHeightByReflect(mContext);
        if (statusBarHeight == 0) {
            statusBarHeight = SystemUtils.dip2px(mContext, 30);
        }
        return statusBarHeight;
    }
    /**
     * 是否支持 hevc 硬解
     * @return
     */
    public static boolean isH265HWDecoderSupport() {
        MediaCodecList codecList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            codecList = new MediaCodecList(REGULAR_CODECS);
        }

        // 获取设备支持的所有 codec 信息
        MediaCodecInfo[] codecInfos = new MediaCodecInfo[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            codecInfos = codecList.getCodecInfos();
        }
        for (int i = 0; i < codecInfos.length; i++) {
            MediaCodecInfo codecInfo = codecInfos[i];

            // 解码codec & 解码器名称包含'hevc' & 不是软件codec
            if (!codecInfo.isEncoder() && (codecInfo.getName().contains("hevc")
                    && !isSWCodec(codecInfo.getName()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否为软件 codec
     * @param codecName
     * @return
     */
    public static boolean isSWCodec(String codecName) {
        if (codecName.startsWith("OMX.google.")) {
            return true;
        }

        if (codecName.startsWith("OMX.")) {
            return false;
        }

        return true;
    }
}
