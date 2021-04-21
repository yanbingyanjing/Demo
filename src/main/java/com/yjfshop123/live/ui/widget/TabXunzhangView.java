package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

import butterknife.BindView;
import butterknife.OnClick;

public class TabXunzhangView extends RelativeLayout implements View.OnClickListener {
    View oneLeft;
    TextView one;
    View oneRight;
    View twoLeft;
    TextView two;
    View twoRight;
    View threeLeft;
    TextView three;
    View threeRight;
    LinearLayout threeLl;
    LinearLayout oneLl;
    LinearLayout twoLl;
    private Context context;

    SelectListener selectListener;

    public TabXunzhangView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TabXunzhangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TabXunzhangView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        View view = View.inflate(context, R.layout.tab_xunzhang, this);

        //获取子控件对象
        oneLeft =view. findViewById(R.id.one_left);
        oneRight = view. findViewById(R.id.one_right);
        one =view.  findViewById(R.id.one);
        twoLeft = view. findViewById(R.id.two_left);
        two = view. findViewById(R.id.two);
        twoRight = view. findViewById(R.id.two_right);
        threeLeft =view.  findViewById(R.id.three_left);
        three = view. findViewById(R.id.three);
        threeRight = view. findViewById(R.id.three_right);
        threeLl= view. findViewById(R.id.three_ll);
        oneLl= view. findViewById(R.id.one_ll);
        twoLl= view. findViewById(R.id.two_ll);


        one.setSelected(true);
        two.setSelected(false);
        three.setSelected(false);
        oneLeft.setVisibility(VISIBLE);
        oneRight.setVisibility(VISIBLE);

        twoLeft.setVisibility(GONE);
        twoRight.setVisibility(GONE);
        threeLeft.setVisibility(INVISIBLE);
        threeRight.setVisibility(INVISIBLE);

        threeLl.setOnClickListener(this);
        oneLl.setOnClickListener(this);
        twoLl.setOnClickListener(this);
    }

    public void oneClick() {
        if (!one.isSelected()) {
            one.setSelected(true);
            two.setSelected(false);
            three.setSelected(false);
            oneLeft.setVisibility(VISIBLE);
            oneRight.setVisibility(VISIBLE);

            twoLeft.setVisibility(INVISIBLE);
            twoRight.setVisibility(INVISIBLE);
            threeLeft.setVisibility(INVISIBLE);
            threeRight.setVisibility(INVISIBLE);
            if (selectListener != null) {
                selectListener.onOneClick();
            }
        }
    }

    public void twoClick() {
        if (!two.isSelected()) {
            one.setSelected(false);
            two.setSelected(true);
            three.setSelected(false);
            oneLeft.setVisibility(INVISIBLE);
            oneRight.setVisibility(INVISIBLE);

            twoLeft.setVisibility(VISIBLE);
            twoRight.setVisibility(VISIBLE);
            threeLeft.setVisibility(INVISIBLE);
            threeRight.setVisibility(INVISIBLE);
            if (selectListener != null) {
                selectListener.onTwoClick();
            }
        }
    }

    public void threeClick() {
        if (!three.isSelected()) {
            one.setSelected(false);
            two.setSelected(false);
            three.setSelected(true);
            oneLeft.setVisibility(INVISIBLE);
            oneRight.setVisibility(INVISIBLE);

            twoLeft.setVisibility(INVISIBLE);
            twoRight.setVisibility(INVISIBLE);
            threeLeft.setVisibility(VISIBLE);
            threeRight.setVisibility(VISIBLE);
            if (selectListener != null) {
                selectListener.onThreeClick();
            }
        }
    }

    public void setBtnText(String leftT, String rightT, String threeT) {
        one.setText(leftT);
        two.setText(rightT);
        three.setText(threeT);
    }

    public void setListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one_ll:
                oneClick();
                break;
            case R.id.two_ll:
                twoClick();
                break;
            case R.id.three_ll:
                threeClick();
                break;
        }
    }

    public interface SelectListener {

        void onOneClick();

        void onTwoClick();

        void onThreeClick();
    }

}
