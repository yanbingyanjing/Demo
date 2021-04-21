package com.yjfshop123.live.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShareActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.activity_share_replace)
    TextView activity_share_replace;
    @BindView(R.id.activity_share_save)
    TextView activity_share_save;
    @BindView(R.id.activity_share_fl)
    FrameLayout activity_share_fl;
    @BindView(R.id.activity_share_code)
    ImageView activity_share_code;
    @BindView(R.id.activity_share_bg)
    ImageView activity_share_bg;

    private String qr_url;
    private Bitmap bitmap;

    private int mNumber = 0;
    private static SparseIntArray sLiveBgCountMap;

    static {
        sLiveBgCountMap = new SparseIntArray();
        sLiveBgCountMap.put(0, R.drawable.share_1);
        sLiveBgCountMap.put(1, R.drawable.share_2);
        sLiveBgCountMap.put(2, R.drawable.share_3);
        sLiveBgCountMap.put(3, R.drawable.share_4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        setHeadLayout();
        mContext = this;

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(getString(R.string.reward_16));

        initView();
    }

    private void initView() {
        activity_share_replace.setOnClickListener(this);
        activity_share_save.setOnClickListener(this);

        qr_url = getIntent().getStringExtra("qr_url");
        Glide.with(mContext).load(qr_url).into(activity_share_code);
//        Glide.with(ShareActivity.this).asBitmap().load(qr_url).listener(new LoadListen()).into(activity_share_code);
        shareBg();
    }

    private void shareBg(){
        int number = new Random().nextInt(4);
        if (number == mNumber){
            shareBg();
        }else {
            mNumber = number;
            activity_share_bg.setImageResource(sLiveBgCountMap.get(number));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_share_replace:
                shareBg();
                break;
            case R.id.activity_share_save:
                bitmap = loadBitmapFromViewBySystem(activity_share_fl);
                if (bitmap == null){
                    bitmap = loadBitmapFromView(activity_share_fl);
                }
                saveImg();
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

    /*class LoadListen implements RequestListener<Bitmap> {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            bitmap = resource;
            return false;
        }
    }*/

    private static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
//        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
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

    @Override
    protected void onDestroy() {
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
        bitmap = null;
    }
}
