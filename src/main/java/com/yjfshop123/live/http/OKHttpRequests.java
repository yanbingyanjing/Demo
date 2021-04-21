package com.yjfshop123.live.http;

import android.util.Log;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class OKHttpRequests {

    private static String domain;
    private final OkHttpClient okHttpClient;
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json; charset=utf-8");

    public OKHttpRequests() {
        domain = Const.getDomain();
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String token = UserInfoUtil.getToken();
                        Log.d("请求参数Header", "token : " +token);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("XX-Token", token)
                                .addHeader("XX-Device-Type", "android")
                                .addHeader("XX-Api-Version", App.versionName)
                                .addHeader("XX-Store-Channel", App.channel_id)
                                .build();
                        RequestBody requestBody = newRequest.body();
                        if (requestBody != null)
                            Log.d("TAGTAG_OKHttpUtils ", "request : " + getParamContent(requestBody));

                        return chain.proceed(newRequest);
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取常规post请求参数
     */
    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

    public void cancelAllRequests() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                okHttpClient.dispatcher().cancelAll();
            }
        }).start();
    }

    public interface OnResponseCallback {
        void onResponse(int code, String String);
    }
    public interface OnResponseCallbackForAuth {
        void onResponse(Response response);
    }
    private void request(Request request, final OnResponseCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onResponse(-1, "网络请求超时，请检查网络~");
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String body = response.body().string();
                if (callback != null) {
                    callback.onResponse(1, body);
                }
            }
        });
    }

    private String getSelfURL(String url) {
        return getSelfURL(url, new String[]{});
    }

    private String getSelfURL(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(domain).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }

    /**
     * @param url
     * @param jsoStr
     * @param callback
     */
    public void getRequest(String url, String jsoStr, final OnResponseCallback callback) {
        Request request = new Request.Builder().url(getSelfURL(url))
                .post(RequestBody.create(MEDIA_JSON, jsoStr))
                .build();
        request(request, callback);
    }


    /**
     * @param url
     * @param jsoStr
     * @param callback
     */
    public void getRequestForFace(String url, String jsoStr, final OnResponseCallback callback) {
        Request request = new Request.Builder().url(getFaceAuthURL(url))
                .get()
                .build();
        request(request, callback);
    }
    private String getFaceAuthURL(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(Const.authUrl).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }
}
