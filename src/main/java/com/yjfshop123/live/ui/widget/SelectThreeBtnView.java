package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

public class SelectThreeBtnView extends RelativeLayout {
    private Context context;
    private TextView left;
    private TextView right;
    private TextView rightTwo;
    SelectBtnView.SelectListener selectListener;

    public SelectThreeBtnView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SelectThreeBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SelectThreeBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        View view = View.inflate(context, R.layout.select_btn_three_view, this);
        rightTwo = view.findViewById(R.id.right_two);
        //获取子控件对象
        left = view.findViewById(R.id.left);
        right = view.findViewById(R.id.right);
        left.setSelected(true);
        right.setSelected(false);
        rightTwo.setSelected(false);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftClick();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightClick();
            }
        });
        rightTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightTwoClick();
            }
        });
    }
    public void leftClick() {
        if (!left.isSelected()) {
            left.setSelected(true);
            right.setSelected(false);
            rightTwo.setSelected(false);
            if (viewPager != null) {
                viewPager.setCurrentItem(0);
            }
        }
    }

    public void rightClick() {
        if (!right.isSelected()) {
            right.setSelected(true);
            left.setSelected(false);
            rightTwo.setSelected(false);
            if (viewPager != null) {
                viewPager.setCurrentItem(1);
            }
        }
    }
    public void rightTwoClick() {
        if (!rightTwo.isSelected()) {
            rightTwo.setSelected(true);
            right.setSelected(false);
            left.setSelected(false);
            if (viewPager != null) {
                viewPager.setCurrentItem(2);
            }
        }
    }
    ViewPager viewPager ;
    public void setViewPager(ViewPager viewPager) {
       this. viewPager=viewPager;
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                   leftClick();
                } else  if (i == 1) {
                   rightClick();
                }
                else  if (i == 2) {
                   rightTwoClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void setBtnText(String leftT, String rightT,String three) {
        left.setText(leftT);
        right.setText(rightT);
        rightTwo.setText(three);
    }

    public void setListener(SelectBtnView.SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public interface SelectListener {

        void onLeftClick();

        void onRightClick();
    }


}
