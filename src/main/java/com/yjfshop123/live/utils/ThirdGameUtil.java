package com.yjfshop123.live.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.ledong.lib.leto.Leto;
import com.mgc.leto.game.base.LetoEvents;
import com.mgc.leto.game.base.LetoScene;
import com.mgc.leto.game.base.MgcAccountManager;
import com.mgc.leto.game.base.bean.LoginResultBean;
import com.mgc.leto.game.base.listener.IJumpListener;
import com.mgc.leto.game.base.listener.JumpError;
import com.mgc.leto.game.base.listener.SyncUserInfoListener;
import com.mgc.leto.game.base.mgc.thirdparty.IMintage;
import com.mgc.leto.game.base.mgc.thirdparty.MintageRequest;
import com.mgc.leto.game.base.mgc.thirdparty.MintageResult;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpRequests;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BadEggActivity;

import org.json.JSONException;

public class ThirdGameUtil {
    private static ThirdGameUtil mInstance;


    public static ThirdGameUtil getInstance() {
        synchronized (OKHttpUtils.class) {
            if (mInstance == null) {
                mInstance = new ThirdGameUtil();
            }
            return mInstance;
        }
    }

    public void openGame(Context context, String gameId) {
        Leto.getInstance().jumpMiniGameWithAppId(context, gameId, LetoScene.DEFAULT, new IJumpListener() {
            @Override
            public void onDownloaded(String s) {

            }

            @Override
            public void onLaunched() {

            }

            @Override
            public void onError(JumpError jumpError, String s) {

            }
        });
    }


    public void tongbuAccount(Context context, String id, String phone) {
        MgcAccountManager.syncAccount(context, id, phone, true, new SyncUserInfoListener() {
            @Override
            public void onSuccess(LoginResultBean data) {

                Log.i("LetoTest", "同步成功");
            }

            @Override
            public void onFail(String s, String s1) {
                Log.i("LetoTest", "同步失败，失败原因：" + s + "，" + s1);
            }
        });
    }

    public void fabi() {
        LetoEvents.setThirdpartyMintage(new IMintage() {
            @Override
            public void requestMintage(Context ctx, final MintageRequest request) {
                final int coin = request.getCoin(); //获取需要发币数量

                NLog.d("游戏结束发币数量", coin + "个");
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("coin", coin)
                            .build();
                } catch (JSONException e) {
                }
                final MintageResult resultGame = new MintageResult();
                OKHttpUtils.getInstance().getRequest("app/game/addCoin", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NLog.d("金币增加失败");
                        //发币结束后 回调通知
                        resultGame.setCoin(coin); //金币数
                        resultGame.setErrCode(1); //操作结果 0 是成功， 非 0 失败
                        request.notifyMintageResult(resultGame);
                    }

                    @Override
                    public void onSuccess(String result) {
                        //发币结束后 回调通知
                        NLog.d("金币增加成功");
                        resultGame.setCoin(coin); //金币数
                        resultGame.setErrCode(0); //操作结果 0 是成功， 非 0 失败
                        request.notifyMintageResult(resultGame);
                    }
                });
            }
        });

    }
}
