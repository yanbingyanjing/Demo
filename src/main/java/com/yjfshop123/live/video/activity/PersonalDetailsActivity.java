package com.yjfshop123.live.video.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.BaseActivity;
import com.yjfshop123.live.video.fragment.MeVideoFragment;

public class PersonalDetailsActivity extends BaseActivity {

    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_chat);

        mUserID = getIntent().getStringExtra("USER_ID");

        setHeadVisibility(View.GONE);
        addFragment(R.id.activity_o_chat_fl, new MeVideoFragment(), "0.0");
    }

    protected void addFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", mUserID);
        fragment.setArguments(bundle);
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void getFinish(){
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
