package com.yjfshop123.live.shop.util;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;


import com.yjfshop123.live.App;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.shop.util.SignMD5Util;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
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

import static com.yjfshop123.live.Const.getDomain;
import static com.yjfshop123.live.Const.ziying_shop_url;

public class HttpUtil {
 //分类接口
    public static String url = "https://openapi.dataoke.com/api/category/get-super-category";
    //首页商品列表
    public static String shop_url = "https://openapi.dataoke.com/api/goods/get-goods-list";
    //每日爆品
    public static String bao_shop_url = "https://openapi.dataoke.com/api/goods/explosive-goods-list";
    //各大榜单
    public static String bangdan_shop_url = "https://openapi.dataoke.com/api/goods/get-ranking-list";
    //搜索
    public static String search_shop_url = "https://openapi.dataoke.com/api/goods/get-dtk-search-goods";
    //单品详情
    public static String detail_shop_url = "https://openapi.dataoke.com/api/goods/get-goods-details";
    //高效转链
    public static String zhuanlian_shop_url = "https://openapi.dataoke.com/api/tb-service/get-privilege-link";
    //视频商品
    public static String video_shop_url = "https://openapi.dataoke.com/api/delanys/media/video/get-goods-list";

    // public static String  ziying_shop_url="http://192.168.50.93:8081";


    //自营商城商品列表
    public static String ziying_shop_list_url = ziying_shop_url + "/shop_api/category/goods_list";
    //自营商城商品详情
    public static String ziying_shop_detail_url = ziying_shop_url + "/shop_api/goods/detail";
    //自营商城立即购买第一步获取cart_id
    public static String ziying_shop_buy_now_first_url = ziying_shop_url + "/shop_api/cart/add_cart";

    //自营商城立即购买第二步获取订单信息
    public static String ziying_shop_buy_now_sencond_url = ziying_shop_url + "/shop_api/checkout/get_goods_list";
    //自营商城立即购买第三步获取订单信息
    public static String ziying_shop_buy_now_thrd_url = ziying_shop_url + "/shop_api/address/get_default_address";
    //自营商城添加地址
    public static String ziying_shop_add_address_url = ziying_shop_url + "/shop_api/address/add";


    //自营商城地址列表
    public static String ziying_shop_address_url = ziying_shop_url + "/shop_api/address/address_list";

    //自营商城修改地址
    public static String ziying_shop_fix_address_url = ziying_shop_url + "/shop_api/address/edit";

    //自营商城删除地址
    public static String ziying_shop_delete_address_url = ziying_shop_url + "/shop_api/address/del";
    //自营商城设置默认地址
    public static String ziying_shop_set_moren_address_url = ziying_shop_url + "/shop_api/address/set_default_address";

    //自营商城下单
    public static String ziying_shop_order_create_url = ziying_shop_url + "/shop_api/checkout/create_order";
    //自营商城付款
    public static String ziying_shop_order_pay_url = ziying_shop_url + "/shop_api/pay/pay";
    //自营商城订单列表
    public static String ziying_shop_order_list_url = ziying_shop_url + "/shop_api/user/order_list";

    //自营商城取消订单
    public static String ziying_shop_order_cancel_url = ziying_shop_url + "/shop_api/user/cancel_order";

    //自营商城订单详情
    public static String ziying_shop_order_detail_url = ziying_shop_url + "/shop_api/user/order_detail";

    //自营商城确认收货
    public static String ziying_shop_order_confir_shouhuo_url = ziying_shop_url + "/shop_api/user/confirm";


    //自营商城地址列表
    public static String ziying_shop_wuliu_url = ziying_shop_url + "/shop_api/user/get_trace_info";
    private static OkHttpClient mOkHttpClient;
    private static volatile HttpUtil instance = null;

    private HttpUtil() {
        mListenerHandler = new Handler(App.getContext().getApplicationContext().getMainLooper());
    }

    public static HttpUtil getInstance() {
        if (mOkHttpClient == null) {
            if (instance == null) {
                synchronized (HttpUtil.class) {
                    if (mOkHttpClient == null)
                        mOkHttpClient = new OkHttpClient();
                    if (instance == null)
                        instance = new HttpUtil();
                    initOkHttpClient();
                }
            }
        }
        return instance;
    }

    /**
     * 设置请求超时时间以及缓存
     */
    private static void initOkHttpClient() {
        //设置缓存目录
        File sdcache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache");
        //设置缓存大小，10MB
        int cacheSize = 1024 * 1024 * 10;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String token = UserInfoUtil.getToken();
                        Log.d("请求参数Header", "token : " + token);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("XX-Token", token)
                                .addHeader("XX-Device-Type", "android")
                                .addHeader("XX-Api-Version", App.versionName)
                                .addHeader("XX-Store-Channel", App.channel_id)
                                .build();
                        RequestBody requestBody = newRequest.body();
                        if (requestBody != null)
                            Log.d("请求参数", "request : " + getParamContent(requestBody));

                        return chain.proceed(newRequest);
                    }
                })
                //连接超时时间 10S
                .connectTimeout(10, TimeUnit.SECONDS)
                //写入超时时间 20S
                .writeTimeout(20, TimeUnit.SECONDS)
                //读取超时时间 20S
                .readTimeout(20, TimeUnit.SECONDS)
                //缓存
                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        //将请求设置参数赋值给OkHttp对象
        mOkHttpClient = builder.build();
    }

    /**
     * 获取常规post请求参数
     */
    private static String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

    private Handler mListenerHandler;

    /**
     * get异步请求
     *
     * @param what         唯一识别码
     * @param httpCallBack 回调
     * @param httpUrl      请求地址
     */
    public void getAsynHttp(final int what, final HttpCallBack httpCallBack, String httpUrl, TreeMap<String, String> paraMap) {
        String paramsStr = null;
        try {
            paraMap.put("sign", SignMD5Util.getSignStr(paraMap, Const.appSecret));
            paramsStr = covertParamsForStr(paraMap);
            Log.d("请求的数据", paramsStr);

            httpUrl = httpUrl.contains("?") ? httpUrl : httpUrl + "?";
            Log.d("请求的连接", httpUrl);
            Request requestBuilder = new Request.Builder()
                    .url(httpUrl + paramsStr)
                    .method("GET", null)
                    .build();
            Call mcall = mOkHttpClient.newCall(requestBuilder);
            mcall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    mListenerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpCallBack.onFailure(what, e.toString());
                        }
                    });

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        final String result = response.body().string();
                        mListenerHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    httpCallBack.onResponse(what, result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    httpCallBack.onFailure(what, result);
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * get异步请求
     *
     * @param what         唯一识别码
     * @param httpCallBack 回调
     * @param httpUrl      请求地址
     */
    public void getAsynHttpNoSign(final int what, final HttpCallBack httpCallBack, String httpUrl, TreeMap<String, String> paraMap) {
        String paramsStr = null;
        try {
            paramsStr = covertParamsForStr(paraMap);
            Log.d("请求的数据", paramsStr);

            httpUrl = httpUrl.contains("?") ? httpUrl : httpUrl + "?";
            Log.d("请求的连接", httpUrl);
            Request requestBuilder = new Request.Builder()
                    .url(httpUrl + paramsStr)
                    .method("GET", null)
                    .build();
            Call mcall = mOkHttpClient.newCall(requestBuilder);
            mcall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    try {
                        mListenerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onFailure(what, e.toString());
                            }
                        });
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        final String result = response.body().string();
                        Log.d("请求到的数据", result);
                        mListenerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    httpCallBack.onResponse(what, result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    httpCallBack.onFailure(what, result);
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * get同步请求
     *
     * @param what    唯一识别码
     * @param httpUrl 请求地址
     */
    public void getSyncHttp(final int what, final String httpUrl) {
        Request builder = new Request.Builder()
                .url(httpUrl)
                .method("GET", null)
                .build();
        Call call = mOkHttpClient.newCall(builder);
        try {
            Response response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * post同步请求
     *
     * @param what    唯一识别码
     * @param httpUrl 请求地址
     */
    public void postSyncHttp(final int what, final String httpUrl) {
        Request builder = new Request.Builder()
                .url(httpUrl)
                .method("POST", null)
                .build();
        Call call = mOkHttpClient.newCall(builder);
        try {
            Response response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * post异步提交键值对
     *
     * @param what         唯一识别码
     * @param httpCallBack 回调
     * @param httpUrl      请求地址
     * @param stringMap    map数组
     */
    public void postAsynHttp(final int what, final HttpCallBack httpCallBack, final String httpUrl, final Map<String, String> stringMap) {
        FormBody.Builder formBody = new FormBody.Builder();
        for (String key : stringMap.keySet()) {
            formBody.add(key, stringMap.get(key));
        }
        Log.d("请求的连接", httpUrl);
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(formBody.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                try {
                    mListenerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpCallBack.onFailure(what, e.toString());
                        }
                    });
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String result = response.body().string();
                    mListenerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpCallBack.onResponse(what, result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                httpCallBack.onFailure(what, result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 异步上传文件
     *
     * @param what         唯一识别码
     * @param httpCallBack 回调
     * @param httpUrl      上传地址
     * @param filapath     本地地址
     */
    public void uploadAsynFile(final int what, final HttpCallBack httpCallBack, final String httpUrl, String filapath) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), filapath);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(httpUrl)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFailure(what, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpCallBack.onResponse(what, response.body().string());
            }
        });
    }

    /**
     * 异步下载文件
     *
     * @param what         唯一识别码
     * @param httpCallBack 回调
     * @param httpFilePath 请求地址
     * @param filePath     本地地址
     */
    public void downAsynFile(final int what, final HttpCallBack httpCallBack, final String httpFilePath, final String filePath) {
        Request request = new Request.Builder()
                .url(httpFilePath)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFailure(what, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                //拿到字节流
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(filePath));
                    byte[] bytes = new byte[1024 * 1024];
                    int len = 0;
                    while ((len = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    fileOutputStream.flush();
                    httpCallBack.onResponse(what, "下载成功");
                } catch (IOException e) {
                    e.printStackTrace();
                    httpCallBack.onResponse(what, "下载异常:" + e.toString());
                }
            }
        });
    }

    public interface HttpCallBack {

        /**
         * 请求成功回调接口
         */
        void onResponse(int what, String response);

        /**
         * 请求失败回调接口
         */
        void onFailure(int what, String error);
    }

    public String covertParamsForStr(TreeMap<String, String> treeMap) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder stringBufferMd = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = treeMap.entrySet().iterator();
        int a = 0;
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (a != 0) {
                stringBufferMd.append("&");
            }
            stringBufferMd.append(entry.getKey() + "=");
            stringBufferMd.append(entry.getValue());
            a = a + 1;
        }


        return stringBufferMd.toString();
    }
}
