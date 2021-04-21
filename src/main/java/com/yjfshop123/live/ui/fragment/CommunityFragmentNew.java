package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.EventModel2;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.ui.activity.BaseActivityH;
import com.yjfshop123.live.ui.activity.CommunityMessageActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.OChatActivity;
import com.yjfshop123.live.ui.activity.SearchCommunityActivity;
import com.yjfshop123.live.ui.activity.SheQuPublishContentActivity;
import com.yjfshop123.live.ui.activity.TaskLobbyActivity;
import com.yjfshop123.live.ui.widget.CommunityPopWindow;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.dialog.GongYueDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CommunityFragmentNew extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener, GongYueDialog.GongYueInterface {

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @BindView(R.id.community_nts_sty)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.community_vp)
    ViewPager mViewPager;
    @BindView(R.id.community_release)
    ImageView communityRelease;
    //    @BindView(R.id.community_appbarLayout)
//    AppBarLayout mAppBarLayout;
    @BindView(R.id.community_unread_num)
    TextView community_unread_num;
    //    @BindView(R.id.community_qiuliao)
//    Button community_qiuliao;
//    @BindView(R.id.community_qiangliao)
//    Button community_qiangliao;
//    @BindView(R.id.community_qq_fl)
//    FrameLayout community_qq_fl;
//    @BindView(R.id.community_xchat_tv)
//    TextView community_xchat_tv;
    @BindView(R.id.top)
    View top;
    //    @BindView(R.id.fabu)
//    LinearLayout fabu;
    private GongYueDialog gyd;
    private CommunityPopWindow communityPopWindow;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;

    private static int community_bannerHeight = 0;
    //社区banner图尺寸比例
    private static final int community_img_width = 1067;
    private static final int community_img_height = 330;

    private static int community_bannerWidth2 = 0;
    private static int community_bannerHeight2 = 0;

    private BaseActivityH activityH;

    private ImageView mIv;

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels - CommonUtils.dip2px(mContext, 20);
    }

    private int getScreenWidth2() {
        return getResources().getDisplayMetrics().widthPixels - CommonUtils.dip2px(mContext, 30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BaseActivityH) {
            activityH = (BaseActivityH) getActivity();
        }

        if (community_bannerHeight == 0) {
            community_bannerHeight = (getScreenWidth() * community_img_height) / community_img_width;
        }

        if (community_bannerWidth2 == 0) {
            community_bannerWidth2 = getScreenWidth2() / 2;
        }
        if (community_bannerHeight2 == 0) {
            community_bannerHeight2 = (community_bannerWidth2 * 75) / 165;
        }

        EventBus.getDefault().register(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void finishRefresh_(String str) {
        if (str.equals("finishRefresh")) {
            finishRefresh();
        }
    }

    private long readCount_like = 0;
    private long readCount_reply = 0;

    @Subscriber(tag = Config.EVENT_SHEQU)
    public void onEventMainThread(EventModel2 eventModel2) {
        if (eventModel2.getType().equals("forum_notice")) {
            readCount_like = eventModel2.getReadCount_like();
            readCount_reply = eventModel2.getReadCount_reply();
            dataSetChanged();
        }
    }

    private void dataSetChanged() {
        long unRead = readCount_like + readCount_reply;
        if (unRead <= 0) {
            community_unread_num.setVisibility(View.INVISIBLE);
        } else {
            community_unread_num.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10) {
                community_unread_num.setBackgroundResource(R.drawable.point1);
            } else {
                community_unread_num.setBackgroundResource(R.drawable.point2);
                if (unRead > 99) {
                    unReadStr = "99+";
                }
            }
            community_unread_num.setText(unReadStr);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_community_new;
    }

    @Override
    protected void initAction() {
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) top.getLayoutParams();
        //获取当前控件的布局对象
        params1.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params1.width = MATCH_PARENT;
        top.setLayoutParams(params1);
//        if (ActivityUtils.IS1V1()){
//            community_qq_fl.setVisibility(View.VISIBLE);
//        }else {
//            community_qq_fl.setVisibility(View.GONE);
//        }

        mContentFragments = new SparseArray<>();
        String[] titles = getResources().getStringArray(R.array.comm_titles_new);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

//        mIv = view.findViewById(R.id.community_banner_item_iv);
//        ViewGroup.LayoutParams params = mIv.getLayoutParams();
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.height = community_bannerHeight;
//        mIv.setLayoutParams(params);
//        mIv.setVisibility(View.GONE);

//        ViewGroup.LayoutParams params2 = community_qiuliao.getLayoutParams();
//        params2.width = community_bannerWidth2;
//        params2.height = community_bannerHeight2;
//        community_qiuliao.setLayoutParams(params2);
//
//        ViewGroup.LayoutParams params3 = community_qiangliao.getLayoutParams();
//        params3.width = community_bannerWidth2;
//        params3.height = community_bannerHeight2;
//        community_qiangliao.setLayoutParams(params3);

        EventModel2 eventModel2 = activityH.getEventModel2();
        if (eventModel2 != null) {
            readCount_like = eventModel2.getReadCount_like();
            readCount_reply = eventModel2.getReadCount_reply();
            dataSetChanged();
        }
    }

    @Override
    protected void initEvent() {
//        fabu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SheQuPublishContentActivity.class);
//                intent.putExtra("type", 1);
//                getActivity().startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
//            }
//        });
        view.findViewById(R.id.community_search).setOnClickListener(this);
        view.findViewById(R.id.community_reply).setOnClickListener(this);
        communityRelease.setOnClickListener(this);

//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (verticalOffset >= 0) {
//                    mSwipeRefresh.setEnabled(true);
//                } else {
//                    mSwipeRefresh.setEnabled(false);
//                }
//            }
//        });

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (isRefresh) {
            adForumIndexTop();
            switch (mPosition) {
                case POS_0:

                    EventBus.getDefault().post("100001", Config.EVENT_START);
                    break;
                case POS_1:

                    EventBus.getDefault().post("100002", Config.EVENT_START);
                    break;
                case POS_2:

                    EventBus.getDefault().post("100003", Config.EVENT_START);
                    break;
                default:
                    finishRefresh();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
    }

    private boolean isLogin() {
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (!isLogin()) {
            return;
        }

        switch (v.getId()) {
            case R.id.community_search:
                startActivity(new Intent(mContext, SearchCommunityActivity.class).putExtra("TYPE", "forum"));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            case R.id.community_reply:
                Intent intent = new Intent(mContext, CommunityMessageActivity.class);
                intent.putExtra("readCount_like", readCount_like);
                intent.putExtra("readCount_reply", readCount_reply);
                intent.putExtra("current_item", 0);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            case R.id.community_release:
                communityPopWindow = new CommunityPopWindow(getActivity(), new CommunityPopWindow.CPWClickListener() {
                    @Override
                    public void onCPWClick_1() {
                        Intent intent = new Intent(getActivity(), SheQuPublishContentActivity.class);
                        intent.putExtra("type", 1);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }

                    @Override
                    public void onCPWClick_2() {
                        Intent intent = new Intent(getActivity(), SheQuPublishContentActivity.class);
                        intent.putExtra("type", 2);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }

                    @Override
                    public void onCPWClick_3() {
                        Intent intent = new Intent(getActivity(), SheQuPublishContentActivity.class);
                        intent.putExtra("type", 3);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }

                    @Override
                    public void onCPWClick_4() {
                        Intent intent = new Intent(getActivity(), SheQuPublishContentActivity.class);
                        intent.putExtra("type", 4);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }
                });

                boolean isRead = UserInfoUtil.getIsRead();
                gyd = new GongYueDialog(getActivity()).buidle();
                gyd.setGongYueInterface(this);
                if (isRead) {
                    communityPopWindow.showPopupWindow(communityRelease);
                } else {
                    gyd.show();
                }
                break;
            case R.id.community_qiangliao:
                startActivity(new Intent(mContext, OChatActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            case R.id.community_qiuliao:
                startActivity(new Intent(mContext, TaskLobbyActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            default:
                break;
        }
    }

    @Override
    public void okButton(View view) {
        gyd.dissmiss();
        UserInfoUtil.setIsRead(true);
        communityPopWindow.showPopupWindow(communityRelease);
    }

    @Override
    public void textClick(View view) {
        if (!TextUtils.isEmpty(link)) {
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getContext().getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
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
            switch (position) {
                case POS_0:
//                    if (mContent == null) {
//                        mContent = new VideoRecyclerViewFragment_1();
//                        mContentFragments.put(POS_0, mContent);
//                    }
//                    VideoRecyclerViewFragment_1 fragment1 = (VideoRecyclerViewFragment_1) mContentFragments.get(POS_0);

                    if (mContent == null) {
                        mContent = new VideoRecyclerViewFragment_1_new();
                        mContentFragments.put(POS_0, mContent);
                    }
                    VideoRecyclerViewFragment_1_new fragment1 = (VideoRecyclerViewFragment_1_new) mContentFragments.get(POS_0);
                   // fragment1.setmSwipeRefreshNew(mSwipeRefresh);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new VideoRecyclerViewFragment_2_new();
                        mContentFragments.put(POS_1, mContent);
                    }
                    VideoRecyclerViewFragment_2_new fragment2 = (VideoRecyclerViewFragment_2_new) mContentFragments.get(POS_1);
                    return fragment2;
                case POS_2:
                    if (mContent == null) {
                        mContent = new VideoRecyclerViewFragment_3_new();
                        mContentFragments.put(POS_2, mContent);
                    }
                    VideoRecyclerViewFragment_3_new fragment3 = (VideoRecyclerViewFragment_3_new) mContentFragments.get(POS_2);
                    return fragment3;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    private int mPosition = 0;

    @Override
    public void onPageSelected(int position) {
        mPosition = position;

        switch (position) {
            case POS_0:
                EventBus.getDefault().post("100002_2", Config.EVENT_START);
                EventBus.getDefault().post("100003_3", Config.EVENT_START);
                break;
            case POS_1:
                EventBus.getDefault().post("100001_1", Config.EVENT_START);
                EventBus.getDefault().post("100003_3", Config.EVENT_START);
                break;
            case POS_2:
                EventBus.getDefault().post("100001_1", Config.EVENT_START);
                EventBus.getDefault().post("100002_2", Config.EVENT_START);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private String link = "";

    private void adForumIndexTop() {
        OKHttpUtils.getInstance().getRequest("app/ad/adForumIndexTop", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONObject ad = data.getJSONObject("ad");
                    String img = ad.getString("img");
                    link = ad.getString("link");

                    if (!TextUtils.isEmpty(img)) {
                        mIv.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .load(CommonUtils.getUrl(img))
                                .into(mIv);

                        mIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(link)) {
                                    Intent intent = new Intent("io.xchat.intent.action.webview");
                                    intent.setPackage(getContext().getPackageName());
                                    intent.putExtra("url", link);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        adForumIndexTop();
    }

}
