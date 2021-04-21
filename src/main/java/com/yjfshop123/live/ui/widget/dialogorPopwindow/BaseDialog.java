package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Description
 * Created by ZTY
 * CreateDate 2017/10/27
 */

public class BaseDialog extends Dialog {

    public FrameLayout dialogMain;

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
        setCancelable(false);//点击弹窗以外的无效果

    }

    public void showAnim(int duration) {
        this.show();
        animation(duration);
    }

    public void animation(int mDuration) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
//                //设置view按照Y周旋转的过程，从-90°到0°
//                ObjectAnimator.ofFloat(this, "rotationY", -90, 0).setDuration(mDuration),
//                //设置view在X轴的位移过程
//                ObjectAnimator.ofFloat(this, "translationX", -300, 0).setDuration(mDuration),
//
//                //定义view的旋转过程，从1080度转到0度，旋转两圈
//                ObjectAnimator.ofFloat(this, "rotation", 1080, 720, 360, 0).setDuration(mDuration),
//                //定义view的透明度从全隐，到全显的过程，setDuration(mDuration)是设置动画时间
//                ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(mDuration * 3 / 2),
                //定义view基于scaleX的缩放过程
                ObjectAnimator.ofFloat(dialogMain, "scaleX", 0.1f, 0.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(dialogMain, "scaleY", 0.1f, 0.5f, 1).setDuration(mDuration)
        );
        animatorSet.start();
    }

}
