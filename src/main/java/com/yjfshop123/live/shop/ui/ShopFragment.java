package com.yjfshop123.live.shop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.model.HomeFenlei;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.shop.ziying.ui.NewShopListActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.DialogHomeActivitiesFragment;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ShopFragment extends BaseFragment {
    @BindView(R.id.btn_left)
    ImageView search;
    @BindView(R.id.h_1_nts_sty)
    PagerSlidingTabStrip mSlidingTabLayout;
    @BindView(R.id.h_1_vp)
    ViewPager mViewPager;
    Unbinder unbinder;
    @BindView(R.id.status_bar_view)
    View statusBarView;

    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;
    @BindView(R.id.text_right)
    TextView textRight;
    @BindView(R.id.layout_head)
    RelativeLayout layoutHead;

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_shop;
    }

    @Override
    protected void initAction() {
        mContentFragments = new SparseArray<>();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        //获取当前控件的布局对象
        params.height = (int) (SystemUtils.getStatusBarHeight(getContext()));//设置当前控件布局的高度

        statusBarView.setLayoutParams(params);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ZiyingActivity.class);
//                startActivity(intent);
                loadUserConfig();
//                try {
//                    JSONObject jsonObject=new JSONObject();
//                    jsonObject.put("Token",UserInfoUtil.getToken());
//                    jsonObject.put("DeviceType", "android");
//                    jsonObject.put("ApiVersion",App.versionName);
//                    jsonObject.put("StoreChannel",App.channel_id);
//                    DCUniMPSDK.getInstance().startApp(getActivity(),"__UNI__2D3DE04",jsonObject);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//
//                    Intent intent = new Intent(getActivity(), PandoraEntry.class);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        });
        hideLoading();
        getData(false);
    }

    public void loadUserConfig() {
        LoadDialog.show(getContext());
        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getContext());
                Intent intent = new Intent(getActivity(), NewShopListActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
                try {
                    if (TextUtils.isEmpty(result)) {
                        Intent intent = new Intent(getActivity(), NewShopListActivity.class);
                        startActivity(intent);
                        return;
                    }
                    UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                    if (data.jump != null) {
                        if (data.jump.shop_is_h5 == 1) {
                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                            if (!TextUtils.isEmpty(data.jump.shop_h5)) {
                                intent.putExtra("url", data.jump.shop_h5);
                            }
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), NewShopListActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), NewShopListActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(getActivity(), NewShopListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }

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

    public void getData(boolean isFromRefresh) {
        if (!isFromRefresh) {
            showLoading();
        }
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.1.0");
        paraMap.put("appKey", Const.appKey);
        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取的数据", response);
                hideLoading();

                initData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取的数据", error);
                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                } else
                    hideLoading();

            }
        }, HttpUtil.url, paraMap);
    }

    public List<HomeFenlei.DataSecond> data = new ArrayList<>();

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        HomeFenlei homeFenlei = new Gson().fromJson(result, HomeFenlei.class);
        if (homeFenlei == null) return;
        if (homeFenlei.data == null) return;


        data.addAll(homeFenlei.data);
        MyFragmentPager fAdapter = new MyFragmentPager(getChildFragmentManager(), data);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(fAdapter);
        mViewPager.setCurrentItem(0);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public class MyFragmentPager extends FragmentPagerAdapter {

        private List<HomeFenlei.DataSecond> mTitles;

        public MyFragmentPager(FragmentManager fm, List<HomeFenlei.DataSecond> mTitles) {
            super(fm);
            this.mTitles = mTitles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position).cname;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);
//
//            if (position == 0) {
//                if (mContent == null) {
//                    mContent = new ZiyingFragment();
//
//                    mContentFragments.put(position, mContent);
//                }
//                ZiyingFragment fragment1 = (ZiyingFragment) mContentFragments.get(position);
//                return fragment1;
//            } else {
            if (mContent == null) {
                mContent = new ShopSubFragment();
                ((ShopSubFragment) mContent).setId(mTitles.get(position).cid + "");
                mContentFragments.put(position, mContent);
            }
            ShopSubFragment fragment1 = (ShopSubFragment) mContentFragments.get(position);
            return fragment1;
            //  }


        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {

        }
    }
}
