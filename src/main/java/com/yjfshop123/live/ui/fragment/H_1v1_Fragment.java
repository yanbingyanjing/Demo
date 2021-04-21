package com.yjfshop123.live.ui.fragment;

import android.content.Context;
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
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.ListActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.SearchActivity;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

public class H_1v1_Fragment extends Fragment implements ViewPager.OnPageChangeListener{

    private Context context;

    public static ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTabLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_1v1, container, false);

        mViewPager = view.findViewById(R.id.h_1_vp);
        mSlidingTabLayout = view.findViewById(R.id.h_1_nts_sty);

        mContentFragments = new SparseArray<>();

        String[] titles = getResources().getStringArray(R.array.home_titles2);

        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);

        ImageView h_1_list = view.findViewById(R.id.h_1_list);
        ImageView h_1_search = view.findViewById(R.id.h_1_search);
        h_1_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()){
                    startActivity(new Intent(context, SearchActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });
        h_1_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ListActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        return view;
    }

    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
    }

    private boolean isLogin(){
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
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
            switch (position) {
                case POS_0:
                    if (mContent == null) {
                        mContent = new H_1_1_Fragment();
                        mContentFragments.put(POS_0, mContent);
                    }
                    H_1_1_Fragment fragment1 = (H_1_1_Fragment)mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new H_1_2_Fragment();
                        mContentFragments.put(POS_1, mContent);
                    }
                    H_1_2_Fragment fragment2 = (H_1_2_Fragment) mContentFragments.get(POS_1);
                    return fragment2;
                case POS_2:
                    if (mContent == null) {
                        mContent = new H_1_3_Fragment();
                        mContentFragments.put(POS_2, mContent);
                    }
                    H_1_3_Fragment fragment3 = (H_1_3_Fragment) mContentFragments.get(POS_2);
                    return fragment3;

            }
            return null;
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

}
