package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

public class H_1_4_1_Fragment extends Fragment implements ViewPager.OnPageChangeListener {

    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_4_1, container, false);

        mViewPager = view.findViewById(R.id.fragment_h_4_1_vp);
        mSlidingTabLayout = view.findViewById(R.id.fragment_h_4_1_psts);

        mContentFragments = new SparseArray<>();

        String[] titles = getResources().getStringArray(R.array.paihang_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                mContent = new H_1_4_1_1_Fragment();
                mContentFragments.put(position, mContent);
            }
            H_1_4_1_1_Fragment fragment = (H_1_4_1_1_Fragment) mContentFragments.get(position);

            Bundle bundle1 = new Bundle();
            bundle1.putInt("type", type);//0女神榜 1富豪榜
            bundle1.putInt("type_2", position);//0日 1月 2总
            fragment.setArguments(bundle1);

            return fragment;
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

    private int type = 0;//0女神榜 1富豪榜

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
