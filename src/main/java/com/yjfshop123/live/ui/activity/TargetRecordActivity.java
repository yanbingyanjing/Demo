package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.fragment.MedalingFragment;
import com.yjfshop123.live.ui.fragment.TargetRecordFragment;
import com.yjfshop123.live.ui.widget.SelectBtnView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TargetRecordActivity extends BaseActivityForNewUi {

    @BindView(R.id.tab)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCenterTitleText(getString(R.string.zhongjiangjilu));
        setContentView(R.layout.activity_target_record);
        ButterKnife.bind(this);
        String[] titles = {getString(R.string.yilingqu), getString(R.string.weilingqu)};
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        // mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mSlidingTabLayout.setBtnText(getString(R.string.yilingqu), getString(R.string.weilingqu));
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

    private SparseArray<Fragment> mContentFragments= new SparseArray<>();;
    private Fragment mContent;


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
                mContent = new TargetRecordFragment();
                ((TargetRecordFragment)mContent).setType(position+1);
                mContentFragments.put(position, mContent);
            }
            TargetRecordFragment fragment1 = (TargetRecordFragment) mContentFragments.get(position);
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
