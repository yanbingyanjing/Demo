package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.utils.NumUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EggOrderDetailActivity extends BaseActivityForNewUi {
    @BindView(R.id.icon)
    CircleImageView icon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.order_type)
    TextView orderType;
    @BindView(R.id.change_des)
    TextView changeDes;
    @BindView(R.id.date)
    TextView date;
    EggListDataResponse.EggListData eggListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.order_detail));
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            eggListData=new Gson().fromJson(getIntent().getStringExtra("data"),EggListDataResponse.EggListData.class);
        }
        if(eggListData!=null){
//            Glide.with(this).load(eggListData.head_icon).into(icon);
//            userName.setText(eggListData.name);
//            phone.setText(eggListData.phone);
            orderType.setText(eggListData.order_type);
            changeDes.setText(NumUtil.clearZero(eggListData.change_des));
            date.setText(eggListData.date);
        }
    }
}
