package com.yjfshop123.live.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.adapter.SearchCommunityAdapter;
import com.yjfshop123.live.ui.widget.TabXunzhangView;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.yjfshop123.live.ui.widget.shimmer.EmptyLayout.STATUS_LOADING;

public class MyXunzhangFragment extends BaseFragment implements ViewPager.OnPageChangeListener {


    @BindView(R.id.tab_xunzhang)
    TabXunzhangView mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    protected Fragment mContentInit;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_xunzhang;
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

    MyFragmentNewTwo fragment;

    public void setFragment(MyFragmentNewTwo fragment) {
        this.fragment = fragment;
    }

    int type = 1;
    String testData = "{\n" +
            "      \"list\": [\n" +
            "        {\n" +
            "        \"id\":1,//数据唯一id\n" +
            "        \"medal_des\": \"体验选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "        \"effective_time\":\"2020-08-30 20:20:20\"//生效时间\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\":2,//数据唯一id\n" +
            "        \"medal_des\": \"一级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-29 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"二级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"二级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"二级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"二级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            ",\n" +
            "        {\n" +
            "          \"id\":3,//数据唯一id\n" +
            "        \"medal_des\": \"二级选品\",//选品名称\n" +
            "        \"medal_icon\": \"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",//选品图标地址\n" +
            "        \"medal_daily_income\": 11,//每日收益\n" +
            "        \"medal_income_percent\": \"25%\",//收益率\n" +
            "        \"medal_exchange\":10,//兑换所需金蛋数量\n" +
            "        \"medal_total_release_day\":44,//总释放天数\n" +
            "        \"medal_released_day\":3,//已释放天数\n" +
            "       \"effective_time\":\"2020-08-28 20:20:20\"//生效时间\n" +
            "        }\n" +
            "      ]\n" +
            "}";

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
            ((XunZhangListFragment) mContent).showLoading();
        }

        ((XunZhangListFragment) mContent).page=1;

        OKHttpUtils.getInstance().getRequest("app/medal/myMedal", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (isFromRefresh) {
                    if (fragment != null)
                        ((MyFragmentNewTwo) fragment).hideLoading();
                } else {
                    if (((XunZhangListFragment) mContent).mEmptyLayout != null && ((XunZhangListFragment) mContent).mEmptyLayout.getVisibility() == View.VISIBLE) {
                        ((XunZhangListFragment) mContent).showNotData();
                    }
                }

                //模拟数据
                // ((XunZhangListFragment) mContent).loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                if (isFromRefresh) {

                    if (fragment != null)
                        ((MyFragmentNewTwo) fragment).hideLoading();

                } else {
                    ((XunZhangListFragment) mContent).hideLoading();
                }
                //模拟数据
                // ((XunZhangListFragment) mContent).loadData(testData);
                ((XunZhangListFragment) mContent).loadData(result);
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
                ((XunZhangListFragment) mContent).page--;
                //模拟数据
                // ((XunZhangListFragment) mContent).loadMoreData(testData);
                ((XunZhangListFragment) mContent).loadMoreOver();
            }

            @Override
            public void onSuccess(String result) {
                ((XunZhangListFragment) mContent).loadMoreData(result);
                ((XunZhangListFragment) mContent).loadMoreOver();
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
            mSlidingTabLayout.threeClick();type = 3;
        }

        mContent = mContentFragments.get(i);
        if (((XunZhangListFragment) mContent).mEmptyLayout != null
                && ((XunZhangListFragment) mContent).mEmptyLayout.getVisibility() == View.VISIBLE
                && ((XunZhangListFragment) mContent).mEmptyLayout.getEmptyStatus() != STATUS_LOADING) {
            getData(false);
            return;
        }

        if (((XunZhangListFragment) mContent).page == 1)
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
                mContentInit = new XunZhangListFragment();
                ((XunZhangListFragment) mContentInit).setFragment(MyXunzhangFragment.this);
                ((XunZhangListFragment) mContentInit).setIndex(position);
                mContentFragments.put(position, mContentInit);
            }
            XunZhangListFragment fragment1 = (XunZhangListFragment) mContentFragments.get(position);
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

    }


}
