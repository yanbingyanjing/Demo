package com.yjfshop123.live.live.live.common.widget.danmaku;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.LiveTextRender;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;


public class DanmuViewHolder extends AbsViewHolder {

    private static final float SPEED = 0.2f;//弹幕的速度，这个值越小，弹幕走的越慢
    private static int MARGIN_TOP = 0;
    private static int SPACE = 0;
    private static int DP_15 = 0;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mContent;
    private int mScreenWidth;//屏幕宽度
    private int mWidth;//控件的宽度
    private ValueAnimator mAnimator;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private boolean mCanNext;//是否可以有下一个
    private boolean mIdle;//是否空闲
    private ActionListener mActionListener;
    private int mLineNum;

    public DanmuViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_gift_danmu;
    }

    @Override
    public void init() {
        if (MARGIN_TOP == 0){
            MARGIN_TOP = CommonUtils.dip2px(mContext, 150);
        }
        if (SPACE == 0){
            SPACE = CommonUtils.dip2px(mContext, 50);
        }
        if (DP_15 == 0){
            DP_15 = CommonUtils.dip2px(mContext, 15);
        }

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mContent = (TextView) findViewById(R.id.content);
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mContentView.setX(v);
                if (!mCanNext && v <= mScreenWidth - mWidth - DP_15) {
                    mCanNext = true;
                    if (mActionListener != null) {
                        mActionListener.onCanNext(mLineNum);
                    }
                }
            }

        };
        mAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeFromParent();
                mIdle = true;
                if (mActionListener != null) {
                    mActionListener.onAnimEnd(DanmuViewHolder.this);
                }
            }
        };
    }

    public void show(DanmuBean bean, int lineNum) {
        mLineNum = lineNum;
        Glide.with(mContext)
                .load(bean.getAvatar())
                .into(mAvatar);
        String name = bean.getUserNiceName();
        mName.setText(name);
        mName.setTextColor(LiveTextRender.calcNameColor(name));
        mContent.setText(bean.getContent());
        mCanNext = false;
        mContentView.measure(0, 0);
        mWidth = mContentView.getMeasuredWidth();
        mContentView.setX(mScreenWidth);
        mContentView.setY(MARGIN_TOP + lineNum * SPACE);
        addToParent();
        mAnimator = ValueAnimator.ofFloat(mScreenWidth, -mWidth);
        mAnimator.addUpdateListener(mUpdateListener);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration((int) ((mScreenWidth + mWidth) / SPEED));
        mAnimator.addListener(mAnimatorListener);
        mAnimator.start();
    }

    public boolean isIdle() {
        return mIdle;
    }

    public void setIdle(boolean idle) {
        mIdle = idle;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        removeFromParent();
        mActionListener = null;
    }

    public interface ActionListener {
        void onCanNext(int lineNum);

        void onAnimEnd(DanmuViewHolder vh);
    }
}
