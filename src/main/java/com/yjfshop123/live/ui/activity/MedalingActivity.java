package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.fragment.MedalingFragment;
import com.yjfshop123.live.ui.fragment.MyFragmentNewTwo;
import com.yjfshop123.live.ui.fragment.MyXunzhangFragment;
import com.yjfshop123.live.ui.fragment.XunZhangListFragment;
import com.yjfshop123.live.ui.widget.SelectBtnView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedalingActivity extends BaseActivityForNewUi {
    @BindView(R.id.tabStrip)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.locak_egg));
        setContentView(R.layout.activity_medaling);
        ButterKnife.bind(this);
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.medaling_status);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        // mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mSlidingTabLayout.setListener(new SelectBtnView.SelectListener() {
            @Override
            public void onLeftClick() {
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void onRightClick() {
                mViewPager.setCurrentItem(1);
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);


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
                mContent = new MedalingFragment();
                if (position == 0)
                    ((MedalingFragment) mContent).setType(1);
                else {
                    ((MedalingFragment) mContent).setType(3);
                }
                mContentFragments.put(position, mContent);
            }
            MedalingFragment fragment1 = (MedalingFragment) mContentFragments.get(position);
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
