package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.yonghuxieyi)
    TextView yonghuxieyi;
    @BindView(R.id.yinsizhengce)
    TextView yinsizhengce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.about_us);

        yonghuxieyi.setOnClickListener(this);
        yinsizhengce.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yonghuxieyi:
                Intent intent = new Intent(this, HtmlWebViewActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.yinsizhengce:
                Intent intent2 = new Intent(this, HtmlWebViewActivity.class);
                intent2.putExtra("type", 1);
                startActivity(intent2);
                break;
        }
    }
}
