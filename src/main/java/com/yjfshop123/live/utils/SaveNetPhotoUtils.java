package com.yjfshop123.live.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.utils.CommonUtils;
import com.yjfshop123.live.server.widget.LoadDialog;

import org.jetbrains.annotations.Nls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author ThirdGoddess
 * @email ofmyhub@gmail.com
 * @Github https://github.com/ThirdGoddess
 * @date :2020-03-14 10:11
 */
public class SaveNetPhotoUtils {
    private static String shareTitle;//分享链接
    private static Context contexts;
    private static String photoUrls;
    private static Bitmap bitmap;
    private static ArrayList<Uri> uriList;
    private static String mSaveMessage = "failed";
    private static String[] photoUrlList;
    //自定义名字
    private static String photoNames;

    /**
     * 保存图片，无须自定义名字
     *
     * @param context
     * @param photoUrl
     */
    public static void savePhoto(Context context, String photoUrl) {
        contexts = context;
        photoUrls = photoUrl;
        new Thread(saveFileRunnable).start();
    }

    public static LoadDialog loadDialog;
    private static OnsaveOk onsaveOk;

    /**
     * 保存图片，无须自定义名字
     *
     * @param context
     * @param photoUrl
     */
    public static void savePhotoList(Context context, String[] photoUrl) {
        contexts = context;
        photoUrlList = photoUrl;
        loadDialog = new LoadDialog(context, false);
        loadDialog.show();
        new Thread(saveListFileRunnableForShare).start();
    }

    public static void savePhotoListForShare(Context context, String[] photoUrl, String shareTitle1, OnsaveOk onsaveOk1) {
        contexts = context;
        photoUrlList = photoUrl;
        shareTitle = shareTitle1;
        uriList = new ArrayList<>();
        onsaveOk = onsaveOk1;
        loadDialog = new LoadDialog(context, false);
        loadDialog.show();
        new Thread(saveListFileRunnableForShare).start();
    }

    /**
     * 定义图片名字保存到相册
     *
     * @param context
     * @param photoUrl
     * @param photoName 图片名字，定义格式 name.jpg/name.png/...
     */
    public static void savePhoto(Context context, String photoUrl, String photoName) {
        contexts = context;
        photoUrls = photoUrl;
        photoNames = photoName;

        new Thread(saveFileRunnable2).start();
    }

    private static Runnable saveListFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (photoUrlList == null) return;
                for (int i = 0; i < photoUrlList.length; i++) {
                    String urlPhoto = photoUrlList[i];
                    if (!TextUtils.isEmpty(urlPhoto)) {
                        URL url = new URL(urlPhoto);
                        InputStream inputStream = url.openStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    }
                    saveFile(bitmap);
                }
                mSaveMessage = "保存成功！";
            } catch (IOException e) {
                mSaveMessage = "保存失败";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    private static Runnable saveListFileRunnableForShare = new Runnable() {
        @Override
        public void run() {
            try {
                if (photoUrlList == null) return;
                for (int i = 0; i < photoUrlList.length; i++) {
                    String urlPhoto = photoUrlList[i];
                    if (!TextUtils.isEmpty(urlPhoto)) {
                        URL url = new URL(urlPhoto);
                        InputStream inputStream = url.openStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        if (!TextUtils.isEmpty(shareTitle)) {
                            Bitmap shareBitmap = CommonUtils.create2DCodeWithWhite(shareTitle);
                            if (shareBitmap != null) {
                                //TODO 吧二维码合成到分享图片上（）
                                bitmap = combineBitmap(bitmap, shareBitmap);
                            }
                        }
                        inputStream.close();
                    }
                    saveFile(bitmap);
                }
                mSaveMessage = "保存成功！";
            } catch (IOException e) {
                mSaveMessage = "保存失败";
                e.printStackTrace();
            } catch (Exception e) {
                mSaveMessage = "保存失败";

                e.printStackTrace();
            }
            messageHandlerForShare.sendMessage(messageHandlerForShare.obtainMessage());
        }
    };

    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);

        int src_w = foreground.getWidth();
        int src_h = foreground.getHeight();
        float scale_w = ((float) bgWidth / 5) / src_w;
        float scale_h = ((float) bgWidth / 5) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(foreground, 0, 0, src_w, src_h, matrix,
                true);

        canvas.drawBitmap(dstbmp, bgWidth - dstbmp.getWidth() - 20,
                bgHeight - dstbmp.getHeight() - 20, null);
        canvas.save();
        canvas.restore();
        return newmap;
    }

    private static Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (!TextUtils.isEmpty(photoUrls)) {
                    URL url = new URL(photoUrls);
                    InputStream inputStream = url.openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
                saveFile(bitmap);
                mSaveMessage = "success！";
            } catch (IOException e) {
                mSaveMessage = "failed";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    private static Runnable saveFileRunnable2 = new Runnable() {
        @Override
        public void run() {
            try {
                if (!TextUtils.isEmpty(photoUrls)) {
                    URL url = new URL(photoUrls);
                    InputStream inputStream = url.openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
                saveFile(bitmap, photoNames);
                mSaveMessage = "success！";
            } catch (IOException e) {
                mSaveMessage = "failed";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    /**
     * 保存成功和失败通知
     */
    @SuppressLint("HandlerLeak")
    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (loadDialog != null) loadDialog.dismiss();
            Toast.makeText(contexts, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 保存成功和失败通知
     */
    @SuppressLint("HandlerLeak")
    private static Handler messageHandlerForShare = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //  share();
            if (loadDialog != null) loadDialog.dismiss();
            if (onsaveOk != null) onsaveOk.onSave();
            //Toast.makeText(contexts, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };

    private static void share() {

        if (uriList != null) {

            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            share_intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
            share_intent.setType("image/*");  //设置分享内容的类型

            //share_intent.putExtra(Intent.EXTRA_TEXT, shareTitle);
            NLog.d("数据", uriList.size());
            share_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //创建分享的Dialog
            //  share_intent = Intent.createChooser(share_intent, "Share");
            ((Activity) contexts).startActivity(share_intent);
        } else {
            Toast.makeText(contexts, contexts.getString(R.string.share_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存图片
     *
     * @param bm
     * @throws IOException
     */
    public static void saveFile(Bitmap bm) throws IOException {
//        File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
//        if (!dirFile.exists()) {
//            dirFile.mkdir();
//        }
//
//        //图片命名
//        String fileName = UUID.randomUUID().toString() + ".jpg";
//        File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + fileName);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//        bos.flush();
//        bos.close();
        String savePath = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            savePath = saveImageToGallery2(contexts, bitmap);
            if (uriList != null) uriList.add(Uri.parse(savePath));
        } else {
            String path = MediaStore.Images.Media.insertImage(contexts.getContentResolver(), bm, null, null);


            Uri uri = Uri.parse(path);
            NLog.d("照片保存", path + "  " + uri.getScheme());

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = contexts.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            savePath = cursor.getString(column_index);

            // contexts.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            if (uriList != null) uriList.add(Uri.parse(path));

        }
        if (!TextUtils.isEmpty(savePath)) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fromUri = Uri.fromFile(new File(savePath));
            intent.setData(fromUri);
            contexts.sendBroadcast(intent);
        }
        //广播通知相册有图片更新
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(myCaptureFile);
//        intent.setData(uri);
//        contexts.sendBroadcast(intent);

    }

    public static String saveImageToGallery2(Context context, Bitmap image) {
        Long mImageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
        String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s.png";//图片名称，以"Screenshot"+时间戳命名
        String mImageFileName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, imageDate);


        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
                + File.separator + "dgj"); //Environment.DIRECTORY_SCREENSHOTS:截图,图库中显示的文件夹名。"dh"
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, mImageFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATE_ADDED, mImageTime / 1000);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, mImageTime / 1000);
        values.put(MediaStore.MediaColumns.DATE_EXPIRES, (mImageTime + DateUtils.DAY_IN_MILLIS) / 1000);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        final Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        String path = "";
        try {
            // First, write the actual data for our screenshot
            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (!image.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    throw new IOException("Failed to compress");
                }
            }
            // Everything went well above, publish it!
            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            values.putNull(MediaStore.MediaColumns.DATE_EXPIRES);
            resolver.update(uri, values, null, null);
            path = uri.getPath();
        } catch (IOException e) {
            resolver.delete(uri, null, null);
            // resolver.delete(uri, null);
            Log.d("Exception", e.toString());
        } finally {
            return path;
        }
    }

    /**
     * 保存图片
     *
     * @param bm
     * @param photoName 图片命名
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String photoName) throws IOException {
        File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        //图片命名后保存到相册
        File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + photoName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        //广播通知相册有图片更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        contexts.sendBroadcast(intent);
    }

    public interface OnsaveOk {
        void onSave();
    }
}

