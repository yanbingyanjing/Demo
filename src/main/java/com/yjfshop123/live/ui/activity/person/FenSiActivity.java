package com.yjfshop123.live.ui.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FenSiActivity extends BaseActivityForNewUi {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.clear)
    ImageView clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_fix);
        ButterKnife.bind(this);
        setCenterTitleText("粉丝");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
