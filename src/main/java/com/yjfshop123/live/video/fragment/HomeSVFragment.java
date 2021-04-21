package com.yjfshop123.live.video.fragment;

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
import com.yjfshop123.live.ui.activity.SearchCommunityActivity;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

public class HomeSVFragment extends Fragment implements ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTabLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_sv, container, false);

        mContentFragments = new SparseArray<>();
        mViewPager = view.findViewById(R.id.sv_vp);
        mSlidingTabLayout = view.findViewById(R.id.sv_nts_sty);


        String[] titles = new String[]{"关注", "推荐"};

        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);

        mViewPager.setCurrentItem(1);

        view.findViewById(R.id.h_video_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchCommunityActivity.class).putExtra("TYPE", "short_video"));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });
        return view;
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
                mContent = new HomeSmallVideoFragment();
                mContentFragments.put(position, mContent);
            }
            HomeSmallVideoFragment fragment = (HomeSmallVideoFragment) mContentFragments.get(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        HomeSmallVideoFragment fragment0 = (HomeSmallVideoFragment) mContentFragments.get(0);
        HomeSmallVideoFragment fragment1 = (HomeSmallVideoFragment) mContentFragments.get(1);
        if (fragment0 != null){
            fragment0.hiddenChanged(hidden);
        }
        if (fragment1 != null){
            fragment1.hiddenChanged(hidden);
        }
    }

    public void cleanLogin(){
        HomeSmallVideoFragment fragment0 = (HomeSmallVideoFragment) mContentFragments.get(0);
        HomeSmallVideoFragment fragment1 = (HomeSmallVideoFragment) mContentFragments.get(1);
        if (fragment0 != null){
            fragment0.cleanLogin();
        }
        if (fragment1 != null){
            fragment1.cleanLogin();
        }
    }
}
