package com.yjfshop123.live.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.list.FragmentLiveList;
import com.yjfshop123.live.live.live.list.H_2_Fragment;
import com.yjfshop123.live.model.ModelImg;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.ui.activity.ListActivity;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.activity.SearchActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.FastBlurUtil;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.fragment.SmallVideoFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.video.fragment.SmallVideoFragmentNew;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

//首页
public class H_1_Fragment extends Fragment implements ViewPager.OnPageChangeListener {

    private Context context;

    public  ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTabLayout;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    ImageView h_1_list;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;

    private ImageView mBackground;
    private CommonCallback<ModelImg> mCallback;
    private String mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString("TYPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_1_, container, false);

        mBackground = view.findViewById(R.id.h_1_background);

        mViewPager = view.findViewById(R.id.h_1_vp);

        mSlidingTabLayout = view.findViewById(R.id.h_1_nts_sty);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.findViewById(R.id.h_1_top_fl).getLayoutParams();
        //获取当前控件的布局对象
        params.topMargin = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度

        view.findViewById(R.id.h_1_top_fl).setLayoutParams(params);
        mContentFragments = new SparseArray<>();
        mSlidingTabLayout.setOnPagerTitleItemClickListener(new PagerSlidingTabStrip.OnPagerTitleItemClickListener() {
            @Override
            public void onSingleClickItem(int position) {

            }

            @Override
            public void onDoubleClickItem(int position) {

            }
        });
        String[] titles;
        if (mType.equals("1")) {
            titles = getResources().getStringArray(R.array.home_titles);
        } else {
            titles = getResources().getStringArray(R.array.home_titles2);
        }

        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(4);
        h_1_list = view.findViewById(R.id.h_1_list);
        ImageView h_1_search = view.findViewById(R.id.h_1_search);
        if (mType.equals("1")) {
            h_1_search.setVisibility(View.GONE);
            h_1_list.setVisibility(View.VISIBLE);
            h_1_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ListActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            });
        } else {
            h_1_list.setVisibility(View.GONE);
            h_1_search.setVisibility(View.VISIBLE);
            h_1_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin()) {
                        startActivity(new Intent(context, SearchActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }
                }
            });
        }

        setUI();

        mCallback = new CommonCallback<ModelImg>() {
            @SuppressLint("ResourceType")
            @Override
            public void callback(ModelImg mode) {
                String img = CommonUtils.getUrl(mode.getImg());
                if (mode.getPosition() == 0) {
                    imgUrl_1 = img;
                }
                if (mode.getPosition() == 1) {
                    imgUrl_2 = img;
                }

                if (mode.getPosition() != mPosition) {
                    return;
                }

                if (TextUtils.isEmpty(mode.getImg())) {
                    mBackground.setImageDrawable(getResources().getDrawable(R.drawable.ql_rw_bg));
                    return;
                }
                toBlur(img);
            }
        };
        return view;
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

    private void setUI() {
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    //控制短视频暂停和继续
    public void setVisible(boolean visible) {
        if (mPosition != POS_1) {
            return;
        }
        if (visible)
            StatusBarUtil.StatusBarDarkMode(getActivity());
        SmallVideoFragmentNew fragment4 = (SmallVideoFragmentNew) mContentFragments.get(POS_1);
        if (fragment4 != null)
            fragment4.setUserVisibleHint(visible);
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


                    if (mType.equals("1")) {
                        if (mContent == null) {
                            // mContent = new H_2_Fragment();
                            mContent = new FragmentLiveList();
                            mContentFragments.put(POS_0, mContent);
                        }
                        FragmentLiveList fragment2 = (FragmentLiveList) mContentFragments.get(POS_0);
//                        Bundle bundle2 = new Bundle();
//                        bundle2.putBoolean("ISHB", false);
//                        fragment2.setArguments(bundle2);
//                        fragment2.setCallback(mCallback);
                        return fragment2;
                    } else {
                        if (mContent == null) {
                            mContent = new H_1_1_Fragment();
                            mContentFragments.put(POS_0, mContent);
                        }
                        H_1_1_Fragment fragment1 = (H_1_1_Fragment) mContentFragments.get(POS_0);
                        return fragment1;
                    }
                case POS_1:
                    if (mType.equals("1")) {
//                        if (mContent == null) {
//                            mContent = new H_2_Fragment();
//                            mContentFragments.put(POS_1, mContent);
//                        }
//                        H_2_Fragment fragment2 = (H_2_Fragment) mContentFragments.get(POS_1);
//                        Bundle bundle2 = new Bundle();
//                        bundle2.putBoolean("ISHB", true);
//                        fragment2.setArguments(bundle2);
//                        fragment2.setCallback(mCallback);
                        if (mContent == null) {
                            mContent = new SmallVideoFragmentNew();
                            mContentFragments.put(POS_1, mContent);
                        }
                        SmallVideoFragmentNew fragment2 = (SmallVideoFragmentNew) mContentFragments.get(POS_1);
                        return fragment2;
                    } else {
                        if (mContent == null) {
                            mContent = new H_1_2_Fragment();
                            mContentFragments.put(POS_1, mContent);
                        }
                        H_1_2_Fragment fragment2 = (H_1_2_Fragment) mContentFragments.get(POS_1);
                        return fragment2;
                    }
                case POS_2:
                    if (mType.equals("1")) {
//                        if (mContent == null) {
//                            mContent = new SmallVideoFragment();
//                            mContentFragments.put(POS_2, mContent);
//                        }
//                        SmallVideoFragment fragment4 = (SmallVideoFragment) mContentFragments.get(POS_2);
//                        if (mContent == null) {
//                            mContent = new SmallVideoFragmentNew();
//                            mContentFragments.put(POS_2, mContent);
//                        }
//                        SmallVideoFragmentNew fragment4 = (SmallVideoFragmentNew) mContentFragments.get(POS_2);
                        if (mContent == null) {
                            mContent = new H_1_3_Fragment();
                            mContentFragments.put(POS_2, mContent);
                        }
                        H_1_3_Fragment fragment3 = (H_1_3_Fragment) mContentFragments.get(POS_2);
                        return fragment3;

                    } else {
                        if (mContent == null) {
                            mContent = new H_1_3_Fragment();
                            mContentFragments.put(POS_2, mContent);
                        }
                        H_1_3_Fragment fragment3 = (H_1_3_Fragment) mContentFragments.get(POS_2);
                        return fragment3;
                    }
                case POS_3:
                    if (mType.equals("1")) {
//                        if (mContent == null) {
////                            mContent = new H_1_3_Fragment();
////                            mContentFragments.put(POS_3, mContent);
////                        }
////                        H_1_3_Fragment fragment3 = (H_1_3_Fragment) mContentFragments.get(POS_3);
                        if (mContent == null) {
                            mContent = new H_2_Fragment();
                            mContentFragments.put(POS_3, mContent);
                        }
                        H_2_Fragment fragment2 = (H_2_Fragment) mContentFragments.get(POS_3);
                        Bundle bundle2 = new Bundle();
                        bundle2.putBoolean("ISHB", true);
                        fragment2.setArguments(bundle2);
                        fragment2.setCallback(mCallback);
                        return fragment2;
                       // return fragment3;
                    } else {
                        return null;
                    }

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        if (position == 0 || position == 2) {

            Drawable drawable = getResources().getDrawable(R.drawable.f_h_list_icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint(getResources().getColor(R.color.color_000000));
            h_1_list.setImageDrawable(drawable);
            StatusBarUtil.StatusBarLightMode(getActivity());
            mSlidingTabLayout.setSelectedTextColor(getResources().getColor(R.color.color_333333));
            mSlidingTabLayout.setTextColor(getResources().getColor(R.color.color_593B3025));
        } else {
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.f_h_list_icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint(getContext().getResources().getColor(R.color.white));
            h_1_list.setImageDrawable(drawable);
            StatusBarUtil.StatusBarDarkMode(getActivity());
            mSlidingTabLayout.setSelectedTextColor(getResources().getColor(R.color.white));
            mSlidingTabLayout.setTextColor(getResources().getColor(R.color.white));
        }
        if (position > 1) {
            mBackground.setImageDrawable(getResources().getDrawable(R.drawable.ql_rw_bg));
            return;
        }
        if (position == 0 && imgUrl_1 != null) {
            toBlur(imgUrl_1);
        }
//        if (position == 1 && imgUrl_2 != null) {
//            toBlur(imgUrl_2);
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private int mPosition = 0;
    private String imgUrl_1;
    private String imgUrl_2;

    private void toBlur(String img) {
        if (!isDestroy((Activity) context)) {
            Glide.with(context).load(img).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    try {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                        mBackground.setImageBitmap(FastBlurUtil.toBlur(bitmapDrawable.getBitmap(), 9));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

}
