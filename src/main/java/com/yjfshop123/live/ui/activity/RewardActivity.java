package com.yjfshop123.live.ui.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.response.PromDataResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.fragment.RewardFragment;
import com.yjfshop123.live.ui.widget.RewardDialogFragment;
import com.yjfshop123.live.ui.widget.RewardDialogFragment2;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;
//推广邀请界面
public class RewardActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private AppBarLayout mAppBarLayout;
    private TextView rewardCount;
    private TextView rewardUserCount;
    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private int mScreenWidth;
    private String mHintContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_reward);
        setHeadLayout();

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        init();
    }

    private void init() {
        TextView tv_title_center = findViewById(R.id.tv_title_center);
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.reward_1);

        FrameLayout rewardFl = findViewById(R.id.reward_fl);
        ViewGroup.LayoutParams params = rewardFl.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (mScreenWidth * 666) / 750;
        rewardFl.setLayoutParams(params);

        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mAppBarLayout = findViewById(R.id.reward_appbarLayout);

        findViewById(R.id.reward_rules).setOnClickListener(this);

        rewardCount = findViewById(R.id.reward_count);
        rewardUserCount = findViewById(R.id.reward_user_count);

        findViewById(R.id.reward_earn_money).setOnClickListener(this);
        findViewById(R.id.reward_earn_money_2).setOnClickListener(this);

        mSlidingTabLayout = findViewById(R.id.reward_nts_sty);
        mViewPager = findViewById(R.id.reward_vp);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    mSwipeRefresh.setEnabled(true);
                } else {
                    mSwipeRefresh.setEnabled(false);
                }
            }
        });

        mContentFragments = new SparseArray<>();
        String[] titles = {getString(R.string.reward_7), getString(R.string.reward_8),
                getString(R.string.reward_9), getString(R.string.reward_10)};
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

        initSwipeRefresh();
        getMyInviteData();

        OKHttpUtils.getInstance().getRequest("app/promotion/getPromData", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    PromDataResponse response = JsonMananger.jsonToBean(result, PromDataResponse.class);
                    link = response.getShare_link().getLink();
                    title = response.getShare_link().getTitle();
                    desc = response.getShare_link().getDesc();
                    icon_link = CommonUtils.getUrl(response.getShare_link().getIcon_url());
                    qr_url = response.getShare_qr().getQr_url();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward_rules:
                if (mHintContent != null){
                    RewardDialogFragment fragment = new RewardDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("hintContent", mHintContent);
                    fragment.setArguments(bundle);
                    fragment.show(getSupportFragmentManager(), "RewardDialogFragment");
                }
                break;
            case R.id.reward_earn_money:
                RewardDialogFragment2 fragment2 = new RewardDialogFragment2();
                fragment2.show(getSupportFragmentManager(), "RewardDialogFragment2");
                break;
            case R.id.reward_earn_money_2:
                Intent intent = new Intent();
                intent.setClass(mContext, TiXianActivity1.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    RewardFragment fragment = (RewardFragment) mContentFragments.get(mPosition);
                    if (fragment != null) {
                        fragment.refresh();
                    }

                    getMyInviteData();
                }
            });
        }
    }

    private void getMyInviteData(){
        OKHttpUtils.getInstance().getRequest("app/promotion/getMyInviteData", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();

                try {
                    JSONObject data = new JSONObject(result);
                    rewardCount.setText(data.getString("total_money"));
                    rewardUserCount.setText(data.getString("total_user"));
                    mHintContent = data.getString("regular_zh");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    if (mContent == null) {
                        mContent = new RewardFragment();
                        mContentFragments.put(POS_0, mContent);
                    }
                    RewardFragment fragment1 = (RewardFragment) mContentFragments.get(POS_0);

                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("TYPE", POS_0);
                    fragment1.setArguments(bundle1);

                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new RewardFragment();
                        mContentFragments.put(POS_1, mContent);
                    }
                    RewardFragment fragment2 = (RewardFragment) mContentFragments.get(POS_1);

                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("TYPE", POS_1);
                    fragment2.setArguments(bundle2);

                    return fragment2;
                case POS_2:
                    if (mContent == null) {
                        mContent = new RewardFragment();
                        mContentFragments.put(POS_2, mContent);
                    }
                    RewardFragment fragment3 = (RewardFragment) mContentFragments.get(POS_2);

                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("TYPE", POS_2);
                    fragment3.setArguments(bundle3);

                    return fragment3;
                case POS_3:
                    if (mContent == null) {
                        mContent = new RewardFragment();
                        mContentFragments.put(POS_3, mContent);
                    }
                    RewardFragment fragment4 = (RewardFragment) mContentFragments.get(POS_3);

                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("TYPE", POS_3);
                    fragment4.setArguments(bundle4);

                    return fragment4;
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;

    private String link;//分享链接
    private String title;//分享标题
    private String desc;//分享描述
    private String icon_link;//分享ICON图
    private String qr_url;//二维码 图片URL

    public void copy() {
        if (link == null){
            NToast.shortToast(this, "复制失败");
            return;
        }
        myClip = ClipData.newPlainText("text", link);
        myClipboard.setPrimaryClip(myClip);
        NToast.shortToast(this, "已复制到剪贴板");
    }

    public void saveCode(){
        if (qr_url == null){
            return;
        }
        Intent intent = new Intent(mContext, ShareActivity.class);
        intent.putExtra("qr_url", qr_url);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    public void showShareDialog() {
        if (link == null){
//            NToast.shortToast(this, "分享失败");
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.share_dialog2, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this, R.style.BottomDialog).create();
        mDialog.show();

        Window window =mDialog.getWindow();
        if (window != null) {
            window.setContentView(view);
            window.setWindowAnimations(R.style.BottomDialog_Animation);
            WindowManager.LayoutParams mParams = window.getAttributes();
            mParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.BOTTOM);
            window.setAttributes(mParams);
        }

        TextView btn_wx = view.findViewById(R.id.btn_share_wx);
        TextView btn_circle = view.findViewById(R.id.btn_share_circle);
        TextView btn_qq = view.findViewById(R.id.btn_share_qq);
        TextView btn_qzone = view.findViewById(R.id.btn_share_qzone);
        TextView btn_wb = view.findViewById(R.id.btn_share_wb);
        TextView btn_cancel = view.findViewById(R.id.btn_share_cancle);

        btn_wx.setOnClickListener(mShareBtnClickListen);
        btn_circle.setOnClickListener(mShareBtnClickListen);
        btn_qq.setOnClickListener(mShareBtnClickListen);
        btn_qzone.setOnClickListener(mShareBtnClickListen);
        btn_wb.setOnClickListener(mShareBtnClickListen);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    private View.OnClickListener mShareBtnClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_share_wx:
                    mShare_meidia = SHARE_MEDIA.WEIXIN;
                    break;
                case R.id.btn_share_circle:
                    mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
                    break;
                case R.id.btn_share_qq:
                    mShare_meidia = SHARE_MEDIA.QQ;
                    break;
                case R.id.btn_share_qzone:
                    mShare_meidia = SHARE_MEDIA.QZONE;
                    break;
                case R.id.btn_share_wb:
                    mShare_meidia = SHARE_MEDIA.SINA;
                    break;
                default:
                    break;
            }

            startShare();
        }
    };

    private void startShare() {
        ShareAction shareAction = new ShareAction(this);
        String shareUrl = link;
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(new UMImage(this, icon_link));
        web.setTitle(title);
        web.setDescription(desc);
//        shareAction.withText(desc);
        shareAction.withMedia(web);
        shareAction.setCallback(umShareListener);
        shareAction.setPlatform(mShare_meidia).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
//            NToast.shortToast(mContext, platform + " 分享成功啦");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            NToast.shortToast(mContext, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            NToast.shortToast(mContext, "分享取消了");
        }
    };

}

