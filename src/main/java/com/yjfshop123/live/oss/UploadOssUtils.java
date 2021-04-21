/*
package com.yjfshop123.live.oss;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yjfshop123.live.BuildConfig;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.response.OssImageResponse;
import com.yjfshop123.live.net.response.OssVideoResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.UploadDialog;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;

public class UploadOssUtils {

    private Context context;
    private UploadDialog uploadDialog;

    // 访问的endpoint地址
    public String endpoint;
    public String bucket;
    //公司账号
    private String ACCESS_ID1;
    private String ACCESS_KEY1;

    public OSS oss;

    public static SynchronousQueue<String> ff = new SynchronousQueue<String>();
    private OSSResultListener ossResultListener;
    private ArrayList<OssImageResponse> responses = new ArrayList<>();
    private ArrayList<OssVideoResponse> videoResponses = new ArrayList<>();
    private ArrayList<String> updated = new ArrayList<>();

    public void setOssResultListener(OSSResultListener ossResultListener) {
        this.ossResultListener = ossResultListener;
    }


    public UploadOssUtils(Context context) {
        this.context = context;
    }

    public void initData() {
        String oss_key[] = UserInfoUtil.getOssKey().split("@@");
        ACCESS_ID1 = oss_key[0];
        ACCESS_KEY1 = oss_key[1];
        endpoint = oss_key[2];
        bucket = oss_key[3];

        initOSSConfig();
    }

    private void initOSSConfig() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ACCESS_ID1, ACCESS_KEY1);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
    }

    //上传图片(单图)
    public void uploadData(String uploadFilePath, final String type, final String scenes, final String userId, UploadDialog uploadDialog_) {
        uploadDialog = uploadDialog_;
        int size = 0;
        uploadFilePath = uploadFilePath.replace("/raw/", "");
        File file = new File(uploadFilePath);
        if (null != file || file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//zhibo/2018/12/14/
        String obj = "zhibo/" + getImageObjectKey(UUID.randomUUID().toString(), uploadFilePath.substring(uploadFilePath.lastIndexOf("."), uploadFilePath.length()));
        PutObjectRequest put = new PutObjectRequest(bucket, obj, uploadFilePath);

        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", Const.DOMAIN + "/app/material/aliyunossCallback");
                //callbackBody可以自定义传入的信息
//                    put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
//                    put("callbackBodyType", "application/json");

//                    put("callbackBody", "{\"object\":${object},\"size\":${size},\"userid\":${x:userid},\"type\":${x:type},\"file\":${x:file},\"length\":${x:length},\"title\":${x:title},\"artist\":${x:artist}}");
//                put("callbackBody", "filename=${object}&size=${size}&userid=${x:userid}&type=${x:type}&file=${x:file}&length=${x:length}&title=${x:title}&artist=${x:artist}");
                put("callbackBody", "user_id=${x:user_id}&bucket=${x:bucket}&object=${x:object}&etag=${x:etag}&size=${x:size}&mime_type=${x:mime_type}&class_id=${x:class_id}");
            }
        });
        String class_id = "";
        if (type.equals("image")) {
            if (scenes.equals("touxiang")) {
                class_id = "6";
            } else {
                class_id = "1";
            }
        } else if (type.equals("video")) {
            class_id = "2";
        } else if (type.equals("audio")) {
            class_id = "3";
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("x:user_id", userId);
        map.put("x:bucket", bucket);
        map.put("x:object", obj);
        map.put("x:etag", "");
        map.put("x:size", size + "");
        map.put("x:mime_type", type);
        map.put("x:class_id", class_id);
//        map.put("x:extra_info", "{'width:'" + size + ",'height:'" + size + "}");
        put.setCallbackVars(map);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize + " baifen:" + (currentSize / totalSize));
                if (uploadDialog != null && !type.equals("image")) {
                    int progress = (int) (100 * currentSize / totalSize);
                    Log.e("process  ", progress + "");
//                    uploadDialog.uploadProcess.setText(progress + "%");
                    uploadDialog.setProcess(progress);
                }
            }
        });

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(final PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                try {
                    JSONObject jsonObject = new JSONObject(serverCallbackReturnJson);
                    String str = jsonObject.getString("data");
                    if (!str.equals("null")) {
//                        BaseActivity.entity.getBgm().setName(BaseActivity.musicBean.song);
//                        BaseActivity.entity.getBgm().setId(Integer.parseInt(str));

                        if (type.equals("image") || type.equals("audio")) {
                            OssImageResponse response = JsonMananger.jsonToBean(str, OssImageResponse.class);
                            responses.add(response);
                            ossResultListener.ossResult(responses);
                        } else if (type.equals("video")) {
                            videoResponses.clear();
                            OssVideoResponse response = JsonMananger.jsonToBean(str, OssVideoResponse.class);
                            videoResponses.add(response);
                            ossResultListener.ossVideoResult(videoResponses);
                        }
                        if (uploadDialog != null) {
                            uploadDialog.dissmis();
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //将上传成功的图片地址传给自己的服务器后台，比如修改用户数据库中，用户头像的url。
                //修改后台url成功后，再利用glide 下载最新的照片，修改本地头像图片。
                //request.getObjectKey() 是图片地址，但是不包含，OSS 图片域名
//	            uploadImage(request.getObjectKey());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
                uploadDialog.dissmis();
//                try {
//                    ff.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    //上传图片（多图）
    public void ossUploadList(final ArrayList<String> urls, final String type, final String scenes, final String userId, UploadDialog uploadDialog_) {
        uploadDialog = uploadDialog_;
        VideoMessage vm = new VideoMessage();
        if (urls.size() <= 0) {
            if (type.equals("image") || type.equals("audio")) {
                ossResultListener.ossResult(responses);
            } else if (type.equals("video")) {
                ossResultListener.ossVideoResult(videoResponses);
            }
            uploadDialog.dissmis();

            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
            return;// 这个return必须有，否则下面报越界异常，原因自己思考下哈
        }
        if (type.equals("video")) {
            vm = getPlayTime(urls.get(0));
        }
        String url = urls.get(0);
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUploadList(urls, type, scenes, userId, uploadDialog);
            return;
        } else {
            url = url.replace("/raw/", "");
        }
        if (url.startsWith("/external")) {
            String path = "content://media" + url;

            ContentResolver res = context.getContentResolver();
            Cursor c = res.query(Uri.parse(path), null, null, null, null);
            int id = -1;
            if (c != null) {
                c.moveToFirst();
                int actual_image_column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                url = c.getString(actual_image_column_index);
            }

//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor actualimagecursor = this.ctx.managedQuery(uri, proj, null, null, null);
//            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            url = actualimagecursor.getString(actual_image_column_index);
        }
        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUploadList(urls, type, scenes, userId, uploadDialog);
            return;
        }
        // 文件后缀
        String fileSuffix = "";
        int size = 0;
        if (file.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // 获取文件后缀名
            fileSuffix = file.getName().substring(file.getName().lastIndexOf("."));
        }
        final String obj = "zhibo/" + getImageObjectKey(UUID.randomUUID().toString(), fileSuffix);
        PutObjectRequest put = new PutObjectRequest(bucket, obj, url);
        String class_id = "";
        if (type.equals("image")) {
            if (scenes.equals("daren")) {
                class_id = "4";
            } else if (scenes.equals("shiming")) {
                class_id = "5";
            } else {
                class_id = "1";
            }
        } else if (type.equals("video")) {
            class_id = "2";
        }
        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", Const.DOMAIN + "/app/material/aliyunossCallback");
                //callbackBody可以自定义传入的信息
//                    put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
//                    put("callbackBodyType", "application/json");

//                    put("callbackBody", "{\"object\":${object},\"size\":${size},\"userid\":${x:userid},\"type\":${x:type},\"file\":${x:file},\"length\":${x:length},\"title\":${x:title},\"artist\":${x:artist}}");
//                put("callbackBody", "filename=${object}&size=${size}&userid=${x:userid}&type=${x:type}&file=${x:file}&length=${x:length}&title=${x:title}&artist=${x:artist}");
                if (!type.equals("video")) {
                    put("callbackBody", "user_id=${x:user_id}&bucket=${x:bucket}&object=${x:object}&etag=${x:etag}&size=${x:size}&mime_type=${x:mime_type}&class_id=${x:class_id}");
                } else {
                    put("callbackBody", "user_id=${x:user_id}&bucket=${x:bucket}&object=${x:object}&etag=${x:etag}&size=${x:size}&mime_type=${x:mime_type}&class_id=${x:class_id}&extra_info=${x:extra_info}&video_cover_img=${x:video_cover_img}");
                }
            }
        });
        HashMap<String, String> map = new HashMap<>();
        map.put("x:user_id", userId);
        map.put("x:bucket", bucket);
        map.put("x:object", obj);
        map.put("x:etag", "");
        map.put("x:size", size + "");
        map.put("x:mime_type", type);
        map.put("x:class_id", class_id);
        if (type.equals("video")) {
            map.put("x:extra_info", "{\"width\":" + vm.getWidth() + ",\"height\":" + vm.getHeight() + "}");
            map.put("x:video_cover_img", obj + ".jpg");
        }
        put.setCallbackVars(map);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize + " baifen:" + (currentSize / totalSize));
                if (uploadDialog != null && !type.equals("image")) {
                    int progress = (int) (100 * currentSize / totalSize);
                    Log.e("process  ", progress + "");
//                    uploadDialog.uploadProcess.setText(progress + "%");
                    uploadDialog.setProcess(progress);
                }
            }
        });

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(final PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                if (type.equals("video")) {
                    String str = firstImage(urls.get(0), obj);
                    ossUploadImage(obj, str, type);
                }

                String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                try {
                    JSONObject jsonObject = new JSONObject(serverCallbackReturnJson);
                    String str = jsonObject.getString("data");
                    if (!str.equals("null")) {
//                        BaseActivity.entity.getBgm().setName(BaseActivity.musicBean.song);
//                        BaseActivity.entity.getBgm().setId(Integer.parseInt(str));
                        if (type.equals("image") || type.equals("audio")) {
                            OssImageResponse response = JsonMananger.jsonToBean(str, OssImageResponse.class);
                            responses.add(response);
                            uploadDialog.setPhotoProcess(responses.size());
                        } else if (type.equals("video")) {
                            videoResponses.clear();
                            OssVideoResponse response = JsonMananger.jsonToBean(str, OssVideoResponse.class);
                            videoResponses.add(response);
                        }
                        updated.add(urls.get(0));
                        urls.remove(0);
                        ossUploadList(urls, type, scenes, userId, uploadDialog);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //将上传成功的图片地址传给自己的服务器后台，比如修改用户数据库中，用户头像的url。
                //修改后台url成功后，再利用glide 下载最新的照片，修改本地头像图片。
                //request.getObjectKey() 是图片地址，但是不包含，OSS 图片域名
//	            uploadImage(request.getObjectKey());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }

                uploadDialog.dissmis();

                EventBus.getDefault().post(updated,Config.EVENT_START);

//                try {
//                    ff.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }


    private void ossUploadImage(String obj, String urls, final String type) {
        File file = new File(urls);
        if (null == file || !file.exists()) {
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            return;
        }
        PutObjectRequest put = new PutObjectRequest(bucket, obj + ".jpg", urls);
        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(final PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                try {
                    String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                    JSONObject jsonObject = new JSONObject(serverCallbackReturnJson);
                    String str = jsonObject.getString("data");
                    if (!str.equals("null")) {
//                        BaseActivity.entity.getBgm().setName(BaseActivity.musicBean.song);
//                        BaseActivity.entity.getBgm().setId(Integer.parseInt(str));
                        if (type.equals("image") || type.equals("audio")) {
                            OssImageResponse response = JsonMananger.jsonToBean(str, OssImageResponse.class);
                            responses.add(response);
                            uploadDialog.setPhotoProcess(responses.size());
                        } else if (type.equals("video")) {
                            videoResponses.clear();
                            OssVideoResponse response = JsonMananger.jsonToBean(str, OssVideoResponse.class);
                            videoResponses.add(response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //将上传成功的图片地址传给自己的服务器后台，比如修改用户数据库中，用户头像的url。
                //修改后台url成功后，再利用glide 下载最新的照片，修改本地头像图片。
                //request.getObjectKey() 是图片地址，但是不包含，OSS 图片域名
//	            uploadImage(request.getObjectKey());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
                uploadDialog.dissmis();
                try {
                    ff.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //通过UserCode 加上日期组装 OSS路径
    private static String getImageObjectKey(String strUserCode, String addr) {
        Date date = new Date();
        return new SimpleDateFormat("yyyy/M/d").format(date) + "/" + strUserCode + new SimpleDateFormat("yyyyMMddssSSS").format(date) + addr;

    }

    //获取视频宽高时常
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
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return vm;
    }


    //视频截帧
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
*/
