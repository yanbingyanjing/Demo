/*
package com.yjfshop123.live.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.yjfshop123.live.App;
import com.yjfshop123.live.net.utils.NToast;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    private int code = -3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //     setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, App.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        code = resp.errCode;
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
//                RequestParams requestParams = new RequestParams(UrlConstants.HOST_SITE_HTTPS_INDEX + UrlConstants.CHARGE_QUERY);
//                requestParams.addHeader("appkey", App.loginSmsBean.getAppkey());
//                requestParams.addHeader("accessToken", App.loginSmsBean.getAccesstoken());
//                requestParams.addBodyParameter("out_trade_no", CommonConstants.out_no_trade);
//                requestParams.addBodyParameter("userid", App.loginSmsBean.getUserid());
//                XutilsHttp.getInstance().getData(requestParams, new XPostCallBack() {
//                    @Override
//                    public void onSuccess(String result) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(result);
//                            int error = jsonObject.getInt("error");
//                            if (error == 0) {
//                                EventBus.getDefault().post("success");
                                NToast.shortToast(WXPayEntryActivity.this, "充值成功");
//                                MobclickAgent.onEvent(WXPayEntryActivity.this, "rechangeSuccess");
//                            } else {
//                                EventBus.getDefault().post("failure");
//                                NToast.shortToast(WXPayEntryActivity.this, "充值失败");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(String result) {
//
//                    }
//
//                    @Override
//                    public void onFinished() {
//                        finish();
//                    }
//                });
                //         ToastUtil.show("支付成功");
            } else if (resp.errCode == -2) {
                NToast.shortToast(WXPayEntryActivity.this, "支付取消");
            } else {
                NToast.shortToast(WXPayEntryActivity.this, "支付失败");
            }
        }
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("com.yjfshop123.live");
        intent.putExtra("code", code);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        return true;
    }
}*/
