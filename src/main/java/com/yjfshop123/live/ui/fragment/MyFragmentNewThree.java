package com.yjfshop123.live.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.KefuResponse;
import com.yjfshop123.live.model.PriceResponse;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.model.UserInfoResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.otc.MainOtcActivity;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.ziying.ui.NewShopListActivity;
import com.yjfshop123.live.ui.activity.ActivityNumActivity;
import com.yjfshop123.live.ui.activity.BadEggActivity;
import com.yjfshop123.live.ui.activity.DaRenRenZhengActivity;
import com.yjfshop123.live.ui.activity.GoldEggActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.HuodongCenterActivity;
import com.yjfshop123.live.ui.activity.InviteFriendActivity;
import com.yjfshop123.live.ui.activity.LevelActivity;
import com.yjfshop123.live.ui.activity.MsgActivity;
import com.yjfshop123.live.ui.activity.MyMoreActivity;
import com.yjfshop123.live.ui.activity.MyXuanPinActivity;
import com.yjfshop123.live.ui.activity.PersonalInformationActivity;
import com.yjfshop123.live.ui.activity.ShouZhiAtivity;
import com.yjfshop123.live.ui.activity.TargetReciveDetailActivity;
import com.yjfshop123.live.ui.activity.TaskNewCenterActivity;
import com.yjfshop123.live.ui.activity.TestH5Activity;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.activity.ZhutuiActivity;
import com.yjfshop123.live.ui.activity.team.MyTeamActivity;
import com.yjfshop123.live.ui.activity.yinegg.NewSilverEggActivity;
import com.yjfshop123.live.ui.widget.ChoujiangFragment;
import com.yjfshop123.live.ui.widget.ShowTargetDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.xuanpin.ui.XuanPinActivity;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.net.utils.json.JsonMananger.jsonToBean;

public class MyFragmentNewThree extends BaseFragment {

    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.vip_level)
    TextView vipLevel;
    @BindView(R.id.upgrade)
    RelativeLayout upgrade;
    @BindView(R.id.activity_num)
    TextView activity_num;
    @BindView(R.id.activity_um_ll)
    LinearLayout activityUmLl;
    @BindView(R.id.gold_egg_num)
    TextView goldEggNum;
    @BindView(R.id.my_gold_egg)
    LinearLayout myGoldEgg;
    @BindView(R.id.yin_egg_num)
    TextView yinEggNum;
    @BindView(R.id.my_yin_egg)
    LinearLayout myYinEgg;
    @BindView(R.id.chou_num)
    TextView chouNum;
    @BindView(R.id.my_bad_egg)
    LinearLayout myBadEgg;
    @BindView(R.id.below)
    LinearLayout below;
    @BindView(R.id.autorImg)
    CircleImageView autorImg;
    @BindView(R.id.tip_update_tx)
    TextView tip_update_tx;
    @BindView(R.id.tip_update)
    LinearLayout tip_update;
    @BindView(R.id.choujiang_tips)
    LinearLayout choujiangTips;
    @BindView(R.id.tartget_current_activity_num)
    TextView tartget_current_activity_num;
    @BindView(R.id.tartget_need_activity_num)
    TextView tartgetNeedActivityNum;
    @BindView(R.id.reward_des)
    TextView rewardDes;
    @BindView(R.id.twwww)
    LinearLayout twwww;
    @BindView(R.id.recive_btn_two)
    TextView recive_btn_two;
    @BindView(R.id.recive_progress)
    TextView recive_progress;
    @BindView(R.id.recive_btn)
    LinearLayout reciveBtn;
    @BindView(R.id.target_reward_ll)
    LinearLayout targetRewardLl;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.buy_gold)
    ImageView buyGold;
    @BindView(R.id.xuanppin_center)
    ImageView xuanppinCenter;
    @BindView(R.id.my_xuanpin)
    ImageView myXuanpin;
    @BindView(R.id.task_center)
    ImageView taskCenter;
    @BindView(R.id.invite_friend)
    ImageView inviteFriend;
    @BindView(R.id.my_team)
    LinearLayout myTeam;
    @BindView(R.id.shangcheng_order)
    LinearLayout shangchengOrder;
    @BindView(R.id.income)
    LinearLayout income;
    @BindView(R.id.kefu)
    LinearLayout kefu;
    @BindView(R.id.help_reward)
    LinearLayout helpReward;
    @BindView(R.id.anquan_center)
    LinearLayout anquanCenter;
    @BindView(R.id.ziying_shangcheng)
    LinearLayout ziyingShangcheng;
    @BindView(R.id.shenqingzhubo)
    LinearLayout shenqingzhubo;
    @BindView(R.id.shangjia_ruzhu)
    LinearLayout shangjiaRuzhu;
    Unbinder unbinder;
    @BindView(R.id.view_status_bar)
    View viewStatusBar;

    @BindView(R.id.ss)
    LinearLayout ss;

    @BindView(R.id.msg_ll)
    RelativeLayout msgLl;
    @BindView(R.id.user_account)
    TextView userAccount;
    @BindView(R.id.copy)
    ImageView copy;
    @BindView(R.id.msg)
    ImageView msg;
    @BindView(R.id.chou_tips)
    ImageView chou_tips;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_my_new_three;
    }

    @Override
    protected void initAction() {
        Glide.with(this).asGif().load(R.drawable.choubianjin).into(chou_tips);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewStatusBar.getLayoutParams();
        //获取当前控件的布局对象
        params1.height = SystemUtils.getStatusBarHeight(getContext());//设置当前控件布局的高度
        params1.width = MATCH_PARENT;
        viewStatusBar.setLayoutParams(params1);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) xuanppinCenter.getLayoutParams();
        //获取当前控件的布局对象

        params.width = (SystemUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 43)) / 2;
        params.height = params.width * 80 / 164;
        xuanppinCenter.setLayoutParams(params);

        LinearLayout.LayoutParams paramsbuyGold = (LinearLayout.LayoutParams) buyGold.getLayoutParams();
        //获取当前控件的布局对象

        paramsbuyGold.width = (SystemUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 43)) / 2;
        paramsbuyGold.height = paramsbuyGold.width * 80 / 164;
        buyGold.setLayoutParams(paramsbuyGold);

        LinearLayout.LayoutParams paramsmyXuanpin = (LinearLayout.LayoutParams) myXuanpin.getLayoutParams();
        //获取当前控件的布局对象

        paramsmyXuanpin.width = (SystemUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 64)) / 3;
        paramsmyXuanpin.height = paramsmyXuanpin.width * 60 / 104;
        myXuanpin.setLayoutParams(paramsmyXuanpin);


        LinearLayout.LayoutParams paramsmytaskCenter = (LinearLayout.LayoutParams) taskCenter.getLayoutParams();
        //获取当前控件的布局对象

        paramsmytaskCenter.width = (SystemUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 64)) / 3;
        paramsmytaskCenter.height = paramsmytaskCenter.width * 60 / 104;
        taskCenter.setLayoutParams(paramsmytaskCenter);


        LinearLayout.LayoutParams paramsinviteFriend = (LinearLayout.LayoutParams) inviteFriend.getLayoutParams();
        //获取当前控件的布局对象

        paramsinviteFriend.width = (SystemUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 64)) / 3;
        paramsinviteFriend.height = paramsinviteFriend.width * 60 / 104;
        inviteFriend.setLayoutParams(paramsinviteFriend);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.setClipboard(getActivity(), UserInfoUtil.getPhoneNumber());
            }
        });

    }

    @Override
    protected void initEvent() {
        queryConverMsg();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getUserInfoData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void getUserInfoData() {
        if (!TextUtils.isEmpty(UserInfoUtil.getAvatar()))

            Glide.with(mContext)
                    .load(CommonUtils.getUrl(UserInfoUtil.getAvatar()))
                    .into(autorImg);
        username.setText(UserInfoUtil.getName());
        userAccount.setText(getString(R.string.account) + UserInfoUtil.getPhoneNumber());
        getUserInfo();
        loadChoujiangData();
        getTargetReward();
        getUserStatus();

    }

    private void getUserStatus() {
        OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                if (!data.can_selectt_prize) {
                    tip_update.setVisibility(View.VISIBLE);
                    tip_update_tx.setText(data.hint1);
                } else tip_update.setVisibility(View.GONE);

            }
        });
    }

    public void choujiangSuccess() {
        getTargetReward();
        choujiangTips.setVisibility(View.GONE);
    }

    public void loadChoujiangData() {

        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

        // LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result)) return;
                    PriceResponse mResponse = jsonToBean(result, PriceResponse.class);
                    int size = mResponse.list.size();
                    if (size > 0) {
                        choujiangTips.setVisibility(View.VISIBLE);
                    } else {
                        choujiangTips.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    LoadDialog.dismiss(getActivity());
                }
            }
        });
    }

    private void getUserInfo() {
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                finishRefresh();
                initDatas(result);
            }
        });
    }

    TargetRewardResponse data;

    private void receiveTargetReward() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", data.id)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getPrize", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(getActivity(), errInfo);
                LoadDialog.dismiss(getActivity());
            }

            @Override
            public void onSuccess(String result) {
                getTargetReward();
                LoadDialog.dismiss(getActivity());
                DialogUitl.showSimpleHintDialog(getActivity(), "", getString(R.string.confirm),
                        "",
                        getString(R.string.gongxi_lingqu_success) + data.target_reward_value,
                        true,
                        false,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                            }
                        });
//                NToast.shortToast(TargetRewardActivity.this, getString(R.string.gongxi_lingqu_success) + data.target_reward_value_unit + data.target_reward_value);
//                loadData();
            }
        });
    }

    private void getTargetReward() {
        OKHttpUtils.getInstance().getRequest("app/prize/getPrizeInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                //npb.setProgress(targetProgress);
            }

            @Override
            public void onSuccess(final String result) {
                //  if (targetRewardLl.getVisibility() == View.GONE) {
                targetRewardLl.setVisibility(View.VISIBLE);
                //   getTargetReward();
                // } else {

                initTargetRewardDatas(result);

                // }
            }
        });
    }

    Handler handler = new Handler();

    UserInfoResponse userData;
    private int personActivityNum;
    private String vip_level = "0";
    private String gold_egg;
    private String silver_egg;
    private String bad_egg;

    private void initDatas(String result) {
        if (result == null) {
            return;
        }


        UserInfoResponse data = new Gson().fromJson(result, UserInfoResponse.class);
        userData = data;
        if (!TextUtils.isEmpty(data.avatar)) {
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(data.avatar))
                    .into(autorImg);
        }
        username.setText(data.user_nickname);


        UserInfoUtil.setUserInfo(
                data.sex,
                data.daren_status,
                data.is_vip,
                data.speech_introduction,
                data.auth_status,
                data.province_name + "," + data.city_name + "," + data.district_name,
                data.tags,
                data.age,
                data.signature);
        UserInfoUtil.setInviteCode(data.invite_code);

        personActivityNum = data.person_activity_num;
        vip_level = data.vip_level;


        vipLevel.setText(data.level_name + data.level_title);
        activity_num.setText(personActivityNum + "");
        gold_egg = data.gold_egg;
        silver_egg = data.silver_egg;
        bad_egg = data.bad_egg;
        goldEggNum.setText(NumUtil.dealNumByScale(gold_egg, 2));
        yinEggNum.setText(NumUtil.dealNumByScale(silver_egg, 2));
        chouNum.setText(NumUtil.dealNumByScale(bad_egg, 2));

    }

    float count;
    float targetProgress = 0;
    boolean isrecive = true;
    String tartgetReward;

    private void initTargetRewardDatas(String result) {
        if (TextUtils.isEmpty(result)) {
            targetRewardLl.setVisibility(View.GONE);
            return;
        }


        data = new Gson().fromJson(result, TargetRewardResponse.class);

        tartgetReward = data.target_reward_value;
        count = data.target_reward_count;
        targetProgress = data.target_reward_progress;
        isrecive = data.target_reward_is_get;
        tartget_current_activity_num.setText(data.team_activity_num);
        recive_progress.setText(getString(R.string.has_complete) + targetProgress + "%");
        tartgetNeedActivityNum.setText(NumUtil.clearZero(data.target_reward_need_activity_num));
        if (TextUtils.isEmpty(data.target_reward)) {
            targetRewardLl.setVisibility(View.GONE);
        }
        reciveBtn.setEnabled(!isrecive);
        recive_btn_two.setText(targetProgress >= 100 && isrecive ? getString(R.string.yilingqu) : getString(R.string.receive_reward));

        rewardDes.setText(data.target_reward_value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static final int CAMERA_REQ_CODE = 111;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;

    /**
     * Apply for permissions.
     */
    private void decodePermission(int requestCode) {
        requestPermissions(
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            if (data == null || data.getParcelableExtra(ScanUtil.RESULT) == null) return;
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            //MultiProcessor & Bitmap
            qiandao(obj.showResult);
        }
    }

    private void qiandao(String content) {
        LoadDialog.show(getActivity());
        String body = "";
        try {
            body = new JsonBuilder()
                    // .put("content", "type=1&name=20210403长沙活动")
                    .put("content", content.replace("\ufeff", ""))
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/activity/signIn", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getActivity());
                //npb.setProgress(targetProgress);
              //  NToast.shortToast(getContext(), errInfo);
//                new XPopup.Builder(getContext()).asConfirm("签到失败", errInfo,
//                        new OnConfirmListener() {
//                            @Override
//                            public void onConfirm() {
//                            }
//                        })
//                        .show();
                AlertDialog.Builder  builder = new AlertDialog.Builder(getContext())
                        .setMessage(errInfo).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //ToDo: 你想做的事情
                            }
                        });
                builder.create().show();
            }

            @Override
            public void onSuccess(final String result) {
                LoadDialog.dismiss(getActivity());
                //  if (targetRewardLl.getVisibility() == View.GONE) {
                // NToast.shortToast(getContext(),"签到成功");
//                new XPopup.Builder(getContext()).asConfirm("签到成功", "",
//                        new OnConfirmListener() {
//                            @Override
//                            public void onConfirm() {
//                            }
//                        })
//                        .show();
                AlertDialog.Builder  builder = new AlertDialog.Builder(getContext())
                        .setMessage("签到成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //ToDo: 你想做的事情
                            }
                        });
                builder.create().show();
            }
        });
    }

    @OnClick({R.id.mqiandao_ll, R.id.msg_ll, R.id.more, R.id.upgrade, R.id.activity_um_ll, R.id.my_gold_egg, R.id.my_yin_egg, R.id.my_bad_egg, R.id.autorImg, R.id.tip_update, R.id.choujiang_tips, R.id.recive_btn, R.id.buy_gold, R.id.xuanppin_center, R.id.my_xuanpin, R.id.task_center, R.id.invite_friend, R.id.my_team, R.id.shangcheng_order, R.id.income, R.id.kefu, R.id.help_reward, R.id.anquan_center, R.id.ziying_shangcheng, R.id.shenqingzhubo, R.id.shangjia_ruzhu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mqiandao_ll:
                decodePermission(CAMERA_REQ_CODE);
                break;
            case R.id.msg_ll:
                Intent intents = new Intent(getContext(), MsgActivity.class);
                startActivity(intents);
                break;
            case R.id.more:
                Intent intentMore = new Intent();
                intentMore.setClass(getActivity(), MyMoreActivity.class);
                getActivity().startActivityForResult(intentMore, 10002);

                break;
            case R.id.tip_update:

            case R.id.upgrade:
                startActivity(new Intent(getActivity(), LevelActivity.class));
                break;
            case R.id.activity_um_ll:
                startActivity(new Intent(getActivity(), ActivityNumActivity.class));
                break;
            case R.id.my_gold_egg:
                startActivity(new Intent(getActivity(), GoldEggActivity.class));
                break;
            case R.id.my_yin_egg:
                //  startActivity(new Intent(getActivity(), SilverEggActivity.class));
                startActivity(new Intent(getActivity(), NewSilverEggActivity.class));
                break;
            case R.id.my_bad_egg:
                startActivity(new Intent(getActivity(), BadEggActivity.class));
                break;
            case R.id.autorImg:
                Intent intentHead = new Intent();
                intentHead.setClass(getActivity(), PersonalInformationActivity.class);
                getActivity().startActivity(intentHead);
                break;

            case R.id.choujiang_tips:
                ChoujiangFragment fragmentRotary = new ChoujiangFragment();
                fragmentRotary.setHomeActivity((HomeActivity) getActivity());
                fragmentRotary.setmyFragmentNewThree(this);
                fragmentRotary.show(getChildFragmentManager(), "ChoujiangFragment");
                break;
            case R.id.recive_btn:
                if (data != null && targetProgress >= 100 && !isrecive) {
                    ShowTargetDialog dialogFragment = new ShowTargetDialog();
                    dialogFragment.setTitle(data.target_reward_des);
                    dialogFragment.setLogo(data.target_reward_icon);
                    dialogFragment.isRealReward(data.is_real_reward);
                    dialogFragment.setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data != null && data.is_real_reward) {
                                Intent intent = new Intent(getContext(), TargetReciveDetailActivity.class);
                                startActivity(intent);
                                return;
                            }
                            receiveTargetReward();
                        }
                    });

                    dialogFragment.show(getChildFragmentManager(), "ShowTargetDialog");

                }
                if (data != null && targetProgress < 100) {
                    NToast.shortToast(getActivity(), getString(R.string.haiweiwancheng));
                }

                break;
            case R.id.buy_gold:
                startActivity(new Intent(getActivity(), MainOtcActivity.class));
                break;
            case R.id.xuanppin_center:
                Intent xuanpin = new Intent();
                xuanpin.setClass(getActivity(), XuanPinActivity.class);
                getActivity().startActivity(xuanpin);
                break;
            case R.id.my_xuanpin:
                Intent myxuanpin = new Intent();
                myxuanpin.setClass(getActivity(), MyXuanPinActivity.class);
                getActivity().startActivity(myxuanpin);
                break;
            case R.id.task_center:
                LoadDialog.show(getContext());
                OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(getContext());
                        Intent task = new Intent();
                        task.setClass(getActivity(), TaskNewCenterActivity.class);
                        getActivity().startActivityForResult(task, 10001);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(getContext());
                        try {
                            if (TextUtils.isEmpty(result)) {
                                Intent task = new Intent();
                                task.setClass(getActivity(), TaskNewCenterActivity.class);
                                getActivity().startActivityForResult(task, 10001);
                                return;
                            }
                            UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                            if (data.jump != null) {
                                if (data.jump.task_is_h5 == 1) {
                                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                    if (!TextUtils.isEmpty(data.jump.task_h5)) {
                                        intent.putExtra("url", data.jump.task_h5);
                                    }
                                    startActivity(intent);
                                } else {
                                    Intent task = new Intent();
                                    task.setClass(getActivity(), TaskNewCenterActivity.class);
                                    getActivity().startActivityForResult(task, 10001);
                                }
                            } else {
                                Intent task = new Intent();
                                task.setClass(getActivity(), TaskNewCenterActivity.class);
                                getActivity().startActivityForResult(task, 10001);
                            }
                        } catch (Exception e) {
                            Intent task = new Intent();
                            task.setClass(getActivity(), TaskNewCenterActivity.class);
                            getActivity().startActivityForResult(task, 10001);
                        }
                    }
                });


                break;
            case R.id.invite_friend:
                Intent invite = new Intent();
                invite.setClass(getActivity(), InviteFriendActivity.class);
                getActivity().startActivity(invite);

                break;
            case R.id.my_team:
                Intent intent = new Intent(getActivity(), MyTeamActivity.class);
                intent.putExtra("user_name", username.getText().toString());
                intent.putExtra("vip_level", TextUtils.isEmpty(vip_level) ? "0" : vip_level);
                startActivity(intent);
                break;
            case R.id.shangcheng_order:
                NToast.shortToast(getContext(), "暂未开放");
                break;
            case R.id.income:
                startActivity(new Intent(getActivity(), ShouZhiAtivity.class));
                break;
            case R.id.kefu:
                LoadDialog.show(getContext());
                String body = "";
                OKHttpUtils.getInstance().getRequest("app/user/getKefu", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(getContext());
                        NToast.shortToast(mContext, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(getContext());
                        try {
                            KefuResponse mResponse = jsonToBean(result, KefuResponse.class);
                            IMConversation imConversation = new IMConversation();
                            imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

                            imConversation.setUserIMId(UserInfoUtil.getMiTencentId());
                            imConversation.setUserId(UserInfoUtil.getMiPlatformId());

                            imConversation.setOtherPartyIMId(String.valueOf(mResponse.user_id));
                            imConversation.setOtherPartyId(String.valueOf(mResponse.user_id));

                            imConversation.setUserName(UserInfoUtil.getName());
                            imConversation.setUserAvatar(UserInfoUtil.getAvatar());

                            imConversation.setOtherPartyName(mResponse.user_nickname);
                            imConversation.setOtherPartyAvatar(CommonUtils.getUrl(mResponse.avatar));

                            imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());


                            Intent intent1 = new Intent(mContext, MessageListActivity.class);
                            intent1.putExtra("IMConversation", imConversation);
                            startActivity(intent1);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.help_reward:
                Intent zhutui = new Intent();
                zhutui.setClass(getActivity(), ZhutuiActivity.class);
                getActivity().startActivity(zhutui);
                break;
            case R.id.anquan_center:
                //NToast.shortToast(getContext(), "暂未开放");
//                Intent huodong = new Intent();
//              huodong.setClass(getActivity(), HuodongCenterActivity.class);
//                //   huodong.setClass(getActivity(), TestH5Activity.class);
//                getActivity().startActivity(huodong);
                LoadDialog.show(getContext());
                OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(getContext());
                        Intent huodong = new Intent();
                        huodong.setClass(getActivity(), HuodongCenterActivity.class);
                        getActivity().startActivity(huodong);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(getContext());
                        try {
                            if (TextUtils.isEmpty(result)) {
                                Intent huodong = new Intent();
                                huodong.setClass(getActivity(), HuodongCenterActivity.class);
                                getActivity().startActivity(huodong);
                                return;
                            }
                            UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                            if (data.jump != null) {
                                if (data.jump.huodong_is_h5 == 1) {
                                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                    if (!TextUtils.isEmpty(data.jump.huodong_h5)) {
                                        intent.putExtra("url", data.jump.huodong_h5);
                                    }
                                    startActivity(intent);
                                } else {
                                    Intent huodong = new Intent();
                                    huodong.setClass(getActivity(), HuodongCenterActivity.class);
                                    getActivity().startActivity(huodong);
                                }
                            } else {
                                Intent huodong = new Intent();
                                huodong.setClass(getActivity(), HuodongCenterActivity.class);
                                getActivity().startActivity(huodong);
                            }
                        } catch (Exception e) {
                            Intent huodong = new Intent();
                            huodong.setClass(getActivity(), HuodongCenterActivity.class);
                            getActivity().startActivity(huodong);
                        }
                    }
                });
                break;
            case R.id.ziying_shangcheng:
                loadUserConfig();
                //NToast.shortToast(getContext(),"暂未开放");
                break;
            case R.id.shenqingzhubo:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), DaRenRenZhengActivity.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.shangjia_ruzhu:
                NToast.shortToast(getContext(), "暂未开放");
                break;
        }
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

    String mi_platformId;
    private RealmResults<IMConversationDB> query;

    private void queryConverMsg() {
        mi_platformId = UserInfoUtil.getMiPlatformId();
        final Realm mRealm = Realm.getDefaultInstance();
        query = mRealm.where(IMConversationDB.class).equalTo("userIMId", UserInfoUtil.getMiPlatformId()).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<IMConversationDB>>() {
            @Override
            public void onChange(RealmResults<IMConversationDB> element) {
                element = element.sort("timestamp", Sort.DESCENDING);//时间戳 增序
                List<IMConversationDB> datas = mRealm.copyFromRealm(element);

                //
                long readCount_like = 0;
                long readCount_reply = 0;

                long readCount = 0;
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 2) {
                        readCount = datas.get(i).getUnreadCount();

                        datas.remove(i);
                    }
                }

                //
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 5) {
                        readCount_like = datas.get(i).getUnreadCount();
                        datas.remove(i);
                    }
                }
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getType() == 6) {
                        readCount_reply = datas.get(i).getUnreadCount();
                        datas.remove(i);
                    }
                }

                for (int i = 0; i < datas.size(); i++) {
                    readCount = readCount + datas.get(i).getUnreadCount();
                }
                //圈子消息数量
                if ((int) readCount == 0) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msg.getLayoutParams();
                    //获取当前控件的布局对象

                    params.width = (SystemUtils.dip2px(getContext(), 42));
                    params.height = params.width;
                    msg.setLayoutParams(params);

                    Glide.with(getActivity()).load(R.drawable.community_reply).into(msg);
//                    msg_red.setVisibility(View.INVISIBLE);
//                    communityUnreadNum.setVisibility(View.INVISIBLE);
//                    communityUnreadNum.setText("");
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msg.getLayoutParams();
                    //获取当前控件的布局对象

                    params.width = (SystemUtils.dip2px(getContext(), 48));
                    params.height = params.width;
                    msg.setLayoutParams(params);
                    Glide.with(getActivity()).asGif().load(R.drawable.message).into(msg);
//                    msg_red.setVisibility(View.VISIBLE);
//                    communityUnreadNum.setVisibility(View.VISIBLE);
//                    communityUnreadNum.setText((int) readCount > 99 ? "99+" : readCount + "");
                }
                // mTabbar.showBadge(3, (int) readCount, true);

                ShortcutBadger.applyCount(App.getInstance(), (int) readCount); //for 1.1.4+
                //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfoData();
    }


}
