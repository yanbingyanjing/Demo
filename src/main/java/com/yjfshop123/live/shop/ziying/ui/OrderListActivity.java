package com.yjfshop123.live.shop.ziying.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.App;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.BaseActivityForNewUi;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderListActivity extends BaseActivityH {
    @BindView(R.id.mSlidingTabLayout)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    MyFragmentPagerAdapter fAdapter;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;


    public String[] titles = {App.getContext().getString(R.string.all_),
            "待付款",
            "待发货",
            "待收货",
            "待评价",
            "退换货"
    };
    public String[] type = {"all",
            "3",
            "1",
            "4",
            "6",
            "after_sales"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziying_order_list);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        statusBarView.setLayoutParams(params);

        mContentFragments = new SparseArray<>();
        fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
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
                mContent = new OrderFragment();
                ((OrderFragment) mContent).setStatus(type[position]);
                mContentFragments.put(position, mContent);
            }
            OrderFragment fragment1 = (OrderFragment) mContentFragments.get(position);
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
