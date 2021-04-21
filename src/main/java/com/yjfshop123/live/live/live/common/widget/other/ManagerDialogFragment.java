package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.net.utils.NToast;

import org.json.JSONException;

public class ManagerDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private String userId;//用户ID
    private String meUserId;//自己ID
    private String liveUid;//主播ID
    private String mLiveId;//直播间ID
    private String is_banspeech;//是否被禁言
    private String is_manager;

    private FrameLayout dialog_live_manager_1 ;
    private FrameLayout dialog_live_manager_2 ;
    private FrameLayout dialog_live_manager_3 ;
    private FrameLayout dialog_live_manager_4 ;
    private FrameLayout dialog_live_manager_5 ;
    private FrameLayout dialog_live_manager_6 ;
    private TextView dialog_live_manager_2_tv;
    private TextView dialog_live_manager_4_tv;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_manager;
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
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
        mLiveId = bundle.getString("mLiveId");
        is_banspeech = bundle.getString("is_banspeech");
        is_manager = bundle.getString("is_manager");

        dialog_live_manager_1 = (FrameLayout) findViewById(R.id.dialog_live_manager_1);
        dialog_live_manager_2 = (FrameLayout) findViewById(R.id.dialog_live_manager_2);
        dialog_live_manager_3 = (FrameLayout) findViewById(R.id.dialog_live_manager_3);
        dialog_live_manager_4 = (FrameLayout) findViewById(R.id.dialog_live_manager_4);
        dialog_live_manager_5 = (FrameLayout) findViewById(R.id.dialog_live_manager_5);
        dialog_live_manager_6 = (FrameLayout) findViewById(R.id.dialog_live_manager_6);
        dialog_live_manager_2_tv = (TextView) findViewById(R.id.dialog_live_manager_2_tv);
        dialog_live_manager_4_tv = (TextView) findViewById(R.id.dialog_live_manager_4_tv);
        dialog_live_manager_1.setOnClickListener(this);
        dialog_live_manager_2.setOnClickListener(this);
        dialog_live_manager_3.setOnClickListener(this);
        dialog_live_manager_4.setOnClickListener(this);
        dialog_live_manager_5.setOnClickListener(this);
        dialog_live_manager_6.setOnClickListener(this);


        //userId;//用户ID
        //meUserId;//自己ID
        //liveUid;//主播ID
        if (meUserId.equals(liveUid)){
            //主播
            if (!is_manager.equals("0")){
                dialog_live_manager_4_tv.setText("取消管理");
            }
        }else {
            dialog_live_manager_4.setVisibility(View.GONE);
            dialog_live_manager_5.setVisibility(View.GONE);
        }

        if (!is_banspeech.equals("0")){
            //被禁言
            dialog_live_manager_3.setVisibility(View.GONE);
            dialog_live_manager_2_tv.setText("解除禁言");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dialog_live_manager_1:
                //踢人
                String body_ = "";
                try {
                    body_ = new JsonBuilder()
                            .put("remove_uid", userId)//被踢者id
                            .put("live_id", mLiveId)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/live/removeUser", body_, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(mContext, errInfo);
                        dismiss();
                    }
                    @Override
                    public void onSuccess(String result) {
                        NToast.shortToast(mContext, "已踢出直播间");
                        dismiss();
                    }
                });
                break;
            case R.id.dialog_live_manager_2:
                if (!is_banspeech.equals("0")){
                    //解除禁言
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("banspeech_uid", userId)//被禁言者id
                                .put("live_id", mLiveId)//直播间id
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/live/unbanspeechUser", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            dismiss();
                        }
                        @Override
                        public void onSuccess(String result) {
                            NToast.shortToast(mContext, "解除禁言成功");
                            dismiss();
                        }
                    });
                }else {
                    //永久禁言
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("type", "2")//类型（1:本场禁言 2:永久禁言）
                                .put("banspeech_uid", userId)//被禁言者id
                                .put("live_id", mLiveId)
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/live/banspeechUser", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            dismiss();
                        }
                        @Override
                        public void onSuccess(String result) {
                            NToast.shortToast(mContext, "永久禁言成功");
                            dismiss();
                        }
                    });
                }
                break;
            case R.id.dialog_live_manager_3:
                if (is_banspeech.equals("0")){
                    //本场禁言
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("type", "1")//类型（1:本场禁言 2:永久禁言）
                                .put("banspeech_uid", userId)//被禁言者id
                                .put("live_id", mLiveId)
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/live/banspeechUser", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            dismiss();
                        }
                        @Override
                        public void onSuccess(String result) {
                            NToast.shortToast(mContext, "本场禁言成功");
                            dismiss();
                        }
                    });
                }
                break;
            case R.id.dialog_live_manager_4:
                if (!is_manager.equals("0")){
                    //直播间删除管理员
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("manager_uid", userId)//管理员uid
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/live/delManager", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            dismiss();
                        }
                        @Override
                        public void onSuccess(String result) {
                            NToast.shortToast(mContext, "取消管理成功");
                            dismiss();
                        }
                    });
                }else {
                    //设为管理
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("manager_uid", userId)//管理员uid
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/live/addManager", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                            dismiss();
                        }
                        @Override
                        public void onSuccess(String result) {
                            NToast.shortToast(mContext, "设为管理成功");
                            dismiss();
                        }
                    });
                }
                break;
            case R.id.dialog_live_manager_5:
                //管理员列表
                dismiss();
                if (mContext instanceof TCLiveBasePublisherActivity) {
                    ((TCLiveBasePublisherActivity) mContext).managerList();
                }
                break;
            case R.id.dialog_live_manager_6:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

