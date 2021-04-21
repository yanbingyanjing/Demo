package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.AlbumVideoResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.fragment.PhotosFragment;
import com.yjfshop123.live.ui.fragment.VideosFragment;
import com.yjfshop123.live.ui.widget.NoScrollViewPager;
import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyAlbumActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.text_right)
    TextView text_right;

    private Fragment mContent;
    private SparseArray<Fragment> mContentFragments;

    @BindView(R.id.h_1_vp)
    NoScrollViewPager mViewPager;
    @BindView(R.id.h_1_nts_top)
    PagerSlidingTabStrip mTopNavigationTabStrip;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;

    private String option = "edit";
    public static ArrayList<AlbumVideoResponse.AlbumBean> localPhotoList = new ArrayList<>();
    public static ArrayList<AlbumVideoResponse.VideoBean> localVideoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
        initEvent();

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.album_my);
        text_right.setVisibility(View.VISIBLE);
        text_right.setText(R.string.album_edit);
        text_right.setTextSize(15);

        mContentFragments = new SparseArray<>();

        String[] titles = getResources().getStringArray(R.array.album_titles);
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
//        mViewPager.setOnPageChangeListener(this);
        mTopNavigationTabStrip.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        getUserAlbumVideo();
    }

    private void initEvent() {
        text_right.setOnClickListener(this);

        localPhotoList.clear();
        localVideoList.clear();

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

    static ArrayList<String> photos = new ArrayList<>();

    @Override
    public void onHeadLeftButtonClick(View v) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_right:
                if (option.equals("edit")) {
                    text_right.setText(R.string.Done);
//                    deleteLayout.setVisibility(View.VISIBLE);
                    option = "complite";
//                    currentPosition = mViewPager.getCurrentItem();
                    EventBus.getDefault().post("bianji",Config.EVENT_START);
                    mViewPager.setNoScroll(true);
                    mTopNavigationTabStrip.setNoScroll(true);
                    mBtnLeft.setClickable(false);
                } else if (option.equals("complite")) {
                    text_right.setText(R.string.album_edit);
//                    currentPosition = mViewPager.getCurrentItem();
//                    deleteLayout.setVisibility(View.GONE);
                    option = "edit";
                    EventBus.getDefault().post("complite",Config.EVENT_START);
                    mViewPager.setNoScroll(false);
                    mTopNavigationTabStrip.setNoScroll(false);
                    mBtnLeft.setClickable(true);
                }
                break;
        }
    }

    private void getUserAlbumVideo(){
        OKHttpUtils.getInstance().getRequest("app/user/getUserAlbumVideo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    AlbumVideoResponse albumVideoResponse = JsonMananger.jsonToBean(result, AlbumVideoResponse.class);
                    for (int i = 0; i < albumVideoResponse.getAlbum().size(); i++) {
                        localPhotoList.add(albumVideoResponse.getAlbum().get(i));
                    }
                    for (int i = 0; i < albumVideoResponse.getVideo().size(); i++) {
                        AlbumVideoResponse.VideoBean response = albumVideoResponse.getVideo().get(i);
                        localVideoList.add(response);
                    }
                    EventBus.getDefault().post("loadedData",Config.EVENT_START);
//                    new PhotosFragment().freshData(localPhotoList);
//                    PhotosFragment.setCount();
                } catch (HttpException e) {
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
//            Bundle bundle = new Bundle();
            switch (position) {
                case POS_0:
//                    currentPosition = POS_0;
                    if (mContent == null) {

                        mContent = new PhotosFragment();
//                        bundle.putSerializable("photoString", localPhotoList);
//                        mContent.setArguments(bundle);
                        mContentFragments.put(POS_0, mContent);
                    }
                    PhotosFragment fragment1 = (PhotosFragment) mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
//                    currentPosition = POS_1;
                    if (mContent == null) {
                        mContent = new VideosFragment();
//                        bundle.putSerializable("videoString", userInfoBean.getVideos());
//                        mContent.setArguments(bundle);
                        mContentFragments.put(POS_1, mContent);
                    }
                    VideosFragment fragment2 = (VideosFragment) mContentFragments.get(POS_1);
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
