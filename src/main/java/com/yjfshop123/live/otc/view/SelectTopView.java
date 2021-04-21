package com.yjfshop123.live.otc.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.widget.SelectBtnView;
import com.yjfshop123.live.utils.SystemUtils;

public class SelectTopView extends RelativeLayout {
    private Context context;
    private TextView left;
    private TextView right;
    SelectListener selectListener;
    private TextView huadong;

    public SelectTopView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SelectTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SelectTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        View view = View.inflate(context, R.layout.select_top_otc, this);

        //获取子控件对象
        left = view.findViewById(R.id.left);
        right = view.findViewById(R.id.right);
        huadong = view.findViewById(R.id.huadong);

        left.setSelected(false);
        right.setSelected(false);
        huadong.setSelected(true);
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                leftClick();
            }
        });
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rightClick();
            }
        });
    }

    boolean isleft = true;

    public void leftClick() {
        if (isHUadongzhong) return;
        if (!isleft) {
            isleft = true;
            left.setSelected(false);
            right.setSelected(false);
            huadong.setText(left.getText().toString());
            anin();
            if (selectListener != null) {
                selectListener.onLeftClick();
            }
        }
    }

    public void rightClick() {
        if (isHUadongzhong) return;

        if (isleft) {
            isleft = false;

            right.setSelected(false);
            left.setSelected(false);
            huadong.setText(right.getText().toString());
            anin();
            if (selectListener != null) {
                selectListener.onRightClick();
            }
        }
    }

    boolean isHUadongzhong = false;

    private void anin() {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                huadong, "translationX", !isleft ? getWidth() - huadong.getWidth() : 0);

        //定义属性动画集合的对象
        AnimatorSet animVideoSetStart = new AnimatorSet();
        animVideoSetStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isHUadongzhong = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isHUadongzhong = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //通过with方法，让两个动画同时进行
        animVideoSetStart.play(animator2);
        //设置延迟时间,让菜单内容相继弹出
        animVideoSetStart.setDuration(200);
        animVideoSetStart.start();

    }

    public void setBtnText(String leftT, String rightT) {
        huadong.setText(leftT);
        left.setText(leftT);
        right.setText(rightT);
    }

    public void setListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public int initIndex = 0;

    public void setInitIndex(int initIndex) {
        this.initIndex = initIndex;
    }

    public void setViewPager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    if (!isleft) {
                        isleft = true;
                        left.setSelected(false);
                        right.setSelected(false);
                        huadong.setText(left.getText().toString());
                        anin();
                    }
                } else {
                    if (isleft) {
                        isleft = false;

                        right.setSelected(false);
                        left.setSelected(false);
                        huadong.setText(right.getText().toString());
                        anin();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public interface SelectListener {

        void onLeftClick();

        void onRightClick();
    }

}
