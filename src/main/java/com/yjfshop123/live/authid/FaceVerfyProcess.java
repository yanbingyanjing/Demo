package com.yjfshop123.live.authid;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.webank.facelight.contants.WbCloudFaceContant;
import com.webank.facelight.contants.WbFaceError;
import com.webank.facelight.contants.WbFaceVerifyResult;
import com.webank.facelight.listerners.WbCloudFaceVeirfyLoginListner;
import com.webank.facelight.listerners.WbCloudFaceVeirfyResultListener;
import com.webank.facelight.tools.WbCloudFaceVerifySdk;
import com.webank.facelight.ui.FaceVerifyStatus;
import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.model.AccessTokenResponse;
import com.yjfshop123.live.model.NonceTickerResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.io.IOException;

public class FaceVerfyProcess {
    //实名认证姓名
    private String name;
    //身份证号
    private String id;

    //请输入32位随机数
    private String nonce = null;
    //唯一订单号
    private String orderNo = null;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    //签名
    private String sign = null;


    //此处为demo模拟，请输入标识唯一用户的userId
    private String userId = UserInfoUtil.getMiPlatformId();
    //由合作方提供包名申请，统一下发
    private String keyLicence = Const.faceVerifykeyLicence;
    private String webankAppId = Const.faceVerifyappid;

    //设置选择的比对类型  默认为公安网纹图片对比
    //公安网纹图片比对 WbCloudFaceContant.ID_CRAD
    //自带比对源比对  WbCloudFaceContant.SRC_IMG
    //仅活体检测  WbCloudFaceContant.NONE
    //默认公安网纹图片对比
    private String compareType = WbCloudFaceContant.ID_CARD;
    //默认选择黑色模式
    private String color = WbCloudFaceContant.BLACK;
    //默认展示成功/失败页面
    private boolean isShowSuccess = true;
    private boolean isShowFail = true;
    //默认录制视频
    private boolean isRecordVideo = true;
    //默认播放提示语
    private boolean isPlayVoice = true;
    //默认不检测闭眼
    private boolean isEnableCloseEyes = false;


    private String version = Const.version;
    //刷脸类别：光线活体 FaceVerifyStatus.Mode.REFLECTION
    //         动作活体 FaceVerifyStatus.Mode.ACT
    //         数字活体 FaceVerifyStatus.Mode.NUM
    private FaceVerifyStatus.Mode mode = FaceVerifyStatus.Mode.REFLECTION;
    private Context context;
    private FaceListener faceListener;

    private WeOkHttp myOkHttp = new WeOkHttp();

    public FaceVerfyProcess(Context context) {
        this.context = context;
        initHttp();
    }

    public FaceVerfyProcess(Context context, String name, String id, String nonce, String orderNo) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.nonce = nonce;
        this.orderNo = orderNo;
        initHttp();
    }

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(20, 20, 20)
                //添加PIN
                .log(WeLog.Level.BODY);
    }


    public void setListener(FaceListener faceListener) {
        this.faceListener = faceListener;
    }

    /**
     * 获取accesstoken-获取nonce_ticket 生成签名  获取faceId  拉起sdk
     */
    public void startProcess() {
        if (TextUtils.isEmpty(sign)) {
            if (faceListener != null) faceListener.onError("签名为空");
            return;
        }
        getFaceId();
    }

    private void getAccessToken() {
        GetAccessToken.requestExec("api/oauth2/access_token", new GetAccessToken.GetAccessTokenParam(), new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (faceListener != null) faceListener.onError(errInfo);
            }

            @Override
            public void onSuccess(String result) {
                AccessTokenResponse accessTokenResponse = new Gson().fromJson(result, AccessTokenResponse.class);
                NLog.d("人脸核身", "获取accesstoken：" + accessTokenResponse.getAccess_token());
                getNonceTicket(accessTokenResponse.getAccess_token());
            }
        });
    }

    /**
     * 获取nonceTicket
     *
     * @param accessToken
     */
    private void getNonceTicket(String accessToken) {
        GetNonceTicket.GetNonceTicketParam getNonceTicketParam = new GetNonceTicket.GetNonceTicketParam();
        getNonceTicketParam.access_token = accessToken;
        GetNonceTicket.requestExec("api/oauth2/api_ticket", getNonceTicketParam, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

                if (faceListener != null) faceListener.onError(errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NonceTickerResponse data = new Gson().fromJson(result, NonceTickerResponse.class);
                if (data != null && data.getTickets() != null && data.getTickets().length > 0) {
                    NLog.d("人脸核身", "获取nonceTicket：" + data.getTickets()[0].getValue());
                    sign = SignUtil.sign(FaceVerfyProcess.this, data.getTickets()[0].getValue());
                    NLog.d("人脸核身", "获取sign：" + sign);
                    getFaceId();
                }

            }
        });
    }

    /**
     * 获取faceId
     */
    private void getFaceId() {
        GetFaceId.GetFaceIdParam param = new GetFaceId.GetFaceIdParam();
        param.orderNo = orderNo;
        param.webankAppId = webankAppId;
        param.version = version;
        param.userId = userId;
        param.sign = sign;
        param.name = name;
        param.idNo = id;
        NLog.d("人脸核身", "获取getFaceId：" + new Gson().toJson(param));
        GetFaceId.requestExec(myOkHttp, "api/server/getfaceid", param, new WeReq.WeCallback<GetFaceId.GetFaceIdResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, int i, int code, String message, IOException e) {
                if (faceListener != null) faceListener.onError(message);
            }

            @Override
            public void onSuccess(WeReq weReq, GetFaceId.GetFaceIdResponse getFaceIdResponse) {
                if (getFaceIdResponse != null) {
                    String code = getFaceIdResponse.code;
                    if (code.equals("0")) {
                        GetFaceId.Result result = getFaceIdResponse.result;
                        if (result != null) {
                            String faceId = result.faceId;
                            if (!TextUtils.isEmpty(faceId)) {
                                NLog.d("人脸核身", "获取faceid：" + faceId);
                                openCloudFaceService(faceId);
                            } else {
                                if (faceListener != null) faceListener.onError("登录异常(faceId为空)");
                            }
                        } else {
                            if (faceListener != null)
                                faceListener.onError("登录异常(faceId请求失败:getFaceIdResponse result is null)");
                        }
                    } else {
                        if (faceListener != null)
                            faceListener.onError("登录异常(faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg + ")");
                    }
                } else {
                    if (faceListener != null)
                        faceListener.onError("登录异常(faceId请求失败:getFaceIdResponse is null)");
                }
            }
        });
    }

    //拉起刷脸sdk
    public void openCloudFaceService(String faceId) {
        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                faceId,
                orderNo,
                webankAppId,
                version,
                nonce,
                userId,
                sign,
                mode,
                keyLicence);
        NLog.d("人脸核身", "openCloudFaceService：" + new Gson().toJson(inputData));
        data.putSerializable(WbCloudFaceContant.INPUT_DATA, inputData);
        //是否展示刷脸成功页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, isShowSuccess);
        //是否展示刷脸失败页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, isShowFail);
        //颜色设置
        data.putString(WbCloudFaceContant.COLOR_MODE, color);
        //是否需要录制上传视频 默认需要
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, isRecordVideo);
        //是否开启闭眼检测，默认不开启
        data.putBoolean(WbCloudFaceContant.ENABLE_CLOSE_EYES, isEnableCloseEyes);
        //是否播放提示音，默认播放
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, isPlayVoice);
        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceContant.ID_CRAD
        //自带比对源比对  WbCloudFaceContant.SRC_IMG
        //仅活体检测  WbCloudFaceContant.NONE
        //默认公安网纹图片比对
        data.putString(WbCloudFaceContant.COMPARE_TYPE, compareType);

        WbCloudFaceVerifySdk.getInstance().initSdk(context, data, new WbCloudFaceVeirfyLoginListner() {
            @Override
            public void onLoginSuccess() {
                //登录sdk成功
                if (faceListener != null) faceListener.oncomplete();
                //拉起刷脸页面
                WbCloudFaceVerifySdk.getInstance().startWbFaceVeirifySdk(context, new WbCloudFaceVeirfyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        //得到刷脸结果
                        if (result != null) {
                            if (result.isSuccess()) {
                                NLog.d("人脸核身完成", "刷脸成功! Sign=" + result.getSign() + "; liveRate=" + result.getLiveRate() +
                                        "; similarity=" + result.getSimilarity() + "userImageString=" + result.getUserImageString());
                                if (faceListener != null) faceListener.onAuthSuccess(result);

                            } else {
                                if (faceListener != null) faceListener.onFail(result);
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    NLog.d("人脸核身完成", "刷脸失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        NLog.d("人脸核身完成", "对比失败，liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity());
                                    }
                                } else {
                                    NLog.d("人脸核身完成", "sdk返回error为空！");
                                }
                            }
                        } else {
                            if (faceListener != null) faceListener.onFail(result);
                            NLog.d("人脸核身完成", "sdk返回结果为空！");
                        }
                    }
                });
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                //登录失败
                if (error != null) {
                    NLog.d("人脸核身", "登录失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        if (faceListener != null) faceListener.onError("传入参数有误！" + error.getDesc());

                    } else {
                        if (faceListener != null)
                            faceListener.onError("登录刷脸sdk失败！" + error.getDesc());
                    }
                } else {
                    if (faceListener != null) faceListener.onError("登录刷脸sdk失败！ sdk返回error为空");
                }
            }
        });
    }

    public interface FaceListener {
        //成功拉起刷脸sdk
        void oncomplete();

        void onError(String errInfo);

        void onFail(WbFaceVerifyResult result);

        void onAuthSuccess(WbFaceVerifyResult result);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWebankAppId() {
        return webankAppId;
    }

    public void setWebankAppId(String webankAppId) {
        this.webankAppId = webankAppId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
