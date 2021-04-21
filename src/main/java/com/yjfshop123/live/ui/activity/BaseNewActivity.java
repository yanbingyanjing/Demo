package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.order.CtcBuyOrderDetailActivity;
import com.yjfshop123.live.ctc.order.CtcSellOrderDetailActivity;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptBean;
import com.yjfshop123.live.model.VIPEvent;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.otc.view.OtcTipsDialog;
import com.yjfshop123.live.ui.widget.ShowTargetDialog;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.VerfyTipsDialog;
import com.yjfshop123.live.ui.widget.float_view.FloatViewListener;
import com.yjfshop123.live.ui.widget.float_view.FloatWindowManager;
import com.yjfshop123.live.ui.widget.float_view.IFloatView;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.CheckIsInstallExchangeUtils;
import com.yjfshop123.live.utils.update.UpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public abstract class BaseNewActivity extends FragmentActivity {
    VerfyTipsDialog verfyTipsDialog;

    public boolean is_need_check_verfy = true;
    public boolean is_need_show_flow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        verfyTipsDialog = new VerfyTipsDialog(this);
    }

    public void onCheckIsInstallExchange() {
        CheckIsInstallExchangeUtils updateUtils = new CheckIsInstallExchangeUtils(this);
        updateUtils.updateDiy();
    }

    boolean is_first = true;


    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;

    }

    protected abstract View getRootView();


    boolean isPause = true;

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
    @Subscriber(tag = Config.EVENT_task)
    public void onTaskTipsMessage(String content) {
        if (isPause) return;
        if (TextUtils.isEmpty(content)) {
            return;
        }
        try {
            JSONObject jso = null;
            jso = new JSONObject(content);
            String code = jso.getString("code");
            if (code.equals("task")) {
                JSONObject data = jso.getJSONObject("data");
                String cmd = data.getString("cmd");
                String tips = data.getJSONObject("content").getString("desc");
                ShowTargetDialog dialogFragment = new ShowTargetDialog();
                dialogFragment.setTitle(tips);
                dialogFragment.setTopTitle("每日完成");
                dialogFragment.show(getSupportFragmentManager(), "ShowTargetDialog");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscriber(tag = Config.EVENT_OTC)
    public void onOtcTipsMessage(String content) {
        if (isPause) return;
        if (TextUtils.isEmpty(content)) {
            return;
        }
        try {
            JSONObject jso = null;
            jso = new JSONObject(content);
            String code = jso.getString("code");
            if (code.equals("otc")) {
                JSONObject data = jso.getJSONObject("data");
                String cmd = data.getString("cmd");
                //if (cmd.equals("otc_receive_eggs")||cmd.equals("otc_sell_eggs")) {
                String tips = data.getJSONObject("content").getString("desc");
                OtcTipsDialog dialogFragment = new OtcTipsDialog();
                dialogFragment.setTitle(tips);
                dialogFragment.show(getSupportFragmentManager(), "OtcTipsDialog");
                // }
            }

            if (code.equals("c2c")) {
                final JSONObject data = jso.getJSONObject("data");
                final String cmd = data.getString("cmd");

                String tips = data.getJSONObject("content").getString("desc");
                final String order = data.getJSONObject("content").getString("order");
                final OtcTipsDialog dialogFragment = new OtcTipsDialog();
                dialogFragment.setTitle(tips);
                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cmd.equals("c2c_buy_release")) {
                            Intent intent = new Intent(BaseNewActivity.this, CtcSellOrderDetailActivity.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                            dialogFragment.dismiss();
                        }
                        if (cmd.equals("c2c_sell_release")) {
                            Intent intent = new Intent(BaseNewActivity.this, CtcBuyOrderDetailActivity.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                            dialogFragment.dismiss();
                        }
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "OtcTipsDialog");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}