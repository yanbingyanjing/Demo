package com.yjfshop123.live.ui.activity.income;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.ui.activity.AdditionActivity;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.utils.DateFormatUtil;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhuBoIncomeActivity extends BaseActivityForNewUi {
    @BindView(R.id.mSlidingTabLayout)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.zhubo_income));
        setContentView(R.layout.activity_zhubo_income);
        ButterKnife.bind(this);
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.income);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            if (mContent == null) {
                mContent = new IncomeFragment();
                if (position == 0) {
                    ((IncomeFragment) mContent).setDate(DateFormatUtil.getCurTimeYMDString(),DateFormatUtil.getCurTimeYMDString());
                } else if (position == 1) {
                    ((IncomeFragment) mContent).setDate(DateFormatUtil.getNextDate(-1),DateFormatUtil.getNextDate(-1));
                } else {
                    ((IncomeFragment) mContent).setDate(DateFormatUtil.getNextDate(-6),DateFormatUtil.getCurTimeYMDString());
                }
                mContentFragments.put(position, mContent);
            }
            IncomeFragment fragment1 = (IncomeFragment) mContentFragments.get(position);
            return fragment1;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
