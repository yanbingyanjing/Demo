package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.otc.view.SelectTopView;
import com.yjfshop123.live.ui.fragment.MyFragmentNewTwo;
import com.yjfshop123.live.ui.fragment.MyXunzhangFragment;
import com.yjfshop123.live.ui.fragment.MyXunzhangFragmentNew;
import com.yjfshop123.live.ui.fragment.XunZhangConfigFragment;
import com.yjfshop123.live.ui.fragment.XunzhangConfigFragmentNew;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MyXuanPinActivity extends BaseActivityH {
    @BindView(R.id.text_right)
    SelectTopView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.text_right_btn)
    TextView textRightBtn;


    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wode_xunzhang);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        ButterKnife.bind(this);
        textRightBtn.setVisibility(View.INVISIBLE);
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.xunzhang_title);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setBtnText(titles[0], titles[1]);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setListener(new SelectTopView.SelectListener() {
            @Override
            public void onLeftClick() {
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void onRightClick() {
                mViewPager.setCurrentItem(1);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0 && mContentFragments.get(i) != null)
                    ((MyXunzhangFragmentNew) mContentFragments.get(i)).getData(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setCurrentItem(0);

    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                case 0:
                    if (mContent == null) {
                        mContent = new MyXunzhangFragmentNew();

                        mContentFragments.put(0, mContent);
                    }
                    MyXunzhangFragmentNew fragment1 = (MyXunzhangFragmentNew) mContentFragments.get(0);
                    return fragment1;
                case 1:
                    if (mContent == null) {
                        mContent = new XunzhangConfigFragmentNew();
                        mContentFragments.put(1, mContent);
                    }
                    XunzhangConfigFragmentNew fragment2 = (XunzhangConfigFragmentNew) mContentFragments.get(1);
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
//
//    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//
//
//        public MyFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            mContent = mContentFragments.get(position);
//            if (position == 0) {
//                if (mContent == null) {
//                    mContent = new CtcHomeFragment();
////                if (position == 0)
////                    //((AdditonFragment) mContent).setType(1);
////                else {
////                    ///((AdditonFragment) mContent).setType(3);
////                }
//                    mContentFragments.put(position, mContent);
//                }
//                CtcHomeFragment fragment1 = (CtcHomeFragment) mContentFragments.get(position);
//                return fragment1;
//            } else {
//                if (mContent == null) {
//                    mContent = new OtcGoldFragment();
////                if (position == 0)
////                    //((AdditonFragment) mContent).setType(1);
////                else {
////                    ///((AdditonFragment) mContent).setType(3);
////                }
//                    mContentFragments.put(position, mContent);
//                }
//                OtcGoldFragment fragment1 = (OtcGoldFragment) mContentFragments.get(position);
//                return fragment1;
//
//            }
//
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//
//        @SuppressWarnings("deprecation")
//        @Override
//        public void destroyItem(View container, int position, Object object) {
//            super.destroyItem(container, position, object);
//        }
//    }
}