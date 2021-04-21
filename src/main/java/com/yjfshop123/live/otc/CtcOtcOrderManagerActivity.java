package com.yjfshop123.live.otc;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcOrderManagerFragment;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.otc.order.OtcOrderManagerFragment;
import com.yjfshop123.live.otc.view.SelectTopView;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//订单管理界面
public class CtcOtcOrderManagerActivity extends BaseActivityH {
    @BindView(R.id.text_right)
    SelectTopView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.msg_count_otc)
    TextView msgCountOtc;
    @BindView(R.id.msg_count_ctc)
    TextView msgCountCtc;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ctc_otc_order_manager);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        ButterKnife.bind(this);
        final int current=getIntent().getIntExtra("index",0);
        mContentFragments = new SparseArray<>();
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setBtnText("自选订单","快捷订单" );
        mSlidingTabLayout.setInitIndex(current);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(current);
            }
        },0);

    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
        hideKeyBord();
    }

    public void setRed() {
        getUserStatus();
    }

    private void getUserStatus() {
        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {
                UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                int totalotc = data.otc_order_running_nums;
                int totalctc = data.c2c_order_running_nums;
                if (totalotc > 0) {
                    msgCountOtc.setText(totalotc > 99 ? "99+" : totalotc + "");
                    msgCountOtc.setVisibility(View.VISIBLE);
                } else {
                    msgCountOtc.setVisibility(View.INVISIBLE);
                }
                if (totalctc > 0) {
                    msgCountCtc.setText(totalctc > 99 ? "99+" : totalctc + "");
                    msgCountCtc.setVisibility(View.VISIBLE);
                } else {
                    msgCountCtc.setVisibility(View.INVISIBLE);
                }
            }
        });
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
                    mContent = new CtcOrderManagerFragment();
                    ((CtcOrderManagerFragment) mContent).setActivity(CtcOtcOrderManagerActivity.this);
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                CtcOrderManagerFragment fragment1 = (CtcOrderManagerFragment) mContentFragments.get(position);
                return fragment1;
            } else {
                if (mContent == null) {
                    mContent = new OtcOrderManagerFragment();
                    ((OtcOrderManagerFragment) mContent).setActivity(CtcOtcOrderManagerActivity.this);
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                OtcOrderManagerFragment fragment1 = (OtcOrderManagerFragment) mContentFragments.get(position);
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