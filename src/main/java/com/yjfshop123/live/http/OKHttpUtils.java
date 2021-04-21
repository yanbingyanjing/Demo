package com.yjfshop123.live.http;

import android.os.Handler;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OKHttpUtils {

    private static OKHttpUtils mInstance;
    private OKHttpRequests mHttpRequest;
    private Handler mListenerHandler;

    public static OKHttpUtils getInstance() {
        synchronized (OKHttpUtils.class) {
            if (mInstance == null) {
                mInstance = new OKHttpUtils();
            }
            return mInstance;
        }
    }

    public static void destroyInstance() {
        synchronized (OKHttpUtils.class) {
            if (mInstance != null) {
                mInstance.destroy();
                mInstance = null;
            }
        }
    }

    private void destroy() {
        if (mHttpRequest != null) {
            mHttpRequest.cancelAllRequests();
            mHttpRequest = null;
        }
    }

    protected OKHttpUtils() {
        mHttpRequest = new OKHttpRequests();
        mListenerHandler = new Handler(App.getContext().getApplicationContext().getMainLooper());
    }

    public void getRequest(final String url, String jsoStr, final RequestCallback callback) {
        NLog.e("TAGTAG_OKHttpUtils", url + ":" + jsoStr);
        if (mHttpRequest == null) {
            callbackOnThread(callback, "onError", -1, "初始化失败");
            NLog.e("TAGTAG_OKHttpUtils", url + ":onError:1001:初始化失败");
            return;
        }
        mHttpRequest.getRequest(url, jsoStr, new OKHttpRequests.OnResponseCallback() {
            @Override
            public void onResponse(int code, String result) {
                NLog.e("TAGTAG_OKHttpUtils", url + ":" + result);
                //printJson("TAGTAG_OKHttpUtils", url, result);//解析JSON展示
                if (code < 0){
                    callbackOnThread(callback, "onError", -1, result);
                }else {
                    try {
                        JSONObject jso = new JSONObject(result);
                        int jso_code = jso.getInt("code");
                        if (jso_code == 1){
                            callbackOnThread(callback, "onSuccess", jso.getString("data"));
                        }else if (jso_code == 201){
                            //充值金蛋
                            String msg = jso.getString("msg");
                            callbackOnThread(callback, "onError", jso_code, msg);
                        }else if (jso_code == 202){
                            //充值VIP
                            String msg = jso.getString("msg");
                            callbackOnThread(callback, "onError", jso_code, msg);
                        }else if (jso_code == 1001){
                            //退出登录
                            String msg = jso.getString("msg");
                            callbackOnThread(callback, "onError", jso_code, msg);
                            callbackOnThread(new Runnable() {
                                @Override
                                public void run() {
                                    BroadcastManager.getInstance(App.getInstance()).sendBroadcast(Config.LOGIN, Config.LOGOUTSUCCESS);
                                }
                            });
                        }else{
                            //错误提示
                            String msg = jso.getString("msg");
                            callbackOnThread(callback, "onError", jso_code, msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackOnThread(callback, "onError", -1, result);
                    }
                }
            }
        });
    }
    public void getRequestForFace(final String url, String jsoStr, final RequestCallback callback) {
        if (mHttpRequest == null) {
            callbackOnThread(callback, "onError", -1, "初始化失败");
            NLog.e("TAGTAG_OKHttpUtils", url + ":onError:1001:初始化失败");
            return;
        }
        mHttpRequest.getRequestForFace(url, jsoStr, new OKHttpRequests.OnResponseCallback() {
            @Override
            public void onResponse(int code, String result) {
                NLog.e("人脸核身", url + ":" + result);
                //printJson("TAGTAG_OKHttpUtils", url, result);//解析JSON展示
                if (code < 0){
                    callbackOnThread(callback, "onError", -1, result);
                }else {
                    try {
                        JSONObject jso = new JSONObject(result);
                        int jso_code = jso.getInt("code");
                        if (jso_code == 0){
                            callbackOnThread(callback, "onSuccess", result);
                        }else{
                            //错误提示
                            String msg = jso.getString("msg");
                            callbackOnThread(callback, "onError", jso_code, msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackOnThread(callback, "onError", -1, result);
                    }
                }
            }
        });
    }
    public void getRequest2(final String url, String jsoStr, final RequestCallback callback) {
        if (mHttpRequest == null) {
            callbackOnThread(callback, "onError", -1, "初始化失败");
            NLog.e("TAGTAG_OKHttpUtils", url + ":onError:1001:初始化失败");
            return;
        }
        mHttpRequest.getRequest(url, jsoStr, new OKHttpRequests.OnResponseCallback() {
            @Override
            public void onResponse(int code, String result) {
                NLog.e("TAGTAG_OKHttpUtils", url + ":" + result);
                callbackOnThread(callback, "onSuccess", result);
            }
        });
    }

    private void callbackOnThread(final Object object, final String methodName, final Object... args) {
        if (object == null || methodName == null || methodName.length() == 0) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                Class objClass = object.getClass();
                while (objClass != null) {
                    Method[] methods = objClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName() == methodName) {
                            try {
                                method.invoke(object, args);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                    objClass = objClass.getSuperclass();
                }
            }
        });
    }

    private void callbackOnThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static synchronized void printJson(String tag, String headString, String msg) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        NLog.e(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            NLog.e(tag, "║ " + line);
        }
        NLog.e(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
    }
}
