package com.yjfshop123.live.model;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CtcListResopnse {
    public class CtcListData {
        public String order;
        public String user_id;
        public String name;//
        public String deal;//处理了多少笔
        public String amount;//剩余数量
        public String total_eggs;//全部数量

        public String price;//价格
        public String minNum;//限额
        public String maxNum;//限额
        public String avatar;
        public String card_type; // "card_type": "alipay"支持的付款方式
        public int is_self;//1表示自己挂的委托单，显示取消按钮
        public String desc;//订单状态

        public int status;//订单审核状态
    }

    public List<CtcListData> list;
}
