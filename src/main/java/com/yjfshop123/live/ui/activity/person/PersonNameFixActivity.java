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

public class PersonNameFixActivity extends BaseActivityForNewUi {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.clear)
    ImageView clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.persional_my_nick));
        setContentView(R.layout.activity_name_fix);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            name.setText(getIntent().getStringExtra("name"));
        }
    }

    @OnClick(R.id.clear)
    public void onViewClicked() {
        name.setText("");
    }

    @Override
    public void finish() {
        if (!TextUtils.isEmpty(name.getText().toString())) {
            Intent intent = new Intent();
            intent.putExtra("name", name.getText().toString());
            setResult(100,intent);
        }
        super.finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
