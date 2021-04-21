package com.yjfshop123.live.video.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.GlideImageLoader;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.PersonalInformationActivity;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.video.activity.FansActivity;
import com.yjfshop123.live.video.activity.PersonalDetailsActivity;
import com.yjfshop123.live.video.activity.SmallVideoActivity;
import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AccordionTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MeVideoFragment extends BaseFragment implements
        OnBannerListener,
        ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private List<String> images;

    private String[] mTitles;
    private String mi_tencentId;
    private String mUserID;
    private boolean isHost = false;

    @BindView(R.id.mm_back_iv)
    ImageView backIV;

    @BindView(R.id.mm_details_banner)
    Banner bannerView;

    @BindView(R.id.mm_details_nickname_tv)
    TextView nicknameTv;

    @BindView(R.id.mm_details_nickname_tv2)
    TextView nicknameTv2;

    @BindView(R.id.mm_details_age_tv)
    TextView ageTv;

    @BindView(R.id.mm_details_location_tv)
    TextView mLocationTV;

    @BindView(R.id.mm_details_vip_tv)
    TextView mVIPTV;

    @BindView(R.id.mm_details_desc_tv)
    TextView descTv;

    @BindView(R.id.mm_details_id_tv)
    TextView idTv;

    @BindView(R.id.mm_details_follow_num)
    TextView followNumTv;
    @BindView(R.id.mm_details_like_num)
    TextView likeNumTv;
    @BindView(R.id.mm_details_fans_num)
    TextView fansNumTv;

    @BindView(R.id.mm_details_civ)
    CircleImageView detailsCiv;

    @BindView(R.id.mm_details_daren_status)
    ImageView darenStatusIv;

    @BindView(R.id.mm_details_controls_add)
    TextView controlsAdd;

    @BindView(R.id.mm_details_more)
    ImageView mMore;

    @BindView(R.id.mm_abl)
    AppBarLayout mABL;

    @BindView(R.id.mm_psts)
    PagerSlidingTabStrip mSlidingTabLayout;

    @BindView(R.id.mm_vp)
    ViewPager mViewPager;

    @BindView(R.id.me_b_view)
    View meBView;

    @BindView(R.id.mm_details_chat)
    ImageView mChatIV;

    @BindView(R.id.video_me_l)
    TextView videoMeL;
    @BindView(R.id.video_me_r)
    TextView videoMeR;


    private int isFollow;
    private String userAvatar;
    private String userNickname;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);

        mi_tencentId = UserInfoUtil.getMiTencentId();
        Bundle bundle = getArguments();
        mUserID = bundle.getString("USER_ID");
        if (TextUtils.isEmpty(mUserID)){
            isHost = true;
            mUserID = mi_tencentId;
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_video_me;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isHost){
            mi_tencentId = UserInfoUtil.getMiTencentId();
            mUserID = mi_tencentId;
        }
        getUserHome();
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden) {
//            getUserHome();
//        }
//    }

    @Override
    protected void initAction() {

        if (isHost){
            //自己
            backIV.setVisibility(View.GONE);
            controlsAdd.setText("修改资料");
            controlsAdd.setBackgroundResource(R.drawable.icon_focus_2);
            controlsAdd.setTextColor(getResources().getColor(R.color.color_999999));
            meBView.setVisibility(View.VISIBLE);
            mChatIV.setVisibility(View.GONE);
            mVIPTV.setVisibility(View.VISIBLE);
        }else {
            backIV.setVisibility(View.VISIBLE);
            backIV.setOnClickListener(this);
            meBView.setVisibility(View.GONE);
            mVIPTV.setVisibility(View.GONE);
            mChatIV.setVisibility(View.VISIBLE);
            mChatIV.setOnClickListener(this);
        }

        followNumTv.setOnClickListener(this);
        likeNumTv.setOnClickListener(this);
        fansNumTv.setOnClickListener(this);

        mContentFragments = new SparseArray<>();

        mTitles = new String[]{"作品88880", "喜欢88880"};
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager()));
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mm_back_iv:
                if (!isHost){
                    if (getActivity()instanceof PersonalDetailsActivity){
                        ((PersonalDetailsActivity)getActivity()).getFinish();
                    }
                }
                break;
            case R.id.mm_details_more:
                if (isHost){
                    if (getActivity() instanceof SmallVideoActivity){
                        ((SmallVideoActivity)getActivity()).onDrawer();
                    }
                }else {
                    dialogMore();
                }
                break;
            case R.id.mm_details_controls_add:
                if (isHost){
                    startActivity(new Intent(mContext, PersonalInformationActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }else {
                    focus();
                }
                break;
            case R.id.mm_details_chat:
                startChat();
                break;
            case R.id.mm_details_like_num:
                break;
            case R.id.mm_details_follow_num:
                startActivity(new Intent(mContext, FansActivity.class).putExtra("TYPE", 0).putExtra("USER_ID", mUserID));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
            case R.id.mm_details_fans_num:
                startActivity(new Intent(mContext, FansActivity.class).putExtra("TYPE", 1).putExtra("USER_ID", mUserID));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                break;
        }
    }

    private void focus(){
        if (isFollow > 0) {
            //取消关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", mUserID)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    isFollow = 0;
                    controlsAdd.setText("关注");
                    controlsAdd.setBackgroundResource(R.drawable.icon_focus_1);
                    controlsAdd.setTextColor(getResources().getColor(R.color.white));
                }
            });
        } else {
            //关注
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("be_user_id", mUserID)
                        .build();
            } catch (JSONException e) {
            }
            OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    isFollow = 1;
                    controlsAdd.setText("取消关注");
                    controlsAdd.setBackgroundResource(R.drawable.icon_focus_2);
                    controlsAdd.setTextColor(getResources().getColor(R.color.color_999999));
                    NToast.shortToast(mContext, "已关注成功");
                }
            });
        }
    }

    @Override
    protected void initEvent() {
        controlsAdd.setOnClickListener(this);
        mMore.setOnClickListener(this);

        mABL.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    //展开
                    nicknameTv2.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //折叠
                    nicknameTv2.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    nicknameTv2.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    private void loadData(String result) {
        if (result == null){
            return;
        }

        try {
            JSONObject data = new JSONObject(result);
            userAvatar = CommonUtils.getUrl(data.getString("avatar"));
            userNickname = data.getString("user_nickname");
            userId = data.getString("user_id");

            //banner
            images = new ArrayList<>();
//            for (int i = 0; i < list_banner.size(); i++) {
//                images.add(list_banner.get(i).getFull_url());
//            }
            //TODO
            //TODO
            //TODO
            images.add(userAvatar);

            bannerView.setImages(images)
                    //.setBannerTitles(App.titles)//标
                    .setBannerAnimation(AccordionTransformer.class)//动画
                    .setIndicatorGravity(BannerConfig.CENTER)//指示器位置
                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)//指示器样式
                    .setDelayTime(3000)//延迟4S
                    .setImageLoader(new GlideImageLoader())
                    .setOnBannerListener(this)
                    .start();

            nicknameTv.setText(userNickname);
            nicknameTv2.setText(userNickname);
            descTv.setText(data.getString("signature"));
            idTv.setText("ID:" + userId);

            int sex = data.getInt("sex");
            if (sex == 1) {
                Drawable drawable = getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ageTv.setCompoundDrawables(drawable, null, null, null);

            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ageTv.setCompoundDrawables(drawable, null, null, null);
            }
            ageTv.setText(data.getString("age") + "岁");

            mLocationTV.setText(data.getString("city_name"));

            if (isHost){
                int is_vip = data.getInt("is_vip");
                String vip_expire_time = data.getString("vip_expire_time");
                if (is_vip == 0){
                    mVIPTV.setVisibility(View.GONE);
                }else {
                    mVIPTV.setText("VIP到期:" + vip_expire_time);
                }
            }else {
                isFollow = data.getInt("is_follow");
                if (isFollow > 0) {
                    controlsAdd.setText("取消关注");
                    controlsAdd.setBackgroundResource(R.drawable.icon_focus_2);
                    controlsAdd.setTextColor(getResources().getColor(R.color.color_999999));
                } else {
                    controlsAdd.setText("关注");
                    controlsAdd.setBackgroundResource(R.drawable.icon_focus_1);
                    controlsAdd.setTextColor(getResources().getColor(R.color.white));
                }
            }

            likeNumTv.setText(Html.fromHtml(data.getString("like_num") + " <font color='#999999'>获赞</font>" ));
            followNumTv.setText(Html.fromHtml(data.getString("guaznhu_num") + " <font color='#999999'>关注</font>" ));
            fansNumTv.setText(Html.fromHtml(data.getString("fans_num") + " <font color='#999999'>粉丝</font>" ));

            if (data.getInt("daren_status") == 2){
                darenStatusIv.setVisibility(View.VISIBLE);
            }else {
                darenStatusIv.setVisibility(View.GONE);
            }

            Glide.with(mContext)
                    .load(userAvatar)
                    .into(detailsCiv);

            detailsCiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        List<String> list = new ArrayList<>();
                        list.add(userAvatar);
                        Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                        intent.putExtra(Config.POSITION, 0);
                        intent.putExtra("Picture", JsonMananger.beanToJson(list));
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            });

            videoMeL.setText("作品" + data.getString("zuopin_num"));
            videoMeR.setText("喜欢" + data.getString("xihuan_num"));

            if (isHost){
                UserInfoUtil.setUserInfo(
                        sex,
                        data.getInt("daren_status"),
                        data.getInt("is_vip"),
                        "",
                        data.getInt("auth_status"),
                        data.getString("province_name") + "," + data.getString("city_name") + "," + data.getString("district_name"),
                        "",
                        data.getInt("age"),
                        data.getString("signature"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnBannerClick(int position) {
        if (images.size() > 0) {
            try {
                Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                intent.putExtra(Config.POSITION, position);
                intent.putExtra("Picture", JsonMananger.beanToJson(images));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }
    }

    private void startChat() {
        String platformId = UserInfoUtil.getMiPlatformId();
        String prom_custom_uid = userId;
        String user_id = userId;

        if (mi_tencentId.equals(prom_custom_uid)) {
            //自己不能跟自己聊天
            NToast.shortToast(mContext, getString(R.string.not_me_chat));
            return;
        }

        imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(platformId);

        imConversation.setOtherPartyIMId(prom_custom_uid);
        imConversation.setOtherPartyId(user_id);

        imConversation.setUserName(UserInfoUtil.getName());
        imConversation.setUserAvatar(UserInfoUtil.getAvatar());

        imConversation.setOtherPartyName(userNickname);
        imConversation.setOtherPartyAvatar(userAvatar);

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        Intent intent = new Intent(mContext, MessageListActivity.class);
        intent.putExtra("IMConversation", imConversation);
        startActivity(intent);
    }

    private IMConversation imConversation;

    private void getUserHome(){
        String url;
        if (isHost){
            url = "app/user/getShortVideoUserInfo";
        }else {
            url = "app/user/getShortVideoUserInfoByuid";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", mUserID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                loadData(result);
            }
        });
    }

    private void dialogMore() {
        final Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_c_video, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        contentView.findViewById(R.id.dialog_c_video_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mUserID)){
                    String link = Const.getDomain() + "/apph5/inform/index"
                            + "?user_id=" + mi_tencentId
                            + "&be_user_id=" + mUserID;
                    Intent intent = new Intent("io.xchat.intent.action.webview");
                    intent.setPackage(getActivity().getPackageName());
                    intent.putExtra("url", link);
                    startActivity(intent);
                }

                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_c_video_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                blockUser();
            }
        });

        contentView.findViewById(R.id.dialog_c_video_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
    }

    private void blockUser(){
        DialogUitl.showSimpleHintDialog(getActivity(),
                getString(R.string.prompt),
                getString(R.string.other_ok),
                getString(R.string.other_cancel),
                "拉黑该用户？",
                true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        String body = "";
                        try {
                            body = new JsonBuilder()
                                    .put("user_id", mUserID)
                                    .build();
                        } catch (JSONException e) {
                        }
                        OKHttpUtils.getInstance().getRequest("app/user/blockUser", body, new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                NToast.shortToast(mContext, errInfo);
                            }
                            @Override
                            public void onSuccess(String result) {
                                NToast.shortToast(mContext, "拉黑成功");
                            }
                        });

                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0){
            videoMeL.setTextColor(getResources().getColor(R.color.white));
            videoMeR.setTextColor(getResources().getColor(R.color.color_999999));
        }else {
            videoMeL.setTextColor(getResources().getColor(R.color.color_999999));
            videoMeR.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            if (mContent == null) {
                mContent = new LoveVideoFragment();
                mContentFragments.put(position, mContent);
            }
            LoveVideoFragment fragment = (LoveVideoFragment) mContentFragments.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", position);
            bundle.putString("USER_ID", mUserID);
            fragment.setArguments(bundle);

            return fragment;
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
