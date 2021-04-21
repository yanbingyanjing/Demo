package com.yjfshop123.live.ui.activity;

import android.os.Bundle;

import com.yjfshop123.live.R;
import com.yjfshop123.live.utils.StatusBarUtil;

public class MyMoreActivity extends BaseActivityH {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_more);
        StatusBarUtil.StatusBarDarkMode(this);
    }
}
