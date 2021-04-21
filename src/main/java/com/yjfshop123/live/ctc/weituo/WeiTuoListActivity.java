package com.yjfshop123.live.ctc.weituo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcOrderManagerActivity;
import com.yjfshop123.live.ctc.view.BuyEggDialog;
import com.yjfshop123.live.otc.order.OrderFragment;
import com.yjfshop123.live.otc.order.OrderManagerActivity;
import com.yjfshop123.live.otc.ui.BuySellFragment;
import com.yjfshop123.live.otc.ui.OtcGoldActivity;
import com.yjfshop123.live.otc.ui.SellFragment;
import com.yjfshop123.live.otc.view.OtcTipsDialog;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WeiTuoListActivity extends BaseActivityH {


    @BindView(R.id.select_btn)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.guadan)
    TextView guadan;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.text_right)
    TextView textRight;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctc_home);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        mContentFragments = new SparseArray<>();
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mViewPager.setCurrentItem(0);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeiTuoListActivity.this, CtcOrderManagerActivity.class);
                startActivity(intent);
            }
        });
        guadan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeiTuoListActivity.this, GuaOrderActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (mContent != null) {
                ((WetituoFragment) mContent).getData(false);
            }
        }
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);
            if (mContent == null) {
                mContent = new WetituoFragment();
                mContentFragments.put(position, mContent);
            }
            WetituoFragment fragment1 = (WetituoFragment) mContentFragments.get(position);
            return fragment1;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}

