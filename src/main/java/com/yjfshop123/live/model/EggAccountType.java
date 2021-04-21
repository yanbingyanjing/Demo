package com.yjfshop123.live.model;

import android.content.Context;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;

public class EggAccountType {
    public static String[] goldEggAccountInData = {App.getContext().getString(R.string.all_),
            App.getContext().getString(R.string.addition),
            App.getContext().getString(R.string.medaling),

            App.getContext().getString(R.string.choudan_duihuan),

            App.getContext().getString(R.string.reward),
            App.getContext().getString(R.string.target),
            App.getContext().getString(R.string.boost),
            App.getContext().getString(R.string.income_coin),
            App.getContext().getString(R.string.bonus),
            App.getContext().getString(R.string.qita)
    };
    public static int[] goldEggAccountInDataType = {0,
            4,
            3,
            16,
            7,
            10,
            11,
            20,
            2,
             22};
    public static String[] goldEggAccountOutData = {App.getContext().getString(R.string.all_),
            App.getContext().getString(R.string.exchange_medals),
            App.getContext().getString(R.string.send_reward),
            App.getContext().getString(R.string.turnout_coin)
           };

    public static int[] goldEggAccountOutDataType = {0,
            1,
            6,
            21
    };
    public static String[] silverEggAccountInData = {App.getContext().getString(R.string.all_),
            "城市代理银蛋",
            App.getContext().getString(R.string.target),
            App.getContext().getString(R.string.boost)
    };
    public static int[] silverEggAccountInDataType = {0,
            4,
            10,
            11
    };

    public static String[] silverEggAccountOutData = {App.getContext().getString(R.string.all_)
    };
    public static int[] silverEggAccountOutDataType = {0
    };

    public class EggAccountData {
        public int type;
        public String typeName;
    }
}
