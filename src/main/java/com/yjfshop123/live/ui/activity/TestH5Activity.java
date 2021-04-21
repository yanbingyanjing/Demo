package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestH5Activity extends AppCompatActivity {
    @BindView(R.id.url)
    EditText url;
    @BindView(R.id.tiaozhuan)
    Button tiaozhuan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_h5);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tiaozhuan)
    public void onViewClicked() {
        if (TextUtils.isEmpty(url.getText().toString())) {
            NToast.shortToast(this, "请输入网址");
            return;
        }
        Intent intent = new Intent(this, WebViewActivity.class);

        intent.putExtra("url", url.getText().toString());

        startActivity(intent);
    }
}
