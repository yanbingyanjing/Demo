package com.yjfshop123.live.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.ReceiveGiftsResponse;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.net.utils.MD5;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.utils.imageloader.CircleBitmapDisplayer;
import com.yjfshop123.live.server.utils.imageloader.DisplayImageOptions;
import com.yjfshop123.live.server.utils.imageloader.ImageViewAware;
import com.yjfshop123.live.server.utils.imageloader.LoadedFrom;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.activity.XAMapPreviewActivity;
import com.yjfshop123.live.ui.adapter.MMAdapter;
import com.yjfshop123.live.ui.animation.XScaleAnimation;
import com.yjfshop123.live.ui.widget.VoisePlayingIcon;
import android.support.v7.widget.RecyclerView;
import com.yjfshop123.live.utils.MediaManager;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.update.all.download.DownloadManager;
import com.yjfshop123.live.utils.update.all.download.OnDownloadListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class MMFragment extends BaseFragment implements MediaPlayer.OnCompletionListener, PoiSearch.OnPoiSearchListener{

    private UserHomeResponse mResponse;
    private String user_id;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    private View vHead;

    private SelectableRoundedImageView mmCiv;
    private TextView voiceTv;
    private VoisePlayingIcon voisePlayingIcon;
    private TextView voiceCostTv, videoCostTv;

    private int mDuration = 0;
    private Drawable mDrawable;

    private MMAdapter mmAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private static List<Integer> colors;
    static {
        colors = Arrays.asList(
                R.color.mm_color_1,
                R.color.mm_color_2,
                R.color.mm_color_3,
                R.color.mm_color_4,
                R.color.mm_color_5,
                R.color.mm_color_6,
                R.color.mm_color_7,
                R.color.mm_color_8
        );
    }

    private Bundle savedInstanceState;
    private MapView mAMapView;
    private Button mmDetailsDD;
    private double mLatitude = 0;
    private double mlongitude = 0;
    private String mImgUrl;
    private AMap amap;
    private Marker mMarker;
    private LinearLayout mmMapLl;
    private TextView  city;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    public void setResponse(UserHomeResponse response, String user_id){
        this.mResponse = response;
        this.user_id = user_id;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_mm;
    }

    @Override
    protected void initAction() {
        vHead = View.inflate(mContext, R.layout.mm_headerview, null);
        mmMapLl = vHead.findViewById(R.id.mm_map_ll);
        mmCiv = vHead.findViewById(R.id.mm_civ);
        city = vHead.findViewById(R.id.city);
        city.setText(getString(R.string.suozai_city)+mResponse.getUser_info().getProvince_name()+" "+mResponse.getUser_info().getCity_name()+" "+mResponse.getUser_info().getDistrict_name());
        voiceTv = vHead.findViewById(R.id.mm_details_voice_tv);
        voisePlayingIcon = vHead.findViewById(R.id.voise_playint_icon);
        voiceCostTv = vHead.findViewById(R.id.mm_details_voice_cost_tv);
        videoCostTv = vHead.findViewById(R.id.mm_details_video_cost_tv);

        mAMapView = vHead.findViewById(R.id.mm_details_location_ext_amap);
        mAMapView.onCreate(savedInstanceState);
        mmDetailsDD = vHead.findViewById(R.id.mm_details_dd);

        mDrawable = getResources().getDrawable(R.drawable.chat_video_play_small);
        mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setFocusableInTouchMode(false);

        mmAdapter = new MMAdapter(mContext, 1, vHead);
        shimmerRecycler.setAdapter(mmAdapter);
        mmAdapter.notifyDataSetChanged();
    }
    String cityS;
public void setCity(String cityS){this.cityS=cityS;}

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
        getReceiveGifts();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        finishRefresh();
    }

    private void getReceiveGifts(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", user_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest2("app/gift/getReceiveGifts", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    ReceiveGiftsResponse receiveGiftsResponse = JsonMananger.jsonToBean(result, ReceiveGiftsResponse.class);
                    loadGift(receiveGiftsResponse);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadData() {
        if (mResponse == null){
            return;
        }

        Glide.with(mContext)
                .load(mResponse.getUser_info().getAvatar())
                .into(mmCiv);

        String tags = mResponse.getUser_info().getTags();
        LinearLayout mControlsTab = vHead.findViewById(R.id.mm_details_controls_tab);
        if (!TextUtils.isEmpty(tags)) {
            String[] tags_;
            if (tags.contains(",")) {
                tags_ = tags.split(",");
            } else {
                tags_ = new String[]{tags};
            }
            for (int i = 0; i < tags_.length; i++) {
                View view_tab = View.inflate(mContext, R.layout.tab_item, null);
                TextView tabTv = view_tab.findViewById(R.id.tab_item_id);
                tabTv.setText(tags_[i]);
                GradientDrawable tabTv_GD = (GradientDrawable) tabTv.getBackground();
                int pos = new Random().nextInt(8);
                tabTv_GD.setColor(getResources().getColor(colors.get(pos)));
                mControlsTab.addView(view_tab);
            }
        }else{
            View view_ = View.inflate(mContext, R.layout.mm_hint_txt, null);
            TextView mm_hint_txt_tv = view_.findViewById(R.id.mm_hint_txt_tv);
            mm_hint_txt_tv.setText(getString(R.string.not_tag));
            mControlsTab.addView(view_);
        }

        if (mResponse.getUser_info().getOpen_speech() == 0){
            voiceCostTv.setText(getString(R.string.not_open));
        }else {
            voiceCostTv.setText(mResponse.getUser_info().getSpeech_cost() + getString(R.string.ql_cost));
        }
        if (mResponse.getUser_info().getOpen_video() == 0){
            videoCostTv.setText(getString(R.string.not_open));
        }else {
            videoCostTv.setText(mResponse.getUser_info().getVideo_cost() + getString(R.string.ql_cost));
        }

        String speech_introduction = mResponse.getUser_info().getSpeech_introduction();
        if (!TextUtils.isEmpty(speech_introduction) && speech_introduction.contains("http") &&
                (speech_introduction.contains(".mp4") ||
                        speech_introduction.contains(".mp3") ||
                        speech_introduction.contains(".amr") ||
                        speech_introduction.contains(".ogg") ||
                        speech_introduction.contains(".pcm") ||
                        speech_introduction.contains(".m4a") ||
                        speech_introduction.contains(".aac"))) {
            //有语音
            //http://zhibo005.oss-cn-beijing.aliyuncs.com/zhibo/2018/11/1/8.mp4
            download(speech_introduction);
        } else {
            voiceTv.setText(getString(R.string.ac_detail_discussion_no));
            stopAnim();
        }

        //map
        if (mResponse.getUser_info().getOpen_position() == 0){
            mmMapLl.setVisibility(View.GONE);
        }else {
            mmMapLl.setVisibility(View.GONE);
            mLatitude = mResponse.getUser_info().getLatitude();
            mlongitude = mResponse.getUser_info().getLongitude();
            mImgUrl = mResponse.getUser_info().getAvatar();
            displayImage_();
            mmDetailsDD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, XAMapPreviewActivity.class);
                    intent.putExtra("latitude", mLatitude);
                    intent.putExtra("longitude", mlongitude);
                    intent.putExtra("imgUrl", mImgUrl);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            });
        }
    }

    private void loadGift(ReceiveGiftsResponse receiveGiftsResponse){
        if (receiveGiftsResponse.getCode() == 1) {
            if (receiveGiftsResponse.getData().size() > 0) {
                LinearLayout detailsControlsGift = vHead.findViewById(R.id.mm_details_controls_gift);
                for (int i = 0; i < receiveGiftsResponse.getData().size(); i++) {
                    View view_ = View.inflate(mContext, R.layout.mm_details_gift_item, null);
                    SelectableRoundedImageView giftIv = view_.findViewById(R.id.mm_details_gift_iv);
                    TextView giftTv = view_.findViewById(R.id.mm_details_gift_tv);
                    Glide.with(mContext)
                            .load(receiveGiftsResponse.getData().get(i).getIcon_img())
                            .into(giftIv);
                    giftTv.setText("x" + receiveGiftsResponse.getData().get(i).getTotal());
                    detailsControlsGift.addView(view_);
                }
            }
        }
    }

    private static DownloadManager mDownloadManager;
    private void download(String url){
        String fileName = MD5.encrypt(url);
        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.startDownload(url, fileName, new
                OnDownloadListener() {

                    @Override
                    public void onException() {
                    }

                    @Override
                    public void onProgress(int progress) {
                    }

                    @Override
                    public void onSuccess() {
                        //NLog.e("TAGTAG", filePath + "0000000" + url);
                        int duration = MediaManager.playSound(mContext, mDownloadManager.getDownloadFilePath(), MMFragment.this);
                        mDuration = duration / 1000;
                        voiceTv.setCompoundDrawables(mDrawable,null,null,null);
                        voiceTv.setText(/*"▶" +*/ mDuration + "'");
                        voiceTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (MediaManager.isPlaying()) {
                                    MediaManager.pause();
                                    voiceTv.setVisibility(View.VISIBLE);
                                    stopAnim();
                                    voiceTv.setCompoundDrawables(mDrawable,null,null,null);
                                    voiceTv.setText(/*"▶" +*/ mDuration + "'");
                                } else {
                                    //继续
                                    MediaManager.resume();
                                    voiceTv.setCompoundDrawables(null,null,null,null);
                                    voiceTv.setText("");
                                    startAnim();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                    }

                    @Override
                    public void onPaused() {
                    }

                    @Override
                    public void onCanceled() {
                    }
                });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //重置
        //MediaManager.stop();
        //MediaManager.reset();
        voiceTv.setVisibility(View.VISIBLE);
        stopAnim();
        voiceTv.setCompoundDrawables(mDrawable,null,null,null);
        voiceTv.setText(/*"▶" + */mDuration + "'");
    }

    @Override
    public void onDestroy() {
        mAMapView.onDestroy();
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
        //释放资源
        MediaManager.release();
    }

    @Override
    public void onPause() {
        mAMapView.onPause();
        super.onPause();
        //暂停
        MediaManager.pause();
        if (mDuration != 0) {
            voiceTv.setVisibility(View.VISIBLE);
            stopAnim();
            voiceTv.setCompoundDrawables(mDrawable,null,null,null);
            voiceTv.setText(/*"▶" + */mDuration + "'");
        }
    }

    private void startAnim() {
        /*voiceLoad.setVisibility(View.VISIBLE);
        voiceLoad.show();*/
        // or avi.smoothToShow();
        voisePlayingIcon.setVisibility(View.VISIBLE);
        voisePlayingIcon.start();
    }

    private void stopAnim() {
        /*voiceLoad.hide();
        voiceLoad.setVisibility(View.GONE);*/
        // or avi.smoothToHide();
        voisePlayingIcon.stop();
        voisePlayingIcon.setVisibility(View.GONE);
    }


    public void onResume() {
        super.onResume();
        mAMapView.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAMapView.onSaveInstanceState(outState);
    }

    private DisplayImageOptions options;
    private View iconView;
    private ImageView avatar;
    private Bitmap bitmap;

    private void displayImage_(){
        if (options == null){
            options = createDisplayImageOptions(mImgUrl == null ? R.drawable.mm_default_avatar : 0);
        }
        iconView = LayoutInflater.from(mContext).inflate(R.layout.xamap_rt_location_marker, null);
        avatar = iconView.findViewById(R.id.xamap_rt_location_marker_civ);

        final ImageViewAware imageViewAware = new ImageViewAware(avatar);
        if (mImgUrl == null) {
            Drawable drawable = options.getImageForEmptyUri(null);
            try {
                Bitmap bitmap = drawableToBitmap(drawable);
                options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
            } catch (Exception var9) {
                var9.printStackTrace();
            }

            initMap_();
        } else {
            new AsyncTask<String, Void, File>(){

                @Override
                protected File doInBackground(String... strings) {
                    File file;
                    try {
                        file = Glide.with(mContext)
                                .load(strings[0])
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    }catch (Exception e){
                        file = null;
                    }
                    return file;
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    if (file == null) {
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
                        BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                        opt.inSampleSize = computeSampleSize(opt, 200, 200 * 200);;
                        opt.inJustDecodeBounds = false;
//                        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//                        opt.inDither = true;
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                        options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
                    }

                    initMap_();
                }
            }.execute(mImgUrl);
        }
    }

    private void initMap_(){
        if (mLatitude == 0) {
            poi();
        } else {
            initMap();
        }
    }

    protected Handler mHandler = new Handler() {
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (msg.what == 111) {
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

    private class MyThread extends Thread {
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

    private void initMap() {
        amap = mAMapView.getMap();
        amap.setMyLocationEnabled(false);
        amap.getUiSettings().setTiltGesturesEnabled(false);
        amap.getUiSettings().setZoomControlsEnabled(false);
        amap.getUiSettings().setMyLocationButtonEnabled(false);

        double lat = mLatitude;
        double lng = mlongitude;

//        setAvatar(avatar, mImgUrl);
        MarkerOptions markerOptions = new MarkerOptions().
                anchor(0.5F, 0.5F).
                icon(BitmapDescriptorFactory.fromView(iconView)).
                position(new LatLng(lat, lng)).
//                title(poi).
//                snippet(lat + "," + lng).
        draggable(false);
        mMarker = amap.addMarker(markerOptions);
//        amap.setInfoWindowAdapter(new AmapInfoWindowAdapter(this));
        mMarker.showInfoWindow();
        amap.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(new LatLng(lat, lng)).zoom(16.0F).bearing(0.0F).build()));
        DD();

        new MyThread().start();
    }

    private void DD() {
        Animation markerAnimation_1 = new XScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f);
        markerAnimation_1.setDuration(1000);
        mMarker.setAnimation(markerAnimation_1);
        mMarker.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                XX();
            }
        });
        mMarker.startAnimation();
    }

    private void XX() {
        Animation markerAnimation_2 = new XScaleAnimation(1.2f, 0.8f, 1.2f, 0.8f);
        markerAnimation_2.setDuration(1000);
        mMarker.setAnimation(markerAnimation_2);
        mMarker.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                DD();
            }
        });
        mMarker.startAnimation();
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

    //################################################################################################
    //机器人主播 获取附近位置给你他
    private int currentPage;
    private LatLonPoint lp;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    private void poi() {
        lp = new LatLonPoint(Double.parseDouble(UserInfoUtil.getLatitude()), Double.parseDouble(UserInfoUtil.getLongitude()));
        String keyWord = "楼";
        currentPage = 5;
        //第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）北京
        query = new PoiSearch.Query(keyWord, "", "");
        query.setPageSize(20);//setting how mang itmes to return;
        query.setPageNum(currentPage);//setup query the first page;

        if (lp != null) {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            //设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            poiSearch.searchPOIAsyn();//asyn search
        }
    }

    private PoiResult poiResult;//the result of the poi
    private List<PoiItem> poiItems;//poi数据

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {
//                是否是同一条,避免多次多点查询的时候混乱
                if (result.getQuery().equals(query)) {
                    poiResult = result;
                    poiItems = poiResult.getPois();
//                   获取poitem数据
//                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                    Random rand = new Random();
                    if (poiItems != null && poiItems.size() > 0) {
                        int la = rand.nextInt(poiItems.size());
                        mLatitude = poiItems.get(la).getLatLonPoint().getLatitude();
                        mlongitude = poiItems.get(la).getLatLonPoint().getLongitude();
                        initMap();
                        //这里是查出的全部信息,这里是你想要的
//                        for (int i = 0; i < poiItems.size(); ++i) {
//                            PoiItem poiItem = poiItems.get(i);
//                            鼎尚香火锅(尚泽大都会店),蜀山区,,合肥市,,,安徽省,0551-65986795;15209838002,31.771299,117.23031
//                            遇见榴芒(榴莲芒果甜品),蜀山区,,合肥市,,,安徽省,,31.771955,117.230335
//                            联东U谷,蜀山区,,合肥市,,,安徽省,,31.771259,117.230342
//                            洛基健身工作室,蜀山区,,合肥市,,,安徽省,0551-62860864;18302148158,31.771241,117.229436

                            /*NLog.e("TAGTAG", "poiItems内容:" + poiItem.getTitle() + "," +
                                            poiItem.getAdName() + "," + poiItem.getBusinessArea() + "," +
                                            poiItem.getCityName() + "," + poiItem.getDirection() + "," +
                                            poiItem.getEmail() + "," + poiItem.getProvinceName() + "," +
                                            poiItem.getTel() + "," + poiItem.getLatLonPoint().getLatitude() + "," +
                                    poiItem.getLatLonPoint().getLongitude());*/
//                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
    //################################################################################################

}
