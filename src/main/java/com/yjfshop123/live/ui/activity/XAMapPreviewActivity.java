package com.yjfshop123.live.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.yjfshop123.live.R;
import com.yjfshop123.live.server.utils.imageloader.CircleBitmapDisplayer;
import com.yjfshop123.live.server.utils.imageloader.DisplayImageOptions;
import com.yjfshop123.live.server.utils.imageloader.ImageViewAware;
import com.yjfshop123.live.server.utils.imageloader.LoadedFrom;
import com.yjfshop123.live.ui.animation.XScaleAnimation;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class XAMapPreviewActivity extends BaseSwipeBackActivity {

    private MapView mAMapView;
    private AMap amap;
    private Marker mMarker;

    private double mLatitude = 0;
    private double mLongitude = 0;
    private String mImgUrl;

    private double miLatitude = 0;
    private double miLongitude = 0;
    private String avatarUrl;

    private Context mContext;

    protected Handler mHandler = new Handler(){
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (msg.what == 111){
                amap.animateCamera(CameraUpdateFactory.zoomIn(), 1500, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };

    private class MyThread extends Thread{
        @Override
        public void run() {
//            super.run();
            try {
                Thread.currentThread().sleep(500);
                mHandler.sendEmptyMessage(111);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    protected View rootView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.x_location_preview_activity, null);
        setContentView(rootView);


        mAMapView = findViewById(R.id.location_ext_amap);
        mAMapView.onCreate(savedInstanceState);

        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        mLongitude = getIntent().getDoubleExtra("longitude", 0);
        mImgUrl = getIntent().getStringExtra("imgUrl");

        try {
            double lat = getIntent().getDoubleExtra("lat", 0);
            double lot = getIntent().getDoubleExtra("lot", 0);
            String rob_imgUrl = getIntent().getStringExtra("rob_imgUrl");

            if (TextUtils.isEmpty(rob_imgUrl)){
                miLatitude = Double.parseDouble(UserInfoUtil.getLatitude());
                miLongitude = Double.parseDouble(UserInfoUtil.getLongitude());
                avatarUrl = UserInfoUtil.getAvatar();
            }else {
                miLatitude = lat;
                miLongitude = lot;
                avatarUrl = rob_imgUrl;
            }
        }catch (Exception e){
            miLatitude = Double.parseDouble(UserInfoUtil.getLatitude());
            miLongitude = Double.parseDouble(UserInfoUtil.getLongitude());
            avatarUrl = UserInfoUtil.getAvatar();
        }

        findViewById(R.id.location_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAvatar();
    }

    private DisplayImageOptions options;
    private View iconView_, iconView;
    private ImageView avatar_, avatar;
    private Bitmap bitmap;

    private void loadAvatar(){
        if (options == null){
            options = createDisplayImageOptions(avatarUrl == null ? R.drawable.mm_default_avatar : 0);
        }

        //自己
        iconView_ = LayoutInflater.from(this).inflate(R.layout.xamap_rt_location_marker, null);
        avatar_ = iconView_.findViewById(R.id.xamap_rt_location_marker_civ);

        //他人
        iconView = LayoutInflater.from(this).inflate(R.layout.xamap_rt_location_marker, null);
        avatar = iconView.findViewById(R.id.xamap_rt_location_marker_civ);

        final ImageViewAware imageViewAware_ = new ImageViewAware(avatar_);
        if (avatarUrl == null){
            Drawable drawable = options.getImageForEmptyUri(null);
            try {
                Bitmap bitmap = drawableToBitmap(drawable);
                options.getDisplayer().display(bitmap, imageViewAware_, LoadedFrom.DISC_CACHE);
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        final ImageViewAware imageViewAware = new ImageViewAware(avatar);
        if (mImgUrl == null) {
            Drawable drawable = options.getImageForEmptyUri(null);
            try {
                Bitmap bitmap = drawableToBitmap(drawable);
                options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        if (avatarUrl == null && mImgUrl == null){
            initMap();
            return;
        }

        new AsyncTask<String, File, Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                File file_ = null;
                if (strings[0] != null){
                    try {
                        file_ = Glide.with(mContext)
                                .load(strings[0])
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    }catch (Exception e){
                        file_ = null;
                    }
                }

                File file = null;
                if (strings[1] != null){
                    try {
                        file = Glide.with(mContext)
                                .load(strings[1])
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    }catch (Exception e){
                        file = null;
                    }
                }
                
                publishProgress(file_, file);

                return null;
            }

            @Override
            protected void onProgressUpdate(File... file) {
                super.onProgressUpdate(file);

                if (file[0] == null) {
                    Drawable drawable = options.getImageForEmptyUri(null);
                    try {
                        Bitmap bitmap = drawableToBitmap(drawable);
                        options.getDisplayer().display(bitmap, imageViewAware_, LoadedFrom.DISC_CACHE);
                    } catch (Exception var9) {
                        var9.printStackTrace();
                    }
                } else {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file[0].getAbsolutePath(), opt);
                    opt.inSampleSize = computeSampleSize(opt, 200, 200 * 200);;
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(file[0].getAbsolutePath(), opt);
                    options.getDisplayer().display(bitmap, imageViewAware_, LoadedFrom.DISC_CACHE);
                }

                if (file[1] == null) {
                    Drawable drawable = options.getImageForEmptyUri(null);
                    try {
                        Bitmap bitmap = drawableToBitmap(drawable);
                        options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
                    } catch (Exception var9) {
                        var9.printStackTrace();
                    }
                } else {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file[1].getAbsolutePath(), opt);
                    opt.inSampleSize = computeSampleSize(opt, 200, 200 * 200);;
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(file[1].getAbsolutePath(), opt);
                    options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
                }

                initMap();
            }

            @Override
            protected void onPostExecute(Void void_) {
                super.onPostExecute(void_);
            }
        }.execute(avatarUrl, mImgUrl);


        /*File file = ImageLoader.getInstance().getDiskCache().get(avatarUrl);
        try{
            if (file == null && !TextUtils.isEmpty(avatarUrl)) {
                ImageLoader.getInstance().displayImage(avatarUrl, avatar_, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String var1, View var2) {
                    }

                    @Override
                    public void onLoadingFailed(String var1, View var2, FailReason var3) {
                        initMap();

                    }

                    @Override
                    public void onLoadingComplete(String var1, View var2, Bitmap bitmap) {
                        initMap();
                    }

                    @Override
                    public void onLoadingCancelled(String var1, View var2) {
                        initMap();
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String var1, View var2, int var3, int var4) {

                    }
                });
            }else {
                initMap();
            }
        }catch (Exception e){
            initMap();
        }*/
    }

    private void initMap() {
        amap = mAMapView.getMap();
        amap.setMyLocationEnabled(false);
        amap.getUiSettings().setTiltGesturesEnabled(false);
        amap.getUiSettings().setZoomControlsEnabled(false);
        amap.getUiSettings().setMyLocationButtonEnabled(false);

//        setAvatar(avatar_, avatarUrl);
        MarkerOptions markerOptionsMi = new MarkerOptions().
                anchor(0.5F, 0.5F).
                icon(BitmapDescriptorFactory.fromView(iconView_)).
                position(new LatLng(miLatitude, miLongitude)).
                draggable(false);
        amap.addMarker(markerOptionsMi);

//        setAvatar(avatar, mImgUrl);

        MarkerOptions markerOptions = new MarkerOptions().
                anchor(0.5F, 0.5F).
                icon(BitmapDescriptorFactory.fromView(iconView)).
                position(new LatLng(mLatitude, mLongitude)).
//                title(poi).
//                snippet(lat + "," + lng).
        draggable(false);

        mMarker = amap.addMarker(markerOptions);
//        amap.setInfoWindowAdapter(new AmapInfoWindowAdapter(this));
        mMarker.showInfoWindow();
        amap.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(new LatLng(mLatitude, mLongitude)).zoom(16.0F).bearing(0.0F).build()));
        DD();

        new MyThread().start();
    }

    private void DD(){
        Animation markerAnimation_1 = new XScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f);
        markerAnimation_1.setDuration(1000);
        mMarker.setAnimation(markerAnimation_1);
        mMarker.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {}
            @Override
            public void onAnimationEnd() {
                XX();
            }
        });
        mMarker.startAnimation();
    }

    private void XX(){
        Animation markerAnimation_2 = new XScaleAnimation(1.2f, 0.8f, 1.2f, 0.8f);
        markerAnimation_2.setDuration(1000);
        mMarker.setAnimation(markerAnimation_2);
        mMarker.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {}
            @Override
            public void onAnimationEnd() {
                DD();
            }
        });
        mMarker.startAnimation();
    }

    /*private void setAvatar(ImageView imageView, String url) {
        ImageViewAware imageViewAware = new ImageViewAware(imageView);
        if (url == null) {
            Drawable drawable = options.getImageForEmptyUri((Resources)null);
            try {
                Bitmap bitmap = this.drawableToBitmap(drawable);
                options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        } else {
            File file = ImageLoader.getInstance().getDiskCache().get(url);
            if (file == null && !TextUtils.isEmpty(url)) {
                ImageLoader.getInstance().displayImage(url, imageViewAware, options, null, null);
            } else {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                    opt.inSampleSize = computeSampleSize(opt, 200, 200 * 200);;
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                    options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
            }
        }
    }*/

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private DisplayImageOptions createDisplayImageOptions(int defaultResId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        if (defaultResId != 0) {
            Drawable defaultDrawable = this.getResources().getDrawable(defaultResId);
            builder.showImageOnLoading(defaultDrawable);
            builder.showImageForEmptyUri(defaultDrawable);
            builder.showImageOnFail(defaultDrawable);
        }

        builder.displayer(new CircleBitmapDisplayer());
        DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        @SuppressLint("WrongConstant")
        Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    protected void onDestroy() {
        mAMapView.onDestroy();
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        mAMapView.onResume();
    }

    protected void onPause() {
        super.onPause();
        mAMapView.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAMapView.onSaveInstanceState(outState);
    }
}

