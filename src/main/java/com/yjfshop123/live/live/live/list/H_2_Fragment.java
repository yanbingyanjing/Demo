package com.yjfshop123.live.live.live.list;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.response.ChannelListResponse;
import com.yjfshop123.live.model.ModelImg;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.BannerResponse;
import com.yjfshop123.live.net.response.NoticeResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.MarqueeTextView;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.CustPagerTransformer;
import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class H_2_Fragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.live_psts)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.h_vp)
    ViewPager mViewPager;
    @BindView(R.id.live_ll)
    LinearLayout mLiveLl;

    @BindView(R.id.live_list_notice_fl)
    FrameLayout live_list_notice_fl;
    @BindView(R.id.live_list_notice_content)
    MarqueeTextView live_list_notice_content;
    @BindView(R.id.live_list_notice_close)
    ImageView live_list_notice_close;

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.indicator_ll)
    LinearLayout mPointContainerLl;

    @BindView(R.id.live_home_appbarLayout)
    AppBarLayout mAppBarLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    private boolean isHB = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isHB = bundle.getBoolean("ISHB");
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_h_2;
    }

    @Override
    protected void initAction() {
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (getScreenWidth() * (0.35));
        viewPager.setLayoutParams(params);
        viewPager.setPageTransformer(false, new CustPagerTransformer(mContext));
    }

    @Override
    protected void initEvent() {
        live_list_notice_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                live_list_notice_fl.setVisibility(View.GONE);
            }
        });

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
    }

    @Override
    protected void initData() {
        super.initData();
        mContentFragments = new SparseArray<>();

        OKHttpUtils.getInstance().getRequest("app/live/getChannelList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                mLiveLl.setVisibility(View.GONE);
                loadTitle();
            }
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    if (mList.size() > 0){
                        mList.clear();
                    }
                    try{
                        ChannelListResponse.ChannelListBean channelListBean = new ChannelListResponse.ChannelListBean();
                        channelListBean.setName(getString(R.string.all_));
                        channelListBean.setId(0);
                        mList.add(channelListBean);
                        ChannelListResponse channelListResponse = JsonMananger.jsonToBean(result, ChannelListResponse.class);
                        if (channelListResponse.getChannel_list().size() > 0){
                            mList.addAll(channelListResponse.getChannel_list());
                            mLiveLl.setVisibility(View.VISIBLE);
                        }else {
                            mLiveLl.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        mLiveLl.setVisibility(View.GONE);
                    }
                    loadTitle();
                }
            }
        });

        loadData();
    }

    private void loadData(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("scene_id", 4)//场景 1：首页顶部广告 2：社区顶部广告 3:APP开屏广告 4:直播顶部广告
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/ad/getBanner", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                if (result != null) {
                    try {
                        mBanners = JsonMananger.jsonToBean(result, BannerResponse.class);
                        setTopAdvertisement();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        live_list_notice_fl.setVisibility(View.GONE);
        String body_ = "";
        try {
            body_ = new JsonBuilder()
                    .put("scene_id", 1)//场景（1：直播列表页顶部 2：直播房间页顶部）
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/ad/getNotice", body_, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                live_list_notice_fl.setVisibility(View.GONE);
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                if (result != null) {
                    try {
                        NoticeResponse response = JsonMananger.jsonToBean(result, NoticeResponse.class);
                        String text = response.getContent().getText();
                        if (TextUtils.isEmpty(text)){
                            live_list_notice_fl.setVisibility(View.GONE);
                            return;
                        }
                        live_list_notice_fl.setVisibility(View.VISIBLE);
                        live_list_notice_content.setText(text);

                        final String link = response.getContent().getLink();
                        live_list_notice_content.setOnClickListener(new View.OnClickListener() {
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
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (isRefresh){
            FragmentLiveList fragment = (FragmentLiveList) mContentFragments.get(mPosition);
            if (fragment != null) {
                fragment.refresh();
            }

            loadData();
        }
    }

    private BannerResponse mBanners;

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private List<ChannelListResponse.ChannelListBean> mList = new ArrayList<>();

    private void loadTitle(){
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position).getName();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            if (mContent == null) {
                mContent = new FragmentLiveList();
                mContentFragments.put(position, mContent);
            }
            FragmentLiveList fragment = (FragmentLiveList) mContentFragments.get(position);

            Bundle bundle1 = new Bundle();
            bundle1.putInt("channel_id", mList.get(position).getId());
            bundle1.putBoolean("ISHB", isHB);
            fragment.setArguments(bundle1);

            return fragment;
        }

        @Override
        public int getCount() {
            return mList.size();
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

    private static final int NEXT_MESSAGE = 0;
    private static final int TIME = 5000;

    private void setTopAdvertisement() {
        if (mBanners.getList().size() <= 0){
            return;
        }

        String imgUrl = mBanners.getList().get(0).getImg();
        mCallback.callback(new ModelImg((isHB ? 1 : 0), imgUrl));

        mPointContainerLl.removeAllViews();
        for(int i = 0; i < mBanners.getList().size(); i++){
            View point = new View(mContext);
            int radius = CommonUtils.dip2px(mContext, 6);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(radius, radius);
            if(i==0){
                point.setEnabled(true);
                layoutParams.leftMargin = 0;
            }else{
                point.setEnabled(false);
                layoutParams.leftMargin = CommonUtils.dip2px(mContext, 8);
            }

            point.setLayoutParams(layoutParams);
            point.setBackgroundResource(R.drawable.operater);
            mPointContainerLl.addView(point);
        }

        viewPager.setAdapter(new AdPagerAdapter());

        mHandler.sendEmptyMessageDelayed(NEXT_MESSAGE, TIME);
        viewPager.setCurrentItem(mBanners.getList().size() * 100);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mHandler.removeMessages(NEXT_MESSAGE);
                mHandler.sendEmptyMessageDelayed(NEXT_MESSAGE, TIME);
            }

            @Override
            public void onPageSelected(int position) {
                position = position % mBanners.getList().size();
                for (int i = 0; i < mBanners.getList().size(); i++) {
                    if (i == position) {
                        mPointContainerLl.getChildAt(i).setEnabled(true);
                    } else {
                        mPointContainerLl.getChildAt(i).setEnabled(false);
                    }
                }

                String imgUrl = mBanners.getList().get(position).getImg();
                mCallback.callback(new ModelImg((isHB ? 1 : 0), imgUrl));

                mHandler.removeMessages(NEXT_MESSAGE);
                mHandler.sendEmptyMessageDelayed(NEXT_MESSAGE, TIME);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class AdPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            int pos = position % mBanners.getList().size();

            View view = View.inflate(mContext, R.layout.fragment_banner, null);
            ImageView imageView = view.findViewById(R.id.image);

            Glide.with(mContext)
                    .load(CommonUtils.getUrl(mBanners.getList().get(pos).getImg()))
                    .into(imageView);

//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = position % mBanners.getList().size();
                    String link = mBanners.getList().get(pos).getLink();
                    if (!TextUtils.isEmpty(link)) {
                        Intent intent = new Intent("io.xchat.intent.action.webview");
                        intent.setPackage(getContext().getPackageName());
                        intent.putExtra("url", link);
                        startActivity(intent);
                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == NEXT_MESSAGE) {
                removeMessages(NEXT_MESSAGE);
                int currentItem = viewPager.getCurrentItem();
                viewPager.setCurrentItem(currentItem + 1);
                sendEmptyMessageDelayed(NEXT_MESSAGE, TIME);
            }
        };
    };

    private CommonCallback<ModelImg> mCallback;

    public void setCallback(CommonCallback<ModelImg> callback) {
        mCallback = callback;
    }

}