package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;

import com.yjfshop123.live.R;
import com.yjfshop123.live.server.utils.photo.PhotoView;
import com.yjfshop123.live.server.utils.photo.PhotoViewAttacher;
import com.yjfshop123.live.ui.widget.HackyViewPager;
import com.bumptech.glide.Glide;
import com.yjfshop123.live.ui.widget.SucaiSaveTipsDialog;
import com.yjfshop123.live.utils.CheckInstalledUtil;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SaveNetPhotoUtils;
import com.yjfshop123.live.utils.SystemUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

public class XPicturePagerActivity extends BaseActivity implements View.OnLongClickListener {

    private HackyViewPager mViewPager;
    private ImageAdapter mImageAdapter;
    private int mCurrentIndex = 0;
    private List<String> images;

    private TextView numIndicator;
    private int count;
    private ImageView save;
    private Context mContext;
    private static int mIndex = 0;
    private boolean is_need_save = false;
    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        public void onPageSelected(int position) {
            mCurrentIndex = position;

            numIndicator.setText((position + 1) + "/" + count);

            /*if (position == 0){
                slide(true);
            }else {
                slide(false);
            }*/
        }

        public void onPageScrollStateChanged(int state) {

        }
    };
    String erweima = "";

    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.x_rc_fr_photo);

        setHeadVisibility(View.GONE);

        mViewPager = findViewById(R.id.viewpager);
        numIndicator = findViewById(R.id.x_numIndicator);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        save = findViewById(R.id.save);
        try {
            images = JsonMananger.jsonToBean(getIntent().getStringExtra("Picture"), List.class);
            mIndex = getIntent().getIntExtra(Config.POSITION, 0);
            is_need_save = getIntent().getBooleanExtra("is_need_save", false);
            erweima = getIntent().getStringExtra("erweima");
        } catch (HttpException e) {
            e.printStackTrace();
        }

        mImageAdapter = new ImageAdapter(images);
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.setCurrentItem(mIndex);
        mCurrentIndex = mIndex;

        findViewById(R.id.x_fr_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        count = images.size();
        numIndicator.setText((mCurrentIndex + 1) + "/" + count);
        if (is_need_save) {
            save.setVisibility(View.VISIBLE);
        } else save.setVisibility(View.GONE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] a = new String[1];
                a[0] = images.get(mCurrentIndex);
                SaveNetPhotoUtils.savePhotoListForShare(XPicturePagerActivity.this, a, erweima,
                        new SaveNetPhotoUtils.OnsaveOk() {
                            @Override
                            public void onSave() {
                                //保存图片弹窗
                                NToast.shortToast(XPicturePagerActivity.this, "已保存到相册");
                            }
                        });
            }
        });
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (mIndex == 0){
            slide(true);
        }else {
            slide(false);
        }*/
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onLongClick(View v) {
        /*final String imageInfo = mImageAdapter.getImageInfo(mCurrentIndex);
        if (imageInfo == null) {
            return false;
        }

        String[] items = new String[]{"保存图片"};
        OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    AsyncTaskManager.getInstance(mContext).request(imageInfo, 4244, XPicturePagerActivity.this);
                }
            }
        }).show();*/
        return true;
    }

    /*@Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        File cacheFile = null;
        if (requestCode == 4244){
            FutureTarget<File> future = Glide.with(mContext).load(parameter).downloadOnly(100, 100);
            try {
                cacheFile = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return cacheFile;
    }*/

    /*@Override
    public void onSuccess(int requestCode, Object result) {
        if (requestCode == 4244){
            if (result != null){
                File cacheFile = (File) result;

                String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                if (!PermissionCheckUtil.requestPermissions(XPicturePagerActivity.this, permissions)) {
                    return;
                }

                File path = Environment.getExternalStorageDirectory();
                String defaultPath = getString(R.string.rc_image_default_saved_path);
                String appName = SystemUtils.getAppName(XPicturePagerActivity.this);
                StringBuilder builder = new StringBuilder(defaultPath);
                if (appName != null) {
                    builder.append(appName).append(File.separator);
                }

                String appPath = builder.toString();
                File dir = new File(path, appPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (cacheFile != null && cacheFile.exists()) {
                    String name = System.currentTimeMillis() + ".jpg";
                    FileUtils.copyFile(cacheFile, dir.getPath() + File.separator, name);
                    MediaScannerConnection.scanFile(mContext, new String[]{dir.getPath() + File.separator + name}, (String[])null, (MediaScannerConnection.OnScanCompletedListener)null);
                    Toast.makeText(mContext, getString(R.string.rc_save_picture_at), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.rc_src_file_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    /*@Override
    public void onFailure(int requestCode, int state, Object result) {

    }*/

    private class ImageAdapter extends PagerAdapter {
        private List<String> mImageList;

        private ImageAdapter(List<String> images) {
            mImageList = images;
        }

        private View newView(Context context) {
            View result = LayoutInflater.from(context).inflate(R.layout.rc_fr_image, null);
            ViewHolder holder = new ViewHolder();
            holder.erweima_logo = result.findViewById(R.id.erweima_logo);
            holder.progressBar = result.findViewById(R.id.rc_progress);
            holder.progressText = result.findViewById(R.id.rc_txt);
            holder.photoView = result.findViewById(R.id.rc_photoView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.erweima_logo.getLayoutParams();
            //获取当前控件的布局对象
            params.width = SystemUtils.getScreenWidth(XPicturePagerActivity.this) / 5;
            params.height = params.width;//设置当前控件布局的高度
            holder.erweima_logo.setLayoutParams(params);

            holder.photoView.setOnLongClickListener(XPicturePagerActivity.this);

            holder.photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                public void onPhotoTap(View view, float x, float y) {
                    XPicturePagerActivity.this.finish();
                }

                public void onOutsidePhotoTap() {

                }
            });

            result.setTag(holder);

            return result;
        }

        public String getItem(int index) {
            return mImageList.get(index);
        }

        @Override
        public int getItemPosition(Object object) {
            return -2;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageView = this.newView(container.getContext());

            updatePhotoView(position, imageView);

            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewHolder holder = (ViewHolder) container.findViewById(position).getTag();
            holder.photoView.setImageURI(null);
            container.removeView((View) object);
        }

        private void updatePhotoView(int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            String url = mImageList.get(position);

            holder.progressText.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(erweima)) {
                NLog.d("二维码", erweima);
                holder.erweima_logo.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Objects.requireNonNull(com.yjfshop123.live.server.utils.CommonUtils.create2DCodeWithWhite(erweima)).compress(Bitmap.CompressFormat.PNG, 100, baos);
                        final byte[] bytes = baos.toByteArray();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!XPicturePagerActivity.this.isDestroyed())
                                    Glide.with(XPicturePagerActivity.this).load(bytes).into(holder.erweima_logo);
                            }
                        });
                    }
                }).start();


            } else {
                holder.erweima_logo.setVisibility(View.INVISIBLE);
            }
            if(url.contains("/storage")){
                Glide.with(mContext)
                        .load(url)
                        .into(holder.photoView);
            }
           else Glide.with(mContext)
                    .load(CommonUtils.getUrl(url))
                    .into(holder.photoView);
        }

        public String getImageInfo(int position) {
            return mImageList.get(position);
        }

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            PhotoView photoView;
            ImageView erweima_logo;

            public ViewHolder() {

            }
        }
    }

}

