package com.yjfshop123.live.ui.fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.AccountantResponse;
import com.yjfshop123.live.model.EggAccountType;
import com.yjfshop123.live.ui.activity.EggOrderListActivity;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSDateTotalDialog;
import com.yjfshop123.live.utils.DateFormatUtil;

import org.json.JSONException;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yjfshop123.live.model.EggListDataResponse.testData;
import static com.yjfshop123.live.utils.DateFormatUtil.getCurTimeYMDString;

/**
 * 金银臭蛋明细
 */
public class EggOrderListFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    @BindView(R.id.start_time)
    LinearLayout startTime;
    @BindView(R.id.end_time)
    LinearLayout endTime;

    @BindView(R.id.mSlidingTabLayout)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    Unbinder unbinder;
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.end_time_tv)
    TextView endTimeTv;
    public int eggType = -1;//0全部 1金蛋 2银蛋 3臭蛋
    public int accountant = -1;  //0全部  //1收入  2支出
    public int indexPage = 0;
    MyFragmentPagerAdapter fAdapter;
    String[] titles = {""};
    int[] type = {0};
    IOSDateTotalDialog startdialog;
    IOSDateTotalDialog enddialog;
    String startTimeRequest = "";
    String endTimeRequest = getCurTimeYMDString();
    @BindView(R.id.days)
    TextView days;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_egg_list;
    }

    @Override
    protected void initAction() {
        if (eggType == EggOrderListActivity.EGG_GOLD && accountant == EggOrderListActivity.accountant_in) {
            titles = EggAccountType.goldEggAccountInData;
            type = EggAccountType.goldEggAccountInDataType;
        }
        if (eggType == EggOrderListActivity.EGG_GOLD && accountant == EggOrderListActivity.accountant_out) {
            titles = EggAccountType.goldEggAccountOutData;
            type = EggAccountType.goldEggAccountOutDataType;
        }
        if (eggType == EggOrderListActivity.EGG_SILVER && accountant == EggOrderListActivity.accountant_in) {
            titles = EggAccountType.silverEggAccountInData;
            type = EggAccountType.silverEggAccountInDataType;
        }
        if (eggType == EggOrderListActivity.EGG_SILVER && accountant == EggOrderListActivity.accountant_out) {
            titles = EggAccountType.silverEggAccountOutData;
            type = EggAccountType.silverEggAccountOutDataType;
        }
        mContentFragments = new SparseArray<>();
        fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSlidingTabLayout.setViewPager(mViewPager);
        if (eggType == EggOrderListActivity.EGG_BAD || (eggType == EggOrderListActivity.EGG_SILVER && accountant == EggOrderListActivity.accountant_out)) {
            mSlidingTabLayout.setVisibility(View.GONE);
        }
        mViewPager.setCurrentItem(0);

        if (titles.length > 0)
            mViewPager.setOffscreenPageLimit(titles.length - 1);

        days.setText(getString(R.string.select_days, "-"));
    }

    boolean isFirst = true;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }


    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        if (mContent == null) mContent = mContentFragments.get(0);
        if (mContent == null) return;
        if (eggType == -1 || accountant == -1) return;
        if (mViewPager == null || mViewPager.getCurrentItem() >= type.length) return;
        String body = "";
        try {
            /**
             * start_time	T文本	否
             * 2020-09-13
             *
             * 开始日期，默认当天
             *
             * end_time	T文本	否
             * 2020-09-13
             *
             * 结束日期，默认当天
             *
             * type	T文本	是
             * 1
             *
             * 1收入，2支出
             *
             * log_type	T文本	否
             * 0
             *
             * 0全部（默认），10目标奖，11助推奖
             *
             * page	T文本	否	页码
             * 0
             *
             * 查询起始数据，默认1
             *
             * pagesize	T文本	否
             * 20
             *
             * 分页大小，默认20
             */
            //if (eggType == EggOrderListActivity.EGG_GOLD || eggType == EggOrderListActivity.EGG_SILVER) {
            body = new JsonBuilder()

                    .put("start_date", startTimeRequest)
                    .put("end_date", endTimeRequest)
                    .put("page", 1)
                    .put("accountant", accountant)
                    .put("order_type", type[mViewPager.getCurrentItem()])
                    // .put("pagesize", "20")
                    .put("egg_type", eggType)
                    .build();
//            } else
//                body = new JsonBuilder()
//                        .put("start_time", startTimeRequest)
//                        .put("end_time", endTimeRequest)
//                        .put("pageIndex", 1)
//                        .put("type", accountant)
//                        .put("log_type", type[mViewPager.getCurrentItem()])
//                        .build();
        } catch (JSONException e) {
        }
        if (isFromRefresh) {
            showLoading();
        } else {
            if (((EggListByOrderTypeFragment) mContent).mEmptyLayout != null && ((EggListByOrderTypeFragment) mContent).mEmptyLayout.getVisibility() == View.VISIBLE) {

                ((EggListByOrderTypeFragment) mContent).showLoading();
            }
        }

        OKHttpUtils.getInstance().getRequest(getUrl(), body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (isFromRefresh) {
                    hideLoading();
                } else {
                    if (((EggListByOrderTypeFragment) mContent).mEmptyLayout != null && ((EggListByOrderTypeFragment) mContent).mEmptyLayout.getVisibility() == View.VISIBLE) {
                        ((EggListByOrderTypeFragment) mContent).showNetError();
                    }
                }

                //模拟数据
                //((EggListByOrderTypeFragment) mContent).loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                ((EggListByOrderTypeFragment) mContent).page = 1;

                if (isFromRefresh) {
                    hideLoading();
                } else {
                    ((EggListByOrderTypeFragment) mContent).hideLoading();
                }
                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadData(testData);
                ((EggListByOrderTypeFragment) mContent).loadData(result);
            }
        });

    }

    private String getUrl() {
        String url = "app/egg/tantDetail";
//        if (eggType == EggOrderListActivity.EGG_GOLD || eggType == EggOrderListActivity.EGG_SILVER) {
//            url = "app/silver_egg/silverEggDetail";
//        }
        return url;
    }

    /**
     * 获取数据
     */
    public void getMoreData(int page) {
        if (eggType == -1 || accountant == -1) return;
        if (mViewPager == null || mViewPager.getCurrentItem() >= type.length) return;
        String body = "";
        try {
            //  if (eggType == EggOrderListActivity.EGG_GOLD || eggType == EggOrderListActivity.EGG_SILVER) {
            body = new JsonBuilder()
                    .put("start_date", startTimeRequest)
                    .put("end_date", endTimeRequest)
                    .put("page", page)
                    .put("accountant", accountant)
                    .put("order_type", type[mViewPager.getCurrentItem()])
                    // .put("pagesize", "20")
                    .put("egg_type", eggType).build();
//            } else
//                body = new JsonBuilder()
//                        .put("start_time", startTimeRequest)
//                        .put("end_time", endTimeRequest)
//                        .put("pageIndex", page)
//                        .put("type", accountant)
//                        .put("log_type", type[mViewPager.getCurrentItem()])
//                        .build();

        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest(getUrl(), body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //模拟数据
                // ((EggListByOrderTypeFragment) mContent).loadMoreData(testData);
                ((EggListByOrderTypeFragment) mContent).page--;
                ((EggListByOrderTypeFragment) mContent).loadMoreOver();
            }

            @Override
            public void onSuccess(String result) {
                ((EggListByOrderTypeFragment) mContent).loadMoreData(result);
                ((EggListByOrderTypeFragment) mContent).loadMoreOver();
                //模拟数据
                //  ((EggListByOrderTypeFragment) mContent).loadMoreData(testData);

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    boolean isSetStart = false;
    boolean isSetEnd = false;


    @OnClick({R.id.start_time, R.id.end_time, R.id.confir})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_time:
                if (startdialog == null) {
                    startdialog = new IOSDateTotalDialog(Objects.requireNonNull(getContext())).builder();
                    startdialog.setTitle(getString(R.string.select_date));
                    startdialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    startdialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isSetStart = true;
                            startTimeTv.setText(startdialog.getDateStr());
                            if (!TextUtils.isEmpty(startdialog.getDateStr()) && enddialog != null && !TextUtils.isEmpty(enddialog.getDateStr()))
                                days.setText(getString(R.string.select_days, (DateFormatUtil.dateDiff(startdialog.getDateStr(), enddialog.getDateStr()) + 1) + ""));

                        }
                    });
                }
                startdialog.show();
                break;
            case R.id.end_time:
                if (enddialog == null) {
                    enddialog = new IOSDateTotalDialog(Objects.requireNonNull(getContext())).builder();
                    enddialog.setTitle(getString(R.string.select_date));
                    enddialog.setNegativeButton(getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    enddialog.setPositiveButton(getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isSetEnd = true;
                            endTimeTv.setText(enddialog.getDateStr());
                            if (startdialog != null && !TextUtils.isEmpty(startdialog.getDateStr()) && enddialog != null && !TextUtils.isEmpty(enddialog.getDateStr()))
                                days.setText(getString(R.string.select_days, (DateFormatUtil.dateDiff(startdialog.getDateStr(), enddialog.getDateStr()) + 1) + ""));

                        }
                    });
                }
                enddialog.show();
                break;
            case R.id.confir:
                if (isSetEnd) endTimeRequest = enddialog.getDateStr();
                if (isSetStart) startTimeRequest = startdialog.getDateStr();
                long startLong = DateFormatUtil.dataOne(startTimeRequest);
                long endLong = DateFormatUtil.dataOne(endTimeRequest);
                if (startLong > endLong) {
                    //如果开始时间大于结束时间，则第一个开始时间的日期设置为请求参数end_date的值
                    backup = startTimeRequest;
                    startTimeRequest = endTimeRequest;
                    endTimeRequest = backup;
                }
                getData(false);
                break;
        }
    }

    String backup;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    private Fragment mContentInit;

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mContent = mContentFragments.get(i);
        if (((EggListByOrderTypeFragment) mContent).mEmptyLayout != null
                && ((EggListByOrderTypeFragment) mContent).mEmptyLayout.getVisibility() == View.VISIBLE)
            getData(false);
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
            mContentInit = mContentFragments.get(position);

            if (mContentInit == null) {
                mContentInit = new EggListByOrderTypeFragment();
                ((EggListByOrderTypeFragment) mContentInit).setFragment(EggOrderListFragment.this);
                ((EggListByOrderTypeFragment) mContentInit).setIndex(position);
                ((EggListByOrderTypeFragment) mContentInit).setAccount(accountant);
                mContentFragments.put(position, mContentInit);
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
