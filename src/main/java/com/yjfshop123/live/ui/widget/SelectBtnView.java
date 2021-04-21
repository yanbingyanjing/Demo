package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

public class SelectBtnView extends RelativeLayout {
    private Context context;
    private TextView left;
    private TextView right;
    SelectListener selectListener;

    public SelectBtnView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public SelectBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public SelectBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }


    private void init() {
        View view = View.inflate(context, R.layout.select_btn, this);

        //获取子控件对象
        left = view.findViewById(R.id.left);
        right = view.findViewById(R.id.right);
        left.setSelected(true);
        right.setSelected(false);
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
    public void leftClick() {
        if (!left.isSelected()) {
            left.setSelected(true);
            right.setSelected(false);
            if (selectListener != null) {
                selectListener.onLeftClick();
            }
        }
    }

    public void rightClick() {
        if (!right.isSelected()) {
            right.setSelected(true);
            left.setSelected(false);
            if (selectListener != null) {
                selectListener.onRightClick();
            }
        }
    }
    public void setViewPager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    if (!left.isSelected()) {
                        left.setSelected(true);
                        right.setSelected(false);
                    }
                } else{
                    if (!right.isSelected()) {
                        right.setSelected(true);
                        left.setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    public void setBtnText(String leftT, String rightT) {
        left.setText(leftT);
        right.setText(rightT);
    }

    public void setListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public interface SelectListener {

        void onLeftClick();

        void onRightClick();
    }

}
