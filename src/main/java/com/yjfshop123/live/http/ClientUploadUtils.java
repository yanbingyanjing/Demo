package com.yjfshop123.live.http;

import android.os.Handler;

import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.RequestCallback2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ClientUploadUtils {

    private static ClientUploadUtils mInstance;
    private OkHttpClient mClient;
    private Handler mListenerHandler;

    public static ClientUploadUtils getInstance() {
        synchronized (ClientUploadUtils.class) {
            if (mInstance == null) {
                mInstance = new ClientUploadUtils();
            }
            return mInstance;
        }
    }

    public static void destroyInstance() {
        synchronized (ClientUploadUtils.class) {
            if (mInstance != null) {
                mInstance.cancelAllRequests();
                mInstance = null;
            }
        }
    }

    public void cancelAllRequests(){
        if (mClient == null){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mClient.dispatcher().cancelAll();
            }
        }).start();
    }

    protected ClientUploadUtils() {
        mClient = new OkHttpClient();
        mListenerHandler = new Handler(App.getContext().getApplicationContext().getMainLooper());
    }


    public void upload(String url, String filePath, String filetype/*, String file_save_name*/, final RequestCallback2 callback) {
        if (filePath == null){
            return;
        }
        String fileName = getFileName(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filetype", filetype)
//                .addFormDataPart("file_save_name", file_save_name)
//                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .addFormDataPart("file", fileName, createCustomRequestBody(MediaType.parse("multipart/form-data"), new File(filePath), callback))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .post(requestBody)
                .build();

//        Response response = client.newCall(request).execute();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackOnThread(callback, "onError", -1, "网络请求超时，请检查网络~");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String body = response.body().string();
                callbackOnThread(callback, "onSuccess", body);
            }
        });
    }

    private RequestBody createCustomRequestBody(final MediaType contentType, final File file, final RequestCallback2 callback) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return file.length();
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        callbackOnThread(callback, "onProgress", contentLength(), remaining -= readCount, remaining == 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static String getFileName(String pathandname){
        int start = pathandname.lastIndexOf("/");
        if(start != -1 ){
            return pathandname.substring(start + 1);
        }else{
            return null;
        }
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
}
