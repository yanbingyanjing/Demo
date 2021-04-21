package com.yjfshop123.live.otc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcOrderManagerActivity;
import com.yjfshop123.live.ctc.weituo.GuaDanManagerActivity;
import com.yjfshop123.live.ctc.weituo.GuaOrderFragment;
import com.yjfshop123.live.ctc.weituo.WetituoFragment;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.OtcOrderListResponse;
import com.yjfshop123.live.ui.fragment.BaseFragment;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CtcHomeFragment extends BaseFragment {
    @BindView(R.id.h_1_nts_sty)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.order_manager)
    TextView order_manager;

    Unbinder unbinder;
    @BindView(R.id.msg_count)
    TextView msg_count;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_otc_gold;
    }

    @Override
    protected void initAction() {
        mContentFragments = new SparseArray<>();
        String[] titles;

        titles = getResources().getStringArray(R.array.otc_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
//        mSlidingTabLayout.setBtnText(getString(R.string.wo_buy), getString(R.string.wo_sell));
//        mSlidingTabLayout.setListener(new SelectBtnView.SelectListener() {
//            @Override
//            public void onLeftClick() {
//                mViewPager.setCurrentItem(0);
//            }
//
//            @Override
//            public void onRightClick() {
//                mViewPager.setCurrentItem(1);
//            }
//        });
//
        mSlidingTabLayout.setIsNeedBlod(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        order_manager.setVisibility(View.VISIBLE);
        order_manager.setText(R.string.wodeweituo);
        order_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), CtcOrderManagerActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(getContext(), GuaDanManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
         //   getbUYData();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //getbUYData();
    }
    /**
     * 获取数据
     */
    public void getbUYData() {

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", -1)
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/trade/c2cOrderList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) return;
                OtcOrderListResponse data = new Gson().fromJson(result, OtcOrderListResponse.class);
                if (data.list == null) return;
                if (data.list.size() > 0) {
                    msg_count.setVisibility(View.VISIBLE);
                } else msg_count.setVisibility(View.INVISIBLE);
            }
        });

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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] mTitles) {
            super(fm);
            this.mTitles = mTitles;
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
            if (position == 0) {
                if (mContent == null) {
                    mContent = new WetituoFragment();
                    mContentFragments.put(position, mContent);
                }
                WetituoFragment fragment1 = (WetituoFragment) mContentFragments.get(position);
                return fragment1;
            } else {
                if (mContent == null) {
                    mContent = new GuaOrderFragment();
                    mContentFragments.put(position, mContent);
                }
                GuaOrderFragment fragment1 = (GuaOrderFragment) mContentFragments.get(position);
                return fragment1;
            }

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

