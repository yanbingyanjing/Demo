package com.yjfshop123.live.otc;

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

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.otc.view.SelectTopView;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainOtcActivity extends BaseActivityH {
    @BindView(R.id.text_right)
    SelectTopView mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.text_right_btn)
    TextView textRightBtn;
    @BindView(R.id.msg_count)
    TextView msgCount;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_otc);
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
        mSlidingTabLayout.setBtnText(getString(R.string.zixuanqu),getString(R.string.kuaijie));
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
        mViewPager.setCurrentItem(0);
        textRightBtn.setVisibility(View.VISIBLE);
        textRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainOtcActivity.this, CtcOtcOrderManagerActivity.class);
                intent.putExtra("index",mViewPager.getCurrentItem());
                startActivity(intent);
            }
        });
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
                int totalotc = data.otc_order_running_nums + data.c2c_order_running_nums;
                if (totalotc > 0) {
                    msgCount.setText(totalotc > 99 ? "99+" : totalotc + "");
                    msgCount.setVisibility(View.VISIBLE);
                } else {
                    msgCount.setVisibility(View.INVISIBLE);
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
                    mContent = new CtcHomeFragment();
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                CtcHomeFragment fragment1 = (CtcHomeFragment) mContentFragments.get(position);
                return fragment1;
            } else {
                if (mContent == null) {
                    mContent = new OtcGoldFragment();
//                if (position == 0)
//                    //((AdditonFragment) mContent).setType(1);
//                else {
//                    ///((AdditonFragment) mContent).setType(3);
//                }
                    mContentFragments.put(position, mContent);
                }
                OtcGoldFragment fragment1 = (OtcGoldFragment) mContentFragments.get(position);
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