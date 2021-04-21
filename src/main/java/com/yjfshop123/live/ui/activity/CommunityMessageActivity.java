package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.RealmConverUtils;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.fragment.CommunityMessageFragment1;
import com.yjfshop123.live.ui.fragment.CommunityMessageFragment2;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.jpeng.jptabbar.PagerSlidingTabStrip;

public class CommunityMessageActivity extends BaseActivity implements PagerSlidingTabStrip.OnPagerTitleItemClickListener {

    private Context mContext;

    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private TextView acm_unread_num_l, acm_unread_num_r;

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;

    private int currentPosition = 0;

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        public void onPageSelected(int position) {
            /*if (position == 0){
                slide(true);
            }else {
                slide(false);
            }*/

            switchB(position);
        }

        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_community_message);
        setHeadVisibility(View.GONE);

        init();
    }

    private void init(){
        findViewById(R.id.acm_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFinish();
            }
        });

        findViewById(R.id.acm_dele).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint;
                if (currentPosition == 0){
                    hint = getString(R.string.comm_8);
                }else {
                    hint = getString(R.string.comm_9);
                }
                DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel), hint, true, true,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {

                            }
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                clearMessage();
                            }
                        });
            }
        });

        mSlidingTabLayout = findViewById(R.id.h_1_nts_sty);
        acm_unread_num_l = findViewById(R.id.acm_unread_num_l);
        acm_unread_num_r = findViewById(R.id.acm_unread_num_r);
        mViewPager = findViewById(R.id.acm_vp);

        mContentFragments = new SparseArray<>();
        String[] titles = new String[]{getString(R.string.comm_6), getString(R.string.comm_7)};
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(fAdapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPagerTitleItemClickListener(this);

        int currentItem = getIntent().getIntExtra("current_item", 0);
        mViewPager.setCurrentItem(currentItem);
        dataSetChanged(acm_unread_num_l, getIntent().getLongExtra("readCount_like", 0));
        dataSetChanged(acm_unread_num_r, getIntent().getLongExtra("readCount_reply", 0));
    }

    private void dataSetChanged(TextView tv, long unRead){
        if (unRead <= 0){
            tv.setVisibility(View.INVISIBLE);
        }else{
            tv.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                tv.setBackgroundResource(R.drawable.point1);
            }else{
                tv.setBackgroundResource(R.drawable.point2);
                if (unRead > 99){
                    unReadStr = "99+";
                }
            }
            tv.setText(unReadStr);
        }
    }

    public void clearMessage_L(){
        acm_unread_num_l.setVisibility(View.INVISIBLE);
    }

    public void clearMessage_R(){
        acm_unread_num_r.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        slide(true);
    }

    private void switchB(int pos){
        if (currentPosition != pos){
            if (pos == 0){
                clearMessage_R();
            }else {
                clearMessage_L();
            }
        }
        currentPosition = pos;
    }

    @Override
    public void onSingleClickItem(int position) {
        switchB(position);
    }

    @Override
    public void onDoubleClickItem(int position) {

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
                        mContent = new CommunityMessageFragment1();
                        mContentFragments.put(POS_0, mContent);
                    }
                    CommunityMessageFragment1 fragment1 = (CommunityMessageFragment1)mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new CommunityMessageFragment2();
                        mContentFragments.put(POS_1, mContent);
                    }
                    CommunityMessageFragment2 fragment2 = (CommunityMessageFragment2) mContentFragments.get(POS_1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealmConverUtils.clerCommunityRedCount(UserInfoUtil.getMiPlatformId());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getFinish(){
        finish();
    }

    //type 0清空顶 1清空回复
    private void clearMessage(){
        String url;
        if (currentPosition == 0){
            url = "app/forum/clearLikeMessage";
        }else {
            url = "app/forum/clearReplyMessage";
        }
        OKHttpUtils.getInstance().getRequest(url, "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                if (currentPosition == 0){
                    CommunityMessageFragment1 fragment1 = (CommunityMessageFragment1)mContentFragments.get(POS_0);
                    fragment1.loadData();
                }else if (currentPosition == 1){
                    CommunityMessageFragment2 fragment2 = (CommunityMessageFragment2)mContentFragments.get(POS_1);
                    fragment2.loadData();
                }
            }
        });
    }
}
