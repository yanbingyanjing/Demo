package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.BackCallActivity;
import com.yjfshop123.live.ui.activity.CallSettingActivity1v1;
import com.yjfshop123.live.ui.activity.ChatPriceActivity;
import com.yjfshop123.live.ui.activity.DaRenRenZhengActivity;
import com.yjfshop123.live.ui.activity.GZActivity;
import com.yjfshop123.live.ui.activity.MountActivity;
import com.yjfshop123.live.ui.activity.MyAlbumActivity;
import com.yjfshop123.live.ui.activity.MyVideoActivity;
import com.yjfshop123.live.ui.activity.MyWalletActivity1;
import com.yjfshop123.live.ui.activity.PersonalInformationActivity;
import com.yjfshop123.live.ui.activity.PostListActivity;
import com.yjfshop123.live.ui.activity.RewardActivity;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class MySelfFragment1V1 extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.autorImg)
    CircleImageView autorImg;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.editPersional)
    TextView editPersional;
    @BindView(R.id.be_look_num)
    CircleImageView be_look_num;
    @BindView(R.id.be_follow_num)
    CircleImageView be_follow_num;
    @BindView(R.id.follow_num)
    CircleImageView follow_num;

    @BindView(R.id.lookMe)
    LinearLayout lookMe;
    @BindView(R.id.guanzhuMe)
    LinearLayout guanzhuMe;
    @BindView(R.id.meguanzhu)
    LinearLayout meguanzhu;

    @BindView(R.id.myWallet)
    FrameLayout myWallet;
    @BindView(R.id.darenLayout)
    FrameLayout darenLayout;
    @BindView(R.id.chatLayout)
    FrameLayout chatLayout;
    @BindView(R.id.shiMingLayout)
    FrameLayout shiMingLayout;

    @BindView(R.id.myAlbum)
    FrameLayout myAlbum;
    @BindView(R.id.beautyLayout)
    FrameLayout beautyLayout;
    @BindView(R.id.shareLayout)
    FrameLayout shareLayout;

    @BindView(R.id.tieziLayout)
    FrameLayout tieziLayout;
    @BindView(R.id.settingLayout)
    FrameLayout settingLayout;

    @BindView(R.id.gonghuiLayout)
    FrameLayout gonghuiLayout;
    @BindView(R.id.xdLayout)
    FrameLayout xdLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_myself_1v1;
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getUserInfo();
        }
    }

    private String mUserid;
    private boolean isRequest = true;

    @Override
    public void onResume() {
        super.onResume();
        mUserid = UserInfoUtil.getMiTencentId();
        Glide.with(mContext)
                .load(CommonUtils.getUrl(UserInfoUtil.getAvatar()))
                .into(autorImg);
        username.setText(UserInfoUtil.getName());
        userId.setText("ID:" + mUserid);

        if (isRequest){
            isRequest = false;
            getUserInfo();
        }
    }

    @Override
    protected void initEvent() {
        myWallet.setOnClickListener(this);
        darenLayout.setOnClickListener(this);
        chatLayout.setOnClickListener(this);
        shiMingLayout.setOnClickListener(this);
        myAlbum.setOnClickListener(this);
        beautyLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        tieziLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);

        gonghuiLayout.setOnClickListener(this);
        xdLayout.setOnClickListener(this);

        lookMe.setOnClickListener(this);
        guanzhuMe.setOnClickListener(this);
        meguanzhu.setOnClickListener(this);
        username.setOnClickListener(this);
        userId.setOnClickListener(this);
        autorImg.setOnClickListener(this);
        editPersional.setOnClickListener(this);
    }

    private void initDatas(String result) {
        if (result == null){
            return;
        }
        try {
            JSONObject data = new JSONObject(result);

            mUserid = data.getString("user_id");
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(data.getString("avatar")))
                    .into(autorImg);
            username.setText(data.getString("user_nickname"));
            userId.setText("ID:" + mUserid);

            String last_look_me = data.getString("last_look_me");
            if (!TextUtils.isEmpty(last_look_me)){
                Glide.with(getActivity())
                        .load(CommonUtils.getUrl(last_look_me))
                        .into(be_look_num);
            }
            String last_follow_me = data.getString("last_follow_me");
            if (!TextUtils.isEmpty(last_follow_me)){
                Glide.with(getActivity())
                        .load(CommonUtils.getUrl(last_follow_me))
                        .into(be_follow_num);
            }
            String last_follow = data.getString("last_follow");
            if (!TextUtils.isEmpty(last_follow)){
                Glide.with(getActivity())
                        .load(CommonUtils.getUrl(last_follow))
                        .into(follow_num);
            }

            UserInfoUtil.setUserInfo(
                    data.getInt("sex"),
                    data.getInt("daren_status"),
                    data.getInt("is_vip"),
                    data.getString("speech_introduction"),
                    data.getInt("auth_status"),
                    data.getString("province_name") + "," + data.getString("city_name") + "," + data.getString("district_name"),
                    data.getString("tags"),
                    data.getInt("age"),
                    data.getString("signature"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        try {
            switch (view.getId()) {
                case R.id.username:
                case R.id.userId:
                case R.id.autorImg:
                    ActivityUtils.startUserHome(mContext,String.valueOf(mUserid));
                    break;
                case R.id.editPersional:
                    intent.setClass(getActivity(), PersonalInformationActivity.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.settingLayout:
                    intent.setClass(getActivity(), SettingActivity.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.myWallet:
                    intent.setClass(getActivity(), MyWalletActivity1.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.myAlbum:
                    intent.setClass(getActivity(), MyAlbumActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.shiMingLayout:
                    intent.setClass(getActivity(), ShiMingRenZhengActivity.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.darenLayout:
                    intent.setClass(getActivity(), DaRenRenZhengActivity.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.chatLayout:
                    intent.setClass(getActivity(), ChatPriceActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.lookMe:
                    intent.setClass(getActivity(), GZActivity.class);
                    intent.putExtra("TYPE", 0);
                    getActivity().startActivity(intent);
                    break;
                case R.id.guanzhuMe:
                    intent.setClass(getActivity(), GZActivity.class);
                    intent.putExtra("TYPE", 1);
                    getActivity().startActivity(intent);
                    break;
                case R.id.meguanzhu:
                    intent.setClass(getActivity(), GZActivity.class);
                    intent.putExtra("TYPE", 2);
                    getActivity().startActivity(intent);
                    break;
                case R.id.shareLayout:
                    intent.setClass(getActivity(), RewardActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.tieziLayout:
                    intent.setClass(getActivity(), PostListActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.backCallLayout:
                    intent.setClass(getActivity(), BackCallActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.mountLayout:
                    intent.setClass(getActivity(), MountActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.beautyLayout:
                    intent.setClass(getActivity(), CallSettingActivity1v1.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.videoLayout:
                    intent.setClass(getActivity(), MyVideoActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.gonghuiLayout:
                    String url = Const.getDomain() + "/apph5/guild/index";
                    intent.setAction("io.xchat.intent.action.webview");
                    intent.setPackage(getActivity().getPackageName());
                    intent.putExtra("url", url);
                    startActivity(intent);
                    break;
                case R.id.xdLayout:
                    String url_xd = Const.getDomain() + "/apph5/shop/myshop";
                    intent.setAction("io.xchat.intent.action.webview");
                    intent.setPackage(getActivity().getPackageName());
                    intent.putExtra("url", url_xd);
                    startActivity(intent);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUserInfo(){
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    @Override
    protected void updateViews(boolean isRefresh) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
