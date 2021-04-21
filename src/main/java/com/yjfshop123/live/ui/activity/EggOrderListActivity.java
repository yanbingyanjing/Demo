package com.yjfshop123.live.ui.activity;

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
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.fragment.EggOrderListFragment;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 金银臭蛋明细
 */
public class EggOrderListActivity extends BaseActivityH {
    @BindView(R.id.h_1_top_view)
    View h1TopView;
    @BindView(R.id.btn_left)
    ImageView btnLeft;
    @BindView(R.id.h_1_nts_sty)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    public static final int accountant_all = 0;//全部
    public static final int accountant_in = 1;//收入
    public static final int accountant_out = 2;//支出
    public static final int EGG_GOLD = 1;//金蛋
    public static final int EGG_SILVER = 2;//银蛋
    public static final int EGG_BAD = 3;//臭蛋
    public static final int EGG_TRADE = 4;//交易明细类型（包含充值和提币）

    private int eggType = -1;
    private int index=0;//用于切换到显示臭蛋兑换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silver_egg_list);
        ButterKnife.bind(this);
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.shouru_zhichu);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        if (getIntent() != null) {
            eggType = getIntent().getIntExtra("eggType", -1);
            index=getIntent().getIntExtra("index", 0);
        }

        mViewPager.setAdapter(fAdapter);

        mViewPager.setCurrentItem(0);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBtnText(titles[0], titles[1]);
        mSlidingTabLayout.setListener(new SelectBtnView.SelectListener() {
            @Override
            public void onLeftClick() {
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void onRightClick() {
                mViewPager.setCurrentItem(1);
            }
        });

        if (eggType == EGG_BAD) {
            mSlidingTabLayout.setVisibility(View.GONE);
            tvTitleCenter.setVisibility(View.VISIBLE);
            tvTitleCenter.setText(R.string.bad_egg_list);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.h_1_top_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.h_1_top_view).setLayoutParams(params);
        StatusBarUtil.StatusBarDarkMode(this);
    }

    @OnClick(R.id.btn_left)
    public void onViewClicked() {
        finish();
    }


    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;

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
                        mContent = new EggOrderListFragment();
                        if (eggType == EGG_BAD) {
                            //臭蛋全部
                            ((EggOrderListFragment) mContent).accountant = accountant_all;
                        } else {
                            ((EggOrderListFragment) mContent).accountant = accountant_in;
                            ((EggOrderListFragment) mContent).indexPage =index;
                        }
                        ((EggOrderListFragment) mContent).eggType = eggType;
                        mContentFragments.put(POS_0, mContent);
                    }
                    EggOrderListFragment fragment1 = (EggOrderListFragment) mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new EggOrderListFragment();
                        ((EggOrderListFragment) mContent).accountant = accountant_out;
                        ((EggOrderListFragment) mContent).eggType = eggType;
                        mContentFragments.put(POS_1, mContent);
                    }
                    EggOrderListFragment fragment2 = (EggOrderListFragment) mContentFragments.get(POS_1);
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
