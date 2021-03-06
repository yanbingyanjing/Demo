package com.yjfshop123.live.live.live.common.widget.gift.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.MediaController;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.utils.GifCacheUtil;
import com.yjfshop123.live.live.live.common.widget.gift.view.LiveGiftViewHolder;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.ImgLoader;
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

public class LiveGiftAnimPresenter {

    private Context mContext;
    private ViewGroup mParent2;
    private SVGAImageView mSVGAImageView;
    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;
    private View mGifGiftTipGroup;
    private TextView mGifGiftTip;
    private ObjectAnimator mGifGiftTipShowAnimator;
    private ObjectAnimator mGifGiftTipHideAnimator;
    private LiveGiftViewHolder[] mLiveGiftViewHolders;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mQueue;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mGifQueue;
    private Map<String, LiveReceiveGiftBean> mMap;
    private Handler mHandler;
    private MediaController mMediaController;//koral--/android-gif-drawable ?????????????????????gif?????????
    private static final int WHAT_GIF = -1;
    private static final int WHAT_ANIM = -2;
    private boolean mShowGif;
    private CommonCallback<File> mDownloadGifCallback;
    private int mDp10;
    private int mDp500;
    private LiveReceiveGiftBean mTempGifGiftBean;
    private String mSendString;
    private SVGAParser mSVGAParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private long mSvgaPlayTime;
    private Map<String, SoftReference<SVGAVideoEntity>> mSVGAMap;


    public LiveGiftAnimPresenter(Context context, View v, GifImageView gifImageView, SVGAImageView svgaImageView) {
        mContext = context;
        mParent2 = (ViewGroup) v.findViewById(R.id.gift_group_1);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
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
                    mHandler.sendEmptyMessageDelayed(WHAT_GIF, diffTime);
                }
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {

            }
        });
        mGifGiftTipGroup = v.findViewById(R.id.gif_gift_tip_group);
        mGifGiftTip = (TextView) v.findViewById(R.id.gif_gift_tip);
        mDp500 = CommonUtils.dip2px(mContext, 500);
        mGifGiftTipShowAnimator = ObjectAnimator.ofFloat(mGifGiftTipGroup, "translationX", mDp500, 0);
        mGifGiftTipShowAnimator.setDuration(1000);
        mGifGiftTipShowAnimator.setInterpolator(new LinearInterpolator());
        mGifGiftTipShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_ANIM, 2000);
                }
            }
        });
        mDp10 = CommonUtils.dip2px(mContext, 10);
        mGifGiftTipHideAnimator = ObjectAnimator.ofFloat(mGifGiftTipGroup, "translationX", 0);
        mGifGiftTipHideAnimator.setDuration(800);
        mGifGiftTipHideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mGifGiftTipHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGifGiftTipGroup.setAlpha(1 - animation.getAnimatedFraction());
            }
        });
        mSendString = mContext.getString(R.string.live_send_gift_3);
        mLiveGiftViewHolders = new LiveGiftViewHolder[2];
        mLiveGiftViewHolders[0] = new LiveGiftViewHolder(context, (ViewGroup) v.findViewById(R.id.gift_group_2));
        mLiveGiftViewHolders[0].addToParent();
        mQueue = new ConcurrentLinkedQueue<>();
        mGifQueue = new ConcurrentLinkedQueue<>();
        mMap = new HashMap<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT_GIF) {
                    mShowGif = false;
                    if (mGifImageView != null) {
                        mGifImageView.setImageDrawable(null);
                    }
                    if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
                        mGifDrawable.stop();
                        mGifDrawable.recycle();
                    }
                    LiveReceiveGiftBean bean = mGifQueue.poll();
                    if (bean != null) {
                        showGifGift(bean);
                    }
                } else if (msg.what == WHAT_ANIM) {
                    mGifGiftTipHideAnimator.setFloatValues(0, -mDp10 - mGifGiftTipGroup.getWidth());
                    mGifGiftTipHideAnimator.start();
                } else {
                    LiveGiftViewHolder vh = mLiveGiftViewHolders[msg.what];
                    if (vh != null) {
                        LiveReceiveGiftBean bean = mQueue.poll();
                        if (bean != null) {
                            mMap.remove(bean.getKey());
                            vh.show(bean, false);
                            resetTimeCountDown(msg.what);
                        } else {
                            vh.hide();
                        }
                    }
                }
            }
        };
        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
                    playHaoHuaGift(file);
                } else {
                    mShowGif = false;
                }
            }
        };
    }

    public void showGiftAnim(LiveReceiveGiftBean bean) {
        //1:?????? 2:gif?????? 3:svga??????
        if (bean.getLiveReceiveGiftBeanItem().getStyle() == 2 || bean.getLiveReceiveGiftBeanItem().getStyle() == 3) {
            showGifGift(bean);
        } else {
            showNormalGift(bean);
        }
    }

    /**
     * ??????gif??????
     */
    private void showGifGift(LiveReceiveGiftBean bean) {
        String url = bean.getLiveReceiveGiftBeanItem().getGifUrl();
        NLog.e("gif??????----->" + bean.getLiveReceiveGiftBeanItem().getGiftName() + "----->" + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mShowGif) {
            if (mGifQueue != null) {
                mGifQueue.offer(bean);
            }
        } else {
            mShowGif = true;
            mTempGifGiftBean = bean;
            if (!url.endsWith(".gif") && !url.endsWith(".svga")) {
                ImgLoader.displayDrawable(url, new ImgLoader.DrawableCallback() {
                    @Override
                    public void onLoadSuccess(Drawable drawable) {
                        resizeGifImageView(drawable);
                        mGifImageView.setImageDrawable(drawable);
                        mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getLiveReceiveGiftBeanItem().getGiftName());
                        mGifGiftTipGroup.setAlpha(1f);
                        mGifGiftTipShowAnimator.start();
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_GIF, 4000);
                        }
                    }

                    @Override
                    public void onLoadFailed() {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(WHAT_GIF);
                        }
                    }
                });
            } else {
                String gifPath = bean.getGifPath();
                if (TextUtils.isEmpty(gifPath)){
                    GifCacheUtil.getFile(url, mDownloadGifCallback);
                }else {
                    File file = new File(gifPath);
                    if (file != null) {
                        playHaoHuaGift(file);
                    } else {
                        mShowGif = false;
                    }
                }
            }
        }
    }

    /**
     * ??????mGifImageView?????????
     */
    private void resizeGifImageView(Drawable drawable) {
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        ViewGroup.LayoutParams params = mGifImageView.getLayoutParams();
        params.height = (int) (mGifImageView.getWidth() * h / w);
        mGifImageView.setLayoutParams(params);
    }

    /**
     * ??????mSVGAImageView?????????
     */
    private void resizeSvgaImageView(double w, double h) {
        ViewGroup.LayoutParams params = mSVGAImageView.getLayoutParams();
        params.height = (int) (mSVGAImageView.getWidth() * h / w);
        mSVGAImageView.setLayoutParams(params);
    }

    /**
     * ??????????????????
     */
    private void playHaoHuaGift(File file) {
        if (mTempGifGiftBean.getLiveReceiveGiftBeanItem().getStyle() == 2) {//?????????????????? 2:gif?????? 3:svga??????
            if (mTempGifGiftBean != null) {
                mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getLiveReceiveGiftBeanItem().getGiftName());
                mGifGiftTipGroup.setAlpha(1f);
                mGifGiftTipShowAnimator.start();
            }
            playGift(file);
        } else {
            SVGAVideoEntity svgaVideoEntity = null;
            if (mSVGAMap != null) {
                SoftReference<SVGAVideoEntity> reference = mSVGAMap.get(mTempGifGiftBean.getLiveReceiveGiftBeanItem().getGiftId());
                if (reference != null) {
                    svgaVideoEntity = reference.get();
                }
            }
            if (svgaVideoEntity != null) {
                playSVGA(svgaVideoEntity);
            } else {
                decodeSvga(file);
            }
        }
    }

    /**
     * ??????gif
     */
    private void playGift(File file) {
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
            if (duration < 4000) {
                duration = 4000;
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_GIF, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mShowGif = false;
        }
    }

    /**
     * ??????svga
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
            if (mTempGifGiftBean != null) {
                mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getLiveReceiveGiftBeanItem().getGiftName());
                mGifGiftTipGroup.setAlpha(1f);
                mGifGiftTipShowAnimator.start();
            }
        }
    }

    /**
     * ??????svga
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
                    if (mTempGifGiftBean != null) {
                        mSVGAMap.put(mTempGifGiftBean.getLiveReceiveGiftBeanItem().getGiftId(), new SoftReference<>(svgaVideoEntity));
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
     * ??????????????????
     */
    private void showNormalGift(LiveReceiveGiftBean bean) {
        if (mLiveGiftViewHolders[0].isIdle()) {
            if (mLiveGiftViewHolders[1] != null && mLiveGiftViewHolders[1].isSameGift(bean)) {
                mLiveGiftViewHolders[1].show(bean, true);
                resetTimeCountDown(1);
                return;
            }
            mLiveGiftViewHolders[0].show(bean, false);
            resetTimeCountDown(0);
            return;
        }
        if (mLiveGiftViewHolders[0].isSameGift(bean)) {
            mLiveGiftViewHolders[0].show(bean, true);
            resetTimeCountDown(0);
            return;
        }
        if (mLiveGiftViewHolders[1] == null) {
            mLiveGiftViewHolders[1] = new LiveGiftViewHolder(mContext, mParent2);
            mLiveGiftViewHolders[1].addToParent();
        }
        if (mLiveGiftViewHolders[1].isIdle()) {
            mLiveGiftViewHolders[1].show(bean, false);
            resetTimeCountDown(1);
            return;
        }
        if (mLiveGiftViewHolders[1].isSameGift(bean)) {
            mLiveGiftViewHolders[1].show(bean, true);
            resetTimeCountDown(1);
            return;
        }
        String key = bean.getKey();
        if (!mMap.containsKey(key)) {
            mMap.put(key, bean);
            mQueue.offer(bean);
        } else {
            LiveReceiveGiftBean bean1 = mMap.get(key);
            bean1.setLianCount(bean1.getLianCount() + 1);
        }
    }

    private void resetTimeCountDown(int index) {
        if (mHandler != null) {
            mHandler.removeMessages(index);
            mHandler.sendEmptyMessageDelayed(index, 5000);
        }
    }


    public void cancelAllAnim() {
        clearAnim();
        mShowGif = false;
        cancelNormalGiftAnim();
        if (mGifGiftTipGroup != null && mGifGiftTipGroup.getTranslationX() != mDp500) {
            mGifGiftTipGroup.setTranslationX(mDp500);
        }
    }

    private void cancelNormalGiftAnim() {
        if (mLiveGiftViewHolders[0] != null) {
            mLiveGiftViewHolders[0].cancelAnimAndHide();
        }
        if (mLiveGiftViewHolders[1] != null) {
            mLiveGiftViewHolders[1].cancelAnimAndHide();
        }
    }


    private void clearAnim() {
//        CommonHttpUtil.cancel(CommonHttpConsts.DOWNLOAD_GIF);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mGifGiftTipShowAnimator != null) {
            mGifGiftTipShowAnimator.cancel();
        }
        if (mGifGiftTipHideAnimator != null) {
            mGifGiftTipHideAnimator.cancel();
        }
        if (mQueue != null) {
            mQueue.clear();
        }
        if (mGifQueue != null) {
            mGifQueue.clear();
        }
        if (mMap != null) {
            mMap.clear();
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
    }

    public void release() {
        clearAnim();
        if (mLiveGiftViewHolders[0] != null) {
            mLiveGiftViewHolders[0].release();
        }
        if (mLiveGiftViewHolders[1] != null) {
            mLiveGiftViewHolders[1].release();
        }
        if (mSVGAImageView != null) {
            mSVGAImageView.setCallback(null);
        }
        mSVGAImageView = null;
        mDownloadGifCallback = null;
        mHandler = null;
    }

}

