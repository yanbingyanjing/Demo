package com.yjfshop123.live.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;

import com.yjfshop123.live.server.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.Const.exchangeApkUrl;

//交易所下载
public class ExchangeDownloadActivity extends BaseActivityForNewUi {
    @BindView(R.id.one)
    View one;
    @BindView(R.id.btn_left_exchnage)
    ImageView btnLeft;
    @BindView(R.id.qr)
    ImageView qr;
    @BindView(R.id.save_btn)
    TextView saveBtn;
    @BindView(R.id.download_rul)
    TextView downloadRul;
    String url=exchangeApkUrl;
    String qrUrl;
  ;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_download_exchange);
        ButterKnife.bind(this);
        setHeadVisibility(View.GONE);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) one.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        one.setLayoutParams(params);
        downloadRul.setText(getString(R.string.download_url_tx, url));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Objects.requireNonNull(CommonUtils.create2DCode(url)).compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes=baos.toByteArray();
        Glide.with(this).load(bytes).into(qr);
    }

    @OnClick({R.id.save_btn, R.id.download_rul_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_btn:
                bitmap = loadBitmapFromViewBySystem(qr);
                if (bitmap == null) {
                    bitmap = loadBitmapFromView(qr);
                }
                saveImg();
                break;
            case R.id.download_rul_copy:
                setClipboard();
                break;
        }
    }

    private void saveImg() {
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;

        // 首先保存图片
        File appDir = new File(galleryPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
        NToast.shortToast(this, "图片已经保存到相册");
    }

    private static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
//        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        canvas.translate(-v.getScrollX(), -v.getScrollY());
        //我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
        v.draw(canvas);// 将 view 画到画布上
        return screenshot;
    }

    public void setClipboard() {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("text", url);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        NToast.shortToast(this, "已复制到剪贴板");
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
        bitmap = null;
    }

}
