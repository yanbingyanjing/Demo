package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yjfshop123.live.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 日期:2018/10/22
 * 描述:我的个性签名
 **/
public class MySignatureActivity extends BaseActivity implements View.OnClickListener {

    private static final int MAX_NUM = 100;

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.currentTxt)
    TextView currentTxt;
    @BindView(R.id.signatureTxt)
    EditText signatureTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysignature);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.signature_change);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(R.string.Done);

        mHeadRightText.setOnClickListener(this);

        signatureTxt.addTextChangedListener(watcher);

        String content = getIntent().getStringExtra("content");
        signatureTxt.setText(content);
        signatureTxt.setSelection(content.length());

    }

    TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
            Log.i("onTextChanged", s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
            Log.i("beforeTextChanged", s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            Log.i("afterTextChanged", s.toString());
            if (s.length() > MAX_NUM) {
                s.delete(MAX_NUM, s.length());
            }
            int num = MAX_NUM - s.length();
            currentTxt.setText(s.length() + "/" + MAX_NUM + "字");
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_right:
                setResult(1, new Intent().putExtra("result", signatureTxt.getText().toString()));
                finish();
                break;
        }
    }
}
