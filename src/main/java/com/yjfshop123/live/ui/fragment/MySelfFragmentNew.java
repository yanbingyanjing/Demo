package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.otc.MainOtcActivity;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.DaRenRenZhengActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.MMDetailsActivityNew;
import com.yjfshop123.live.ui.activity.MountActivity;
import com.yjfshop123.live.ui.activity.MyVideoActivity;
import com.yjfshop123.live.ui.activity.PostListActivity;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.ui.activity.income.ZhuBoIncomeActivity;
import com.yjfshop123.live.ui.widget.DialogZan;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.InstallExchangeDialog;
import com.yjfshop123.live.video.activity.FansActivity;
import com.yjfshop123.live.video.activity.PersonalDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MySelfFragmentNew extends BaseFragment implements View.OnClickListener {


    Unbinder unbinder;
    @BindView(R.id.top)
    View top;
    @BindView(R.id.two)
    LinearLayout two;
    @BindView(R.id.autorImg)
    CircleImageView autorImg;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.vip)
    ImageView vip;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.editPersional)
    TextView editPersional;
    @BindView(R.id.guanzhu)
    TextView guanzhu;
    @BindView(R.id.fensi)
    TextView fensi;
    @BindView(R.id.zan)
    TextView zan;
    @BindView(R.id.dongtai)
    TextView dongtai;
    @BindView(R.id.below)
    LinearLayout below;
    @BindView(R.id.three)
    FrameLayout three;
    @BindView(R.id.buy_gld_egg)
    FrameLayout buyGldEgg;
    @BindView(R.id.videoLayout)
    FrameLayout videoLayout;
    @BindView(R.id.live_shouyi)
    FrameLayout liveShouyi;
    @BindView(R.id.gonghuiLayout)
    FrameLayout gonghuiLayout;
    @BindView(R.id.tieziLayout)
    FrameLayout tieziLayout;
    @BindView(R.id.mountLayout)
    FrameLayout mountLayout;
    @BindView(R.id.settingLayout)
    FrameLayout settingLayout;
    @BindView(R.id.send_guanggao)
    FrameLayout sendGuanggao;
    @BindView(R.id.darenLayout)
    FrameLayout darenLayout;


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.guanzhu_ll)
    LinearLayout guanzhuLl;
    @BindView(R.id.fensi_ll)
    LinearLayout fensiLl;
    @BindView(R.id.zan_ll)
    LinearLayout zanLl;
    @BindView(R.id.dongtai_ll)
    LinearLayout dongtaiLl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_myself_new;
    }

    @Override
    protected void initAction() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) top.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(getActivity());//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        top.setLayoutParams(params);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getUserInfo();
        }
    }

    private String mUserid;
    private int guildStatus;
    private boolean isRequest = true;

    @Override
    public void onResume() {
        super.onResume();
        mUserid = UserInfoUtil.getMiTencentId();
        if (!TextUtils.isEmpty(UserInfoUtil.getAvatar()))
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(UserInfoUtil.getAvatar()))
                    .into(autorImg);
        username.setText(UserInfoUtil.getName());
        userId.setText("ID:" + mUserid);

        if (isRequest) {
            isRequest = false;
            getUserInfo();
        }
    }

    @Override
    protected void initEvent() {
        //  myWallet.setOnClickListener(this);
        //  darenLayout.setOnClickListener(this);
        // chatLayout.setOnClickListener(this);
        //  shiMingLayout.setOnClickListener(this);
        // myAlbum.setOnClickListener(this);
        // beautyLayout.setOnClickListener(this);
        // shareLayout.setOnClickListener(this);
        buyGldEgg.setOnClickListener(this);
        tieziLayout.setOnClickListener(this);
        // backCallLayout.setOnClickListener(this);
        mountLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        darenLayout.setOnClickListener(this);
zanLl.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        gonghuiLayout.setOnClickListener(this);
fensiLl.setOnClickListener(this);
        //  buyGldEgg.setOnClickListener(this);
        liveShouyi.setOnClickListener(this);
        sendGuanggao.setOnClickListener(this);
        // xdLayout.setOnClickListener(this);
//
//        lookMe.setOnClickListener(this);
//        guanzhuMe.setOnClickListener(this);
//        meguanzhu.setOnClickListener(this);
        username.setOnClickListener(this);
        userId.setOnClickListener(this);
        autorImg.setOnClickListener(this);
        editPersional.setOnClickListener(this);

        guanzhuLl.setOnClickListener(this);
        dongtaiLl.setOnClickListener(this);
    }

    private void initDatas(String result) {
        if (result == null) {
            return;
        }
        try {
            JSONObject data = new JSONObject(result);

            mUserid = data.getString("user_id");

            if (!TextUtils.isEmpty(data.getString("avatar"))) {
                Glide.with(mContext)
                        .load(CommonUtils.getUrl(data.getString("avatar")))
                        .into(autorImg);
            }
            username.setText(data.getString("user_nickname"));
            userId.setText("ID:" + mUserid);
            guanzhu.setText(data.getInt("follow_num") + "");
            fensi.setText(data.getInt("be_follow_num") + "");
            zan.setText(data.getString("like_num"));
            dongtai.setText(data.getString("forum_num"));
//            String last_look_me = data.getString("last_look_me");
//            if (!TextUtils.isEmpty(last_look_me)) {
//                Glide.with(getActivity())
//                        .load(CommonUtils.getUrl(last_look_me))
//                        .into(be_look_num);
//            }
//            String last_follow_me = data.getString("last_follow_me");
//            if (!TextUtils.isEmpty(last_follow_me)) {
//                Glide.with(getActivity())
//                        .load(CommonUtils.getUrl(last_follow_me))
//                        .into(be_follow_num);
//            }
//            String last_follow = data.getString("last_follow");
//            if (!TextUtils.isEmpty(last_follow)) {
//                Glide.with(getActivity())
//                        .load(CommonUtils.getUrl(last_follow))
//                        .into(follow_num);
//            }

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

            guildStatus = data.getInt("guild_status");

            if (data.getInt("is_vip") == 1) {
                vip.setVisibility(View.VISIBLE);
            } else {
                vip.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void initData() {
        super.initData();
    }

    InstallExchangeDialog dialog;

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        try {
            switch (view.getId()) {
                case R.id.darenLayout:
                    intent.setClass(getActivity(), DaRenRenZhengActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.fensi_ll:
                    startActivity(new Intent(mContext, FansActivity.class).putExtra("TYPE", 1).putExtra("USER_ID", mUserid));
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    isRequest = true;
                    break;
                case R.id.zan_ll:
                    DialogZan dialogZan=new DialogZan();
                    dialogZan.setTitle("\""+username.getText().toString()+"\""+"共获得"+zan.getText().toString()+"个赞");
                    dialogZan.show(getChildFragmentManager(), "DialogZan");
                    break;
                case R.id.guanzhu_ll:
//                    getActivity().setResult(10002);
//                    getActivity().finish();
                    startActivity(new Intent(mContext, FansActivity.class).putExtra("TYPE", 0).putExtra("USER_ID", mUserid));
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    isRequest = true;
                    break;

                case R.id.buy_gld_egg:
                    startActivity( new Intent(getActivity(), MainOtcActivity.class));
//                    dialog = new InstallExchangeDialog(getContext()).builder();
//                    dialog.setFocus(false)
////                .setFocus(false)
//                            .setPositiveButton(getString(R.string.now_go_to), new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    if (CheckInstalledUtil.checkAppInstalled(getContext(), Const.exchangePkg)) {
//                                        CheckInstalledUtil.openPackage(getContext(), Const.exchangePkg);
//                                        dialog.cancel();
//                                    } else {
//                                        dialog.cancel();
//                                        ((HomeActivity) getActivity()).onCheckIsInstallExchange();
//                                    }
//                                }
//                            })
//                            .setNegativeButton(null).setTitle(getString(R.string.go_to_exchange));
//                    dialog.show();
                    break;
                case R.id.videoLayout:
                    intent.setClass(getActivity(), MyVideoActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.live_shouyi:
                    intent.setClass(getActivity(), ZhuBoIncomeActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.gonghuiLayout:
                    isRequest = true;
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
                case R.id.tieziLayout:
                    intent.setClass(getActivity(), PostListActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.mountLayout:
                    intent.setClass(getActivity(), MountActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.settingLayout:
                    intent.setClass(getActivity(), SettingActivity.class);
                    getActivity().startActivity(intent);
                    isRequest = true;
                    break;
                case R.id.send_guanggao:
                    break;

                case R.id.username:
                case R.id.userId:
                case R.id.autorImg:
                    ActivityUtils.startUserHome(mContext, mUserid);
                    break;
                case R.id.dongtai_ll:
                    Intent intent2;
                    intent2 = new Intent(getActivity(), MMDetailsActivityNew.class);
                    intent2.putExtra("from","dongtai");
                    intent2.putExtra("USER_ID", mUserid);
                    getActivity().startActivity(intent2);
                    break;
                case R.id.editPersional:
//                    intent.setClass(getActivity(), PersonalInformationActivity.class);
//                    getActivity().startActivity(intent);
//                    isRequest = true;
                    break;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUserInfo() {
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
}
