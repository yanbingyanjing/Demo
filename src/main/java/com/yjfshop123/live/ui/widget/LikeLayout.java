package com.yjfshop123.live.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yjfshop123.live.R;

import java.util.Random;

public class LikeLayout extends FrameLayout {

    private Drawable icon;

    public LikeLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LikeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LikeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private OnLikeListener onLikeListener;

    public interface OnLikeListener {
        void onItemClick();
    }

    public void setOnLikeListener(OnLikeListener listener){
        this.onLikeListener = listener;
    }

    private void init(Context context) {
        icon = getResources().getDrawable(R.drawable.ic_heart);
        setClipChildren(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                this.addHeartView(x, y);
                onLikeListener.onItemClick();
            }
            /*switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_UP:
                    float x = event.getX();
                    float y = event.getY();
                    addHeartView(x, y);
                    onLikeListener.onItemClick();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }*/
        }
        return super.onTouchEvent(event);
    }

    /**
     * 在Layout中添加红心并，播放消失动画
     */
    private void addHeartView(float x, float y) {
        LayoutParams lp = new LayoutParams(this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight());
        lp.leftMargin = (int)(x - (float)(this.icon.getIntrinsicWidth() / 2));
        lp.topMargin = (int)(y - (float)this.icon.getIntrinsicHeight());
        final ImageView img = new ImageView(this.getContext());
        img.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.postRotate(this.getRandomRotate());
        img.setImageMatrix(matrix);
        img.setImageDrawable(this.icon);
        img.setLayoutParams((android.view.ViewGroup.LayoutParams)lp);
        this.addView((View)img);
        AnimatorSet animSet = this.getShowAnimSet(img);
        final AnimatorSet hideSet = this.getHideAnimSet(img);
        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideSet.start();
            }
        });

        hideSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView((View)img);
            }
        });
    }

    /**
     * 刚点击的时候的一个缩放效果
     */
    private AnimatorSet getShowAnimSet(ImageView view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.2F, 1.0F});
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.2F, 1.0F});
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(new Animator[]{(Animator)scaleX, (Animator)scaleY});
        animSet.setDuration(100L);
        return animSet;
    }

    /**
     * 缩放结束后到红心消失的效果
     */
    private AnimatorSet getHideAnimSet(ImageView view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0F, 0.1F});
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.0F, 2.0F});
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.0F, 2.0F});
        ObjectAnimator translation = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0F, -150.0F});
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(new Animator[]{(Animator)alpha, (Animator)scaleX, (Animator)scaleY, (Animator)translation});
        animSet.setDuration(500L);
        return animSet;
    }

    /**
     * 生成一个随机的左右偏移量
     */
    private float getRandomRotate() {
        return (float)((new Random()).nextInt(20) - 10);
    }


}
