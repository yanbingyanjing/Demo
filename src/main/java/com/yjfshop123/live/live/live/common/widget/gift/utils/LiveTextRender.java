package com.yjfshop123.live.live.live.common.widget.gift.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.view.VerticalImageSpan;
import com.yjfshop123.live.utils.CommonUtils;

public class LiveTextRender {

    //SPAN_EXCLUSIVE_EXCLUSIVE//在Span前后输入的字符都不应用Span效果
    //SPAN_EXCLUSIVE_INCLUSIVE //在Span前面输入的字符不应用Span效果，后面输入的字符应用Span效果
    //SPAN_INCLUSIVE_EXCLUSIVE//在Span前面输入的字符应用Span效果，后面输入的字符不应用Span效果
    //SPAN_INCLUSIVE_INCLUSIVE//在Span前后输入的字符都应用Span效果

    private static StyleSpan sItalicSpan;//斜体

    private static StyleSpan sBoldItalicSpan;//斜粗体
    private static ForegroundColorSpan sGreenColorSpan;//绿色
    private static AbsoluteSizeSpan sFontSizeSpan_15;//字体

    private static StyleSpan sBoldSpan;//粗体
    private static ForegroundColorSpan sWhiteColorSpan;//白色
    private static AbsoluteSizeSpan sFontSizeSpan_16;//字体

    private static ForegroundColorSpan sGlobalColorSpan;//黄色

    static {
        sItalicSpan = new StyleSpan(Typeface.ITALIC);

        sBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
        sGreenColorSpan = new ForegroundColorSpan(0xFD03F975);
        sFontSizeSpan_15 = new AbsoluteSizeSpan(15, true);

        sBoldSpan = new StyleSpan(Typeface.BOLD);
        sWhiteColorSpan = new ForegroundColorSpan(0xffffffff);
        sFontSizeSpan_16 = new AbsoluteSizeSpan(16, true);

        sGlobalColorSpan = new ForegroundColorSpan(0xffffdd00);
    }



    //...
    private static SpannableStringBuilder createPrefix2(Context context, boolean isVip, boolean isGuard, String user_level, String manager) {
        /*SpannableStringBuilder builder = new SpannableStringBuilder();
        int index = 0;
        if (user_level != null){
            int start = 0;
            String userLevel = user_level + " ";
            builder.append(userLevel);
            int end = start + userLevel.length();
            builder.setSpan(sGreenColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(sFontSizeSpan_15, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(sBoldItalicSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index += end;
        }
        if (isVip) {//vip图标
            Drawable vipDrawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_vip);
            if (vipDrawable != null) {
                builder.append("  ");
                vipDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 28), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (isGuard) {//守护图标
            Drawable drawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_guard_2);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, CommonUtils.dip2px(context, 14), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;*/

        SpannableStringBuilder builder = new SpannableStringBuilder();
        int index = 0;
        if (user_level != null){
            Drawable levelDrawable;
            int level = Integer.parseInt(user_level);
            if (level > 100 ){
                levelDrawable = ContextCompat.getDrawable(App.getInstance(), LiveIconUtil.getLevel().get(100));
            }else {
                levelDrawable = ContextCompat.getDrawable(App.getInstance(), LiveIconUtil.getLevel().get(level));
            }
            if (levelDrawable != null) {
                builder.append("  ");
                levelDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 35), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(levelDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }

//            String userLevel = user_level + "  ";
//            builder.append(userLevel);
//            int end = index + userLevel.length();
//            builder.setSpan(sGreenColorSpan, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(sFontSizeSpan_15, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(sItalicSpan, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            index = end;
        }
        if (manager.equals("2")){//管理员
            Drawable vipDrawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_manager);
            if (vipDrawable != null) {
                builder.append("  ");
                vipDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 14), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (isVip) {//vip图标
            Drawable vipDrawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_vip);
            if (vipDrawable != null) {
                builder.append("  ");
                vipDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 28), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (isGuard) {//守护图标
            Drawable drawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_guard_2);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, CommonUtils.dip2px(context, 14), CommonUtils.dip2px(context, 14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    //守护 VIP 进入样式
    public static void renderEnterRoom(TextView textView, Context context, boolean isVip, boolean isGuard, String userName, String user_level, String manager) {
        /*if (textView != null) {
            SpannableStringBuilder builder = createPrefix2(context, isVip, isGuard, user_level);
            int start = builder.length();
            String name = userName + " ";
            builder.append(name);
            int end = start + name.length();
            builder.setSpan(sWhiteColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(sFontSizeSpan_16, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(sBoldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("进入了直播间");
            textView.setText(builder);
        }*/

        if (textView != null) {
            SpannableStringBuilder builder = createPrefix2(context, isVip, isGuard, user_level, manager);
            int start = builder.length();
            String name = userName + "：";
            builder.append(name);
            int end = start + name.length();
            builder.setSpan(new ForegroundColorSpan(calcNameColor(userName)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(sFontSizeSpan_15, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("进入了直播间");
            textView.setText(builder);
        }
    }
    //..



    //..
    private static SpannableStringBuilder createPrefix(Context context, String isVip, String isGuard, String user_level, String manager) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int index = 0;
        if (user_level != null){
            Drawable levelDrawable;
            int level = Integer.parseInt(user_level);
            if (level > 9 ){
                levelDrawable = ContextCompat.getDrawable(App.getInstance(), LiveIconUtil.getLevel().get(9));
            }else {
                levelDrawable = ContextCompat.getDrawable(App.getInstance(), LiveIconUtil.getLevel().get(level));
            }
            if (levelDrawable != null) {
                builder.append("  ");
                levelDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 15), CommonUtils.dip2px(context, 15));
                builder.setSpan(new VerticalImageSpan(levelDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }

//            String userLevel = user_level + "  ";
//            builder.append(userLevel);
//            int end = index + userLevel.length();
//            builder.setSpan(sGreenColorSpan, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(sItalicSpan, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            index = end;
        }
        if (manager.equals("2")){//管理员
            Drawable vipDrawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_manager);
            if (vipDrawable != null) {
                builder.append("  ");
                vipDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 12), CommonUtils.dip2px(context, 12));
                builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (isVip.equals("1")) {//vip图标
            Drawable vipDrawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_vip);
            if (vipDrawable != null) {
                builder.append("  ");
                vipDrawable.setBounds(0, 0, CommonUtils.dip2px(context, 24), CommonUtils.dip2px(context, 12));
                builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (isGuard.equals("1")) {//守护图标
            Drawable drawable = ContextCompat.getDrawable(App.getInstance(), R.mipmap.icon_live_chat_guard_2);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, CommonUtils.dip2px(context, 12), CommonUtils.dip2px(context, 12));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    //普通消息
    public static void renderOrdinaryMsg(TextView textView, Context context, String isVip, String isGuard, String userName, String user_level, String content, String manager) {
        if (textView != null) {
            SpannableStringBuilder builder = createPrefix(context, isVip, isGuard, user_level, manager);
            int start = builder.length();
            String name = userName + "：";
            builder.append(name);
            int end = start + name.length();
            builder.setSpan(new ForegroundColorSpan(calcNameColor(userName)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(content);
            textView.setText(builder);
        }
    }

    //通知消息样式
    public static void renderSystemMsg(TextView textView, Context context, String userName, String content) {
        if (textView != null) {
            SpannableString spanString = new SpannableString(content);
            spanString.setSpan(sBoldItalicSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setTextColor(context.getResources().getColor(R.color.color_style));
            textView.setText(spanString);

            /*SpannableString spanString = new SpannableString(userName + "  " + content);
            spanString.setSpan(sBoldItalicSpan, 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setTextColor(context.getResources().getColor(R.color.color_style_net));
            textView.setText(spanString);*/
        }
    }

    //主播消息样式
    public static void renderHostMsg(TextView textView, Context context, String userName, String content) {
        if (textView != null) {
            SpannableString spanString = new SpannableString(userName + "  " + content);
            spanString.setSpan(sBoldSpan, 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setTextColor(context.getResources().getColor(R.color.edit_red));
            textView.setText(spanString);
        }
    }
    //..



    public static SpannableStringBuilder renderGiftCount(Context context, int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s = String.valueOf(count);
        builder.append(s);
        for (int i = 0, length = s.length(); i < length; i++) {
            String c = String.valueOf(s.charAt(i));
            if (StringUtil.isInt(c)) {
                int icon = LiveIconUtil.getGiftCountIcon(Integer.parseInt(c));
                Drawable drawable = ContextCompat.getDrawable(App.getInstance(), icon);
                if (drawable != null) {
                    drawable.setBounds(0, 0, CommonUtils.dip2px(context, 24), CommonUtils.dip2px(context, 32));
                    builder.setSpan(new ImageSpan(drawable), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return builder;
    }

    public static SpannableStringBuilder renderGiftInfo2(Context context, String giftName) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = context.getString(R.string.live_send_gift_1);
        String content = s1 + " " + giftName;
        int index1 = s1.length();
        builder.append(content);
        builder.setSpan(sGlobalColorSpan, index1, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static int calcNameColor(String strName) {
        if (strName == null) return 0;
        byte idx = 0;
        byte[] byteArr = strName.getBytes();
        for (int i = 0; i < byteArr.length; i++) {
            idx ^= byteArr[i];
        }
        return 0xFFFBD28D;
//        switch (idx & 0x7) {
//            case 1:
//                return 0xFFF12B5B;
//            case 2:
//                return 0xFF2B7DE2;
//            case 3:
//                return 0xFF006599;
//            case 4:
//                return 0xFFFF7906;
//            case 5:
//                return 0xFF1FBCB6;
//            case 6:
//                return 0xFFFFAE1F;
//            case 7:
//                return 0xFFFF437D;
//            case 0:
//            default:
//                return 0xFF9966cc;
//        }
    }

}

