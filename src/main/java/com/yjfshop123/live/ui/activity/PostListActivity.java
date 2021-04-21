package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.fragment.MyReplyPostFragment;
import com.yjfshop123.live.ui.fragment.MySendPostFragment;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 描述: 我的发帖
 **/
public class PostListActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    public ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTabLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);

        tv_title_center.setText(getString(R.string.myself_tiezi));

        mViewPager = findViewById(R.id.h_1_vp);
        mSlidingTabLayout = findViewById(R.id.h_1_nts_sty);

        mContentFragments = new SparseArray<>();

        String[] titles = getResources().getStringArray(R.array.tiezi_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);

        mViewPager.setOnPageChangeListener(this);

        setUI();
    }

    private void setUI() {
        mSlidingTabLayout.setViewPager(mViewPager);

        currentItem = getIntent().getIntExtra("currentItem", -1);
        if (currentItem > 0) {
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    private int currentItem;

    @Override
    protected void onResume() {
        super.onResume();

        /*if (currentItem > 0) {
            slide(false);
        } else {
            slide(true);
        }*/
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        /*if (position == 0){
            slide(true);
        }else {
            slide(false);
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
                        mContent = new MySendPostFragment();
                        mContentFragments.put(POS_0, mContent);
                    }
                    MySendPostFragment fragment1 = (MySendPostFragment) mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new MyReplyPostFragment();
                        mContentFragments.put(POS_1, mContent);
                    }
                    MyReplyPostFragment fragment2 = (MyReplyPostFragment) mContentFragments.get(POS_1);
                    return fragment2;
            }
            return null;
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
