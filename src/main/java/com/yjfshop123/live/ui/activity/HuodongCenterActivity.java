package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.yjfshop123.live.R;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class HuodongCenterActivity extends BaseActivityForNewUi {


    @BindView(R.id.tupian)
    SelectableRoundedImageView tupian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huodong_center);
        ButterKnife.bind(this);
        setBlackColorTooBar();
        setCenterTitleText(getString(R.string.huodong_center));
        setTooBarBack(R.drawable.bg_gradient_faf7ed_f4ecd7);
        StatusBarUtil.StatusBarLightMode(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tupian.getLayoutParams();
        //获取当前控件的布局对象
        params.width =SystemUtils.getScreenWidth(this)-SystemUtils.dip2px(this,32);
        params.height =  params.width *340/750;//设置当前控件布局的高度
        tupian.setLayoutParams(params);
    }


    @OnClick(R.id.tupian)
    public void onViewClicked() {
        Intent huodong = new Intent();
        huodong.setClass(this, XinxiShangchuanActivity.class);
       startActivity(huodong);
    }
}
