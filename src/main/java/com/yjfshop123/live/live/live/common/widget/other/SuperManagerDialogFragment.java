package com.yjfshop123.live.live.live.common.widget.other;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.net.utils.NToast;

import org.json.JSONException;

public class SuperManagerDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private String mLiveId;//直播间ID

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_super_manager;
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
        mLiveId = bundle.getString("mLiveId");

        findViewById(R.id.dialog_live_sm_1).setOnClickListener(this);
        findViewById(R.id.dialog_live_sm_2).setOnClickListener(this);
        findViewById(R.id.dialog_live_sm_3).setOnClickListener(this);
        findViewById(R.id.dialog_live_sm_4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dialog_live_sm_1:
                patrolOption("1");
                break;
            case R.id.dialog_live_sm_2:
                patrolOption("2");
                break;
            case R.id.dialog_live_sm_3:
                patrolOption("3");
                break;
            case R.id.dialog_live_sm_4:
                dismiss();
                break;
        }
    }

//    类型 1关闭直播间 2禁止主播开播 3禁止用户登录
    private void patrolOption(String type){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", mLiveId)
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/patrolOption", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "操作成功~");
                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
