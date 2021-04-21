package com.yjfshop123.live.oss;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback2;
import com.yjfshop123.live.http.ClientUploadUtils;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.request.UploadObjectCallbackRequest;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.MD5;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.FileUtil;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CosXmlUtils {

    private Context context;
    private UploadDialog uploadDialog;//进度dialog

    private String region;//存储桶所在的地域
    private String secretId; //永久密钥 secretId
    private String secretKey; //永久密钥 secretKey
    private String bucket;//存储桶

    private CosXmlSimpleService cosXmlService;

    private Handler mHandler;

    private OSSResultListener ossResultListener;
    public ArrayList<OssImageResponse> responses = new ArrayList<>();
    private ArrayList<OssVideoResponse> videoResponses = new ArrayList<>();
    private ArrayList<String> updated = new ArrayList<>();

    private String uploadUrl;
    private String meCallbackUrl;
    private String callbackUrl;

    public void setOssResultListener(OSSResultListener ossResultListener) {
        this.ossResultListener = ossResultListener;
    }

    public CosXmlUtils(Context context) {
        this.context = context;
        initData();
    }

    private void initData() {
        region = Const.region;
        bucket = Const.bucket;
        secretId = Const.secretId;
        secretKey = Const.secretKey;

        uploadUrl = Const.getDomain() + "/app/material/upload";
        meCallbackUrl = "app/material/uploadCallback";
        callbackUrl = "app/material/uploadObjectCallback";

        mHandler = new Handler(this.context.getMainLooper());
        initOSSConfig();
    }

    private void initOSSConfig() {
        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 https 请求, 默认 http 请求
                .setDebuggable(false)
                .builder();

        //永久密钥进行授权
        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(secretId, secretKey, 300);

        cosXmlService = new CosXmlSimpleService(context, serviceConfig, credentialProvider);
    }

    private UploadObjectCallbackRequest uploadObjectCallbackRequest;

    /**
     * 单个上传(图片跟语音)
     *
     * @param uploadFilePath 本地文件绝对地址
     * @param type 本地文件类型
     * @param class_id（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面 51 短视频）
     * @param userId
     * @param uploadDialog_
     */
    public void uploadData(String uploadFilePath, final String type, int class_id, String userId, UploadDialog uploadDialog_) {
        uploadDialog = uploadDialog_;

        uploadFilePath = uploadFilePath.replace("/raw/", "");
        int size = 0;
        File file = new File(uploadFilePath);
        if (null != file || file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        /**
         若有特殊要求，则可以如下进行初始化定制。如限定当对象 >= 2M 时，启用分片上传，且分片上传的分片大小为 1M, 当源对象大于 5M 时启用分片复制，且分片复制的大小为 5M。
         TransferConfig transferConfig = new TransferConfig.Builder()
         .setDividsionForCopy(5 * 1024 * 1024) // 是否启用分片复制的最小对象大小
         .setSliceSizeForCopy(5 * 1024 * 1024) //分片复制时的分片大小
         .setDivisionForUpload(2 * 1024 * 1024) // 是否启用分片上传的最小对象大小
         .setSliceSizeForUpload(1024 * 1024) //分片上传时的分片大小
         .build();
         */

        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String cosPath; //即对象到 COS 上的绝对路径, 格式如 cosPath = "text.txt";
        String srcPath = uploadFilePath; // 如 srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        String uploadId = null; //若存在初始化分片上传的 UploadId，则赋值对应 uploadId 值用于续传，否则，赋值 null。

        cosPath = getImageObjectKey(userId) + uploadFilePath.substring(uploadFilePath.lastIndexOf("."), uploadFilePath.length());

        uploadObjectCallbackRequest = new UploadObjectCallbackRequest();
        uploadObjectCallbackRequest.setClass_id(class_id);
        uploadObjectCallbackRequest.setObject(cosPath);
        uploadObjectCallbackRequest.setBucket(bucket);
        uploadObjectCallbackRequest.setEtag("");
        uploadObjectCallbackRequest.setMime_type(type);//类型 image,video,audio
        uploadObjectCallbackRequest.setExtra_info("");//扩展信息，json字符串 （ 例如：{“width”:600,”height”:480} ）
        uploadObjectCallbackRequest.setVideo_cover_img("");//视频封面图，当mime_type=video时，必须传
        uploadObjectCallbackRequest.setSize(size);

        if (!Const.IS_COS){
            //文件类型（image，video，audio）
            //文件保存名（带路径，唯一）
            String filetype = "image";
            //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面 51 短视频）
            if (class_id == 51 || class_id == 12 || class_id == 2){
                filetype = "video";
            }else if (class_id == 3){
                filetype = "audio";
            }else {
                filetype = "image";
            }
            ClientUploadUtils.getInstance().upload(uploadUrl, srcPath, filetype/*, cosPath*/, new RequestCallback2() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(context, "上传异常");
                    if (uploadDialog != null) {
                        uploadDialog.dissmis();
                    }
                }

                @Override
                public void onSuccess(final String result) {
                    NLog.e("TAGTAG",  result);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jso = new JSONObject(result);
                                int code = jso.getInt("code");
                                String object = jso.getJSONObject("data").getString("object");
                                uploadObjectCallbackRequest.setObject(object);
                                if (code == 1){
                                    String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                    OKHttpUtils.getInstance().getRequest(meCallbackUrl, body, new RequestCallback() {
                                        @Override
                                        public void onError(int errCode, String errInfo) {
                                            NToast.shortToast(context, errInfo);
                                            if (uploadDialog != null) {
                                                uploadDialog.dissmis();
                                            }
                                        }
                                        @Override
                                        public void onSuccess(String result) {
                                            try {
                                                if (result != null){
                                                    if (type.equals("image") || type.equals("audio")) {
                                                        OssImageResponse response = JsonMananger.jsonToBean(result, OssImageResponse.class);
                                                        responses.add(response);
                                                        ossResultListener.ossResult(responses);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }else {
                                    NToast.shortToast(context, jso.getString("msg"));
                                }
                            } catch (HttpException e) {
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                                e.printStackTrace();
                            } catch (JSONException e) {
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                    long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));
                    if (uploadDialog != null && !type.equals("image")) {
                        uploadDialog.setProcess((int)progress);
                    }
                }
            });
        }else {
            //上传对象
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);

            /**
             * 若是上传字节数组，则可调用 TransferManager 的 upload(string, string, byte[]) 方法实现;
             * byte[] bytes = "this is a test".getBytes(Charset.forName("UTF-8"));
             * cosxmlUploadTask = transferManager.upload(bucket, cosPath, bytes);
             */

            /**
             * 若是上传字节流，则可调用 TransferManager 的 upload(String, String, InputStream) 方法实现；
             * InputStream inputStream = new ByteArrayInputStream("this is a test".getBytes(Charset.forName("UTF-8")));
             * cosxmlUploadTask = transferManager.upload(bucket, cosPath, inputStream);
             */

            //设置上传进度回调
            cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long complete, long target) {

                    float progress = 1.0f * complete / target * 100;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));

                    if (uploadDialog != null && !type.equals("image")) {
//                    uploadDialog.uploadProcess.setText(progress + "%");
                        uploadDialog.setProcess((int)progress);
                    }
                }
            });

            //设置返回结果回调
            cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult)result;
                    NLog.e("TAGTAG",  "Success: " + cOSXMLUploadTaskResult.printResult());

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                OKHttpUtils.getInstance().getRequest(callbackUrl, body, new RequestCallback() {
                                    @Override
                                    public void onError(int errCode, String errInfo) {
                                        NToast.shortToast(context, errInfo);
                                        if (uploadDialog != null) {
                                            uploadDialog.dissmis();
                                        }
                                    }
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            if (uploadDialog != null) {
                                                uploadDialog.dissmis();
                                            }
                                            if (result != null){
                                                if (type.equals("image") || type.equals("audio")) {
                                                    OssImageResponse response = JsonMananger.jsonToBean(result, OssImageResponse.class);
                                                    responses.add(response);
                                                    ossResultListener.ossResult(responses);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (HttpException e) {
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                                e.printStackTrace();
                            }
                        }
                    });
                }
                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    NLog.e("TAGTAG",  "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "上传异常");
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

            //设置任务状态回调, 可以查看任务过程
            cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    NLog.e("TAGTAG", "Task state:" + state.name());
                }
            });

            /**
             若有特殊要求，则可以如下操作：
             PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
             putObjectRequest.setRegion(region); //设置存储桶所在的地域
             putObjectRequest.setNeedMD5(true); //是否启用 Md5 校验
             COSXMLUploadTask cosxmlUploadTask = transferManager.upload(putObjectRequest, uploadId);
             */

            /*//取消上传
            cosxmlUploadTask.cancel();

            //暂停上传
            cosxmlUploadTask.pause();

            //恢复上传
            cosxmlUploadTask.resume();*/
        }
    }

    /**
     * 多个上传
     *
     * @param urls 本地文件绝对地址数组
     * @param type 本地文件类型
     * @param class_id （1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面 51 短视频）
     * @param userId
     * @param uploadDialog_
     */
    public void ossUploadList(final ArrayList<String> urls, final String type, final int class_id, final String userId, UploadDialog uploadDialog_) {
        uploadDialog = uploadDialog_;

        if (urls.size() <= 0) {
            if (type.equals("image") || type.equals("audio")) {
                ossResultListener.ossResult(responses);
            } else if (type.equals("video")) {
                ossResultListener.ossVideoResult(videoResponses);
            }
            if (uploadDialog != null){
                uploadDialog.dissmis();
            }
            // 结束上传
            return;
        }

        String url = urls.get(0);
        //本地路径为空 不做处理到下一步
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            ossUploadList(urls, type, class_id, userId, uploadDialog);
            return;
        }

        url = url.replace("/raw/", "");
        VideoMessage vm = new VideoMessage();
        if (type.equals("video")) {
            vm = getPlayTime(url);
        }

        if (url.startsWith("/external")) {
            String path = "content://media" + url;
            ContentResolver res = context.getContentResolver();
            Cursor c = res.query(Uri.parse(path), null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                int actual_image_column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                url = c.getString(actual_image_column_index);
            }
        }

        //本地文件不存在 不处理 到下一步
        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUploadList(urls, type, class_id, userId, uploadDialog);
            return;
        }

        int size = 0;
        if (file.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String cosPath; //即对象到 COS 上的绝对路径, 格式如 cosPath = "text.txt";
        final String srcPath = url; // 如 srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        String uploadId = null; //若存在初始化分片上传的 UploadId，则赋值对应 uploadId 值用于续传，否则，赋值 null。

        String addr = url.substring(url.lastIndexOf("."), url.length());
        cosPath = getImageObjectKey(userId) + addr;
        final String coverImgPath = cosPath + ".jpg";

        uploadObjectCallbackRequest = new UploadObjectCallbackRequest();
        uploadObjectCallbackRequest.setClass_id(class_id);
        uploadObjectCallbackRequest.setObject(cosPath);
        uploadObjectCallbackRequest.setBucket(bucket);
        uploadObjectCallbackRequest.setEtag("");
        uploadObjectCallbackRequest.setMime_type(type);//类型 image,video,audio
        uploadObjectCallbackRequest.setSize(size);
        if (type.equals("video")) {
            uploadObjectCallbackRequest.setExtra_info("{\"width\":" + vm.getWidth() + ",\"height\":" + vm.getHeight() + "}");//扩展信息，json字符串 （ 例如：{“width”:600,”height”:480} ）
            uploadObjectCallbackRequest.setVideo_cover_img(coverImgPath);//视频封面图，当mime_type=video时，必须传
        }else {
            uploadObjectCallbackRequest.setExtra_info("");//扩展信息，json字符串 （ 例如：{“width”:600,”height”:480} ）
            uploadObjectCallbackRequest.setVideo_cover_img("");//视频封面图，当mime_type=video时，必须传
        }

        if (!Const.IS_COS){
            //文件类型（image，video，audio）
            //文件保存名（带路径，唯一）
            String filetype = "image";
            //（1:个人相册 2:个人视频 3:个人语音介绍 4:达人认证 5:实名认证 6:个人头像 11:动态图片 12:动态视频 21:直播间封面 51 短视频）
            if (class_id == 51 || class_id == 12 || class_id == 2){
                filetype = "video";
            }else if (class_id == 3){
                filetype = "audio";
            }else {
                filetype = "image";
            }
            ClientUploadUtils.getInstance().upload(uploadUrl, srcPath, filetype/*, cosPath*/, new RequestCallback2() {
                @Override
                public void onError(int errCode, String errInfo) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "上传异常");
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }

                @Override
                public void onSuccess(final String result) {
                    NLog.e("TAGTAG",  result);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jso = new JSONObject(result);
                                int code = jso.getInt("code");
                                if (code == 1){
                                    String object = jso.getJSONObject("data").getString("object");
                                    if (type.equals("video")) {
                                        String str = firstImage(srcPath, object/*coverImgPath*/);
                                        ossUploadImage(object/*coverImgPath*/, str);
                                    }else {
                                        uploadObjectCallbackRequest.setObject(object);
                                        String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                        OKHttpUtils.getInstance().getRequest(meCallbackUrl, body, new RequestCallback() {
                                            @Override
                                            public void onError(int errCode, String errInfo) {
                                                NToast.shortToast(context, errInfo);
                                                if (uploadDialog != null) {
                                                    uploadDialog.dissmis();
                                                }
                                            }
                                            @Override
                                            public void onSuccess(String result) {
                                                try {
                                                    if (result != null) {
                                                        if (type.equals("image") || type.equals("audio")) {
                                                            OssImageResponse response = JsonMananger.jsonToBean(result, OssImageResponse.class);
                                                            responses.add(response);
                                                            uploadDialog.setPhotoProcess(responses.size());
                                                        }
                                                        updated.add(urls.get(0));
                                                        urls.remove(0);
                                                        ossUploadList(urls, type, class_id, userId, uploadDialog);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }else {
                                    NToast.shortToast(context, jso.getString("msg"));
                                }
                            } catch (HttpException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                    long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));
                    if (uploadDialog != null && !type.equals("image")) {
                        uploadDialog.setProcess((int)progress);
                    }
                }
            });
        }else {
            //上传对象
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);

            //设置上传进度回调
            cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long complete, long target) {

                    float progress = 1.0f * complete / target * 100;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));

                    if (uploadDialog != null && !type.equals("image")) {
//                    uploadDialog.uploadProcess.setText(progress + "%");
                        uploadDialog.setProcess((int)progress);
                    }
                }
            });

            //设置返回结果回调
            cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult)result;
                    NLog.e("TAGTAG",  "Success: " + cOSXMLUploadTaskResult.printResult());

                    if (type.equals("video")) {
                        String str = firstImage(srcPath, coverImgPath);
                        ossUploadImage(coverImgPath, str);
                    }else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                    OKHttpUtils.getInstance().getRequest(callbackUrl, body, new RequestCallback() {
                                        @Override
                                        public void onError(int errCode, String errInfo) {
                                            NToast.shortToast(context, errInfo);
                                            if (uploadDialog != null) {
                                                uploadDialog.dissmis();
                                            }
                                        }
                                        @Override
                                        public void onSuccess(String result) {
                                            try {
                                                if (result != null) {
                                                    if (type.equals("image") || type.equals("audio")) {
                                                        OssImageResponse response = JsonMananger.jsonToBean(result, OssImageResponse.class);
                                                        responses.add(response);
                                                        uploadDialog.setPhotoProcess(responses.size());
                                                    }
                                                    updated.add(urls.get(0));
                                                    urls.remove(0);
                                                    ossUploadList(urls, type, class_id, userId, uploadDialog);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (HttpException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    NLog.e("TAGTAG",  "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "上传异常");
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            //设置任务状态回调, 可以查看任务过程
            cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    NLog.e("TAGTAG", "Task state:" + state.name());
                }
            });
        }
    }

    /**
     * 上传视频封面
     *
     * @param cosPath
     * @param urls
     */
    private void ossUploadImage(String cosPath, String urls) {
        File file = new File(urls);
        if (null == file || !file.exists()) {
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            return;
        }

        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        if (!Const.IS_COS){
            //文件类型（image，video，audio）
            //文件保存名（带路径，唯一）
            String filetype = "image";
            uploadObjectCallbackRequest.setObject(cosPath);
            ClientUploadUtils.getInstance().upload(uploadUrl, urls, filetype/*, cosPath*/, new RequestCallback2() {
                @Override
                public void onError(int errCode, String errInfo) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "上传异常");
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                @Override
                public void onSuccess(final String result) {
                    NLog.e("TAGTAG",  result);
                    if (uploadObjectCallbackRequest == null){
                        if (uploadDialog != null) {
                            uploadDialog.dissmis();
                        }
                        return;
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jso = new JSONObject(result);
                                int code = jso.getInt("code");
                                if (code == 1){
                                    String object = jso.getJSONObject("data").getString("object");
                                    uploadObjectCallbackRequest.setVideo_cover_img(object);
                                    String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                    OKHttpUtils.getInstance().getRequest(meCallbackUrl, body, new RequestCallback() {
                                        @Override
                                        public void onError(int errCode, String errInfo) {
                                            NToast.shortToast(context, errInfo);
                                            if (uploadDialog != null) {
                                                uploadDialog.dissmis();
                                            }
                                        }
                                        @Override
                                        public void onSuccess(String result) {
                                            try {
                                                if (result != null) {
                                                    videoResponses.clear();
                                                    OssVideoResponse response = JsonMananger.jsonToBean(result, OssVideoResponse.class);
                                                    videoResponses.add(response);
                                                    ossResultListener.ossVideoResult(videoResponses);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }else {
                                    NToast.shortToast(context, jso.getString("msg"));
                                }
                            } catch (HttpException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                    long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));
                }
            });
        }else {
            //上传对象
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, urls, null);

            //设置上传进度回调
            cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long complete, long target) {
                    float progress = 1.0f * complete / target * 100;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));
                }
            });

            //设置返回结果回调
            cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult)result;
                    NLog.e("TAGTAG",  "Success: " + cOSXMLUploadTaskResult.printResult());
                    if (uploadObjectCallbackRequest == null){
                        if (uploadDialog != null) {
                            uploadDialog.dissmis();
                        }
                        return;
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String body = JsonMananger.beanToJson(uploadObjectCallbackRequest);
                                OKHttpUtils.getInstance().getRequest(callbackUrl, body, new RequestCallback() {
                                    @Override
                                    public void onError(int errCode, String errInfo) {
                                        NToast.shortToast(context, errInfo);
                                        if (uploadDialog != null) {
                                            uploadDialog.dissmis();
                                        }
                                    }
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            if (result != null) {
                                                videoResponses.clear();
                                                OssVideoResponse response = JsonMananger.jsonToBean(result, OssVideoResponse.class);
                                                videoResponses.add(response);
                                                ossResultListener.ossVideoResult(videoResponses);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (HttpException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    NLog.e("TAGTAG",  "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "上传异常");
                                if (uploadDialog != null) {
                                    uploadDialog.dissmis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            //设置任务状态回调, 可以查看任务过程
            cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    NLog.e("TAGTAG", "Task state:" + state.name());
                }
            });
        }
    }

    /**
     *
     * @param userId
     *
     * @return
     */
    private static String getImageObjectKey(String userId) {
        //2019/07/01/md5(用户uid+毫秒时间戳).后缀
        //upload/20190715/abcdefg.png
        Date date = new Date();
        return "upload/" + new SimpleDateFormat("yyyyMMdd").format(date) + "/" + MD5.encrypt(userId + System.currentTimeMillis());

    }

    /**
     * 获取视频宽高时常
     *
     * @param mUri
     */
    private VideoMessage getPlayTime(String mUri) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        VideoMessage vm = new VideoMessage();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(context, Uri.parse(mUri));
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }

            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            String rotationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
            if (rotationStr.equals("90")) {
                vm.setWidth(height);
                vm.setHeight(width);
            } else {
                vm.setWidth(width);
                vm.setHeight(height);
            }
            vm.setDuration(duration);
        } catch (Exception ex) {
            NLog.e("TAGTAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return vm;
    }

    /**
     * 视频截帧
     *
     * @param filePath
     * @return
     */
    private String firstImage(String filePath, String fileName) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        String ss = fileName.substring(fileName.lastIndexOf("/"), fileName.length()) + ".jpg";
        String snapshotPath = FileUtil.createFile(thumb, ss);
        return snapshotPath;
    }

    class VideoMessage {
        private String duration;
        private String width;
        private String height;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }

    public interface OSSResultListener {
        void ossResult(ArrayList<OssImageResponse> response);

        void ossVideoResult(ArrayList<OssVideoResponse> response);
    }

}
