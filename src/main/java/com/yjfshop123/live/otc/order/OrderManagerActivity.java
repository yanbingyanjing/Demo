package com.yjfshop123.live.otc.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.fragment.EggListByOrderTypeFragment;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class OrderManagerActivity extends BaseActivityH {


    @BindView(R.id.mSlidingTabLayout)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.msg_count)
    TextView msg_count;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    @BindView(R.id.text_right)
    TextView text_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        ButterKnife.bind(this);
        text_right.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        mContentFragments = new SparseArray<>();
        String[] title = {"进行中", "已完成", "已取消"};
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), title);
//        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    public void setHongDian(int Visible) {
        msg_count.setVisibility(Visible);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        public String[] getmTitles() {
            return mTitles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            switch (position) {
                case 0:
                    if (mContent == null) {
                        mContent = new OrderFragment();
                        ((OrderFragment) mContent).setType(-1);
                        ((OrderFragment) mContent).setActivity(OrderManagerActivity.this);
                        mContentFragments.put(0, mContent);
                    }
                    OrderFragment fragment1 = (OrderFragment) mContentFragments.get(0);
                    return fragment1;
                case 1:
                    if (mContent == null) {
                        mContent = new OrderFragment();
                        ((OrderFragment) mContent).setType(3);
                        mContentFragments.put(1, mContent);
                    }
                    OrderFragment fragment2 = (OrderFragment) mContentFragments.get(1);
                    return fragment2;
                case 2:
                    if (mContent == null) {
                        mContent = new OrderFragment();
                        ((OrderFragment) mContent).setType(0);

                        mContentFragments.put(2, mContent);
                    }
                    OrderFragment fragment3 = (OrderFragment) mContentFragments.get(2);
                    return fragment3;
            }
            EggListByOrderTypeFragment fragment1 = (EggListByOrderTypeFragment) mContentFragments.get(position);
            return fragment1;


        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            //  super.destroyItem(container, position, object);
        }
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }
}
