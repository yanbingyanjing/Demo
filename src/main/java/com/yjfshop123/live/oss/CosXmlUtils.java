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
    private UploadDialog uploadDialog;//??????dialog

    private String region;//????????????????????????
    private String secretId; //???????????? secretId
    private String secretKey; //???????????? secretKey
    private String bucket;//?????????

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
        //?????? CosXmlServiceConfig ????????????????????????????????????????????????
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // ?????? https ??????, ?????? http ??????
                .setDebuggable(false)
                .builder();

        //????????????????????????
        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(secretId, secretKey, 300);

        cosXmlService = new CosXmlSimpleService(context, serviceConfig, credentialProvider);
    }

    private UploadObjectCallbackRequest uploadObjectCallbackRequest;

    /**
     * ????????????(???????????????)
     *
     * @param uploadFilePath ????????????????????????
     * @param type ??????????????????
     * @param class_id???1:???????????? 2:???????????? 3:?????????????????? 4:???????????? 5:???????????? 6:???????????? 11:???????????? 12:???????????? 21:??????????????? 51 ????????????
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

        // ????????? TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        /**
         ?????????????????????????????????????????????????????????????????????????????? >= 2M ???????????????????????????????????????????????????????????? 1M, ?????????????????? 5M ??????????????????????????????????????????????????? 5M???
         TransferConfig transferConfig = new TransferConfig.Builder()
         .setDividsionForCopy(5 * 1024 * 1024) // ?????????????????????????????????????????????
         .setSliceSizeForCopy(5 * 1024 * 1024) //??????????????????????????????
         .setDivisionForUpload(2 * 1024 * 1024) // ?????????????????????????????????????????????
         .setSliceSizeForUpload(1024 * 1024) //??????????????????????????????
         .build();
         */

        //????????? TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String cosPath; //???????????? COS ??????????????????, ????????? cosPath = "text.txt";
        String srcPath = uploadFilePath; // ??? srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        String uploadId = null; //????????????????????????????????? UploadId?????????????????? uploadId ????????????????????????????????? null???

        cosPath = getImageObjectKey(userId) + uploadFilePath.substring(uploadFilePath.lastIndexOf("."), uploadFilePath.length());

        uploadObjectCallbackRequest = new UploadObjectCallbackRequest();
        uploadObjectCallbackRequest.setClass_id(class_id);
        uploadObjectCallbackRequest.setObject(cosPath);
        uploadObjectCallbackRequest.setBucket(bucket);
        uploadObjectCallbackRequest.setEtag("");
        uploadObjectCallbackRequest.setMime_type(type);//?????? image,video,audio
        uploadObjectCallbackRequest.setExtra_info("");//???????????????json????????? ??? ?????????{???width???:600,???height???:480} ???
        uploadObjectCallbackRequest.setVideo_cover_img("");//?????????????????????mime_type=video???????????????
        uploadObjectCallbackRequest.setSize(size);

        if (!Const.IS_COS){
            //???????????????image???video???audio???
            //???????????????????????????????????????
            String filetype = "image";
            //???1:???????????? 2:???????????? 3:?????????????????? 4:???????????? 5:???????????? 6:???????????? 11:???????????? 12:???????????? 21:??????????????? 51 ????????????
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
                    NToast.shortToast(context, "????????????");
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
            //????????????
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);

            /**
             * ??????????????????????????????????????? TransferManager ??? upload(string, string, byte[]) ????????????;
             * byte[] bytes = "this is a test".getBytes(Charset.forName("UTF-8"));
             * cosxmlUploadTask = transferManager.upload(bucket, cosPath, bytes);
             */

            /**
             * ???????????????????????????????????? TransferManager ??? upload(String, String, InputStream) ???????????????
             * InputStream inputStream = new ByteArrayInputStream("this is a test".getBytes(Charset.forName("UTF-8")));
             * cosxmlUploadTask = transferManager.upload(bucket, cosPath, inputStream);
             */

            //????????????????????????
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

            //????????????????????????
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
                                NToast.shortToast(context, "????????????");
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

            //????????????????????????, ????????????????????????
            cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    NLog.e("TAGTAG", "Task state:" + state.name());
                }
            });

            /**
             ?????????????????????????????????????????????
             PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
             putObjectRequest.setRegion(region); //??????????????????????????????
             putObjectRequest.setNeedMD5(true); //???????????? Md5 ??????
             COSXMLUploadTask cosxmlUploadTask = transferManager.upload(putObjectRequest, uploadId);
             */

            /*//????????????
            cosxmlUploadTask.cancel();

            //????????????
            cosxmlUploadTask.pause();

            //????????????
            cosxmlUploadTask.resume();*/
        }
    }

    /**
     * ????????????
     *
     * @param urls ??????????????????????????????
     * @param type ??????????????????
     * @param class_id ???1:???????????? 2:???????????? 3:?????????????????? 4:???????????? 5:???????????? 6:???????????? 11:???????????? 12:???????????? 21:??????????????? 51 ????????????
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
            // ????????????
            return;
        }

        String url = urls.get(0);
        //?????????????????? ????????????????????????
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

        //????????????????????? ????????? ????????????
        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // ????????????????????????????????????????????????????????????????????????????????????????????????
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

        // ????????? TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //????????? TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String cosPath; //???????????? COS ??????????????????, ????????? cosPath = "text.txt";
        final String srcPath = url; // ??? srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        String uploadId = null; //????????????????????????????????? UploadId?????????????????? uploadId ????????????????????????????????? null???

        String addr = url.substring(url.lastIndexOf("."), url.length());
        cosPath = getImageObjectKey(userId) + addr;
        final String coverImgPath = cosPath + ".jpg";

        uploadObjectCallbackRequest = new UploadObjectCallbackRequest();
        uploadObjectCallbackRequest.setClass_id(class_id);
        uploadObjectCallbackRequest.setObject(cosPath);
        uploadObjectCallbackRequest.setBucket(bucket);
        uploadObjectCallbackRequest.setEtag("");
        uploadObjectCallbackRequest.setMime_type(type);//?????? image,video,audio
        uploadObjectCallbackRequest.setSize(size);
        if (type.equals("video")) {
            uploadObjectCallbackRequest.setExtra_info("{\"width\":" + vm.getWidth() + ",\"height\":" + vm.getHeight() + "}");//???????????????json????????? ??? ?????????{???width???:600,???height???:480} ???
            uploadObjectCallbackRequest.setVideo_cover_img(coverImgPath);//?????????????????????mime_type=video???????????????
        }else {
            uploadObjectCallbackRequest.setExtra_info("");//???????????????json????????? ??? ?????????{???width???:600,???height???:480} ???
            uploadObjectCallbackRequest.setVideo_cover_img("");//?????????????????????mime_type=video???????????????
        }

        if (!Const.IS_COS){
            //???????????????image???video???audio???
            //???????????????????????????????????????
            String filetype = "image";
            //???1:???????????? 2:???????????? 3:?????????????????? 4:???????????? 5:???????????? 6:???????????? 11:???????????? 12:???????????? 21:??????????????? 51 ????????????
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
                                NToast.shortToast(context, "????????????");
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
            //????????????
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);

            //????????????????????????
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

            //????????????????????????
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
                                NToast.shortToast(context, "????????????");
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

            //????????????????????????, ????????????????????????
            cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    NLog.e("TAGTAG", "Task state:" + state.name());
                }
            });
        }
    }

    /**
     * ??????????????????
     *
     * @param cosPath
     * @param urls
     */
    private void ossUploadImage(String cosPath, String urls) {
        File file = new File(urls);
        if (null == file || !file.exists()) {
            // ????????????????????????????????????????????????????????????????????????????????????????????????
            return;
        }

        // ????????? TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //????????? TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        if (!Const.IS_COS){
            //???????????????image???video???audio???
            //???????????????????????????????????????
            String filetype = "image";
            uploadObjectCallbackRequest.setObject(cosPath);
            ClientUploadUtils.getInstance().upload(uploadUrl, urls, filetype/*, cosPath*/, new RequestCallback2() {
                @Override
                public void onError(int errCode, String errInfo) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NToast.shortToast(context, "????????????");
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
            //????????????
            COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, urls, null);

            //????????????????????????
            cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long complete, long target) {
                    float progress = 1.0f * complete / target * 100;
                    NLog.e("TAGTAG",  String.format("progress = %d%%", (int)progress));
                }
            });

            //????????????????????????
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
                                NToast.shortToast(context, "????????????");
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

            //????????????????????????, ????????????????????????
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
        //2019/07/01/md5(??????uid+???????????????).??????
        //upload/20190715/abcdefg.png
        Date date = new Date();
        return "upload/" + new SimpleDateFormat("yyyyMMdd").format(date) + "/" + MD5.encrypt(userId + System.currentTimeMillis());

    }

    /**
     * ????????????????????????
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

            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//??????(??????)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//???
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//???
            String rotationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // ??????????????????
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
     * ????????????
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
