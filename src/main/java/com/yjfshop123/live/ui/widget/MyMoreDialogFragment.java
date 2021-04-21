package com.yjfshop123.live.ui.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.TintableImageSourceView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.UserInfoResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.BackCallActivity;
import com.yjfshop123.live.ui.activity.CallSettingActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.yjfshop123.video_shooting.utils.UIUtils.getScreenWidth;

public class MyMoreDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    CircleImageView autorImg;
    TextView username;
    TextView userId;
    TextView editPersional;
    CircleImageView be_look_num;
    CircleImageView be_follow_num;
    CircleImageView follow_num;

    LinearLayout lookMe;
    LinearLayout guanzhuMe;
    LinearLayout meguanzhu;

    FrameLayout myWallet;
    FrameLayout darenLayout;
    FrameLayout chatLayout;
    FrameLayout shiMingLayout;

    FrameLayout myAlbum;
    FrameLayout beautyLayout;
    FrameLayout shareLayout;

    FrameLayout tieziLayout;
    FrameLayout backCallLayout;
    FrameLayout mountLayout;
    FrameLayout settingLayout;

    FrameLayout gonghuiLayout;
    FrameLayout videoLayout;
    FrameLayout xdLayout;

    private MainStartChooseCallback mCallback;
    UserInfoResponse data;

    public void setData(UserInfoResponse userData) {
        this.data = userData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_my_more;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.LeftDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = getScreenWidth()*4/5;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.LEFT;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        init();

    }

    private String mUserid;
    private int guildStatus;

    private void initView() {
        autorImg = mRootView.findViewById(R.id.autorImg);
        username = mRootView.findViewById(R.id.username);
        userId = mRootView.findViewById(R.id.userId);
        editPersional = mRootView.findViewById(R.id.editPersional);
        be_look_num = mRootView.findViewById(R.id.be_look_num);
        be_follow_num = mRootView.findViewById(R.id.be_follow_num);
        follow_num = mRootView.findViewById(R.id.follow_num);

        lookMe = mRootView.findViewById(R.id.lookMe);
        guanzhuMe = mRootView.findViewById(R.id.guanzhuMe);
        meguanzhu = mRootView.findViewById(R.id.meguanzhu);

        myWallet = mRootView.findViewById(R.id.myWallet);
        darenLayout = mRootView.findViewById(R.id.darenLayout);
        chatLayout = mRootView.findViewById(R.id.chatLayout);
        shiMingLayout = mRootView.findViewById(R.id.shiMingLayout);

        myAlbum = mRootView.findViewById(R.id.myAlbum);
        beautyLayout = mRootView.findViewById(R.id.beautyLayout);
        shareLayout = mRootView.findViewById(R.id.shareLayout);

        tieziLayout = mRootView.findViewById(R.id.tieziLayout);
        backCallLayout = mRootView.findViewById(R.id.backCallLayout);
        mountLayout = mRootView.findViewById(R.id.mountLayout);
        settingLayout = mRootView.findViewById(R.id.settingLayout);

        gonghuiLayout = mRootView.findViewById(R.id.gonghuiLayout);
        videoLayout = mRootView.findViewById(R.id.videoLayout);
        xdLayout = mRootView.findViewById(R.id.xdLayout);
        myWallet.setOnClickListener(this);
        darenLayout.setOnClickListener(this);
        chatLayout.setOnClickListener(this);
        shiMingLayout.setOnClickListener(this);
        myAlbum.setOnClickListener(this);
        beautyLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        tieziLayout.setOnClickListener(this);
        backCallLayout.setOnClickListener(this);
        mountLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);

        videoLayout.setOnClickListener(this);
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

    private void init() {
        mUserid = data.user_id + "";
        Glide.with(mContext)
                .load(CommonUtils.getUrl(data.avatar))
                .into(autorImg);
        username.setText(data.user_nickname);
        userId.setText("ID:" + mUserid);

        String last_look_me = data.last_look_me;
        if (!TextUtils.isEmpty(last_look_me)) {
            Glide.with(getActivity())
                    .load(CommonUtils.getUrl(last_look_me))
                    .into(be_look_num);
        }
        String last_follow_me = data.last_follow_me;
        if (!TextUtils.isEmpty(last_follow_me)) {
            Glide.with(getActivity())
                    .load(CommonUtils.getUrl(last_follow_me))
                    .into(be_follow_num);
        }
        String last_follow = data.last_follow;
        if (!TextUtils.isEmpty(last_follow)) {
            Glide.with(getActivity())
                    .load(CommonUtils.getUrl(last_follow))
                    .into(follow_num);
        }


        guildStatus = data.guild_status;

    }

    public void setMainStartChooseCallback(MainStartChooseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        try {
            switch (view.getId()) {
                case R.id.username:
                case R.id.userId:
                case R.id.autorImg:
                    ActivityUtils.startUserHome(mContext, mUserid);
                    break;
                case R.id.editPersional:
                    intent.setClass(getActivity(), PersonalInformationActivity.class);
                    getActivity().startActivity(intent);

                    break;
                case R.id.settingLayout:
                    intent.setClass(getActivity(), SettingActivity.class);
                    getActivity().startActivity(intent);

                    break;
                case R.id.myWallet:
                    intent.setClass(getActivity(), MyWalletActivity1.class);
                    getActivity().startActivity(intent);

                    break;
                case R.id.myAlbum:
                    intent.setClass(getActivity(), MyAlbumActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.shiMingLayout:
                    intent.setClass(getActivity(), ShiMingRenZhengActivity.class);
                    getActivity().startActivity(intent);

                    break;
                case R.id.darenLayout:
                    intent.setClass(getActivity(), DaRenRenZhengActivity.class);
                    getActivity().startActivity(intent);

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
                    intent.setClass(getActivity(), CallSettingActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.videoLayout:
                    intent.setClass(getActivity(), MyVideoActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.gonghuiLayout:
//                    String url = Const.getDomain() + "/apph5/guild/index";
                    String url = "file:///android_asset/guild/";
                    if (guildStatus == 1) {
                        url = url + "PresidentCenter.html";
                    } else if (guildStatus == 2) {
                        url = url + "GuildCenter.html";
                    } else if (guildStatus == 3) {
                        url = url + "PerCenter.html";
                    } else {
                        return;
                    }
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


}
