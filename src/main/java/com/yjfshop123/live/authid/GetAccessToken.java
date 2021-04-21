package com.yjfshop123.live.authid;

import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;
import com.webank.normal.net.BaseResponse;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GetAccessToken {

    public static class GetAccessTokenParam {
        public String app_id=Const.faceVerifyappid;
        public String secret=Const.faceVerifySecret;
        public String grant_type="client_credential";
        public String version= Const.version;


        public String toJson() {
            String body = "";
            try {
                body = new JsonBuilder()
                        .put("app_id", app_id)
                        .put("secret", secret)
                        .put("grant_type", grant_type)
                        .put("version", version)
                        .build();
            } catch (JSONException e) {
            }
            return body.toString();
        }
    }

    public static class Result implements Serializable {
        public String bizSeqNo;  //openApi给的业务流水号
        public String orderNo;  //合作方上送的订单号
        public String faceId;  //32位唯一标识
    }

    public static class GetFaceIdResponse extends BaseResponse<Result> {

    }

    public static void requestExec(String url, GetAccessTokenParam param,
                                   RequestCallback callback) {
        StringBuilder tempParams = new StringBuilder();




                //对参数进行URLEncoder
        try {
            tempParams.append(String.format("%s=%s", "app_id", URLEncoder.encode(param.app_id, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "secret", URLEncoder.encode(param.secret, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "grant_type", URLEncoder.encode(param.grant_type, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "version", URLEncoder.encode(param.version, "utf-8")));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        OKHttpUtils.getInstance().getRequestForFace(url+"?"+tempParams.toString(),param.toJson(),callback);
    }


}
