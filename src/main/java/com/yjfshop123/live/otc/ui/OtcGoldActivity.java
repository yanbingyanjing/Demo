package com.yjfshop123.live.otc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.weituo.WeiTuoListActivity;
import com.yjfshop123.live.otc.order.OrderManagerActivity;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class OtcGoldActivity extends BaseActivityH {
    @BindView(R.id.select_btn)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.text_right)
    TextView textRight;
    @BindView(R.id.zixuan)
    TextView zixuan;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otc_gold);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        ButterKnife.bind(this);
        mContentFragments = new SparseArray<>();
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setBtnText(getString(R.string.wo_buy), getString(R.string.wo_sell));
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
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtcGoldActivity.this, OrderManagerActivity.class);
                startActivity(intent);
            }
        });
        zixuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtcGoldActivity.this, WeiTuoListActivity.class);
                startActivity(intent);
            }
        });
        //TODO 模拟收到通知
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //要执行的操作
//                EventBus.getDefault().post("{\n" +
//                        "    \"code\": \"otc\",\n" +
//                        "    \"data\": {\n" +
//                        "        \"cmd\": \"otc_receive_eggs\",\n" +
//                        "        \"content\": {\n" +
//                        "            \"content\": \"购买的88个金蛋已到账\"\n" +
//                        "        }\n" +
//                        "    }\n" +
//                        "}", Config.EVENT_OTC);
//
//            }
//        }, 5000);//2秒后执行Runnable中的run方法
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

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
                    mContent = new BuySellFragment();
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                BuySellFragment fragment1 = (BuySellFragment) mContentFragments.get(position);
                return fragment1;
            } else {
                if (mContent == null) {
                    mContent = new SellFragment();
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                SellFragment fragment1 = (SellFragment) mContentFragments.get(position);
                return fragment1;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}
