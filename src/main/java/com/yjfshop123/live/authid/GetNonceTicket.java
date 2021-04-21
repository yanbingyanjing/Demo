package com.yjfshop123.live.authid;

import com.webank.normal.net.BaseResponse;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GetNonceTicket {
    public static class GetNonceTicketParam {
        public String app_id= Const.faceVerifyappid;
        public String access_token="";
        public String type="NONCE";
        public String version= Const.version;
        public String user_id= UserInfoUtil.getMiPlatformId();
    }


    public static void requestExec(String url, GetNonceTicketParam param,
                                   RequestCallback callback) {
        StringBuilder tempParams = new StringBuilder();

        //对参数进行URLEncoder
        try {
            tempParams.append(String.format("%s=%s", "app_id", URLEncoder.encode(param.app_id, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "access_token", URLEncoder.encode(param.access_token, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "type", URLEncoder.encode(param.type, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "version", URLEncoder.encode(param.version, "utf-8")));
            tempParams.append("&");
            tempParams.append(String.format("%s=%s", "user_id", URLEncoder.encode(param.user_id, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        OKHttpUtils.getInstance().getRequestForFace(url+"?"+tempParams.toString(),"",callback);
    }


}
