package com.yjfshop123.live.ctc.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.otc.CtcOtcOrderManagerActivity;
import com.yjfshop123.live.otc.order.OrderFragment;
import com.yjfshop123.live.otc.order.OrderManagerActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.fragment.EggListByOrderTypeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CtcOrderManagerFragment extends BaseFragment {
    @BindView(R.id.mSlidingTabLayout)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.msg_count)
    TextView msg_count;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    CtcOtcOrderManagerActivity orderManagerActivity;

    public void setActivity(CtcOtcOrderManagerActivity orderManagerActivity) {
        this.orderManagerActivity = orderManagerActivity;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.otc_order_manager_fragment;
    }


    @Override
    protected void initAction() {
        mContentFragments = new SparseArray<>();
        String[] title = {"进行中", "已完成", "已取消"};
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), title);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);


    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    protected void initEvent() {

    }


    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setHongDian(int Visible) {
        msg_count.setVisibility(Visible);
        if(orderManagerActivity!=null)orderManagerActivity.setRed();
    }



    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        public String[] getmTitles() {
            return mTitles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            switch (position) {
                case 0:
                    if (mContent == null) {
                        mContent = new CtcOrderFragment();
                        ((CtcOrderFragment) mContent).setType(-1);
                        ((CtcOrderFragment) mContent).setFragment(CtcOrderManagerFragment.this);
                        mContentFragments.put(0, mContent);
                    }
                    CtcOrderFragment fragment1 = (CtcOrderFragment) mContentFragments.get(0);
                    return fragment1;
                case 1:
                    if (mContent == null) {
                        mContent = new CtcOrderFragment();
                        ((CtcOrderFragment) mContent).setType(3);
                        mContentFragments.put(1, mContent);
                    }
                    CtcOrderFragment fragment2 = (CtcOrderFragment) mContentFragments.get(1);
                    return fragment2;
                case 2:
                    if (mContent == null) {
                        mContent = new CtcOrderFragment();
                        ((CtcOrderFragment) mContent).setType(0);

                        mContentFragments.put(2, mContent);
                    }
                    CtcOrderFragment fragment3 = (CtcOrderFragment) mContentFragments.get(2);
                    return fragment3;
            }
            EggListByOrderTypeFragment fragment1 = (EggListByOrderTypeFragment) mContentFragments.get(position);
            return fragment1;


        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            //  super.destroyItem(container, position, object);
        }
    }


}
