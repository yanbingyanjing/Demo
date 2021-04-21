package com.yjfshop123.live.ui.fragment;

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
import android.widget.LinearLayout;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.ListActivity;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

//女神富豪榜界面
public class H_1_4_Fragment extends Fragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.fragment_h_4_psts)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    private ListActivity listActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ListActivity){
            listActivity = (ListActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_4, container, false);
        ButterKnife.bind(this, view);
        viewPager = view.findViewById(R.id.viewPager);

        mContentFragments = new SparseArray<>();

        String[] titles = getResources().getStringArray(R.array.h_4_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        viewPager.setAdapter(fAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        mSlidingTabLayout.setViewPager(viewPager);
        mSlidingTabLayout.setBtnText(titles[0], titles[1]);
        mSlidingTabLayout.setListener(new SelectBtnView.SelectListener() {
            @Override
            public void onLeftClick() {
                viewPager.setCurrentItem(0);
            }

            @Override
            public void onRightClick() {
                viewPager.setCurrentItem(1);
            }
        });
        ImageView _back = view.findViewById(R.id._back);
        if (listActivity != null){
            _back.setVisibility(View.VISIBLE);
            _back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listActivity.getFinish();
                }
            });
        }else {
            _back.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)   view.findViewById(R.id.status_bar).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        view.findViewById(R.id.status_bar).setLayoutParams(params);
        return view;
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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        String[] titles;

        public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            if (mContent == null) {
                mContent = new H_1_4_1_Fragment();
                mContentFragments.put(position, mContent);
            }
            H_1_4_1_Fragment fragment = (H_1_4_1_Fragment) mContentFragments.get(position);
            fragment.setType(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
