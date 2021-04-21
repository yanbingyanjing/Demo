package com.yjfshop123.live.ui.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.utils.CommonUtils;
import com.yjfshop123.live.utils.FileUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

import static com.zhouwei.mzbanner.MZBannerView.dpToPx;

public class HaiBaoDialog extends AbsDialogFragment implements View.OnClickListener {

    private View.OnClickListener mCallback;
    private String title;
    private String logo;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_haibao;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    private Bitmap bitmap;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.findViewById(R.id.haibao_img).getLayoutParams();
//获取当前控件的布局对象
        params.width = width - dpToPx(38);
        params.height = params.width * 4 / 3;//设置当前控件布局的高度
        mRootView.findViewById(R.id.haibao_img).setLayoutParams(params);//将设置好的布局参数应用到控件中
        ((TextView) mRootView.findViewById(R.id.user_name)).setText(UserInfoUtil.getName());
        Glide.with(this).load(logo).into((ImageView) mRootView.findViewById(R.id.haibao_img));
        if (!TextUtils.isEmpty(url)) {
          //  Glide.with(this).load(url).into((ImageView) mRootView.findViewById(R.id.qr));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Objects.requireNonNull(CommonUtils.create2DCode(url)).compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes=baos.toByteArray();
            Glide.with(this).load(bytes).into((ImageView) mRootView.findViewById(R.id.qr));

        }
        ((LinearLayout) mRootView.findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        ((LinearLayout) mRootView.findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(path)) {
                    bitmap = FileUtil.loadBitmapFromViewBySystem((LinearLayout) mRootView.findViewById(R.id.haibao));
                    if (bitmap == null) {
                        bitmap = FileUtil.loadBitmapFromView((LinearLayout) mRootView.findViewById(R.id.haibao));
                    }
                    path = FileUtil.saveImg(getContext(), bitmap);
                    if (TextUtils.isEmpty(path)) {
                        Toast.makeText(getContext(), R.string.share_fail, Toast.LENGTH_SHORT).show();
                        return;
                    }else    NToast.shortToast(getContext(), getContext().getString(R.string.has_saveed));
                }else {
                    NToast.shortToast(getContext(), getContext().getString(R.string.has_saveed));
                }
            }
        });
        ((ImageView) mRootView.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });


    }

    private String path;//海报保存路径

    private void share() {

        if (TextUtils.isEmpty(path)) {
            //如果没有保存过
            if (bitmap == null) {
                bitmap = FileUtil.loadBitmapFromViewBySystem((LinearLayout) mRootView.findViewById(R.id.haibao));
                if (bitmap == null) {
                    bitmap = FileUtil.loadBitmapFromView((LinearLayout) mRootView.findViewById(R.id.haibao));
                }
            }
            //path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, null, null);
            path = FileUtil.saveImg(getContext(), bitmap);
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(getContext(), R.string.share_fail, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Log.d("获取路径",path);
        uri = Uri.parse(path);
        if (uri != null) {
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
            share_intent.setType("image/*");  //设置分享内容的类型
            share_intent.putExtra(Intent.EXTRA_STREAM, uri);
            share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //创建分享的Dialog
            share_intent = Intent.createChooser(share_intent, "Share");
            startActivityForResult(share_intent, 1);
        } else {
            Toast.makeText(getContext(), R.string.share_fail, Toast.LENGTH_SHORT).show();
        }
    }

    Uri uri;


    public void deleteUri(Uri uri) {
        if (uri != null) {
            if (uri.toString().startsWith("content://")) {
                // content://开头的Uri
                getContext().getContentResolver().delete(uri, null, null);
            } else {
                File file = new File(getRealFilePath(getContext(), uri));
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        try {
            if (scheme == null) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
        bitmap = null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogo(String res) {
        logo = res;
    }

    String url = "";


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.cancel) {
        } else if (i == R.id.confir) {
            if (mCallback != null) {
                mCallback.onClick(v);
            }
        }
    }

}
