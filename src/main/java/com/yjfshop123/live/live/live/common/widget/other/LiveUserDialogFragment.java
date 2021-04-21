package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.UserInfo4LiveResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;

public class LiveUserDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private String userId;//用户ID
    private String meUserId;//自己ID
    private String liveUid;//主播ID
    private String liveID;
    private TextView dialog_live_user_name, dialog_live_user_id, dialog_live_user_signature, dialog_live_user_be_follow_num, dialog_live_user_follow_num, dialog_live_user_used_coin;
    private ImageView dialog_live_user_icon, dialog_live_user_sex;
    private TextView dialog_live_user_follow, dialog_live_user_home;
    private View dialog_live_user_divider;
    private ImageView dialog_live_user_set;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_user;
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
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = CommonUtils.dip2px(mContext, 285);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        userId = bundle.getString("user_id");
        meUserId = bundle.getString("meUserId");
        liveUid = bundle.getString("liveUid");
        liveID = bundle.getString("liveID");

        dialog_live_user_name = (TextView) findViewById(R.id.dialog_live_user_name);
        dialog_live_user_sex = (ImageView) findViewById(R.id.dialog_live_user_sex);
        dialog_live_user_id = (TextView) findViewById(R.id.dialog_live_user_id);
        dialog_live_user_signature = (TextView) findViewById(R.id.dialog_live_user_signature);
        dialog_live_user_be_follow_num = (TextView) findViewById(R.id.dialog_live_user_be_follow_num);
        dialog_live_user_follow_num = (TextView) findViewById(R.id.dialog_live_user_follow_num);
        dialog_live_user_used_coin = (TextView) findViewById(R.id.dialog_live_user_used_coin);
        dialog_live_user_icon = (ImageView) findViewById(R.id.dialog_live_user_icon);

        dialog_live_user_follow = (TextView) findViewById(R.id.dialog_live_user_follow);
        dialog_live_user_home = (TextView) findViewById(R.id.dialog_live_user_home);
        dialog_live_user_home.setOnClickListener(this);
        dialog_live_user_divider = findViewById(R.id.dialog_live_user_divider);
        dialog_live_user_set = (ImageView) findViewById(R.id.dialog_live_user_set);
        dialog_live_user_set.setOnClickListener(this);

        if (userId.equals(meUserId)) {
            dialog_live_user_follow.setVisibility(View.GONE);
            dialog_live_user_divider.setVisibility(View.GONE);
        } else {
            dialog_live_user_follow.setOnClickListener(this);
            dialog_live_user_follow.setVisibility(View.VISIBLE);
            dialog_live_user_divider.setVisibility(View.VISIBLE);
        }

        loadData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_live_user_home) {
            dismiss();

            ActivityUtils.startUserHome(mContext, userId);
            getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        } else if (v.getId() == R.id.dialog_live_user_follow) {
            if (is_follow == -100) {

            } else if (is_follow == 0) {
                addGZ();
            } else {
                NToast.shortToast(mContext, "您已关注了该用户");
            }
        } else if (v.getId() == R.id.dialog_live_user_set) {
            dismiss();
            if (mContext instanceof TCLiveBasePublisherActivity) {
                ((TCLiveBasePublisherActivity) mContext).manager(response);
            } else {
                ((TCLivePlayerActivity) mContext).manager(response);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addGZ() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("be_user_id", userId)
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
                is_follow = 1;
                dialog_live_user_follow.setText("已关注");
                NToast.shortToast(mContext, "已关注");

                if (mContext instanceof TCLivePlayerActivity) {
                    ((TCLivePlayerActivity) mContext).addgz(userId);
                }
            }
        });
    }

    private UserInfo4LiveResponse response;

    private void loadData() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", userId)
                    .put("live_id", liveID)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/getUserInfo4Live", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    response = JsonMananger.jsonToBean(result, UserInfo4LiveResponse.class);
                    dialog_live_user_name.setText(response.getUser_info().getUser_nickname());
                    dialog_live_user_id.setText("用户ID：" + response.getUser_info().getUser_id());
                    dialog_live_user_signature.setText(response.getUser_info().getSignature());
                    dialog_live_user_be_follow_num.setText("" + response.getUser_info().getBe_follow_num());
                    dialog_live_user_follow_num.setText("" + response.getUser_info().getFollow_num());
                    dialog_live_user_used_coin.setText("" + response.getUser_info().getUsed_coin());
                    if (!TextUtils.isEmpty(response.getUser_info().getAvatar())) {
                        Glide.with(mContext)
                                .load(CommonUtils.getUrl(response.getUser_info().getAvatar()))
                                .into(dialog_live_user_icon);
                    }else {
                        Glide.with(mContext)
                                .load(R.drawable.splash_logo)
                                .into(dialog_live_user_icon);
                    }

                    is_follow = response.getIs_follow();
                    if (is_follow == 0) {
                        dialog_live_user_follow.setText("关注");
                    } else {
                        dialog_live_user_follow.setText("已关注");
                    }

                    if (response.getUser_info().getSex() == 1) {
                        dialog_live_user_sex.setImageResource(R.drawable.ic_voice_sex_man);
                    } else {
                        dialog_live_user_sex.setImageResource(R.drawable.ic_voice_sex_women);
                    }

                    //userId;//用户ID
                    //meUserId;//自己ID
                    //liveUid;//主播ID
                    if (response.getIs_manager() == 1 ||
                            meUserId.equals(liveUid)) {
                        //管理员或者主播
                        if (userId.equals(meUserId)) {
                            //看自己隐藏
                            dialog_live_user_set.setVisibility(View.GONE);
                        } else if (!meUserId.equals(liveUid) && response.getUser_info().getIs_manager() == 1) {
                            //管理员 看管理员隐藏
                            dialog_live_user_set.setVisibility(View.GONE);
                        } else if (userId.equals(liveUid)) {
                            //管理员看主播 隐藏
                            dialog_live_user_set.setVisibility(View.GONE);
                        } else {
                            dialog_live_user_set.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //非主播管理员隐藏
                        dialog_live_user_set.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int is_follow = -100;
}

