package com.yjfshop123.live.live.live.common.widget.gift.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.MediaController;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveEnterRoomBean;
import com.yjfshop123.live.live.live.common.widget.gift.utils.GifCacheUtil;
import com.yjfshop123.live.live.live.common.widget.gift.utils.LiveTextRender;
import com.yjfshop123.live.net.utils.MD5;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.opensource.svgaplayer.utils.SVGARect;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 观众进场动画效果
 */
public class LiveEnterRoomAnimPresenter {

    private Context mContext;
    private View mBg;
    private View mUserGroup;
    private CircleImageView mAvatar;
    private TextView mName;
    private View mStar;
    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;
    private MediaController mMediaController;//koral--/android-gif-drawable 这个库用来播放gif动画的
    private ObjectAnimator mBgAnimator1;
    private ObjectAnimator mBgAnimator2;
    private ObjectAnimator mBgAnimator3;
    private ObjectAnimator mUserAnimator1;
    private ObjectAnimator mUserAnimator2;
    private ObjectAnimator mUserAnimator3;
    private Animation mStarAnim;
    private int mDp500;
    private boolean mIsAnimating;//是否在执行动画
    private ConcurrentLinkedQueue<LiveEnterRoomBean> mQueue;
    private Handler mHandler;
    private int mScreenWidth;
    private CommonCallback<File> mDownloadGifCallback;
    private boolean mShowGif;
    private boolean mEnd;

    private View mEnterTipGroup;
    private TextView mEnterTip;
    private ObjectAnimator mGifGiftTipShowAnimator;
    private ObjectAnimator mGifGiftTipHideAnimator;
    private static final int WHAT_ANIM = -2;

    private SVGAImageView mSVGAImageView;
    private SVGAParser mSVGAParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private long mSvgaPlayTime;
    private Map<String, SoftReference<SVGAVideoEntity>> mSVGAMap;

    public LiveEnterRoomAnimPresenter(Context context, View root) {
        mContext = context;
        mBg = root.findViewById(R.id.jg_bg);
        mUserGroup = root.findViewById(R.id.jg_user);
        mAvatar = root.findViewById(R.id.jg_avatar);
        mName = (TextView) root.findViewById(R.id.jg_name);
        mStar = root.findViewById(R.id.star);
        mGifImageView = (GifImageView) root.findViewById(R.id.enter_room_gif);

        mSVGAImageView = (SVGAImageView)root.findViewById(R.id.gift_room_svga);
        mSVGAImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                long diffTime = 4000 - (System.currentTimeMillis() - mSvgaPlayTime);
                if (diffTime < 0) {
                    diffTime = 0;
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, diffTime);
                }
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {

            }
        });

        mDp500 = CommonUtils.dip2px(context,500);
        mQueue = new ConcurrentLinkedQueue<>();
        Interpolator interpolator1 = new AccelerateDecelerateInterpolator();
        Interpolator interpolator2 = new LinearInterpolator();
        Interpolator interpolator3 = new AccelerateInterpolator();
        mBgAnimator1 = ObjectAnimator.ofFloat(mBg, "translationX", CommonUtils.dip2px(context,70));
        mBgAnimator1.setDuration(1000);
        mBgAnimator1.setInterpolator(interpolator1);

        mBgAnimator2 = ObjectAnimator.ofFloat(mBg, "translationX", 0);
        mBgAnimator2.setDuration(700);
        mBgAnimator2.setInterpolator(interpolator2);

        mBgAnimator3 = ObjectAnimator.ofFloat(mBg, "translationX", -mDp500);
        mBgAnimator3.setDuration(300);
        mBgAnimator3.setInterpolator(interpolator3);

        mUserAnimator1 = ObjectAnimator.ofFloat(mUserGroup, "translationX", CommonUtils.dip2px(context,70));
        mUserAnimator1.setDuration(1000);
        mUserAnimator1.setInterpolator(interpolator1);
        mUserAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBgAnimator2.start();
                mUserAnimator2.start();
            }
        });

        mUserAnimator2 = ObjectAnimator.ofFloat(mUserGroup, "translationX", 0);
        mUserAnimator2.setDuration(700);
        mUserAnimator2.setInterpolator(interpolator2);
        mUserAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mStar.startAnimation(mStarAnim);
            }
        });

        mUserAnimator3 = ObjectAnimator.ofFloat(mUserGroup, "translationX", mDp500);
        mUserAnimator3.setDuration(450);
        mUserAnimator3.setInterpolator(interpolator3);
        mUserAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBg.setTranslationX(mDp500);
                mUserGroup.setTranslationX(-mDp500);
                if (!mShowGif) {
                    getNextEnterRoom();
                }
            }
        });

        mStarAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mStarAnim.setDuration(1500);
        mStarAnim.setInterpolator(interpolator2);
        mStarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBgAnimator3.start();
                mUserAnimator3.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT_ANIM) {
                    mGifGiftTipHideAnimator.setFloatValues(0, -mDp500);
                    mGifGiftTipHideAnimator.start();
                } else {
                    mShowGif = false;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mGifImageView != null) {
                        mGifImageView.setImageDrawable(null);
                    }
                    if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
                        mGifDrawable.stop();
                        mGifDrawable.recycle();
                    }
                    getNextEnterRoom();
                }
            }
        };
        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
                    playGif(file);
                }
            }
        };

        //
        mEnterTipGroup = root.findViewById(R.id.enter_tip_group);
        mEnterTip = (TextView) root.findViewById(R.id.enter_tip);
        mGifGiftTipShowAnimator = ObjectAnimator.ofFloat(mEnterTipGroup, "translationX", -mDp500, 0);
        mGifGiftTipShowAnimator.setDuration(800);
        mGifGiftTipShowAnimator.setInterpolator(new LinearInterpolator());
        mGifGiftTipShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_ANIM, 1500);
                }
            }
        });
        mGifGiftTipHideAnimator = ObjectAnimator.ofFloat(mEnterTipGroup, "translationX", 0);
        mGifGiftTipHideAnimator.setDuration(800);
        mGifGiftTipHideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mGifGiftTipHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mEnterTipGroup.setAlpha(1 - animation.getAnimatedFraction());
            }
        });
        //
    }

    private void getNextEnterRoom() {
        mIsAnimating = false;
        if (mQueue == null) {
            return;
        }
        LiveEnterRoomBean bean = mQueue.poll();
        if (bean == null) {
            mIsAnimating = false;
        } else {
            startAnim(bean);
        }
    }


    public void enterRoom(LiveEnterRoomBean bean) {
        if (mIsAnimating) {
            mQueue.offer(bean);
        } else {
            startAnim(bean);
        }
    }

    private boolean isSVGA = false;
    private String mMount;
    private String mUserName;
    private void startAnim(LiveEnterRoomBean bean) {
        if (bean != null) {
            mIsAnimating = true;
            boolean needAnim = false;
            mMount = bean.getMount();//坐骑
            if (!TextUtils.isEmpty(mMount)){//有坐骑且为VIP 坐骑进场
                needAnim = true;
                mShowGif = true;
                mUserName = bean.getUserName();
                if (mMount.contains(".svga")){
                    isSVGA = true;
                }else {
                    isSVGA = false;
                }
                GifCacheUtil.getFile(CommonUtils.getUrl(mMount), mDownloadGifCallback);

                /*if (raw_id != 0){
                    needAnim = true;
                    mShowGif = true;
                    mWordText.setText(bean.getUserName() + " 加入直播间");
                    playGif(mContext.getResources().openRawResourceFd(raw_id));
                }*/
            }else {
                if(!isDestroy((Activity)mContext)){
                    needAnim = true;
                    Glide.with(mContext)
                            .load(bean.getAvatar())
                            .into(mAvatar);
                    LiveTextRender.renderEnterRoom(mName, mContext, bean.isVip(), bean.isGuard(), bean.getUserName(), bean.getUser_level(), bean.getIdentityType());
                    mBgAnimator1.start();
                    mUserAnimator1.start();
                }
            }

            if (!needAnim) {
                getNextEnterRoom();
            }
        }else {
            getNextEnterRoom();
        }
    }

    private static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 调整mGifImageView的大小
     */
    private void resizeGifImageView(Drawable drawable) {
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        ViewGroup.LayoutParams params = mGifImageView.getLayoutParams();
        params.height = (int) (mScreenWidth * h / w);
        mGifImageView.setLayoutParams(params);
    }

    /**
     * 播放gif或svga
     */
    private void playGif(File file) {
        if (mEnd) {
            return;
        }

        if (mGifGiftTipShowAnimator != null){
            mEnterTip.setText("欢迎 " + mUserName + " 加入直播间");
            mEnterTipGroup.setAlpha(1f);
            mGifGiftTipShowAnimator.start();
        }

        if (isSVGA){
            SVGAVideoEntity svgaVideoEntity = null;
            if (mSVGAMap != null) {
                SoftReference<SVGAVideoEntity> reference = mSVGAMap.get(MD5.encrypt(mMount));
                if (reference != null) {
                    svgaVideoEntity = reference.get();
                }
            }
            if (svgaVideoEntity != null) {
                playSVGA(svgaVideoEntity);
            } else {
                decodeSvga(file);
            }
        }else {
            try {
                mGifDrawable = new GifDrawable(file);
                mGifDrawable.setLoopCount(1);
                resizeGifImageView(mGifDrawable);
                mGifImageView.setImageDrawable(mGifDrawable);
                if (mMediaController == null) {
                    mMediaController = new MediaController(mContext);
                    mMediaController.setVisibility(View.GONE);
                }
                mMediaController.setMediaPlayer((GifDrawable) mGifImageView.getDrawable());
                mMediaController.setAnchorView(mGifImageView);
                int duration = mGifDrawable.getDuration();
                mMediaController.show(duration);
                if (duration < 2500) {
                    duration = 2500;
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, duration);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 2500);
                }
            }
        }
    }

    /**
     * 播放本地gif
     */
    private void playGif(AssetFileDescriptor afd) {
        if (mEnd) {
            return;
        }
        try {
            mGifDrawable = new GifDrawable(afd);
            mGifDrawable.setLoopCount(1);
            resizeGifImageView(mGifDrawable);
            mGifImageView.setImageDrawable(mGifDrawable);
            if (mMediaController == null) {
                mMediaController = new MediaController(mContext);
                mMediaController.setVisibility(View.GONE);
            }
            mMediaController.setMediaPlayer((GifDrawable) mGifImageView.getDrawable());
            mMediaController.setAnchorView(mGifImageView);
            int duration = mGifDrawable.getDuration();
            mMediaController.show(duration);
            if (duration < 2500) {
                duration = 2500;
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 2500);
            }
        }
    }

    /**
     * 播放svga
     */
    private void playSVGA(SVGAVideoEntity svgaVideoEntity) {
        if (mSVGAImageView != null) {
            SVGARect rect = svgaVideoEntity.getVideoSize();
            resizeSvgaImageView(rect.getWidth(), rect.getHeight());
            //SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
            //mSVGAImageView.setImageDrawable(drawable);
            mSVGAImageView.setVideoItem(svgaVideoEntity);
            mSvgaPlayTime = System.currentTimeMillis();
            mSVGAImageView.startAnimation();
        }
    }

    /**
     * 播放svga
     */
    private void decodeSvga(File file) {
        if (mSVGAParser == null) {
            mSVGAParser = new SVGAParser(mContext);
        }
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                    if (mSVGAMap == null) {
                        mSVGAMap = new HashMap<>();
                    }
                    if (mMount != null) {
                        mSVGAMap.put(MD5.encrypt(mMount), new SoftReference<>(svgaVideoEntity));
                    }
                    playSVGA(svgaVideoEntity);
                }

                @Override
                public void onError() {
                    mShowGif = false;
                }
            };
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            mSVGAParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true);
        } catch (Exception e) {
            e.printStackTrace();
            mShowGif = false;
        }
    }

    /**
     * 调整mSVGAImageView的大小
     */
    private void resizeSvgaImageView(double w, double h) {
        ViewGroup.LayoutParams params = mSVGAImageView.getLayoutParams();
        params.height = (int) (mSVGAImageView.getWidth() * h / w);
        mSVGAImageView.setLayoutParams(params);
    }

    public void cancelAnim() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mGifGiftTipShowAnimator != null) {
            mGifGiftTipShowAnimator.cancel();
        }
        if (mGifGiftTipHideAnimator != null) {
            mGifGiftTipHideAnimator.cancel();
        }
        if (mBgAnimator1 != null) {
            mBgAnimator1.cancel();
        }
        if (mBgAnimator2 != null) {
            mBgAnimator2.cancel();
        }
        if (mBgAnimator3 != null) {
            mBgAnimator3.cancel();
        }
        if (mUserAnimator1 != null) {
            mUserAnimator1.cancel();
        }
        if (mUserAnimator2 != null) {
            mUserAnimator2.cancel();
        }
        if (mUserAnimator3 != null) {
            mUserAnimator3.cancel();
        }
        if (mStar != null) {
            mStar.clearAnimation();
        }
        if (mQueue != null) {
            mQueue.clear();
        }
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.setAnchorView(null);
        }
        if (mGifImageView != null) {
            mGifImageView.setImageDrawable(null);
        }
        if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
            mGifDrawable.stop();
            mGifDrawable.recycle();
            mGifDrawable = null;
        }
        if (mSVGAImageView != null) {
            mSVGAImageView.stopAnimation(true);
        }
        if (mSVGAMap != null) {
            mSVGAMap.clear();
        }
        mIsAnimating = false;
    }

    public void resetAnimView() {
        if (mBg != null) {
            mBg.setTranslationX(mDp500);
        }
        if (mUserGroup != null) {
            mUserGroup.setTranslationX(-mDp500);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mEnterTipGroup != null && mEnterTipGroup.getTranslationX() != -mDp500) {
            mEnterTipGroup.setTranslationX(-mDp500);
        }
    }

    public void release() {
        mEnd = true;
        cancelAnim();
        if (mSVGAImageView != null) {
            mSVGAImageView.setCallback(null);
        }
        mSVGAImageView = null;
        mDownloadGifCallback = null;
        mHandler = null;
    }
}
