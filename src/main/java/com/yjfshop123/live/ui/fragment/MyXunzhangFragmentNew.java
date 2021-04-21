package com.yjfshop123.live.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.ui.widget.TabXunzhangView;

import org.json.JSONException;

import butterknife.BindView;

import static com.yjfshop123.live.ui.widget.shimmer.EmptyLayout.STATUS_LOADING;

public class MyXunzhangFragmentNew extends BaseFragment implements ViewPager.OnPageChangeListener {


    @BindView(R.id.tab_xunzhang)
    TabXunzhangView mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    protected Fragment mContentInit;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_xunzhang_new;
    }

    @Override
    protected void initAction() {
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.xunzhang_status_title);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSlidingTabLayout.setListener(new TabXunzhangView.SelectListener() {
            @Override
            public void onOneClick() {
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void onTwoClick() {
                mViewPager.setCurrentItem(1);
            }

            @Override
            public void onThreeClick() {
                mViewPager.setCurrentItem(2);
            }
        });
        mViewPager.setCurrentItem(0);

    }


    int type = 1;

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        if (mContent == null) mContent = mContentFragments.get(0);
        if (mContent == null) return;

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }
        if (isFromRefresh) {
            showLoading();
        } else {
            ((XunZhangListFragmentNew) mContent).showLoading();
        }

        ((XunZhangListFragmentNew) mContent).page = 1;

        OKHttpUtils.getInstance().getRequest("app/medal/myMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (isFromRefresh) {
                    hideLoading();
                } else {
                    if (((XunZhangListFragmentNew) mContent).mEmptyLayout != null && ((XunZhangListFragmentNew) mContent).mEmptyLayout.getVisibility() == View.VISIBLE) {
                        ((XunZhangListFragmentNew) mContent).showNotData();
                    }
                }

                //模拟数据
                // ((XunZhangListFragment) mContent).loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                if (isFromRefresh) {
                    hideLoading();
                } else {
                    ((XunZhangListFragmentNew) mContent).hideLoading();
                }
                //模拟数据
                // ((XunZhangListFragment) mContent).loadData(testData);
                ((XunZhangListFragmentNew) mContent).loadData(result);
            }
        });

    }

    /**
     * 获取数据
     */
    public void getMoreData(int page) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/medal/myMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                ((XunZhangListFragmentNew) mContent).page--;
                //模拟数据
                // ((XunZhangListFragment) mContent).loadMoreData(testData);
                ((XunZhangListFragmentNew) mContent).loadMoreOver();
            }

            @Override
            public void onSuccess(String result) {
                ((XunZhangListFragmentNew) mContent).loadMoreData(result);
                ((XunZhangListFragmentNew) mContent).loadMoreOver();
                //模拟数据
                // ((XunZhangListFragment) mContent).loadMoreData(testData);

            }
        });

    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            mSlidingTabLayout.oneClick();
            type = 1;
        }
//        if (i == 1) {
//            mSlidingTabLayout.twoClick();
//        }
        if (i == 1) {
            mSlidingTabLayout.threeClick();
            type = 3;
        }

        mContent = mContentFragments.get(i);
        if (((XunZhangListFragmentNew) mContent).mEmptyLayout != null
                && ((XunZhangListFragmentNew) mContent).mEmptyLayout.getVisibility() == View.VISIBLE
                && ((XunZhangListFragmentNew) mContent).mEmptyLayout.getEmptyStatus() != STATUS_LOADING) {
            getData(false);
            return;
        }

        if (((XunZhangListFragmentNew) mContent).page == 1)
            getData(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
            mContentInit = mContentFragments.get(position);

            if (mContentInit == null) {
                mContentInit = new XunZhangListFragmentNew();
                ((XunZhangListFragmentNew) mContentInit).setFragment(MyXunzhangFragmentNew.this);
                ((XunZhangListFragmentNew) mContentInit).setIndex(position);
                mContentFragments.put(position, mContentInit);
            }
            XunZhangListFragmentNew fragment1 = (XunZhangListFragmentNew) mContentFragments.get(position);
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

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }


}
