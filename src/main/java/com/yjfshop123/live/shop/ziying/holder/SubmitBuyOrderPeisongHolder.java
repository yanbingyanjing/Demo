package com.yjfshop123.live.shop.ziying.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.shop.ziying.model.OrderGoodsResponse;
import com.yjfshop123.live.ui.adapter.TaskAdapter;

import java.math.BigDecimal;

public class SubmitBuyOrderPeisongHolder extends RecyclerView.ViewHolder {


    private TextView peisong;
    private LinearLayout chongzhi_haoma;
    private EditText remark, phone;
    private View phone_line;

    public SubmitBuyOrderPeisongHolder(View itemView, TextWatcher textWatcher,TextWatcher textWatcher2) {
        super(itemView);
        phone_line = itemView.findViewById(R.id.phone_line);
        chongzhi_haoma = itemView.findViewById(R.id.chongzhi_haoma);
        phone = itemView.findViewById(R.id.phone);
        peisong = itemView.findViewById(R.id.peisong);
        remark = itemView.findViewById(R.id.remark);
        remark.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher2);
    }


    public void bind(Context context, OrderGoodsResponse.GoodsItem data, String kind) {
        if (data == null) return;
        if (!TextUtils.isEmpty(kind) && kind.equals("huafei")) {
            chongzhi_haoma.setVisibility(View.VISIBLE);
            phone_line.setVisibility(View.VISIBLE);
        } else {
            chongzhi_haoma.setVisibility(View.GONE);
            phone_line.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(data.yun_fee)) {
            peisong.setText(new BigDecimal(data.yun_fee).compareTo(BigDecimal.ZERO) > 0 ? context.getString(R.string.kuaidi) : context.getString(R.string.baoyou));
        }
    }
}