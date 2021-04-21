package com.yjfshop123.live.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.StringUtil;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.VideoEditActivity;
import com.yjfshop123.live.video.custom.ColorfulProgress;
import com.yjfshop123.live.video.custom.RangeSliderViewContainer;
import com.yjfshop123.live.video.custom.VideoProgressController;
import com.yjfshop123.live.video.custom.VideoProgressView;
import com.tencent.ugc.TXVideoEditConstants;

import java.util.List;

public class VideoEditCutViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ActionListener mActionListener;
    private boolean mShowed;
    private VideoProgressView mVideoProgressView;
    private VideoProgressController mVideoProgressController;
    private ColorfulProgress mColorfulProgress;
    private long mVideoDuration;
    private TextView mStartTime;
    private TextView mEndTime;
    private TextView mTip;
    private View mSpecialGroup;
    private View mBtnSpecialCancel;
    private boolean mTouching;
    private long mCurTime;
    private boolean mSpecialStartMark;

    public VideoEditCutViewHolder(Context context, ViewGroup parentView, long videoDuration) {
        super(context, parentView, videoDuration);
    }

    @Override
    protected void processArguments(Object... args) {
        mVideoDuration = (long) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_edit_cut;
    }

    @Override
    public void init() {
        findViewById(R.id.root).setOnClickListener(this);
        mTip = (TextView) findViewById(R.id.tip);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndTime = (TextView) findViewById(R.id.end_time);
        mSpecialGroup = findViewById(R.id.group_special);
        mBtnSpecialCancel = findViewById(R.id.btn_special_cancel);
        mBtnSpecialCancel.setOnClickListener(this);
        //长按添加特效
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                int action = e.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (mTouching) {//防止多个个其他特效同时按下
                        return false;
                    }
                    mTouching = true;
                    specialDown(v);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    mTouching = false;
                    otherSpecialUp(v);
                }
                return true;
            }
        };
        findViewById(R.id.btn_special_1).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_2).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_3).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_4).setOnTouchListener(onTouchListener);

        //TODO
        /*findViewById(R.id.btn_special_5).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_6).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_7).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_8).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_9).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_10).setOnTouchListener(onTouchListener);
        findViewById(R.id.btn_special_11).setOnTouchListener(onTouchListener);*/

        //TODO 视频倒放
//        mTXVideoEditer.setTXVideoReverseListener(mTxVideoReverseListener);
//        mTXVideoEditer.setReverse(true);

        mVideoProgressView = (VideoProgressView) findViewById(R.id.progress_view);
        List<Bitmap> list = ((VideoEditActivity)mContext).getBitmapList();
        if (list != null) {
            mVideoProgressView.addBitmapList(list);
        }
        mVideoProgressController = new VideoProgressController(mContext, mVideoDuration);
        mVideoProgressController.setVideoProgressView(mVideoProgressView);
        mVideoProgressController.setVideoProgressSeekListener(new VideoProgressController.VideoProgressSeekListener() {
            @Override
            public void onVideoProgressSeek(long currentTimeMs) {
                if (mActionListener != null) {
                    mActionListener.onSeekChanged(currentTimeMs);
                }
            }

            @Override
            public void onVideoProgressSeekFinish(long currentTimeMs) {
                if (mActionListener != null) {
                    mActionListener.onSeekChanged(currentTimeMs);
                }
            }
        });
        RangeSliderViewContainer sliderViewContainer = new RangeSliderViewContainer(mContext);
        sliderViewContainer.init(mVideoProgressController, 0, mVideoDuration, mVideoDuration);
        sliderViewContainer.setDurationChangeListener(new RangeSliderViewContainer.OnDurationChangeListener() {
            @Override
            public void onDurationChange(long startTime, long endTime) {
                if (mStartTime != null) {
                    mStartTime.setText(StringUtil.getDurationText(startTime));
                }
                if (mEndTime != null) {
                    mEndTime.setText(StringUtil.getDurationText(endTime));
                }
                if (mActionListener != null) {
                    mActionListener.onCutTimeChanged(startTime, endTime);
                }
            }
        });
        mVideoProgressController.addRangeSliderView(sliderViewContainer);
        mColorfulProgress = new ColorfulProgress(mContext);
        mColorfulProgress.setWidthHeight(mVideoProgressController.getThumbnailPicListDisplayWidth(), CommonUtils.dip2px(mContext, 50));
        mVideoProgressController.addColorfulProgress(mColorfulProgress);
        if (mEndTime != null) {
            mEndTime.setText(StringUtil.getDurationText(mVideoDuration));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root) {
            hide();

        } else if (i == R.id.btn_special_cancel) {
            specialCancel();

        }
    }

    public void onVideoProgressChanged(long timeMs) {
        if (mShowed) {
            int currentTimeMs = (int) (timeMs / 1000);//转为ms值
            if (mVideoProgressController != null) {
                mVideoProgressController.setCurrentTimeMs(currentTimeMs);
            }
            mCurTime = currentTimeMs;
        }
    }


    /**
     * 特效的按钮被按下
     */
    private void specialDown(View v) {
        if (mCurTime >= mVideoDuration) {
            mSpecialStartMark = false;
            return;
        }
        mSpecialStartMark = true;
        int color = 0;
        int effect = 0;
        int i = v.getId();
        if (i == R.id.btn_special_1) {
            color = 0xAA1FBCB6;
            effect = TXVideoEditConstants.TXEffectType_ROCK_LIGHT;//抖动

        } else if (i == R.id.btn_special_2) {
            color = 0xAAEC8435;
            effect = TXVideoEditConstants.TXEffectType_SPLIT_SCREEN;//动感分屏

        } else if (i == R.id.btn_special_3) {
            color = 0xAA449FF3;
            effect = TXVideoEditConstants.TXEffectType_DARK_DRAEM;//魔法

        } else if (i == R.id.btn_special_4) {
            color = 0xAAEC5F9B;
            effect = TXVideoEditConstants.TXEffectType_SOUL_OUT;//幻觉

            //TODO
        } /*else if (i == R.id.btn_special_5) {
            color = 0xAA0080ff;
            effect = TXVideoEditConstants.TXEffectType_WIN_SHADDOW;//百叶窗

        } else if (i == R.id.btn_special_6) {
            color = 0xAAff80c0;
            effect = TXVideoEditConstants.TXEffectType_GHOST_SHADDOW;//鬼影

        } else if (i == R.id.btn_special_7) {
            color = 0xAAff8040;
            effect = TXVideoEditConstants.TXEffectType_PHANTOM_SHADDOW;//幻影

        } else if (i == R.id.btn_special_8) {
            color = 0xAA008080;
            effect = TXVideoEditConstants.TXEffectType_GHOST;//幽灵

        } else if (i == R.id.btn_special_9) {
            color = 0xAAff0080;
            effect = TXVideoEditConstants.TXEffectType_LIGHTNING;//闪电

        } else if (i == R.id.btn_special_10) {
            color = 0xAA0000ff;
            effect = TXVideoEditConstants.TXEffectType_MIRROR;//镜像

        } else if (i == R.id.btn_special_11) {
            color = 0xAA8000ff;
            effect = TXVideoEditConstants.TXEffectType_ILLUSION;//幻觉

        }*/
        if (mColorfulProgress != null) {
            mColorfulProgress.startMark(color);
        }
        if (mActionListener != null) {
            mActionListener.onSpecialStart(effect, mCurTime);
        }
    }

    /**
     * 特效的按钮被抬起
     */
    private void otherSpecialUp(View v) {
        if (!mSpecialStartMark) {
            return;
        }
        mSpecialStartMark = false;
        int effect = 0;
        int i = v.getId();
        if (i == R.id.btn_special_1) {
            effect = TXVideoEditConstants.TXEffectType_ROCK_LIGHT;//抖动

        } else if (i == R.id.btn_special_2) {
            effect = TXVideoEditConstants.TXEffectType_SPLIT_SCREEN;//动感分屏

        } else if (i == R.id.btn_special_3) {
            effect = TXVideoEditConstants.TXEffectType_DARK_DRAEM;//魔法

        } else if (i == R.id.btn_special_4) {
            effect = TXVideoEditConstants.TXEffectType_SOUL_OUT;//幻觉

            //TODO
        }/*else if (i == R.id.btn_special_5) {
            effect = TXVideoEditConstants.TXEffectType_WIN_SHADDOW;//百叶窗

        } else if (i == R.id.btn_special_6) {
            effect = TXVideoEditConstants.TXEffectType_GHOST_SHADDOW;//鬼影

        } else if (i == R.id.btn_special_7) {
            effect = TXVideoEditConstants.TXEffectType_PHANTOM_SHADDOW;//幻影

        } else if (i == R.id.btn_special_8) {
            effect = TXVideoEditConstants.TXEffectType_GHOST;//幽灵

        } else if (i == R.id.btn_special_9) {
            effect = TXVideoEditConstants.TXEffectType_LIGHTNING;//闪电

        } else if (i == R.id.btn_special_10) {
            effect = TXVideoEditConstants.TXEffectType_MIRROR;//镜像

        } else if (i == R.id.btn_special_11) {
            effect = TXVideoEditConstants.TXEffectType_ILLUSION;//幻觉

        }*/
        if (mColorfulProgress != null) {
            mColorfulProgress.endMark();
        }
        if (mActionListener != null) {
            mActionListener.onSpecialEnd(effect, mCurTime);
        }
        showBtnSpecialCancel();
    }


    /**
     * 撤销最后一次特效
     */
    private void specialCancel() {
        if (mColorfulProgress != null) {
            ColorfulProgress.MarkInfo markInfo = mColorfulProgress.deleteLastMark();
            if (markInfo != null) {
                if(mVideoProgressController!=null){
                    mVideoProgressController.setCurrentTimeMs(markInfo.startTimeMs);
                }
                if (mActionListener != null) {
                    mActionListener.onSpecialCancel(markInfo.startTimeMs);
                }
            }
        }
        showBtnSpecialCancel();
    }

    /**
     * 显示或隐藏撤销其他特效的按钮
     */
    private void showBtnSpecialCancel() {
        if (mBtnSpecialCancel != null && mColorfulProgress != null) {
            if (mColorfulProgress.getMarkListSize() > 0) {
                if (mBtnSpecialCancel.getVisibility() != View.VISIBLE) {
                    mBtnSpecialCancel.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnSpecialCancel.getVisibility() == View.VISIBLE) {
                    mBtnSpecialCancel.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void show(boolean showSpecial) {
        mShowed = true;
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
        if (showSpecial) {
            if (mTip != null) {
                mTip.setText(R.string.video_edit_cut_tip_2);
            }
            if (mSpecialGroup != null && mSpecialGroup.getVisibility() != View.VISIBLE) {
                mSpecialGroup.setVisibility(View.VISIBLE);
            }
        } else {
            if (mTip != null) {
                mTip.setText(R.string.video_edit_cut_tip);
            }
            if (mSpecialGroup != null && mSpecialGroup.getVisibility() == View.VISIBLE) {
                mSpecialGroup.setVisibility(View.GONE);
            }
        }
    }

    public void hide() {
        mShowed = false;
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
        if (mActionListener != null) {
            mActionListener.onHide();
        }
    }


    public interface ActionListener {
        void onHide();

        void onSeekChanged(long currentTimeMs);

        void onCutTimeChanged(long startTime, long endTime);

        void onSpecialStart(int effect, long currentTimeMs);

        void onSpecialEnd(int effect, long currentTimeMs);

        void onSpecialCancel(long currentTimeMs);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        mActionListener = null;
    }

    public boolean isShowed() {
        return mShowed;
    }

}

