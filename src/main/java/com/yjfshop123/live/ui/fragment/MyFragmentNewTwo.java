package com.yjfshop123.live.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.PriceResponse;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.model.UserInfoResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.otc.MainOtcActivity;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.ziying.ui.NewShopListActivity;
import com.yjfshop123.live.ui.activity.ActivityNumActivity;
import com.yjfshop123.live.ui.activity.BadEggActivity;
import com.yjfshop123.live.ui.activity.GoldEggActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.InviteFriendActivity;
import com.yjfshop123.live.ui.activity.LevelActivity;
import com.yjfshop123.live.ui.activity.MyMoreActivity;
import com.yjfshop123.live.ui.activity.OrderListActivity;
import com.yjfshop123.live.ui.activity.PersonalInformationActivity;
import com.yjfshop123.live.ui.activity.ShouZhiAtivity;
import com.yjfshop123.live.ui.activity.TargetReciveDetailActivity;
import com.yjfshop123.live.ui.activity.TargetRewardActivity;
import com.yjfshop123.live.ui.activity.TaskNewCenterActivity;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.activity.ZhutuiActivity;
import com.yjfshop123.live.ui.activity.team.MyTeamActivity;
import com.yjfshop123.live.ui.activity.yinegg.NewSilverEggActivity;
import com.yjfshop123.live.ui.widget.ChoujiangFragment;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.ui.widget.ShowTargetDialog;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.InstallExchangeDialog;
import com.yjfshop123.live.xuanpin.ui.XuanPinActivity;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MyFragmentNewTwo extends BaseFragment {
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.income)
    LinearLayout income;
    @BindView(R.id.autorImg)
    CircleImageView autorImg;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.vip_level)
    TextView vipLevel;
    @BindView(R.id.upgrade)
    RelativeLayout upgrade;
    @BindView(R.id.user_account)
    TextView userAccount;
    @BindView(R.id.activity_num)
    TextView activity_num;
    @BindView(R.id.gold_egg_num)
    TextView goldEggNum;

    @BindView(R.id.yin_egg_num)
    TextView yinEggNum;

    @BindView(R.id.chou_num)
    TextView chouNum;
    @BindView(R.id.tip_update_tx)
    TextView tip_update_tx;

    @BindView(R.id.tip_update)
    LinearLayout tip_update;

    @BindView(R.id.below)
    LinearLayout below;

    @BindView(R.id.recive_btn)
    LinearLayout reciveBtn;
    @BindView(R.id.reward_des)
    TextView rewardDes;
    @BindView(R.id.target_reward_ll)
    LinearLayout targetRewardLl;


    @BindView(R.id.tab_xunzhang)
    SelectBtnView mSlidingTabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout swipeRefresh;
    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.tartget_need_activity_num)
    TextView tartgetNeedActivityNum;
    @BindView(R.id.tartget_current_activity_num)
    TextView tartget_current_activity_num;
    @BindView(R.id.recive_progress)
    TextView recive_progress;
    @BindView(R.id.recive_btn_two)
    TextView recive_btn_two;

    @BindView(R.id.user_id)
    TextView userId;

    @BindView(R.id.xuanppin_center)
    LinearLayout xuanppin_center;
    @BindView(R.id.choujiang_tips)
    LinearLayout choujiangTips;
    @BindView(R.id.msg_count)
    TextView msgCount;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    float count;
    float targetProgress = 0;
    boolean isrecive = true;
    String tartgetReward;
    TargetRewardResponse data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_new_two;
    }

    @Override
    protected void initAction() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewStatusBar.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        viewStatusBar.setLayoutParams(params);
//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) choujiangTips.getLayoutParams();
//        //获取当前控件的布局对象
//
//        params1.width = SystemUtils.getScreenWidth(getContext())-SystemUtils.dip2px(getContext(),75);
//        params1.height = params1.width *336/550;//设置当前控件布局的高度
//        choujiangTips.setLayoutParams(params1);
        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.xunzhang_title);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        //mViewPager.addOnPageChangeListener(this);
        //mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0 && mContentFragments.get(i) != null)
                    ((MyXunzhangFragment) mContentFragments.get(i)).getData(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setCurrentItem(0);
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
        mSlidingTabLayout.setViewPager(mViewPager);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefresh.setEnabled(true);
                } else {
                    swipeRefresh.setEnabled(false);
                }
            }
        });
//        coordinatorLayout.setOnInterceptTouchListener(new CustomCoordinatorLayout.OnInterceptTouchListener() {
//            @Override
//            public void onIntercept() {
//                CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams)appbar.getLayoutParams()).getBehavior();
//                if(behavior instanceof AppBarLayoutBehavior) {
//                    //fix 解决与RecyclerView联合使用的回弹问题
////                    if(!fragmentList.isEmpty() && viewPager.getCurrentItem() >= 0 && viewPager.getCurrentItem() < fragmentList.size()) {
////                        ((ChildFragment)fragmentList.get(viewPager.getCurrentItem())).stopNestedScrolling();
////                    }
//                    //fix 解决动画抖动
//                    ((AppBarLayoutBehavior) behavior).stopFling();
//                }
//
//            }
//        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getUserInfoData();
        if (mViewPager != null) {
            if (mViewPager.getCurrentItem() == 0) {
                if (mContentFragments.get(0) != null)
                    ((MyXunzhangFragment) mContentFragments.get(0)).getData(isRefresh);
            }
            if (mViewPager.getCurrentItem() == 1) {
                if (mContentFragments.get(1) != null)
                    ((XunZhangConfigFragment) mContentFragments.get(1)).getData(isRefresh);
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        NLog.d("我的页面setUserVisibleHint", isVisibleToUser + "");
        if (isResume && isVisibleToUser && xuanppin_center != null) {
            showYindao();
        }
    }


    private static final int POS_0 = 0;
    private static final int POS_1 = 1;


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

    @OnClick({R.id.choujiang_tips, R.id.tip_update, R.id.recive_btn, R.id.invite_friend, R.id.task_center, R.id.xuanppin_center, R.id.help_reward, R.id.trade_list, R.id.more, R.id.autorImg, R.id.activity_num, R.id.income, R.id.upgrade, R.id.my_gold_egg, R.id.my_yin_egg, R.id.my_bad_egg, R.id.my_team, R.id.buy_gold, R.id.target_reward_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.choujiang_tips:
                ChoujiangFragment fragmentRotary = new ChoujiangFragment();
                fragmentRotary.setHomeActivity((HomeActivity) getActivity());
                fragmentRotary.setmyFragmentNewTwo(this);
                fragmentRotary.show(getChildFragmentManager(), "ChoujiangFragment");
                break;
            case R.id.tip_update:
            case R.id.upgrade:
                startActivity(new Intent(getActivity(), LevelActivity.class));
                break;
            case R.id.invite_friend:
//                Intent invite = new Intent();
//                invite.setClass(getActivity(), RewardActivity.class);
//                getActivity().startActivity(invite);

                Intent invite = new Intent();
                invite.setClass(getActivity(), InviteFriendActivity.class);
                getActivity().startActivity(invite);

                break;
            case R.id.task_center:
//                Intent task = new Intent();
//                task.setClass(getActivity(), TaskNewCenterActivity.class);
//                getActivity().startActivityForResult(task, 10001);

                LoadDialog.show(getContext());
                OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(getContext());
                        Intent task = new Intent();
                        task.setClass(getActivity(), TaskNewCenterActivity.class);
                        getActivity().startActivityForResult(task, 10001);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(getContext());
                        try {
                            if (TextUtils.isEmpty(result)) {
                                Intent task = new Intent();
                                task.setClass(getActivity(), TaskNewCenterActivity.class);
                                getActivity().startActivityForResult(task, 10001);
                                return;
                            }
                            UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                            if (data.jump != null) {
                                if (data.jump.task_is_h5 == 1) {
                                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                    if (!TextUtils.isEmpty(data.jump.task_h5)) {
                                        intent.putExtra("url", data.jump.task_h5);
                                    }
                                    startActivity(intent);
                                } else {
                                    Intent task = new Intent();
                                    task.setClass(getActivity(), TaskNewCenterActivity.class);
                                    getActivity().startActivityForResult(task, 10001);
                                }
                            } else {
                                Intent task = new Intent();
                                task.setClass(getActivity(), TaskNewCenterActivity.class);
                                getActivity().startActivityForResult(task, 10001);
                            }
                        } catch (Exception e) {
                            Intent task = new Intent();
                            task.setClass(getActivity(), TaskNewCenterActivity.class);
                            getActivity().startActivityForResult(task, 10001);
                        }
                    }
                });



                break;
            case R.id.xuanppin_center:
//                if (CheckInstalledUtil.checkAppInstalled(getContext(), Const.exchangePkg)) {
//                    CheckInstalledUtil.openPackage(getContext(), Const.exchangePkg);
//                } else {
//                    ((HomeActivity) getActivity()).onCheckIsInstallExchange();
//                }
//                Intent download = new Intent();
//                download.setClass(getActivity(), ExchangeDownloadActivity.class);
//                getActivity().startActivity(download);

                Intent xuanpin = new Intent();
                xuanpin.setClass(getActivity(), XuanPinActivity.class);
                getActivity().startActivity(xuanpin);
                break;
            case R.id.help_reward:
                Intent zhutui = new Intent();
                zhutui.setClass(getActivity(), ZhutuiActivity.class);
                getActivity().startActivity(zhutui);
                break;
            case R.id.trade_list:
                Intent trade = new Intent();
                trade.setClass(getActivity(), OrderListActivity.class);
                getActivity().startActivity(trade);

//                Intent trade = new Intent();
//                trade.setClass(getActivity(), ShopSActivity.class);
//                getActivity().startActivity(trade);
                break;
            case R.id.more:
//                Intent intentMore = new Intent();
//                intentMore.setClass(getActivity(), SettingActivity.class);
//                getActivity().startActivity(intentMore);
                Intent intentMore = new Intent();
                intentMore.setClass(getActivity(), MyMoreActivity.class);
                getActivity().startActivityForResult(intentMore, 10002);

//                MyMoreDialogFragment dialogFragment = new MyMoreDialogFragment();
//                dialogFragment.setData(userData);
//                dialogFragment.show(getChildFragmentManager(), "MyMoreDialogFragment");
                break;
            case R.id.autorImg:
                //ActivityUtils.startUserHome(mContext, UserInfoUtil.getMiTencentId());
                Intent intentHead = new Intent();
                intentHead.setClass(getActivity(), PersonalInformationActivity.class);
                getActivity().startActivity(intentHead);
                break;
            case R.id.activity_num:
                startActivity(new Intent(getActivity(), ActivityNumActivity.class));
                break;
            case R.id.income:
                startActivity(new Intent(getActivity(), ShouZhiAtivity.class));
                break;
            case R.id.my_gold_egg:
                startActivity(new Intent(getActivity(), GoldEggActivity.class));
                break;
            case R.id.my_yin_egg:
                //  startActivity(new Intent(getActivity(), SilverEggActivity.class));
                startActivity(new Intent(getActivity(), NewSilverEggActivity.class));
                break;
            case R.id.my_bad_egg:
                startActivity(new Intent(getActivity(), BadEggActivity.class));
                break;
            case R.id.my_team:

                Intent intent = new Intent(getActivity(), MyTeamActivity.class);
                intent.putExtra("user_name", username.getText().toString());
                intent.putExtra("vip_level", TextUtils.isEmpty(vip_level) ? "0" : vip_level);
                startActivity(intent);
                break;

            case R.id.recive_btn:
                if (data != null && targetProgress >= 100 && !isrecive) {
                    ShowTargetDialog dialogFragment = new ShowTargetDialog();
                    dialogFragment.setTitle(data.target_reward_des);
                    dialogFragment.setLogo(data.target_reward_icon);
                    dialogFragment.isRealReward(data.is_real_reward);
                    dialogFragment.setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data != null && data.is_real_reward) {
                                Intent intent = new Intent(getContext(), TargetReciveDetailActivity.class);
                                startActivity(intent);
                                return;
                            }
                            receiveTargetReward();
                        }
                    });

                    dialogFragment.show(getChildFragmentManager(), "ShowTargetDialog");

                }
                if (data != null && targetProgress < 100) {
                    NToast.shortToast(getActivity(), getString(R.string.haiweiwancheng));
                }

                break;
            case R.id.target_reward_ll:
                startActivity(new Intent(getActivity(), TargetRewardActivity.class));
                break;
            case R.id.buy_gold:
                startActivity(new Intent(getActivity(), MainOtcActivity.class));
                break;
        }
    }

    InstallExchangeDialog dialog;

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
                        mContent = new MyXunzhangFragment();
                        ((MyXunzhangFragment) mContent).setFragment(MyFragmentNewTwo.this);

                        mContentFragments.put(POS_0, mContent);
                    }
                    MyXunzhangFragment fragment1 = (MyXunzhangFragment) mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new XunZhangConfigFragment();
                        ((XunZhangConfigFragment) mContent).setFragment(MyFragmentNewTwo.this);
                        mContentFragments.put(POS_1, mContent);
                    }
                    XunZhangConfigFragment fragment2 = (XunZhangConfigFragment) mContentFragments.get(POS_1);
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

    boolean isResume = false;

    @Override
    public void onResume() {
        super.onResume();
        getUserInfoData();

        if (!isResume && getUserVisibleHint()) {
            isResume = true;
            showYindao();
        }
    }

    private void showYindao() {
//        NewbieGuide.with(this)
//                .setLabel("guide1").setOnGuideChangedListener(new OnGuideChangedListener() {
//            @Override
//            public void onShowed(Controller controller) {
//
//            }
//
//            @Override
//            public void onRemoved(Controller controller) {
//                Intent xuanpin = new Intent();
//                xuanpin.setClass(getActivity(), XuanPinActivity.class);
//                getActivity().startActivity(xuanpin);
//            }
//        }).alwaysShow(true)//总是显示，调试时可以打开
//                .addGuidePage(GuidePage.newInstance()
//                        .addHighLight(xuanppin_center)
//                        //.setBackgroundColor(getResources().getColor(R.color.color_4D000000))
//                        .addHighLight(new RectF(SystemUtils.getScreenWidth(getContext()) * 4 / 5, SystemUtils.getScreenHeight(getContext(), false) - SystemUtils.dip2px(getContext(), 52), SystemUtils.getScreenWidth(getContext()), SystemUtils.getScreenHeight(getContext(), false)))
//                        .setLayoutRes(R.layout.item_my_yindao, R.id.click_btn).setEverywhereCancelable(false).setIsNeedJIsuanPostion(true))
//                .show();
    }

    public void getUserInfoData() {
        if (!TextUtils.isEmpty(UserInfoUtil.getAvatar()))

            Glide.with(mContext)
                    .load(CommonUtils.getUrl(UserInfoUtil.getAvatar()))
                    .into(autorImg);
        username.setText(UserInfoUtil.getName());
        userAccount.setText(getString(R.string.account) + UserInfoUtil.getPhoneNumber());
        getUserInfo();
        loadChoujiangData();
        getTargetReward();
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
                int total = data.c2c_order_running_nums + data.otc_order_running_nums;
                if (total > 0) {
                    msgCount.setText(total > 99 ? "99+" : total + "");
                    msgCount.setVisibility(View.VISIBLE);
                } else {
                    msgCount.setVisibility(View.INVISIBLE);
                }
                if (!data.can_selectt_prize) {
                    tip_update.setVisibility(View.VISIBLE);
                    tip_update_tx.setText(data.hint1);
                } else tip_update.setVisibility(View.GONE);

            }
        });
    }

    public void choujiangSuccess() {
        getTargetReward();
        choujiangTips.setVisibility(View.GONE);
    }

    public void loadChoujiangData() {

        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

        // LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result)) return;
                    PriceResponse mResponse = JsonMananger.jsonToBean(result, PriceResponse.class);
                    int size = mResponse.list.size();
                    if (size > 0) {
                        choujiangTips.setVisibility(View.VISIBLE);
                    } else {
                        choujiangTips.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    LoadDialog.dismiss(getActivity());
                }
            }
        });
    }

    private void getUserInfo() {
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                initDatas(result);
            }
        });
    }

    private void receiveTargetReward() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", data.id)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getPrize", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(getActivity(), errInfo);
                LoadDialog.dismiss(getActivity());
            }

            @Override
            public void onSuccess(String result) {
                getTargetReward();
                LoadDialog.dismiss(getActivity());
                DialogUitl.showSimpleHintDialog(getActivity(), "", getString(R.string.confirm),
                        "",
                        getString(R.string.gongxi_lingqu_success) + data.target_reward_value,
                        true,
                        false,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                            }
                        });
//                NToast.shortToast(TargetRewardActivity.this, getString(R.string.gongxi_lingqu_success) + data.target_reward_value_unit + data.target_reward_value);
//                loadData();
            }
        });
    }

    private void getTargetReward() {
        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                //npb.setProgress(targetProgress);
            }

            @Override
            public void onSuccess(final String result) {
                //  if (targetRewardLl.getVisibility() == View.GONE) {
                targetRewardLl.setVisibility(View.VISIBLE);
                //   getTargetReward();
                // } else {

                initTargetRewardDatas(result);

                // }
            }
        });
    }

    Handler handler = new Handler();

    UserInfoResponse userData;

    private void initDatas(String result) {
        if (result == null) {
            return;
        }


        UserInfoResponse data = new Gson().fromJson(result, UserInfoResponse.class);
        userData = data;
        if (!TextUtils.isEmpty(data.avatar)) {
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(data.avatar))
                    .into(autorImg);
        }
        username.setText(data.user_nickname);


        UserInfoUtil.setUserInfo(
                data.sex,
                data.daren_status,
                data.is_vip,
                data.speech_introduction,
                data.auth_status,
                data.province_name + "," + data.city_name + "," + data.district_name,
                data.tags,
                data.age,
                data.signature);
        UserInfoUtil.setInviteCode(data.invite_code);
        partner_status = data.partner_status;
        guildStatus = data.guild_status;
        personActivityNum = data.person_activity_num;
        vip_level = data.vip_level;


        vipLevel.setText(data.level_name + data.level_title);
        activity_num.setText(getString(R.string.active_num_title) + personActivityNum);
        gold_egg = data.gold_egg;
        silver_egg = data.silver_egg;
        bad_egg = data.bad_egg;
        goldEggNum.setText(NumUtil.dealNum(gold_egg));
        yinEggNum.setText(NumUtil.dealNum(silver_egg));
        chouNum.setText(NumUtil.dealNum(bad_egg));
        userId.setText("ID:" + data.user_id);
    }


    private void initTargetRewardDatas(String result) {
        if (TextUtils.isEmpty(result)) {
            targetRewardLl.setVisibility(View.GONE);
            return;
        }


        data = new Gson().fromJson(result, TargetRewardResponse.class);

        tartgetReward = data.target_reward_value;
        count = data.target_reward_count;
        targetProgress = data.target_reward_progress;
        isrecive = data.target_reward_is_get;
        tartget_current_activity_num.setText(data.team_activity_num);
        recive_progress.setText(getString(R.string.has_complete) + targetProgress + "%");
        tartgetNeedActivityNum.setText(NumUtil.clearZero(data.target_reward_need_activity_num));
        if (TextUtils.isEmpty(data.target_reward)) {
            targetRewardLl.setVisibility(View.GONE);
        }
        reciveBtn.setEnabled(!isrecive);
        recive_btn_two.setText(targetProgress >= 100 && isrecive ? getString(R.string.yilingqu) : getString(R.string.receive_reward));

        rewardDes.setText(data.target_reward_value);
    }

    private int partner_status;
    private int guildStatus;
    private int personActivityNum;
    private String vip_level = "0";
    private String gold_egg;
    private String silver_egg;
    private String bad_egg;
}
