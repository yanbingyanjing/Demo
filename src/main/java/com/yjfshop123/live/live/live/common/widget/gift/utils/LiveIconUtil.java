package com.yjfshop123.live.live.live.common.widget.gift.utils;


import android.util.SparseIntArray;


import com.yjfshop123.live.R;

import java.util.Arrays;
import java.util.List;


public class LiveIconUtil {

    private static SparseIntArray sLiveGiftCountMap;//送礼物数字
    private static List<Integer> sLinkMicPkAnim;//连麦pk帧动画
    private static List<Integer> sLevel;//等级icon

    static {
        sLiveGiftCountMap = new SparseIntArray();
        sLiveGiftCountMap.put(0, R.mipmap.icon_live_gift_count_0);
        sLiveGiftCountMap.put(1, R.mipmap.icon_live_gift_count_1);
        sLiveGiftCountMap.put(2, R.mipmap.icon_live_gift_count_2);
        sLiveGiftCountMap.put(3, R.mipmap.icon_live_gift_count_3);
        sLiveGiftCountMap.put(4, R.mipmap.icon_live_gift_count_4);
        sLiveGiftCountMap.put(5, R.mipmap.icon_live_gift_count_5);
        sLiveGiftCountMap.put(6, R.mipmap.icon_live_gift_count_6);
        sLiveGiftCountMap.put(7, R.mipmap.icon_live_gift_count_7);
        sLiveGiftCountMap.put(8, R.mipmap.icon_live_gift_count_8);
        sLiveGiftCountMap.put(9, R.mipmap.icon_live_gift_count_9);

        sLinkMicPkAnim = Arrays.asList(
                R.mipmap.pk01,
                R.mipmap.pk02,
                R.mipmap.pk03,
                R.mipmap.pk04,
                R.mipmap.pk05,
                R.mipmap.pk06,
                R.mipmap.pk07,
                R.mipmap.pk08,
                R.mipmap.pk09,
                R.mipmap.pk10,
                R.mipmap.pk11,
                R.mipmap.pk12,
                R.mipmap.pk13,
                R.mipmap.pk14,
                R.mipmap.pk15,
                R.mipmap.pk16,
                R.mipmap.pk17,
                R.mipmap.pk18,
                R.mipmap.pk19,
                R.mipmap.pk20,
                R.mipmap.pk21,
                R.mipmap.pk22,
                R.mipmap.pk23,
                R.mipmap.pk24,
                R.mipmap.pk25,
                R.mipmap.pk26,
                R.mipmap.pk27,
                R.mipmap.pk28,
                R.mipmap.pk29,
                R.mipmap.pk30,
                R.mipmap.pk31,
                R.mipmap.pk32,
                R.mipmap.pk33,
                R.mipmap.pk34,
                R.mipmap.pk35,
                R.mipmap.pk36,
                R.mipmap.pk37,
                R.mipmap.pk38,
                R.mipmap.pk39,
                R.mipmap.pk40,
                R.mipmap.pk41,
                R.mipmap.pk42,
                R.mipmap.pk43,
                R.mipmap.pk44,
                R.mipmap.pk45,
                R.mipmap.pk46,
                R.mipmap.pk47,
                R.mipmap.pk48,
                R.mipmap.pk49,
                R.mipmap.icon_live_vs
        );

        sLevel = Arrays.asList(
                R.mipmap.icon_live_chat_level_0,
                R.mipmap.icon_live_chat_level_1,
                R.mipmap.icon_live_chat_level_2,
                R.mipmap.icon_live_chat_level_3,
                R.mipmap.icon_live_chat_level_4,
                R.mipmap.icon_live_chat_level_5,
                R.mipmap.icon_live_chat_level_6,
                R.mipmap.icon_live_chat_level_7,
                R.mipmap.icon_live_chat_level_8,
                R.mipmap.icon_live_chat_level_9,
                R.mipmap.icon_live_chat_level_10,
                R.mipmap.icon_live_chat_level_11,
                R.mipmap.icon_live_chat_level_12,
                R.mipmap.icon_live_chat_level_13,
                R.mipmap.icon_live_chat_level_14,
                R.mipmap.icon_live_chat_level_15,
                R.mipmap.icon_live_chat_level_16,
                R.mipmap.icon_live_chat_level_17,
                R.mipmap.icon_live_chat_level_18,
                R.mipmap.icon_live_chat_level_19,
                R.mipmap.icon_live_chat_level_20,
                R.mipmap.icon_live_chat_level_21,
                R.mipmap.icon_live_chat_level_22,
                R.mipmap.icon_live_chat_level_23,
                R.mipmap.icon_live_chat_level_24,
                R.mipmap.icon_live_chat_level_25,
                R.mipmap.icon_live_chat_level_26,
                R.mipmap.icon_live_chat_level_27,
                R.mipmap.icon_live_chat_level_28,
                R.mipmap.icon_live_chat_level_29,
                R.mipmap.icon_live_chat_level_30,
                R.mipmap.icon_live_chat_level_31,
                R.mipmap.icon_live_chat_level_32,
                R.mipmap.icon_live_chat_level_33,
                R.mipmap.icon_live_chat_level_34,
                R.mipmap.icon_live_chat_level_35,
                R.mipmap.icon_live_chat_level_36,
                R.mipmap.icon_live_chat_level_37,
                R.mipmap.icon_live_chat_level_38,
                R.mipmap.icon_live_chat_level_39,
                R.mipmap.icon_live_chat_level_40,
                R.mipmap.icon_live_chat_level_41,
                R.mipmap.icon_live_chat_level_42,
                R.mipmap.icon_live_chat_level_43,
                R.mipmap.icon_live_chat_level_44,
                R.mipmap.icon_live_chat_level_45,
                R.mipmap.icon_live_chat_level_46,
                R.mipmap.icon_live_chat_level_47,
                R.mipmap.icon_live_chat_level_48,
                R.mipmap.icon_live_chat_level_49,
                R.mipmap.icon_live_chat_level_50,
                R.mipmap.icon_live_chat_level_51,
                R.mipmap.icon_live_chat_level_52,
                R.mipmap.icon_live_chat_level_53,
                R.mipmap.icon_live_chat_level_54,
                R.mipmap.icon_live_chat_level_55,
                R.mipmap.icon_live_chat_level_56,
                R.mipmap.icon_live_chat_level_57,
                R.mipmap.icon_live_chat_level_58,
                R.mipmap.icon_live_chat_level_59,
                R.mipmap.icon_live_chat_level_60,
                R.mipmap.icon_live_chat_level_61,
                R.mipmap.icon_live_chat_level_62,
                R.mipmap.icon_live_chat_level_63,
                R.mipmap.icon_live_chat_level_64,
                R.mipmap.icon_live_chat_level_65,
                R.mipmap.icon_live_chat_level_66,
                R.mipmap.icon_live_chat_level_67,
                R.mipmap.icon_live_chat_level_68,
                R.mipmap.icon_live_chat_level_69,
                R.mipmap.icon_live_chat_level_70,
                R.mipmap.icon_live_chat_level_71,
                R.mipmap.icon_live_chat_level_72,
                R.mipmap.icon_live_chat_level_73,
                R.mipmap.icon_live_chat_level_74,
                R.mipmap.icon_live_chat_level_75,
                R.mipmap.icon_live_chat_level_76,
                R.mipmap.icon_live_chat_level_77,
                R.mipmap.icon_live_chat_level_78,
                R.mipmap.icon_live_chat_level_79,
                R.mipmap.icon_live_chat_level_80,
                R.mipmap.icon_live_chat_level_81,
                R.mipmap.icon_live_chat_level_82,
                R.mipmap.icon_live_chat_level_83,
                R.mipmap.icon_live_chat_level_84,
                R.mipmap.icon_live_chat_level_85,
                R.mipmap.icon_live_chat_level_86,
                R.mipmap.icon_live_chat_level_87,
                R.mipmap.icon_live_chat_level_88,
                R.mipmap.icon_live_chat_level_89,
                R.mipmap.icon_live_chat_level_90,
                R.mipmap.icon_live_chat_level_91,
                R.mipmap.icon_live_chat_level_92,
                R.mipmap.icon_live_chat_level_93,
                R.mipmap.icon_live_chat_level_94,
                R.mipmap.icon_live_chat_level_95,
                R.mipmap.icon_live_chat_level_96,
                R.mipmap.icon_live_chat_level_97,
                R.mipmap.icon_live_chat_level_98,
                R.mipmap.icon_live_chat_level_99,
                R.mipmap.icon_live_chat_level_100
        );
    }

    public static int getGiftCountIcon(int key) {
        return sLiveGiftCountMap.get(key);
    }

    public static List<Integer> getLinkMicPkAnim() {
        return sLinkMicPkAnim;
    }

    public static List<Integer> getLevel() {
        return sLevel;
    }

}

